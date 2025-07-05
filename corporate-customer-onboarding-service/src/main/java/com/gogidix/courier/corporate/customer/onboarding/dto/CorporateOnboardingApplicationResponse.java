package com.gogidix.courier.corporate.customer.onboarding.dto;

import com.gogidix.courier.corporate.customer.onboarding.enums.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for corporate customer onboarding application.
 * 
 * Contains comprehensive application information for API responses.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record CorporateOnboardingApplicationResponse(
    UUID id,
    String applicationReferenceId,
    
    // Company information
    String companyName,
    String companyRegistrationNumber,
    String taxIdentificationNumber,
    String businessLicenseNumber,
    String companyEmail,
    String companyPhone,
    String companyWebsite,
    
    // Business classification
    BusinessType businessType,
    IndustrySector industrySector,
    CompanySize companySize,
    ShippingVolume annualShippingVolume,
    
    // Business address
    String businessAddressLine1,
    String businessAddressLine2,
    String businessCity,
    String businessStateProvince,
    String businessPostalCode,
    String businessCountry,
    
    // Primary contact
    String primaryContactFirstName,
    String primaryContactLastName,
    String primaryContactEmail,
    String primaryContactPhone,
    String primaryContactPosition,
    
    // Billing contact (optional)
    String billingContactFirstName,
    String billingContactLastName,
    String billingContactEmail,
    String billingContactPhone,
    
    // Application status and processing
    CorporateOnboardingStatus applicationStatus,
    String kybVerificationId,
    String authServiceUserId,
    String billingCustomerId,
    
    // Financial information
    BigDecimal requestedCreditLimit,
    BigDecimal approvedCreditLimit,
    PaymentTerms paymentTerms,
    VolumeDiscountTier volumeDiscountTier,
    
    // Service requirements
    String slaRequirements,
    CommunicationMethod preferredCommunicationMethod,
    
    // Consent and agreements
    Boolean marketingConsent,
    Boolean termsAccepted,
    Boolean privacyPolicyAccepted,
    Boolean dataProcessingAgreementAccepted,
    
    // Status timestamps
    LocalDateTime submittedAt,
    LocalDateTime approvedAt,
    String approvedBy,
    LocalDateTime rejectedAt,
    String rejectedBy,
    String rejectionReason,
    
    // Audit fields
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String updatedBy,
    Long version
) {
    
    /**
     * Check if application is in final state.
     */
    public boolean isFinalState() {
        return applicationStatus != null && applicationStatus.isCompleted();
    }
    
    /**
     * Check if application requires customer action.
     */
    public boolean requiresCustomerAction() {
        return applicationStatus != null && applicationStatus.requiresCustomerAction();
    }
    
    /**
     * Check if application requires admin action.
     */
    public boolean requiresAdminAction() {
        return applicationStatus != null && applicationStatus.requiresAdminAction();
    }
    
    /**
     * Get processing status description.
     */
    public String getProcessingStatusDescription() {
        if (applicationStatus == null) {
            return "Unknown status";
        }
        return applicationStatus.getDescription();
    }
    
    /**
     * Check if billing contact is provided.
     */
    public boolean hasBillingContact() {
        return billingContactFirstName != null && billingContactLastName != null && 
               billingContactEmail != null;
    }
    
    /**
     * Get expected processing time for current status.
     */
    public String getExpectedProcessingTime() {
        return applicationStatus != null ? applicationStatus.getExpectedProcessingTime() : "N/A";
    }
}