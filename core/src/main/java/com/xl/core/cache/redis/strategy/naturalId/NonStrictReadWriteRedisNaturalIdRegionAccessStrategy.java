package com.xl.core.cache.redis.strategy.naturalId;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.region.RedisNaturalIdRegion;
import com.xl.core.cache.redis.strategy.RedisAccessStrategy;

public class NonStrictReadWriteRedisNaturalIdRegionAccessStrategy extends RedisAccessStrategy<RedisNaturalIdRegion> implements NaturalIdRegionAccessStrategy {

	public NonStrictReadWriteRedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region, Settings settings) {
		super(region, settings);
	}

	public Object get(Object key, long txTimestamp) throws CacheException {
		return this.region().get(key);
	}

	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
		if (minimalPutOverride && region().contains(key)) {
			return false;
		} else {
			region().put(key, value);
			return true;
		}
	}

	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return null;
	}

	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		this.region().remove(key);
	}

	public NaturalIdRegion getRegion() {
		return this.region();
	}

	public boolean insert(Object key, Object value) throws CacheException {
		return false;
	}

	public boolean afterInsert(Object key, Object value) throws CacheException {
		return false;
	}

	public boolean update(Object key, Object value) throws CacheException {
		this.remove(key);
		return false;
	}

	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
		this.unlockItem(key, lock);
		return false;
	}

}
