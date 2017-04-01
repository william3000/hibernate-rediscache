package com.xl.core.cache.redis.common;

import com.xl.core.cache.redis.lock.Lockable;

public class RedisElement {
	
	private String name;
	
	private Object key;
	
	private Lockable lockable;

	public Lockable getLockable() {
		return lockable;
	}

	public void setLockable(Lockable lockable) {
		this.lockable = lockable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}
	
	

}
