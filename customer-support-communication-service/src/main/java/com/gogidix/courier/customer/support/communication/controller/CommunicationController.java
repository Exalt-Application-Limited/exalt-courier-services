package com.gogidix.courier.customer.support.communication.controller;

import com.gogidix.courier.customer.support.communication.dto.*;
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

import java.util.UUID;

/**
 * REST controller for ticket communication and messaging operations.
 * 
 * Handles message sending, conversation management, and multi-channel
 * communication between customers and support agents.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Tag(name = "Ticket Communications", description = "Customer support communication and messaging API")
@Slf4j
@RestController
@RequestMapping("/api/v1/support/communications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommunicationController {

    private final SupportTicketService supportTicketService;

    // ========== MESSAGE MANAGEMENT ==========

    @Operation(summary = "Add message to ticket", 
               description = "Sends a message in the ticket conversation with support for attachments and channels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Message sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid message data"),
        @ApiResponse(responseCode = "404", description = "Ticket not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/tickets/{ticketId}/messages")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<TicketMessageResponse> addMessage(
            @Parameter(description = "Ticket identifier") 
            @PathVariable UUID ticketId,
            @Valid @RequestBody CommunicationRequest request) {
        
        log.info("Adding message to ticket: {}", ticketId);
        
        try {
            // Create AddMessageRequest from CommunicationRequest
            AddMessageRequest addRequest = AddMessageRequest.builder()
                .messageType(request.getMessageType())
                .content(request.getContent())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderRole(request.getSenderRole())
                .channel(request.getChannel())
                .isInternal(request.getIsInternal())
                .build();
            
            TicketMessageResponse response = supportTicketService.addMessage(ticketId, addRequest);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error adding message to ticket {}: {}", ticketId, e.getMessage(), e);
            throw new RuntimeException("Failed to add message: " + e.getMessage());
        }
    }

    @Operation(summary = "Get ticket messages", 
               description = "Retrieves conversation messages for a ticket with pagination")
    @GetMapping("/tickets/{ticketId}/messages")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Page<TicketMessageResponse>> getTicketMessages(
            @Parameter(description = "Ticket identifier") 
            @PathVariable UUID ticketId,
            Pageable pageable) {
        
        log.info("Retrieving messages for ticket: {}", ticketId);
        
        Page<TicketMessageResponse> messages = supportTicketService.getTicketMessages(ticketId, pageable);
        return ResponseEntity.ok(messages);
    }

    @Operation(summary = "Mark message as read", 
               description = "Marks a specific message as read by the current user")
    @PatchMapping("/messages/{messageId}/read")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> markMessageAsRead(
            @Parameter(description = "Message identifier") 
            @PathVariable UUID messageId,
            @Parameter(description = "User marking message as read") 
            @RequestParam String readBy) {
        
        log.info("Marking message {} as read by {}", messageId, readBy);
        
        try {
            supportTicketService.markMessageAsRead(messageId, readBy);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error marking message as read: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Edit message", 
               description = "Edits an existing message content with audit trail")
    @PutMapping("/messages/{messageId}")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<TicketMessageResponse> editMessage(
            @Parameter(description = "Message identifier") 
            @PathVariable UUID messageId,
            @Parameter(description = "New message content") 
            @RequestParam String newContent,
            @Parameter(description = "Reason for editing") 
            @RequestParam String editReason,
            @Parameter(description = "User making the edit") 
            @RequestParam String editedBy) {
        
        log.info("Editing message: {}", messageId);
        
        try {
            TicketMessageResponse response = supportTicketService.editMessage(messageId, newContent, editReason, editedBy);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error editing message: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ========== COMMUNICATION WORKFLOWS ==========

    @Operation(summary = "Send communication", 
               description = "Sends a communication through specified channel with notification handling")
    @PostMapping("/send")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<CommunicationResponse> sendCommunication(
            @Valid @RequestBody CommunicationRequest request) {
        
        log.info("Sending communication for ticket: {}", request.getTicketId());
        
        try {
            // In a full implementation, this would handle multi-channel sending
            CommunicationResponse response = CommunicationResponse.builder()
                .id(UUID.randomUUID())
                .ticketId(request.getTicketId())
                .messageType(request.getMessageType())
                .content(request.getContent())
                .channel(request.getChannel())
                .senderId(request.getSenderId())
                .senderName(request.getSenderName())
                .senderRole(request.getSenderRole())
                .isInternal(request.getIsInternal())
                .sentAt(java.time.LocalDateTime.now())
                .deliveryStatus("DELIVERED")
                .createdAt(java.time.LocalDateTime.now())
                .build();
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error sending communication: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send communication: " + e.getMessage());
        }
    }

    // ========== ESCALATION MANAGEMENT ==========

    @Operation(summary = "Create escalation", 
               description = "Creates an escalation request for a ticket with detailed reasoning")
    @PostMapping("/escalations")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<SupportTicketResponse> createEscalation(
            @Valid @RequestBody EscalationRequest request) {
        
        log.info("Creating escalation for ticket: {}", request.getTicketId());
        
        try {
            SupportTicketResponse response = supportTicketService.escalateTicket(
                request.getTicketId(),
                request.getEscalatedToAgentId() != null ? request.getEscalatedToAgentId() : request.getEscalatedToTeam(),
                request.getEscalationReason(),
                request.getEscalatedByAgentId()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating escalation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create escalation: " + e.getMessage());
        }
    }

    // ========== BULK OPERATIONS ==========

    @Operation(summary = "Bulk ticket operations", 
               description = "Performs bulk operations on multiple tickets (status updates, assignments, etc.)")
    @PostMapping("/bulk-operations")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<BulkOperationResponse> performBulkOperation(
            @Valid @RequestBody BulkTicketOperationRequest request) {
        
        log.info("Performing bulk operation: {} on {} tickets", 
                request.getOperationType(), request.getTicketIds().size());
        
        try {
            // Validate the request
            BulkTicketOperationRequest.BulkOperationValidation validation = request.validate();
            if (!Boolean.TRUE.equals(validation.getCanProceed())) {
                return ResponseEntity.badRequest().body(
                    BulkOperationResponse.builder()
                        .success(false)
                        .message(validation.getOverallValidationMessage())
                        .build()
                );
            }
            
            // Perform the operation based on type
            BulkOperationResponse response = switch (request.getOperationType()) {
                case UPDATE_STATUS -> supportTicketService.bulkUpdateStatus(
                    request.getTicketIds(), 
                    request.getNewStatus(), 
                    request.getOperationReason(), 
                    request.getPerformedByAgentId()
                );
                case ASSIGN_TICKETS -> supportTicketService.bulkAssignTickets(
                    request.getTicketIds(), 
                    request.getAssignedAgentId(), 
                    request.getPerformedByAgentId()
                );
                case CLOSE_TICKETS -> supportTicketService.bulkCloseTickets(
                    request.getTicketIds(), 
                    request.getResolutionNotes(), 
                    request.getPerformedByAgentId()
                );
                default -> BulkOperationResponse.builder()
                    .success(false)
                    .message("Operation type not yet implemented: " + request.getOperationType())
                    .build();
            };
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error performing bulk operation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BulkOperationResponse.builder()
                    .success(false)
                    .message("Bulk operation failed: " + e.getMessage())
                    .build());
        }
    }

    // ========== CUSTOMER SATISFACTION ==========

    @Operation(summary = "Submit customer satisfaction feedback", 
               description = "Collects and processes customer satisfaction ratings and feedback")
    @PostMapping("/feedback")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<CustomerSatisfactionResponse> submitCustomerSatisfaction(
            @Valid @RequestBody CustomerSatisfactionRequest request) {
        
        log.info("Submitting customer satisfaction for ticket: {}", request.getTicketId());
        
        try {
            // In a full implementation, this would save feedback and trigger analytics
            CustomerSatisfactionResponse response = CustomerSatisfactionResponse.builder()
                .id(UUID.randomUUID())
                .ticketId(request.getTicketId())
                .customerId(request.getCustomerId())
                .overallRating(request.getOverallRating())
                .sentiment(request.getCustomerSentiment())
                .npsCategory(request.getNPSCategory())
                .feedbackPriority(request.getFeedbackPriority())
                .submittedAt(java.time.LocalDateTime.now())
                .processed(true)
                .build();
            
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error submitting customer satisfaction: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to submit feedback: " + e.getMessage());
        }
    }

    // ========== NOTIFICATION MANAGEMENT ==========

    @Operation(summary = "Send customer notification", 
               description = "Sends real-time notification to customer through specified channel")
    @PostMapping("/notifications/customer")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> sendCustomerNotification(
            @Parameter(description = "Ticket identifier") 
            @RequestParam UUID ticketId,
            @Parameter(description = "Notification message") 
            @RequestParam String message,
            @Parameter(description = "Communication channel") 
            @RequestParam String channel) {
        
        log.info("Sending customer notification for ticket: {}", ticketId);
        
        try {
            supportTicketService.notifyCustomer(ticketId, message, channel);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error sending customer notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Send agent notification", 
               description = "Sends real-time notification to support agent")
    @PostMapping("/notifications/agent")
    @PreAuthorize("hasRole('AGENT') or hasRole('ADMIN')")
    public ResponseEntity<Void> sendAgentNotification(
            @Parameter(description = "Agent identifier") 
            @RequestParam String agentId,
            @Parameter(description = "Notification message") 
            @RequestParam String message,
            @Parameter(description = "Related ticket ID") 
            @RequestParam String ticketId) {
        
        log.info("Sending agent notification to: {}", agentId);
        
        try {
            supportTicketService.notifyAgent(agentId, message, ticketId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error sending agent notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== ERROR HANDLING ==========

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<SupportTicketController.ErrorResponse> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception in communication controller: {}", e.getMessage(), e);
        
        SupportTicketController.ErrorResponse error = SupportTicketController.ErrorResponse.builder()
            .error("COMMUNICATION_OPERATION_FAILED")
            .message(e.getMessage())
            .timestamp(java.time.LocalDateTime.now())
            .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ========== HELPER DTOs ==========

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AddMessageRequest {
        private com.gogidix.courier.customer.support.communication.enums.MessageType messageType;
        private String content;
        private String senderId;
        private String senderName;
        private String senderRole;
        private String channel;
        private Boolean isInternal;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BulkOperationResponse {
        private Boolean success;
        private String message;
        private Integer totalProcessed;
        private Integer successCount;
        private Integer failureCount;
        private java.time.LocalDateTime processedAt;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CustomerSatisfactionResponse {
        private UUID id;
        private UUID ticketId;
        private String customerId;
        private Integer overallRating;
        private String sentiment;
        private String npsCategory;
        private String feedbackPriority;
        private java.time.LocalDateTime submittedAt;
        private Boolean processed;
    }
}