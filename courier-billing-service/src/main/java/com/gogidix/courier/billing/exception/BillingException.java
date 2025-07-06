package com.gogidix.courier.billing.exception;

/**
 * Custom exception for billing service related business logic errors.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public class BillingException extends RuntimeException {

    public BillingException(String message) {
        super(message);
    }

    public BillingException(String message, Throwable cause) {
        super(message, cause);
    }
}