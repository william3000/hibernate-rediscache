package com.xl.core.websession.tomcat.redissession;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.Session;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.lambdaworks.redis.api.sync.RedisCommands;
import com.xl.core.websession.Serializer;

public class RedisWebSessionPersister {
	
	private static final Log log = LogFactory.getLog(RedisWebSessionPersister.class);
	
	public static RedisWebSessionAttribute getAttributeByte(RedisWebSession session) throws IOException {
		byte[] serialized = null;
		Map<String, Object> attributes = new HashMap<String, Object>();
		Enumeration<String> enumerator = session.getAttributeNames();
		while (enumerator.hasMoreElements()) {
			String key = enumerator.nextElement();
			Object obj = session.getAttribute(key);
			attributes.put(key, obj);
		}
//		serialized = Serializer.kryoSerializeToBytes(attributes);
		serialized = String.valueOf(attributes.size()).getBytes();
		
		MessageDigest digester = null;
		try{
			digester = MessageDigest.getInstance("MD5");
		}catch(NoSuchAlgorithmException e){
			log.error("Unable to get MessageDigest instance for MD5");
		}
		RedisWebSessionAttribute attribute = new RedisWebSessionAttribute();
		attribute.setAttributeHash(digester.digest(serialized));
		return attribute;
	}
	
	public static boolean sessionPersist(RedisCommands<String,String> command , RedisWebSessionLocal local,boolean forcePersist) throws IOException{
		boolean isSuccess = false;
		try{
			log.info("Saving session " + local.getSessionId() + " into Redis");
		
			String sessionId = local.getSessionId();
			RedisWebSession session = local.getSession();
			
			RedisCachedWebSession cacheSession = new RedisCachedWebSession();
			
			session.updateCacheBySession(cacheSession);
			
			String sessionString = Serializer.getInstance().fstSerializeToString(cacheSession);
			
//			System.out.println(sessionString);
			
//			Object obj = Serializer.getInstance().fstUnserializeFromString(sessionString);
			
			if( "OK".equals(command.set(sessionId, sessionString))  ){
				isSuccess = true;
			}else{
				isSuccess = false;
			}
			command.expire(sessionId, 60 * 60 * 6);
			log.info("Saving session " + session.getId() + " into Redis , result:"+isSuccess);
			return isSuccess;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static RedisWebSessionLocal sessionLoad(RedisWebSessionManager manager , RedisCommands<String,String> command,String id) throws IOException{
		String data = command.get(id);
		if( data == null || data.isEmpty() ){
			return null;
		}
		System.out.println("id:"+id);
		RedisWebSession session = null;
		RedisWebSessionAttribute attribute = new RedisWebSessionAttribute();
		try{
			session = (RedisWebSession)manager.createEmptySession();
			RedisCachedWebSession cacheSession = (RedisCachedWebSession)Serializer.getInstance().fstUnserializeFromString(data);
			session.updateSessionByCache(cacheSession);
			session.setId(id);
			session.setNew(false);
			session.setMaxInactiveInterval(manager.getMaxInactiveInterval());
			session.access();
			session.setValid(true);
			session.resetDirtyTracking();
			
			attribute = getAttributeByte(session);
		}catch( Exception ex ){
			log.fatal("Unable to deserialize into session", ex);
			throw new IOException("Unable to deserialize into session", ex);
		}
		RedisWebSessionLocal local = new RedisWebSessionLocal();
		local.setAttribute(attribute);
		local.setSession(session);
		return local;
	}
	
	public static void sessionDelete(RedisCommands<String,String> command,String id) throws IOException{
		command.del(id);
	}

}
