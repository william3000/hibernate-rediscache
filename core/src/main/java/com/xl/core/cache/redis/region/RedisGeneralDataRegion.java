package com.xl.core.cache.redis.region;

import java.util.Properties;

import org.hibernate.cache.spi.GeneralDataRegion;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;

public class RedisGeneralDataRegion extends RedisDataRegion implements GeneralDataRegion {

	public RedisGeneralDataRegion(RedisCache cache, RedisAccessStrategyFactory accessStrategyFactory, Settings settings, Properties properties) {
		super(accessStrategyFactory,cache,settings, properties);
	}

}
