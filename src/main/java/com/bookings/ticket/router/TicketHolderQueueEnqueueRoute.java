/*
 * Java filename    TicketHolderQueueEnqueueRoute.java enQueue route for queuing the requests.
 *
 *
 */
package com.bookings.ticket.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component("ticketHolderQueueEnqueueRoute")
public class TicketHolderQueueEnqueueRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("direct:httpCall")
		.log("pushing to QUEUE")
		.to("activemq:holdTIckets");
	}

}
