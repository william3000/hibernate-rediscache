package com.xl.core.cache.redis.region;

import java.util.Properties;

import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;

public class RedisTimestampsRegion extends RedisGeneralDataRegion implements TimestampsRegion {

	public RedisTimestampsRegion(RedisAccessStrategyFactory accessStrategyFactory,RedisCache cache,Settings settings, Properties properties) {
		super(cache, accessStrategyFactory, settings, properties);
		// TODO Auto-generated constructor stub
	}

}
