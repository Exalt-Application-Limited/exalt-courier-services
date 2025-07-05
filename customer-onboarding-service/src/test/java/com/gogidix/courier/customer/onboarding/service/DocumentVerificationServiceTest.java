package com.gogidix.courier.customer.onboarding.service;

import com.gogidix.courier.customer.onboarding.dto.*;
import com.gogidix.courier.customer.onboarding.model.CustomerOnboardingApplication;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentType;
import com.gogidix.courier.customer.onboarding.model.CustomerVerificationDocument.DocumentVerificationStatus;
import com.gogidix.courier.customer.onboarding.repository.CustomerOnboardingApplicationRepository;
import com.gogidix.courier.customer.onboarding.repository.CustomerVerificationDocumentRepository;
import com.gogidix.courier.customer.onboarding.service.impl.DocumentVerificationServiceImpl;
import com.gogidix.shared.exceptions.BusinessException;
import com.gogidix.shared.exceptions.ResourceNotFoundException;
import com.gogidix.shared.utilities.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DocumentVerificationService.
 * 
 * Tests document verification operations including:
 * - Document upload and storage
 * - Document verification workflow
 * - Approval/rejection operations
 * - Document search and retrieval
 * - AI verification integration
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Document Verification Service Tests")
class DocumentVerificationServiceTest {

    @Mock
    private CustomerVerificationDocumentRepository documentRepository;

    @Mock
    private CustomerOnboardingApplicationRepository applicationRepository;

    @InjectMocks
    private DocumentVerificationServiceImpl documentVerificationService;

    @TempDir
    Path tempDir;

    private CustomerOnboardingApplication application;
    private CustomerVerificationDocument document;
    private String applicationReferenceId;
    private String documentReferenceId;
    private MultipartFile testFile;

    @BeforeEach
    void setUp() {
        // Configure service properties
        ReflectionTestUtils.setField(documentVerificationService, "documentStoragePath", 
            tempDir.toString());
        ReflectionTestUtils.setField(documentVerificationService, "maxFileSize", 10485760L);
        ReflectionTestUtils.setField(documentVerificationService, "allowedMimeTypes", 
            "image/jpeg,image/png,application/pdf");
        ReflectionTestUtils.setField(documentVerificationService, "aiVerificationEnabled", true);

        applicationReferenceId = "APP-2025-001234";
        documentReferenceId = "DOC-APP-NAT-123456";

        // Create test application
        application = CustomerOnboardingApplication.builder()
            .id(UUID.randomUUID())
            .applicationReferenceId(applicationReferenceId)
            .customerEmail("john.doe@gmail.com")
            .build();

        // Create test document
        document = CustomerVerificationDocument.builder()
            .id(UUID.randomUUID())
            .documentReferenceId(documentReferenceId)
            .customerApplication(application)
            .documentType(DocumentType.NATIONAL_ID)
            .fileName("national-id.jpg")
            .fileSize(1024L)
            .mimeType("image/jpeg")
            .storedFilePath(tempDir.resolve("national-id.jpg").toString())
            .verificationStatus(DocumentVerificationStatus.PENDING)
            .uploadedAt(LocalDateTime.now())
            .uploadedBy("customer")
            .isPrimary(true)
            .build();

        // Create test file
        testFile = new MockMultipartFile(
            "file",
            "national-id.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );
    }

    @Test
    @DisplayName("Should upload document successfully")
    void shouldUploadDocumentSuccessfully() throws IOException {
        // Given
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));
        when(documentRepository.existsByCustomerApplication_ApplicationReferenceIdAndDocumentTypeAndVerificationStatus(
            eq(applicationReferenceId), eq(DocumentType.NATIONAL_ID), eq(DocumentVerificationStatus.APPROVED)))
            .thenReturn(false);
        when(documentRepository.save(any(CustomerVerificationDocument.class)))
            .thenReturn(document);

        // When
        try (MockedStatic<SecurityUtil> mockedSecurity = mockStatic(SecurityUtil.class)) {
            mockedSecurity.when(SecurityUtil::getCurrentUsername).thenReturn("customer");
            
            DocumentUploadResponse response = documentVerificationService.uploadDocument(
                applicationReferenceId, DocumentType.NATIONAL_ID, testFile, true);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.documentType()).isEqualTo(DocumentType.NATIONAL_ID);
            assertThat(response.fileName()).isEqualTo("national-id.jpg");
            assertThat(response.verificationStatus()).isEqualTo(DocumentVerificationStatus.PENDING);
            assertThat(response.aiVerificationInitiated()).isNotNull();

            verify(documentRepository).save(any(CustomerVerificationDocument.class));
            
