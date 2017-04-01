package com.xl.core.cache.redis.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.redis.region.RedisTransactionalDataRegion;

public abstract class RedisAccessStrategy<T extends RedisTransactionalDataRegion> {

	private final T region;
	
	private final Settings settings;

	public RedisAccessStrategy(T region, Settings settings) {
		super();
		this.region = region;
		this.settings = settings;
	}
	
	protected T region(){
		return this.region;
	}
	
	protected Settings settings(){
		return this.settings;
	}
	
	public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
		return putFromLoad( key, value, txTimestamp, version, settings.isMinimalPutsEnabled() );
	}
	
	public abstract boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
			throws CacheException;
	
	public final SoftLock lockRegion(){
		return null;
	}
	
	public void unlockRegion(SoftLock lock){
		this.region.evictAll();
	}
	
	
	public final void evict(Object key){
		this.region.evict(key);
	}
	
	public final void evictAll(){
		this.region.evictAll();
	}
	
	public final void remove(Object key){
		this.region.evict(key);
	}
	
	public final void removeAll(){
		this.region.evictAll();
	}
	
	
}
