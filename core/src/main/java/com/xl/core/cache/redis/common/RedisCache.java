package com.xl.core.cache.redis.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import com.xl.core.serialize.SerializeUtil;

public class RedisCache {
	
	private final static Log logger = LogFactory.getLog(RedisCache.class);	
	
	private static final String TIMESTAMP_KEY = "redis.timestamp.map";
	
	private final String TIMESTAMP_FIELD;
	
	private final String name;
	
	private RedisCommands<String, String> command;
	
	private StatefulRedisConnection<String,String> connection;
	
	public RedisCache(String name){
		this.name = name;
		this.TIMESTAMP_FIELD = name;
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
//			System.out.println(Thread.currentThread().getId() + "--init1 connection in redisCache:id="+connection.hashCode());
			this.command = this.connection.sync();
//			System.out.println(Thread.currentThread().getId() + "--init2 connection in redisCache:id="+connection.hashCode());
//		}
	}
	
	public void destroyConnection(){
		System.out.println("error occor connection:id="+connection.hashCode()+",thread="+Thread.currentThread().getId());
//		if( this.command!=null ){
//			logger.info("destroying connection in redis cache");
//			CacheManagerFactory.getCacheManager().destoryConnection(this.connection);
//			this.command = null;
//		}
	}
	
	public void closeConnection(){
//		if( this.command!=null ){
//			logger.info("destroying connection");
//			System.out.println(Thread.currentThread().getId() + "--close connection in redisCache:id="+connection.hashCode());
			CacheManagerFactory.getCacheManager().closeConnection(this.connection);
//			this.command = null;
//		}
	}
	
	public long getNextTimeStamp(){
		try{
			this.initConnection();
			String value = this.getRedisCommand().hget(TIMESTAMP_KEY, TIMESTAMP_FIELD);
//			logger.debug("In getNextTimeStamp:key="+TIMESTAMP_KEY+",field="+TIMESTAMP_FIELD+",value="+value);
			if( value==null || value.isEmpty() ){
				long newTimeStamp = 1;
				logger.debug("in getNextTimeStamp,redisCommand is null:"+(this.getRedisCommand()==null));
				this.getRedisCommand().hset(TIMESTAMP_KEY, TIMESTAMP_FIELD, String.valueOf(newTimeStamp));
				return 1;
			}else{
				long newTimeStamp = Long.parseLong(value) + 1 ;
				logger.debug("in getNextTimeStamp,redisCommand:"+(this.getRedisCommand()==null));
				this.getRedisCommand().hset(TIMESTAMP_KEY, TIMESTAMP_FIELD, String.valueOf(newTimeStamp));
				return newTimeStamp;
			}
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public Object get(Object key){
//		long start = new Date().getTime();
		try{
			Object valueObj = null;
			this.initConnection();
			try{
				String value = this.getRedisCommand().hget(name, String.valueOf(key));
				if(  value!=null ){
					valueObj = SerializeUtil.unserialize(value);
				}
//				logger.info("hget " + name + " " + String.valueOf(key) + ",return:" + (valueObj==null ? "null" : valueObj.toString() ) );
				return valueObj;
			}catch( RuntimeException re){
				re.printStackTrace();
				return null;
			}
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
//			long end = new Date().getTime();
//			System.out.println("cost1:"+ (end-start));
		}
	}
	
	public boolean contains(Object key){
		try{
			this.initConnection();
			return this.getRedisCommand().hexists(name, String.valueOf(key));
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public void put(Object key,Object value){
		try{
			this.initConnection();
			String valueString = SerializeUtil.serialize(value);
//			logger.info("hset " + name + " " + String.valueOf(key) );
			this.getRedisCommand().hset(name, String.valueOf(key), valueString);
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public void remove(Object key){
		logger.info("hdel "+name + " " + String.valueOf(key));
		logger.info(this.connection.hashCode());
		try{
			this.initConnection();
			Long returnLong =this.getRedisCommand().hdel(name, String.valueOf(key));
			logger.info("returnLong:"+returnLong);
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
		Object obj = this.get(key);
		logger.info("after remove,key is null:"+  (obj==null));
		
	}
	
//	public void remove(Object key){
//		String redisUri = "redis://127.0.0.1:6379";
//		RedisClient redisClient = RedisClient.create(redisUri);
//		StatefulRedisConnection<String,String> connection =  redisClient.connect();
//		connection.setAutoFlushCommands(true);
//		RedisCommands<String, String> command = connection.sync();
//		logger.info("hdel "+name + " " + String.valueOf(key));
//		Long returnLong = command.hdel(name, String.valueOf(key));
//		logger.info("returnLong:"+returnLong);
//		connection.flushCommands();
//		logger.info("connection:"+connection.isOpen());
//		connection.close();
//		
//		connection = redisClient.connect();
//		command = connection.sync();
//		String value = command.hget(name, String.valueOf(key));
//		logger.info("value:"+value);
//		
//		connection.close();
//		redisClient.shutdown();
		
//		String redisUri = "redis://127.0.0.1:6379";
//		RedisClient redisClient1 = RedisClient.create(redisUri);
//		StatefulRedisConnection<String,String> connection1 =  redisClient1.connect();
//		RedisCommands<String, String> command1 = connection1.sync();
//		String value = command1.hget(name, String.valueOf(key));
//		logger.info("value:"+value);
//		connection1.close();
//	}
	
	public void removeAll(){
		try{
			this.initConnection();
			this.getRedisCommand().del(name);
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public Map<Object,Object> getAll(){
		try{
			this.initConnection();
			Map<String,String> maps = this.getRedisCommand().hgetall(name);
			Map<Object,Object> results = new HashMap<Object,Object>();
			for( String key : maps.keySet() ){
				Object value = SerializeUtil.unserialize(maps.get(key));
				results.put(key, value);
			}
			return results;
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public Set<Object> getAllKeys(){
		try{
			this.initConnection();
			List<String> redisSet = this.getRedisCommand().hkeys(name);
			Set<Object> objectSet = new HashSet<Object>();
			for(String key : redisSet){
				objectSet.add(key);
			}
			return objectSet;
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public Set<Object> getAllValues(){
		try{
			this.initConnection();
			Set<Object> objectSet = new HashSet<Object>();
			Collection<String> valueCollection = this.getRedisCommand().hvals(name);
			for( String key : valueCollection ){
				Object value = SerializeUtil.unserialize(key);
				objectSet.add(value);
			}
			return objectSet;
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public long getLength(){
		try{
			this.initConnection();
			return this.getRedisCommand().hlen(name);
		}catch(Exception e){
			this.destroyConnection();
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			this.closeConnection();
		}
	}
	
	public String getName(){
		return name;
	}
	
	public long calculateInMemorySize(){
		return 0;
	}
	
	public long getMemoryStoreSize(){
		return 0;
	}
	
	public long getElementCountOnDisk(){
		return 0;
	}

//	public ShardedJedis getShardedJedis() {
//		return shardedJedis;
//	}

	
}
