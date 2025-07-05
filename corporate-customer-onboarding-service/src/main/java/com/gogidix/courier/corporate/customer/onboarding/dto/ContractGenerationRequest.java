package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for generating corporate service agreement contracts.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request to generate corporate service agreement")
public record ContractGenerationRequest(
    
    @NotBlank(message = "Contract type is required")
    @Schema(description = "Type of contract to generate", example = "STANDARD_SERVICE_AGREEMENT", 
            allowableValues = {"STANDARD_SERVICE_AGREEMENT", "ENTERPRISE_CONTRACT", "GOVERNMENT_CONTRACT", "NONPROFIT_AGREEMENT"})
    String contractType,
    
    @NotNull(message = "Contract terms are required")
    @Schema(description = "Contract terms and conditions")
    ContractTermsRequest contractTerms,
    
    @Schema(description = "Service level agreements")
    ServiceLevelAgreementRequest slaTerms,
    
    @Schema(description = "Pricing and billing terms")
    ContractPricingRequest pricingTerms,
    
    @Schema(description = "Legal and compliance requirements")
    LegalComplianceRequest complianceTerms,
    
    @Schema(description = "Custom contract clauses")
    List<CustomClauseRequest> customClauses,
    
    @Schema(description = "Contract template preferences")
    ContractTemplatePreferences templatePreferences,
    
    @Schema(description = "Additional contract metadata")
    Map<String, Object> metadata
) {}

/**
 * Contract terms request details.
 */
@Schema(description = "Contract terms request")
record ContractTermsRequest(
    
    @Schema(description = "Contract duration in months", example = "12")
    Integer durationMonths,
    
    @Schema(description = "Contract start date")
    LocalDateTime startDate,
    
    @Schema(description = "Contract end date")
    LocalDateTime endDate,
    
    @Schema(description = "Auto-renewal clause enabled")
    Boolean autoRenewal,
    
    @Schema(description = "Termination notice period (days)", example = "30")
    Integer terminationNoticeDays,
    
    @Schema(description = "Early termination clauses")
    List<String> earlyTerminationClauses,
    
    @Schema(description = "Renewal terms")
    RenewalTermsRequest renewalTerms,
    
    @Schema(description = "Governing law jurisdiction", example = "State of Delaware")
    String governingLaw,
    
    @Schema(description = "Dispute resolution method", example = "ARBITRATION", 
            allowableValues = {"ARBITRATION", "MEDIATION", "LITIGATION"})
    String disputeResolution
) {}

/**
 * Service level agreement request details.
 */
@Schema(description = "Service level agreement request")
record ServiceLevelAgreementRequest(
    
    @Schema(description = "Guaranteed delivery timeframes")
    Map<String, Integer> deliveryTimeframes,
    
    @Schema(description = "Service availability percentage", example = "99.9")
    Double serviceAvailability,
    
    @Schema(description = "Performance metrics and KPIs")
    Map<String, Object> performanceMetrics,
    
    @Schema(description = "Service level penalties")
    List<ServicePenaltyRequest> servicePenalties,
    
    @Schema(description = "Service level bonuses")
    List<ServiceBonusRequest> serviceBonuses,
    
    @Schema(description = "Escalation procedures")
    List<String> escalationProcedures,
    
    @Schema(description = "Reporting requirements")
    ReportingRequirementsRequest reportingRequirements
) {}

/**
 * Contract pricing request details.
 */
@Schema(description = "Contract pricing request")
record ContractPricingRequest(
    
    @Schema(description = "Base pricing structure")
    Map<String, BigDecimal> basePricing,
    
    @Schema(description = "Volume discount tiers")
    Map<String, Double> volumeDiscounts,
    
    @Schema(description = "Fuel surcharge terms")
    FuelSurchargeRequest fuelSurcharge,
    
    @Schema(description = "Additional fees and charges")
    Map<String, BigDecimal> additionalFees,
    
    @Schema(description = "Payment terms", example = "NET_30")
    String paymentTerms,
    
    @Schema(description = "Billing frequency", example = "MONTHLY")
    String billingFrequency,
    
    @Schema(description = "Currency", example = "USD")
    String currency,
    
    @Schema(description = "Price adjustment mechanisms")
    List<PriceAdjustmentRequest> priceAdjustments
) {}

