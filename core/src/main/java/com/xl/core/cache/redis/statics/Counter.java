package com.xl.core.cache.redis.statics;

public interface Counter {
	
	public Long getLastAccessTime();

	public Long getHitCount();

	public Long getLastUpdateTime();
	
	public void setLastAccessTime(long lastAccessTime);
	
	public void setHitCount(long hitCount);
	
	public void setLastUpdateTime(long lastUpdateTime);

}
