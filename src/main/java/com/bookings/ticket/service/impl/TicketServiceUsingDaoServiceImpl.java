/*
 * Java filename    TicketServiceUsingDaoServiceImpl.java<K,V> DAO Logic
 *
 *
 */
package com.bookings.ticket.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bookings.ticket.model.SeatHold;
import com.bookings.ticket.model.SeatInfo;
import com.bookings.ticket.service.TicketService;
import com.bookings.ticket.service.dao.TicketServiceDao;

/**
 * @author Madhuri
 *
 */
@Component("ticketServiceUsingDaoServiceImpl")
public class TicketServiceUsingDaoServiceImpl implements TicketService {

	static Logger log = Logger.getLogger(TicketServiceUsingDaoServiceImpl.class.getName());

	@Autowired
	TicketServiceDao ticketDao;

	@Value("${hold.time.out}")
	protected int timeOut;

	@PostConstruct
	@Transactional
	public void init() {
		char _rowNum_char_end = 'A' + 10;
		for (char _rowNum_char = 'A'; _rowNum_char < _rowNum_char_end; _rowNum_char++) {
			for (int seatNumber = 0; seatNumber < 10; seatNumber++) {
				SeatInfo seatInfo = new SeatInfo(seatNumber, String.valueOf(_rowNum_char), null, SeatInfo.AVILABLE);
				ticketDao.insert(seatInfo);
			}
		}
	}

