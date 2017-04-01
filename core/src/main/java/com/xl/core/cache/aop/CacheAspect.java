package com.xl.core.cache.aop;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import com.xl.core.annotation.Cache;
import com.xl.core.redis.RedisHandler;
import com.xl.core.redis.RedisUtils;

public class CacheAspect {
	
	private final static Log logger = LogFactory.getLog(CacheAspect.class);
	
	public void doAfter(JoinPoint jp) {

	}

	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
//		logger.info("into the doAround of CacheAspect");
		Cache cache = getCacheAnnotation(pjp);
		if( cache != null ){
			//Need to cache
			String cacheKey = cache.cacheKey();
			if( cacheKey!=null && !cacheKey.isEmpty() ){
				//Check whether has got something in the cache
				RedisHandler redisHandler = RedisUtils.getRedisHandler();
				
				Object[] args = pjp.getArgs();
				String argString = "";
				if( args!=null ){
					for( Object arg : args ){
						argString = String.valueOf(arg) + ".";
					}
				}
				String key = cacheKey + "." + argString;
				
				
				Object cacheObj = redisHandler.get(key);
				if( cacheObj != null ){
					logger.info("In CacheAspect,getting data from redis,key="+key);
					return cacheObj;
				}else{
					Object retVal = pjp.proceed();
					redisHandler.put(key, retVal);
//					logger.info("In CacheAspect,getting data from function and put them into redis,key="+cacheKey);
					return retVal;
				}
			}
		}
		Object retVal = pjp.proceed();
		return retVal;
	}

	public void doBefore(JoinPoint jp) {

	}

	public void doThrowing(JoinPoint jp, Throwable ex) {

	}

	public static Cache getCacheAnnotation(ProceedingJoinPoint pjp) {
		Signature signature = pjp.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method != null) {
			return method.getAnnotation(Cache.class);
		}
		return null;
	}

}
