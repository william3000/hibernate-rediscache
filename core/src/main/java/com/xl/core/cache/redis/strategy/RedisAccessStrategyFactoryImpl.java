package com.xl.core.cache.redis.strategy;

import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

import com.xl.core.cache.redis.region.RedisCollectionRegion;
import com.xl.core.cache.redis.region.RedisEntityRegion;
import com.xl.core.cache.redis.region.RedisNaturalIdRegion;
import com.xl.core.cache.redis.strategy.collection.NonStrictReadWriteRedisCollectionRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.collection.ReadOnlyRedisCollectionRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.collection.ReadWriteRedisCollectionRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.collection.TransactionalRedisCollectionRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.entity.NonStrictReadWriteRedisEntityRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.entity.ReadOnlyRedisEntityRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.entity.ReadWriteRedisEntityRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.entity.TransactionalRedisEntityRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.naturalId.NonStrictReadWriteRedisNaturalIdRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.naturalId.ReadOnlyRedisNaturalIdRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.naturalId.ReadWriteRedisNaturalIdRegionAccessStrategy;
import com.xl.core.cache.redis.strategy.naturalId.TransactionalRedisNaturalIdRegionAccessStrategy;

public class RedisAccessStrategyFactoryImpl implements RedisAccessStrategyFactory {

	public EntityRegionAccessStrategy createEntityRegionAccessStrategy(RedisEntityRegion entityRegion, AccessType accessType) {
		switch (accessType) {
			case READ_ONLY:
				return new ReadOnlyRedisEntityRegionAccessStrategy(entityRegion,entityRegion.getSettings());
			case READ_WRITE:
				return new ReadWriteRedisEntityRegionAccessStrategy(entityRegion,entityRegion.getSettings());
			case NONSTRICT_READ_WRITE:
				return new NonStrictReadWriteRedisEntityRegionAccessStrategy(entityRegion,entityRegion.getSettings());
			case TRANSACTIONAL:
				return new TransactionalRedisEntityRegionAccessStrategy(entityRegion,entityRegion.getSettings());
			default:
				throw new IllegalArgumentException( "unrecognized access strategy type [" + accessType + "]" );
		}
	}

	public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(RedisCollectionRegion collectionRegion, AccessType accessType) {
		switch (accessType) {
		case READ_ONLY:
			return new ReadOnlyRedisCollectionRegionAccessStrategy(collectionRegion,collectionRegion.getSettings());
		case READ_WRITE:
			return new ReadWriteRedisCollectionRegionAccessStrategy(collectionRegion,collectionRegion.getSettings());
		case NONSTRICT_READ_WRITE:
			return new NonStrictReadWriteRedisCollectionRegionAccessStrategy(collectionRegion,collectionRegion.getSettings());
		case TRANSACTIONAL:
			return new TransactionalRedisCollectionRegionAccessStrategy(collectionRegion,collectionRegion.getSettings());
		default:
			throw new IllegalArgumentException( "unrecognized access strategy type [" + accessType + "]" );
		}
	}

	public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(RedisNaturalIdRegion naturalIdRegion, AccessType accessType) {
		switch (accessType) {
		case READ_ONLY:
			return new ReadOnlyRedisNaturalIdRegionAccessStrategy(naturalIdRegion,naturalIdRegion.getSettings());
		case READ_WRITE:
			return new ReadWriteRedisNaturalIdRegionAccessStrategy(naturalIdRegion,naturalIdRegion.getSettings());
		case NONSTRICT_READ_WRITE:
			return new NonStrictReadWriteRedisNaturalIdRegionAccessStrategy(naturalIdRegion,naturalIdRegion.getSettings());
		case TRANSACTIONAL:
			return new TransactionalRedisNaturalIdRegionAccessStrategy(naturalIdRegion,naturalIdRegion.getSettings());
		default:
			throw new IllegalArgumentException( "unrecognized access strategy type [" + accessType + "]" );
		}
	}

}
