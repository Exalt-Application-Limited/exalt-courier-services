package com.gogidix.courierservices.customer.support.communication.service;

import com.gogidix.courierservices.customer.support.communication.dto.AddMessageRequest;
import com.gogidix.courierservices.customer.support.communication.dto.CreateTicketRequest;
import com.gogidix.courierservices.customer.support.communication.model.SupportTicket;
import com.gogidix.courierservices.customer.support.communication.model.TicketMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for support ticket management
 */
public interface SupportTicketService {
    
    SupportTicket createTicket(CreateTicketRequest request);
    
    SupportTicket getTicketById(Long id);
    
    SupportTicket getTicketByNumber(String ticketNumber);
    
    List<SupportTicket> getTicketsByCustomerId(Long customerId);
    
    Page<SupportTicket> getTicketsByCustomerId(Long customerId, Pageable pageable);
    
    List<SupportTicket> getTicketsByAgentId(Long agentId);
    
    SupportTicket updateTicketStatus(Long ticketId, SupportTicket.TicketStatus status);
    
    SupportTicket assignTicketToAgent(Long ticketId, Long agentId, String agentName);
    
    SupportTicket updateTicketPriority(Long ticketId, SupportTicket.TicketPriority priority);
    
    TicketMessage addMessage(AddMessageRequest request);
    
    List<TicketMessage> getTicketMessages(Long ticketId);
    
    List<TicketMessage> getPublicTicketMessages(Long ticketId);
}