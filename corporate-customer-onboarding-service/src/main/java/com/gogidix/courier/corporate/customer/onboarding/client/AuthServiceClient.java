package com.gogidix.courier.corporate.customer.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Feign client for Auth Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "auth-service", path = "/api/v1/auth")
public interface AuthServiceClient {

    @PostMapping("/corporate/accounts")
    CorporateAccountResponse createCorporateAccount(@RequestBody CreateCorporateAccountRequest request);

    @PostMapping("/corporate/users")
    CorporateUserResponse createCorporateUser(@RequestBody CreateCorporateUserRequest request);

    // Request DTOs
    record CreateCorporateAccountRequest(
            String businessName,
            String businessEmail,
            String businessPhone,
            String primaryContactFirstName,
            String primaryContactLastName,
            String primaryContactEmail,
            String role,
            String applicationReferenceId
    ) {}

    record CreateCorporateUserRequest(
            String corporateId,
            String email,
            String firstName,
            String lastName,
            String role,
            List<String> permissions
    ) {}

    // Response DTOs
    record CorporateAccountResponse(
            String corporateId,
            String businessName,
            String businessEmail,
            String status,
            LocalDateTime createdAt
    ) {}

    record CorporateUserResponse(
            String userId,
            String corporateId,
            String email,
            String firstName,
            String lastName,
            String role,
            String status,
            LocalDateTime createdAt
    ) {}
}