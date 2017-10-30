/*
 * Java filename    SeatInfo.java Basic seat info like row, seat number
 *
 *
 */
package com.bookings.ticket.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "SeatInfo")
@Cacheable
public class SeatInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5073796890419714779L;

	public static final String AVILABLE = "avilable";
	public static final String HOLD = "hold";
	public static final String RESERVED = "reserved";



	@Id
	private String seatInfoId;

	@Column(name = "seat_number")
	private Integer seatNumber;

	@Column(name = "seat_row")
	private String seatRow;

	@Column(name = "rate")
	private String seatCost;

	@Column(name = "status")
	private String status;
	
	@Column(name = "seatholdId")
	private String seatHoldUniqueId;
	
	@Column(name = "holdTime")
	private Timestamp holdTime;

	public SeatInfo() {

	}

	/**
	 * @param seatId
	 * @param seatRow
	 * @param seatCost
	 */
	public SeatInfo(Integer seatNumber, String seatRow, String seatCost, String status) {
		super();
		this.seatNumber = seatNumber;
		this.seatRow = seatRow;
		this.seatCost = seatCost;
		this.status = status;
		this.setSeatInfoId(this.seatRow + "" + this.seatNumber);
	}

	/**
	 * @return the seatId
	 */
	public Integer getSeatNumber() {
		return seatNumber;
	}

	/**
	 * @param seatId
	 *            the seatId to set
	 */
	public void setSeatNumber(Integer seatNumber) {
		this.seatNumber = seatNumber;
	}

	/**
	 * @return the seatRow
	 */
	public String getSeatRow() {
		return seatRow;
	}

	/**
	 * @param seatRow
	 *            the seatRow to set
	 */
	public void setSeatRow(String seatRow) {
		this.seatRow = seatRow;
	}

	/**
	 * @return the seatCost
	 */
	public String getSeatCost() {
		return seatCost;
	}

	/**
	 * @param seatCost
	 *            the seatCost to set
	 */
	public void setSeatCost(String seatCost) {
		this.seatCost = seatCost;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @param seatInfoId
	 *            the seatInfoId to set
	 */
	public void setSeatInfoId(String seatInfoId) {
		this.seatInfoId = seatInfoId;
	}

	/**
	 * @return the seatInfoId
	 */
	public String getSeatInfoId() {
		return this.seatInfoId;
	}

	/**
	 * @return the seatHoldUniqueId
	 */
	public String getSeatHoldUniqueId() {
		return seatHoldUniqueId;
	}

	/**
	 * @param seatHoldUniqueId
	 *            the seatHoldUniqueId to set
	 */
	public void setSeatHoldUniqueId(String seatHoldUniqueId) {
		this.seatHoldUniqueId = seatHoldUniqueId;
	}


	
	/**
	 * @return the holdTime
	 */
	public Timestamp getHoldTime() {
		return holdTime;
	}

	/**
	 * @param holdTime the holdTime to set
	 */
	public void setHoldTime(Timestamp holdTime) {
		this.holdTime = holdTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seatInfoId == null) ? 0 : seatInfoId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SeatInfo other = (SeatInfo) obj;
		if (seatInfoId == null) {
			if (other.seatInfoId != null)
				return false;
		} else if (!seatInfoId.equals(other.seatInfoId))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SeatInfo [seatNumber=" + seatNumber + ", seatRow=" + seatRow + ", seatCost=" + seatCost + ", status="
				+ status + ", seatInfoId=" + seatInfoId + ", seatHoldUniqueId=" + seatHoldUniqueId + "]";
	}

}
