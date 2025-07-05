package com.gogidix.courier.customer.onboarding.controller;

import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.service.DocumentVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller for Document Verification operations.
 * 
 * Handles KYC document upload, verification, and management operations
 * for customer onboarding applications.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/customer/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Document Verification", description = "APIs for KYC document verification and management")
@SecurityRequirement(name = "bearerAuth")
public class DocumentVerificationController {

    private final DocumentVerificationService documentVerificationService;

    // ========== CUSTOMER ENDPOINTS ==========

    @Operation(summary = "Upload verification document", 
               description = "Upload a document for KYC verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Document uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file or document type"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "413", description = "File too large")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            @Parameter(description = "Customer application reference ID")
            @RequestParam @NotBlank String applicationReferenceId,
            
            @Parameter(description = "Type of document being uploaded")
            @RequestParam DocumentType documentType,
            
            @Parameter(description = "Document file to upload")
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Whether this is the primary document for this type")
            @RequestParam(defaultValue = "false") Boolean isPrimary) {
        
        log.info("Uploading document for application: {}, type: {}", applicationReferenceId, documentType);
        
        DocumentUploadResponse response = documentVerificationService.uploadDocument(
            applicationReferenceId, documentType, file, isPrimary);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get application documents", 
               description = "Retrieve all documents for a customer application")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Documents retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/application/{applicationReferenceId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<List<VerificationDocumentResponse>> getApplicationDocuments(
            @Parameter(description = "Customer application reference ID")
            @PathVariable String applicationReferenceId) {
        
        log.info("Retrieving documents for application: {}", applicationReferenceId);
        
        List<VerificationDocumentResponse> documents = documentVerificationService
            .getApplicationDocuments(applicationReferenceId);
        
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Get document details", 
               description = "Retrieve details of a specific document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document details retrieved"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{documentReferenceId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<VerificationDocumentResponse> getDocument(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId) {
        
        log.info("Retrieving document: {}", documentReferenceId);
        
        VerificationDocumentResponse document = documentVerificationService.getDocument(documentReferenceId);
        
        return ResponseEntity.ok(document);
    }

    @Operation(summary = "Download document file", 
               description = "Download the actual document file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document file downloaded"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/{documentReferenceId}/download")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<DocumentDownloadResponse> downloadDocument(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId) {
        
        log.info("Downloading document: {}", documentReferenceId);
        
        DocumentDownloadResponse response = documentVerificationService.downloadDocument(documentReferenceId);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Check document completion status", 
               description = "Check the completion status of required documents for KYC")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Completion status retrieved"),
        @ApiResponse(responseCode = "404", description = "Application not found"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/completion-status/{applicationReferenceId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<DocumentCompletionStatus> checkCompletionStatus(
            @Parameter(description = "Customer application reference ID")
            @PathVariable String applicationReferenceId) {
        
        log.info("Checking document completion status for application: {}", applicationReferenceId);
        
        DocumentCompletionStatus status = documentVerificationService
            .checkDocumentCompletionStatus(applicationReferenceId);
        
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Get required documents", 
               description = "Get list of required document types for KYC")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Required documents list retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid customer segment")
    })
    @GetMapping("/required")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<List<RequiredDocumentInfo>> getRequiredDocuments(
            @Parameter(description = "Customer segment (INDIVIDUAL, BUSINESS, etc.)")
            @RequestParam(defaultValue = "INDIVIDUAL") String customerSegment) {
        
        log.info("Getting required documents for segment: {}", customerSegment);
        
        List<RequiredDocumentInfo> requiredDocs = documentVerificationService
            .getRequiredDocuments(customerSegment);
        
        return ResponseEntity.ok(requiredDocs);
    }

    // ========== ADMIN ENDPOINTS ==========

    @Operation(summary = "Get documents pending review (Admin)", 
               description = "Retrieve documents that require manual review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending documents retrieved"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @GetMapping("/admin/pending-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Page<VerificationDocumentResponse>> getDocumentsPendingReview(
            Pageable pageable) {
        
        log.info("Getting documents pending review - page: {}, size: {}", 
                 pageable.getPageNumber(), pageable.getPageSize());
        
        Page<VerificationDocumentResponse> documents = documentVerificationService
            .getDocumentsPendingReview(pageable);
        
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Get overdue documents (Admin)", 
               description = "Retrieve documents that have been pending for too long")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Overdue documents retrieved"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @GetMapping("/admin/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Page<VerificationDocumentResponse>> getOverdueDocuments(
            @Parameter(description = "Maximum days a document should wait")
            @RequestParam(defaultValue = "3") int maxDaysWaiting,
            Pageable pageable) {
        
        log.info("Getting overdue documents (max {} days) - page: {}, size: {}", 
                 maxDaysWaiting, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<VerificationDocumentResponse> documents = documentVerificationService
            .getOverdueDocuments(maxDaysWaiting, pageable);
        
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Approve document (Admin)", 
               description = "Approve a document after verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document approved successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/{documentReferenceId}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Void> approveDocument(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId,
            @Valid @RequestBody DocumentApprovalRequest request) {
        
        log.info("Approving document: {} by {}", documentReferenceId, request.reviewedBy());
        
        documentVerificationService.approveDocument(documentReferenceId, request);
        
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Reject document (Admin)", 
               description = "Reject a document after verification")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document rejected successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/{documentReferenceId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Void> rejectDocument(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId,
            @Valid @RequestBody DocumentRejectionRequest request) {
        
        log.info("Rejecting document: {} by {} - reason: {}", 
                 documentReferenceId, request.reviewedBy(), request.rejectionReason());
        
        documentVerificationService.rejectDocument(documentReferenceId, request);
        
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Request document resubmission (Admin)", 
               description = "Request customer to resubmit document with corrections")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resubmission requested successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/{documentReferenceId}/request-resubmission")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Void> requestResubmission(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId,
            @Valid @RequestBody DocumentResubmissionRequest request) {
        
        log.info("Requesting resubmission for document: {} by {}", 
                 documentReferenceId, request.reviewedBy());
        
        documentVerificationService.requestResubmission(documentReferenceId, request);
        
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Submit document for manual review (Admin)", 
               description = "Submit a document for manual review by verification team")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document submitted for manual review"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/{documentReferenceId}/manual-review")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<ManualReviewResponse> submitForManualReview(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId,
            @Parameter(description = "Optional review notes")
            @RequestParam(required = false) String reviewNotes) {
        
        log.info("Submitting document for manual review: {}", documentReferenceId);
        
        ManualReviewResponse response = documentVerificationService
            .submitForManualReview(documentReferenceId, reviewNotes);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get verification statistics (Admin)", 
               description = "Get document verification statistics and metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @GetMapping("/admin/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocumentVerificationStatistics> getVerificationStatistics() {
        
        log.info("Getting document verification statistics");
        
        DocumentVerificationStatistics stats = documentVerificationService.getVerificationStatistics();
        
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Search documents (Admin)", 
               description = "Search documents by multiple criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @PostMapping("/admin/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<Page<VerificationDocumentResponse>> searchDocuments(
            @Valid @RequestBody DocumentSearchRequest searchRequest,
            Pageable pageable) {
        
        log.info("Searching documents with criteria: {}", searchRequest);
        
        Page<VerificationDocumentResponse> results = documentVerificationService
            .searchDocuments(searchRequest, pageable);
        
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Delete document (Admin)", 
               description = "Delete a document (permanent action)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @DeleteMapping("/{documentReferenceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDocument(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId,
            @Parameter(description = "Reason for deletion")
            @RequestParam String reason) {
        
        log.info("Deleting document: {} - reason: {}", documentReferenceId, reason);
        
        documentVerificationService.deleteDocument(documentReferenceId, reason);
        
        return ResponseEntity.ok().build();
    }

    // ========== AI VERIFICATION ENDPOINTS ==========

    @Operation(summary = "Initiate AI verification", 
               description = "Start AI-powered verification for a document")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "AI verification initiated"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "400", description = "Document not suitable for AI verification")
    })
    @PostMapping("/{documentReferenceId}/ai-verification")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<DocumentAIVerificationResponse> initiateAIVerification(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId) {
        
        log.info("Initiating AI verification for document: {}", documentReferenceId);
        
        DocumentAIVerificationResponse response = documentVerificationService
            .initiateAIVerification(documentReferenceId);
        
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Extract document data", 
               description = "Extract data from document using AI/OCR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Data extracted successfully"),
        @ApiResponse(responseCode = "404", description = "Document not found"),
        @ApiResponse(responseCode = "400", description = "Document not suitable for extraction")
    })
    @PostMapping("/{documentReferenceId}/extract-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT_AGENT')")
    public ResponseEntity<DocumentExtractionResult> extractDocumentData(
            @Parameter(description = "Document reference ID")
            @PathVariable String documentReferenceId) {
        
        log.info("Extracting data from document: {}", documentReferenceId);
        
        DocumentExtractionResult result = documentVerificationService
            .extractDocumentData(documentReferenceId);
        
        return ResponseEntity.ok(result);
    }
}