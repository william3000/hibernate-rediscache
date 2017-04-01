package com.xl.core.cache.redis.strategy.entity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.lock.Item;
import com.xl.core.cache.redis.lock.Lock;
import com.xl.core.cache.redis.lock.Lockable;
import com.xl.core.cache.redis.region.RedisEntityRegion;
import com.xl.core.cache.redis.strategy.ReadWriteRedisAccessStrategy;

public class ReadWriteRedisEntityRegionAccessStrategy extends ReadWriteRedisAccessStrategy<RedisEntityRegion> implements EntityRegionAccessStrategy {
	
	private final static Log logger = LogFactory.getLog(ReadWriteRedisEntityRegionAccessStrategy.class);	

	public ReadWriteRedisEntityRegionAccessStrategy(RedisEntityRegion region, Settings settings) {
		super(region, settings);
	}

	public EntityRegion getRegion() {
		return this.region();
	}

	public boolean insert(Object key, Object value, Object version) throws CacheException {
		return false;
	}

	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
		
		logger.info("ReadWriteRedisEntityRegionAccessStrategy.afterInsert:" + this.region().getName() + " " + String.valueOf(key) );
		
		this.region().writeLock(key);
		try {
			final Lockable item = (Lockable) this.region().get(key);
			if (item == null) {
				this.region().put(key, new Item(value, version, region().nextTimestamp()));
				return true;
			} else {
				return false;
			}
		} finally {
			this.region().writeUnLock(key);
		}
	}

	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
		return false;
	}

	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
		
		logger.info("ReadWriteRedisEntityRegionAccessStrategy.afterUpdate:" + this.region().getName() + " " + String.valueOf(key) );
		
		region().writeLock(key);
		try {
			final Lockable item = (Lockable) region().get(key);

			if (item != null && item.isUnlockable(lock)) {
				final Lock lockItem = (Lock) item;
				if (lockItem.wasLockedConcurrently()) {
					decrementLock(key, lockItem);
					return false;
				} else {
					region().put(key, new Item(value, currentVersion, region().nextTimestamp()));
					return true;
				}
			} else {
				handleLockExpiry(key, item);
				return false;
			}
		} finally {
			region().writeUnLock(key);
		}
	}

}
