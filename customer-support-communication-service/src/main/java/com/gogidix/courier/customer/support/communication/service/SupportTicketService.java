package com.gogidix.courier.customer.support.communication.service;

import com.gogidix.courier.customer.support.communication.dto.*;
import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import com.gogidix.courier.customer.support.communication.model.SupportTicket;
import com.gogidix.courier.customer.support.communication.model.TicketMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for support ticket management and communication workflows.
 * 
 * Handles ticket lifecycle, agent assignment, SLA monitoring, and real-time
 * communication between customers and support agents.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
public interface SupportTicketService {

    // ========== TICKET MANAGEMENT ==========

    /**
     * Create a new support ticket from customer request.
     */
    SupportTicketResponse createTicket(CreateTicketRequest request);

    /**
     * Update an existing support ticket.
     */
    SupportTicketResponse updateTicket(UUID ticketId, UpdateTicketRequest request);

    /**
     * Get ticket by ID with full details.
     */
    Optional<SupportTicketResponse> getTicket(UUID ticketId);

    /**
     * Get ticket by reference ID.
     */
    Optional<SupportTicketResponse> getTicketByReference(String ticketReferenceId);

    /**
     * Get tickets for a specific customer.
     */
    Page<SupportTicketResponse> getCustomerTickets(String customerId, Pageable pageable);

    /**
     * Search tickets with comprehensive filtering.
     */
    Page<SupportTicketResponse> searchTickets(TicketSearchRequest request, Pageable pageable);

    // ========== STATUS MANAGEMENT ==========

    /**
     * Change ticket status with validation and history tracking.
     */
    SupportTicketResponse changeTicketStatus(UUID ticketId, TicketStatus newStatus, 
                                           String reason, String changedBy);

    /**
     * Assign ticket to agent.
     */
    SupportTicketResponse assignTicket(UUID ticketId, String agentId, String assignedBy);

    /**
     * Reassign ticket to different agent.
     */
    SupportTicketResponse reassignTicket(UUID ticketId, String newAgentId, 
                                       String reason, String assignedBy);

    /**
     * Escalate ticket to higher level support.
     */
    SupportTicketResponse escalateTicket(UUID ticketId, String escalatedTo, 
                                       String reason, String escalatedBy);

    /**
     * Close ticket with resolution notes.
     */
    SupportTicketResponse closeTicket(UUID ticketId, String resolutionNotes, String closedBy);

    /**
     * Reopen a closed ticket.
     */
    SupportTicketResponse reopenTicket(UUID ticketId, String reason, String reopenedBy);

    // ========== MESSAGE MANAGEMENT ==========

    /**
     * Add message to ticket conversation.
     */
    TicketMessageResponse addMessage(UUID ticketId, AddMessageRequest request);

    /**
     * Get messages for a ticket with pagination.
     */
    Page<TicketMessageResponse> getTicketMessages(UUID ticketId, Pageable pageable);

    /**
     * Mark message as read.
     */
    void markMessageAsRead(UUID messageId, String readBy);

    /**
     * Edit an existing message.
     */
    TicketMessageResponse editMessage(UUID messageId, String newContent, 
                                    String editReason, String editedBy);

    // ========== AGENT OPERATIONS ==========

    /**
     * Get tickets assigned to specific agent.
     */
    Page<SupportTicketResponse> getAgentTickets(String agentId, Pageable pageable);

    /**
     * Get agent's active workload.
     */
    AgentWorkloadResponse getAgentWorkload(String agentId);

    /**
     * Auto-assign ticket to available agent based on workload and skills.
     */
    SupportTicketResponse autoAssignTicket(UUID ticketId);

    /**
     * Get unassigned tickets for manual assignment.
     */
    List<SupportTicketResponse> getUnassignedTickets();

    // ========== SLA AND MONITORING ==========

    /**
     * Get overdue tickets requiring immediate attention.
     */
    List<SupportTicketResponse> getOverdueTickets();

    /**
     * Get tickets requiring first response.
     */
    List<SupportTicketResponse> getTicketsRequiringFirstResponse();

    /**
     * Get tickets needing escalation based on age and priority.
     */
    List<SupportTicketResponse> getTicketsNeedingEscalation();

    /**
     * Get tickets with stale customer responses.
     */
    List<SupportTicketResponse> getTicketsWithStaleCustomerResponse(int hours);

    /**
     * Process SLA monitoring and automated actions.
     */
    void processSLAMonitoring();

    // ========== ANALYTICS AND REPORTING ==========

    /**
     * Get dashboard summary statistics.
     */
    DashboardSummaryResponse getDashboardSummary();

    /**
     * Get ticket statistics for date range.
     */
    TicketStatisticsResponse getTicketStatistics(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get agent performance metrics.
     */
    List<AgentPerformanceResponse> getAgentPerformanceMetrics(LocalDateTime startDate, 
                                                             LocalDateTime endDate);

    /**
     * Get tickets by status distribution.
     */
    List<StatusDistributionResponse> getStatusDistribution();

    /**
     * Get average response times by category.
     */
    List<CategoryMetricsResponse> getResponseTimesByCategory();

    // ========== BULK OPERATIONS ==========

    /**
     * Bulk update ticket status.
     */
    BulkOperationResponse bulkUpdateStatus(List<UUID> ticketIds, TicketStatus newStatus, 
                                         String reason, String updatedBy);

    /**
     * Bulk assign tickets to agent.
     */
    BulkOperationResponse bulkAssignTickets(List<UUID> ticketIds, String agentId, 
                                          String assignedBy);

    /**
     * Bulk close tickets.
     */
    BulkOperationResponse bulkCloseTickets(List<UUID> ticketIds, String resolutionNotes, 
                                         String closedBy);

    // ========== AUTOMATED ACTIONS ==========

    /**
     * Process auto-close for resolved tickets.
     */
    void processAutoClose();

    /**
     * Send automated response based on ticket category.
     */
    void sendAutomatedResponse(UUID ticketId);

    /**
     * Process ticket priority updates based on content analysis.
     */
    void updateTicketPriority(UUID ticketId);

    /**
     * Generate suggested responses based on ticket content and knowledge base.
     */
    List<SuggestedResponseDto> getSuggestedResponses(UUID ticketId);

    // ========== NOTIFICATION MANAGEMENT ==========

    /**
     * Send real-time notification to customer.
     */
    void notifyCustomer(UUID ticketId, String message, String channel);

    /**
     * Send real-time notification to agent.
     */
    void notifyAgent(String agentId, String message, String ticketId);

    /**
     * Send escalation notification to supervisors.
     */
    void notifyEscalation(UUID ticketId, String escalatedTo, String reason);

    // ========== VALIDATION AND UTILITIES ==========

    /**
     * Validate ticket status transition.
     */
    boolean canTransitionStatus(TicketStatus currentStatus, TicketStatus newStatus);

    /**
     * Generate unique ticket reference ID.
     */
    String generateTicketReferenceId();

    /**
     * Calculate ticket due date based on priority and creation time.
     */
    LocalDateTime calculateDueDate(TicketPriority priority, LocalDateTime createdAt);

    /**
     * Determine optimal agent for ticket assignment.
     */
    Optional<String> findOptimalAgent(TicketCategory category, TicketPriority priority);
}