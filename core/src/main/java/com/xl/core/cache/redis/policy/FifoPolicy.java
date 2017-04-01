package com.xl.core.cache.redis.policy;

import com.xl.core.cache.redis.common.RedisElement;
import com.xl.core.cache.redis.lock.Lockable;

public class FifoPolicy extends AbstractPolicy {

	public static final String NAME = "FIFO";
	
	public String getName() {
        return NAME;
    }
	
	public boolean compare(RedisElement element1, RedisElement element2) {
		return element2.getLockable().getLastUpdateTime() < element1.getLockable().getLastUpdateTime();
	}

}
