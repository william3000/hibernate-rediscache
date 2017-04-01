package com.xl.core.cache.redis.strategy;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.sun.media.jfxmedia.logging.Logger;
import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.lock.Item;
import com.xl.core.cache.redis.lock.Lock;
import com.xl.core.cache.redis.lock.Lockable;
import com.xl.core.cache.redis.region.RedisTransactionalDataRegion;

public class ReadWriteRedisAccessStrategy<T extends RedisTransactionalDataRegion> extends RedisAccessStrategy<T> {

	private final static Log logger = LogFactory.getLog(ReadWriteRedisAccessStrategy.class);	
	
	private final UUID uuid = UUID.randomUUID();
	private final AtomicLong nextLockId = new AtomicLong();
	
	private final Comparator versionComparator;
	
	public ReadWriteRedisAccessStrategy(T region, Settings settings) {
		super(region, settings);
		versionComparator = region.getCacheDataDescription().getVersionComparator();
	}
	
	public final Object get(Object key,long txTimestamp) throws CacheException{
		
		logger.info("ReadWriteRedisAccessStrategy get " + this.region().getName() + " " + String.valueOf(key) );

		this.readLockIfNeeded(key);
		try{
			final Lockable item = (Lockable)this.region().get(key);
			final boolean readable = item!=null && item.isReadable(txTimestamp);
			if( readable ){
				return item.getValue();
			}else{
				return null;
			}
		}finally{
			this.readUnlockIfNeeded(key);
		}
	}
	
	

	@Override
	public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
		
		logger.info("ReadWriteRedisAccessStrategy putFromLoad " + this.region().getName() + " " + String.valueOf(key) );
		
		region().writeLock( key );
		try {
			final Lockable item = (Lockable) region().get( key );
			
			final boolean writeable = item == null || item.isWriteable( txTimestamp, version, versionComparator );
			if ( writeable ) {
				region().put( key, new Item( value, version, region().nextTimestamp() ) );
				return true;
			}
			else {
				return false;
			}
		}
		finally {
			region().writeUnLock( key );
		}
	}
	
	public final SoftLock lockItem(Object key,Object version) throws CacheException{
		logger.info("lockItem:"+String.valueOf(key));
		
		this.region().writeLock(key);
		try{
			final Lockable item = (Lockable)this.region().get(key);
			final long timeout = this.region().nextTimestamp() + region().getTimeout();
			final Lock lock = (item==null)? new Lock(timeout,uuid,nextLockId(),version) : item.lock(timeout, uuid, nextLockId());
			this.region().put(key, lock);
			return lock;
		}finally{
			region().writeUnLock(key);
		}
	}
	
	public final void unlockItem(Object key,SoftLock lock) throws CacheException{
		
		logger.info("unlockItem:"+String.valueOf(key));
		
		this.region().writeLock(key);
		try{
			final Lockable item = (Lockable)this.region().get(key);
			if( item!=null && item.isUnlockable(lock) ){
				this.decrementLock(key, (Lock)item);
			}else{
				this.handleLockExpiry(key, item);
			}
		}finally{
			this.region().writeUnLock(key);
		}
	}
	
	public long nextLockId(){
		return this.nextLockId.getAndIncrement();
	}
	
	protected void decrementLock(Object key,Lock lock){
		lock.unlock(this.region().nextTimestamp());
		this.region().put(key, lock);
	}
	
	public void handleLockExpiry(Object key,Lockable lock){
		//TODO to be tested
		if( lock != null ){
			final long ts = this.region().nextTimestamp() + this.region().getTimeout();
			final Lock newLock = new Lock(ts,uuid,nextLockId.getAndIncrement(),null);
			newLock.unlock(ts);
			this.region().put(key, newLock);
		}
	}
	
	public void readLockIfNeeded(Object key){
		if( this.region().locksAreIndependentOfCache() ){
			this.region().readLock(key);
		}
	}
	
	public void readUnlockIfNeeded(Object key){
		if( this.region().locksAreIndependentOfCache() ){
			this.region().readUnLock(key);
		}
	}

}
