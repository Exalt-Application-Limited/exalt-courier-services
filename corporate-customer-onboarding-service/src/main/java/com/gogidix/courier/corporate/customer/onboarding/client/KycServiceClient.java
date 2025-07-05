package com.gogidix.courier.corporate.customer.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Feign client for KYC Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "kyc-service", path = "/api/v1/kyc")
public interface KycServiceClient {

    @PostMapping("/corporate/initiate")
    CorporateKycVerificationResponse initiateCorporateKycVerification(@RequestBody InitiateCorporateKycRequest request);

    @GetMapping("/corporate/{verificationId}/status")
    CorporateKycStatusResponse getCorporateKycStatus(@PathVariable String verificationId);

    // Request DTOs
    record InitiateCorporateKycRequest(
            String applicationReferenceId,
            String businessName,
            String businessType,
            String industryType,
            String businessRegistrationNumber,
            String taxIdentificationNumber,
            String businessEmail,
            String businessPhone,
            String businessAddress,
            String primaryContactFirstName,
            String primaryContactLastName,
            String primaryContactEmail,
            String verificationType
    ) {}

    // Response DTOs
    record CorporateKycVerificationResponse(
            String verificationId,
            String status,
            String corporateReferenceId,
            String estimatedCompletionTime,
            List<String> requiredDocuments,
            LocalDateTime createdAt
    ) {}

    record CorporateKycStatusResponse(
            String verificationId,
            String status,
            Integer progress,
            LocalDateTime lastUpdated,
            String statusMessage,
            Boolean requiresManualReview,
            String nextAction,
            Map<String, Object> businessVerificationDetails,
            Map<String, String> complianceChecks,
            String estimatedCompletionTime
    ) {}
}