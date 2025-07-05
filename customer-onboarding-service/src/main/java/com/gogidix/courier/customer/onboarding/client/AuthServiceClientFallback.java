package com.gogidix.courier.customer.onboarding.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for AuthServiceClient to handle service unavailability.
 */
@Slf4j
@Component
public class AuthServiceClientFallback implements AuthServiceClient {

    @Override
    public CustomerUserResponse createCustomerUser(CreateCustomerUserRequest request) {
        log.error("Auth Service unavailable - cannot create customer user for email: {}", request.email());
        throw new RuntimeException("Auth Service is currently unavailable. Please try again later.");
    }

    @Override
    public CustomerUserResponse getUser(String userId) {
        log.error("Auth Service unavailable - cannot retrieve user: {}", userId);
        throw new RuntimeException("Auth Service is currently unavailable. Please try again later.");
    }

    @Override
    public void activateUser(String userId) {
        log.error("Auth Service unavailable - cannot activate user: {}", userId);
        throw new RuntimeException("Auth Service is currently unavailable. Please try again later.");
    }

    @Override
    public void suspendUser(String userId) {
        log.error("Auth Service unavailable - cannot suspend user: {}", userId);
        throw new RuntimeException("Auth Service is currently unavailable. Please try again later.");
    }

    @Override
    public void initiatePasswordReset(PasswordResetRequest request) {
        log.error("Auth Service unavailable - cannot initiate password reset for email: {}", request.email());
        throw new RuntimeException("Auth Service is currently unavailable. Please try again later.");
    }
}