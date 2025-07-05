package com.gogidix.courier.customer.onboarding.dto;

import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentVerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for document verification operations.
 * 
 * @param documentReferenceId Unique reference ID for the document
 * @param documentType Type of document
 * @param fileName Original filename
 * @param fileSize Size of the file in bytes
 * @param mimeType MIME type of the file
 * @param verificationStatus Current verification status
 * @param uploadedAt When the document was uploaded
 * @param uploadedBy Who uploaded the document
 * @param reviewedAt When the document was reviewed (if applicable)
 * @param reviewedBy Who reviewed the document (if applicable)
 * @param reviewNotes Notes from the reviewer
 * @param rejectionReason Reason for rejection (if rejected)
 * @param suggestedAction Suggested action for the customer
 * @param confidenceScore AI/reviewer confidence score (0.0 - 1.0)
 * @param documentExpiryDate Document expiry date (if applicable)
 * @param isPrimary Whether this is the primary document for this type
 * @param allowResubmission Whether resubmission is allowed
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Verification document information")
public record VerificationDocumentResponse(
    @Schema(description = "Document reference ID", example = "DOC-REF-2025-001234")
    String documentReferenceId,
    
    @Schema(description = "Document type")
    DocumentType documentType,
    
    @Schema(description = "Original file name", example = "passport.jpg")
    String fileName,
    
    @Schema(description = "File size in bytes", example = "2048000")
    Long fileSize,
    
    @Schema(description = "File MIME type", example = "image/jpeg")
    String mimeType,
    
    @Schema(description = "Verification status")
    DocumentVerificationStatus verificationStatus,
    
    @Schema(description = "When document was uploaded")
    LocalDateTime uploadedAt,
    
    @Schema(description = "Who uploaded the document", example = "customer")
    String uploadedBy,
    
    @Schema(description = "When document was reviewed")
    LocalDateTime reviewedAt,
    
    @Schema(description = "Who reviewed the document", example = "admin")
    String reviewedBy,
    
    @Schema(description = "Review notes", example = "Document is clear and valid")
    String reviewNotes,
    
    @Schema(description = "Rejection reason (if rejected)", example = "Document expired")
    String rejectionReason,
    
    @Schema(description = "Suggested action for customer", example = "Please upload a current ID")
    String suggestedAction,
    
    @Schema(description = "AI/reviewer confidence score", example = "0.95")
    Double confidenceScore,
    
    @Schema(description = "Document expiry date (if applicable)")
    LocalDateTime documentExpiryDate,
    
    @Schema(description = "Whether this is the primary document for this type", example = "true")
    Boolean isPrimary,
    
    @Schema(description = "Whether resubmission is allowed", example = "true")
    Boolean allowResubmission
) {
    
    /**
     * Get display-friendly status text.
     */
    public String getStatusDisplayText() {
        return switch (verificationStatus) {
            case PENDING -> "Pending Review";
            case AI_VERIFICATION_IN_PROGRESS -> "AI Verification in Progress";
            case AI_VERIFIED -> "AI Verified";
            case AI_FAILED -> "AI Verification Failed";
            case MANUAL_REVIEW -> "Under Manual Review";
            case APPROVED -> "Approved";
            case REJECTED -> "Rejected";
            case RESUBMISSION_REQUIRED -> "Resubmission Required";
            case EXPIRED -> "Expired";
            case ARCHIVED -> "Archived";
            case FLAGGED_FOR_REVIEW -> "Flagged for Review";
            case QUALITY_CHECK_FAILED -> "Quality Check Failed";
        };
    }
    
    /**
     * Check if the document is in a final state.
     */
    public Boolean isFinalStatus() {
        return verificationStatus == DocumentVerificationStatus.APPROVED ||
               verificationStatus == DocumentVerificationStatus.REJECTED ||
               verificationStatus == DocumentVerificationStatus.EXPIRED ||
               verificationStatus == DocumentVerificationStatus.ARCHIVED;
    }
    
    /**
     * Check if the document requires customer action.
     */
    public Boolean requiresCustomerAction() {
        return verificationStatus == DocumentVerificationStatus.RESUBMISSION_REQUIRED ||
               verificationStatus == DocumentVerificationStatus.QUALITY_CHECK_FAILED;
    }
    
    /**
     * Format file size for display.
     */
    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown";
        
        if (fileSize < 1024) {
            return fileSize + " B";
        } else if (fileSize < 1024 * 1024) {
            return String.format("%.1f KB", fileSize / 1024.0);
        } else {
            return String.format("%.1f MB", fileSize / (1024.0 * 1024.0));
        }
    }
}