package com.xl.core.cache.redis.policy;

import java.util.Set;

import com.xl.core.cache.redis.common.RedisElement;
import com.xl.core.cache.redis.lock.Lockable;

public abstract class AbstractPolicy implements Policy {

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public RedisElement getEvictionElementOnPolicy(Set<RedisElement> sampleElements, RedisElement justAdded) {
		if (sampleElements.size() == 1 && justAdded != null) {
            return justAdded;
        }
		RedisElement lowestElement = null;
		for( RedisElement element : sampleElements ){
			if( element ==null ){
				continue;
			}
			if( lowestElement==null ){
				if( !element.equals(justAdded) ){
					lowestElement = element;
				}
			}else if( compare(lowestElement, element) && !element.equals(justAdded) ){
				lowestElement = element;
			}
		}
		return lowestElement;
	}

}
