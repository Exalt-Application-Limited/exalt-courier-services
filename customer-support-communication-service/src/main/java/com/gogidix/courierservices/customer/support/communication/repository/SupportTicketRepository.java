package com.gogidix.courierservices.customer.support.communication.repository;

import com.gogidix.courierservices.customer.support.communication.model.SupportTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SupportTicket entity
 */
@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    
    Optional<SupportTicket> findByTicketNumber(String ticketNumber);
    
    List<SupportTicket> findByCustomerId(Long customerId);
    
    List<SupportTicket> findByCustomerEmail(String customerEmail);
    
    List<SupportTicket> findByAssignedAgentId(Long agentId);
    
    List<SupportTicket> findByStatus(SupportTicket.TicketStatus status);
    
    List<SupportTicket> findByPriority(SupportTicket.TicketPriority priority);
    
    List<SupportTicket> findByCategory(SupportTicket.TicketCategory category);
    
    Page<SupportTicket> findByCustomerId(Long customerId, Pageable pageable);
    
    Page<SupportTicket> findByAssignedAgentId(Long agentId, Pageable pageable);
    
    @Query("SELECT t FROM SupportTicket t WHERE t.status IN :statuses")
    List<SupportTicket> findByStatusIn(@Param("statuses") List<SupportTicket.TicketStatus> statuses);
    
    @Query("SELECT COUNT(t) FROM SupportTicket t WHERE t.customerId = :customerId AND t.status = :status")
    Long countByCustomerIdAndStatus(@Param("customerId") Long customerId, 
                                   @Param("status") SupportTicket.TicketStatus status);
}