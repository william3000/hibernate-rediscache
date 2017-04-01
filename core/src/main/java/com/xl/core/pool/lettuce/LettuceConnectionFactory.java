package com.xl.core.pool.lettuce;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;

public class LettuceConnectionFactory extends BasePooledObjectFactory<StatefulRedisConnection<String,String>> {
	
	private final static Log logger = LogFactory.getLog(LettuceConnectionFactory.class);	
	
	private static AtomicLong cCounter = new AtomicLong(0);
	
	private static AtomicLong dCounter = new AtomicLong(0);
	
	private final RedisClient redisClient;
	
	public LettuceConnectionFactory(RedisClient redisClient) {
		super();
		this.redisClient = redisClient;
	}

	@Override
	public StatefulRedisConnection<String, String> create() throws Exception {
		StatefulRedisConnection<String, String> connection = this.redisClient.connect();
		System.out.println("create connection in factory:"+cCounter.incrementAndGet() + ",hascode:"+connection.hashCode());
		return connection;
	}

	@Override
	public PooledObject<StatefulRedisConnection<String, String>> wrap(StatefulRedisConnection<String, String> connection) {
		return new DefaultPooledObject<StatefulRedisConnection<String, String>>(connection);
	}

	@Override
	public void destroyObject(PooledObject<StatefulRedisConnection<String, String>> p) throws Exception {
		StatefulRedisConnection<String, String> connection = p.getObject();
		logger.info("destory connection in factory:"+dCounter.incrementAndGet());
		if( connection.isOpen() ){
			connection.close();
		}
	}

	@Override
	public boolean validateObject(PooledObject<StatefulRedisConnection<String, String>> p) {
		StatefulRedisConnection<String, String> connection = p.getObject();
		boolean result = connection.isOpen();
		if( !result ){
			logger.info( "validate connection in factory:result="+connection.isOpen() );
		}
		return result;
	}
	
	

}
