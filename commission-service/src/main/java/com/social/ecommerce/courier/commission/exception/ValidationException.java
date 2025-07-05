package com.gogidix.courierservices.commission.$1;

/**
 * Exception thrown when input validation fails.
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
