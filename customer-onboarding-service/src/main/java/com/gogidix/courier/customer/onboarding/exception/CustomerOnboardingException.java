package com.gogidix.courier.customer.onboarding.exception;

/**
 * Custom exception for customer onboarding related business logic errors.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public class CustomerOnboardingException extends RuntimeException {

    public CustomerOnboardingException(String message) {
        super(message);
    }

    public CustomerOnboardingException(String message, Throwable cause) {
        super(message, cause);
    }
}