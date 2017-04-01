package com.xl.core.cache.redis.lock;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import org.hibernate.cache.spi.access.SoftLock;

public interface Lockable {

	public boolean isReadable(long txTimestamp);

	public boolean isWriteable(long txTImestamp, Object version, Comparator versionComparator);

	public Object getValue();

	public boolean isUnlockable(SoftLock lock);

	public Lock lock(long timeout, UUID uuid, long lockId);

	public Long getLastAccessTime();

	public Long getHitCount();

	public Long getLastUpdateTime();
	
	public void setLastAccessTime(long lastAccessTime);
	
	public void setHitCount(long hitCount);
	
	public void setLastUpdateTime(long lastUpdateTime);
	
	public boolean isNative();
	
	public void setNative(boolean isNative);

}
