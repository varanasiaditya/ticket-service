/*
 * Java filename    TicketHolderQueueDequeueRoute.java deQueue route and hold the seats.
 *
 *
 */
package com.bookings.ticket.router;

import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bookings.ticket.model.SeatHold;
import com.bookings.ticket.service.TicketService;

@Component("ticketHolderQueueDequeueRoute")
public class TicketHolderQueueDequeueRoute extends RouteBuilder {
	
	@Autowired
	@Qualifier("ticketServiceUsingDaoServiceImpl")
	TicketService service;
	
	@Value("${uuid.files}")
	protected String fileLocation;

	@Override
	public void configure() throws Exception {
		onException(IllegalArgumentException.class)
		.handled(false)
		.setBody().simple("Error due to invalid seats or No seats Available ")
		.to("bean:ticketHolderQueueDequeueRoute?method=setFileName")
		.to("file:"+fileLocation);

		from("activemq:holdTIckets")
			.log("Poped from QUEUE")
			.to("bean:ticketHolderQueueDequeueRoute?method=setFileName")
			.to("bean:ticketHolderQueueDequeueRoute?method=holdTickets")
			//USING FILE Can used SMTP
			.to("file:"+fileLocation);
	}

	public void setFileName(Exchange ex, @Body Map<String, Object> dataMap) {
		ex.getIn().setHeader(Exchange.FILE_NAME, dataMap.get("EMAIL")+".txt");
	}

	public void setBody(Exchange ex, @Body Map<String, Object> dataMap) {
		ex.getIn().setBody("Please click on Link ");
	}
	
	public void holdTickets(Exchange ex, @Body Map<String, String> dataMap) {
		SeatHold hold = service.findAndHoldSeats(Integer.valueOf(dataMap.get("NO_OF_SEATS")), dataMap.get("EMAIL"));
		ex.getIn().setBody("Please use the UUID and reserve the seats :"+ hold.getSeatId());
	}

}
