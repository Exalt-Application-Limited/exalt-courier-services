package com.gogidix.courier.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for document rejection operations.
 * 
 * @param reviewedBy Username of the person rejecting the document
 * @param rejectionReason Primary reason for rejection
 * @param rejectionNotes Detailed notes explaining the rejection
 * @param suggestedAction What the customer should do to fix the issue
 * @param allowResubmission Whether the customer can resubmit this document type
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentRejectionRequest(
    @NotBlank(message = "Reviewed by is required")
    @Size(max = 100, message = "Reviewed by must not exceed 100 characters")
    String reviewedBy,
    
    @NotBlank(message = "Rejection reason is required")
    @Size(max = 200, message = "Rejection reason must not exceed 200 characters")
    String rejectionReason,
    
    @Size(max = 1000, message = "Rejection notes must not exceed 1000 characters")
    String rejectionNotes,
    
    @Size(max = 500, message = "Suggested action must not exceed 500 characters")
    String suggestedAction,
    
    Boolean allowResubmission
) {
    
    /**
     * Common rejection reasons.
     */
    public static class RejectionReasons {
        public static final String BLURRY_IMAGE = "Image is blurry or unclear";
        public static final String EXPIRED_DOCUMENT = "Document has expired";
        public static final String WRONG_DOCUMENT_TYPE = "Incorrect document type uploaded";
        public static final String INCOMPLETE_DOCUMENT = "Document appears to be incomplete";
        public static final String SUSPICIOUS_DOCUMENT = "Document appears to be altered or fraudulent";
        public static final String POOR_QUALITY = "Image quality is too poor for verification";
        public static final String WRONG_PERSON = "Document does not match application details";
        public static final String UNREADABLE_TEXT = "Text in document is not readable";
    }
    
    /**
     * Creates a rejection request for poor image quality.
     */
    public static DocumentRejectionRequest poorQuality(String reviewedBy) {
        return new DocumentRejectionRequest(
            reviewedBy,
            RejectionReasons.POOR_QUALITY,
            "The uploaded image quality is insufficient for verification. Please ensure the document is well-lit, in focus, and all text is clearly readable.",
            "Take a new photo in good lighting with all document details clearly visible",
            true
        );
    }
    
    /**
     * Creates a rejection request for expired document.
     */
    public static DocumentRejectionRequest expiredDocument(String reviewedBy) {
        return new DocumentRejectionRequest(
            reviewedBy,
            RejectionReasons.EXPIRED_DOCUMENT,
            "The document has expired and is no longer valid for verification purposes.",
            "Please upload a current, valid document of the same type",
            true
        );
    }
    
    /**
     * Creates a rejection request for wrong document type.
     */
    public static DocumentRejectionRequest wrongType(String reviewedBy, String expectedType) {
        return new DocumentRejectionRequest(
            reviewedBy,
            RejectionReasons.WRONG_DOCUMENT_TYPE,
            "The uploaded document does not match the required document type.",
            "Please upload a " + expectedType + " as specified",
            true
        );
    }
    
    /**
     * Creates a rejection request for suspicious document.
     */
    public static DocumentRejectionRequest suspicious(String reviewedBy) {
        return new DocumentRejectionRequest(
            reviewedBy,
            RejectionReasons.SUSPICIOUS_DOCUMENT,
            "The document appears to have been altered or may not be authentic.",
            "Please contact customer support for assistance with document verification",
            false
        );
    }
}