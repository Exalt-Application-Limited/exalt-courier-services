package com.gogidix.courier.customer.onboarding.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for KycServiceClient to handle service unavailability.
 */
@Slf4j
@Component
public class KycServiceClientFallback implements KycServiceClient {

    @Override
    public KycVerificationResponse initiateKycVerification(InitiateKycRequest request) {
        log.error("KYC Service unavailable - cannot initiate KYC verification for customer: {}", request.customerReferenceId());
        throw new RuntimeException("KYC Service is currently unavailable. Please try again later.");
    }

    @Override
    public KycStatusResponse getKycStatus(String verificationId) {
        log.error("KYC Service unavailable - cannot get KYC status for verification: {}", verificationId);
        throw new RuntimeException("KYC Service is currently unavailable. Please try again later.");
    }

    @Override
    public void submitForManualReview(String verificationId, ManualReviewRequest request) {
        log.error("KYC Service unavailable - cannot submit for manual review: {}", verificationId);
        throw new RuntimeException("KYC Service is currently unavailable. Please try again later.");
    }

    @Override
    public void approveKyc(String verificationId, KycDecisionRequest request) {
        log.error("KYC Service unavailable - cannot approve KYC: {}", verificationId);
        throw new RuntimeException("KYC Service is currently unavailable. Please try again later.");
    }

    @Override
    public void rejectKyc(String verificationId, KycDecisionRequest request) {
        log.error("KYC Service unavailable - cannot reject KYC: {}", verificationId);
        throw new RuntimeException("KYC Service is currently unavailable. Please try again later.");
    }
}