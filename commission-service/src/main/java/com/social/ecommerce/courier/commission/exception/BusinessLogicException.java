package com.gogidix.courierservices.commission.$1;

/**
 * Exception thrown when business logic rules are violated.
 */
public class BusinessLogicException extends RuntimeException {

    public BusinessLogicException(String message) {
        super(message);
    }
}
