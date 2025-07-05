package com.gogidix.courier.customer.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for integrating with the Auth Service from shared-infrastructure.
 * Handles customer authentication and user account management.
 */
@FeignClient(name = "auth-service", fallback = AuthServiceClientFallback.class)
public interface AuthServiceClient {

    @PostMapping("/api/v1/users/customer")
    CustomerUserResponse createCustomerUser(@RequestBody CreateCustomerUserRequest request);

    @GetMapping("/api/v1/users/{userId}")
    CustomerUserResponse getUser(@PathVariable("userId") String userId);

    @PutMapping("/api/v1/users/{userId}/activate")
    void activateUser(@PathVariable("userId") String userId);

    @PutMapping("/api/v1/users/{userId}/suspend")
    void suspendUser(@PathVariable("userId") String userId);

    @PostMapping("/api/v1/auth/password-reset")
    void initiatePasswordReset(@RequestBody PasswordResetRequest request);

    /**
     * Request DTO for creating a customer user account
     */
    record CreateCustomerUserRequest(
            String email,
            String phone,
            String firstName,
            String lastName,
            String temporaryPassword,
            String userType,
            String customerReferenceId
    ) {}

    /**
     * Request DTO for password reset
     */
    record PasswordResetRequest(
            String email
    ) {}

    /**
     * Response DTO for customer user operations
     */
    record CustomerUserResponse(
            String userId,
            String email,
            String phone,
            String firstName,
            String lastName,
            String status,
            String userType,
            String createdAt
    ) {}
}