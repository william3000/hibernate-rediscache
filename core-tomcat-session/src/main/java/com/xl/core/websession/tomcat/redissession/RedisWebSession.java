package com.xl.core.websession.tomcat.redissession;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.Manager;
import org.apache.catalina.SessionListener;
import org.apache.catalina.session.StandardSession;

public class RedisWebSession extends StandardSession implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8367824437219592364L;

	protected static boolean manualDirtyTrackingSupportEnabled = false;

	protected static String manualDirtyTrackingAttributeKey = "__changed__";

	protected HashMap<String, Object> changedAttributes;

	protected boolean dirty;

	public RedisWebSession(Manager manager) {
		super(manager);
		this.resetDirtyTracking();
	}

	public static void setManualDirtyTrackingSupportEnabled(boolean manualDirtyTrackingSupportEnabled) {
		RedisWebSession.manualDirtyTrackingSupportEnabled = manualDirtyTrackingSupportEnabled;
	}

	public static void setManualDirtyTrackingAttributeKey(String manualDirtyTrackingAttributeKey) {
		RedisWebSession.manualDirtyTrackingAttributeKey = manualDirtyTrackingAttributeKey;
	}

	public HashMap<String, Object> getChangedAttributes() {
		return changedAttributes;
	}

	public void resetDirtyTracking() {
		changedAttributes = new HashMap<String, Object>();
		dirty = false;
	}

	public boolean isDirty() {
		return dirty || !changedAttributes.isEmpty();
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setAttribute(String key, Object value) {
		if (manualDirtyTrackingSupportEnabled && manualDirtyTrackingAttributeKey.equals(key)) {
			dirty = true;
			return;
		}

		Object oldValue = this.getAttribute(key);
		super.setAttribute(key, value);

		if ((value != null || oldValue != null) && (value == null && oldValue != null || oldValue == null && value != null || !value.getClass().isInstance(oldValue) || !value.equals(oldValue))) {
			if (this.manager instanceof RedisWebSessionManager && ((RedisWebSessionManager) this.manager).getSaveOnChange()) {
				try {
					RedisWebSessionManager redisManager = (RedisWebSessionManager) this.manager;

					System.out.println("set attribute:" + this.getId());
					redisManager.saveSession(this, true);
				} catch (IOException ex) {
					System.out.println("Error saving session on setAttribute (triggered by saveOnChange=true): " + ex.getMessage());
				}
			} else {
				this.changedAttributes.put(key, value);
			}
		}

	}

	@Override
	public void removeAttribute(String name) {
		super.removeAttribute(name);
		if (this.manager instanceof RedisWebSessionManager && ((RedisWebSessionManager) this.manager).getSaveOnChange()) {
			try {
				RedisWebSessionManager redisManager = (RedisWebSessionManager) this.manager;
				redisManager.delSession(name);
			} catch (IOException ex) {
				System.out.println("Error remove session on setAttribute (triggered by saveOnChange=true): " + ex.getMessage());
			}
		} else {
			dirty = true;
		}
	}

	@Override
	public void setPrincipal(Principal principal) {
		dirty = true;
		super.setPrincipal(principal);
	}

	@Override
	public void readObjectData(ObjectInputStream stream) throws ClassNotFoundException, IOException {
//		super.readObjectData(stream);
//		this.setCreationTime(stream.readLong());
	}

	@Override
	public void writeObjectData(ObjectOutputStream stream) throws IOException {
//		super.writeObjectData(stream);
//		stream.writeLong(this.getCreationTime());
	}
	
	protected void updateSessionByCache(RedisCachedWebSession cacheSession) throws ClassNotFoundException, IOException{
		authType = null; // Transient only
		creationTime = cacheSession.getCreateTime();
		lastAccessedTime = cacheSession.getLastAccessTime();
		maxInactiveInterval = cacheSession.getMaxInactiveInterval();
		isNew = cacheSession.isNew();
		isValid = cacheSession.isValid();
		thisAccessedTime = cacheSession.getThisAccessedTime();
		principal = null; // Transient only
		id = cacheSession.getId();
		if (attributes == null){
			attributes = new ConcurrentHashMap<String, Object>();
		}
		int n = cacheSession.getN();
		boolean isValidSave = isValid;
		isValid = true;
		if( cacheSession.getAttributes()!=null && !cacheSession.getAttributes().isEmpty() ){
			for( String key : cacheSession.getAttributes().keySet() ){
				Object value = cacheSession.getAttributes().get(key);
				if( (value instanceof String) && (value.equals(NOT_SERIALIZED)) ){
					continue;
				}else{
					attributes.put(key, value);
				}
			}
		}
		isValid = isValidSave;
		if (listeners == null) {
			listeners = new ArrayList<SessionListener>();
		}

		if (notes == null) {
			notes = new Hashtable<String, Object>();
		}
	}
	
	protected void updateCacheBySession(RedisCachedWebSession cacheSession){
		cacheSession.setId(manualDirtyTrackingAttributeKey);
		cacheSession.setCreateTime(Long.valueOf(creationTime));
		cacheSession.setLastAccessTime(Long.valueOf(lastAccessedTime));
		cacheSession.setMaxInactiveInterval(Integer.valueOf(maxInactiveInterval));
		cacheSession.setNew(Boolean.valueOf(isNew));
		cacheSession.setThisAccessedTime(Long.valueOf(thisAccessedTime));
		cacheSession.setValid(Boolean.valueOf(isValid));
		
		if( attributes!=null && !attributes.isEmpty() ){
			Map<String,Object> serialMap = new ConcurrentHashMap<String,Object>();
			cacheSession.setN(attributes.size());
			for( String name : attributes.keySet() ){
				Object value = attributes.get(name);
				if (value == null){
					continue;
				}else if( (value instanceof Serializable) && (!exclude(name)) ){
					serialMap.put(name, value);
				}else{
					removeAttributeInternal(name, true);
				}
			}
			cacheSession.setAttributes(serialMap);
		}
	}
	
	

	/**
	protected void doReadObject(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		// Deserialize the scalar instance variables (except Manager)
		authType = null; // Transient only
		creationTime = ((Long) stream.readObject()).longValue();
		lastAccessedTime = ((Long) stream.readObject()).longValue();
		maxInactiveInterval = ((Integer) stream.readObject()).intValue();
		isNew = ((Boolean) stream.readObject()).booleanValue();
		isValid = ((Boolean) stream.readObject()).booleanValue();
		thisAccessedTime = ((Long) stream.readObject()).longValue();
		principal = null; // Transient only
		// setId((String) stream.readObject());
		id = (String) stream.readObject();
		if (manager.getContext().getLogger().isDebugEnabled())
			manager.getContext().getLogger().debug("readObject() loading session " + id);

		// Deserialize the attribute count and attribute values
		if (attributes == null)
			attributes = new ConcurrentHashMap<String, Object>();
		int n = ((Integer) stream.readObject()).intValue();
		boolean isValidSave = isValid;
		isValid = true;
		for (int i = 0; i < n; i++) {
			String name = (String) stream.readObject();
			Object value = stream.readObject();
			if ((value instanceof String) && (value.equals(NOT_SERIALIZED)))
				continue;
			if (manager.getContext().getLogger().isDebugEnabled())
				manager.getContext().getLogger().debug("  loading attribute '" + name + "' with value '" + value + "'");
			attributes.put(name, value);
		}
		isValid = isValidSave;

		if (listeners == null) {
			listeners = new ArrayList<SessionListener>();
		}

		if (notes == null) {
			notes = new Hashtable<String, Object>();
		}
	}
	*/
	
	
	/**
	protected void doWriteObject(ObjectOutputStream stream) throws IOException {
		// Write the scalar instance variables (except Manager)
		stream.writeObject(Long.valueOf(creationTime));
		stream.writeObject(Long.valueOf(lastAccessedTime));
		stream.writeObject(Integer.valueOf(maxInactiveInterval));
		stream.writeObject(Boolean.valueOf(isNew));
		stream.writeObject(Boolean.valueOf(isValid));
		stream.writeObject(Long.valueOf(thisAccessedTime));
		stream.writeObject(id);
		if (manager.getContext().getLogger().isDebugEnabled())
			manager.getContext().getLogger().debug("writeObject() storing session " + id);

		// Accumulate the names of serializable and non-serializable attributes
		String keys[] = keys();
		ArrayList<String> saveNames = new ArrayList<String>();
		ArrayList<Object> saveValues = new ArrayList<Object>();
		for (int i = 0; i < keys.length; i++) {
			Object value = attributes.get(keys[i]);
			if (value == null)
				continue;
			else if ((value instanceof Serializable) && (!exclude(keys[i]))) {
				saveNames.add(keys[i]);
				saveValues.add(value);
			} else {
				removeAttributeInternal(keys[i], true);
			}
		}

		// Serialize the attribute count and the Serializable attributes
		int n = saveNames.size();
		stream.writeObject(Integer.valueOf(n));
		for (int i = 0; i < n; i++) {
			stream.writeObject(saveNames.get(i));
			try {
				stream.writeObject(saveValues.get(i));
				if (manager.getContext().getLogger().isDebugEnabled())
					manager.getContext().getLogger().debug("  storing attribute '" + saveNames.get(i) + "' with value '" + saveValues.get(i) + "'");
			} catch (NotSerializableException e) {
				manager.getContext().getLogger().warn(sm.getString("standardSession.notSerializable", saveNames.get(i), id), e);
				stream.writeObject(NOT_SERIALIZED);
				if (manager.getContext().getLogger().isDebugEnabled())
					manager.getContext().getLogger().debug("  storing attribute '" + saveNames.get(i) + "' with value NOT_SERIALIZED");
			}
		}
	}
	*/

}
