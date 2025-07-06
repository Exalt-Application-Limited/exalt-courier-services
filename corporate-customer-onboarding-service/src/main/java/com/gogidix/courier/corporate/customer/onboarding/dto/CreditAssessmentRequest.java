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
 * Request DTO for initiating credit assessment for corporate customers.
 * Contains financial and business information required for credit evaluation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditAssessmentRequest {
    
    @NotBlank(message = "Application reference ID is required")
    private String applicationReferenceId;
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    @NotBlank(message = "Company registration number is required")
    private String companyRegistrationNumber;
    
    @Valid
    @NotNull(message = "Financial information is required")
    private FinancialInformation financialInformation;
    
    @Valid
    @NotNull(message = "Business information is required")
    private BusinessInformation businessInformation;
    
    @Valid
    private List<BankReference> bankReferences;
    
    @Valid
    private List<TradeReference> tradeReferences;
    
    @NotNull(message = "Requested credit limit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Credit limit must be positive")
    private BigDecimal requestedCreditLimit;
    
    @NotBlank(message = "Credit purpose is required")
    private String creditPurpose;
    
    @Valid
    private PaymentTermsPreference paymentTermsPreference;
    
    private String additionalInformation;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialInformation {
        
        @NotNull(message = "Annual revenue is required")
        @DecimalMin(value = "0.0", message = "Annual revenue must be non-negative")
        private BigDecimal annualRevenue;
        
        @NotNull(message = "Annual profit is required")
        private BigDecimal annualProfit;
        
        @NotNull(message = "Total assets value is required")
        @DecimalMin(value = "0.0", message = "Total assets must be non-negative")
        private BigDecimal totalAssets;
        
        @NotNull(message = "Total liabilities value is required")
        @DecimalMin(value = "0.0", message = "Total liabilities must be non-negative")
        private BigDecimal totalLiabilities;
        
        @NotNull(message = "Cash flow is required")
        private BigDecimal monthlyAverageCashFlow;
        
        @NotNull(message = "Financial year end date is required")
        private LocalDate financialYearEnd;
        
        private boolean auditedFinancials;
        private String auditorFirm;
        private String creditRating;
        private List<String> existingCreditFacilities;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessInformation {
        
        @NotNull(message = "Years in business is required")
        @Min(value = 0, message = "Years in business must be non-negative")
        private Integer yearsInBusiness;
        
        @NotBlank(message = "Industry sector is required")
        private String industrySector;
        
        @NotBlank(message = "Business type is required")
        private String businessType;
        
        @NotNull(message = "Number of employees is required")
        @Min(value = 1, message = "Number of employees must be at least 1")
        private Integer numberOfEmployees;
        
        @NotBlank(message = "Primary business location is required")
        private String primaryBusinessLocation;
        
        private List<String> operatingLocations;
        private String businessModel;
        private List<String> majorCustomers;
        private List<String> majorSuppliers;
        private String competitivePosition;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BankReference {
        
        @NotBlank(message = "Bank name is required")
        private String bankName;
        
        @NotBlank(message = "Account type is required")
        private String accountType;
        
        @NotNull(message = "Account opening date is required")
        private LocalDate accountOpeningDate;
        
        @NotNull(message = "Average balance is required")
        @DecimalMin(value = "0.0", message = "Average balance must be non-negative")
        private BigDecimal averageBalance;
        
        @NotBlank(message = "Contact person is required")
        private String contactPerson;
        
        @Email(message = "Valid email is required")
        private String contactEmail;
        
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Valid phone number is required")
        private String contactPhone;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TradeReference {
        
        @NotBlank(message = "Company name is required")
        private String companyName;
        
        @NotBlank(message = "Contact person is required")
        private String contactPerson;
        
        @Email(message = "Valid email is required")
        private String contactEmail;
        
        @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Valid phone number is required")
        private String contactPhone;
        
        @NotBlank(message = "Trade relationship type is required")
        private String relationshipType;
        
        @NotNull(message = "Relationship duration is required")
        @Min(value = 0, message = "Relationship duration must be non-negative")
        private Integer relationshipDurationMonths;
        
        @NotNull(message = "Average monthly transaction volume is required")
        @DecimalMin(value = "0.0", message = "Transaction volume must be non-negative")
        private BigDecimal averageMonthlyTransactionVolume;
        
        @NotBlank(message = "Payment terms are required")
        private String paymentTerms;
        
        private String paymentHistory;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentTermsPreference {
        
        @NotNull(message = "Preferred payment terms are required")
        @Min(value = 0, message = "Payment terms must be non-negative")
        private Integer preferredPaymentTermsDays;
        
        private BigDecimal earlyPaymentDiscountRate;
        private Integer earlyPaymentDiscountDays;
        private BigDecimal latePaymentPenaltyRate;
        private String preferredPaymentMethod;
        private boolean autoPaySetup;
        private String billingFrequency;
    }
}
