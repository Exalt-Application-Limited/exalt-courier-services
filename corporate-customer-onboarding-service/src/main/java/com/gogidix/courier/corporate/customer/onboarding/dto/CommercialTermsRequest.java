package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for setting commercial terms for corporate customers.
 * Defines pricing, payment terms, and service level agreements.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommercialTermsRequest {
    
    @NotBlank(message = "Application reference ID is required")
    private String applicationReferenceId;
    
    @Valid
    @NotNull(message = "Pricing structure is required")
    private PricingStructure pricingStructure;
    
    @Valid
    @NotNull(message = "Payment terms are required")
    private PaymentTerms paymentTerms;
    
    @Valid
    @NotNull(message = "Service level agreement is required")
    private ServiceLevelAgreement serviceLevelAgreement;
    
    @Valid
    private List<VolumeDiscount> volumeDiscounts;
    
    @Valid
    private List<ServiceAddOn> serviceAddOns;
    
    @NotNull(message = "Contract start date is required")
    @Future(message = "Contract start date must be in the future")
    private LocalDate contractStartDate;
    
    @NotNull(message = "Contract end date is required")
    private LocalDate contractEndDate;
    
    @NotNull(message = "Auto-renewal flag is required")
    private Boolean autoRenewal;
    
    private Integer renewalTermMonths;
    private String specialTermsAndConditions;
    private String negotiatedBy;
    private String approvedBy;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingStructure {
        
        @NotBlank(message = "Pricing model is required")
        private String pricingModel; // STANDARD, TIERED, CUSTOM, NEGOTIATED
        
        @NotNull(message = "Base rate is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Base rate must be positive")
        private BigDecimal baseRate;
        
        @NotBlank(message = "Rate unit is required")
        private String rateUnit; // PER_KG, PER_PACKAGE, PER_KM, FLAT_RATE
        
        private BigDecimal minimumCharge;
        private BigDecimal fuelSurchargeRate;
        private BigDecimal insuranceFeeRate;
        private BigDecimal handlingFeeRate;
        
        @Valid
        private List<ZonePricing> zonePricing;
        
        @Valid
        private List<ServiceTypePricing> serviceTypePricing;
        
        private boolean customPricingApplied;
        private String pricingNotes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ZonePricing {
        
        @NotBlank(message = "Zone name is required")
        private String zoneName;
        
        @NotBlank(message = "Zone description is required")
        private String zoneDescription;
        
        @NotNull(message = "Zone rate is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Zone rate must be positive")
        private BigDecimal rate;
        
        private BigDecimal minimumCharge;
        private List<String> includedAreas;
        private String deliveryTimeframe;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceTypePricing {
        
        @NotBlank(message = "Service type is required")
        private String serviceType; // STANDARD, EXPRESS, OVERNIGHT, SAME_DAY
        
        @NotNull(message = "Service rate is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Service rate must be positive")
        private BigDecimal rate;
        
        private BigDecimal surchargeRate;
        private String deliveryTimeframe;
        private boolean availableWeekends;
        private boolean trackingIncluded;
        private boolean signatureRequired;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentTerms {
        
        @NotNull(message = "Payment terms days is required")
        @Min(value = 0, message = "Payment terms must be non-negative")
        private Integer paymentTermsDays;
        
        @NotBlank(message = "Payment method is required")
        private String paymentMethod; // BANK_TRANSFER, CREDIT_CARD, CHECK, DIRECT_DEBIT
        
        @NotBlank(message = "Billing frequency is required")
        private String billingFrequency; // WEEKLY, MONTHLY, QUARTERLY
        
        private BigDecimal creditLimit;
        private BigDecimal securityDeposit;
        private BigDecimal earlyPaymentDiscountRate;
        private Integer earlyPaymentDiscountDays;
        private BigDecimal latePaymentPenaltyRate;
        private Integer gracePeriodDays;
        private boolean autoPayEnabled;
        private String invoiceDeliveryMethod;
        private String billingContact;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceLevelAgreement {
        
        @NotBlank(message = "Delivery timeframe is required")
        private String standardDeliveryTimeframe;
        
        @NotNull(message = "On-time delivery rate is required")
        @DecimalMin(value = "0.0", message = "Delivery rate must be non-negative")
        @DecimalMax(value = "100.0", message = "Delivery rate cannot exceed 100%")
        private BigDecimal guaranteedOnTimeDeliveryRate;
        
        @NotBlank(message = "Customer service response time is required")
        private String customerServiceResponseTime;
        
        private String trackingAndVisibility;
        private String claimsProcessingTime;
        private String performanceReporting;
        private List<String> escalationProcedures;
        private BigDecimal serviceFailurePenalty;
        private String disputeResolutionProcess;
        private String dedicatedAccountManager;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolumeDiscount {
        
        @NotNull(message = "Minimum volume is required")
        @Min(value = 1, message = "Minimum volume must be at least 1")
        private Integer minimumVolume;
        
        @NotNull(message = "Maximum volume is required")
        private Integer maximumVolume;
        
        @NotBlank(message = "Volume unit is required")
        private String volumeUnit; // PACKAGES_PER_MONTH, KG_PER_MONTH, SHIPMENTS_PER_MONTH
        
        @NotNull(message = "Discount rate is required")
        @DecimalMin(value = "0.0", message = "Discount rate must be non-negative")
        @DecimalMax(value = "100.0", message = "Discount rate cannot exceed 100%")
        private BigDecimal discountRate;
        
        @NotBlank(message = "Discount type is required")
        private String discountType; // PERCENTAGE, FIXED_AMOUNT
        
        private String applicableServices;
        private String validityPeriod;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceAddOn {
        
        @NotBlank(message = "Add-on name is required")
        private String addOnName;
        
        @NotBlank(message = "Add-on description is required")
        private String description;
        
        @NotNull(message = "Add-on price is required")
        @DecimalMin(value = "0.0", message = "Add-on price must be non-negative")
        private BigDecimal price;
        
        @NotBlank(message = "Pricing type is required")
        private String pricingType; // PER_SHIPMENT, MONTHLY_FEE, ONE_TIME
        
        private boolean included;
        private boolean optional;
        private String availabilityConditions;
    }
}
