package com.exalt.courier.onboarding.controller;

import com.exalt.courier.onboarding.dto.VerificationDocumentResponse;
import com.exalt.courier.onboarding.model.DocumentType;
import com.exalt.courier.onboarding.model.VerificationDocument;
import com.exalt.courier.onboarding.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing document uploads and verification.
 */
@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload a verification document for an application
     *
     * @param applicationReferenceId Application reference ID
     * @param documentType Type of document
     * @param file Document file
     * @param notes Optional notes
     * @return The uploaded document details
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VerificationDocumentResponse> uploadDocument(
            @RequestParam String applicationReferenceId,
            @RequestParam DocumentType documentType,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String notes) {
        
        log.info("Uploading document of type {} for application {}", documentType, applicationReferenceId);
        
        VerificationDocument document = documentService.uploadVerificationDocument(
                applicationReferenceId, documentType, file, notes);
        
        VerificationDocumentResponse response = convertToDto(document);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all documents for an application
     *
     * @param applicationReferenceId Application reference ID
     * @return List of documents
     */
    @GetMapping("/application/{applicationReferenceId}")
    public ResponseEntity<List<VerificationDocumentResponse>> getDocumentsForApplication(
            @PathVariable String applicationReferenceId) {
        
        log.info("Fetching documents for application {}", applicationReferenceId);
        
        List<VerificationDocument> documents = documentService.getDocumentsForApplication(applicationReferenceId);
        List<VerificationDocumentResponse> responses = documents.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Get a document by reference ID
     *
     * @param documentReferenceId Document reference ID
     * @return The document if found
     */
    @GetMapping("/{documentReferenceId}")
    public ResponseEntity<VerificationDocumentResponse> getDocumentByReferenceId(
            @PathVariable String documentReferenceId) {
        
        log.info("Fetching document with reference ID {}", documentReferenceId);
        
        return documentService.getDocumentByReferenceId(documentReferenceId)
                .map(document -> ResponseEntity.ok(convertToDto(document)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Verify a document
     *
     * @param documentReferenceId Document reference ID
     * @param userId ID of the user verifying the document
     * @param notes Optional notes
     * @return The verified document
     */
    @PostMapping("/{documentReferenceId}/verify")
    public ResponseEntity<VerificationDocumentResponse> verifyDocument(
            @PathVariable String documentReferenceId,
            @RequestParam String userId,
            @RequestParam(required = false) String notes) {
        
        log.info("Verifying document {} by user {}", documentReferenceId, userId);
        
        VerificationDocument document = documentService.verifyDocument(documentReferenceId, userId, notes);
        VerificationDocumentResponse response = convertToDto(document);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Reject a document
     *
     * @param documentReferenceId Document reference ID
     * @param userId ID of the user rejecting the document
     * @param reason Reason for rejection
     * @return The rejected document
     */
    @PostMapping("/{documentReferenceId}/reject")
    public ResponseEntity<VerificationDocumentResponse> rejectDocument(
            @PathVariable String documentReferenceId,
            @RequestParam String userId,
            @RequestParam String reason) {
        
        log.info("Rejecting document {} by user {} for reason: {}", 
                documentReferenceId, userId, reason);
        
        VerificationDocument document = documentService.rejectDocument(documentReferenceId, userId, reason);
        VerificationDocumentResponse response = convertToDto(document);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a document
     *
     * @param documentReferenceId Document reference ID
     * @return No content if successful
     */
    @DeleteMapping("/{documentReferenceId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable String documentReferenceId) {
        
        log.info("Deleting document {}", documentReferenceId);
        
        documentService.deleteDocument(documentReferenceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get missing documents for an application
     *
     * @param applicationReferenceId Application reference ID
     * @return List of required document types that are missing
     */
    @GetMapping("/missing/{applicationReferenceId}")
    public ResponseEntity<List<DocumentType>> getMissingDocuments(
            @PathVariable String applicationReferenceId) {
        
        log.info("Checking for missing documents for application {}", applicationReferenceId);
        
        List<DocumentType> missingDocuments = documentService.getMissingDocuments(applicationReferenceId);
        return ResponseEntity.ok(missingDocuments);
    }

    /**
     * Convert entity to DTO
     *
     * @param document Entity
     * @return Response DTO
     */
    private VerificationDocumentResponse convertToDto(VerificationDocument document) {
        return VerificationDocumentResponse.builder()
                .id(document.getId())
                .referenceId(document.getReferenceId())
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .uploadedAt(document.getUploadedAt())
                .verified(document.getVerified())
                .verifiedBy(document.getVerifiedBy())
                .verifiedAt(document.getVerifiedAt())
                .rejected(document.getRejected())
                .rejectedBy(document.getRejectedBy())
                .rejectedAt(document.getRejectedAt())
                .rejectionReason(document.getRejectionReason())
                .notes(document.getNotes())
                .build();
    }
}