            // Verify file was stored
            ArgumentCaptor<CustomerVerificationDocument> captor = 
                ArgumentCaptor.forClass(CustomerVerificationDocument.class);
            verify(documentRepository).save(captor.capture());
            
            String storedPath = captor.getValue().getStoredFilePath();
            assertThat(Files.exists(Path.of(storedPath))).isTrue();
        }
    }

    @Test
    @DisplayName("Should reject oversized file")
    void shouldRejectOversizedFile() {
        // Given
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile largeFile = new MockMultipartFile(
            "file", "large-file.jpg", "image/jpeg", largeContent);

        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));

        // When/Then
        assertThatThrownBy(() -> documentVerificationService.uploadDocument(
            applicationReferenceId, DocumentType.NATIONAL_ID, largeFile, true))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("File size exceeds maximum");
    }

    @Test
    @DisplayName("Should reject invalid file type")
    void shouldRejectInvalidFileType() {
        // Given
        MultipartFile invalidFile = new MockMultipartFile(
            "file", "document.txt", "text/plain", "text content".getBytes());

        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));

        // When/Then
        assertThatThrownBy(() -> documentVerificationService.uploadDocument(
            applicationReferenceId, DocumentType.NATIONAL_ID, invalidFile, true))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("File type not allowed");
    }

    @Test
    @DisplayName("Should reject duplicate primary document")
    void shouldRejectDuplicatePrimaryDocument() {
        // Given
        when(applicationRepository.findByApplicationReferenceId(applicationReferenceId))
            .thenReturn(Optional.of(application));
        when(documentRepository.existsByCustomerApplication_ApplicationReferenceIdAndDocumentTypeAndVerificationStatus(
            eq(applicationReferenceId), eq(DocumentType.NATIONAL_ID), eq(DocumentVerificationStatus.APPROVED)))
            .thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> documentVerificationService.uploadDocument(
            applicationReferenceId, DocumentType.NATIONAL_ID, testFile, true))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("already has an approved document");
    }

    @Test
    @DisplayName("Should approve document successfully")
    void shouldApproveDocumentSuccessfully() {
        // Given
        when(documentRepository.findByDocumentReferenceId(documentReferenceId))
            .thenReturn(Optional.of(document));
        when(documentRepository.save(any(CustomerVerificationDocument.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        DocumentApprovalRequest approvalRequest = new DocumentApprovalRequest(
            "admin", "Document verified successfully", 0.95, null, "2026-12-31");

        // When
        documentVerificationService.approveDocument(documentReferenceId, approvalRequest);

        // Then
        ArgumentCaptor<CustomerVerificationDocument> captor = 
            ArgumentCaptor.forClass(CustomerVerificationDocument.class);
        verify(documentRepository).save(captor.capture());
        
        CustomerVerificationDocument approved = captor.getValue();
        assertThat(approved.getVerificationStatus()).isEqualTo(DocumentVerificationStatus.APPROVED);
        assertThat(approved.getReviewedBy()).isEqualTo("admin");
        assertThat(approved.getConfidenceScore()).isEqualTo(0.95);
        assertThat(approved.getDocumentExpiryDate()).isNotNull();
    }

    @Test
    @DisplayName("Should reject document successfully")
    void shouldRejectDocumentSuccessfully() {
        // Given
        when(documentRepository.findByDocumentReferenceId(documentReferenceId))
            .thenReturn(Optional.of(document));
        when(documentRepository.save(any(CustomerVerificationDocument.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        DocumentRejectionRequest rejectionRequest = DocumentRejectionRequest.expiredDocument("admin");

        // When
        documentVerificationService.rejectDocument(documentReferenceId, rejectionRequest);

        // Then
        ArgumentCaptor<CustomerVerificationDocument> captor = 
            ArgumentCaptor.forClass(CustomerVerificationDocument.class);
        verify(documentRepository).save(captor.capture());
        
        CustomerVerificationDocument rejected = captor.getValue();
        assertThat(rejected.getVerificationStatus()).isEqualTo(DocumentVerificationStatus.REJECTED);
        assertThat(rejected.getReviewedBy()).isEqualTo("admin");
        assertThat(rejected.getRejectionReason()).contains("expired");
        assertThat(rejected.getAllowResubmission()).isTrue();
    }

    @Test
    @DisplayName("Should check document completion status")
    void shouldCheckDocumentCompletionStatus() {
        // Given
        when(applicationRepository.existsByApplicationReferenceId(applicationReferenceId))
            .thenReturn(true);
        
        // Create multiple documents with different statuses
        CustomerVerificationDocument approvedDoc = createDocument(
            DocumentType.NATIONAL_ID, DocumentVerificationStatus.APPROVED);
        CustomerVerificationDocument pendingDoc = createDocument(
            DocumentType.PROOF_OF_ADDRESS, DocumentVerificationStatus.PENDING);
        
        when(documentRepository.findByCustomerApplication_ApplicationReferenceIdOrderByUploadedAtDesc(
            applicationReferenceId))
            .thenReturn(List.of(approvedDoc, pendingDoc));

        // When
        DocumentCompletionStatus status = documentVerificationService
            .checkDocumentCompletionStatus(applicationReferenceId);

        // Then
        assertThat(status).isNotNull();
        assertThat(status.isComplete()).isFalse();
        assertThat(status.completedDocuments()).contains(DocumentType.NATIONAL_ID);
        assertThat(status.pendingDocuments()).contains(DocumentType.PROOF_OF_ADDRESS);
        assertThat(status.completionPercentage()).isGreaterThan(0).isLessThan(100);
    }

    @Test
    @DisplayName("Should get documents pending review")
    void shouldGetDocumentsPendingReview() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomerVerificationDocument> pendingDocs = List.of(document);
        Page<CustomerVerificationDocument> page = new PageImpl<>(pendingDocs, pageable, 1);
        
        when(documentRepository.findByVerificationStatusInOrderByUploadedAtAsc(
            anyList(), eq(pageable)))
            .thenReturn(page);

        // When
        Page<VerificationDocumentResponse> result = documentVerificationService
            .getDocumentsPendingReview(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).verificationStatus())
            .isEqualTo(DocumentVerificationStatus.PENDING);
    }

    @Test
    @DisplayName("Should download document successfully")
    void shouldDownloadDocumentSuccessfully() throws IOException {
        // Given
        // Create actual file
        Path filePath = tempDir.resolve("test-document.jpg");
        Files.write(filePath, "test content".getBytes());
        document.setStoredFilePath(filePath.toString());
        
        when(documentRepository.findByDocumentReferenceId(documentReferenceId))
            .thenReturn(Optional.of(document));

        // When
        DocumentDownloadResponse response = documentVerificationService
            .downloadDocument(documentReferenceId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.fileName()).isEqualTo("national-id.jpg");
        assertThat(response.mimeType()).isEqualTo("image/jpeg");
        assertThat(new String(response.fileContent())).isEqualTo("test content");
    }

    @Test
    @DisplayName("Should initiate AI verification for suitable document")
    void shouldInitiateAIVerificationForSuitableDocument() {
        // Given
        when(documentRepository.findByDocumentReferenceId(documentReferenceId))
            .thenReturn(Optional.of(document));
        when(documentRepository.save(any(CustomerVerificationDocument.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        DocumentAIVerificationResponse response = documentVerificationService
            .initiateAIVerification(documentReferenceId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.verificationInitiated()).isTrue();
        assertThat(response.documentReferenceId()).isEqualTo(documentReferenceId);
        
        ArgumentCaptor<CustomerVerificationDocument> captor = 
            ArgumentCaptor.forClass(CustomerVerificationDocument.class);
        verify(documentRepository).save(captor.capture());
        
        assertThat(captor.getValue().getVerificationStatus())
            .isEqualTo(DocumentVerificationStatus.AI_VERIFICATION_IN_PROGRESS);
    }

    @Test
    @DisplayName("Should reject AI verification for unsuitable document type")
    void shouldRejectAIVerificationForUnsuitableDocument() {
        // Given
        document.setDocumentType(DocumentType.OTHER);
        when(documentRepository.findByDocumentReferenceId(documentReferenceId))
            .thenReturn(Optional.of(document));

        // When/Then
        assertThatThrownBy(() -> documentVerificationService.initiateAIVerification(documentReferenceId))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not suitable for AI verification");
    }

    @Test
    @DisplayName("Should delete document successfully")
    void shouldDeleteDocumentSuccessfully() throws IOException {
        // Given
        // Create actual file
        Path filePath = tempDir.resolve("to-delete.jpg");
        Files.write(filePath, "content".getBytes());
        document.setStoredFilePath(filePath.toString());
        
        when(documentRepository.findByDocumentReferenceId(documentReferenceId))
            .thenReturn(Optional.of(document));

        // When
        documentVerificationService.deleteDocument(documentReferenceId, "Test deletion");

        // Then
        verify(documentRepository).delete(document);
        assertThat(Files.exists(filePath)).isFalse();
    }

    // Helper method
    private CustomerVerificationDocument createDocument(DocumentType type, 
                                                       DocumentVerificationStatus status) {
        return CustomerVerificationDocument.builder()
            .id(UUID.randomUUID())
            .documentReferenceId("DOC-" + type.name())
            .customerApplication(application)
            .documentType(type)
            .verificationStatus(status)
            .uploadedAt(LocalDateTime.now())
            .build();
    }
}