/*
 * Java filename    QueueController.java for controller layer class for Queue based.
 *
 *
 */

package com.bookings.ticket.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookings.ticket.service.TicketService;

@Controller
public class QueueController {

	@Produce(uri = "direct:httpCall")
	private ProducerTemplate template;

	@Autowired
	@Qualifier("ticketServiceUsingDaoServiceImpl")
	TicketService service;



	static Logger log = Logger.getLogger(TicketController.class.getName());
	
	@RequestMapping("/async/startBookings")
	public String startBookingsFromDAOModule(Model model) {
		model.addAttribute("numofseats", service.numSeatsAvailable());
		model.addAttribute("url", "/async/holdSeatEmail");
		log.info("Execute startBookingsFromDAOModule");
		return "input";
	}

	@RequestMapping("/async/holdSeatEmail")
	public String processHoldSeat(Model model,
			@RequestParam(value = "noOfSeats", required = true, defaultValue = "0") Integer numOfSeats,
			@RequestParam(value = "email", required = true, defaultValue = "") String customerEmail) {
		
		int seatsAvilableCount = service.numSeatsAvailable();

		if (numOfSeats <= 0 || seatsAvilableCount <= 0 || numOfSeats > seatsAvilableCount) {
			model.addAttribute("numofseats", seatsAvilableCount);
			model.addAttribute("error","Invalid number of seats or Seats full " + numOfSeats);
			model.addAttribute("url", "/async/holdSeatEmail");
			return "input";
		}


		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("NO_OF_SEATS", String.valueOf(numOfSeats));
		dataMap.put("EMAIL", customerEmail);
		// Below method call will invoke route 'direct:start' from DmsRouteBuilder.
		template.sendBody(template.getDefaultEndpoint(), dataMap);

		model.addAttribute("url", "/async/reserve");
		return "process_ack";
	}
	
	
	@RequestMapping("/async/reserve")
	public String reserveSeats(Model model,
			@RequestParam(value = "seatHoldId", required = true, defaultValue = "0") String seatHoldId,
			@RequestParam(value = "email", required = true, defaultValue = "") String customerEmail) {

		try {
			model.addAttribute("seatmessage", service.reserveSeats(UUID.fromString(seatHoldId), customerEmail));
		} catch (IllegalArgumentException ex) {
			model.addAttribute("error", ex.getMessage());
			model.addAttribute("url", "/async/startBookings");
			return "reserve";
		}
		model.addAttribute("url", "/async/startBookings");
		model.addAttribute("table", "");
		log.info("Execute reserveSeats");
		return "confirm";
	}



}
