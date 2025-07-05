package com.gogidix.courier.customer.support.communication.repository;

import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import com.gogidix.courier.customer.support.communication.model.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Support Ticket entities.
 * 
 * Provides comprehensive data access methods for customer support tickets
 * including complex queries for agent workload, SLA monitoring, and analytics.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    // ========== BASIC FINDERS ==========

    /**
     * Find ticket by unique reference ID.
     */
    Optional<SupportTicket> findByTicketReferenceId(String ticketReferenceId);

    /**
     * Find all tickets for a specific customer.
     */
    Page<SupportTicket> findByCustomerId(String customerId, Pageable pageable);

    /**
     * Find tickets by customer email.
     */
    Page<SupportTicket> findByCustomerEmail(String customerEmail, Pageable pageable);

    /**
     * Find tickets by shipment reference.
     */
    List<SupportTicket> findByShipmentReferenceId(String shipmentReferenceId);

    // ========== STATUS-BASED QUERIES ==========

    /**
     * Find tickets by status.
     */
    Page<SupportTicket> findByStatus(TicketStatus status, Pageable pageable);

    /**
     * Find tickets by multiple statuses.
     */
    Page<SupportTicket> findByStatusIn(List<TicketStatus> statuses, Pageable pageable);

    /**
     * Find active tickets (not closed or cancelled).
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.status NOT IN ('CLOSED', 'CANCELLED') ORDER BY t.priority ASC, t.createdAt ASC")
    Page<SupportTicket> findActiveTickets(Pageable pageable);

    /**
     * Count tickets by status.
     */
    long countByStatus(TicketStatus status);

    // ========== AGENT ASSIGNMENT QUERIES ==========

    /**
     * Find tickets assigned to specific agent.
     */
    Page<SupportTicket> findByAssignedAgentId(String agentId, Pageable pageable);

    /**
     * Find active tickets assigned to agent.
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.assignedAgentId = :agentId AND t.status NOT IN ('CLOSED', 'CANCELLED', 'RESOLVED') ORDER BY t.priority ASC")
    List<SupportTicket> findActiveTicketsByAgentId(@Param("agentId") String agentId);

    /**
     * Find tickets by assigned team.
     */
    Page<SupportTicket> findByAssignedTeam(String team, Pageable pageable);

    /**
     * Find unassigned tickets.
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.assignedAgentId IS NULL AND t.status IN ('OPEN', 'REOPENED') ORDER BY t.priority ASC, t.createdAt ASC")
    List<SupportTicket> findUnassignedTickets();

    /**
     * Count active tickets for agent.
     */
    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.assignedAgentId = :agentId AND t.status NOT IN ('CLOSED', 'CANCELLED', 'RESOLVED')")
    long countActiveTicketsByAgentId(@Param("agentId") String agentId);

    // ========== PRIORITY AND CATEGORY QUERIES ==========

    /**
     * Find tickets by priority.
     */
    Page<SupportTicket> findByPriority(TicketPriority priority, Pageable pageable);

    /**
     * Find tickets by category.
     */
    Page<SupportTicket> findByCategory(TicketCategory category, Pageable pageable);

    /**
     * Find high priority tickets requiring immediate attention.
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.priority IN ('CRITICAL', 'HIGH') AND t.status NOT IN ('CLOSED', 'CANCELLED', 'RESOLVED') ORDER BY t.priority ASC, t.createdAt ASC")
    List<SupportTicket> findHighPriorityTickets();

    // ========== SLA AND ESCALATION QUERIES ==========

    /**
     * Find overdue tickets (past due date).
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.dueDate < :currentTime AND t.status NOT IN ('CLOSED', 'CANCELLED', 'RESOLVED')")
    List<SupportTicket> findOverdueTickets(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find tickets requiring first response (no agent response yet).
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.firstResponseAt IS NULL AND t.status NOT IN ('DRAFT', 'CLOSED', 'CANCELLED') ORDER BY t.priority ASC, t.createdAt ASC")
    List<SupportTicket> findTicketsRequiringFirstResponse();

    /**
     * Find tickets needing escalation based on age and priority.
     */
    @Query("""
        SELECT t FROM SupportTicket t 
        WHERE t.status NOT IN ('CLOSED', 'CANCELLED', 'RESOLVED', 'ESCALATED') 
        AND (
            (t.priority = 'CRITICAL' AND t.createdAt < :criticalThreshold) OR
            (t.priority = 'HIGH' AND t.createdAt < :highThreshold) OR
            (t.priority = 'NORMAL' AND t.createdAt < :normalThreshold) OR
            (t.priority = 'LOW' AND t.createdAt < :lowThreshold)
        )
        ORDER BY t.priority ASC, t.createdAt ASC
    """)
    List<SupportTicket> findTicketsNeedingEscalation(
            @Param("criticalThreshold") LocalDateTime criticalThreshold,
            @Param("highThreshold") LocalDateTime highThreshold,
            @Param("normalThreshold") LocalDateTime normalThreshold,
            @Param("lowThreshold") LocalDateTime lowThreshold
    );

    /**
     * Find tickets pending customer response for too long.
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.status = 'PENDING_CUSTOMER' AND t.lastAgentResponseAt < :threshold")
    List<SupportTicket> findTicketsWithStaleCustomerResponse(@Param("threshold") LocalDateTime threshold);

    // ========== SEARCH AND FILTERING ==========

    /**
     * Search tickets with multiple criteria.
     */
    @Query("""
        SELECT t FROM SupportTicket t 
        WHERE (:searchTerm IS NULL OR 
               LOWER(t.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(t.ticketReferenceId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
               LOWER(t.customerEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:category IS NULL OR t.category = :category)
        AND (:priority IS NULL OR t.priority = :priority)
        AND (:status IS NULL OR t.status = :status)
        AND (:agentId IS NULL OR t.assignedAgentId = :agentId)
        AND (:customerId IS NULL OR t.customerId = :customerId)
        AND (:fromDate IS NULL OR t.createdAt >= :fromDate)
        AND (:toDate IS NULL OR t.createdAt <= :toDate)
        ORDER BY t.priority ASC, t.createdAt DESC
    """)
    Page<SupportTicket> searchTickets(
            @Param("searchTerm") String searchTerm,
            @Param("category") TicketCategory category,
            @Param("priority") TicketPriority priority,
            @Param("status") TicketStatus status,
            @Param("agentId") String agentId,
            @Param("customerId") String customerId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    // ========== TIME-BASED QUERIES ==========

    /**
     * Find tickets created in date range.
     */
    List<SupportTicket> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find tickets resolved in date range.
     */
    List<SupportTicket> findByResolvedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find tickets with recent customer activity.
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.lastCustomerResponseAt > :threshold ORDER BY t.lastCustomerResponseAt DESC")
    List<SupportTicket> findTicketsWithRecentCustomerActivity(@Param("threshold") LocalDateTime threshold);

    // ========== ANALYTICS QUERIES ==========

    /**
     * Get ticket statistics by status for date range.
     */
    @Query("""
        SELECT t.status, COUNT(t) 
        FROM SupportTicket t 
        WHERE t.createdAt BETWEEN :startDate AND :endDate 
        GROUP BY t.status
    """)
    List<Object[]> getTicketStatisticsByStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Get ticket distribution by category.
     */
    @Query("SELECT t.category, COUNT(t) FROM SupportTicket t GROUP BY t.category")
    List<Object[]> getTicketDistributionByCategory();

    /**
     * Get ticket distribution by priority.
     */
    @Query("SELECT t.priority, COUNT(t) FROM SupportTicket t GROUP BY t.priority")
    List<Object[]> getTicketDistributionByPriority();

    /**
     * Get average response time by category.
     */
    @Query("""
        SELECT t.category, 
               AVG(EXTRACT(EPOCH FROM (t.firstResponseAt - t.createdAt))/3600) as avgResponseHours
        FROM SupportTicket t 
        WHERE t.firstResponseAt IS NOT NULL
        GROUP BY t.category
    """)
    List<Object[]> getAverageResponseTimeByCategory();

    /**
     * Get average resolution time by priority.
     */
    @Query("""
        SELECT t.priority, 
               AVG(EXTRACT(EPOCH FROM (t.resolvedAt - t.createdAt))/3600) as avgResolutionHours
        FROM SupportTicket t 
        WHERE t.resolvedAt IS NOT NULL
        GROUP BY t.priority
    """)
    List<Object[]> getAverageResolutionTimeByPriority();

    /**
     * Get agent performance metrics.
     */
    @Query("""
        SELECT t.assignedAgentId, t.assignedAgentName,
               COUNT(t) as totalTickets,
               AVG(CASE WHEN t.firstResponseAt IS NOT NULL 
                   THEN EXTRACT(EPOCH FROM (t.firstResponseAt - t.createdAt))/3600 
                   ELSE NULL END) as avgResponseHours,
               AVG(CASE WHEN t.resolvedAt IS NOT NULL 
                   THEN EXTRACT(EPOCH FROM (t.resolvedAt - t.createdAt))/3600 
                   ELSE NULL END) as avgResolutionHours,
               AVG(t.customerSatisfactionRating) as avgSatisfaction
        FROM SupportTicket t 
        WHERE t.assignedAgentId IS NOT NULL
        AND t.createdAt BETWEEN :startDate AND :endDate
        GROUP BY t.assignedAgentId, t.assignedAgentName
    """)
    List<Object[]> getAgentPerformanceMetrics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // ========== DASHBOARD QUERIES ==========

    /**
     * Get dashboard summary for today.
     */
    @Query("""
        SELECT 
            COUNT(CASE WHEN t.status = 'OPEN' THEN 1 END) as openTickets,
            COUNT(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 END) as inProgressTickets,
            COUNT(CASE WHEN t.status = 'PENDING_CUSTOMER' THEN 1 END) as pendingCustomerTickets,
            COUNT(CASE WHEN t.status = 'RESOLVED' THEN 1 END) as resolvedTickets,
            COUNT(CASE WHEN t.priority = 'CRITICAL' AND t.status NOT IN ('CLOSED', 'CANCELLED') THEN 1 END) as criticalTickets,
            COUNT(CASE WHEN t.dueDate < :currentTime AND t.status NOT IN ('CLOSED', 'CANCELLED', 'RESOLVED') THEN 1 END) as overdueTickets
        FROM SupportTicket t
    """)
    Object[] getDashboardSummary(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find recently updated tickets for activity feed.
     */
    @Query("SELECT t FROM SupportTicket t WHERE t.updatedAt > :threshold ORDER BY t.updatedAt DESC")
    List<SupportTicket> findRecentlyUpdatedTickets(@Param("threshold") LocalDateTime threshold, Pageable pageable);
}