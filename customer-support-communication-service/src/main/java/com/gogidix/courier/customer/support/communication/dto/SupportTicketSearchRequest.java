package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for searching support tickets with various filters.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketSearchRequest {

    // Text search filters
    @Size(max = 100, message = "Ticket reference ID must not exceed 100 characters")
    private String ticketReferenceId;

    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    private String customerId;

    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;

    @Size(max = 200, message = "Search query must not exceed 200 characters")
    private String searchQuery; // Search in subject/description

    // Categorization filters
    private List<TicketCategory> categories;
    private List<TicketPriority> priorities;
    private List<TicketStatus> statuses;

    // Assignment filters
    @Size(max = 50, message = "Assigned agent ID must not exceed 50 characters")
    private String assignedAgentId;

    @Size(max = 30, message = "Assigned team must not exceed 30 characters")
    private String assignedTeam;

    private Boolean unassigned; // Show only unassigned tickets

    // Reference filters
    @Size(max = 50, message = "Shipment reference ID must not exceed 50 characters")
    private String shipmentReferenceId;

    @Size(max = 100, message = "Order reference ID must not exceed 100 characters")
    private String orderReferenceId;

    // Date range filters
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private LocalDateTime dueDateFrom;
    private LocalDateTime dueDateTo;
    private LocalDateTime resolvedFrom;
    private LocalDateTime resolvedTo;

    // Flag filters
    private Boolean isUrgent;
    private Boolean requiresCallback;
    private Boolean isOverdue;
    private Boolean needsEscalation;
    private Boolean hasRecentCustomerResponse;
    private Boolean isWithinResponseSLA;

    // Customer satisfaction filters
    @Min(value = 1, message = "Minimum rating must be at least 1")
    @Max(value = 5, message = "Maximum rating must be at most 5")
    private Integer minCustomerRating;

    @Min(value = 1, message = "Maximum rating must be at least 1")
    @Max(value = 5, message = "Maximum rating must be at most 5")
    private Integer maxCustomerRating;

    // Source and tags
    @Size(max = 200, message = "Source must not exceed 200 characters")
    private String source;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    // Pagination and sorting
    @Min(value = 0, message = "Page must be non-negative")
    private Integer page = 0;

    @Min(value = 1, message = "Size must be at least 1")
    @Max(value = 100, message = "Size must not exceed 100")
    private Integer size = 20;

    @Size(max = 50, message = "Sort field must not exceed 50 characters")
    private String sortField = "createdAt";

    private String sortDirection = "DESC"; // ASC or DESC

    // Include related data flags
    private Boolean includeMessages = false;
    private Boolean includeAttachments = false;
    private Boolean includeAgentDetails = false;

    /**
     * Check if any search filters are applied.
     */
    public boolean hasFilters() {
        return ticketReferenceId != null || customerId != null || customerEmail != null ||
               searchQuery != null || (categories != null && !categories.isEmpty()) ||
               (priorities != null && !priorities.isEmpty()) || (statuses != null && !statuses.isEmpty()) ||
               assignedAgentId != null || assignedTeam != null || unassigned != null ||
               shipmentReferenceId != null || orderReferenceId != null ||
               createdFrom != null || createdTo != null || dueDateFrom != null || dueDateTo != null ||
               resolvedFrom != null || resolvedTo != null ||
               isUrgent != null || requiresCallback != null || isOverdue != null ||
               needsEscalation != null || hasRecentCustomerResponse != null || isWithinResponseSLA != null ||
               minCustomerRating != null || maxCustomerRating != null ||
               source != null || tags != null;
    }

    /**
     * Validate date ranges.
     */
    public boolean isValidDateRanges() {
        boolean validCreatedRange = createdFrom == null || createdTo == null || !createdFrom.isAfter(createdTo);
        boolean validDueDateRange = dueDateFrom == null || dueDateTo == null || !dueDateFrom.isAfter(dueDateTo);
        boolean validResolvedRange = resolvedFrom == null || resolvedTo == null || !resolvedFrom.isAfter(resolvedTo);
        
        return validCreatedRange && validDueDateRange && validResolvedRange;
    }
}