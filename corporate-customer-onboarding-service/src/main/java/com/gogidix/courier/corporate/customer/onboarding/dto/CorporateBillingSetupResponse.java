package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for corporate billing setup completion.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Response for corporate billing setup")
public record CorporateBillingSetupResponse(
    
    @Schema(description = "Corporate billing profile ID", example = "CORP-BILL-789012")
    String billingId,
    
    @Schema(description = "Configured billing cycle", example = "MONTHLY")
    String billingCycle,
    
    @Schema(description = "Configured payment terms", example = "NET_30")
    String paymentTerms,
    
    @Schema(description = "Applied volume discount percentage", example = "0.15")
    Double volumeDiscount,
    
    @Schema(description = "Setup completion message")
    String message,
    
    @Schema(description = "Billing setup completion timestamp")
    LocalDateTime setupCompletedAt,
    
    @Schema(description = "Assigned account manager")
    AccountManagerInfo accountManager,
    
    @Schema(description = "Credit limit assigned")
    BigDecimal creditLimit,
    
    @Schema(description = "Payment methods configured")
    List<String> configuredPaymentMethods,
    
    @Schema(description = "Next billing cycle start date")
    LocalDateTime nextBillingDate,
    
    @Schema(description = "Invoice portal access URL")
    String invoicePortalUrl,
    
    @Schema(description = "API endpoints for billing integration")
    BillingApiEndpoints apiEndpoints,
    
    @Schema(description = "Cost centers configured")
    Integer costCentersConfigured,
    
    @Schema(description = "Tax configuration status")
    String taxConfigurationStatus
) {}

/**
 * Account manager information.
 */
@Schema(description = "Account manager information")
record AccountManagerInfo(
    
    @Schema(description = "Account manager ID", example = "AM-001")
    String managerId,
    
    @Schema(description = "Account manager name", example = "John Doe")
    String name,
    
    @Schema(description = "Account manager email", example = "john.doe@exaltcourier.com")
    String email,
    
    @Schema(description = "Account manager phone", example = "+1-555-0123")
    String phone,
    
    @Schema(description = "Manager's territory or region", example = "North America")
    String territory
) {}

/**
 * Billing API endpoints for integration.
 */
@Schema(description = "Billing API endpoints")
record BillingApiEndpoints(
    
    @Schema(description = "Invoice retrieval endpoint")
    String invoiceEndpoint,
    
    @Schema(description = "Payment processing endpoint")
    String paymentEndpoint,
    
    @Schema(description = "Balance inquiry endpoint")
    String balanceEndpoint,
    
    @Schema(description = "Billing webhook URL for notifications")
    String webhookUrl,
    
    @Schema(description = "API authentication token")
    String authToken,
    
    @Schema(description = "API rate limits")
    String rateLimits
) {}