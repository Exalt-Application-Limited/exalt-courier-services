package com.gogidix.courier.customer.support.communication.controller;

import com.gogidix.courier.customer.support.communication.dto.*;
import com.gogidix.courier.customer.support.communication.enums.TicketStatus;
import com.gogidix.courier.customer.support.communication.service.SupportTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for support ticket management operations.
 * 
 * Provides endpoints for ticket CRUD operations, status management,
 * assignment workflows, and customer interactions.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Tag(name = "Support Tickets", description = "Customer support ticket management API")
@Slf4j
@RestController
@RequestMapping("/api/v1/support/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    // ========== TICKET MANAGEMENT ==========

    @Operation(summary = "Create new support ticket", 
               description = "Creates a new customer support ticket with automatic categorization and assignment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Ticket created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<SupportTicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request) {
        
        log.info("Creating support ticket for customer: {}", request.getCustomerId());
        
        try {
            SupportTicketResponse response = supportTicketService.createTicket(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating ticket: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create ticket: " + e.getMessage());
        }
    }

    @Operation(summary = "Get ticket by ID", 
               description = "Retrieves complete ticket information including messages and attachments")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ticket found"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{ticketId}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> getTicket(
            @Parameter(description = "Unique ticket identifier") 
            @PathVariable UUID ticketId) {
        
        log.info("Retrieving ticket: {}", ticketId);
        
        return supportTicketService.getTicket(ticketId)
                .map(ticket -> ResponseEntity.ok(ticket))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get ticket by reference ID", 
               description = "Retrieves ticket using customer-friendly reference number")
    @GetMapping("/reference/{ticketReferenceId}")
    public ResponseEntity<SupportTicketResponse> getTicketByReference(
            @Parameter(description = "Customer-facing ticket reference ID") 
            @PathVariable String ticketReferenceId) {
        
        log.info("Retrieving ticket by reference: {}", ticketReferenceId);
        
        return supportTicketService.getTicketByReference(ticketReferenceId)
                .map(ticket -> ResponseEntity.ok(ticket))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update ticket", 
               description = "Updates ticket information, status, assignment, or other properties")
    @PutMapping("/{ticketId}")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> updateTicket(
            @PathVariable UUID ticketId,
            @Valid @RequestBody UpdateTicketRequest request) {
        
        log.info("Updating ticket: {}", ticketId);
        
        try {
            SupportTicketResponse response = supportTicketService.updateTicket(ticketId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error updating ticket {}: {}", ticketId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Search tickets", 
               description = "Advanced ticket search with filtering, sorting, and pagination")
    @PostMapping("/search")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Page<SupportTicketResponse>> searchTickets(
            @RequestBody TicketSearchRequest searchRequest,
            Pageable pageable) {
        
        log.info("Searching tickets with criteria: {}", searchRequest);
        
        Page<SupportTicketResponse> tickets = supportTicketService.searchTickets(searchRequest, pageable);
        return ResponseEntity.ok(tickets);
    }

    // ========== CUSTOMER OPERATIONS ==========

    @Operation(summary = "Get customer tickets", 
               description = "Retrieves all tickets for a specific customer with pagination")
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('CUSTOMER') and #customerId == authentication.name or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Page<SupportTicketResponse>> getCustomerTickets(
            @Parameter(description = "Customer identifier") 
            @PathVariable String customerId,
            Pageable pageable) {
        
        log.info("Retrieving tickets for customer: {}", customerId);
        
        Page<SupportTicketResponse> tickets = supportTicketService.getCustomerTickets(customerId, pageable);
        return ResponseEntity.ok(tickets);
    }

    // ========== STATUS MANAGEMENT ==========

    @Operation(summary = "Change ticket status", 
               description = "Updates ticket status with validation and audit trail")
    @PatchMapping("/{ticketId}/status")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> changeTicketStatus(
            @PathVariable UUID ticketId,
            @Parameter(description = "New ticket status") 
            @RequestParam TicketStatus status,
            @Parameter(description = "Reason for status change") 
            @RequestParam String reason,
            @Parameter(description = "Agent making the change") 
            @RequestParam String changedBy) {
        
        log.info("Changing ticket {} status to {}", ticketId, status);
        
        try {
            SupportTicketResponse response = supportTicketService.changeTicketStatus(ticketId, status, reason, changedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error changing ticket status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Assign ticket to agent", 
               description = "Assigns ticket to a specific support agent")
    @PatchMapping("/{ticketId}/assign")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> assignTicket(
            @PathVariable UUID ticketId,
            @Parameter(description = "Agent ID to assign ticket to") 
            @RequestParam String agentId,
            @Parameter(description = "Agent making the assignment") 
            @RequestParam String assignedBy) {
        
        log.info("Assigning ticket {} to agent {}", ticketId, agentId);
        
        try {
            SupportTicketResponse response = supportTicketService.assignTicket(ticketId, agentId, assignedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error assigning ticket: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Escalate ticket", 
               description = "Escalates ticket to higher level support or management")
    @PatchMapping("/{ticketId}/escalate")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> escalateTicket(
            @PathVariable UUID ticketId,
            @Parameter(description = "Escalation target (agent, team, or department)") 
            @RequestParam String escalatedTo,
            @Parameter(description = "Reason for escalation") 
            @RequestParam String reason,
            @Parameter(description = "Agent performing escalation") 
            @RequestParam String escalatedBy) {
        
        log.info("Escalating ticket {} to {}", ticketId, escalatedTo);
        
        try {
            SupportTicketResponse response = supportTicketService.escalateTicket(ticketId, escalatedTo, reason, escalatedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error escalating ticket: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Close ticket", 
               description = "Closes ticket with resolution notes and triggers customer feedback")
    @PatchMapping("/{ticketId}/close")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> closeTicket(
            @PathVariable UUID ticketId,
            @Parameter(description = "Resolution details and notes") 
            @RequestParam String resolutionNotes,
            @Parameter(description = "Agent closing the ticket") 
            @RequestParam String closedBy) {
        
        log.info("Closing ticket: {}", ticketId);
        
        try {
            SupportTicketResponse response = supportTicketService.closeTicket(ticketId, resolutionNotes, closedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error closing ticket: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Reopen ticket", 
               description = "Reopens a previously closed ticket")
    @PatchMapping("/{ticketId}/reopen")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> reopenTicket(
            @PathVariable UUID ticketId,
            @Parameter(description = "Reason for reopening") 
            @RequestParam String reason,
            @Parameter(description = "User reopening the ticket") 
            @RequestParam String reopenedBy) {
        
        log.info("Reopening ticket: {}", ticketId);
        
        try {
            SupportTicketResponse response = supportTicketService.reopenTicket(ticketId, reason, reopenedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error reopening ticket: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ========== AGENT OPERATIONS ==========

    @Operation(summary = "Get agent tickets", 
               description = "Retrieves all tickets assigned to a specific agent")
    @GetMapping("/agent/{agentId}")
    @PreAuthorize("hasRole('AGENT') and #agentId == authentication.name or hasRole('ADMIN')")
    public ResponseEntity<Page<SupportTicketResponse>> getAgentTickets(
            @Parameter(description = "Agent identifier") 
            @PathVariable String agentId,
            Pageable pageable) {
        
        log.info("Retrieving tickets for agent: {}", agentId);
        
        Page<SupportTicketResponse> tickets = supportTicketService.getAgentTickets(agentId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Get unassigned tickets", 
               description = "Retrieves tickets that need agent assignment")
    @GetMapping("/unassigned")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<List<SupportTicketResponse>> getUnassignedTickets() {
        
        log.info("Retrieving unassigned tickets");
        
        List<SupportTicketResponse> tickets = supportTicketService.getUnassignedTickets();
        return ResponseEntity.ok(tickets);
    }

    // ========== MONITORING AND ALERTS ==========

    @Operation(summary = "Get overdue tickets", 
               description = "Retrieves tickets that have exceeded their SLA deadlines")
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<List<SupportTicketResponse>> getOverdueTickets() {
        
        log.info("Retrieving overdue tickets");
        
        List<SupportTicketResponse> tickets = supportTicketService.getOverdueTickets();
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Get tickets requiring first response", 
               description = "Retrieves new tickets that need initial agent response")
    @GetMapping("/pending-first-response")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<List<SupportTicketResponse>> getTicketsRequiringFirstResponse() {
        
        log.info("Retrieving tickets requiring first response");
        
        List<SupportTicketResponse> tickets = supportTicketService.getTicketsRequiringFirstResponse();
        return ResponseEntity.ok(tickets);
    }

    @Operation(summary = "Get tickets needing escalation", 
               description = "Retrieves tickets that should be escalated based on age and priority")
    @GetMapping("/needs-escalation")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<List<SupportTicketResponse>> getTicketsNeedingEscalation() {
        
        log.info("Retrieving tickets needing escalation");
        
        List<SupportTicketResponse> tickets = supportTicketService.getTicketsNeedingEscalation();
        return ResponseEntity.ok(tickets);
    }

    // ========== UTILITY ENDPOINTS ==========

    @Operation(summary = "Generate ticket reference ID", 
               description = "Generates a unique customer-friendly ticket reference number")
    @GetMapping("/generate-reference")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<String> generateTicketReferenceId() {
        
        String referenceId = supportTicketService.generateTicketReferenceId();
        return ResponseEntity.ok(referenceId);
    }

    // ========== ERROR HANDLING ==========

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception in ticket controller: {}", e.getMessage(), e);
        
        ErrorResponse error = ErrorResponse.builder()
            .error("TICKET_OPERATION_FAILED")
            .message(e.getMessage())
            .timestamp(java.time.LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid argument in ticket controller: {}", e.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
            .error("INVALID_REQUEST")
            .message(e.getMessage())
            .timestamp(java.time.LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Error response DTO for API errors.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String message;
        private java.time.LocalDateTime timestamp;
    }
}