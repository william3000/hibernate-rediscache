package com.xl.core.cache.exception;

public final class HibernateNonstopCacheExceptionHandler {

	public static final String HIBERNATE_THROW_EXCEPTION_ON_TIMEOUT_PROPERTY = "ehcache.hibernate.propagateNonStopCacheException";

	public static final String HIBERNATE_LOG_EXCEPTION_STACK_TRACE_PROPERTY = "ehcache.hibernate.logNonStopCacheExceptionStackTrace";

	private static final HibernateNonstopCacheExceptionHandler INSTANCE = new HibernateNonstopCacheExceptionHandler();

	private HibernateNonstopCacheExceptionHandler() {
		// private
	}

	public static HibernateNonstopCacheExceptionHandler getInstance() {
		return INSTANCE;
	}

	public void handleNonstopCacheException(NonStopCacheException nonStopCacheException) {
		if (Boolean.getBoolean(HIBERNATE_THROW_EXCEPTION_ON_TIMEOUT_PROPERTY)) {
			throw nonStopCacheException;
		} else {
			if (Boolean.getBoolean(HIBERNATE_LOG_EXCEPTION_STACK_TRACE_PROPERTY)) {

			} else {

			}
		}
	}
}
