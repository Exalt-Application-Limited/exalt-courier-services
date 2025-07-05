package com.gogidix.courier.customer.onboarding.service;

import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentVerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Document Verification operations.
 * 
 * This service handles the complete KYC document verification workflow including:
 * - Document upload and storage
 * - AI-powered document verification
 * - Manual review processes
 * - Integration with shared infrastructure services
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public interface DocumentVerificationService {

    /**
     * Uploads a verification document for a customer application.
     * 
     * @param applicationReferenceId The customer application reference ID
     * @param documentType The type of document being uploaded
     * @param file The document file
     * @param isPrimary Whether this is the primary document for this type
     * @return Document upload response with verification details
     */
    DocumentUploadResponse uploadDocument(String applicationReferenceId, 
                                        DocumentType documentType, 
                                        MultipartFile file, 
                                        Boolean isPrimary);

    /**
     * Gets all documents for a specific application.
     * 
     * @param applicationReferenceId The customer application reference ID
     * @return List of verification document responses
     */
    List<VerificationDocumentResponse> getApplicationDocuments(String applicationReferenceId);

    /**
     * Gets a specific document by its reference ID.
     * 
     * @param documentReferenceId The document reference ID
     * @return Verification document response
     */
    VerificationDocumentResponse getDocument(String documentReferenceId);

    /**
     * Downloads a document file.
     * 
     * @param documentReferenceId The document reference ID
     * @return Document download response with file data
     */
    DocumentDownloadResponse downloadDocument(String documentReferenceId);

    /**
     * Initiates AI verification for a document.
     * 
     * @param documentReferenceId The document reference ID
     * @return AI verification response
     */
    DocumentAIVerificationResponse initiateAIVerification(String documentReferenceId);

    /**
     * Submits a document for manual review.
     * 
     * @param documentReferenceId The document reference ID
     * @param reviewNotes Optional notes for the manual review
     * @return Manual review submission response
     */
    ManualReviewResponse submitForManualReview(String documentReferenceId, String reviewNotes);

    /**
     * Approves a document after verification.
     * 
     * @param documentReferenceId The document reference ID
     * @param request Document approval request with reviewer details
     */
    void approveDocument(String documentReferenceId, DocumentApprovalRequest request);

    /**
     * Rejects a document after verification.
     * 
     * @param documentReferenceId The document reference ID
     * @param request Document rejection request with reason
     */
    void rejectDocument(String documentReferenceId, DocumentRejectionRequest request);

    /**
     * Requests document resubmission.
     * 
     * @param documentReferenceId The document reference ID
     * @param request Resubmission request with feedback
     */
    void requestResubmission(String documentReferenceId, DocumentResubmissionRequest request);

    /**
     * Gets documents pending manual review.
     * 
     * @param pageable Pagination information
     * @return Page of documents requiring manual review
     */
    Page<VerificationDocumentResponse> getDocumentsPendingReview(Pageable pageable);

    /**
     * Gets overdue documents (pending too long).
     * 
     * @param maxDaysWaiting Maximum days a document should wait
     * @param pageable Pagination information
     * @return Page of overdue documents
     */
    Page<VerificationDocumentResponse> getOverdueDocuments(int maxDaysWaiting, Pageable pageable);

    /**
     * Checks if an application has all required documents approved.
     * 
     * @param applicationReferenceId The customer application reference ID
     * @return Document completion status
     */
    DocumentCompletionStatus checkDocumentCompletionStatus(String applicationReferenceId);

    /**
     * Gets document verification statistics.
     * 
     * @return Document verification statistics
     */
    DocumentVerificationStatistics getVerificationStatistics();

    /**
     * Searches documents by multiple criteria.
     * 
     * @param searchRequest Document search criteria
     * @param pageable Pagination information
     * @return Page of matching documents
     */
    Page<VerificationDocumentResponse> searchDocuments(DocumentSearchRequest searchRequest, Pageable pageable);

    /**
     * Validates document file before upload.
     * 
     * @param file The document file to validate
     * @param documentType The type of document
     * @return Validation result
     */
    DocumentValidationResult validateDocument(MultipartFile file, DocumentType documentType);

    /**
     * Extracts data from document using AI/OCR.
     * 
     * @param documentReferenceId The document reference ID
     * @return Extracted document data
     */
    DocumentExtractionResult extractDocumentData(String documentReferenceId);

    /**
     * Compares extracted data with application data for verification.
     * 
     * @param documentReferenceId The document reference ID
     * @return Data comparison result
     */
    DocumentDataComparisonResult compareWithApplicationData(String documentReferenceId);

    /**
     * Gets required document types for KYC completion.
     * 
     * @param customerSegment Customer segment (INDIVIDUAL, BUSINESS, etc.)
     * @return List of required document types
     */
    List<RequiredDocumentInfo> getRequiredDocuments(String customerSegment);

    /**
     * Deletes a document (admin only).
     * 
     * @param documentReferenceId The document reference ID
     * @param reason Reason for deletion
     */
    void deleteDocument(String documentReferenceId, String reason);

    /**
     * Updates document verification status.
     * 
     * @param document The document entity
     * @param newStatus The new verification status
     * @param notes Status change notes
     * @param changedBy User who changed the status
     */
    void updateDocumentStatus(CustomerVerificationDocument document,
                            DocumentVerificationStatus newStatus,
                            String notes,
                            String changedBy);

    /**
     * Generates document hash for duplicate detection.
     * 
     * @param fileBytes The document file bytes
     * @return SHA-256 hash of the document
     */
    String generateDocumentHash(byte[] fileBytes);

    /**
     * Checks for duplicate documents.
     * 
     * @param documentHash The document hash
     * @param applicationReferenceId The application reference ID
     * @return true if duplicate exists
     */
    boolean isDuplicateDocument(String documentHash, String applicationReferenceId);

    /**
     * Archives old documents for compliance.
     * 
     * @param maxAgeInDays Maximum age of documents to keep active
     * @return Number of documents archived
     */
    int archiveOldDocuments(int maxAgeInDays);
}