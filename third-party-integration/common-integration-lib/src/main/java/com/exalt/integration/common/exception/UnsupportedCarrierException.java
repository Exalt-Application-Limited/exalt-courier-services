package com.exalt.integration.common.exception;

/**
 * Exception thrown when a shipping carrier is not supported
 * or when a specific carrier implementation is not available.
 */
public class UnsupportedCarrierException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new UnsupportedCarrierException with the specified detail message.
     *
     * @param message the detail message
     */
    public UnsupportedCarrierException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new UnsupportedCarrierException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public UnsupportedCarrierException(String message, Throwable cause) {
        super(message, cause);
    }
} 