	/**
	 * Clear DB when destroy
	 */
	@PreDestroy
	public void destroy() {
		log.info("Clear DB");
		ticketDao.clearDB();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bookings.ticket.service.TicketService#numSeatsAvailable()
	 */
	@Override
	public int numSeatsAvailable() {

		List<SeatInfo> seatInfos = ticketDao.getSeats(SeatInfo.HOLD);
		Stream<SeatInfo> stream = seatInfos.stream()
				.filter(p -> (((System.currentTimeMillis() - p.getHoldTime().getTime()) / 1000) % 60) >= timeOut);

		if (stream != null && (int) stream.count() > 0) {
			for (SeatInfo info : seatInfos) {
				info.setStatus(SeatInfo.AVILABLE);
				info.setHoldTime(null);
				this.ticketDao.updateSeatInfo(info);
			}
		}
		List<SeatInfo> infos = ticketDao.getSeats(SeatInfo.AVILABLE);
		return infos == null ? 0 : infos.size();
	}

	protected Map<String, List<SeatInfo>> arrangeSeatsRowWise(List<SeatInfo> seatInfos) {
		Map<String, List<SeatInfo>> seatsByRow = new HashMap<String, List<SeatInfo>>();
		seatsByRow.clear();
		List<SeatInfo> seatInfoList = null;
		for (SeatInfo seatInfo : seatInfos) {
			if (SeatInfo.HOLD.equals(seatInfo.getStatus()) || SeatInfo.RESERVED.equals(seatInfo.getStatus())) {
				continue;
			}
			if (seatsByRow.get(seatInfo.getSeatRow()) == null) {
				seatInfoList = new ArrayList<SeatInfo>();
				seatInfoList.add(seatInfo);
			} else {
				seatsByRow.get(seatInfo.getSeatRow()).add(seatInfo);

			}
			seatsByRow.put(seatInfo.getSeatRow(), seatInfoList);

		}
		return seatsByRow;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bookings.ticket.service.TicketService#findAndHoldSeats(int,
	 * java.lang.String)
	 */
	@Override
	public synchronized SeatHold findAndHoldSeats(int numSeats, String customerEmail) {

		if (numSeats <= 0) {
			throw new IllegalArgumentException("Invalid number of seats " + numSeats);
		}

		int seatsAvilableCount = this.numSeatsAvailable();

		if (seatsAvilableCount <= 0) {
			throw new IllegalArgumentException("Seats are full ");
		}
		if (numSeats > seatsAvilableCount) {
			throw new IllegalArgumentException("Max seats Avilable " + seatsAvilableCount);
		}

		List<SeatInfo> seatInfos = ticketDao.getSeats(SeatInfo.AVILABLE);
		List<SeatInfo> seatInfos2 = null;
		SeatHold seatHold = new SeatHold();
		Map<String, List<SeatInfo>> seatsByRow = this.arrangeSeatsRowWise(seatInfos);
		TreeSet<String> seatInfosByRowKeys = new TreeSet<String>(seatsByRow.keySet());

		/*
		 * for (SeatInfo seatInfo : seatInfos) { // check is seats are available in
		 * current row and are adjacent seats. if (seatInfos.size() >= numSeats &&
		 * numSeats - 1 <= seatInfos.size() && (seatInfo.getSeatNumber() + (numSeats -
		 * 1)) == seatInfos.get(numSeats - 1).getSeatNumber()) { seatInfos2 =
		 * seatInfos.stream().filter(p -> p.getSeatRow().equals(seatInfo.getSeatRow()))
		 * .collect(Collectors.toList()); break; } }
		 */
		String deciderRow = "-1";
		boolean fixed = false;

		for (String seatInfosByRowKey : seatInfosByRowKeys) {
			List<SeatInfo> seatInfoByRow = seatsByRow.get(seatInfosByRowKey);
			for (SeatInfo seatInfo : seatInfoByRow) {
				// check is seats are available in current row and are adjacent seats.
				if (seatInfoByRow.size() >= numSeats && numSeats - 1 <= seatInfoByRow.size()
						&& (seatInfo.getSeatNumber() + (numSeats - 1)) == seatInfoByRow.get(numSeats - 1)
								.getSeatNumber()) {
					deciderRow = seatInfo.getSeatRow();
					fixed = true;
					break;
				}
			}
			if (fixed) {
				break;
			}
		}

		if (!"-1".equals(deciderRow)) {
			final String row = deciderRow;
			seatInfos2 = seatInfos.stream().filter(p -> p.getSeatRow().equals(row)).collect(Collectors.toList());
		} else {
			seatInfos2 = seatInfos;
		}

		if (seatInfos2 == null) {
			seatInfos2 = seatInfos;
		}
		UUID uuid = UUID.randomUUID();
		for (SeatInfo seatInfo : seatInfos2) {
			if (numSeats == 0) {
				break;
			}
			seatInfo.setStatus(SeatInfo.HOLD);
			seatInfo.setSeatHoldUniqueId(uuid.toString());
			seatInfo.setHoldTime(new Timestamp(System.currentTimeMillis()));
			seatHold.getSeatInfo().add(seatInfo.getSeatInfoId());
			this.ticketDao.updateSeatInfo(seatInfo);
			numSeats--;
		}
		seatHold.setCustomerEmail(customerEmail);
		seatHold.setSeatId(uuid);

		return seatHold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bookings.ticket.service.TicketService#reserveSeats(java.util.UUID,
	 * java.lang.String)
	 */
	@Override
	public String reserveSeats(UUID seatHoldId, String customerEmail) {
		List<SeatInfo> seatInfos = ticketDao.getSeatsByUUID(seatHoldId.toString());
		String seatsNumbers = "";

		if (seatInfos.size() == 0) {
			return "Timeout or invalid ID Please try again" + seatHoldId.toString();
		}
		long count = seatInfos.stream().filter(p -> SeatInfo.HOLD.equals(p.getStatus())).count();
		if ((int) count == 0) {
			return "Timeout or invalid ID Please try again" + seatHoldId.toString();
		}

		Stream<SeatInfo> stream = seatInfos.stream()
				.filter(p -> (((System.currentTimeMillis() - p.getHoldTime().getTime()) / 1000) % 60) >= timeOut);

		if (stream != null && (int) stream.count() > 0) {
			for (SeatInfo info : seatInfos) {
				info.setStatus(SeatInfo.AVILABLE);
				this.ticketDao.updateSeatInfo(info);
			}
			return "Timeout or invalid ID Please try again" + seatHoldId.toString();
		}

		for (SeatInfo info : seatInfos) {
			info.setStatus(SeatInfo.RESERVED);
			this.ticketDao.updateSeatInfo(info);
			seatsNumbers += info.getSeatRow() + "" + String.valueOf(info.getSeatNumber()) + "  ";

		}

		return seatsNumbers;
	}

}
