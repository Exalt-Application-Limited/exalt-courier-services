package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Response DTO for document upload operations.
 */
@Schema(description = "Response for document upload")
public record DocumentUploadResponse(
    
    @Schema(description = "Document reference ID", example = "DOC-CORP-20241201-XYZ789")
    String documentReferenceId,
    
    @Schema(description = "Upload status", example = "UPLOADED")
    String status,
    
    @Schema(description = "Secure upload URL if applicable")
    String uploadUrl,
    
    @Schema(description = "Upload URL expiration time")
    LocalDateTime expiresAt,
    
    @Schema(description = "Upload status message")
    String message
) {}