package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.EscalationType;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for escalating support tickets.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscalationRequest {

    @NotNull(message = "Ticket ID is required")
    private UUID ticketId;

    @NotNull(message = "Escalation type is required")
    private EscalationType escalationType;

    @NotBlank(message = "Escalation reason is required")
    @Size(max = 1000, message = "Escalation reason must not exceed 1000 characters")
    private String escalationReason;

    @Size(max = 50, message = "Escalated by agent ID must not exceed 50 characters")
    private String escalatedByAgentId;

    @Size(max = 100, message = "Escalated by agent name must not exceed 100 characters")
    private String escalatedByAgentName;

    // Target escalation destination
    @Size(max = 50, message = "Escalated to agent ID must not exceed 50 characters")
    private String escalatedToAgentId;

    @Size(max = 100, message = "Escalated to agent name must not exceed 100 characters")
    private String escalatedToAgentName;

    @Size(max = 30, message = "Escalated to team must not exceed 30 characters")
    private String escalatedToTeam;

    @Size(max = 50, message = "Escalated to department must not exceed 50 characters")
    private String escalatedToDepartment;

    // Priority and urgency adjustments
    private TicketPriority newPriority;
    private Boolean markAsUrgent = false;

    // SLA adjustments
    private Boolean adjustSLA = false;
    private Integer newResponseTimeSLAHours;
    private Integer newResolutionTimeSLAHours;

    // Additional context
    @Size(max = 2000, message = "Additional notes must not exceed 2000 characters")
    private String additionalNotes;

    @Size(max = 500, message = "Internal comments must not exceed 500 characters")
    private String internalComments;

    // Escalation rules
    private Boolean followStandardEscalationRules = true;
    private Boolean skipEscalationLevels = false;
    private Integer targetEscalationLevel;

    // Customer notification
    private Boolean notifyCustomer = true;
    private String customerNotificationMessage;
    private Boolean useStandardNotificationTemplate = true;

    // Stakeholder notifications
    private List<String> additionalNotificationRecipients;
    private Boolean notifyManagement = false;
    private Boolean notifyQualityAssurance = false;

    // Escalation scheduling
    private Boolean isImmediate = true;
    private String scheduledEscalationDateTime; // ISO format if not immediate

    // Documentation requirements
    private Boolean requireEscalationDocumentation = false;
    private List<String> requiredDocumentTypes;

    // Escalation metadata
    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    @Size(max = 200, message = "Escalation source must not exceed 200 characters")
    private String escalationSource; // AGENT_MANUAL, AUTO_SLA_BREACH, CUSTOMER_REQUEST, QUALITY_ISSUE

    private Boolean createEscalationTask = true;
    private String escalationTaskDescription;

    // Resolution expectations
    @Size(max = 1000, message = "Expected resolution must not exceed 1000 characters")
    private String expectedResolution;

    private Integer expectedResolutionTimeHours;

    // Quality and tracking
    private Boolean trackEscalationMetrics = true;
    private Boolean requireManagerApproval = false;

    @Size(max = 100, message = "Approval request message must not exceed 100 characters")
    private String approvalRequestMessage;

    /**
     * Nested class for escalation validation rules.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EscalationValidationRule {
        private String ruleName;
        private String ruleDescription;
        private Boolean isRequired;
        private String validationMessage;
    }

    /**
     * Check if this escalation targets a specific agent.
     */
    public boolean hasTargetAgent() {
        return escalatedToAgentId != null && !escalatedToAgentId.trim().isEmpty();
    }

    /**
     * Check if this escalation targets a team.
     */
    public boolean hasTargetTeam() {
        return escalatedToTeam != null && !escalatedToTeam.trim().isEmpty();
    }

    /**
     * Check if this escalation requires manager approval.
     */
    public boolean requiresManagerApproval() {
        return Boolean.TRUE.equals(requireManagerApproval) || 
               EscalationType.URGENT.equals(escalationType) ||
               (newPriority != null && TicketPriority.CRITICAL.equals(newPriority));
    }

    /**
     * Check if SLA should be adjusted with escalation.
     */
    public boolean shouldAdjustSLA() {
        return Boolean.TRUE.equals(adjustSLA) && 
               (newResponseTimeSLAHours != null || newResolutionTimeSLAHours != null);
    }

    /**
     * Get escalation urgency level (1-5, 5 being highest).
     */
    public int getEscalationUrgencyLevel() {
        if (EscalationType.CRITICAL.equals(escalationType)) return 5;
        if (EscalationType.URGENT.equals(escalationType)) return 4;
        if (EscalationType.HIGH.equals(escalationType)) return 3;
        if (EscalationType.STANDARD.equals(escalationType)) return 2;
        return 1; // LOW
    }

    /**
     * Check if customer should be notified about escalation.
     */
    public boolean shouldNotifyCustomer() {
        return Boolean.TRUE.equals(notifyCustomer) && 
               !EscalationType.INTERNAL.equals(escalationType);
    }

    /**
     * Validate escalation request.
     */
    public boolean isValid() {
        // Must have either target agent, team, or department
        boolean hasTarget = hasTargetAgent() || hasTargetTeam() || 
                           (escalatedToDepartment != null && !escalatedToDepartment.trim().isEmpty());
        
        // If adjusting SLA, must provide new values
        boolean validSLA = !Boolean.TRUE.equals(adjustSLA) || 
                          (newResponseTimeSLAHours != null || newResolutionTimeSLAHours != null);
        
        return hasTarget && validSLA;
    }
}