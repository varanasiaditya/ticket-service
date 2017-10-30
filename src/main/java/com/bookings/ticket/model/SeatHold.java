/*
 * Java filename    SeatHold.java Final object to reserve seats and notify customer.
 *
 *
 */
package com.bookings.ticket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeatHold implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2332233635169032731L;

	private List<String> seatInfo = new ArrayList<String>();

	private String customerEmail;

	private UUID seatId;

	/**
	 * @return the seatInfo
	 */
	public List<String> getSeatInfo() {
		return seatInfo;
	}

	/**
	 * @param seatInfo
	 *            the seatInfo to set
	 */
	public void setSeatInfo(List<String> seatInfo) {
		this.seatInfo = seatInfo;
	}

	/**
	 * @return the customerEmail
	 */
	public String getCustomerEmail() {
		return customerEmail;
	}

	/**
	 * @param customerEmail
	 *            the customerEmail to set
	 */
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	/**
	 * @return the seatId
	 */
	public UUID getSeatId() {
		return seatId;
	}

	/**
	 * @param seatId
	 *            the seatId to set
	 */
	public void setSeatId(UUID seatId) {
		this.seatId = seatId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SeatHold [seatInfo=" + seatInfo + ", customerEmail=" + customerEmail + ", seatId=" + seatId + "]";
	}

	

}
