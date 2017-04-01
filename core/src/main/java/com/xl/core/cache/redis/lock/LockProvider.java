package com.xl.core.cache.redis.lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.xl.core.cache.redis.common.CacheManagerFactory;
import com.xl.core.cache.redis.common.RedisCache;
import com.xl.core.serialize.SerializeUtil;

public class LockProvider {
	
	private final static Log logger = LogFactory.getLog(LockProvider.class);	
	
	private static final int DEFAULT_SINGLE_EXPIRE_TIME = 3;  
	
    public enum LockType {
		READ("read"), WRITE("write");
		private String LockType;
		private LockType(String _LockType) {
			this.LockType = _LockType;
		}
		@Override
		public String toString() {
			return this.LockType;
		}
	}

    private StatefulRedisConnection<String,String> connection;
    
    private RedisCommands<String, String> command;
    
	public LockProvider(){
		
	}
	
	public RedisCommands<String, String> getRedisCommand(){
		if( this.command == null ){
			this.initConnection();
		}
		return this.command;
	}
	
	public void initConnection(){
//		if( this.command==null || this.connection ==null ){
			this.connection = CacheManagerFactory.getCacheManager().getConnection();
//			System.out.println(Thread.currentThread().getId() + "--init1 connection in lockprovider="+connection.hashCode());
			this.command = this.connection.sync();
//			System.out.println(Thread.currentThread().getId() + "--init2 connection in lockprovider="+connection.hashCode());
			
//			System.out.println("getting connection in lockProvider,id="+this.connection.hashCode());
//		}
	}
	
	public void destroyConnection(){
		System.out.println("error occor connection:id="+connection.hashCode()+",thread="+Thread.currentThread().getId());
//		if( this.command!=null ){
//			logger.info("destroying connection in lock provider");
//			CacheManagerFactory.getCacheManager().destoryConnection(this.connection);
//			this.command = null;
//		}
	}
	
	public void closeConnection(){
//		if( this.command!=null ){
//		System.out.println("getting connection in lockProvider,id="+this.connection.hashCode());
//		System.out.println(Thread.currentThread().getId() + "--close connection in lockprovider="+connection.hashCode());
		CacheManagerFactory.getCacheManager().closeConnection(this.connection);
//		}
	}
	
	public boolean readLock(RedisCache cache,Object key){
		try{
			this.initConnection();
			String keyString = String.valueOf(key);
			return this.lock(cache.getName(), keyString, LockType.READ, 1000);
		}catch(RuntimeException e){
			this.destroyConnection();
			throw e;
		}finally{
			this.closeConnection();
		}
	}
	
	public boolean writeLock(RedisCache cache,Object key){
		try{
			this.initConnection();
			String keyString = String.valueOf(key);
			return this.lock(cache.getName(), keyString, LockType.WRITE, 1000);
		}catch(RuntimeException e){
			this.destroyConnection();
			throw e;
		}finally{
			this.closeConnection();
		}
	}
	
	public void readUnLock(RedisCache cache,Object key){
		try{
			this.initConnection();
			String keyString = String.valueOf(key);
			this.unLock(cache.getName(), keyString, LockType.READ);
		}catch(RuntimeException e){
			this.destroyConnection();
			throw e;
		}finally{
			this.closeConnection();
		}
	}
	
	public void writeUnLock(RedisCache cache,Object key){
		try{
			this.initConnection();
			String keyString = String.valueOf(key);
			this.unLock(cache.getName(), keyString, LockType.WRITE);
		}catch(RuntimeException e){
			this.destroyConnection();
			throw e;
		}finally{
			this.closeConnection();
		}
	}
	
	private boolean lock(String regionName,String key ,LockType lockType,long timeOut){
		try{
			long nano = System.nanoTime(); 
			do{
				String lockReginName = regionName + "_lock";
				String filed = String.valueOf(key) + "_" + lockType.toString();
//				logger.info("jedis is null:"+ (shardedJedis==null) + ",lockReginName:"+lockReginName + ",filed:"+filed + ",key is null:"+ (key==null) );
				boolean i = getRedisCommand().hsetnx(lockReginName, filed, filed);
				if( i ){
					getRedisCommand().expire(lockReginName, DEFAULT_SINGLE_EXPIRE_TIME);
					return Boolean.TRUE;
				}
				if( timeOut==0 ){
					break;
				}
				Thread.sleep(3000);
			}while( (System.nanoTime() - nano) < timeOut );
			return Boolean.FALSE;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}
	
	private void unLock(String regionName,String key,LockType lockType){
		try{
			String lockReginName = regionName + "_lock";
			String filed = String.valueOf(key) + "_" + lockType.toString();
			getRedisCommand().hdel(lockReginName, filed);
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
