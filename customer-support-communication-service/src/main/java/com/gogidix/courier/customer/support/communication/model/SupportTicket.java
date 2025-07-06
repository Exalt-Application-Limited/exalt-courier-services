package com.gogidix.courier.customer.support.communication.model;

import com.gogidix.courier.customer.support.communication.enums.*;
import com.gogidix.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a customer support ticket.
 * 
 * This entity tracks customer support requests, communications, and resolutions
 * for courier service customers. It extends BaseEntity to leverage shared audit
 * and UUID patterns.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "support_tickets", indexes = {
    @Index(name = "idx_ticket_reference_id", columnList = "ticket_reference_id"),
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_ticket_status", columnList = "status"),
    @Index(name = "idx_ticket_priority", columnList = "priority"),
    @Index(name = "idx_ticket_category", columnList = "category"),
    @Index(name = "idx_assigned_agent_id", columnList = "assigned_agent_id"),
    @Index(name = "idx_shipment_reference", columnList = "shipment_reference_id"),
    @Index(name = "idx_ticket_created_at", columnList = "created_at"),
    @Index(name = "idx_ticket_due_date", columnList = "due_date")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportTicket extends BaseEntity {

    @NotBlank(message = "Ticket reference ID is required")
    @Size(max = 50, message = "Ticket reference ID must not exceed 50 characters")
    @Column(name = "ticket_reference_id", unique = true, nullable = false, length = 50)
    private String ticketReferenceId;

    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @Email(message = "Customer email must be valid")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    @Column(name = "customer_name", length = 100)
    private String customerName;

    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    @Column(name = "customer_phone", length = 20)
    private String customerPhone;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    @Column(name = "subject", nullable = false, length = 200)
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private TicketCategory category;

    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    private TicketPriority priority;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status;

    @Size(max = 50, message = "Assigned agent ID must not exceed 50 characters")
    @Column(name = "assigned_agent_id", length = 50)
    private String assignedAgentId;

    @Size(max = 100, message = "Assigned agent name must not exceed 100 characters")
    @Column(name = "assigned_agent_name", length = 100)
    private String assignedAgentName;

    @Size(max = 30, message = "Assigned team must not exceed 30 characters")
    @Column(name = "assigned_team", length = 30)
    private String assignedTeam;

    @Size(max = 50, message = "Shipment reference ID must not exceed 50 characters")
    @Column(name = "shipment_reference_id", length = 50)
    private String shipmentReferenceId;

    @Size(max = 100, message = "Order reference ID must not exceed 100 characters")
    @Column(name = "order_reference_id", length = 100)
    private String orderReferenceId;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "first_response_at")
    private LocalDateTime firstResponseAt;

    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;

    @Size(max = 50, message = "Escalated to must not exceed 50 characters")
    @Column(name = "escalated_to", length = 50)
    private String escalatedTo;

    @Size(max = 1000, message = "Resolution notes must not exceed 1000 characters")
    @Column(name = "resolution_notes", length = 1000)
    private String resolutionNotes;

    @Size(max = 1000, message = "Internal notes must not exceed 1000 characters")
    @Column(name = "internal_notes", length = 1000)
    private String internalNotes;

    @Min(value = 1, message = "Customer satisfaction rating must be between 1 and 5")
    @Max(value = 5, message = "Customer satisfaction rating must be between 1 and 5")
    @Column(name = "customer_satisfaction_rating")
    private Integer customerSatisfactionRating;

    @Size(max = 1000, message = "Customer feedback must not exceed 1000 characters")
    @Column(name = "customer_feedback", length = 1000)
    private String customerFeedback;

    @Builder.Default
    @Column(name = "is_urgent", nullable = false)
    private Boolean isUrgent = false;

    @Builder.Default
    @Column(name = "requires_callback", nullable = false)
    private Boolean requiresCallback = false;

    @Builder.Default
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Builder.Default
    @Column(name = "auto_close_enabled", nullable = false)
    private Boolean autoCloseEnabled = true;

    @Column(name = "last_customer_response_at")
    private LocalDateTime lastCustomerResponseAt;

    @Column(name = "last_agent_response_at")
    private LocalDateTime lastAgentResponseAt;

    @Builder.Default
    @Column(name = "response_count", nullable = false)
    private Integer responseCount = 0;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    @Column(name = "tags", length = 500)
    private String tags;

    @Size(max = 200, message = "Source must not exceed 200 characters")
    @Column(name = "source", length = 200)
    private String source; // web, mobile, email, phone, chat

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketMessage> messages;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketAttachment> attachments;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketStatusHistory> statusHistory;

    // Business Logic Methods

    /**
     * Check if ticket is overdue based on priority SLA.
     */
    public boolean isOverdue() {
        if (dueDate == null || status.isCompleted()) {
            return false;
        }
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Calculate response time in hours.
     */
    public Long getResponseTimeHours() {
        if (firstResponseAt == null) {
            return null;
        }
        return java.time.Duration.between(getCreatedAt(), firstResponseAt).toHours();
    }

    /**
     * Calculate resolution time in hours.
     */
    public Long getResolutionTimeHours() {
        if (resolvedAt == null) {
            return null;
        }
        return java.time.Duration.between(getCreatedAt(), resolvedAt).toHours();
    }

    /**
     * Check if ticket needs escalation based on age and priority.
     */
    public boolean needsEscalation() {
        if (status.isCompleted() || status == TicketStatus.ESCALATED) {
            return false;
        }
        
        if (priority.requiresImmediateEscalation()) {
            return true;
        }
        
        long hoursOpen = java.time.Duration.between(getCreatedAt(), LocalDateTime.now()).toHours();
        return hoursOpen >= priority.getEscalationThresholdHours();
    }

    /**
     * Check if ticket has been responded to within SLA.
     */
    public boolean isWithinResponseSLA() {
        if (firstResponseAt != null) {
            return true; // Already responded
        }
        
        long hoursOpen = java.time.Duration.between(getCreatedAt(), LocalDateTime.now()).toHours();
        return hoursOpen <= priority.getResponseTimeHours();
    }

    /**
     * Get time remaining until SLA breach in hours.
     */
    public long getTimeToSLABreachHours() {
        if (firstResponseAt != null) {
            return 0; // SLA already met
        }
        
        long hoursOpen = java.time.Duration.between(getCreatedAt(), LocalDateTime.now()).toHours();
        return Math.max(0, priority.getResponseTimeHours() - hoursOpen);
    }

    /**
     * Check if customer has responded recently.
     */
    public boolean hasRecentCustomerResponse() {
        if (lastCustomerResponseAt == null) {
            return false;
        }
        
        long hoursSinceResponse = java.time.Duration.between(lastCustomerResponseAt, LocalDateTime.now()).toHours();
        return hoursSinceResponse <= 24;
    }

    /**
     * Generate summary for dashboard display.
     */
    public String generateSummary() {
        return String.format("Ticket %s: %s [%s] - %s (%s)", 
                ticketReferenceId, 
                subject.length() > 50 ? subject.substring(0, 47) + "..." : subject,
                category.getDisplayName(),
                status.getDescription(),
                priority.getDisplayName());
    }

    // Note: BaseEntity provides id, version, createdAt, updatedAt, createdBy, updatedBy fields
}