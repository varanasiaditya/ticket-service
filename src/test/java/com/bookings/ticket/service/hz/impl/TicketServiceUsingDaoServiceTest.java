package com.bookings.ticket.service.hz.impl;

import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.h2.tools.DeleteDbFiles;
import org.junit.BeforeClass;
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
import com.bookings.ticket.service.dao.TicketServiceDao;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TicketServiceUsingDaoServiceTest {
	private static final String TEST_TEST_COM = "test@test.com";

	@Autowired
	@Qualifier("ticketServiceUsingDaoServiceImpl")
	TicketService service;

	@Autowired
	TicketServiceDao ticketDao;

	@BeforeClass
	public static void before() {
		DeleteDbFiles.execute("c:/java/h2/app_db", "", true);

	}
	@PostConstruct
	public void init() {
		DeleteDbFiles.execute("~/h2/app_db", "", true);
		reset();
	}

	@PreDestroy
	public void destory() {
		ticketDao.clearDB();

	}

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

	@Test
	public void testNumberOfSeats() throws InterruptedException {
		assertTrue(service.numSeatsAvailable()==100);
	}
	
	@Test
	public void testHoldSeats() throws InterruptedException {
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(hold.getSeatInfo().size() == 4);
		reset();
		
	}

	@Test
	public void testHoldSeatTimeout() throws InterruptedException {
		service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		Thread.currentThread().sleep(30000);
		assertTrue(service.numSeatsAvailable() == 100);
		reset();
		
	}

	@Test
	public void testReserveHoldSeats() throws InterruptedException {
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "").contains("A0A1A2A3"));
		reset();
		
	}

	@Test
	public void testReserveHoldAdjSeats() throws InterruptedException {
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "").contains("A0A1A2A3"));
		
		hold = service.findAndHoldSeats(10, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 86);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "").contains("B0B1B2B3B4B5B6B7B8B9"));
		
		
		reset();
		
	}
	@Test
	public void testReserveHoldAdjSeatWithTimeout() throws InterruptedException {
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		
		Thread.currentThread().sleep(30000);

		hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).replace(" ", "").contains("A0A1A2A3"));
		
		
		reset();
		
	}
	
	@Test
	public void testReserveTimeOutSeat() throws InterruptedException {
		SeatHold hold = service.findAndHoldSeats(4, TEST_TEST_COM);
		assertTrue(service.numSeatsAvailable() == 96);
		Thread.currentThread().sleep(30000);
		assertTrue(service.reserveSeats(hold.getSeatId(), TEST_TEST_COM).contains("Timeout or invalid ID Please try again" + hold.getSeatId().toString()));
		
		
		reset();
		
	}

	
	private void reset() {
		char _rowNum_char_end = 'A' + 10;
		for (char _rowNum_char = 'A'; _rowNum_char < _rowNum_char_end; _rowNum_char++) {
			for (int seatNumber = 0; seatNumber < 10; seatNumber++) {
				SeatInfo seatInfo = new SeatInfo(seatNumber, String.valueOf(_rowNum_char), null, SeatInfo.AVILABLE);
				ticketDao.insert(seatInfo);
			}
		}

	}

}
