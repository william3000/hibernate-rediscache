package com.xl.core.websession.tomcat.redissession;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class RedisWebSessionValve extends ValveBase {
	
	private final Log log = LogFactory.getLog(RedisWebSessionValve.class);

	private RedisWebSessionManager manager;
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		try{
			this.getNext().invoke(request, response);
		}finally{
			manager.afterRequest();
		}
	}

	public void setManager(RedisWebSessionManager manager) {
		this.manager = manager;
	}

}
