package com.gogidix.courier.corporate.customer.onboarding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for KYB (Know Your Business) verification initiation.
 * Contains all necessary information for corporate customer KYB process.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KybInitiationResponse {
    
    private UUID kybId;
    private String applicationReferenceId;
    private String companyName;
    private String companyRegistrationNumber;
    private String kybStatus;
    private String kybProviderSessionId;
    private List<RequiredBusinessDocument> requiredDocuments;
    private String verificationUrl;
    private LocalDateTime expiresAt;
    private String nextSteps;
    private List<String> supportedCountries;
    private EstimatedTimeline estimatedTimeline;
    private ContactInformation supportContact;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequiredBusinessDocument {
        private String documentType;
        private String documentName;
        private String description;
        private boolean mandatory;
        private List<String> acceptedFormats;
        private String maxFileSize;
        private String validityRequirement;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstimatedTimeline {
        private String documentReview;
        private String businessVerification;
        private String complianceCheck;
        private String totalProcessTime;
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
