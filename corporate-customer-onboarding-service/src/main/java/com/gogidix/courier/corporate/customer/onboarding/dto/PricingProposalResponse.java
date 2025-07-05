package com.gogidix.courier.corporate.customer.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for pricing proposals generated for corporate customers.
 * Contains comprehensive pricing structure and commercial terms.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingProposalResponse {
    
    private UUID proposalId;
    private String applicationReferenceId;
    private String companyName;
    private String proposalStatus;
    private LocalDateTime proposalDate;
    private LocalDate validUntil;
    private PricingStructure pricingStructure;
    private List<ServicePricing> servicePricing;
    private List<VolumeDiscount> volumeDiscounts;
    private PaymentTerms paymentTerms;
    private ServiceLevelAgreement serviceLevelAgreement;
    private CostSummary costSummary;
    private List<ValueAddedService> valueAddedServices;
    private CompetitiveComparison competitiveComparison;
    private ImplementationPlan implementationPlan;
    private String proposalNotes;
    private ContactInformation salesContact;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingStructure {
        private String pricingModel;
        private BigDecimal baseRate;
        private String rateUnit;
        private BigDecimal minimumCharge;
        private BigDecimal fuelSurchargeRate;
        private boolean fuelSurchargeVariable;
        private BigDecimal insuranceFeeRate;
        private BigDecimal handlingFeeRate;
        private String effectiveDate;
        private String priceAdjustmentPolicy;
        private String contractTerm;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicePricing {
        private String serviceType;
        private String serviceName;
        private String description;
        private BigDecimal rate;
        private String rateUnit;
        private String deliveryTimeframe;
        private BigDecimal minimumCharge;
        private BigDecimal surchargeRate;
        private List<String> includedFeatures;
        private List<String> availableAddOns;
        private String availability;
        private String coverageArea;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolumeDiscount {
        private String tierName;
        private Integer minimumVolume;
        private Integer maximumVolume;
        private String volumeUnit;
        private BigDecimal discountRate;
        private String discountType;
        private BigDecimal discountAmount;
        private List<String> applicableServices;
        private String qualificationPeriod;
        private String resetFrequency;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentTerms {
        private Integer paymentTermsDays;
        private String paymentMethod;
        private String billingFrequency;
        private BigDecimal creditLimit;
        private BigDecimal securityDeposit;
        private boolean securityDepositWaived;
        private BigDecimal earlyPaymentDiscountRate;
        private Integer earlyPaymentDiscountDays;
        private BigDecimal latePaymentPenaltyRate;
        private Integer gracePeriodDays;
        private boolean autoPayAvailable;
        private String invoiceDeliveryMethod;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceLevelAgreement {
        private String standardDeliveryTimeframe;
        private BigDecimal onTimeDeliveryGuarantee;
        private String customerServiceResponseTime;
        private String trackingVisibility;
        private String claimsProcessingTime;
        private String performanceReporting;
        private List<String> escalationProcedures;
        private BigDecimal serviceFailurePenalty;
        private String disputeResolutionProcess;
        private boolean dedicatedAccountManager;
        private String accountManagerContact;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostSummary {
        private BigDecimal estimatedMonthlyCost;
        private BigDecimal estimatedAnnualCost;
        private BigDecimal averageCostPerShipment;
        private BigDecimal totalSavingsVsStandardRates;
        private BigDecimal savingsPercentage;
        private CostBreakdown costBreakdown;
        private String basedOnVolume;
        private String assumptions;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostBreakdown {
        private BigDecimal baseShippingCosts;
        private BigDecimal fuelSurcharges;
        private BigDecimal insuranceFees;
        private BigDecimal handlingFees;
        private BigDecimal valueAddedServices;
        private BigDecimal volumeDiscounts;
        private BigDecimal netTotal;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValueAddedService {
        private String serviceName;
        private String description;
        private BigDecimal price;
        private String pricingType;
        private boolean included;
        private boolean recommended;
        private String benefits;
        private String availability;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitiveComparison {
        private List<CompetitorComparison> competitorComparisons;
        private List<String> competitiveAdvantages;
        private String valueProposition;
        private String differentiators;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitorComparison {
        private String competitorName;
        private BigDecimal competitorRate;
        private BigDecimal ourRate;
        private BigDecimal savings;
        private String serviceComparison;
        private List<String> advantages;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImplementationPlan {
        private LocalDate proposedStartDate;
        private String implementationTimeline;
        private List<ImplementationStep> implementationSteps;
        private String onboardingSupport;
        private String trainingProvided;
        private String integrationSupport;
        private String goLiveSupport;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImplementationStep {
        private String stepName;
        private String description;
        private LocalDate targetDate;
        private String responsible;
        private List<String> requirements;
        private String deliverables;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInformation {
        private String salesRepName;
        private String salesRepEmail;
        private String salesRepPhone;
        private String salesManagerName;
        private String salesManagerEmail;
        private String salesManagerPhone;
        private String supportEmail;
        private String supportPhone;
    }
}
