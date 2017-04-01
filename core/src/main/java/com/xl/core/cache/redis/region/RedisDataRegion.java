package com.xl.core.cache.redis.region;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.Region;
import org.hibernate.cfg.Settings;

import com.xl.core.cache.config.ConfigItem;
import com.xl.core.cache.config.Configuration;
import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.cache.redis.common.RedisElement;
import com.xl.core.cache.redis.lock.Item;
import com.xl.core.cache.redis.lock.Lockable;
import com.xl.core.cache.redis.policy.Policy;
import com.xl.core.cache.redis.policy.PolicyFactory;
import com.xl.core.cache.redis.strategy.RedisAccessStrategyFactory;


public abstract class RedisDataRegion implements Region {
	
	private final static Log logger = LogFactory.getLog(RedisDataRegion.class);	
	
	private final RedisCache cache;
	
	private final RedisAccessStrategyFactory accessStrategyFactory;
	
	private final Settings settings;
	
	private final Properties properties;
	
	public RedisDataRegion(RedisAccessStrategyFactory accessStrategyFactory,RedisCache cache, Settings settings, Properties properties) {
		super();
		this.cache = cache;
		this.accessStrategyFactory = accessStrategyFactory;
		this.settings = settings;
		this.properties = properties;
	}
	
	public  RedisCache getCache(){
		return this.cache;
	}
	
	public Settings getSettings(){
		return this.settings;
	}
	
	protected RedisAccessStrategyFactory getAccessStrategyFactory(){
		return this.accessStrategyFactory;
	}
	
	
	public String getName() {
		return this.getCache().getName();
	}

	public void destroy() throws CacheException {
//		this.getCache().removeAll();
	}

	public boolean contains(Object key) {
		return this.getCache().contains(key);
	}

	public long getSizeInMemory() {
		return this.getCache().calculateInMemorySize();
	}

	public long getElementCountInMemory() {
		return this.getCache().getMemoryStoreSize();
	}

	public long getElementCountOnDisk() {
		return this.getCache().getElementCountOnDisk();
	}

	public Map toMap() {
		return this.getCache().getAll();
//		final Map<Object, Object> result = new HashMap<Object, Object>();
//		for(Object key : this.getCache().getAllKeys() ){
//			result.put(key, this.getCache().get(key));
//		}
//		return result;
	}
	
	public long nextTimestamp() {
//		long current = CURRENT.get();
//		long tx = CURRENT.incrementAndGet();
//		System.out.println("In AtomicLong #### :current="+current + ",next="+tx);
//		return tx;
		long next = this.getCache().getNextTimeStamp();
		logger.debug("In nextTimestamp #### next:" + next );
		return next;
	}

	public int getTimeout() {
		return 300;
	}
	
	public Object get(Object key) throws CacheException {
		Long start = new Date().getTime();
		try{
			if( String.valueOf(key).equals("com.xl.haolin.model.product.Application#260") ){
				System.out.println("found 260");
			}
			
			Object value = this.getCache().get(key);
			
			logger.info("RedisDataRegion hget " + this.getCache().getName() + " " + String.valueOf(key) + ",return:" + (value==null ? "null" : value.toString() ) );
			
			if( value==null ){
				return null;
			}
			
			Lockable lockable = (Lockable)value;
			ConfigItem item = Configuration.getConfig(this.getCache().getName());
			
//			logger.info("get from region , name:" + this.getCache().getName() + ",key:" + String.valueOf(key) );
			
			long timeToIdel = Long.parseLong(item.getTimeToIdleSeconds());
			long timeToLive = Long.parseLong(item.getTimeToLiveSeconds());
			long liveExpireDate = lockable.getLastUpdateTime() + timeToLive * 1000;
			long idelExpireDate = lockable.getLastAccessTime() + timeToIdel * 1000;
			if( new Date().getTime() > liveExpireDate ){
				//Expired , need to remove
				logger.info("live expired when get, need to remove , name:" + this.getCache().getName() + " , key:" + String.valueOf(key) );
				this.getCache().remove(key);
				return null;
			}else if( new Date().getTime() > idelExpireDate ){
				//Expired , need to remove
				logger.info("idel expired when get, need to remove , name:" + this.getCache().getName() + " , key:" + String.valueOf(key) );
				this.getCache().remove(key);
				return null;
			}else{
				
				lockable.setHitCount(lockable.getHitCount()+1);
				lockable.setLastAccessTime(new Date().getTime());
				this.getCache().put(key, lockable);
				
//				Object[] params=new Object[]{this.getCache(),key,lockable};
//				AsynFactory.getAsynService().addWork(CacheUpdateTarget.class, "updateGetInfo", params, new CommonCallback());
				
				if( lockable.isNative() ){
					return lockable.getValue();
				}else{
					return lockable;
				}
			}
		}finally{
			Long end1 = new Date().getTime();
			logger.debug( "get cost:"+(end1 -start)  );
		}
		
	}

