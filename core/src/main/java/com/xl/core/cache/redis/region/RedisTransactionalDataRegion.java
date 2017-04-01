package com.xl.core.cache.redis.region;

import java.util.Properties;

import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.common.CacheManagerFactory;
import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.lock.LockProvider;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;

public class RedisTransactionalDataRegion extends RedisDataRegion implements TransactionalDataRegion {

	protected final CacheDataDescription metadata;
	
	private final LockProvider lockProvider;
	
	RedisTransactionalDataRegion( RedisAccessStrategyFactory accessStrategyFactory , RedisCache cache,Settings settings,
			Properties properties,CacheDataDescription metadata){
		super(accessStrategyFactory,cache,settings,properties);
		this.metadata = metadata;
		this.lockProvider = CacheManagerFactory.getLockProvider(properties);
	}
	
	public boolean isTransactionAware() {
		return false;
	}

	public CacheDataDescription getCacheDataDescription() {
		return metadata;
	}
	
	public final void writeLock(Object key){
		this.lockProvider.writeLock(getCache(), key);
	}
	
	public final void writeUnLock(Object key){
		this.lockProvider.writeUnLock(getCache(), key);
	}
	
	public final void readLock(Object key){
		this.lockProvider.readLock(getCache(), key);
	}
	
	public final void readUnLock(Object key){
		this.lockProvider.readUnLock(getCache(), key);
	}
	
	public final boolean locksAreIndependentOfCache(){
		return true;
	}

}
