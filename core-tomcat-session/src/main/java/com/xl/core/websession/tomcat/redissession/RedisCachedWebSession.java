package com.xl.core.websession.tomcat.redissession;

import java.io.Serializable;
import java.util.Map;

public class RedisCachedWebSession implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8568036044966875772L;
	
	private long createTime;
	
	private long lastAccessTime;
	
	private int maxInactiveInterval;
	
	private boolean isNew;
	
	private boolean isValid;
	
	private long thisAccessedTime;
	
	private String id;
	
	private int n;
	
	private Map<String,Object> attributes;

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(long lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	public void setMaxInactiveInterval(int maxInactiveInterval) {
		this.maxInactiveInterval = maxInactiveInterval;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	public long getThisAccessedTime() {
		return thisAccessedTime;
	}

	public void setThisAccessedTime(long thisAccessedTime) {
		this.thisAccessedTime = thisAccessedTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
}
