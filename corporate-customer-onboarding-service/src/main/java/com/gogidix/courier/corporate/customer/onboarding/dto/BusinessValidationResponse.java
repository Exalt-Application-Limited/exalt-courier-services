package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for business registration validation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Response from business registration validation")
public record BusinessValidationResponse(
    
    @Schema(description = "Business registration number", example = "BRN-123456789")
    String registrationNumber,
    
    @Schema(description = "Validation status", example = "VALID", allowableValues = {"VALID", "INVALID", "EXPIRED", "SUSPENDED", "PENDING"})
    String status,
    
    @Schema(description = "Registered business name", example = "Acme Corporation Ltd.")
    String businessName,
    
    @Schema(description = "Business type", example = "LIMITED_LIABILITY_COMPANY")
    String businessType,
    
    @Schema(description = "Registration status", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DISSOLVED", "SUSPENDED"})
    String registrationStatus,
    
    @Schema(description = "Validation timestamp")
    LocalDateTime validatedAt,
    
    @Schema(description = "Validation message or details")
    String message,
    
    @Schema(description = "Whether the business is valid for courier services")
    Boolean isValid,
    
    @Schema(description = "Business incorporation date")
    LocalDateTime incorporationDate,
    
    @Schema(description = "Registered address")
    String registeredAddress,
    
    @Schema(description = "List of authorized signatories")
    List<String> authorizedSignatories,
    
    @Schema(description = "Business compliance status")
    Map<String, Boolean> complianceStatus,
    
    @Schema(description = "Risk assessment score (0-100)", example = "85")
    Integer riskScore,
    
    @Schema(description = "Additional validation flags")
    Map<String, String> validationFlags
) {}