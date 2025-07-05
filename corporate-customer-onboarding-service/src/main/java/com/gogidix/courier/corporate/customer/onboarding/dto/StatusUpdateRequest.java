package com.gogidix.courier.corporate.customer.onboarding.dto;

import com.gogidix.courier.corporate.customer.onboarding.enums.CorporateOnboardingStatus;
import jakarta.validation.constraints.*;

/**
 * Request DTO for updating application status.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record StatusUpdateRequest(
    @NotNull(message = "New status is required")
    CorporateOnboardingStatus newStatus,
    
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    String reason,
    
    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    String notes
) {
    
    /**
     * Check if this is an approval status update.
     */
    public boolean isApproval() {
        return newStatus == CorporateOnboardingStatus.APPROVED ||
               newStatus == CorporateOnboardingStatus.KYB_APPROVED ||
               newStatus == CorporateOnboardingStatus.CREDIT_CHECK_APPROVED ||
               newStatus == CorporateOnboardingStatus.CONTRACT_SIGNED;
    }
    
    /**
     * Check if this is a rejection status update.
     */
    public boolean isRejection() {
        return newStatus == CorporateOnboardingStatus.REJECTED ||
               newStatus == CorporateOnboardingStatus.KYB_FAILED ||
               newStatus == CorporateOnboardingStatus.CREDIT_CHECK_FAILED;
    }
    
    /**
     * Check if this status requires additional documentation.
     */
    public boolean requiresDocuments() {
        return newStatus == CorporateOnboardingStatus.DOCUMENTS_REQUIRED;
    }
    
    /**
     * Check if this is a suspension status update.
     */
    public boolean isSuspension() {
        return newStatus == CorporateOnboardingStatus.SUSPENDED;
    }
}