/**
 * Legal compliance request details.
 */
@Schema(description = "Legal compliance request")
record LegalComplianceRequest(
    
    @Schema(description = "Industry-specific regulations")
    List<String> industryRegulations,
    
    @Schema(description = "Data protection requirements")
    DataProtectionRequest dataProtection,
    
    @Schema(description = "Insurance requirements")
    InsuranceRequirementsRequest insurance,
    
    @Schema(description = "Liability limitations")
    LiabilityLimitationRequest liability,
    
    @Schema(description = "Intellectual property clauses")
    List<String> intellectualProperty,
    
    @Schema(description = "Confidentiality agreements")
    ConfidentialityRequest confidentiality,
    
    @Schema(description = "Compliance certifications required")
    List<String> requiredCertifications
) {}

/**
 * Custom contract clause request.
 */
@Schema(description = "Custom contract clause")
record CustomClauseRequest(
    
    @Schema(description = "Clause title", example = "Special Handling Requirements")
    String title,
    
    @Schema(description = "Clause content")
    String content,
    
    @Schema(description = "Clause section", example = "OPERATIONAL_REQUIREMENTS")
    String section,
    
    @Schema(description = "Clause priority", example = "HIGH")
    String priority,
    
    @Schema(description = "Whether clause is mandatory")
    Boolean mandatory
) {}

/**
 * Contract template preferences.
 */
@Schema(description = "Contract template preferences")
record ContractTemplatePreferences(
    
    @Schema(description = "Template style", example = "PROFESSIONAL", 
            allowableValues = {"PROFESSIONAL", "CORPORATE", "LEGAL", "SIMPLIFIED"})
    String templateStyle,
    
    @Schema(description = "Language", example = "EN_US")
    String language,
    
    @Schema(description = "Include company logos")
    Boolean includeLogo,
    
    @Schema(description = "Digital signature requirements")
    DigitalSignatureRequest digitalSignature,
    
    @Schema(description = "Document format", example = "PDF", allowableValues = {"PDF", "DOCX", "HTML"})
    String documentFormat,
    
    @Schema(description = "Watermark requirements")
    String watermark
) {}

// Supporting record classes for complex nested structures

record RenewalTermsRequest(
    Boolean automaticRenewal,
    Integer renewalPeriodMonths,
    String renewalNoticeRequirement,
    List<String> renewalConditions
) {}

record ServicePenaltyRequest(
    String metricName,
    String condition,
    BigDecimal penaltyAmount,
    String penaltyType
) {}

record ServiceBonusRequest(
    String metricName,
    String condition,
    BigDecimal bonusAmount,
    String bonusType
) {}

record ReportingRequirementsRequest(
    String frequency,
    List<String> requiredMetrics,
    String deliveryMethod,
    String format
) {}

record FuelSurchargeRequest(
    Boolean enabled,
    String calculationMethod,
    Double baseRate,
    String adjustmentFrequency
) {}

record PriceAdjustmentRequest(
    String trigger,
    String adjustmentMethod,
    Double maximumAdjustment,
    String notificationRequirement
) {}

record DataProtectionRequest(
    Boolean gdprCompliant,
    Boolean ccpaCompliant,
    String dataRetentionPeriod,
    List<String> dataProcessingActivities
) {}

record InsuranceRequirementsRequest(
    BigDecimal generalLiability,
    BigDecimal cargoInsurance,
    BigDecimal professionalLiability,
    String certificateRequirement
) {}

record LiabilityLimitationRequest(
    BigDecimal maximumLiability,
    List<String> excludedLiabilities,
    String indemnificationClause
) {}

record ConfidentialityRequest(
    Integer durationYears,
    List<String> excludedInformation,
    String returnRequirement
) {}

record DigitalSignatureRequest(
    Boolean required,
    String provider,
    String authenticationLevel,
    Boolean timestampRequired
) {}