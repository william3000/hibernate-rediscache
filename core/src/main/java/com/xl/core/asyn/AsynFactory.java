package com.xl.core.asyn;

import com.googlecode.asyn4j.core.handler.CacheAsynWorkHandler;
import com.googlecode.asyn4j.service.AsynService;
import com.googlecode.asyn4j.service.AsynServiceImpl;

public class AsynFactory {
	
	private static AsynService asynService;
	
	public static void initAsyn(){
		asynService =  AsynServiceImpl.getService(1000, 1000L, 100, 100, 60*1000); 
		asynService.setWorkQueueFullHandler(new CacheAsynWorkHandler(100));
		asynService.init();
		System.out.println("asynService inited");
	}

	public static AsynService getAsynService() {
		if( asynService ==null ){
			initAsyn();
		}
		return asynService;
	}

}
