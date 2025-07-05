package com.gogidix.courier.customer.onboarding.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for document data extraction operations.
 * 
 * @param documentReferenceId Reference ID of the document
 * @param extractionSuccessful Whether data extraction was successful
 * @param message Status or error message
 * @param extractedData Map of extracted data fields
 * @param confidenceScore Overall confidence score for extraction (0.0 - 1.0)
 * @param extractedAt When the extraction was performed
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentExtractionResult(
    String documentReferenceId,
    Boolean extractionSuccessful,
    String message,
    Map<String, Object> extractedData,
    Double confidenceScore,
    LocalDateTime extractedAt
) {
    
    /**
     * Creates a successful extraction result.
     */
    public static DocumentExtractionResult success(
            String documentReferenceId,
            Map<String, Object> extractedData,
            Double confidenceScore) {
        return new DocumentExtractionResult(
            documentReferenceId,
            true,
            "Data extraction completed successfully",
            extractedData,
            confidenceScore,
            LocalDateTime.now()
        );
    }
    
    /**
     * Creates a failed extraction result.
     */
    public static DocumentExtractionResult failed(String documentReferenceId, String reason) {
        return new DocumentExtractionResult(
            documentReferenceId,
            false,
            "Data extraction failed: " + reason,
            Map.of(),
            0.0,
            LocalDateTime.now()
        );
    }
    
    /**
     * Get a specific extracted field value.
     */
    public Object getExtractedField(String fieldName) {
        return extractedData != null ? extractedData.get(fieldName) : null;
    }
    
    /**
     * Get extracted field as string.
     */
    public String getExtractedFieldAsString(String fieldName) {
        Object value = getExtractedField(fieldName);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Check if extraction has high confidence.
     */
    public Boolean hasHighConfidence() {
        return confidenceScore != null && confidenceScore >= 0.8;
    }
    
    /**
     * Check if manual review is recommended.
     */
    public Boolean requiresManualReview() {
        return confidenceScore == null || confidenceScore < 0.7;
    }
}