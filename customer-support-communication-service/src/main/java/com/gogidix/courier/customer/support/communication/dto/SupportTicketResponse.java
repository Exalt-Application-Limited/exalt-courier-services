package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for support ticket data.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketResponse {

    private UUID id;
    private String ticketReferenceId;
    
    // Customer information
    private String customerId;
    private String customerEmail;
    private String customerName;
    private String customerPhone;
    
    // Ticket details
    private String subject;
    private String description;
    private TicketCategory category;
    private TicketPriority priority;
    private TicketStatus status;
    
    // Assignment
    private String assignedAgentId;
    private String assignedAgentName;
    private String assignedTeam;
    
    // Related references
    private String shipmentReferenceId;
    private String orderReferenceId;
    
    // Timestamps
    private LocalDateTime dueDate;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private LocalDateTime firstResponseAt;
    private LocalDateTime escalatedAt;
    private String escalatedTo;
    
    // Resolution
    private String resolutionNotes;
    private String internalNotes;
    
    // Customer feedback
    private Integer customerSatisfactionRating;
    private String customerFeedback;
    
    // Flags
    private Boolean isUrgent;
    private Boolean requiresCallback;
    private Boolean isPublic;
    private Boolean autoCloseEnabled;
    
    // Communication tracking
    private LocalDateTime lastCustomerResponseAt;
    private LocalDateTime lastAgentResponseAt;
    private Integer responseCount;
    
    // Metadata
    private String tags;
    private String source;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    
    // Related data
    private List<TicketMessageResponse> recentMessages;
    private List<TicketAttachmentResponse> attachments;
    
    // Calculated fields
    private Long responseTimeHours;
    private Long resolutionTimeHours;
    private Long timeToSLABreachHours;
    private Boolean isOverdue;
    private Boolean needsEscalation;
    private Boolean isWithinResponseSLA;
    private Boolean hasRecentCustomerResponse;
    
    /**
     * Generate summary for display purposes.
     */
    public String generateSummary() {
        return String.format("Ticket %s: %s [%s] - %s (%s)", 
                ticketReferenceId, 
                subject != null && subject.length() > 50 ? subject.substring(0, 47) + "..." : subject,
                category != null ? category.getDisplayName() : "Unknown",
                status != null ? status.getDescription() : "Unknown",
                priority != null ? priority.getDisplayName() : "Unknown");
    }
}