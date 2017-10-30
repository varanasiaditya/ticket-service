/*
 * Java filename    CenterlCortroller.java for controller layer class
 *
 *
 */
package com.bookings.ticket.controller;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookings.ticket.service.TicketService;

@Controller
public class TicketController {

	@Autowired
	@Qualifier("ticketServiceImpl")
	TicketService service;
	
	
	@Autowired
	@Qualifier("ticketServiceUsingDaoServiceImpl")
	TicketService service2;
	
	
	static Logger log = Logger.getLogger(TicketController.class.getName());

	@RequestMapping("/dao/startBookings")
	public String startBookingsFromDAOModule(Model model) {
		model.addAttribute("numofseats", service2.numSeatsAvailable());
		model.addAttribute("url", "/dao/holdSeat");
		log.info("Execute startBookingsFromDAOModule");
		return "input";
	}

	@RequestMapping("/dao/holdSeat")
	public String holdSeatsFromDAOModule(Model model,
			@RequestParam(value = "noOfSeats", required = true, defaultValue = "0") Integer numOfSeats,
			@RequestParam(value = "email", required = true, defaultValue = "") String customerEmail) {
		try {
			model.addAttribute("seatHold", service2.findAndHoldSeats(numOfSeats, customerEmail));
		} catch (IllegalArgumentException ex) {
			model.addAttribute("numofseats", service2.numSeatsAvailable());
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("url", "/dao/holdSeat");
			return "input";
		}
		log.info("Execute holdSeatsFromDAOModule");
		model.addAttribute("url", "/dao/reserve");

		return "reserve";
	}

	@RequestMapping("/dao/reserve")
	public String reserveSeatsFromDAOModule(Model model,
			@RequestParam(value = "seatHoldId", required = true, defaultValue = "0") String seatHoldId,
			@RequestParam(value = "email", required = true, defaultValue = "") String customerEmail) {

		try {
			model.addAttribute("seatmessage", service2.reserveSeats(UUID.fromString(seatHoldId), customerEmail));
		} catch (IllegalArgumentException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("url", "/dao/reserve");
			return "reserve";
		}
		log.info("Execute reserveSeatsFromDAOModule");
		model.addAttribute("url", "/dao/startBookings");

		return "confirm";
	}

	@RequestMapping("/hz/startBookings")
	public String startBookings(Model model) {
		model.addAttribute("numofseats", service.numSeatsAvailable());
		model.addAttribute("url", "/hz/holdSeat");
		log.info("Execute startBookings");
		return "input";
	}

	@RequestMapping("/hz/holdSeat")
	public String holdSeats(Model model,
			@RequestParam(value = "noOfSeats", required = true, defaultValue = "0") Integer numOfSeats,
			@RequestParam(value = "email", required = true, defaultValue = "") String customerEmail) {
		try {
			model.addAttribute("seatHold", service.findAndHoldSeats(numOfSeats, customerEmail));
		} catch (IllegalArgumentException ex) {
			model.addAttribute("numofseats", service.numSeatsAvailable());
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("url", "/hz/holdSeat");
			return "input";
		}
		model.addAttribute("url", "/hz/reserve");
		log.info("Execute holdSeats");
		return "reserve";
	}

	
	@RequestMapping("/hz/reserve")
	public String reserveSeats(Model model,
			@RequestParam(value = "seatHoldId", required = true, defaultValue = "0") String seatHoldId,
			@RequestParam(value = "email", required = true, defaultValue = "") String customerEmail) {

		try {
			model.addAttribute("seatmessage", service.reserveSeats(UUID.fromString(seatHoldId), customerEmail));
		} catch (IllegalArgumentException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("url", "/hz/reserve");
			return "reserve";
		}
		log.info("Execute reserveSeatsFromDAOModule");
		model.addAttribute("url", "/hz/startBookings");

		return "confirm";
	}

	
}
