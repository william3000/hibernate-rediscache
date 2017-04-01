package com.xl.core.cache.exception;

public class NonStopCacheException extends CommonCacheException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5368826494453106377L;

	public NonStopCacheException() {
        super();
    }

    /**
     * Constructor accepting a String message and a Throwable cause
     * 
     * @param message
     * @param cause
     */
    public NonStopCacheException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor accepting a String message
     * 
     * @param message
     */
    public NonStopCacheException(final String message) {
        super(message);
    }

    /**
     * Constructor accepting a Throwable cause
     * 
     * @param cause
     */
    public NonStopCacheException(final Throwable cause) {
        super(cause);
    }
}
