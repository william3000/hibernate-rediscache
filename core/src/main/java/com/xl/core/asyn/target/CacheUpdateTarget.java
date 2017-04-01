package com.xl.core.asyn.target;

import java.util.Date;

import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.lock.Lockable;

public class CacheUpdateTarget {
	
	public boolean updateGetInfo(RedisCache redisCache ,Object key , Lockable lockable){
//		System.out.println("updateGeInfo");
		lockable.setHitCount(lockable.getHitCount()+1);
		lockable.setLastAccessTime(new Date().getTime());
		redisCache.put(key, lockable);
		return true;
	}
	

}
