package com.gogidix.courier.customer.onboarding.dto;

import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;

import java.util.List;

/**
 * Information about a required document type for KYC completion.
 * 
 * @param documentType The type of document required
 * @param displayName Human-readable name for the document
 * @param description Detailed description of the document
 * @param isRequired Whether this document is mandatory
 * @param isOptional Whether this document is optional
 * @param acceptedFormats List of accepted file formats/MIME types
 * @param maxFileSize Maximum allowed file size
 * @param instructions Instructions for the customer
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record RequiredDocumentInfo(
    DocumentType documentType,
    String displayName,
    String description,
    Boolean isRequired,
    Boolean isOptional,
    List<String> acceptedFormats,
    String maxFileSize,
    String instructions
) {
    
    /**
     * Get formatted accepted formats for display.
     */
    public String getFormattedAcceptedFormats() {
        if (acceptedFormats == null || acceptedFormats.isEmpty()) {
            return "Any format";
        }
        
        return acceptedFormats.stream()
            .map(format -> {
                // Convert MIME types to user-friendly format names
                return switch (format) {
                    case "image/jpeg" -> "JPEG";
                    case "image/png" -> "PNG";
                    case "image/gif" -> "GIF";
                    case "application/pdf" -> "PDF";
                    default -> format.toUpperCase();
                };
            })
            .reduce((a, b) -> a + ", " + b)
            .orElse("Any format");
    }
    
    /**
     * Check if a MIME type is accepted.
     */
    public Boolean isFormatAccepted(String mimeType) {
        return acceptedFormats == null || acceptedFormats.isEmpty() || 
               acceptedFormats.contains(mimeType);
    }
    
    /**
     * Get priority level (required documents have higher priority).
     */
    public String getPriority() {
        if (Boolean.TRUE.equals(isRequired)) {
            return "HIGH";
        } else if (Boolean.TRUE.equals(isOptional)) {
            return "LOW";
        } else {
            return "MEDIUM";
        }
    }
    
    /**
     * Creates required document info for National ID.
     */
    public static RequiredDocumentInfo nationalId() {
        return new RequiredDocumentInfo(
            DocumentType.NATIONAL_ID,
            "National ID Card",
            "Valid government-issued national identification card",
            true,
            false,
            List.of("image/jpeg", "image/png", "application/pdf"),
            "10MB",
            "Upload a clear photo of both sides of your national ID card"
        );
    }
    
    /**
     * Creates required document info for Passport.
     */
    public static RequiredDocumentInfo passport() {
        return new RequiredDocumentInfo(
            DocumentType.PASSPORT,
            "Passport",
            "Valid passport (alternative to National ID)",
            false,
            true,
            List.of("image/jpeg", "image/png", "application/pdf"),
            "10MB",
            "Upload a clear photo of your passport information page"
        );
    }
    
    /**
     * Creates required document info for Proof of Address.
     */
    public static RequiredDocumentInfo proofOfAddress() {
        return new RequiredDocumentInfo(
            DocumentType.PROOF_OF_ADDRESS,
            "Proof of Address",
            "Recent utility bill or bank statement showing your address",
            true,
            false,
            List.of("image/jpeg", "image/png", "application/pdf"),
            "10MB",
            "Upload a utility bill or bank statement not older than 3 months"
        );
    }
}