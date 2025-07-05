package com.gogidix.courier.customer.onboarding.dto;

import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for customer onboarding application operations.
 */
@Schema(description = "Customer onboarding application response")
public record CustomerOnboardingApplicationResponse(
    
    @Schema(description = "Application reference ID", example = "CUST-ONB-2025-001234")
    String applicationReferenceId,
    
    @Schema(description = "Customer email", example = "john.doe@example.com")
    String customerEmail,
    
    @Schema(description = "Customer phone", example = "+1234567890")
    String customerPhone,
    
    @Schema(description = "Customer first name", example = "John")
    String firstName,
    
    @Schema(description = "Customer last name", example = "Doe")
    String lastName,
    
    @Schema(description = "Date of birth", example = "1990-01-15")
    String dateOfBirth,
    
    @Schema(description = "National ID", example = "123456789")
    String nationalId,
    
    @Schema(description = "Address line 1", example = "123 Main Street")
    String addressLine1,
    
    @Schema(description = "Address line 2", example = "Apt 4B")
    String addressLine2,
    
    @Schema(description = "City", example = "New York")
    String city,
    
    @Schema(description = "State or Province", example = "NY")
    String stateProvince,
    
    @Schema(description = "Postal code", example = "10001")
    String postalCode,
    
    @Schema(description = "Country", example = "United States")
    String country,
    
    @Schema(description = "Current application status")
    CustomerOnboardingStatus applicationStatus,
    
    @Schema(description = "KYC verification ID", example = "KYC-VER-2025-001234")
    String kycVerificationId,
    
    @Schema(description = "Auth service user ID", example = "AUTH-USER-001234")
    String authServiceUserId,
    
    @Schema(description = "Billing customer ID", example = "BILL-CUST-001234")
    String billingCustomerId,
    
    @Schema(description = "Preferred communication method", example = "EMAIL")
    String preferredCommunicationMethod,
    
    @Schema(description = "Marketing consent", example = "true")
    Boolean marketingConsent,
    
    @Schema(description = "Terms accepted", example = "true")
    Boolean termsAccepted,
    
    @Schema(description = "Privacy policy accepted", example = "true")
    Boolean privacyPolicyAccepted,
    
    @Schema(description = "When application was submitted")
    LocalDateTime submittedAt,
    
    @Schema(description = "When application was approved")
    LocalDateTime approvedAt,
    
    @Schema(description = "When application was rejected")
    LocalDateTime rejectedAt,
    
    @Schema(description = "Rejection reason if applicable")
    String rejectionReason,
    
    @Schema(description = "Application creation timestamp")
    LocalDateTime createdAt,
    
    @Schema(description = "Last update timestamp")
    LocalDateTime updatedAt,
    
    @Schema(description = "Number of verification documents uploaded")
    Integer documentCount,
    
    @Schema(description = "List of uploaded verification documents")
    List<VerificationDocumentResponse> verificationDocuments
) {}