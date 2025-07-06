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

/**
 * Alias for SupportTicketSearchRequest to match service interface.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketSearchRequest {
    private String customerId;
    private String ticketReferenceId;
    private String searchQuery;
    private List<TicketCategory> categories;
    private List<TicketPriority> priorities;
    private List<TicketStatus> statuses;
    private String assignedAgentId;
    private LocalDateTime createdFrom;
    private LocalDateTime createdTo;
    private Boolean isOverdue;
    private Integer page = 0;
    private Integer size = 20;
}