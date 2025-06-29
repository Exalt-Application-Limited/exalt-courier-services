package com.exalt.courierservices.management.$1;

/**
 * Exception for business rule violations.
 */
public class BusinessException extends RuntimeException {

    /**
     * Creates a new business exception with the specified message.
     *
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Creates a new business exception with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
} 