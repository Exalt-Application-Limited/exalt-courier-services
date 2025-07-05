package com.gogidix.courier.customer.onboarding.repository;

import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentVerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for CustomerVerificationDocument entity.
 * 
 * Provides data access operations for customer verification documents
 * used in the KYC process during customer onboarding.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface CustomerVerificationDocumentRepository extends JpaRepository<CustomerVerificationDocument, UUID> {

    /**
     * Finds all documents for a specific application.
     * 
     * @param application The customer onboarding application
     * @return List of verification documents ordered by upload date
     */
    List<CustomerVerificationDocument> findByApplicationOrderByUploadedAtDesc(CustomerOnboardingApplication application);

    /**
     * Finds all documents for a specific application ID.
     * 
     * @param applicationId The application UUID
     * @return List of verification documents ordered by upload date
     */
    List<CustomerVerificationDocument> findByApplicationIdOrderByUploadedAtDesc(UUID applicationId);

    /**
     * Finds documents by application and document type.
     * 
     * @param application The customer onboarding application
     * @param documentType The type of document to filter by
     * @return List of documents of the specified type
     */
    List<CustomerVerificationDocument> findByApplicationAndDocumentType(
            CustomerOnboardingApplication application, 
            DocumentType documentType);

    /**
     * Finds documents by verification status.
     * 
     * @param status The verification status to filter by
     * @param pageable Pagination information
     * @return Page of documents with the specified status
     */
    Page<CustomerVerificationDocument> findByVerificationStatus(DocumentVerificationStatus status, Pageable pageable);

    /**
     * Finds the primary document for a specific type and application.
     * 
     * @param application The customer onboarding application
     * @param documentType The document type
     * @return Optional containing the primary document if found
     */
    Optional<CustomerVerificationDocument> findByApplicationAndDocumentTypeAndIsPrimaryTrue(
            CustomerOnboardingApplication application, 
            DocumentType documentType);

    /**
     * Finds documents by document reference ID.
     * 
     * @param documentReferenceId The document reference ID
     * @return Optional containing the document if found
     */
    Optional<CustomerVerificationDocument> findByDocumentReferenceId(String documentReferenceId);

    /**
     * Finds documents by document verification service ID.
     * 
     * @param documentVerificationServiceId The external verification service ID
     * @return Optional containing the document if found
     */
    Optional<CustomerVerificationDocument> findByDocumentVerificationServiceId(String documentVerificationServiceId);

    /**
     * Counts documents by application and status.
     * 
     * @param application The customer onboarding application
     * @param status The verification status
     * @return Number of documents with the specified status
     */
    long countByApplicationAndVerificationStatus(CustomerOnboardingApplication application, DocumentVerificationStatus status);

    /**
     * Finds documents uploaded within a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of documents uploaded within the date range
     */
    Page<CustomerVerificationDocument> findByUploadedAtBetween(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            Pageable pageable);

    /**
     * Finds documents that require manual review.
     * 
     * @param pageable Pagination information
     * @return Page of documents requiring manual review
     */
    @Query("SELECT d FROM CustomerVerificationDocument d WHERE " +
           "d.verificationStatus IN ('MANUAL_REVIEW_REQUIRED', 'UNDER_REVIEW', 'SUSPICIOUS') " +
           "ORDER BY d.uploadedAt ASC")
    Page<CustomerVerificationDocument> findDocumentsRequiringManualReview(Pageable pageable);

    /**
     * Finds documents pending verification for too long.
     * 
     * @param cutoffDate Documents uploaded before this date
     * @param pageable Pagination information
     * @return Page of overdue documents
     */
    @Query("SELECT d FROM CustomerVerificationDocument d WHERE " +
           "d.verificationStatus IN ('PENDING', 'UNDER_REVIEW', 'AI_PROCESSING') AND " +
           "d.uploadedAt < :cutoffDate " +
           "ORDER BY d.uploadedAt ASC")
    Page<CustomerVerificationDocument> findOverdueDocuments(
            @Param("cutoffDate") LocalDateTime cutoffDate,
            Pageable pageable);

    /**
     * Finds documents by multiple criteria for admin search.
     * 
     * @param documentType Document type (optional)
     * @param status Verification status (optional)
     * @param startDate Uploaded after this date (optional)
     * @param endDate Uploaded before this date (optional)
     * @param pageable Pagination information
     * @return Page of matching documents
     */
    @Query("SELECT d FROM CustomerVerificationDocument d WHERE " +
           "(:documentType IS NULL OR d.documentType = :documentType) AND " +
           "(:status IS NULL OR d.verificationStatus = :status) AND " +
           "(:startDate IS NULL OR d.uploadedAt >= :startDate) AND " +
           "(:endDate IS NULL OR d.uploadedAt <= :endDate) " +
           "ORDER BY d.uploadedAt DESC")
    Page<CustomerVerificationDocument> findByMultipleCriteria(
            @Param("documentType") DocumentType documentType,
            @Param("status") DocumentVerificationStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Finds all approved identity documents for an application.
     * 
     * @param application The customer onboarding application
     * @return List of approved identity documents
     */
    @Query("SELECT d FROM CustomerVerificationDocument d WHERE " +
           "d.application = :application AND " +
           "d.verificationStatus = 'APPROVED' AND " +
           "d.documentType IN ('GOVERNMENT_ID', 'PASSPORT', 'DRIVERS_LICENSE', 'BIRTH_CERTIFICATE')")
    List<CustomerVerificationDocument> findApprovedIdentityDocuments(@Param("application") CustomerOnboardingApplication application);

    /**
     * Finds all approved address proof documents for an application.
     * 
     * @param application The customer onboarding application
     * @return List of approved address proof documents
     */
    @Query("SELECT d FROM CustomerVerificationDocument d WHERE " +
           "d.application = :application AND " +
           "d.verificationStatus = 'APPROVED' AND " +
           "d.documentType IN ('UTILITY_BILL', 'BANK_STATEMENT', 'LEASE_AGREEMENT', 'MORTGAGE_STATEMENT', 'COUNCIL_TAX')")
    List<CustomerVerificationDocument> findApprovedAddressProofDocuments(@Param("application") CustomerOnboardingApplication application);

    /**
     * Checks if application has required documents for KYC completion.
     * 
     * @param application The customer onboarding application
     * @return true if all required documents are approved
     */
    @Query("SELECT CASE WHEN " +
           "(SELECT COUNT(d) FROM CustomerVerificationDocument d WHERE " +
           " d.application = :application AND d.verificationStatus = 'APPROVED' AND " +
           " d.documentType IN ('GOVERNMENT_ID', 'PASSPORT', 'DRIVERS_LICENSE')) >= 1 AND " +
           "(SELECT COUNT(d) FROM CustomerVerificationDocument d WHERE " +
           " d.application = :application AND d.verificationStatus = 'APPROVED' AND " +
           " d.documentType IN ('UTILITY_BILL', 'BANK_STATEMENT', 'LEASE_AGREEMENT', 'MORTGAGE_STATEMENT')) >= 1 " +
           "THEN true ELSE false END")
    boolean hasRequiredDocumentsApproved(@Param("application") CustomerOnboardingApplication application);

    /**
     * Gets verification statistics by document type.
     * 
     * @return List of [documentType, status, count] statistics
     */
    @Query("SELECT d.documentType, d.verificationStatus, COUNT(d) FROM CustomerVerificationDocument d " +
           "GROUP BY d.documentType, d.verificationStatus " +
           "ORDER BY d.documentType, d.verificationStatus")
    List<Object[]> getVerificationStatsByTypeAndStatus();

    /**
     * Gets processing time statistics for documents.
     * 
     * @return List of [documentType, average_processing_hours]
     */
    @Query("SELECT d.documentType, " +
           "AVG(EXTRACT(EPOCH FROM (d.verifiedAt - d.uploadedAt)) / 3600.0) " +
           "FROM CustomerVerificationDocument d " +
           "WHERE d.verifiedAt IS NOT NULL " +
           "GROUP BY d.documentType")
    List<Object[]> getAverageProcessingTimeByType();

    /**
     * Finds documents with low confidence scores that need review.
     * 
     * @param confidenceThreshold Minimum confidence score threshold
     * @param pageable Pagination information
     * @return Page of documents with low confidence scores
     */
    Page<CustomerVerificationDocument> findByConfidenceScoreLessThanAndVerificationStatusIn(
            Double confidenceThreshold,
            List<DocumentVerificationStatus> statuses,
            Pageable pageable);

    /**
     * Finds expired documents that need resubmission.
     * 
     * @param currentDate Current date for comparison
     * @param pageable Pagination information
     * @return Page of expired documents
     */
    Page<CustomerVerificationDocument> findByExpiresAtBeforeAndVerificationStatus(
            LocalDateTime currentDate,
            DocumentVerificationStatus status,
            Pageable pageable);

    /**
     * Deletes old rejected documents (for cleanup).
     * 
     * @param cutoffDate Delete documents older than this date
     * @return Number of deleted documents
     */
    @Query("DELETE FROM CustomerVerificationDocument d WHERE " +
           "d.verificationStatus IN ('REJECTED', 'EXPIRED') AND " +
           "d.uploadedAt < :cutoffDate")
    int deleteOldRejectedDocuments(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Finds documents by hash to detect duplicates.
     * 
     * @param documentHash The document hash
     * @return List of documents with the same hash
     */
    List<CustomerVerificationDocument> findByDocumentHash(String documentHash);

    /**
     * Counts total documents by verification status.
     * 
     * @param status The verification status
     * @return Number of documents with the specified status
     */
    long countByVerificationStatus(DocumentVerificationStatus status);
}