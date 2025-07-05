package com.gogidix.courier.billing.exception;

/**
 * Exception thrown when a requested billing resource is not found.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}