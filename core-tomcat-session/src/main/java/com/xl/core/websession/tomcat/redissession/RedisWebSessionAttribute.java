package com.xl.core.websession.tomcat.redissession;

import java.io.Serializable;

public class RedisWebSessionAttribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8234608903626194526L;
	
	private byte[] attributeHash;

	public RedisWebSessionAttribute() {
		this.attributeHash = new byte[0];
	}

	public byte[] getAttributeHash() {
		return attributeHash;
	}

	public void setAttributeHash(byte[] attributeHash) {
		this.attributeHash = attributeHash;
	}
	
//	public void copyField(RedisWebSessionAttribute attribute){
//		this.setAttributeHash(attribute.getAttributeHash());
//	}

}
