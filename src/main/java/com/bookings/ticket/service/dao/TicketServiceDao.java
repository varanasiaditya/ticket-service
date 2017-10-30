package com.bookings.ticket.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.bookings.ticket.model.SeatInfo;

@Repository
public class TicketServiceDao {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * insert seatinfo
	 * 
	 * @param info
	 */
	@Transactional
	public void insert(SeatInfo info) {
		this.entityManager.merge(info);
	}

	/**
	 * Update seat info
	 * 
	 * @param info
	 * @return
	 */
	@Transactional
	public SeatInfo updateSeatInfo(SeatInfo info) {
		this.entityManager.merge(info);
		return info;
	}

	/**
	 * Get Seatinfo by ID
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public SeatInfo getSeatInfoById(String id) {
		return this.entityManager.find(SeatInfo.class, id);
	}

	/**
	 * Getseats
	 * 
	 * @param status
	 * @return
	 */
	@Transactional
	public List<SeatInfo> getSeats(String status) {
		String q = "FROM SeatInfo where status = :status order by seatRow,seatNumber asc";
		TypedQuery<SeatInfo> query = this.entityManager.createQuery(q, SeatInfo.class);
		query.setParameter("status", status);
		return query.getResultList();
	}

	/**
	 * Getseats
	 * 
	 * @param status
	 * @return
	 */
	// @Transactional
	public List<SeatInfo> getSeats() {
		String q = "FROM " + SeatInfo.class.getName() + " order by seatRow,seatNumber";
		TypedQuery<SeatInfo> query = this.entityManager.createQuery(q, SeatInfo.class);
		return query.getResultList();
	}

	/**
	 * 
	 * @param seatInfos
	 */
	@Transactional
	public void clearDB() {

		List<SeatInfo> seatInfos = getSeats();
		if (seatInfos != null) {
			for (SeatInfo seatInfo : seatInfos) {
				this.entityManager.remove(seatInfo);

			}
		}
	}

	/**
	 * 
	 * @param UUID
	 * @return
	 */
	public List<SeatInfo> getSeatsByUUID(String UUID) {
		String q = "FROM SeatInfo where seatHoldUniqueId = :seatHoldUniqueId order by seatRow,seatNumber";
		TypedQuery<SeatInfo> query = this.entityManager.createQuery(q, SeatInfo.class);
		query.setParameter("seatHoldUniqueId", UUID);
		return query.getResultList();
	}
}
