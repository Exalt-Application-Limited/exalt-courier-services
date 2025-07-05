package com.gogidix.courier.customer.onboarding.dto;

import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentVerificationStatus;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for document search operations.
 * 
 * @param applicationReferenceId Filter by application reference ID
 * @param documentTypes Filter by document types
 * @param verificationStatuses Filter by verification statuses
 * @param uploadedBy Filter by who uploaded the document
 * @param reviewedBy Filter by who reviewed the document
 * @param uploadedAfter Filter by upload date (after this date)
 * @param uploadedBefore Filter by upload date (before this date)
 * @param reviewedAfter Filter by review date (after this date)
 * @param reviewedBefore Filter by review date (before this date)
 * @param fileName Filter by filename (partial match)
 * @param minConfidenceScore Minimum confidence score
 * @param maxConfidenceScore Maximum confidence score
 * @param isPrimary Filter by primary document flag
 * @param allowResubmission Filter by resubmission allowed flag
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentSearchRequest(
    @Size(max = 50, message = "Application reference ID must not exceed 50 characters")
    String applicationReferenceId,
    
    List<DocumentType> documentTypes,
    
    List<DocumentVerificationStatus> verificationStatuses,
    
    @Size(max = 100, message = "Uploaded by must not exceed 100 characters")
    String uploadedBy,
    
    @Size(max = 100, message = "Reviewed by must not exceed 100 characters")
    String reviewedBy,
    
    LocalDateTime uploadedAfter,
    
    LocalDateTime uploadedBefore,
    
    LocalDateTime reviewedAfter,
    
    LocalDateTime reviewedBefore,
    
    @Size(max = 255, message = "File name must not exceed 255 characters")
    String fileName,
    
    Double minConfidenceScore,
    
    Double maxConfidenceScore,
    
    Boolean isPrimary,
    
    Boolean allowResubmission
) {
    
    /**
     * Check if search has any filters applied.
     */
    public Boolean hasFilters() {
        return applicationReferenceId != null ||
               (documentTypes != null && !documentTypes.isEmpty()) ||
               (verificationStatuses != null && !verificationStatuses.isEmpty()) ||
               uploadedBy != null ||
               reviewedBy != null ||
               uploadedAfter != null ||
               uploadedBefore != null ||
               reviewedAfter != null ||
               reviewedBefore != null ||
               fileName != null ||
               minConfidenceScore != null ||
               maxConfidenceScore != null ||
               isPrimary != null ||
               allowResubmission != null;
    }
    
    /**
     * Check if date range is valid.
     */
    public Boolean isDateRangeValid() {
        if (uploadedAfter != null && uploadedBefore != null) {
            return uploadedAfter.isBefore(uploadedBefore);
        }
        if (reviewedAfter != null && reviewedBefore != null) {
            return reviewedAfter.isBefore(reviewedBefore);
        }
        return true;
    }
    
    /**
     * Check if confidence score range is valid.
     */
    public Boolean isConfidenceRangeValid() {
        if (minConfidenceScore != null && maxConfidenceScore != null) {
            return minConfidenceScore <= maxConfidenceScore &&
                   minConfidenceScore >= 0.0 && minConfidenceScore <= 1.0 &&
                   maxConfidenceScore >= 0.0 && maxConfidenceScore <= 1.0;
        }
        return true;
    }
    
    /**
     * Creates a search request for pending documents.
     */
    public static DocumentSearchRequest pendingDocuments() {
        return new DocumentSearchRequest(
            null,
            null,
            List.of(DocumentVerificationStatus.PENDING, DocumentVerificationStatus.MANUAL_REVIEW),
            null, null, null, null, null, null, null, null, null, null, null
        );
    }
    
    /**
     * Creates a search request for documents by application.
     */
    public static DocumentSearchRequest byApplication(String applicationReferenceId) {
        return new DocumentSearchRequest(
            applicationReferenceId,
            null, null, null, null, null, null, null, null, null, null, null, null, null
        );
    }
}