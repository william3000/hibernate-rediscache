package com.xl.core.websession;

import java.util.Arrays;

public class SessionOptions {
	
	public enum SessionPersistPolicy{
		
		DEFAULT,SAVE_ON_CHANGE,SAVE_ON_REQUEST;
		
		public static SessionPersistPolicy forName(String name){
			SessionPersistPolicy policy = SessionPersistPolicy.valueOf(name);
			if( policy!=null ){
				return policy;
			}else{
				throw new IllegalArgumentException("Invalid session persist policy [" + name + "]. Must be one of " + Arrays.asList(SessionPersistPolicy.values())+ ".");
			}
		}
	}

}
