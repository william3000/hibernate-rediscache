package com.xl.core.cache.redis.strategy.nonstop;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

import com.xl.core.cache.exception.HibernateNonstopCacheExceptionHandler;

public class RedisNonstopAwardEntityRegionAccessStrategy implements EntityRegionAccessStrategy {

	private final EntityRegionAccessStrategy actualStrategy;
	private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;
	
	public RedisNonstopAwardEntityRegionAccessStrategy(EntityRegionAccessStrategy actualStrategy, HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
		super();
		this.actualStrategy = actualStrategy;
		this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
	}

	public Object get(Object key, long txTimestamp) throws CacheException {
		return this.actualStrategy.get(key, txTimestamp);
	}

	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
		return this.actualStrategy.putFromLoad(key, value, txTimestamp, version);
	}

	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
		return this.actualStrategy.putFromLoad(key, value, txTimestamp, version, minimalPutOverride);
	}

	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return this.actualStrategy.lockItem(key, version);
	}

	public SoftLock lockRegion() throws CacheException {
		return this.actualStrategy.lockRegion();
	}

	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		this.actualStrategy.unlockItem(key, lock);
	}

	public void unlockRegion(SoftLock lock) throws CacheException {
		this.actualStrategy.unlockRegion(lock);
	}

	public void remove(Object key) throws CacheException {
		this.actualStrategy.remove(key);
	}

	public void removeAll() throws CacheException {
		this.actualStrategy.removeAll();
	}

	public void evict(Object key) throws CacheException {
		this.actualStrategy.evict(key);
	}

	public void evictAll() throws CacheException {
		this.actualStrategy.evictAll();
	}

	public EntityRegion getRegion() {
		return this.actualStrategy.getRegion();
	}

	public boolean insert(Object key, Object value, Object version) throws CacheException {
		return this.actualStrategy.insert(key, value, version);
	}

	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
		return this.actualStrategy.afterInsert(key, value, version);
	}

	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
		return this.actualStrategy.update(key, value, currentVersion, previousVersion);
	}

	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
		return this.actualStrategy.afterUpdate(key, value, currentVersion, previousVersion, lock);
	}

}
