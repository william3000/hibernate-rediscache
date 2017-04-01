package com.xl.core.cache.redis.policy;

import com.xl.core.cache.redis.common.RedisElement;
import com.xl.core.cache.redis.lock.Lockable;

public class LfuPolicy extends AbstractPolicy {

	public static final String NAME = "LFU";
	
	public String getName() {
        return NAME;
    }
	
	public boolean compare(RedisElement element1, RedisElement element2) {
		return element2.getLockable().getHitCount() < element1.getLockable().getHitCount();
	}

}
