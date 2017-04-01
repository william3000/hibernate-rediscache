package com.xl.core.cache.redis.statics;

import java.io.Serializable;

public class GeneralCounter implements Serializable,Counter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 36759883696568631L;
	
	
	private long lastAccessTime;
	private long lastUpdateTime;
	private long hitCount;

	public Long getLastAccessTime() {
		return this.lastAccessTime;
	}

	public Long getHitCount() {
		return this.hitCount;
	}

	public Long getLastUpdateTime() {
		return this.lastUpdateTime;
	}
	
	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public void setHitCount(long hitCount) {
		this.hitCount = hitCount;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

}
