package com.gogidix.courier.commission.exception;

/**
 * Exception thrown when business logic rules are violated.
 */
public class BusinessLogicException extends RuntimeException {

    public BusinessLogicException(String message) {
        super(message);
    }
}
