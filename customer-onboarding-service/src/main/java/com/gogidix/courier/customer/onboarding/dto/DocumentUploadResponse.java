package com.gogidix.courier.customer.onboarding.dto;

import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentVerificationStatus;

import java.time.LocalDateTime;

/**
 * Response DTO for document upload operations.
 * 
 * @param documentReferenceId Unique reference ID for the uploaded document
 * @param documentType Type of document that was uploaded
 * @param fileName Original name of the uploaded file
 * @param fileSize Size of the uploaded file in bytes
 * @param mimeType MIME type of the uploaded file
 * @param verificationStatus Initial verification status
 * @param uploadedAt Timestamp when the document was uploaded
 * @param documentHash SHA-256 hash of the document for integrity
 * @param aiVerificationInitiated Whether AI verification was automatically started
 * @param estimatedProcessingTime Estimated time for verification completion
 * @param nextAction What the customer should do next
 * @param message Success or informational message
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public record DocumentUploadResponse(
    String documentReferenceId,
    DocumentType documentType,
    String fileName,
    Long fileSize,
    String mimeType,
    DocumentVerificationStatus verificationStatus,
    LocalDateTime uploadedAt,
    String documentHash,
    Boolean aiVerificationInitiated,
    String estimatedProcessingTime,
    String nextAction,
    String message
) {
    
    /**
     * Creates a successful upload response.
     */
    public static DocumentUploadResponse success(
            String documentReferenceId,
            DocumentType documentType,
            String fileName,
            Long fileSize,
            String mimeType,
            String documentHash,
            Boolean aiVerificationInitiated) {
        
        String nextAction = aiVerificationInitiated 
            ? "Document is being processed automatically. You will be notified of the results."
            : "Document uploaded successfully. Manual review will begin shortly.";
            
        String estimatedTime = aiVerificationInitiated 
            ? "2-5 minutes" 
            : "1-2 business days";
        
        return new DocumentUploadResponse(
            documentReferenceId,
            documentType,
            fileName,
            fileSize,
            mimeType,
            DocumentVerificationStatus.PENDING,
            LocalDateTime.now(),
            documentHash,
            aiVerificationInitiated,
            estimatedTime,
            nextAction,
            "Document uploaded successfully and verification has begun."
        );
    }
}