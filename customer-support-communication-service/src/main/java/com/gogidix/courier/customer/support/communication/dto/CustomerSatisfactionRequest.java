package com.gogidix.courier.customer.support.communication.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Request DTO for submitting customer satisfaction feedback.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSatisfactionRequest {

    @NotNull(message = "Ticket ID is required")
    private UUID ticketId;

    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    private String customerId;

    // Overall satisfaction rating
    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer overallRating;

    // Specific aspect ratings
    @Min(value = 1, message = "Response time rating must be at least 1")
    @Max(value = 5, message = "Response time rating must be at most 5")
    private Integer responseTimeRating;

    @Min(value = 1, message = "Resolution quality rating must be at least 1")
    @Max(value = 5, message = "Resolution quality rating must be at most 5")
    private Integer resolutionQualityRating;

    @Min(value = 1, message = "Agent professionalism rating must be at least 1")
    @Max(value = 5, message = "Agent professionalism rating must be at most 5")
    private Integer agentProfessionalismRating;

    @Min(value = 1, message = "Communication clarity rating must be at least 1")
    @Max(value = 5, message = "Communication clarity rating must be at most 5")
    private Integer communicationClarityRating;

    @Min(value = 1, message = "Ease of process rating must be at least 1")
    @Max(value = 5, message = "Ease of process rating must be at most 5")
    private Integer easeOfProcessRating;

    // Text feedback
    @Size(max = 2000, message = "General feedback must not exceed 2000 characters")
    private String generalFeedback;

    @Size(max = 1000, message = "What went well must not exceed 1000 characters")
    private String whatWentWell;

    @Size(max = 1000, message = "What could be improved must not exceed 1000 characters")
    private String whatCouldBeImproved;

    @Size(max = 1000, message = "Additional suggestions must not exceed 1000 characters")
    private String additionalSuggestions;

    // Specific questions
    private Boolean wouldRecommendService;
    private Boolean issueFullyResolved;
    private Boolean agentWasHelpful;
    private Boolean processWasEasy;
    private Boolean wouldUseServiceAgain;

    // Effort scores
    @Min(value = 1, message = "Customer effort score must be at least 1")
    @Max(value = 7, message = "Customer effort score must be at most 7")
    private Integer customerEffortScore; // 1 = Very Easy, 7 = Very Difficult

    // Net Promoter Score
    @Min(value = 0, message = "NPS score must be at least 0")
    @Max(value = 10, message = "NPS score must be at most 10")
    private Integer netPromoterScore; // 0-6 Detractors, 7-8 Passives, 9-10 Promoters

    // Agent-specific feedback
    @Size(max = 50, message = "Agent ID must not exceed 50 characters")
    private String primaryAgentId;

    @Size(max = 100, message = "Agent name must not exceed 100 characters")
    private String primaryAgentName;

    @Size(max = 1000, message = "Agent feedback must not exceed 1000 characters")
    private String agentSpecificFeedback;

    private Boolean agentExceededExpectations;
    private Boolean agentMetExpectations;

    // Contact preferences for follow-up
    private Boolean allowFollowUpContact = false;
    private String preferredFollowUpMethod; // EMAIL, PHONE, SMS, NONE

    @Email(message = "Follow-up email must be valid")
    @Size(max = 100, message = "Follow-up email must not exceed 100 characters")
    private String followUpEmail;

    @Size(max = 20, message = "Follow-up phone must not exceed 20 characters")
    private String followUpPhone;

    // Service improvement feedback
    private List<String> suggestedServiceImprovements;
    private List<String> mostValuedServiceAspects;
    private List<String> leastSatisfyingAspects;

    // Comparative feedback
    private Boolean comparedToOtherServices;
    private String competitorComparison; // BETTER, SAME, WORSE
    private String competitorComparisonDetails;

    // Channel-specific feedback
    @Size(max = 50, message = "Communication channel must not exceed 50 characters")
    private String primaryCommunicationChannel;

    private Integer channelSatisfactionRating;
    private String channelFeedback;

    // Resolution timeline feedback
    private Boolean resolutionTimeAcceptable;
    private String expectedResolutionTime;
    private String actualResolutionTime;

    // Custom feedback fields
    private Map<String, String> customResponses;

    // Survey metadata
    @Size(max = 100, message = "Survey version must not exceed 100 characters")
    private String surveyVersion;

    @Size(max = 200, message = "Survey source must not exceed 200 characters")
    private String surveySource; // EMAIL, SMS, PORTAL, PHONE, CHAT

    private Boolean surveyCompleted = true;
    private Integer surveyCompletionTimeSeconds;

    /**
     * Nested class for categorized feedback.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategorizedFeedback {
        private String category;
        private Integer rating;
        private String feedback;
        private List<String> specificIssues;
        private List<String> improvements;
    }

    /**
     * Calculate average rating across all specific aspects.
     */
    public Double calculateAverageAspectRating() {
        List<Integer> ratings = List.of(
            responseTimeRating,
            resolutionQualityRating,
            agentProfessionalismRating,
            communicationClarityRating,
            easeOfProcessRating
        );

        return ratings.stream()
                     .filter(rating -> rating != null)
                     .mapToInt(Integer::intValue)
                     .average()
                     .orElse(0.0);
    }

    /**
     * Determine customer sentiment based on ratings.
     */
    public String getCustomerSentiment() {
        if (overallRating == null) return "UNKNOWN";
        
        return switch (overallRating) {
            case 5 -> "VERY_POSITIVE";
            case 4 -> "POSITIVE";
            case 3 -> "NEUTRAL";
            case 2 -> "NEGATIVE";
            case 1 -> "VERY_NEGATIVE";
            default -> "UNKNOWN";
        };
    }

    /**
     * Determine NPS category.
     */
    public String getNPSCategory() {
        if (netPromoterScore == null) return "UNKNOWN";
        
        if (netPromoterScore >= 9) return "PROMOTER";
        if (netPromoterScore >= 7) return "PASSIVE";
        return "DETRACTOR";
    }

    /**
     * Check if this is a positive feedback.
     */
    public boolean isPositiveFeedback() {
        return overallRating != null && overallRating >= 4;
    }

    /**
     * Check if this feedback indicates service issues.
     */
    public boolean indicatesServiceIssues() {
        return overallRating != null && overallRating <= 2 ||
               Boolean.FALSE.equals(issueFullyResolved) ||
               Boolean.FALSE.equals(agentWasHelpful) ||
               (customerEffortScore != null && customerEffortScore >= 6) ||
               (netPromoterScore != null && netPromoterScore <= 6);
    }

    /**
     * Get feedback priority level for management attention.
     */
    public String getFeedbackPriority() {
        if (indicatesServiceIssues()) {
            return "HIGH";
        }
        
        if (overallRating != null && overallRating == 3) {
            return "MEDIUM";
        }
        
        return "LOW";
    }

    /**
     * Check if customer allows follow-up contact.
     */
    public boolean allowsFollowUp() {
        return Boolean.TRUE.equals(allowFollowUpContact) && 
               preferredFollowUpMethod != null && 
               !"NONE".equals(preferredFollowUpMethod);
    }

    /**
     * Validate feedback completeness.
     */
    public boolean isComplete() {
        return overallRating != null && 
               ticketId != null && 
               customerId != null &&
               (generalFeedback != null || 
                responseTimeRating != null || 
                resolutionQualityRating != null);
    }

    /**
     * Get key improvement areas based on low ratings.
     */
    public List<String> getImprovementAreas() {
        List<String> areas = new java.util.ArrayList<>();
        
        if (responseTimeRating != null && responseTimeRating <= 2) {
            areas.add("Response Time");
        }
        if (resolutionQualityRating != null && resolutionQualityRating <= 2) {
            areas.add("Resolution Quality");
        }
        if (agentProfessionalismRating != null && agentProfessionalismRating <= 2) {
            areas.add("Agent Professionalism");
        }
        if (communicationClarityRating != null && communicationClarityRating <= 2) {
            areas.add("Communication Clarity");
        }
        if (easeOfProcessRating != null && easeOfProcessRating <= 2) {
            areas.add("Process Simplicity");
        }
        
        return areas;
    }
}