	public void put(Object key, Object value) throws CacheException {
		Long start = new Date().getTime();
		if( value instanceof Lockable ){
			logger.info("RedisDataRegion hset " + this.getCache().getName() + " " + String.valueOf(key) );
			Lockable lockable = (Lockable)value;
			long currentLength = this.getCache().getLength();
			ConfigItem item = Configuration.getConfig(this.getCache().getName());
			long maxElementInMemory = Long.parseLong(item.getMaxElementsInMemory());
			if( currentLength >=  maxElementInMemory ){
				logger.info("not enough space in region , need to remove , name:" + this.getCache().getName());
				this.removeExpireElement();
				RedisElement elementJustAdded = new RedisElement();
				elementJustAdded.setName(this.getCache().getName());
				elementJustAdded.setKey(key);
				elementJustAdded.setLockable(lockable);
				this.removeElementChosenByEvictionPolicy(elementJustAdded);
			}
			Long end = new Date().getTime();
			this.getCache().put(key, value);
		}else{
			Item item = new Item(value,null,this.getCache().getNextTimeStamp(),true);
			this.getCache().put(key, item);
		}
		Long end1 = new Date().getTime();
		logger.debug( "put cost:"+(end1 -start)  );
	}

	public void evict(Object key) throws CacheException {
		this.getCache().remove(key);
	}

	public void evictAll() throws CacheException {
		this.getCache().removeAll();
	}
	
	public void remove(Object key) throws CacheException{
		this.getCache().remove(key);
	}
	
	public void clear()	throws CacheException{
		this.getCache().removeAll();
	}
	
	public void removeExpireElement(){
		ConfigItem item = Configuration.getConfig(this.getCache().getName());
		long timeToIdel = Long.parseLong(item.getTimeToIdleSeconds());
		long timeToLive = Long.parseLong(item.getTimeToLiveSeconds());
		Map<Object,Object> all = this.getCache().getAll();
		for( Object key : all.keySet() ){
			Lockable lockable = (Lockable) all.get(key);
			long liveExpireDate = lockable.getLastUpdateTime() + timeToLive * 1000;
			long idelExpireDate = lockable.getLastAccessTime() + timeToIdel * 1000;
			if( new Date().getTime() > liveExpireDate ){
				this.getCache().remove(key);
			}else if( new Date().getTime() > idelExpireDate ){
				this.getCache().remove(key);
			}
		}
	}
	
	public void removeElementChosenByEvictionPolicy(RedisElement elementJustAdded) {
		Policy policy = PolicyFactory.createPolicy();
		Set<RedisElement> elements = new HashSet<RedisElement>();
		Map<Object,Object> all = this.getCache().getAll();
		for( Object key : all.keySet() ){
			Lockable lockable = (Lockable) all.get(key);
			RedisElement element = new RedisElement();
			element.setKey(key);
			element.setName(this.getCache().getName());
			element.setLockable(lockable);
			elements.add(element);
		}
		RedisElement removeItem = policy.getEvictionElementOnPolicy(elements, elementJustAdded);
		
		this.getCache().remove(removeItem.getKey());
		logger.info("removed item---name:"+this.getCache().getName()+",key:"+ removeItem.getKey());
	}

}
