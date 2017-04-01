package com.xl.core.cache.redis.region;

import java.util.Properties;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;

public class RedisCollectionRegion extends RedisTransactionalDataRegion implements CollectionRegion {

	public RedisCollectionRegion(RedisAccessStrategyFactory accessStrategyFactory, RedisCache cache, Settings settings, Properties properties, CacheDataDescription metadata) {
		super(accessStrategyFactory, cache, settings, properties, metadata);
	}

	public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
		return this.getAccessStrategyFactory().createCollectionRegionAccessStrategy(this, accessType);
	}

}
