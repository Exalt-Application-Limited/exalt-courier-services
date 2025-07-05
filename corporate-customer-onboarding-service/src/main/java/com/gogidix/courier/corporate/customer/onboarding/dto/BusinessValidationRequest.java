package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO for business registration validation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request to validate business registration")
public record BusinessValidationRequest(
    
    @NotBlank(message = "Business registration number is required")
    @Pattern(regexp = "^[A-Z0-9\\-]{5,20}$", message = "Invalid registration number format")
    @Schema(description = "Business registration number", example = "BRN-123456789")
    String registrationNumber,
    
    @NotBlank(message = "Business name is required")
    @Schema(description = "Registered business name", example = "Acme Corporation Ltd.")
    String businessName,
    
    @NotBlank(message = "Country is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country must be a 2-letter ISO code")
    @Schema(description = "Country of registration (ISO 3166-1 alpha-2)", example = "US")
    String country,
    
    @Schema(description = "State or province of registration", example = "California")
    String stateProvince,
    
    @Schema(description = "Business type for validation", example = "LIMITED_LIABILITY_COMPANY")
    String businessType,
    
    @Schema(description = "Tax identification number for cross-validation", example = "12-3456789")
    String taxId
) {}