package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for uploading business documents.
 */
@Schema(description = "Request to upload business documents")
public record BusinessDocumentUploadRequest(
    
    @NotBlank(message = "Document type is required")
    @Schema(description = "Type of business document", example = "BUSINESS_REGISTRATION")
    String documentType,
    
    @NotBlank(message = "File name is required")
    @Schema(description = "Name of the document file", example = "business_registration.pdf")
    String fileName,
    
    @NotNull(message = "File content is required")
    @Schema(description = "Base64 encoded file content")
    byte[] fileContent,
    
    @NotNull(message = "File size is required")
    @Positive(message = "File size must be positive")
    @Schema(description = "File size in bytes", example = "2048576")
    Long fileSize,
    
    @NotBlank(message = "MIME type is required")
    @Schema(description = "File MIME type", example = "application/pdf")
    String mimeType,
    
    @Schema(description = "Document description or notes", example = "Certificate of Incorporation")
    String description
) {}