package com.xl.core.cache.redis.strategy.naturalId;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.lock.Item;
import com.xl.core.cache.redis.lock.Lock;
import com.xl.core.cache.redis.lock.Lockable;
import com.xl.core.cache.redis.region.RedisNaturalIdRegion;
import com.xl.core.cache.redis.strategy.ReadWriteRedisAccessStrategy;

public class ReadWriteRedisNaturalIdRegionAccessStrategy extends ReadWriteRedisAccessStrategy<RedisNaturalIdRegion> implements NaturalIdRegionAccessStrategy {

	public ReadWriteRedisNaturalIdRegionAccessStrategy(RedisNaturalIdRegion region, Settings settings) {
		super(region, settings);
	}


	public NaturalIdRegion getRegion() {
		return this.region();
	}

	public boolean insert(Object key, Object value) throws CacheException {
		return false;
	}

	public boolean afterInsert(Object key, Object value) throws CacheException {
		region().writeLock( key );
		try {
			final Lockable item = (Lockable) region().get( key );
			if ( item == null ) {
				region().put( key, new Item( value, null, region().nextTimestamp() ) );
				return true;
			}
			else {
				return false;
			}
		}
		finally {
			region().writeUnLock( key );
		}
	}

	public boolean update(Object key, Object value) throws CacheException {
		return false;
	}

	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
		region().writeLock( key );
		try {
			final Lockable item = (Lockable) region().get( key );

			if ( item != null && item.isUnlockable( lock ) ) {
				final Lock lockItem = (Lock) item;
				if ( lockItem.wasLockedConcurrently() ) {
					decrementLock( key, lockItem );
					return false;
				}
				else {
					region().put( key, new Item( value, null, region().nextTimestamp() ) );
					return true;
				}
			}
			else {
				handleLockExpiry( key, item );
				return false;
			}
		}
		finally {
			region().writeUnLock( key );
		}
	}


}
