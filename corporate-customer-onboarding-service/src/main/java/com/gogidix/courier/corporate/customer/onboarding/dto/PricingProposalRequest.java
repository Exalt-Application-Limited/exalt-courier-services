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
 * Request DTO for generating pricing proposals for corporate customers.
 * Contains business requirements and shipping preferences for customized pricing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingProposalRequest {
    
    @NotBlank(message = "Application reference ID is required")
    private String applicationReferenceId;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @Valid
    @NotNull(message = "Shipping requirements are required")
    private ShippingRequirements shippingRequirements;
    
    @Valid
    @NotNull(message = "Volume projections are required")
    private VolumeProjections volumeProjections;
    
    @Valid
    private List<ServiceRequirement> serviceRequirements;
    
    @Valid
    private GeographicCoverage geographicCoverage;
    
    @Valid
    private SpecialRequirements specialRequirements;
    
    @NotNull(message = "Proposal valid until date is required")
    @Future(message = "Proposal valid until date must be in the future")
    private LocalDate proposalValidUntil;
    
    private String competitorBenchmark;
    private String budgetRange;
    private String currentLogisticsProvider;
    private String switchingMotivation;
    private String additionalComments;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingRequirements {
        
        @NotEmpty(message = "Service types are required")
        private List<String> serviceTypes; // STANDARD, EXPRESS, OVERNIGHT, SAME_DAY
        
        @NotEmpty(message = "Package types are required")
        private List<String> packageTypes; // ENVELOPE, SMALL_PACKAGE, LARGE_PACKAGE, PALLET
        
        @NotNull(message = "Average package weight is required")
        @DecimalMin(value = "0.1", message = "Average package weight must be at least 0.1 kg")
        private BigDecimal averagePackageWeight;
        
        @NotNull(message = "Maximum package weight is required")
        @DecimalMin(value = "0.1", message = "Maximum package weight must be at least 0.1 kg")
        private BigDecimal maximumPackageWeight;
        
        @NotNull(message = "Average package dimensions are required")
        private PackageDimensions averagePackageDimensions;
        
        private PackageDimensions maximumPackageDimensions;
        
        private List<String> specialHandlingRequirements;
        private boolean temperatureControlled;
        private boolean fragileItems;
        private boolean hazardousMaterials;
        private boolean highValueItems;
        private boolean signatureRequired;
        private boolean deliveryConfirmation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PackageDimensions {
        
        @NotNull(message = "Length is required")
        @DecimalMin(value = "0.1", message = "Length must be at least 0.1 cm")
        private BigDecimal length;
        
        @NotNull(message = "Width is required")
        @DecimalMin(value = "0.1", message = "Width must be at least 0.1 cm")
        private BigDecimal width;
        
        @NotNull(message = "Height is required")
        @DecimalMin(value = "0.1", message = "Height must be at least 0.1 cm")
        private BigDecimal height;
        
        @NotBlank(message = "Unit is required")
        private String unit; // CM, INCH
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VolumeProjections {
        
        @NotNull(message = "Monthly package volume is required")
        @Min(value = 1, message = "Monthly package volume must be at least 1")
        private Integer monthlyPackageVolume;
        
        @NotNull(message = "Peak monthly volume is required")
        @Min(value = 1, message = "Peak monthly volume must be at least 1")
        private Integer peakMonthlyVolume;
        
        @NotNull(message = "Annual volume projection is required")
        @Min(value = 12, message = "Annual volume must be at least 12")
        private Integer annualVolumeProjection;
        
        private List<String> peakSeasons;
        private BigDecimal volumeGrowthRate;
        private String volumePattern; // CONSISTENT, SEASONAL, CYCLICAL, IRREGULAR
        private List<MonthlyBreakdown> monthlyBreakdown;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthlyBreakdown {
        
        @NotBlank(message = "Month is required")
        private String month;
        
        @NotNull(message = "Projected volume is required")
        @Min(value = 0, message = "Projected volume must be non-negative")
        private Integer projectedVolume;
        
        private String notes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceRequirement {
        
        @NotBlank(message = "Service name is required")
        private String serviceName;
        
        @NotBlank(message = "Service description is required")
        private String description;
        
        @NotNull(message = "Service priority is required")
        private String priority; // ESSENTIAL, IMPORTANT, NICE_TO_HAVE
        
        private Integer volumePercentage;
        private String deliveryTimeframe;
        private boolean availabilityRequired247;
        private List<String> specificRequirements;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeographicCoverage {
        
        @NotEmpty(message = "Delivery areas are required")
        private List<String> primaryDeliveryAreas;
        
        private List<String> secondaryDeliveryAreas;
        private List<String> internationalDestinations;
        private boolean domesticDeliveryOnly;
        private boolean internationalDeliveryRequired;
        private List<String> excludedAreas;
        private String coveragePreference; // URBAN_ONLY, URBAN_SUBURBAN, FULL_COVERAGE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialRequirements {
        
        private boolean dedicatedAccountManager;
        private boolean customReporting;
        private boolean apiIntegration;
        private boolean warehousePickup;
        private boolean scheduledPickups;
        private boolean returnServices;
        private boolean packagingServices;
        private boolean labelingServices;
        private boolean inventoryManagement;
        private List<String> complianceRequirements;
        private List<String> industrySpecificNeeds;
        private String serviceLevel; // BASIC, STANDARD, PREMIUM, ENTERPRISE
    }
}
