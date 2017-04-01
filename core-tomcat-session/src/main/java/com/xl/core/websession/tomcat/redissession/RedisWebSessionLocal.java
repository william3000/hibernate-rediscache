package com.xl.core.websession.tomcat.redissession;

public class RedisWebSessionLocal {

	private String sessionId;
	
	private Boolean sessionIsPersisted;
	
	private RedisWebSession session;
	
	private RedisWebSessionAttribute attribute;

	public String getSessionId() {
		return sessionId;
	}

//	public void setSessionId(String sessionId) {
//		this.sessionId = sessionId;
//	}

	public Boolean isSessionIsPersisted() {
		return sessionIsPersisted;
	}

	public void setSessionIsPersisted(Boolean sessionIsPersisted) {
		this.sessionIsPersisted = sessionIsPersisted;
	}

	public RedisWebSession getSession() {
		return session;
	}

	public void setSession(RedisWebSession session) {
		this.session = session;
		this.sessionId = session.getId();
	}

	public RedisWebSessionAttribute getAttribute() {
		return attribute;
	}

	public void setAttribute(RedisWebSessionAttribute attribute) {
		this.attribute = attribute;
	}
	
}
