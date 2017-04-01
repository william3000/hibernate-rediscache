package com.xl.core.cache.redis.common;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnectionPool;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.xl.core.cache.redis.lock.LockProvider;

public class CacheManagerFactory implements DisposableBean {
	
	private final static Log logger = LogFactory.getLog(CacheManagerFactory.class);	
	
	private static Properties currentProperties;

	private static CacheManager cacheManager;
	
	private static LockProvider lockProvider;
	
	public static int MaxConnection = 50;
	
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
	}

	public static void initCacheManager(Properties properties) {
		currentProperties = properties;
		if( cacheManager==null ){
			logger.info("----------------------------------init cache manager-------------------------------------");
			String master = properties.getProperty("redis.master");
//			String slave = properties.getProperty("redis.slave");
			
			if( master==null || master.isEmpty() ){
				master = "127.0.0.1:6379";
			}
			
			String redisUri = "redis://" + master;
			
//			for( Object key : properties.keySet() ){
//				System.out.println(key + "---" + properties.get(key));
//			}
			String maxNum = properties.getProperty("redis.maxConnection");
			if( maxNum!=null && !maxNum.isEmpty() ){
				MaxConnection = Integer.parseInt(maxNum);
			}
			logger.info("init redis,serverUri:"+redisUri+",maxConnection:"+MaxConnection);
			
			RedisClient redisClient = RedisClient.create(redisUri);
			redisClient.setDefaultTimeout(60, TimeUnit.SECONDS);
			
			cacheManager =  new CacheManager(redisClient);
		}
	}
	
	public static LockProvider getLockProvider(Properties properties){
		if( cacheManager==null ){
			initCacheManager(currentProperties);
		}
		
		if( lockProvider!=null ){
			return lockProvider;
		}else{
			return new LockProvider();
		}
	}

	public static CacheManager getCacheManager() {
		if( cacheManager==null ){
			initCacheManager(currentProperties);
		}
		return cacheManager;
	}
	
	public static void shutdown(){
		if( cacheManager!=null ){
			cacheManager=null;
		}
	}

}
