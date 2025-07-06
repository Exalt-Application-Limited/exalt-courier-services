package com.gogidix.courier.customer.onboarding.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for manual review submission operations.
 * 
 * @param documentReferenceId Reference ID of the document
 * @param submittedForReview Whether document was successfully submitted for manual review
 * @param reviewTicketId Unique ID for the manual review ticket
 * @param message Status message
 * @param submittedAt When the manual review was requested
 * @param estimatedReviewTime Expected time for manual review completion
 * @param reviewNotes Optional notes for the review team
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record ManualReviewResponse(
    String documentReferenceId,
    Boolean submittedForReview,
    String reviewTicketId,
    String message,
    LocalDateTime submittedAt,
    String estimatedReviewTime,
    String reviewNotes
) {
    
    /**
     * Creates a successful manual review submission response.
     */
    public static ManualReviewResponse success(String documentReferenceId, String reviewTicketId) {
        return new ManualReviewResponse(
            documentReferenceId,
            true,
            reviewTicketId,
            "Document submitted for manual review successfully",
            LocalDateTime.now(),
            "1-2 business days",
            null
        );
    }
    
    /**
     * Creates a failed manual review submission response.
     */
    public static ManualReviewResponse failed(String documentReferenceId, String reason) {
        return new ManualReviewResponse(
            documentReferenceId,
            false,
            null,
            "Failed to submit for manual review: " + reason,
            LocalDateTime.now(),
            null,
            null
        );
    }
}