package com.xl.core.cache.redis.strategy.collection;

import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.region.RedisCollectionRegion;
import com.xl.core.cache.redis.strategy.ReadWriteRedisAccessStrategy;

public class ReadWriteRedisCollectionRegionAccessStrategy extends ReadWriteRedisAccessStrategy<RedisCollectionRegion> implements CollectionRegionAccessStrategy {

	public ReadWriteRedisCollectionRegionAccessStrategy(RedisCollectionRegion region, Settings settings) {
		super(region, settings);
	}

	public CollectionRegion getRegion() {
		return this.region();
	}


}
