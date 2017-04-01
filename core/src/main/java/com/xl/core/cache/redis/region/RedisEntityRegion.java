package com.xl.core.cache.redis.region;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;

public class RedisEntityRegion extends RedisTransactionalDataRegion implements EntityRegion {
	
	public RedisEntityRegion(
			RedisAccessStrategyFactory accessStrategyFactory, 
			RedisCache cache, 
			Settings settings, 
			Properties properties,
			CacheDataDescription metadata) {
		super(accessStrategyFactory, cache, settings, properties,metadata);
	}

	public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return this.getAccessStrategyFactory().createEntityRegionAccessStrategy(this, accessType);
	}

}
