package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO for corporate account setup.
 */
@Schema(description = "Response for corporate account setup")
public record CorporateAccountSetupResponse(
    
    @Schema(description = "Corporate auth service ID", example = "CORP-AUTH-123456")
    String corporateAuthId,
    
    @Schema(description = "Corporate billing ID", example = "CORP-BILL-789012")
    String billingId,
    
    @Schema(description = "Number of users created", example = "5")
    Integer usersCreated,
    
    @Schema(description = "Setup completion message")
    String message,
    
    @Schema(description = "Account activation instructions")
    String activationInstructions
) {}