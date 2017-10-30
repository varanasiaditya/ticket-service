/*
 * Java filename    TicketServiceImplLoadTest.java for test with multiple threads.
 *
 *
 */
package com.bookings.ticket.service.hz.impl;

import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.bookings.ticket.model.SeatHold;
import com.bookings.ticket.model.SeatInfo;
import com.bookings.ticket.service.TicketService;
import com.bookings.ticket.service.hz.IApplicationCache;
import com.hazelcast.core.IMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TicketServiceImplHzTest {
	private static final String TEST_TEST_COM = "test@wm.com";

	@Autowired
	@Qualifier("ticketServiceImpl")
	TicketService service;

	@Autowired
	IApplicationCache<String, SeatInfo> appCache;

	@Autowired
	protected IApplicationCache<UUID, SeatHold> seatHolderCache;

	static Logger log = Logger.getLogger(TicketServiceImpl.class.getName());

/*	@BeforeClass
	public static void before() {
	}

	@AfterClass
	public static void destory() {
		new ApplicationCache<>().disconnect();
	}
*/
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidData1() {
		service.findAndHoldSeats(101, TEST_TEST_COM);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidData2() {
		service.findAndHoldSeats(0, TEST_TEST_COM);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidData3() {
		service.findAndHoldSeats(-1, TEST_TEST_COM);

	}

	// @Test
	public boolean testNumberOfSeats() throws InterruptedException {
		log.info("START : testNumberOfSeats");
		assertTrue(service.numSeatsAvailable() == 100);
		log.info("END : testNumberOfSeats");
		return true;
	}

	// @Test
	public boolean testHoldSeats() throws InterruptedException {
		log.info("START : testHoldSeats");

		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(hold.getSeatInfo().size() == 4);
		refreshCache();
		log.info("END : testHoldSeats");

		return true;

	}

	// @Test
	public boolean testHoldSeatTimeout() throws InterruptedException {
		log.info("START : testHoldSeatTimeout");

		service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		Thread.currentThread().sleep(25000);
		assertTrue(service.numSeatsAvailable() == 100);
		refreshCache();
		log.info("END : testHoldSeatTimeout");
		return true;
	}

	// @Test
	public boolean testReserveHoldSeats() throws InterruptedException {
		log.info("START : testReserveHoldSeats");
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "").contains("A0A1A2A3"));
		refreshCache();
		log.info("END : testReserveHoldSeats");
		return true;
	}

	// @Test
	public boolean testReserveHoldAdjSeats() throws InterruptedException {
		log.info("START : testReserveHoldAdjSeats");
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "").contains("A0A1A2A3"));

		hold = service.findAndHoldSeats(10, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 86);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "")
				.contains("B0B1B2B3B4B5B6B7B8B9"));

		refreshCache();
		log.info("END : testReserveHoldAdjSeats");
		return true;
	}

	// @Test
	public boolean testReserveHoldAdjSeatWithTimeout() throws InterruptedException {
		log.info("START : testReserveHoldAdjSeatWithTimeout");
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);

		Thread.currentThread().sleep(25000);

		hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "").contains("A0A1A2A3"));

		refreshCache();
		log.info("END : testReserveHoldAdjSeatWithTimeout");
		return true;
	}

	// @Test
	public boolean testReserveTimeOutSeat() throws InterruptedException {
		log.info("START : testReserveTimeOutSeat");

		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		Thread.currentThread().sleep(25000);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).contains("Time out for request id"));
		refreshCache();

		log.info("END : testReserveTimeOutSeat");
		return true;
	}

	@Test
	public void testAllOrders() throws InterruptedException {
		if (testReserveHoldSeats() && testHoldSeats() && testHoldSeatTimeout() && testNumberOfSeats()
				&& testReserveHoldAdjSeats() && testReserveTimeOutSeat() /* && sleep() */
				&& testReserveHoldAdjSeatWithTimeout() && loadTest() && loadTestResult()) {
			assertTrue(true);
		}

		refreshCache();
	}

	public boolean loadTest() {
		log.info("START : loadTest");
		Thread tArray[] = new Thread[20];
		ExecPool execPool;

		for (int i = 0; i < 10; i++) {
			execPool = new ExecPool(service, 8, "test" + i + TEST_TEST_COM);
			Thread t = new Thread(execPool, "Thread" + i);
			t.start();
			tArray[i] = t;
		}
		execPool = new ExecPool(service, 20, "test" + 11 + TEST_TEST_COM);
		Thread t = new Thread(execPool, "Thread" + 11);
		t.start();
		log.info("END : loadTest");
		return true;
	}

	public boolean loadTestResult() {
		log.info("START : loadTestResult");

		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(service.numSeatsAvailable() == 0);
		log.info("END : loadTestResult");

		return true;
	}

	public void refreshCache() {
		IMap<String, SeatInfo> map = appCache.getDataMap(IApplicationCache.SEAT_INFO);
		for (String key : map.keySet()) {
			SeatInfo info = map.get(key);
			info.setStatus(SeatInfo.AVILABLE);
			map.put(key, info);
		}

		seatHolderCache.removeCache(IApplicationCache.HOLD_SEAT_INFO);
	}

	private class ExecPool implements Runnable {

		TicketService service;
		int numberofSeats;
		String customerEmail;

		ExecPool(TicketService service, int numberofSeats, String customerEmail) {
			this.service = service;
			this.numberofSeats = numberofSeats;
			this.customerEmail = customerEmail;
		}

		@Override
		public void run() {

			SeatHold hold = service.findAndHoldSeats(this.numberofSeats, customerEmail);
			service.reserveSeats(hold.getSeatId(), customerEmail);

		}

	}
}
