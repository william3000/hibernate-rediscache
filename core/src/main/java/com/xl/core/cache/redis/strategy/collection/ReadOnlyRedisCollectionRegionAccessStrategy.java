package com.xl.core.cache.redis.strategy.collection;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.region.RedisCollectionRegion;
import com.xl.core.cache.redis.strategy.RedisAccessStrategy;

public class ReadOnlyRedisCollectionRegionAccessStrategy extends RedisAccessStrategy<RedisCollectionRegion> implements CollectionRegionAccessStrategy {

	public ReadOnlyRedisCollectionRegionAccessStrategy(RedisCollectionRegion region, Settings settings) {
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
		//no-op
	}

	public CollectionRegion getRegion() {
		return this.region();
	}

}
