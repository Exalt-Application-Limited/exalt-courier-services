package com.exalt.courier.shared.exception;

/**
 * Exception thrown when a business rule is violated.
 * This exception is used across all courier services to indicate
 * that a business rule has been violated.
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new BusinessException with the specified message.
     *
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Creates a new BusinessException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
