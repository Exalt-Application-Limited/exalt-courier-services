package com.gogidix.courierservices.customer.support.communication.dto;

import com.gogidix.courierservices.customer.support.communication.model.TicketMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding messages to support tickets
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMessageRequest {

    @NotNull(message = "Ticket ID is required")
    private Long ticketId;

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Message type is required")
    private TicketMessage.MessageType type;

    private Long senderId;

    @NotBlank(message = "Sender name is required")
    private String senderName;

    private String senderEmail;

    @Builder.Default
    private Boolean isInternal = false;
}