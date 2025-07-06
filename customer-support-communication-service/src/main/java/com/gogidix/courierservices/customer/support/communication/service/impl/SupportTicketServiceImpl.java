package com.gogidix.courierservices.customer.support.communication.service.impl;

import com.gogidix.courierservices.customer.support.communication.dto.AddMessageRequest;
import com.gogidix.courierservices.customer.support.communication.dto.CreateTicketRequest;
import com.gogidix.courierservices.customer.support.communication.model.SupportTicket;
import com.gogidix.courierservices.customer.support.communication.model.TicketMessage;
import com.gogidix.courierservices.customer.support.communication.repository.SupportTicketRepository;
import com.gogidix.courierservices.customer.support.communication.repository.TicketMessageRepository;
import com.gogidix.courierservices.customer.support.communication.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of SupportTicketService
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;

    @Override
    public SupportTicket createTicket(CreateTicketRequest request) {
        log.info("Creating support ticket for customer: {}", request.getCustomerEmail());
        
        String ticketNumber = generateTicketNumber();
        
        SupportTicket ticket = SupportTicket.builder()
                .ticketNumber(ticketNumber)
                .customerId(request.getCustomerId())
                .customerEmail(request.getCustomerEmail())
                .subject(request.getSubject())
                .description(request.getDescription())
                .priority(request.getPriority())
                .category(request.getCategory())
                .status(SupportTicket.TicketStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        SupportTicket savedTicket = ticketRepository.save(ticket);
        
        // Create initial message with the ticket description
        TicketMessage initialMessage = TicketMessage.builder()
                .ticketId(savedTicket.getId())
                .message(request.getDescription())
                .type(TicketMessage.MessageType.CUSTOMER_MESSAGE)
                .senderId(request.getCustomerId())
                .senderName("Customer")
                .senderEmail(request.getCustomerEmail())
                .isInternal(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        messageRepository.save(initialMessage);
        
        log.info("Created support ticket: {}", ticketNumber);
        return savedTicket;
    }

    @Override
    @Transactional(readOnly = true)
    public SupportTicket getTicketById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public SupportTicket getTicketByNumber(String ticketNumber) {
        return ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Support ticket not found with number: " + ticketNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicket> getTicketsByCustomerId(Long customerId) {
        return ticketRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupportTicket> getTicketsByCustomerId(Long customerId, Pageable pageable) {
        return ticketRepository.findByCustomerId(customerId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicket> getTicketsByAgentId(Long agentId) {
        return ticketRepository.findByAssignedAgentId(agentId);
    }

    @Override
    public SupportTicket updateTicketStatus(Long ticketId, SupportTicket.TicketStatus status) {
        log.info("Updating ticket {} status to {}", ticketId, status);
        
        SupportTicket ticket = getTicketById(ticketId);
        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        if (status == SupportTicket.TicketStatus.RESOLVED || status == SupportTicket.TicketStatus.CLOSED) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        
        return ticketRepository.save(ticket);
    }

    @Override
    public SupportTicket assignTicketToAgent(Long ticketId, Long agentId, String agentName) {
        log.info("Assigning ticket {} to agent: {} ({})", ticketId, agentName, agentId);
        
        SupportTicket ticket = getTicketById(ticketId);
        ticket.setAssignedAgentId(agentId);
        ticket.setAssignedAgentName(agentName);
        ticket.setStatus(SupportTicket.TicketStatus.IN_PROGRESS);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        return ticketRepository.save(ticket);
    }

    @Override
    public SupportTicket updateTicketPriority(Long ticketId, SupportTicket.TicketPriority priority) {
        log.info("Updating ticket {} priority to {}", ticketId, priority);
        
        SupportTicket ticket = getTicketById(ticketId);
        ticket.setPriority(priority);
        ticket.setUpdatedAt(LocalDateTime.now());
        
        return ticketRepository.save(ticket);
    }

    @Override
    public TicketMessage addMessage(AddMessageRequest request) {
        log.info("Adding message to ticket: {}", request.getTicketId());
        
        SupportTicket ticket = getTicketById(request.getTicketId());
        
        TicketMessage message = TicketMessage.builder()
                .ticketId(request.getTicketId())
                .message(request.getMessage())
                .type(request.getType())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderEmail(request.getSenderEmail())
                .isInternal(request.getIsInternal())
                .createdAt(LocalDateTime.now())
                .build();
        
        TicketMessage savedMessage = messageRepository.save(message);
        
        // Update ticket's last activity time and set first response time if this is the first agent response
        ticket.setUpdatedAt(LocalDateTime.now());
        if (request.getType() == TicketMessage.MessageType.AGENT_RESPONSE && ticket.getFirstResponseAt() == null) {
            ticket.setFirstResponseAt(LocalDateTime.now());
        }
        ticketRepository.save(ticket);
        
        return savedMessage;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketMessage> getTicketMessages(Long ticketId) {
        return messageRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketMessage> getPublicTicketMessages(Long ticketId) {
        return messageRepository.findByTicketIdAndIsInternalFalseOrderByCreatedAtAsc(ticketId);
    }

    private String generateTicketNumber() {
        return "TKT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}