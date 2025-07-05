package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for corporate KYC verification initiation.
 */
@Schema(description = "Response for corporate KYC verification initiation")
public record CorporateKycInitiationResponse(
    
    @Schema(description = "KYC verification ID", example = "KYC-CORP-20241201-ABC123")
    String verificationId,
    
    @Schema(description = "Initial KYC status", example = "INITIATED")
    String status,
    
    @Schema(description = "Corporate reference ID", example = "CORP-REF-456789")
    String corporateReferenceId,
    
    @Schema(description = "Estimated completion time", example = "5-7 business days")
    String estimatedCompletionTime,
    
    @Schema(description = "Instructions for document upload")
    String instructions,
    
    @Schema(description = "List of required business documents")
    List<String> requiredDocuments,
    
    @Schema(description = "KYC initiation timestamp")
    LocalDateTime createdAt
) {}