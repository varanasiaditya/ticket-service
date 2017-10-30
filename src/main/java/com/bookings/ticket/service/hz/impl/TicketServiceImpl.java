/*
 * Java filename    TicketServiceImpl.java for servie layer class
 *
 *
 */
package com.bookings.ticket.service.hz.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bookings.ticket.model.SeatHold;
import com.bookings.ticket.model.SeatInfo;
import com.bookings.ticket.service.TicketService;
import com.bookings.ticket.service.hz.IApplicationCache;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryExpiredListener;

/**
 * @author Aditya.
 *
 */
@Component("ticketServiceImpl")
public class TicketServiceImpl implements TicketService, EntryExpiredListener<String, SeatInfo> {

	static Logger log = Logger.getLogger(TicketServiceImpl.class.getName());

	@Autowired
	protected IApplicationCache<String, SeatInfo> cache;

	@Autowired
	protected IApplicationCache<UUID, SeatHold> seatHolderCache;

	protected Map<String, List<SeatInfo>> seatsByRow = new HashMap<String, List<SeatInfo>>();

	protected Set<UUID> incompleteTransSet = new HashSet<UUID>();

	@Value("${hold.time.out}")
	protected int timeOut;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bookings.ticket.service.TicketService#numSeatsAvailable()
	 */
	@Override
	public int numSeatsAvailable() {

		Map<String, SeatInfo> seatRows = cache.getDataMap(IApplicationCache.SEAT_INFO);
		Stream<Entry<String, SeatInfo>> stream = seatRows.entrySet().stream()
				.filter(seatrow -> SeatInfo.AVILABLE.equals(seatrow.getValue().getStatus()));
		if (stream == null) {
			return 0;
		}
		// or return actual count
		return (int) stream.count();

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

		if (this.numSeatsAvailable() <= 0) {
			throw new IllegalArgumentException("Seats are full ");
		}
		if (numSeats > seatsAvilableCount) {
			throw new IllegalArgumentException("Max seats Avilable " + seatsAvilableCount);
		}
		arrangeSeatsRowWise();

		Set<String> seatInfosByRowKeys = this.seatsByRow.keySet();
		List<SeatInfo> seatInfos = null;
		String deciderRow = "-1";
		boolean fixed = false;

		for (String seatInfosByRowKey : seatInfosByRowKeys) {
			seatInfos = this.seatsByRow.get(seatInfosByRowKey);
			for (SeatInfo seatInfo : seatInfos) {
				// check is seats are available in current row and are adjacent seats.
				if (seatInfos.size() >= numSeats && numSeats - 1 <= seatInfos.size()
						&& (seatInfo.getSeatNumber() + (numSeats - 1)) == seatInfos.get(numSeats - 1).getSeatNumber()) {
					deciderRow = seatInfo.getSeatRow();
					fixed = true;
					break;
				}
			}
			if (fixed) {
				break;
			}
		}

		// if no adjacent seats avilable it is fist come first serve.
		seatInfos = null;
		if ("-1".equals(deciderRow)) {
			// throw new IllegalArgumentException("System ERROR");
			Set<String> data = this.seatsByRow.keySet();
			if (data != null) {
				seatInfos = new ArrayList<SeatInfo>();
				for (String string : data) {
					for (SeatInfo seatinfo : this.seatsByRow.get(string))
						seatInfos.add(seatinfo);
				}
			}
		} else {
			seatInfos = this.seatsByRow.get(deciderRow).stream().collect(Collectors.toList());
		}

		SeatHold seatHold = new SeatHold();
		UUID uuid = UUID.randomUUID();
		for (SeatInfo seatInfo : seatInfos) {

			if (numSeats == 0) {
				break;
			}
			try {
				if (cache.tryLock(seatInfo.getSeatInfoId(), IApplicationCache.SEAT_INFO)) {
					// uuid = UUID.randomUUID();
					seatInfo.setStatus(SeatInfo.HOLD);
					seatInfo.setSeatHoldUniqueId(uuid.toString());
					cache.put(seatInfo.getSeatInfoId(), seatInfo, IApplicationCache.SEAT_INFO, timeOut, TimeUnit.SECONDS);
					seatHold.getSeatInfo().add(seatInfo.getSeatInfoId());
					seatHold.setCustomerEmail(customerEmail);
					seatHold.setSeatId(uuid);
					seatHolderCache.put(uuid, seatHold, IApplicationCache.HOLD_SEAT_INFO);
				}
			} finally {
				cache.unLock(seatInfo.getSeatInfoId(), IApplicationCache.SEAT_INFO);
			}

			numSeats--;

		}
		return seatHold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bookings.ticket.service.TicketService#reserveSeats(UUID,
	 * java.lang.String)
	 */
	@Override
	public String reserveSeats(UUID seatHoldId, String customerEmail) {

		SeatHold seatHold = this.seatHolderCache.get(seatHoldId, IApplicationCache.HOLD_SEAT_INFO);
		String seatsNumbers = "";
		if (seatHold == null) {
			return "Time out for request id" + seatHoldId + " Please try again.";
		}
		if (this.incompleteTransSet.contains(seatHoldId)) {
			this.incompleteTransSet.remove(seatHoldId);
			return "Time out for request id" + seatHoldId + " Please try again.";
		}

		for (String seatInfo : seatHold.getSeatInfo()) {
			try {
				if (cache.tryLock(seatInfo, IApplicationCache.SEAT_INFO)) {

					SeatInfo info = cache.get(seatInfo, IApplicationCache.SEAT_INFO);
					info.setStatus(SeatInfo.RESERVED);
					cache.put(seatInfo, info, IApplicationCache.SEAT_INFO);
					seatsNumbers += info.getSeatRow() + "" + String.valueOf(info.getSeatNumber()) + "  ";
					this.seatHolderCache.remove(seatHoldId, IApplicationCache.HOLD_SEAT_INFO);
				}
			} finally {
				cache.unLock(seatInfo, IApplicationCache.SEAT_INFO);
			}
		}
		return "Seats reserved for \n" + seatsNumbers;
	}

