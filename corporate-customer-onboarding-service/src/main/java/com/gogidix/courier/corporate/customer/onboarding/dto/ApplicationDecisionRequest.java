package com.gogidix.courier.corporate.customer.onboarding.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for making decisions on corporate customer onboarding applications.
 * Used for approval, rejection, or conditional approval of applications.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDecisionRequest {
    
    @NotBlank(message = "Decision is required")
    @Pattern(regexp = "^(APPROVED|REJECTED|CONDITIONAL_APPROVAL|REQUIRES_ADDITIONAL_INFO)$", 
             message = "Decision must be APPROVED, REJECTED, CONDITIONAL_APPROVAL, or REQUIRES_ADDITIONAL_INFO")
    private String decision;
    
    @NotBlank(message = "Decision reason is required")
    @Size(min = 10, max = 1000, message = "Decision reason must be between 10 and 1000 characters")
    private String decisionReason;
    
    @NotBlank(message = "Decision maker ID is required")
    private String decisionMakerId;
    
    @NotBlank(message = "Decision maker name is required")
    private String decisionMakerName;
    
    @NotBlank(message = "Decision maker role is required")
    private String decisionMakerRole;
    
    private LocalDateTime decisionTimestamp;
    
    private List<String> decisionFactors;
    
    private List<ConditionalRequirement> conditionalRequirements;
    
    private List<String> additionalDocumentsRequired;
    
    private LocalDateTime responseDeadline;
    
    private String escalationLevel;
    
    private String reviewerComments;
    
    private String nextSteps;
    
    private String internalNotes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionalRequirement {
        
        @NotBlank(message = "Requirement type is required")
        private String requirementType;
        
        @NotBlank(message = "Requirement description is required")
        private String description;
        
        @NotNull(message = "Mandatory flag is required")
        private Boolean mandatory;
        
        private LocalDateTime dueDate;
        
        private List<String> acceptableEvidence;
        
        private String verificationMethod;
        
        private String contactForClarification;
    }
}
