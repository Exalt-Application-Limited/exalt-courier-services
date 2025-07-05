package com.gogidix.courier.corporate.customer.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for contract negotiation initiation.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Response for contract negotiation initiation")
public record ContractNegotiationResponse(
    
    @Schema(description = "Unique negotiation session ID", example = "NEG-20241201-ABC123")
    String negotiationId,
    
    @Schema(description = "Negotiation status", example = "INITIATED", allowableValues = {"INITIATED", "IN_PROGRESS", "COMPLETED", "CANCELLED"})
    String status,
    
    @Schema(description = "Monthly volume being negotiated", example = "5000")
    Integer monthlyVolume,
    
    @Schema(description = "Proposed volume discount percentage", example = "0.15")
    Double volumeDiscount,
    
    @Schema(description = "Negotiation status message")
    String message,
    
    @Schema(description = "Expected contact date from sales team")
    LocalDateTime expectedContactDate,
    
    @Schema(description = "Assigned sales representative")
    SalesRepresentativeInfo salesRepresentative,
    
    @Schema(description = "Initial pricing proposal")
    PricingProposal initialPricing,
    
    @Schema(description = "Service level agreement proposal")
    ServiceLevelProposal slaProposal,
    
    @Schema(description = "Contract terms proposal")
    ContractTermsProposal contractTerms,
    
    @Schema(description = "Next steps in the negotiation process")
    List<String> nextSteps,
    
    @Schema(description = "Estimated negotiation timeline (in business days)", example = "5")
    Integer estimatedTimelineDays
) {}

/**
 * Sales representative information.
 */
@Schema(description = "Sales representative information")
record SalesRepresentativeInfo(
    
    @Schema(description = "Sales rep ID", example = "SALES-001")
    String salesRepId,
    
    @Schema(description = "Sales rep name", example = "Jane Smith")
    String name,
    
    @Schema(description = "Sales rep email", example = "jane.smith@exaltcourier.com")
    String email,
    
    @Schema(description = "Sales rep phone", example = "+1-555-0123")
    String phone,
    
    @Schema(description = "Sales rep territory", example = "North America")
    String territory
) {}

/**
 * Pricing proposal details.
 */
@Schema(description = "Pricing proposal details")
record PricingProposal(
    
    @Schema(description = "Base rate per shipment")
    BigDecimal baseRate,
    
    @Schema(description = "Volume discount tiers")
    Map<String, Double> discountTiers,
    
    @Schema(description = "Service-specific rates")
    Map<String, BigDecimal> serviceRates,
    
    @Schema(description = "Fuel surcharge percentage", example = "0.05")
    Double fuelSurcharge,
    
    @Schema(description = "Additional fees")
    Map<String, BigDecimal> additionalFees
) {}

/**
 * Service level agreement proposal.
 */
@Schema(description = "Service level agreement proposal")
record ServiceLevelProposal(
    
    @Schema(description = "Guaranteed delivery time (hours)", example = "24")
    Integer guaranteedDeliveryTime,
    
    @Schema(description = "Delivery success rate guarantee (%)", example = "99.5")
    Double deliverySuccessRate,
    
    @Schema(description = "Customer service response time (minutes)", example = "15")
    Integer customerServiceResponseTime,
    
    @Schema(description = "Service level penalties")
    Map<String, String> penalties,
    
    @Schema(description = "Performance incentives")
    Map<String, String> incentives
) {}

/**
 * Contract terms proposal.
 */
@Schema(description = "Contract terms proposal")
record ContractTermsProposal(
    
    @Schema(description = "Contract duration (months)", example = "12")
    Integer durationMonths,
    
    @Schema(description = "Payment terms", example = "NET_30")
    String paymentTerms,
    
    @Schema(description = "Billing frequency", example = "MONTHLY")
    String billingFrequency,
    
    @Schema(description = "Termination clauses")
    List<String> terminationClauses,
    
    @Schema(description = "Renewal options")
    List<String> renewalOptions,
    
    @Schema(description = "Insurance requirements")
    BigDecimal insuranceRequirement
) {}