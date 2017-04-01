package com.xl.core.redis;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.xl.core.cache.config.ConfigurationFactory;
import com.xl.core.cache.redis.common.CacheManagerFactory;

public class RedisUtils {

	private final static Log logger = LogFactory.getLog(RedisHandler.class);
	
	private static String redisMaster;
	
	private static boolean initFlag = false;
	
	public static void init(){
		if( !initFlag ){
			logger.info("init cacheManager , from redisUtils");
			Properties properties = new Properties();
			properties.put("redis.master", redisMaster);
			CacheManagerFactory.initCacheManager(properties);
			ConfigurationFactory.initConfiguration();
		}
	}
	
	public static RedisHandler getRedisHandler(){
		init();
		return new RedisHandler();
	}

	public String getRedisMaster() {
		return redisMaster;
	}

	public void setRedisMaster(String redisHostPort) {
		redisMaster = redisHostPort;
	}
	
	public static void main(String[] args) {
		String redisUri = "redis://127.0.0.1:6379";
		RedisClient redisClient = RedisClient.create(redisUri);
		StatefulRedisConnection<String,String> connection =  redisClient.connect();
		RedisCommands<String, String> command = connection.sync();
		String key = "com.xl.haolin.model.product.Application";
		String field = "com.xl.haolin.model.product.Application#171";
		String value = command.hget(key, field);
		System.out.println("sleep:"+value);
		try{
			Thread.sleep(1000*1000);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("done");
	}

}
