package com.xl.core.cache.redis.strategy.nonstop;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;

import com.xl.core.cache.exception.HibernateNonstopCacheExceptionHandler;
import com.xl.core.cache.exception.NonStopCacheException;

public class RedisNonstopAwardNaturalIdRegionAccessStrategy implements NaturalIdRegionAccessStrategy {

	private final NaturalIdRegionAccessStrategy actualStrategy;
	private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;

	public RedisNonstopAwardNaturalIdRegionAccessStrategy(NaturalIdRegionAccessStrategy actualStrategy, HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
		super();
		this.actualStrategy = actualStrategy;
		this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
	}

	public boolean insert(Object key, Object value) throws CacheException {
		try {
			return actualStrategy.insert(key, value);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return false;
		}
	}

	public boolean afterInsert(Object key, Object value) throws CacheException {
		try {
			return actualStrategy.afterInsert(key, value);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return false;
		}
	}

	public boolean update(Object key, Object value) throws CacheException {
		try {
			return actualStrategy.update(key, value);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return false;
		}
	}

	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
		try {
			return actualStrategy.afterUpdate(key, value, lock);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return false;
		}
	}

	public NaturalIdRegion getRegion() {
		return actualStrategy.getRegion();
	}

	public void evict(Object key) throws CacheException {
		try {
			actualStrategy.evict(key);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
		}
	}

	public void evictAll() throws CacheException {
		try {
			actualStrategy.evictAll();
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
		}
	}

	public Object get(Object key, long txTimestamp) throws CacheException {
		try {
			return actualStrategy.get(key, txTimestamp);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return null;
		}
	}

	public SoftLock lockItem(Object key, Object version) throws CacheException {
		try {
			return actualStrategy.lockItem(key, version);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return null;
		}
	}

	public SoftLock lockRegion() throws CacheException {
		try {
			return actualStrategy.lockRegion();
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return null;
		}
	}

	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
		try {
			return actualStrategy.putFromLoad(key, value, txTimestamp, version, minimalPutOverride);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return false;
		}
	}

	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
		try {
			return actualStrategy.putFromLoad(key, value, txTimestamp, version);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
			return false;
		}
	}

	public void remove(Object key) throws CacheException {
		try {
			actualStrategy.remove(key);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
		}
	}

	public void removeAll() throws CacheException {
		try {
			actualStrategy.removeAll();
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
		}
	}

	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		try {
			actualStrategy.unlockItem(key, lock);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
		}
	}

	public void unlockRegion(SoftLock lock) throws CacheException {
		try {
			actualStrategy.unlockRegion(lock);
		} catch (NonStopCacheException nonStopCacheException) {
			hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
		}
	}

}
