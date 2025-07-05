package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * Request DTO for setting up corporate account.
 */
@Schema(description = "Request to setup corporate account with multiple users")
public record CorporateAccountSetupRequest(
    
    @NotEmpty(message = "At least one corporate user is required")
    @Valid
    @Schema(description = "List of corporate users to be created")
    List<CorporateUserRequest> users,
    
    @NotNull(message = "Billing configuration is required")
    @Valid
    @Schema(description = "Corporate billing configuration")
    CorporateBillingConfiguration billingConfiguration,
    
    @Schema(description = "Corporate account settings")
    CorporateAccountSettings accountSettings
) {}

/**
 * Corporate user request within account setup.
 */
@Schema(description = "Corporate user creation request")
record CorporateUserRequest(
    
    @Schema(description = "User email address", example = "john.manager@company.com")
    String email,
    
    @Schema(description = "User first name", example = "John")
    String firstName,
    
    @Schema(description = "User last name", example = "Manager")
    String lastName,
    
    @Schema(description = "User role in organization", example = "ADMIN")
    String role,
    
    @Schema(description = "User permissions")
    List<String> permissions
) {}

/**
 * Corporate billing configuration.
 */
@Schema(description = "Corporate billing configuration")
record CorporateBillingConfiguration(
    
    @Schema(description = "Billing cycle", example = "MONTHLY")
    String billingCycle,
    
    @Schema(description = "Payment terms", example = "NET_30")
    String paymentTerms,
    
    @Schema(description = "Volume discount percentage", example = "0.15")
    Double volumeDiscount,
    
    @Schema(description = "Billing contact email")
    String billingContactEmail,
    
    @Schema(description = "Purchase order required flag")
    Boolean requiresPurchaseOrder
) {}

/**
 * Corporate account settings.
 */
@Schema(description = "Corporate account settings")
record CorporateAccountSettings(
    
    @Schema(description = "Account name or alias")
    String accountName,
    
    @Schema(description = "Default shipping preferences")
    String defaultShippingPreferences,
    
    @Schema(description = "Auto-approval settings")
    Boolean autoApprovalEnabled,
    
    @Schema(description = "Spending limits per user")
    Double userSpendingLimit
) {}