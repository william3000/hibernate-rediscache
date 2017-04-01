package com.xl.core.cache.redis;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;
import org.springframework.util.Assert;

import com.xl.core.asyn.AsynFactory;
import com.xl.core.cache.config.Configuration;
import com.xl.core.cache.config.ConfigurationFactory;
import com.xl.core.cache.redis.common.CacheManagerFactory;
import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.region.RedisCollectionRegion;
import com.xl.core.cache.redis.region.RedisEntityRegion;
import com.xl.core.cache.redis.region.RedisNaturalIdRegion;
import com.xl.core.cache.redis.region.RedisQueryResultsRegion;
import com.xl.core.cache.redis.region.RedisTimestampsRegion;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactoryImpl;
import com.xl.core.cache.redis.strategy.RedisNonstopAccessStrategyFactory;

public class RedisCacheRegionFactory implements RegionFactory {
	
	private final static Log logger = LogFactory.getLog(RedisCacheRegionFactory.class);	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1737124286410518824L;

//	private volatile CacheManager cacheManager;
	
	private static final AtomicLong CURRENT = new AtomicLong();
	
	protected Settings settings;
	
	protected final RedisAccessStrategyFactory accessStrategyFactory = 
			new RedisNonstopAccessStrategyFactory( new RedisAccessStrategyFactoryImpl());
	
	protected Configuration configuration;
	
	
	

	public void start(Settings settings, Properties properties) throws CacheException {
		
//		AsynFactory.initAsyn();
		
		CacheManagerFactory.initCacheManager(properties);
		
		//will raise error when redis is down
		try{
			CacheManagerFactory.getCacheManager().testRedis();
		}catch(RuntimeException e){
			e.printStackTrace();
			throw new CacheException(e.getMessage());
		}

		this.settings = settings;
		ConfigurationFactory.initConfiguration();
		Assert.notNull(CacheManagerFactory.getCacheManager(), "cacheManager is required");
		logger.info("Redis as cache , bingo");
	}

	public void stop() {
//		AsynFactory.getAsynService().close(1);
		CacheManagerFactory.getCacheManager().closeRedis();
		CacheManagerFactory.shutdown();
	}

	public boolean isMinimalPutsEnabledByDefault() {
		return true;
	}

	public AccessType getDefaultAccessType() {
		return AccessType.READ_WRITE;
	}

	public long nextTimestamp() {
		return CURRENT.incrementAndGet();
	}

	public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		RedisCache cache = this.getCache(regionName);
//		System.out.println("building EntityRegion:"+regionName);
		return new RedisEntityRegion(accessStrategyFactory, cache, settings, properties,metadata);
	}

	public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		RedisCache cache = this.getCache(regionName);
//		System.out.println("building NaturalIdRegion:"+regionName);
		return new RedisNaturalIdRegion(accessStrategyFactory, cache, settings, properties, metadata);

	}

	public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
		RedisCache cache = this.getCache(regionName);
//		System.out.println("building CollectionRegion:"+regionName);
		return new RedisCollectionRegion(accessStrategyFactory, cache, settings, properties,metadata);

	}

	public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
		RedisCache cache = this.getCache(regionName);
//		System.out.println("building QueryResultsRegion:"+regionName);
		return new RedisQueryResultsRegion(accessStrategyFactory, cache, settings, properties);

	}

	public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
		RedisCache cache = this.getCache(regionName);
//		System.out.println("building TimestampsRegion:"+regionName);
		return new RedisTimestampsRegion(accessStrategyFactory, cache, settings, properties);

	}
	
	private RedisCache getCache(String name) throws CacheException{
		return CacheManagerFactory.getCacheManager().getRedisCache(name);
	}

}
