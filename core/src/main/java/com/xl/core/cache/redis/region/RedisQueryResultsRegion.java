package com.xl.core.cache.redis.region;

import java.util.Properties;

import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;

public class RedisQueryResultsRegion extends RedisGeneralDataRegion implements QueryResultsRegion {

	public RedisQueryResultsRegion(RedisAccessStrategyFactory accessStrategyFactory,RedisCache cache, Settings settings, Properties properties) {
		super(cache, accessStrategyFactory, settings, properties);
	}

}
