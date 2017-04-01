package com.xl.core.cache.redis.lock;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import org.hibernate.cache.spi.access.SoftLock;

public final class Item implements Serializable, Lockable {
	private static final long serialVersionUID = 1L;
	private final Object value;
	private final Object version;
	private final long timestamp;
	
	private long lastAccessTime;
	private long lastUpdateTime;
	private long hitCount;
	
	private boolean isNative;
	
	public Item(){
		this.value = null;
		this.version = null;
		this.timestamp = 0L;
		
		this.lastAccessTime = new Date().getTime();
		this.lastUpdateTime = new Date().getTime();
		this.hitCount =1 ;
		
		this.isNative = false;
	}

	/**
	 * Creates an unlocked item wrapping the given value with a version and creation timestamp.
	 */
	public Item(Object value, Object version, long timestamp) {
		this.value = value;
		this.version = version;
		this.timestamp = timestamp;
		
		this.lastAccessTime = new Date().getTime();
		this.lastUpdateTime = new Date().getTime();
		this.hitCount =1 ;
		
		this.isNative = false;
	}
	
	public Item(Object value, Object version, long timestamp , boolean isNative) {
		this.value = value;
		this.version = version;
		this.timestamp = timestamp;
		
		this.lastAccessTime = new Date().getTime();
		this.lastUpdateTime = new Date().getTime();
		this.hitCount =1 ;
		
		this.isNative = isNative;
	}

	public boolean isReadable(long txTimestamp) {
//		System.out.println("current:"+timestamp + ",request:"+txTimestamp);
//		System.out.println("in item , isReadable:"+ (txTimestamp >= timestamp));
		return txTimestamp >= timestamp;
	}

	@SuppressWarnings("unchecked")
	public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
		return version != null && versionComparator.compare( version, newVersion ) < 0;
	}

	public Object getValue() {
		return value;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public boolean isUnlockable(SoftLock lock) {
		return false;
	}

	public Lock lock(long timeout, UUID uuid, long lockId) {
		return new Lock( timeout, uuid, lockId, version );
	}

	public Long getLastAccessTime() {
		return this.lastAccessTime;
	}

	public Long getHitCount() {
		return this.hitCount;
	}

	public Long getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Item) {
			Item obj1 = (Item)obj;
			return obj1.getValue().equals(this.getValue());
		} else {
			return false;
		}
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

	public boolean isNative() {
		return this.isNative;
	}

	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}
	
	
}
