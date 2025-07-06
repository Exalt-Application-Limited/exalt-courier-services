package com.gogidix.courier.corporate.customer.onboarding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

/**
 * Feign client for Document Verification Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "document-verification", path = "/api/v1/documents")
public interface DocumentVerificationClient {

    @PostMapping("/upload")
    DocumentUploadResponse uploadDocument(@RequestBody UploadDocumentRequest request);

    // Request DTOs
    record UploadDocumentRequest(
            String applicationReferenceId,
            String documentType,
            String fileName,
            byte[] fileContent,
            Long fileSize,
            String mimeType,
            String applicationType
    ) {}

    // Response DTOs
    record DocumentUploadResponse(
            String documentReferenceId,
            String status,
            String uploadUrl,
            LocalDateTime expiresAt
    ) {}
}