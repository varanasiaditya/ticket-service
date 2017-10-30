/*
 * Java filename    IApplicationCache.java<K,V> generic type of key and value
 *
 *
 */
package com.bookings.ticket.service.hz;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import com.hazelcast.core.IMap;

public interface IApplicationCache<K,V> {
	final String SEAT_INFO = "seatinfo";
	final String HOLD_SEAT_INFO = "seatheldinfo";

	/**
	 * Create a cache for Map.
	 * 
	 * @param cacheName
	 */
	public void createCache(String cacheName) ;

	/**
	 * Fetch Hazelcast MAP.
	 * @param cacheName
	 * @return
	 */
	public IMap<K,V> getDataMap(String cacheName);
	
	/**
	 * put value in to Cache.
	 * @param key
	 * @param value
	 * @param cacheName
	 */
	public void put(K key, V value, String cacheName) ;
	
	/**
	 * Put value in cache and expire in give time and time unit.
	 * @param key
	 * @param value
	 * @param cacheName
	 * @param timeToLive
	 * @param timeUnit
	 */
	void put(K key, V value, String cacheName, long timeToLive, TimeUnit timeUnit);
	
	/**
	 * Get value from Cache.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	public V get(K key, String cacheName) ;	
	
	
	/**
	 * Remove value from cache and get value.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	public void remove(K key, String cacheName);
	

	/**
	 *  Lock entry from cache.
	 * @param key
	 * @param cacheName
	 * @return
	 */
	public boolean tryLock(K key, String cacheName);
	
	/**
	 * unlock entry from cache.
	 * @param key
	 * @param cacheName
	 */
	public void unLock(K key, String cacheName);
	
	/**
	 * value from cache and get value.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	public void removeCache(String cacheName) ;
	
	/**
	 * return key set.
	 * @param cacheName
	 * @return
	 */
	public Set<K> getkeys(String cacheName);
	
	/**
	 * remove the entry from cache.
	 * @param key key.
	 * @param cacheName cache Name.
	 */
	public void removeEntry(K key,String cacheName);

	/**
	 * disconnect gracefully.
	 */
	public void disconnect();

	TreeSet<K> getkeysSorted(String cacheName);



}
