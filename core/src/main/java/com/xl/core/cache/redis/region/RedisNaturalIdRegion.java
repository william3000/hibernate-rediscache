package com.xl.core.cache.redis.region;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;

public class RedisNaturalIdRegion extends RedisTransactionalDataRegion implements NaturalIdRegion {

	public RedisNaturalIdRegion(RedisAccessStrategyFactory accessStrategyFactory, RedisCache cache, Settings settings, Properties properties, CacheDataDescription metadata) {
		super(accessStrategyFactory, cache, settings,properties, metadata);
	}

	public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return this.getAccessStrategyFactory().createNaturalIdRegionAccessStrategy(this, accessType);
	}

}
