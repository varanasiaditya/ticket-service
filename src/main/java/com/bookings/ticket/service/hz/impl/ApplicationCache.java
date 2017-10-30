/*
 * Java filename    ApplicationCache.java<K,V> generic type of key and value
 *
 *
 */

package com.bookings.ticket.service.hz.impl;

import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.bookings.ticket.service.hz.IApplicationCache;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Component
public class ApplicationCache<K, V> implements IApplicationCache<K, V> {

	private static final String HZ_COMMON = " HAZELCAST API CALL  ";

	static Logger log = Logger.getLogger(ApplicationCache.class.getName());

	protected HazelcastInstance hazelcast;

	/**
	 * Default constructor.
	 */
	public ApplicationCache() {
		hazelcast = Hazelcast.newHazelcastInstance();
	}

	/**
	 * Create a cache for Map.
	 * 
	 * @param cacheName
	 */
	@Override
	public void createCache(String cacheName) {

		hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + "Cache Created with Name " + cacheName);
	}

	/**
	 * Fetch Hazelcast MAP.
	 * 
	 * @param cacheName
	 * @return
	 */
	@Override
	public IMap<K, V> getDataMap(String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + "Cache getDataMap for " + cacheName);
		return map;
	}

	/**
	 * Get value from Cache.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	@Override
	public void put(K key, V value, String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + "Put Key" + key + " in Cache " + cacheName);
		map.put(key, value);

		map.flush();
	}

	/**
	 * Put value in cache and expire in give time and time unit.
	 * 
	 * @param key
	 * @param value
	 * @param cacheName
	 * @param timeToLive
	 * @param timeUnit
	 */
	@Override
	public void put(K key, V value, String cacheName, long timeToLive, TimeUnit timeUnit) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + "Put Key" + key + " in Cache " + cacheName);
		map.put(key, value, timeToLive, timeUnit);
	}

	/**
	 * Get value from Cache.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	@Override
	public V get(K key, String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + "get Key " + key + " in Cache " + cacheName);
		return map.get(key);

	}

	/**
	 * Remove value from cache and get value.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	@Override
	public void remove(K key, String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + " remove entry for Key " + key + " in Cache " + cacheName);
		map.remove(key);
	}

	/**
	 * value from cache and get value.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	@Override
	public boolean tryLock(K key, String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + " tryLock for Key " + key + " in Cache " + cacheName);
		if (map != null && !map.isEmpty()) {
			try {
				return map.tryLock(key, 10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * value from cache and get value.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	@Override
	public void unLock(K key, String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + " unLock for Key " + key + " in Cache " + cacheName);
		if (map == null || map.isEmpty()) {
			return;
		}

		if (map.isLocked(key)) {
			try {
				map.unlock(key);
			} catch (IllegalMonitorStateException ex) {
				log.debug("Lock Obtained by different thread.");
			}
		}
	}

	/**
	 * value from cache and get value.
	 * 
	 * @param t
	 * @param cacheName
	 * @return
	 */
	@Override
	public void removeCache(String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + " remove  Cache " + cacheName);
		map.destroy();
	}

	/**
	 * return key set.
	 * 
	 * @param cacheName
	 * @return
	 */
	@Override
	public Set<K> getkeys(String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + " getKeys Cache " + cacheName);
		return map.keySet();
	}

	/**
	 * return key set.
	 * 
	 * @param cacheName
	 * @return
	 */
	@Override
	public TreeSet<K> getkeysSorted(String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + " getKeys Cache " + cacheName);
		if (map != null && map.keySet() != null && map.keySet().size() > 0) {
			return new TreeSet<>(map.keySet());
		} else {
			return null;
		}
	}

	/**
	 * remove the entry from cache.
	 * 
	 * @param key
	 *            key.
	 * @param cacheName
	 *            cache Name.
	 */
	@Override
	public void removeEntry(K key, String cacheName) {

		IMap<K, V> map = hazelcast.getMap(cacheName);
		log.debug(HZ_COMMON + " removeEntry for Key" + key + " in Cache " + cacheName);
		map.remove(key);
	}

	/**
	 * Destroy hazelcast gracefully.
	 */
	@Override
	public void disconnect() {
		log.debug(HZ_COMMON + " disconnecting");
		try {
			this.hazelcast.shutdown();
			this.hazelcast.getLifecycleService().shutdown();

		} catch (Exception e) {
			throw new IllegalArgumentException("Hazelcast could not shutdown due to : " + e.getMessage());
		}
	}
}