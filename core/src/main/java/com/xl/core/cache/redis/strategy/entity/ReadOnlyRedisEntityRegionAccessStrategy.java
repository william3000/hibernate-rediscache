package com.xl.core.cache.redis.strategy.entity;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.region.RedisEntityRegion;
import com.xl.core.cache.redis.strategy.RedisAccessStrategy;

public class ReadOnlyRedisEntityRegionAccessStrategy extends RedisAccessStrategy<RedisEntityRegion> implements EntityRegionAccessStrategy {

	public ReadOnlyRedisEntityRegionAccessStrategy(RedisEntityRegion region, Settings settings) {
		super(region, settings);
	}

	public Object get(Object key, long txTimestamp) throws CacheException {
		return this.region().get(key);
	}

	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
		if ( minimalPutOverride && region().contains( key ) ) {
			return false;
		}
		else {
			region().put( key, value );
			return true;
		}
	}

	public SoftLock lockItem(Object key, Object version) throws CacheException {
		return null;
	}

	public void unlockItem(Object key, SoftLock lock) throws CacheException {
		this.evict(key);

	}

	public boolean insert(Object key, Object value, Object version) throws CacheException {
		return false;
	}

	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
		this.region().put(key, value);
		return false;
	}

	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
		throw new UnsupportedOperationException( "Can't write to a readonly object" );
	}

	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
		throw new UnsupportedOperationException( "Can't write to a readonly object" );
	}

	public EntityRegion getRegion() {
		return this.region();
	}

}
