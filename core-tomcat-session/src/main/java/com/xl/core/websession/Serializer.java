package com.xl.core.websession;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.util.CustomObjectInputStream;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.nustaq.serialization.FSTConfiguration;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xl.core.websession.tomcat.redissession.RedisCachedWebSession;

public class Serializer {

	private static final Log log = LogFactory.getLog(Serializer.class);

	protected ThreadLocal<FSTConfiguration> currentConfiguration = new ThreadLocal<FSTConfiguration>();
	
	private FSTConfiguration getFSTConfiguration(){
		
//		if( this.currentConfiguration.get() != null ){
//			return this.currentConfiguration.get();
//		}else{
			FSTConfiguration configuration = FSTConfiguration.createStructConfiguration();
			configuration.setForceSerializable(true);
			this.currentConfiguration.set(configuration);
			return configuration;
//		}
	}
	
	public static Serializer getInstance(){
		return new Serializer();
	}

	/**
	public static String serialize(RedisWebSession session , RedisWebSessionAttribute attribute) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(bos));
        oos.writeObject(attribute);
        session.writeObjectData(oos);
        oos.flush();
        
        return new String(bos.toByteArray(), StandardCharsets.ISO_8859_1);
	}

	public static void unserialize(String sString,RedisWebSession session,RedisWebSessionAttribute attribute) {
		try {
			byte[] bytes = sString.getBytes(StandardCharsets.ISO_8859_1);
			if (bytes != null && bytes.length != 0) {
				BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
		        ObjectInputStream ois = new CustomObjectInputStream(bis, Serializer.class.getClassLoader());
		        RedisWebSessionAttribute sessionAttribute = (RedisWebSessionAttribute)ois.readObject();
		        attribute.setAttributeHash(sessionAttribute.getAttributeHash());
		        session.readObjectData(ois);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
	
	public byte[] fstSerializeToBytes(Object obj) {
		FSTConfiguration configuration = this.getFSTConfiguration();
		return configuration.asByteArray(obj);
	}
	
	public byte[] fstForceSerializeToBytes(Object obj) {
		FSTConfiguration configuration = this.getFSTConfiguration();
		configuration.setForceSerializable(true);
		return configuration.asByteArray(obj);
	}

	public Object fstUnserializeFromBytes(byte[] sec) {
		FSTConfiguration configuration = this.getFSTConfiguration();
		return configuration.asObject(sec);
	}
	
	public String fstSerializeToString(Object obj) {
		byte[] bs = fstSerializeToBytes(obj);
		return new String(bs, StandardCharsets.ISO_8859_1);
	}

	public Object fstUnserializeFromString(String sec) {
		byte[] bs = sec.getBytes(StandardCharsets.ISO_8859_1);
		return fstUnserializeFromBytes(bs);
	}
	

	public byte[] jdkSerializeToBytes(Object object) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(baos));
			oos.writeObject(object);
			oos.flush();
			oos.close();
			byte[] userABytes = baos.toByteArray();
			return userABytes;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object jdkUnserializeFromBytes(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			BufferedInputStream bis = new BufferedInputStream(bais);
			ObjectInputStream ois = new CustomObjectInputStream(bis, Serializer.class.getClassLoader());
			Object obj = ois.readObject();
			ois.close();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String jdkSerializeToString(Object object) {
		byte[] bs = this.jdkSerializeToBytes(object);
		return new String(bs, StandardCharsets.ISO_8859_1);
	}
	
	public Object jdkUnserializeFromString(String sec) {
		byte[] bs = sec.getBytes(StandardCharsets.ISO_8859_1);
		return this.jdkUnserializeFromBytes(bs);
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
	
	public static void main(String[] args) {
		try{
//			String redisUri = "redis://127.0.0.1:6379";
//			RedisClient redisClient = RedisClient.create(redisUri);
//			RedisCommands<String, String> command = redisClient.connect().sync();
//			String valueString = command.get("Session23A11F3DF3F586043C81BC71A9214FE0@frankys-MBP.local");
//			
//			System.out.println(valueString);
//			Object obj = Serializer.getInstance().fstUnserializeFromString(valueString);
//			
			RedisCachedWebSession cacheSession = new RedisCachedWebSession();
			cacheSession.setId("121");
			
			Map<String, String> ss = new HashMap();
			ss.put("abc", "123");
			
			FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();
//			configuration.setForceSerializable(true);
			
			
//			DefaultCoder coder = new DefaultCoder();
			
			byte[] ba = configuration.asByteArray(cacheSession);
			
			Object obj = configuration.asObject(ba);
			
			/**
			RedisWebSessionAttribute attribute = new RedisWebSessionAttribute();
			
			if (bytes != null && bytes.length != 0) {
				BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
		        ObjectInputStream stream = new CustomObjectInputStream(bis, Serializer.class.getClassLoader());
		        RedisWebSessionAttribute sessionAttribute = (RedisWebSessionAttribute)stream.readObject();
		        attribute.setAttributeHash(sessionAttribute.getAttributeHash());
		        
		        
		        
		        String authType = null;        // Transient only
		        long creationTime = ((Long) stream.readObject()).longValue();
		        long lastAccessedTime = ((Long) stream.readObject()).longValue();
		        int maxInactiveInterval = ((Integer) stream.readObject()).intValue();
		        boolean isNew = ((Boolean) stream.readObject()).booleanValue();
		        boolean isValid = ((Boolean) stream.readObject()).booleanValue();
		        long thisAccessedTime = ((Long) stream.readObject()).longValue();
		        Principal principal = null;        // Transient only
		        String id = (String) stream.readObject();
		        Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
		        int n = ((Integer) stream.readObject()).intValue();
		        boolean isValidSave = isValid;
		        isValid = true;
		        String NOT_SERIALIZED = "___NOT_SERIALIZABLE_EXCEPTION___";
		        for (int i = 0; i < n; i++) {
		            String name = (String) stream.readObject();
		            System.out.println(name);
		            
		            Object value = stream.readObject();
		            if ((value instanceof String) && (value.equals(NOT_SERIALIZED)))
		                continue;
		            attributes.put(name, value);
		        }
		        isValid = isValidSave;
		        
		        
		        stream.close();
		        
			}**/
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
//		try{
//			String hostName = InetAddress.getLocalHost().getHostName();
//			System.out.println(hostName);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
	}

}
