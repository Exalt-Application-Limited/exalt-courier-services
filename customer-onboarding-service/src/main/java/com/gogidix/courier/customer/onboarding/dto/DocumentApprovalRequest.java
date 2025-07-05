package com.gogidix.courier.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for document approval operations.
 * 
 * @param reviewedBy Username of the person approving the document
 * @param approvalNotes Notes explaining the approval decision
 * @param confidenceScore Confidence score assigned by the reviewer (0.0 - 1.0)
 * @param extractedData JSON string of data extracted from the document
 * @param expiryDate Document expiry date if applicable (ISO format: yyyy-MM-dd)
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentApprovalRequest(
    @NotBlank(message = "Reviewed by is required")
    @Size(max = 100, message = "Reviewed by must not exceed 100 characters")
    String reviewedBy,
    
    @Size(max = 1000, message = "Approval notes must not exceed 1000 characters")
    String approvalNotes,
    
    Double confidenceScore,
    
    String extractedData,
    
    String expiryDate
) {
    
    /**
     * Creates an approval request with basic information.
     */
    public static DocumentApprovalRequest basic(String reviewedBy, String notes) {
        return new DocumentApprovalRequest(reviewedBy, notes, null, null, null);
    }
    
    /**
     * Creates an approval request with confidence score.
     */
    public static DocumentApprovalRequest withScore(String reviewedBy, String notes, Double score) {
        return new DocumentApprovalRequest(reviewedBy, notes, score, null, null);
    }
}