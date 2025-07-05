package com.gogidix.courier.customer.onboarding.service.impl;

import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentVerificationStatus;
import com.gogidix.courier.customer.onboarding.repository.CustomerOnboardingApplicationRepository;
import com.gogidix.courier.customer.onboarding.repository.CustomerVerificationDocumentRepository;
import com.gogidix.courier.customer.onboarding.service.DocumentVerificationService;
import com.gogidix.shared.exceptions.ResourceNotFoundException;
import com.gogidix.shared.exceptions.BusinessException;
import com.gogidix.shared.utilities.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of DocumentVerificationService for KYC document verification workflow.
 * 
 * Handles document upload, verification, approval/rejection, and management operations
 * for customer onboarding applications with comprehensive business logic.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DocumentVerificationServiceImpl implements DocumentVerificationService {

    private final CustomerVerificationDocumentRepository documentRepository;
    private final CustomerOnboardingApplicationRepository applicationRepository;

    @Value("${app.document.storage.path:/var/app/documents}")
    private String documentStoragePath;

    @Value("${app.document.max-file-size:10485760}") // 10MB default
    private Long maxFileSize;

    @Value("${app.document.allowed-types:image/jpeg,image/png,image/gif,application/pdf}")
    private String allowedMimeTypes;

    @Value("${app.ai.verification.enabled:true}")
    private Boolean aiVerificationEnabled;

    // ========== DOCUMENT UPLOAD AND MANAGEMENT ==========

    @Override
    public DocumentUploadResponse uploadDocument(String applicationReferenceId, 
                                               DocumentType documentType, 
                                               MultipartFile file, 
                                               Boolean isPrimary) {
        log.info("Starting document upload: application={}, type={}, file={}", 
                applicationReferenceId, documentType, file.getOriginalFilename());

        // Validate application exists
        CustomerOnboardingApplication application = applicationRepository
            .findByApplicationReferenceId(applicationReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Customer application not found: " + applicationReferenceId));

        // Validate file
        validateFile(file);

        // Check if document type already has approved document (unless allowing multiple)
        if (isPrimary && hasApprovedDocument(applicationReferenceId, documentType)) {
            throw new BusinessException("Document type " + documentType + 
                " already has an approved document. Please contact support to replace it.");
        }

        try {
            // Generate document reference ID
            String documentReferenceId = generateDocumentReferenceId(applicationReferenceId, documentType);

            // Calculate file hash for integrity
            String documentHash = calculateFileHash(file.getBytes());

            // Store file physically
            String storedFilePath = storeFile(file, documentReferenceId);

            // Create document entity
            CustomerVerificationDocument document = CustomerVerificationDocument.builder()
                .documentReferenceId(documentReferenceId)
                .customerApplication(application)
                .documentType(documentType)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .storedFilePath(storedFilePath)
                .documentHash(documentHash)
                .isPrimary(isPrimary)
                .verificationStatus(DocumentVerificationStatus.PENDING)
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(SecurityUtil.getCurrentUsername())
                .build();

            // Save document
            document = documentRepository.save(document);

            // Initiate AI verification if enabled
            Boolean aiInitiated = false;
            if (aiVerificationEnabled && isDocumentSuitableForAI(documentType, file.getContentType())) {
                try {
                    initiateAIVerificationAsync(document);
                    aiInitiated = true;
                    log.info("AI verification initiated for document: {}", documentReferenceId);
                } catch (Exception e) {
                    log.warn("Failed to initiate AI verification for document: {} - {}", 
                            documentReferenceId, e.getMessage());
                }
            }

            log.info("Document uploaded successfully: {}", documentReferenceId);

            return DocumentUploadResponse.success(
                documentReferenceId,
                documentType,
                file.getOriginalFilename(),
                file.getSize(),
                file.getContentType(),
                documentHash,
                aiInitiated
            );

        } catch (IOException e) {
            log.error("Failed to upload document: {}", e.getMessage(), e);
            throw new BusinessException("Failed to store document file: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during document upload: {}", e.getMessage(), e);
            throw new BusinessException("Document upload failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<VerificationDocumentResponse> getApplicationDocuments(String applicationReferenceId) {
        log.info("Retrieving documents for application: {}", applicationReferenceId);

        // Validate application exists
        if (!applicationRepository.existsByApplicationReferenceId(applicationReferenceId)) {
            throw new ResourceNotFoundException("Customer application not found: " + applicationReferenceId);
        }

        List<CustomerVerificationDocument> documents = documentRepository
            .findByCustomerApplication_ApplicationReferenceIdOrderByUploadedAtDesc(applicationReferenceId);

        return documents.stream()
            .map(this::mapToVerificationDocumentResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VerificationDocumentResponse getDocument(String documentReferenceId) {
        log.info("Retrieving document: {}", documentReferenceId);

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        return mapToVerificationDocumentResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentDownloadResponse downloadDocument(String documentReferenceId) {
        log.info("Downloading document: {}", documentReferenceId);

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        try {
            Path filePath = Paths.get(document.getStoredFilePath());
            if (!Files.exists(filePath)) {
                throw new BusinessException("Document file not found on storage: " + documentReferenceId);
            }

            byte[] fileContent = Files.readAllBytes(filePath);

            return new DocumentDownloadResponse(
                document.getFileName(),
                document.getMimeType(),
                fileContent,
                document.getFileSize()
            );

        } catch (IOException e) {
            log.error("Failed to read document file: {} - {}", documentReferenceId, e.getMessage(), e);
            throw new BusinessException("Failed to download document: " + e.getMessage());
        }
    }

    // ========== DOCUMENT VERIFICATION AND APPROVAL ==========

    @Override
    public void approveDocument(String documentReferenceId, DocumentApprovalRequest request) {
        log.info("Approving document: {} by {}", documentReferenceId, request.reviewedBy());

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        // Validate current status allows approval
        if (!canTransitionToStatus(document.getVerificationStatus(), DocumentVerificationStatus.APPROVED)) {
            throw new BusinessException("Document cannot be approved from current status: " + 
                document.getVerificationStatus());
        }

        // Update document status
        document.setVerificationStatus(DocumentVerificationStatus.APPROVED);
        document.setReviewedAt(LocalDateTime.now());
        document.setReviewedBy(request.reviewedBy());
        document.setReviewNotes(request.approvalNotes());
        document.setConfidenceScore(request.confidenceScore());
        document.setExtractionData(request.extractedData());

        if (request.expiryDate() != null) {
            document.setDocumentExpiryDate(LocalDateTime.parse(request.expiryDate() + "T00:00:00"));
        }

        documentRepository.save(document);

        // Check if this completes the KYC process for the application
        checkAndUpdateApplicationKYCStatus(document.getCustomerApplication().getApplicationReferenceId());

        log.info("Document approved successfully: {}", documentReferenceId);
    }

    @Override
    public void rejectDocument(String documentReferenceId, DocumentRejectionRequest request) {
        log.info("Rejecting document: {} by {} - reason: {}", 
                documentReferenceId, request.reviewedBy(), request.rejectionReason());

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        // Validate current status allows rejection
        if (!canTransitionToStatus(document.getVerificationStatus(), DocumentVerificationStatus.REJECTED)) {
            throw new BusinessException("Document cannot be rejected from current status: " + 
                document.getVerificationStatus());
        }

        // Update document status
        document.setVerificationStatus(DocumentVerificationStatus.REJECTED);
        document.setReviewedAt(LocalDateTime.now());
        document.setReviewedBy(request.reviewedBy());
        document.setReviewNotes(request.rejectionNotes());
        document.setRejectionReason(request.rejectionReason());
        document.setSuggestedAction(request.suggestedAction());
        document.setAllowResubmission(request.allowResubmission());

        documentRepository.save(document);

        // Update application status if needed
        updateApplicationStatusAfterRejection(document.getCustomerApplication().getApplicationReferenceId());

        log.info("Document rejected successfully: {}", documentReferenceId);
    }

    @Override
    public void requestResubmission(String documentReferenceId, DocumentResubmissionRequest request) {
        log.info("Requesting resubmission for document: {} by {}", documentReferenceId, request.reviewedBy());

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        // Update document status
        document.setVerificationStatus(DocumentVerificationStatus.RESUBMISSION_REQUIRED);
        document.setReviewedAt(LocalDateTime.now());
        document.setReviewedBy(request.reviewedBy());
        document.setReviewNotes(request.resubmissionNotes());
        document.setSuggestedAction(request.suggestedAction());
        document.setAllowResubmission(true);

        documentRepository.save(document);

        log.info("Resubmission requested for document: {}", documentReferenceId);
    }

    // ========== DOCUMENT COMPLETION AND STATUS ==========

    @Override
    @Transactional(readOnly = true)
    public DocumentCompletionStatus checkDocumentCompletionStatus(String applicationReferenceId) {
        log.info("Checking document completion status for application: {}", applicationReferenceId);

        // Validate application exists
        if (!applicationRepository.existsByApplicationReferenceId(applicationReferenceId)) {
            throw new ResourceNotFoundException("Customer application not found: " + applicationReferenceId);
        }

        // Get required documents for this customer segment (defaulting to INDIVIDUAL)
        List<DocumentCompletionStatus.RequiredDocumentInfo> requiredDocs = getRequiredDocuments("INDIVIDUAL");

        // Get current documents
        List<CustomerVerificationDocument> currentDocs = documentRepository
            .findByCustomerApplication_ApplicationReferenceIdOrderByUploadedAtDesc(applicationReferenceId);

        // Categorize documents by status
        Set<DocumentType> requiredTypes = requiredDocs.stream()
            .map(DocumentCompletionStatus.RequiredDocumentInfo::documentType)
            .collect(Collectors.toSet());

        List<DocumentType> completed = new ArrayList<>();
        List<DocumentType> pending = new ArrayList<>();
        List<DocumentType> rejected = new ArrayList<>();
        List<DocumentType> missing = new ArrayList<>(requiredTypes);

        for (CustomerVerificationDocument doc : currentDocs) {
            if (requiredTypes.contains(doc.getDocumentType())) {
                missing.remove(doc.getDocumentType());
                
                switch (doc.getVerificationStatus()) {
                    case APPROVED -> {
                        if (!completed.contains(doc.getDocumentType())) {
                            completed.add(doc.getDocumentType());
                        }
                    }
                    case PENDING, AI_VERIFICATION_IN_PROGRESS, MANUAL_REVIEW -> {
                        if (!completed.contains(doc.getDocumentType()) && !pending.contains(doc.getDocumentType())) {
                            pending.add(doc.getDocumentType());
                        }
                    }
                    case REJECTED, RESUBMISSION_REQUIRED -> {
                        if (!completed.contains(doc.getDocumentType()) && !rejected.contains(doc.getDocumentType())) {
                            rejected.add(doc.getDocumentType());
                        }
                    }
                }
            }
        }

        // Check if complete
        boolean isComplete = missing.isEmpty() && rejected.isEmpty() && pending.isEmpty() && 
                           completed.size() == requiredTypes.size();

        if (isComplete) {
            return DocumentCompletionStatus.complete(applicationReferenceId, requiredDocs, completed);
        } else {
            return DocumentCompletionStatus.incomplete(applicationReferenceId, requiredDocs, 
                completed, pending, rejected, missing);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentCompletionStatus.RequiredDocumentInfo> getRequiredDocuments(String customerSegment) {
        log.info("Getting required documents for segment: {}", customerSegment);

        // Define required documents based on customer segment
        List<DocumentCompletionStatus.RequiredDocumentInfo> requiredDocs = new ArrayList<>();

        if ("INDIVIDUAL".equalsIgnoreCase(customerSegment)) {
            requiredDocs.add(new DocumentCompletionStatus.RequiredDocumentInfo(
                DocumentType.NATIONAL_ID,
                "National ID Card",
                "Valid government-issued national identification card",
                true, false,
                List.of("image/jpeg", "image/png", "application/pdf"),
                "10MB",
                "Upload a clear photo of both sides of your national ID card"
            ));

            requiredDocs.add(new DocumentCompletionStatus.RequiredDocumentInfo(
                DocumentType.PASSPORT,
                "Passport",
                "Valid passport (alternative to National ID)",
                false, true,
                List.of("image/jpeg", "image/png", "application/pdf"),
                "10MB",
                "Upload a clear photo of your passport information page"
            ));

            requiredDocs.add(new DocumentCompletionStatus.RequiredDocumentInfo(
                DocumentType.PROOF_OF_ADDRESS,
                "Proof of Address",
                "Recent utility bill or bank statement showing your address",
                true, false,
                List.of("image/jpeg", "image/png", "application/pdf"),
                "10MB",
                "Upload a utility bill or bank statement not older than 3 months"
            ));

        } else if ("BUSINESS".equalsIgnoreCase(customerSegment)) {
            requiredDocs.add(new DocumentCompletionStatus.RequiredDocumentInfo(
                DocumentType.BUSINESS_REGISTRATION,
                "Business Registration Certificate",
                "Official business registration document",
                true, false,
                List.of("image/jpeg", "image/png", "application/pdf"),
                "10MB",
                "Upload your official business registration certificate"
            ));

            requiredDocs.add(new DocumentCompletionStatus.RequiredDocumentInfo(
                DocumentType.TAX_CERTIFICATE,
                "Tax Registration Certificate",
                "Business tax registration document",
                true, false,
                List.of("image/jpeg", "image/png", "application/pdf"),
                "10MB",
                "Upload your business tax registration certificate"
            ));
        }

        return requiredDocs;
    }

    // ========== ADMIN FUNCTIONS ==========

    @Override
    @Transactional(readOnly = true)
    public Page<VerificationDocumentResponse> getDocumentsPendingReview(Pageable pageable) {
        log.info("Getting documents pending review - page: {}, size: {}", 
                pageable.getPageNumber(), pageable.getPageSize());

        Page<CustomerVerificationDocument> documents = documentRepository
            .findByVerificationStatusInOrderByUploadedAtAsc(
                List.of(DocumentVerificationStatus.PENDING, DocumentVerificationStatus.MANUAL_REVIEW),
                pageable);

        return documents.map(this::mapToVerificationDocumentResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VerificationDocumentResponse> getOverdueDocuments(int maxDaysWaiting, Pageable pageable) {
        log.info("Getting overdue documents (max {} days) - page: {}, size: {}", 
                maxDaysWaiting, pageable.getPageNumber(), pageable.getPageSize());

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(maxDaysWaiting);
        
        Page<CustomerVerificationDocument> documents = documentRepository
            .findOverdueDocuments(cutoffDate, pageable);

        return documents.map(this::mapToVerificationDocumentResponse);
    }

    @Override
    public void deleteDocument(String documentReferenceId, String reason) {
        log.info("Deleting document: {} - reason: {}", documentReferenceId, reason);

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        try {
            // Delete physical file
            Path filePath = Paths.get(document.getStoredFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            // Delete database record
            documentRepository.delete(document);

            log.info("Document deleted successfully: {}", documentReferenceId);

        } catch (IOException e) {
            log.error("Failed to delete document file: {} - {}", documentReferenceId, e.getMessage(), e);
            throw new BusinessException("Failed to delete document file: " + e.getMessage());
        }
    }

    // ========== AI VERIFICATION METHODS ==========

    @Override
    public DocumentAIVerificationResponse initiateAIVerification(String documentReferenceId) {
        log.info("Initiating AI verification for document: {}", documentReferenceId);

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        if (!isDocumentSuitableForAI(document.getDocumentType(), document.getMimeType())) {
            throw new BusinessException("Document type not suitable for AI verification: " + 
                document.getDocumentType());
        }

        // Update status to AI verification in progress
        document.setVerificationStatus(DocumentVerificationStatus.AI_VERIFICATION_IN_PROGRESS);
        document.setAiVerificationStartedAt(LocalDateTime.now());
        documentRepository.save(document);

        // TODO: Integrate with actual AI service
        // For now, return a mock response
        return new DocumentAIVerificationResponse(
            documentReferenceId,
            true,
            "AI verification initiated successfully",
            LocalDateTime.now(),
            "2-5 minutes"
        );
    }

    @Override
    public DocumentExtractionResult extractDocumentData(String documentReferenceId) {
        log.info("Extracting data from document: {}", documentReferenceId);

        CustomerVerificationDocument document = documentRepository
            .findByDocumentReferenceId(documentReferenceId)
            .orElseThrow(() -> new ResourceNotFoundException("Document not found: " + documentReferenceId));

        // TODO: Integrate with actual OCR/AI extraction service
        // For now, return a mock response
        Map<String, Object> extractedData = new HashMap<>();
        extractedData.put("document_type", document.getDocumentType().name());
        extractedData.put("confidence_score", 0.95);
        extractedData.put("extraction_timestamp", LocalDateTime.now());

        return new DocumentExtractionResult(
            documentReferenceId,
            true,
            "Data extraction completed successfully",
            extractedData,
            0.95,
            LocalDateTime.now()
        );
    }

    // ========== PRIVATE HELPER METHODS ==========

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("File cannot be empty");
        }

        if (file.getSize() > maxFileSize) {
            throw new BusinessException("File size exceeds maximum allowed size of " + 
                (maxFileSize / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(allowedMimeTypes.split(",")).contains(contentType)) {
            throw new BusinessException("File type not allowed. Allowed types: " + allowedMimeTypes);
        }
    }

    private boolean hasApprovedDocument(String applicationReferenceId, DocumentType documentType) {
        return documentRepository.existsByCustomerApplication_ApplicationReferenceIdAndDocumentTypeAndVerificationStatus(
            applicationReferenceId, documentType, DocumentVerificationStatus.APPROVED);
    }

    private String generateDocumentReferenceId(String applicationReferenceId, DocumentType documentType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return String.format("DOC-%s-%s-%s", 
            applicationReferenceId.substring(0, Math.min(8, applicationReferenceId.length())),
            documentType.name().substring(0, Math.min(3, documentType.name().length())),
            timestamp.substring(timestamp.length() - 6));
    }

    private String calculateFileHash(byte[] fileBytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(fileBytes);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("Failed to calculate file hash: " + e.getMessage());
        }
    }

    private String storeFile(MultipartFile file, String documentReferenceId) throws IOException {
        // Create storage directory if it doesn't exist
        Path storageDir = Paths.get(documentStoragePath);
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }

        // Generate unique filename
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = documentReferenceId + "." + fileExtension;
        Path filePath = storageDir.resolve(fileName);

        // Store file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return "unknown";
    }

    private boolean isDocumentSuitableForAI(DocumentType documentType, String mimeType) {
        // Define which document types and MIME types are suitable for AI verification
        Set<DocumentType> aiSuitableTypes = Set.of(
            DocumentType.NATIONAL_ID,
            DocumentType.PASSPORT,
            DocumentType.DRIVERS_LICENSE,
            DocumentType.BUSINESS_REGISTRATION
        );

        Set<String> aiSuitableMimeTypes = Set.of(
            "image/jpeg",
            "image/png",
            "application/pdf"
        );

        return aiSuitableTypes.contains(documentType) && aiSuitableMimeTypes.contains(mimeType);
    }

    private void initiateAIVerificationAsync(CustomerVerificationDocument document) {
        // TODO: Implement async AI verification call
        // This would typically involve calling an external AI service
        log.info("AI verification would be initiated for document: {}", document.getDocumentReferenceId());
    }

    private boolean canTransitionToStatus(DocumentVerificationStatus currentStatus, 
                                        DocumentVerificationStatus targetStatus) {
        // Define valid status transitions
        Map<DocumentVerificationStatus, Set<DocumentVerificationStatus>> validTransitions = Map.of(
            DocumentVerificationStatus.PENDING, Set.of(
                DocumentVerificationStatus.AI_VERIFICATION_IN_PROGRESS,
                DocumentVerificationStatus.MANUAL_REVIEW,
                DocumentVerificationStatus.APPROVED,
                DocumentVerificationStatus.REJECTED
            ),
            DocumentVerificationStatus.AI_VERIFICATION_IN_PROGRESS, Set.of(
                DocumentVerificationStatus.AI_VERIFIED,
                DocumentVerificationStatus.AI_FAILED,
                DocumentVerificationStatus.MANUAL_REVIEW
            ),
            DocumentVerificationStatus.AI_VERIFIED, Set.of(
                DocumentVerificationStatus.APPROVED,
                DocumentVerificationStatus.MANUAL_REVIEW
            ),
            DocumentVerificationStatus.AI_FAILED, Set.of(
                DocumentVerificationStatus.MANUAL_REVIEW,
                DocumentVerificationStatus.REJECTED
            ),
            DocumentVerificationStatus.MANUAL_REVIEW, Set.of(
                DocumentVerificationStatus.APPROVED,
                DocumentVerificationStatus.REJECTED,
                DocumentVerificationStatus.RESUBMISSION_REQUIRED
            ),
            DocumentVerificationStatus.RESUBMISSION_REQUIRED, Set.of(
                DocumentVerificationStatus.PENDING,
                DocumentVerificationStatus.REJECTED
            )
        );

        return validTransitions.getOrDefault(currentStatus, Set.of()).contains(targetStatus);
    }

    private void checkAndUpdateApplicationKYCStatus(String applicationReferenceId) {
        // Check if all required documents are approved
        DocumentCompletionStatus completionStatus = checkDocumentCompletionStatus(applicationReferenceId);
        
        if (completionStatus.isComplete()) {
            // Update application status to indicate KYC completion
            applicationRepository.findByApplicationReferenceId(applicationReferenceId)
                .ifPresent(application -> {
                    // TODO: Update application status to KYC_COMPLETED
                    log.info("KYC process completed for application: {}", applicationReferenceId);
                });
        }
    }

    private void updateApplicationStatusAfterRejection(String applicationReferenceId) {
        // TODO: Implement logic to update application status after document rejection
        log.info("Updating application status after document rejection: {}", applicationReferenceId);
    }

    private VerificationDocumentResponse mapToVerificationDocumentResponse(CustomerVerificationDocument document) {
        return new VerificationDocumentResponse(
            document.getDocumentReferenceId(),
            document.getDocumentType(),
            document.getFileName(),
            document.getFileSize(),
            document.getMimeType(),
            document.getVerificationStatus(),
            document.getUploadedAt(),
            document.getUploadedBy(),
            document.getReviewedAt(),
            document.getReviewedBy(),
            document.getReviewNotes(),
            document.getRejectionReason(),
            document.getSuggestedAction(),
            document.getConfidenceScore(),
            document.getDocumentExpiryDate(),
            document.getIsPrimary(),
            document.getAllowResubmission()
        );
    }

    // ========== NOT YET IMPLEMENTED METHODS ==========

    @Override
    public ManualReviewResponse submitForManualReview(String documentReferenceId, String reviewNotes) {
        // TODO: Implement manual review submission
        throw new BusinessException("Manual review submission not yet implemented");
    }

    @Override
    public DocumentVerificationStatistics getVerificationStatistics() {
        // TODO: Implement statistics gathering
        throw new BusinessException("Statistics feature not yet implemented");
    }

    @Override
    public Page<VerificationDocumentResponse> searchDocuments(DocumentSearchRequest searchRequest, Pageable pageable) {
        // TODO: Implement document search
        throw new BusinessException("Document search not yet implemented");
    }
}