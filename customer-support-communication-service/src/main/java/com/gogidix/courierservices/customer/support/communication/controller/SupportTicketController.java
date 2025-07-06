package com.gogidix.courierservices.customer.support.communication.controller;

import com.gogidix.courierservices.customer.support.communication.dto.AddMessageRequest;
import com.gogidix.courierservices.customer.support.communication.dto.CreateTicketRequest;
import com.gogidix.courierservices.customer.support.communication.model.SupportTicket;
import com.gogidix.courierservices.customer.support.communication.model.TicketMessage;
import com.gogidix.courierservices.customer.support.communication.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for support ticket management
 */
@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping("/tickets")
    public ResponseEntity<SupportTicket> createTicket(@Valid @RequestBody CreateTicketRequest request) {
        SupportTicket ticket = supportTicketService.createTicket(request);
        return new ResponseEntity<>(ticket, HttpStatus.CREATED);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<SupportTicket> getTicket(@PathVariable Long id) {
        SupportTicket ticket = supportTicketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/tickets/number/{ticketNumber}")
    public ResponseEntity<SupportTicket> getTicketByNumber(@PathVariable String ticketNumber) {
        SupportTicket ticket = supportTicketService.getTicketByNumber(ticketNumber);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/tickets/customer/{customerId}")
    public ResponseEntity<List<SupportTicket>> getCustomerTickets(@PathVariable Long customerId) {
        List<SupportTicket> tickets = supportTicketService.getTicketsByCustomerId(customerId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/tickets/customer/{customerId}/paged")
    public ResponseEntity<Page<SupportTicket>> getCustomerTicketsPaged(
            @PathVariable Long customerId, 
            Pageable pageable) {
        Page<SupportTicket> tickets = supportTicketService.getTicketsByCustomerId(customerId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/tickets/agent/{agentId}")
    public ResponseEntity<List<SupportTicket>> getAgentTickets(@PathVariable Long agentId) {
        List<SupportTicket> tickets = supportTicketService.getTicketsByAgentId(agentId);
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<SupportTicket> updateTicketStatus(
            @PathVariable Long id, 
            @RequestParam SupportTicket.TicketStatus status) {
        SupportTicket ticket = supportTicketService.updateTicketStatus(id, status);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/tickets/{id}/assign")
    public ResponseEntity<SupportTicket> assignTicket(
            @PathVariable Long id, 
            @RequestParam Long agentId,
            @RequestParam String agentName) {
        SupportTicket ticket = supportTicketService.assignTicketToAgent(id, agentId, agentName);
        return ResponseEntity.ok(ticket);
    }

    @PutMapping("/tickets/{id}/priority")
    public ResponseEntity<SupportTicket> updateTicketPriority(
            @PathVariable Long id, 
            @RequestParam SupportTicket.TicketPriority priority) {
        SupportTicket ticket = supportTicketService.updateTicketPriority(id, priority);
        return ResponseEntity.ok(ticket);
    }

    @PostMapping("/tickets/messages")
    public ResponseEntity<TicketMessage> addMessage(@Valid @RequestBody AddMessageRequest request) {
        TicketMessage message = supportTicketService.addMessage(request);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/tickets/{ticketId}/messages")
    public ResponseEntity<List<TicketMessage>> getTicketMessages(@PathVariable Long ticketId) {
        List<TicketMessage> messages = supportTicketService.getTicketMessages(ticketId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/tickets/{ticketId}/messages/public")
    public ResponseEntity<List<TicketMessage>> getPublicTicketMessages(@PathVariable Long ticketId) {
        List<TicketMessage> messages = supportTicketService.getPublicTicketMessages(ticketId);
        return ResponseEntity.ok(messages);
    }
}