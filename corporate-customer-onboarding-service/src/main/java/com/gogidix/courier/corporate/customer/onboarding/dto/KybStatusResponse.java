package com.gogidix.courier.corporate.customer.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for KYB (Know Your Business) verification status.
 * Provides comprehensive status information for corporate verification process.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KybStatusResponse {
    
    private UUID kybId;
    private String applicationReferenceId;
    private String companyName;
    private String overallStatus;
    private double completionPercentage;
    private List<VerificationStep> verificationSteps;
    private List<DocumentStatus> documentStatuses;
    private RiskAssessment riskAssessment;
    private ComplianceChecks complianceChecks;
    private LocalDateTime lastUpdated;
    private LocalDateTime estimatedCompletion;
    private List<ActionRequired> actionsRequired;
    private String statusMessage;
    private ContactInformation supportContact;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificationStep {
        private String stepName;
        private String status;
        private String description;
        private boolean completed;
        private LocalDateTime completedAt;
        private String notes;
        private List<String> issues;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentStatus {
        private String documentType;
        private String status;
        private LocalDateTime uploadedAt;
        private LocalDateTime reviewedAt;
        private String reviewNotes;
        private List<String> rejectionReasons;
        private boolean resubmissionRequired;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskAssessment {
        private String riskLevel;
        private String riskScore;
        private List<String> riskFactors;
        private String assessmentStatus;
        private LocalDateTime assessedAt;
        private String assessorNotes;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComplianceChecks {
        private boolean amlScreening;
        private boolean sanctionsCheck;
        private boolean pepScreening;
        private boolean adverseMediaCheck;
        private boolean regulatoryCompliance;
        private Map<String, String> checkResults;
        private LocalDateTime lastChecked;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActionRequired {
        private String actionType;
        private String description;
        private String priority;
        private LocalDateTime dueDate;
        private List<String> instructions;
        private String contactEmail;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContactInformation {
        private String email;
        private String phone;
        private String liveChatUrl;
        private String supportTicketUrl;
    }
}
