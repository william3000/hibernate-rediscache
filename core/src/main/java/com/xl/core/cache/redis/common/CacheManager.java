package com.xl.core.cache.redis.common;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

public class CacheManager {
	
	private final static Log logger = LogFactory.getLog(CacheManager.class);	
	
	private static AtomicLong cCounter = new AtomicLong(0);
	
	private RedisClient redisClient;
	
	private static ConcurrentLinkedQueue<StatefulRedisConnection<String,String>> queue;
	
//	private static RedisConnectionPool<RedisCommands<String, String>> pool;
	
//	private static GenericObjectPool<StatefulRedisConnection<String,String>> pool;
	
	public CacheManager(RedisClient redisClient){
		this.redisClient = redisClient;
		
		queue = new ConcurrentLinkedQueue<StatefulRedisConnection<String,String>>();
		
//		pool = redisClient.pool(20,50);
		
//		GenericObjectPoolConfig config = new GenericObjectPoolConfig(); 
//        config.setMaxTotal(50);
//        config.setMaxIdle(20);
//        config.setTestOnBorrow(true);  
//        config.setTestOnReturn(true);  
//        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisClient);
//        pool = new GenericObjectPool<StatefulRedisConnection<String,String>>(factory,config);
	}

	public void testRedis(){
		StatefulRedisConnection<String,String> connection = this.getConnection();
		try{
			RedisCommands<String, String> syncCommands = connection.sync();
			syncCommands.set("Test Redis", "Test Redis");
		}catch(RuntimeException e){
			e.printStackTrace();
		}finally{
			this.closeConnection(connection);
		}
	}
	
	public void closeRedis(){
		if( queue!=null && !queue.isEmpty() ){
			StatefulRedisConnection<String,String> connection = queue.poll();
			while ( connection!=null ) {
				connection.close();
				connection = queue.poll();
            }
		}
		this.redisClient.shutdown();
		this.redisClient = null;
		
		
//		pool.clear();
//		pool.clear();
	}
	
	public synchronized StatefulRedisConnection<String,String> getConnection(){
//		logger.info("------entering getConnection,thread:"+Thread.currentThread().getId());
		try{
			StatefulRedisConnection<String,String> connection = queue.poll();
			synchronized( this ){
				if( connection == null ){
					connection = this.getRedisClient().connect();
					logger.info(Thread.currentThread().getId() + "--create new connection:id="+connection.hashCode()+",time="+cCounter.incrementAndGet());
//					StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//					for(int i=0;i<stackTraceElements.length && i<=10;i++){
//						System.out.println(stackTraceElements[i].getClassName() + "." + stackTraceElements[i].getMethodName());
//					}
				}else{
//					connection = queue.poll();
//					logger.info("getting connection:id="+connection.hashCode()+",thread="+Thread.currentThread().getId());
					if( !connection.isOpen() ){
//						this.destoryConnection(connection);
						connection = this.getConnection();
					}
				}
			}
//			System.out.println(Thread.currentThread().getId() + "--getting connection:id="+connection.hashCode() + ",queue:"+this.printCurrent());
			return connection;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
//			logger.info("-----exit getConnection,thread:"+Thread.currentThread().getId());
		}
		
		
//		try{
//			StatefulRedisConnection<String,String> connection =  pool.borrowObject();
//			System.out.println("get connection , hashcode:"+connection.hashCode() + ",Thread:"+Thread.currentThread().getId());
//			return connection;
//		}catch(Exception e){
//			e.printStackTrace();
//			return null;
//		}
	}

	public synchronized void closeConnection(StatefulRedisConnection<String,String> connection){
//		logger.info("*****entering closeConnection,thread:"+Thread.currentThread().getId());
//		System.out.println(Thread.currentThread().getId() + "--closing connection:id="+connection.hashCode() + ",queue:"+this.printCurrent());
		try{
			connection.flushCommands();
			if( connection.isOpen() ){
				int queueSize = queue.size();
//				System.out.println("close connection,queue size:"+queueSize);
				if( queueSize>=CacheManagerFactory.MaxConnection){
					logger.info("destroy connect due to reach the max number,id:"+connection.hashCode()+",thread="+Thread.currentThread().getId());
					this.destoryConnection(connection);
				}else{
					queue.offer(connection);
				}
			}else{
				connection.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
//			logger.info("******exit closeConnection,thread:"+Thread.currentThread().getId());
		}
		
		
//		try{
//			pool.returnObject(connection);
//			System.out.println("close connection , hashcode:"+connection.hashCode() + ",Thread:"+Thread.currentThread().getId());
//		}catch(IllegalStateException ex){
//			System.out.println("Returning connection error , status:"+connection.isOpen() + ",hashcode:"+connection.hashCode() + ",Thread:"+Thread.currentThread().getId());
//			throw ex;
//		}
		
	}
	
	public synchronized void destoryConnection(StatefulRedisConnection<String,String> connection){
		logger.info("destory from pool:id="+connection.hashCode()+",thread="+Thread.currentThread().getId());
		connection.close();
		connection=null;
//		logger.info("destorying connection in pool,id"+connection.hashCode());
//		connection.close();
//		pool.returnObject(connection);
	}
	
	
	public RedisCache getRedisCache(String name) {
//		System.out.println("test in getRedisCache:"+name);
		return new RedisCache(name);
	}
	
	public RedisClient getRedisClient() {
		return redisClient;
	}
	
	public int getQueueSize(){
		return queue.size();
	}
	
	private boolean isConnectionInPool( StatefulRedisConnection<String,String> connection ){
		boolean isSame = false;
		if( !queue.isEmpty() ){
			Iterator<StatefulRedisConnection<String,String>> iter = queue.iterator();
			while( iter.hasNext() ){
				StatefulRedisConnection<String,String> current = iter.next();
				if( connection.hashCode() == current.hashCode() ){
					isSame = true;
					break;
				}
			}
		}
		return isSame;
	}
	
	private String printCurrent(){
		StringBuffer stringBuffer = new StringBuffer();
		if( !queue.isEmpty() ){
			Iterator<StatefulRedisConnection<String,String>> iter = queue.iterator();
			while( iter.hasNext() ){
				StatefulRedisConnection<String,String> current = iter.next();
				stringBuffer.append(current.hashCode() + ",");
			}
//			System.out.println("current exist in queue:"+stringBuffer);
		}
		return stringBuffer.toString();
	}
	
}
