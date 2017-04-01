package com.xl.core.cache.redis.policy;

public class PolicyFactory {
	
	public static Policy createPolicy(){
		return new LruPolicy();
	}
	
	public static Policy createPolicy(String policyName){
		if( "lru".equals(policyName) ){
			return new LruPolicy();
		}else if("lfu".equals(policyName)){
			return new LfuPolicy();
		}else if("fifo".equals(policyName)){
			return new FifoPolicy();
		}else{
			return null;
		}
	}

}
