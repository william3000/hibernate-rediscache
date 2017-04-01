package com.xl.core.cache.redis.store;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.xl.core.cache.config.ConfigItem;
import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.common.RedisElement;
import com.xl.core.cache.redis.lock.Lockable;
import com.xl.core.cache.redis.policy.LruPolicy;
import com.xl.core.cache.redis.policy.Policy;

public class MemoryStore {
	
	private Policy policy;
	
	public Lockable doGet(RedisCache redisCache , Object key){
		Object value = redisCache.get(key);
		Lockable lockable = (Lockable)value;
		
		ConfigItem item = com.xl.core.cache.config.Configuration.getConfig(redisCache.getName());
		if( item==null ){
			item = com.xl.core.cache.config.Configuration.getDefaultConfig();
		}
		Long timeToIdel = Long.parseLong(item.getTimeToIdleSeconds());
		long expireDate = lockable.getLastUpdateTime() + timeToIdel * 1000;
		if( new Date().getTime() > expireDate ){
			//Expired , need to remove
			redisCache.remove(key);
			return null;
		}else{
			lockable.setHitCount( lockable.getHitCount() + 1 );
			lockable.setLastAccessTime(new Date().getTime());
			redisCache.put(key, value);
			return lockable;
		}
		
	}
	
	
	public void doPut(RedisCache redisCache ,  RedisElement elementJustAdded) {
		
		this.removeExpireElement(redisCache);
		
		long currentLength = redisCache.getLength();
		ConfigItem item = com.xl.core.cache.config.Configuration.getConfig(redisCache.getName());
		if( item==null ){
			item = com.xl.core.cache.config.Configuration.getDefaultConfig();
		}
		Long maxElementInMemory = Long.parseLong(item.getMaxElementsInMemory());
		if( currentLength >=  maxElementInMemory ){
			this.removeElementChosenByEvictionPolicy(redisCache,elementJustAdded);
		}
    }
	
	public void removeExpireElement(RedisCache redisCache){
		ConfigItem item = com.xl.core.cache.config.Configuration.getConfig(redisCache.getName());
		if( item==null ){
			item = com.xl.core.cache.config.Configuration.getDefaultConfig();
		}
		Long timeToIdel = Long.parseLong(item.getTimeToIdleSeconds());
		Map<Object,Object> all = redisCache.getAll();
		for( Object key : all.keySet() ){
			Lockable lockable = (Lockable) all.get(key);
			long expireDate = lockable.getLastUpdateTime() + timeToIdel * 1000;
			if( new Date().getTime() > expireDate ){
				//Expired , need to remove
				redisCache.remove(key);
			}
		}
	}
	
	public void removeElementChosenByEvictionPolicy(RedisCache redisCache,RedisElement elementJustAdded) {
		if( policy==null ){
			policy = new LruPolicy();
		}
		Set<RedisElement> elements = new HashSet<RedisElement>();
		Map<Object,Object> all = redisCache.getAll();
		for( Object key : all.keySet() ){
			Lockable lockable = (Lockable) all.get(key);
			RedisElement element = new RedisElement();
			element.setKey(String.valueOf(key));
			element.setName(redisCache.getName());
			element.setLockable(lockable);
		}
		RedisElement removeItem = policy.getEvictionElementOnPolicy(elements, elementJustAdded);
		redisCache.remove(String.valueOf(removeItem.getKey()));
	}

}
