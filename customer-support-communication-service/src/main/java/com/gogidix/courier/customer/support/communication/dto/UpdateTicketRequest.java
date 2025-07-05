package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing support ticket.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketRequest {

    @Email(message = "Customer email must be valid")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;

    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;

    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private TicketCategory category;

    private TicketPriority priority;

    private TicketStatus status;

    @Size(max = 50, message = "Assigned agent ID must not exceed 50 characters")
    private String assignedAgentId;

    @Size(max = 100, message = "Assigned agent name must not exceed 100 characters")
    private String assignedAgentName;

    @Size(max = 30, message = "Assigned team must not exceed 30 characters")
    private String assignedTeam;

    @Size(max = 50, message = "Escalated to must not exceed 50 characters")
    private String escalatedTo;

    @Size(max = 1000, message = "Resolution notes must not exceed 1000 characters")
    private String resolutionNotes;

    @Size(max = 1000, message = "Internal notes must not exceed 1000 characters")
    private String internalNotes;

    private Boolean isUrgent;

    private Boolean requiresCallback;

    private Boolean isPublic;

    private Boolean autoCloseEnabled;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    @Size(max = 500, message = "Update reason must not exceed 500 characters")
    private String updateReason;

    @Size(max = 50, message = "Updated by must not exceed 50 characters")
    private String updatedBy;
}