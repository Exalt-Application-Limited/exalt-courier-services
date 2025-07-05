package com.gogidix.courier.customer.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for integrating with the KYC Service from shared-infrastructure.
 * Handles customer identity verification and KYC compliance.
 */
@FeignClient(name = "kyc-service", fallback = KycServiceClientFallback.class)
public interface KycServiceClient {

    @PostMapping("/api/v1/kyc/initiate")
    KycVerificationResponse initiateKycVerification(@RequestBody InitiateKycRequest request);

    @GetMapping("/api/v1/kyc/{verificationId}/status")
    KycStatusResponse getKycStatus(@PathVariable("verificationId") String verificationId);

    @PostMapping("/api/v1/kyc/{verificationId}/manual-review")
    void submitForManualReview(@PathVariable("verificationId") String verificationId, 
                              @RequestBody ManualReviewRequest request);

    @PutMapping("/api/v1/kyc/{verificationId}/approve")
    void approveKyc(@PathVariable("verificationId") String verificationId, 
                   @RequestBody KycDecisionRequest request);

    @PutMapping("/api/v1/kyc/{verificationId}/reject")
    void rejectKyc(@PathVariable("verificationId") String verificationId, 
                  @RequestBody KycDecisionRequest request);

    /**
     * Request DTO for initiating KYC verification
     */
    record InitiateKycRequest(
            String customerReferenceId,
            String firstName,
            String lastName,
            String dateOfBirth,
            String nationalId,
            String email,
            String phone,
            String address,
            String city,
            String country,
            String verificationType
    ) {}

    /**
     * Request DTO for manual review submission
     */
    record ManualReviewRequest(
            String reason,
            String notes,
            String reviewedBy
    ) {}

    /**
     * Request DTO for KYC decision
     */
    record KycDecisionRequest(
            String decision,
            String reason,
            String notes,
            String reviewedBy
    ) {}

    /**
     * Response DTO for KYC verification initiation
     */
    record KycVerificationResponse(
            String verificationId,
            String status,
            String customerReferenceId,
            String createdAt,
            String estimatedCompletionTime
    ) {}

    /**
     * Response DTO for KYC status inquiry
     */
    record KycStatusResponse(
            String verificationId,
            String status,
            String progress,
            String lastUpdated,
            String statusMessage,
            Boolean requiresManualReview,
            String nextAction
    ) {}
}