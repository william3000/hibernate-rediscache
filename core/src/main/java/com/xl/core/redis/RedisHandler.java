package com.xl.core.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;

import com.xl.core.cache.redis.common.CacheManagerFactory;
import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.lock.Item;
import com.xl.core.cache.redis.lock.Lockable;
import com.xl.core.cache.redis.region.RedisSimpleRegion;
import com.xl.core.common.Const.ReserveWords;

public class RedisHandler {

	private final static Log logger = LogFactory.getLog(RedisHandler.class);
	
	private final static String APPLICATION = ReserveWords.Appliccation.toString();

	private RedisCache redisCache;
	
	private RedisSimpleRegion region;
	
	
	public RedisHandler(){
		redisCache = CacheManagerFactory.getCacheManager().getRedisCache(APPLICATION);
		region = new RedisSimpleRegion(redisCache);
	}
	
	public boolean contains(Object key) {
		return this.region.contains(key);
	}

	public Object get(Object key) throws CacheException {
		Object returnObj = this.region.get(key);
		if( returnObj instanceof Lockable ){
			return ((Lockable)returnObj).getValue();
		}else{
			return returnObj;
		}
	}
	
	public void put(Object key, Object value) throws CacheException {
		Lockable item = new Item(value,null,redisCache.getNextTimeStamp());
		this.region.put(key, item);
	}
	
	public void remove(Object key) throws CacheException{
		this.region.remove(key);
	}
	
	public void clear()	throws CacheException{
		this.region.clear();
	}
	
}
