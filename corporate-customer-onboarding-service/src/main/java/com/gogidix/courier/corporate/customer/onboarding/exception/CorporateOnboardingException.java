package com.gogidix.courier.corporate.customer.onboarding.exception;

/**
 * Custom exception for corporate customer onboarding related business logic errors.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public class CorporateOnboardingException extends RuntimeException {

    public CorporateOnboardingException(String message) {
        super(message);
    }

    public CorporateOnboardingException(String message, Throwable cause) {
        super(message, cause);
    }
}