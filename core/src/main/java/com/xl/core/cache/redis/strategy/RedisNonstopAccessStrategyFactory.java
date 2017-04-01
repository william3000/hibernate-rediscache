package com.xl.core.cache.redis.strategy;

import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

import com.xl.core.cache.exception.HibernateNonstopCacheExceptionHandler;
import com.xl.core.cache.redis.region.RedisCollectionRegion;
import com.xl.core.cache.redis.region.RedisEntityRegion;
import com.xl.core.cache.redis.region.RedisNaturalIdRegion;
import com.xl.core.cache.redis.strategy.nonstop.RedisNonstopAwardCollectionRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.nonstop.RedisNonstopAwardEntityRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.nonstop.RedisNonstopAwardNaturalIdRegionAccessStrategy;

public class RedisNonstopAccessStrategyFactory implements RedisAccessStrategyFactory {

	private final RedisAccessStrategyFactory acturalFactory;
	
	public RedisNonstopAccessStrategyFactory(RedisAccessStrategyFactory acturalFactory) {
		this.acturalFactory = acturalFactory;
	}

	public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion, AccessType accessType) {
		EntityRegionAccessStrategy strategy = this.acturalFactory.createEntityRegionAccessStrategy(entityRegion, accessType);
//		System.out.println("Building Strategy:"+ strategy.getClass().getName());
		return new RedisNonstopAwardEntityRegionAccessStrategy(strategy,HibernateNonstopCacheExceptionHandler.getInstance());
	}

	public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion, AccessType accessType) {
		CollectionRegionAccessStrategy strategy = this.acturalFactory.createCollectionRegionAccessStrategy(collectionRegion, accessType);
//		System.out.println("Building Strategy:"+ strategy.getClass().getName());
		return new RedisNonstopAwardCollectionRegionAccessStrategy(strategy,HibernateNonstopCacheExceptionHandler.getInstance());
	}

	public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(RedisNaturalIdRegion naturalIdRegion, AccessType accessType) {
		NaturalIdRegionAccessStrategy strategy = this.acturalFactory.createNaturalIdRegionAccessStrategy(naturalIdRegion, accessType);
//		System.out.println("Building Strategy:"+ strategy.getClass().getName());
		return new RedisNonstopAwardNaturalIdRegionAccessStrategy(strategy,HibernateNonstopCacheExceptionHandler.getInstance());
	}

}