	@PostConstruct
	public void init() {
		cache.createCache(IApplicationCache.SEAT_INFO);
		seatHolderCache.createCache(IApplicationCache.HOLD_SEAT_INFO);
		List<SeatInfo> seatInfoList = null;
		String status = SeatInfo.AVILABLE;
		char _rowNum_char_end = 'A' + 10;
		for (char _rowNum_char = 'A'; _rowNum_char < _rowNum_char_end; _rowNum_char++) {
			for (int seatNumber = 0; seatNumber < 10; seatNumber++) {
				log.debug(_rowNum_char + "" + seatNumber);
				SeatInfo seatInfo = new SeatInfo(seatNumber, String.valueOf(_rowNum_char), null, status);
				cache.put(seatInfo.getSeatInfoId(), seatInfo, IApplicationCache.SEAT_INFO);
				if (this.seatsByRow.get(seatInfo.getSeatRow()) == null) {
					seatInfoList = new ArrayList<SeatInfo>();
					seatInfoList.add(seatInfo);
				} else {
					seatInfoList.add(seatInfo);
				}
				this.seatsByRow.put(seatInfo.getSeatRow(), seatInfoList);
			}
		}
		cache.getDataMap(IApplicationCache.SEAT_INFO).addEntryListener(this, true);

	}

	@PreDestroy
	public void destory() {
		log.info("Destorying cache");
		cache.disconnect();
	}

	protected void arrangeSeatsRowWise() {
		this.seatsByRow.clear();
		TreeSet<String> seatInfoIds = cache.getkeysSorted(IApplicationCache.SEAT_INFO);

		SeatInfo seatInfo = null;
		List<SeatInfo> seatInfoList = null;

		for (String seatInfoId : seatInfoIds) {
			seatInfo = cache.get(seatInfoId, IApplicationCache.SEAT_INFO);
			if (SeatInfo.HOLD.equals(seatInfo.getStatus()) || SeatInfo.RESERVED.equals(seatInfo.getStatus())) {
				continue;
			}
			if (this.seatsByRow.get(seatInfo.getSeatRow()) == null) {
				seatInfoList = new ArrayList<SeatInfo>();
				seatInfoList.add(seatInfo);
			} else {
				this.seatsByRow.get(seatInfo.getSeatRow()).add(seatInfo);

			}
			this.seatsByRow.put(seatInfo.getSeatRow(), seatInfoList);

		}

	}

	/**
	 * Evet when map entry expire for on hold entries reset it back to Avilable.
	 */
	public synchronized void entryExpired(EntryEvent<String, SeatInfo> entry) {
		SeatInfo seatInfo = entry.getOldValue();
		if (SeatInfo.HOLD.equals(seatInfo.getStatus())) {
			seatInfo.setStatus(SeatInfo.AVILABLE);
			cache.put(seatInfo.getSeatInfoId(), seatInfo, IApplicationCache.SEAT_INFO);
			log.debug("Removed SeatHold ID  " + seatInfo.getSeatHoldUniqueId());
			seatHolderCache.remove(UUID.fromString(seatInfo.getSeatHoldUniqueId()), IApplicationCache.HOLD_SEAT_INFO);
			incompleteTransSet.add(UUID.fromString(seatInfo.getSeatHoldUniqueId()));
		} else {
			cache.put(seatInfo.getSeatInfoId(), seatInfo, IApplicationCache.SEAT_INFO);
			seatHolderCache.remove(UUID.fromString(seatInfo.getSeatHoldUniqueId()), IApplicationCache.HOLD_SEAT_INFO);
			incompleteTransSet.add(UUID.fromString(seatInfo.getSeatHoldUniqueId()));
		}

	}

}
