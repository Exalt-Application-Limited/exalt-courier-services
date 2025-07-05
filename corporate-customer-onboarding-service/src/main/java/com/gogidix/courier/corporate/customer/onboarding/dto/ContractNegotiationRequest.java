package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for initiating contract negotiation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request to initiate contract negotiation")
public record ContractNegotiationRequest(
    
    @NotNull(message = "Expected monthly volume is required")
    @Min(value = 1, message = "Expected monthly volume must be at least 1")
    @Schema(description = "Expected monthly shipment volume", example = "5000")
    Integer expectedMonthlyVolume,
    
    @Schema(description = "Special requirements for the contract", example = "Same-day delivery for priority items")
    String specialRequirements,
    
    @Schema(description = "Preferred contract terms", example = "NET_30")
    String preferredPaymentTerms,
    
    @Schema(description = "Preferred billing cycle", example = "MONTHLY", allowableValues = {"WEEKLY", "MONTHLY", "QUARTERLY", "ANNUALLY"})
    String preferredBillingCycle,
    
    @Schema(description = "Primary service types needed")
    List<String> primaryServiceTypes,
    
    @Schema(description = "Geographic coverage requirements")
    List<String> coverageAreas,
    
    @Schema(description = "Peak season volume estimates")
    Integer peakSeasonVolume,
    
    @Schema(description = "Annual contract value estimate")
    BigDecimal estimatedAnnualValue,
    
    @Schema(description = "Service level agreement requirements")
    ServiceLevelRequirements slaRequirements,
    
    @Schema(description = "Integration requirements")
    List<String> integrationRequirements,
    
    @Schema(description = "Preferred discount structure", example = "VOLUME_BASED")
    String discountStructure,
    
    @Size(max = 1000, message = "Additional notes must not exceed 1000 characters")
    @Schema(description = "Additional negotiation notes")
    String additionalNotes
) {}

/**
 * Service level requirements for contract negotiation.
 */
@Schema(description = "Service level agreement requirements")
record ServiceLevelRequirements(
    
    @Schema(description = "Required delivery time SLA (in hours)", example = "24")
    Integer deliveryTimeSla,
    
    @Schema(description = "Required delivery success rate (%)", example = "99.5")
    Double deliverySuccessRate,
    
    @Schema(description = "Required customer service response time (in minutes)", example = "15")
    Integer customerServiceResponseTime,
    
    @Schema(description = "Required pickup time window (in hours)", example = "2")
    Integer pickupTimeWindow,
    
    @Schema(description = "Tracking update frequency requirements", example = "REAL_TIME")
    String trackingFrequency,
    
    @Schema(description = "Insurance coverage requirements")
    BigDecimal insuranceCoverage
) {}