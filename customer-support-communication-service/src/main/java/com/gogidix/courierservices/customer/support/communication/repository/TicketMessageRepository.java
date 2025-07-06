package com.gogidix.courierservices.customer.support.communication.repository;

import com.gogidix.courierservices.customer.support.communication.model.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TicketMessage entity
 */
@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    
    List<TicketMessage> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
    
    List<TicketMessage> findByTicketIdAndIsInternalFalseOrderByCreatedAtAsc(Long ticketId);
    
    List<TicketMessage> findBySenderIdOrderByCreatedAtDesc(Long senderId);
    
    @Query("SELECT m FROM TicketMessage m WHERE m.ticketId = :ticketId AND m.type = :type ORDER BY m.createdAt ASC")
    List<TicketMessage> findByTicketIdAndType(@Param("ticketId") Long ticketId, 
                                             @Param("type") TicketMessage.MessageType type);
    
    @Query("SELECT COUNT(m) FROM TicketMessage m WHERE m.ticketId = :ticketId AND m.type = 'CUSTOMER_MESSAGE'")
    Long countCustomerMessagesByTicketId(@Param("ticketId") Long ticketId);
    
    @Query("SELECT COUNT(m) FROM TicketMessage m WHERE m.ticketId = :ticketId AND m.type = 'AGENT_RESPONSE'")
    Long countAgentResponsesByTicketId(@Param("ticketId") Long ticketId);
}