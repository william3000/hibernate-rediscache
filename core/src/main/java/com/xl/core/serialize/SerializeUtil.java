package com.xl.core.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.nustaq.serialization.FSTConfiguration;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xl.core.cache.redis.lock.Item;

public class SerializeUtil {
	
	static FSTConfiguration configuration = FSTConfiguration.createStructConfiguration();
	
	public static String serialize(Object object){
		try{
			byte[] bytes = SerializeUtil.jdkSerializeToBytes(object);
//			byte[] bytes = SerializeUtil.kryoSerializeToBytes(object);
//			byte[] bytes = SerializeUtil.fstSerializeToBytes(object);
			return new String(bytes,StandardCharsets.ISO_8859_1);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object unserialize(String sString){
		try{
			byte[] bytes = sString.getBytes(StandardCharsets.ISO_8859_1);
			Object obj = SerializeUtil.jdkUnserializeFromBytes(bytes);
//			Object obj = SerializeUtil.kryoUnserializeFromBytes(bytes);
//			Object obj = SerializeUtil.fstUnserializeFromBytes(bytes);
			return obj;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static byte[] kryoSerializeToBytes(Object object) {
//		Kryox kryo = new Kryox();
		Kryo kryo = new Kryo();
		kryo.setRegistrationRequired(false);
        kryo.setMaxDepth(20);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
		Output output = new Output(out,1024*1024);
		kryo.writeClassAndObject(output, object);
		return output.toBytes();
	}
	
	public static Object kryoUnserializeFromBytes(byte[] bytes) {
//		Kryox kryo = new Kryox();
		Kryo kryo = new Kryo();
		kryo.setRegistrationRequired(false);
        kryo.setMaxDepth(20);
		Input input = new Input(bytes,0,1024*1024);
		Object object = kryo.readClassAndObject(input);
		return object;
	}
	
	public static byte[] fstSerializeToBytes(Object obj) {
		return configuration.asByteArray(obj);
	}

	public static Object fstUnserializeFromBytes(byte[] sec) {
		return configuration.asObject(sec);
	}
	
	
	public static byte[] jdkSerializeToBytes(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.close();
			byte[] userABytes = baos.toByteArray();
			return userABytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	
	public static Object jdkUnserializeFromBytes(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object obj = ois.readObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public static void main(String[] args) {
		try{
			long start = new Date().getTime();
			Item userA = new Item("waafew","1.3.2", 55);
			String aa = SerializeUtil.serialize(userA);
			Object bb = SerializeUtil.unserialize(aa);
			Item userB = (Item) bb;
			System.out.println("sss:"+userB.getValue().toString());
			System.out.println("sss:"+userB.getTimestamp());
			
			List<String> listA = new ArrayList<String>();
			listA.add("abc");
			listA.add("XYZ");
			String xx =  SerializeUtil.serialize(listA);
			Object yy = SerializeUtil.unserialize(xx);
			List<String> listB = (List<String>)yy;
			System.out.println(listB.size());
			
			long end = new Date().getTime();
			
			System.out.println("cost:"+ (end-start));

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	

}
