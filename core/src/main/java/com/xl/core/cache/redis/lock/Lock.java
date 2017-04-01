package com.xl.core.cache.redis.lock;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import org.hibernate.cache.spi.access.SoftLock;

public final class Lock implements Serializable, Lockable, SoftLock {
	private static final long serialVersionUID = 2L;

	private final UUID sourceUuid;
	private final long lockId;
	private final Object version;

	private long timeout;
	private boolean concurrent;
	private int multiplicity = 1;
	private long unlockTimestamp;
	
	private long lastAccessTime;
	private long lastUpdateTime;
	private long hitCount;

	private boolean isNative;
	
	/**
	 * Creates a locked item with the given identifiers and object version.
	 */
	public Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
		this.timeout = timeout;
		this.lockId = lockId;
		this.version = version;
		this.sourceUuid = sourceUuid;
		
		this.lastAccessTime = new Date().getTime();
		this.lastUpdateTime = new Date().getTime();
		this.hitCount =1 ;
		
		this.isNative = false;
	}
	
	public Lock(long timeout, UUID sourceUuid, long lockId, Object version , boolean isNative) {
		this.timeout = timeout;
		this.lockId = lockId;
		this.version = version;
		this.sourceUuid = sourceUuid;
		
		this.lastAccessTime = new Date().getTime();
		this.lastUpdateTime = new Date().getTime();
		this.hitCount =1 ;
		
		this.isNative = isNative;
	}

	public boolean isReadable(long txTimestamp) {
		return false;
	}

	@SuppressWarnings({ "SimplifiableIfStatement", "unchecked" })
	public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
		if (txTimestamp > timeout) {
			// if timedout then allow write
			return true;
		}
		if (multiplicity > 0) {
			// if still locked then disallow write
			return false;
		}
		return version == null ? txTimestamp > unlockTimestamp : versionComparator.compare(version, newVersion) < 0;
	}

	public Object getValue() {
		return null;
	}

	public boolean isUnlockable(SoftLock lock) {
		return equals(lock);
	}

	@Override
	@SuppressWarnings("SimplifiableIfStatement")
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (o instanceof Lock) {
			return (lockId == ((Lock) o).lockId) && sourceUuid.equals(((Lock) o).sourceUuid);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int hash = (sourceUuid != null ? sourceUuid.hashCode() : 0);
		int temp = (int) lockId;
		for (int i = 1; i < Long.SIZE / Integer.SIZE; i++) {
			temp ^= (lockId >>> (i * Integer.SIZE));
		}
		return hash + temp;
	}

	/**
	 * Returns true if this Lock has been concurrently locked by more than one
	 * transaction.
	 */
	public boolean wasLockedConcurrently() {
		return concurrent;
	}

	public Lock lock(long timeout, UUID uuid, long lockId) {
		concurrent = true;
		multiplicity++;
		this.timeout = timeout;
		return this;
	}

	/**
	 * Unlocks this Lock, and timestamps the unlock event.
	 */
	public void unlock(long timestamp) {
		if (--multiplicity == 0) {
			unlockTimestamp = timestamp;
		}
	}

	@Override
	public String toString() {
		return "Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId;
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
