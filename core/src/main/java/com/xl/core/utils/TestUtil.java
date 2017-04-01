package com.xl.core.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;

public class TestUtil {

	public static void main(String[] args) {
		RedisClient redisClient = RedisClient.create("redis://127.0.0.1:6379");
		StatefulRedisConnection<String, String> connection = redisClient.connect();
		RedisCommands<String, String> syncCommands = connection.sync();
//		syncCommands.set("key", "Hello, Redis!");
		
		

		Kryo kryo = new Kryo();
		kryo.register(TestObj.class);
		TestObj obj = new TestObj();
		obj.setId(1);
		obj.setCode("code1");
		obj.setName("name1");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Output output = new Output(out,1024);
		kryo.writeClassAndObject(output, obj);
		System.out.println("1:"+output.toBytes());
		
		byte[] b1 = output.toBytes();
		
		try{
			String s1 = new String(b1,StandardCharsets.ISO_8859_1);
			
			syncCommands.set("aa", s1);
//			System.out.println(s1);
			
			String s2 = syncCommands.get("aa");
			
			Input input = new Input(s2.getBytes(StandardCharsets.ISO_8859_1),0,2014);
			Object o1 = kryo.readClassAndObject(input);
			if(o1 instanceof  TestObj){
				TestObj o2 = (TestObj)o1;
				System.out.println(o2.getId());
				System.out.println(o2.getCode());
				System.out.println(o2.getName());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		connection.close();
		redisClient.shutdown();
		
	}

}
