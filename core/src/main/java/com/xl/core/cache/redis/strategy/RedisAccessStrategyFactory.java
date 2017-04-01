package com.xl.core.cache.redis.strategy;

import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

import com.xl.core.cache.redis.region.RedisCollectionRegion;
import com.xl.core.cache.redis.region.RedisEntityRegion;
import com.xl.core.cache.redis.region.RedisNaturalIdRegion;

public interface RedisAccessStrategyFactory {
	
	public EntityRegionAccessStrategy createEntityRegionAccessStrategy(
			RedisEntityRegion entityRegion,
			AccessType accessType);
	
	public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(
			RedisCollectionRegion collectionRegion,
			AccessType accessType);

	public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(
			RedisNaturalIdRegion naturalIdRegion,
			AccessType accessType);
}
