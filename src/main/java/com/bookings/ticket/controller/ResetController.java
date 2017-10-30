package com.bookings.ticket.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bookings.ticket.model.SeatHold;
import com.bookings.ticket.model.SeatInfo;
import com.bookings.ticket.service.dao.TicketServiceDao;
import com.bookings.ticket.service.hz.IApplicationCache;

@Controller
public class ResetController {

	@Autowired
	TicketServiceDao dao;
	
	@Autowired
	protected IApplicationCache<String, SeatInfo> cache;

	@Autowired
	protected IApplicationCache<UUID, SeatHold> seatHolderCache;

	
	@RequestMapping("/resetAll")
	public String resetAllSeats() {
		List<SeatInfo> infos = dao.getSeats();
		if(infos != null)
		for (SeatInfo seatInfo : infos) {
			seatInfo.setStatus(SeatInfo.AVILABLE);
			dao.updateSeatInfo(seatInfo);
		}

		Set<String> keys = cache.getkeys(IApplicationCache.SEAT_INFO);
		for (String key : keys) {
			SeatInfo value = cache.get(key, IApplicationCache.SEAT_INFO);
			value.setStatus(SeatInfo.AVILABLE);
			cache.put(key, value, IApplicationCache.SEAT_INFO);
		}
		
		cache.removeCache(IApplicationCache.HOLD_SEAT_INFO);
		
		return "reset";
	}
}
