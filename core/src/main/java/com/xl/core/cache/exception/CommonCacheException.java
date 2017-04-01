package com.xl.core.cache.exception;

public class CommonCacheException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5101671465461636129L;

	public CommonCacheException() {
        super();
    }

    public CommonCacheException(String message) {
        super(message);
    }

    public CommonCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonCacheException(Throwable cause) {
        super(cause);
    }
}