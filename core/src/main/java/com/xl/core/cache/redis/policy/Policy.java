package com.xl.core.cache.redis.policy;

import java.util.Set;

import com.xl.core.cache.redis.common.RedisElement;

public interface Policy {
	
	String getName();
	
	RedisElement getEvictionElementOnPolicy(Set<RedisElement> sampleElement,RedisElement justAdded);
	
	boolean compare(RedisElement element1 , RedisElement element2);

}
