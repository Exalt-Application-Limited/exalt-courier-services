package com.gogidix.courier.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for document resubmission operations.
 * 
 * @param reviewedBy Username of the person requesting resubmission
 * @param resubmissionNotes Notes explaining what needs to be corrected
 * @param suggestedAction What the customer should do to fix the issue
 * @param priority Priority level for the resubmission (HIGH, MEDIUM, LOW)
 * @param dueDate Optional due date for resubmission (ISO format: yyyy-MM-dd)
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentResubmissionRequest(
    @NotBlank(message = "Reviewed by is required")
    @Size(max = 100, message = "Reviewed by must not exceed 100 characters")
    String reviewedBy,
    
    @Size(max = 1000, message = "Resubmission notes must not exceed 1000 characters")
    String resubmissionNotes,
    
    @Size(max = 500, message = "Suggested action must not exceed 500 characters")
    String suggestedAction,
    
    String priority,
    
    String dueDate
) {
    
    /**
     * Priority levels for resubmission requests.
     */
    public static class Priority {
        public static final String HIGH = "HIGH";
        public static final String MEDIUM = "MEDIUM";
        public static final String LOW = "LOW";
    }
    
    /**
     * Common resubmission scenarios.
     */
    public static class CommonRequests {
        public static final String BETTER_QUALITY = "Please upload a clearer image";
        public static final String FULL_DOCUMENT = "Please ensure the entire document is visible";
        public static final String CORRECT_TYPE = "Please upload the correct document type";
        public static final String CURRENT_DOCUMENT = "Please upload a current, unexpired document";
        public static final String LEGIBLE_TEXT = "Please ensure all text is clearly readable";
    }
    
    /**
     * Creates a resubmission request for image quality issues.
     */
    public static DocumentResubmissionRequest qualityIssue(String reviewedBy) {
        return new DocumentResubmissionRequest(
            reviewedBy,
            "The uploaded document image quality needs improvement for verification.",
            CommonRequests.BETTER_QUALITY,
            Priority.MEDIUM,
            null
        );
    }
    
    /**
     * Creates a resubmission request for incomplete document.
     */
    public static DocumentResubmissionRequest incompleteDocument(String reviewedBy) {
        return new DocumentResubmissionRequest(
            reviewedBy,
            "The document appears to be incomplete or partially cut off.",
            CommonRequests.FULL_DOCUMENT,
            Priority.HIGH,
            null
        );
    }
    
    /**
     * Creates a resubmission request for wrong document type.
     */
    public static DocumentResubmissionRequest wrongType(String reviewedBy, String expectedType) {
        return new DocumentResubmissionRequest(
            reviewedBy,
            "The uploaded document does not match the required type.",
            "Please upload a " + expectedType + " as specified",
            Priority.HIGH,
            null
        );
    }
}