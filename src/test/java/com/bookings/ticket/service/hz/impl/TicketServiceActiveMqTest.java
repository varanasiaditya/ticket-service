package com.bookings.ticket.service.hz.impl;

import static org.junit.Assert.assertTrue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.h2.tools.DeleteDbFiles;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.Model;

import com.bookings.ticket.controller.QueueController;
import com.bookings.ticket.model.SeatInfo;
import com.bookings.ticket.service.dao.TicketServiceDao;
import com.bookings.ticket.service.impl.TicketServiceUsingDaoServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TicketServiceActiveMqTest {

	@Autowired
	QueueController controller;

	@Autowired
	TicketServiceDao ticketDao;

	@Autowired
	TicketServiceUsingDaoServiceImpl service;

	@Mock
	Model model;


	@PostConstruct
	public void init() {
		DeleteDbFiles.execute("~/h2/app_db", "", true);
		reset();
	}

	@PreDestroy
	public void destory() {
		ticketDao.clearDB();

	}

	@Test
	public void testInvalidData1() {
		String value = this.controller.processHoldSeat(model, 101, "test");
		assertTrue(value.contains("input"));
	}

	@Test
	public void testInvalidData2() {
		String value = this.controller.processHoldSeat(model, 0, "test");
		assertTrue(value.contains("input"));

	}

	@Test
	public void testInvalidData3() {
		String value = this.controller.processHoldSeat(model, -1, "test");
		assertTrue(value.contains("input"));

	}

	@Test
	public void testShowData() {
		assertTrue(this.service.numSeatsAvailable() == 100);
		reset();
	}

	@Test
	public void testHoldTIckets() throws InterruptedException {
		assertTrue(this.service.numSeatsAvailable() == 100);
		this.controller.processHoldSeat(model, 4, "test");
		Thread.currentThread().sleep(10000);
		assertTrue(this.service.numSeatsAvailable() == 96);
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
