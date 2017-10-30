/*
 * Java filename    TicketServiceStarter.java for Spring boot main class.
 *
 *
 */
package com.bookings.ticket;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

@SpringBootApplication(scanBasePackages = { "com.bookings.ticket" })

public class TicketServiceStarter extends SpringBootServletInitializer {


	static Logger log = Logger.getLogger(TicketServiceStarter.class.getName());

	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TicketServiceStarter.class);
	}

	public static void main(String[] args) {
		ApplicationContext ctx =  new FileSystemXmlApplicationContext("broker.xml");
		SpringApplication.run(TicketServiceStarter.class, args);
	}

}
