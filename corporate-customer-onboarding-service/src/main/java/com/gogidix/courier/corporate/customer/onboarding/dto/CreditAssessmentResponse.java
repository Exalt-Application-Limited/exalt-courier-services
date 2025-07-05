package com.gogidix.courier.corporate.customer.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for credit assessment results.
 * Contains comprehensive credit evaluation information for corporate customers.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditAssessmentResponse {
    
    private UUID assessmentId;
    private String applicationReferenceId;
    private String companyName;
    private String assessmentStatus;
    private CreditDecision creditDecision;
    private CreditScore creditScore;
    private RiskAssessment riskAssessment;
    private List<AssessmentFactor> assessmentFactors;
    private RecommendedTerms recommendedTerms;
    private LocalDateTime assessmentDate;
    private LocalDateTime validUntil;
    private String assessorId;
    private String assessorNotes;
    private List<ConditionalRequirement> conditionalRequirements;
    private AppealProcess appealProcess;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditDecision {
        private String decision; // APPROVED, REJECTED, CONDITIONAL, UNDER_REVIEW
        private BigDecimal approvedCreditLimit;
        private String decisionReason;
        private List<String> decisionFactors;
        private String decisionCode;
        private boolean manualReviewRequired;
        private LocalDateTime decisionDate;
        private String decisionMaker;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditScore {
        private Integer score;
        private String scoreRange;
        private String scoreCategory;
        private String scoringModel;
        private LocalDateTime scoreDate;
        private Map<String, Integer> scoreComponents;
        private List<String> positiveFactors;
        private List<String> negativeFactors;
        private String scoreInterpretation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessment {
        private String riskLevel; // LOW, MEDIUM, HIGH, VERY_HIGH
        private String riskCategory;
        private Double riskScore;
        private List<String> riskFactors;
        private String industryRiskProfile;
        private String geographicRisk;
        private String financialRisk;
        private String operationalRisk;
        private String reputationalRisk;
        private String mitigationRecommendations;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssessmentFactor {
        private String factorName;
        private String category;
        private String impact; // POSITIVE, NEGATIVE, NEUTRAL
        private Integer weight;
        private String description;
        private String value;
        private String benchmark;
        private String interpretation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedTerms {
        private BigDecimal creditLimit;
        private Integer paymentTermsDays;
        private BigDecimal interestRate;
        private BigDecimal securityDeposit;
        private String paymentFrequency;
        private boolean personalGuaranteeRequired;
        private boolean collateralRequired;
        private String collateralType;
        private BigDecimal collateralValue;
        private List<String> covenants;
        private String reviewFrequency;
        private LocalDateTime termValidUntil;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionalRequirement {
        private String requirementType;
        private String description;
        private boolean mandatory;
        private LocalDateTime dueDate;
        private String status;
        private List<String> acceptableDocuments;
        private String verificationMethod;
        private String completionInstructions;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppealProcess {
        private boolean appealable;
        private String appealProcedure;
        private LocalDateTime appealDeadline;
        private String appealContactEmail;
        private String appealContactPhone;
        private List<String> requiredAppealDocuments;
        private String appealProcessingTime;
        private BigDecimal appealFee;
    }
}
