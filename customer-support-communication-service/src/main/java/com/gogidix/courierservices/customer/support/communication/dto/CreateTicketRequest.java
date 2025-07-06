package com.gogidix.courierservices.customer.support.communication.dto;

import com.gogidix.courierservices.customer.support.communication.model.SupportTicket;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating support tickets
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Email(message = "Valid customer email is required")
    @NotBlank(message = "Customer email is required")
    private String customerEmail;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Description is required")
    private String description;

    private SupportTicket.TicketPriority priority = SupportTicket.TicketPriority.MEDIUM;

    private SupportTicket.TicketCategory category = SupportTicket.TicketCategory.GENERAL;

    private String shipmentId; // Optional - for shipment-related tickets

    private String orderNumber; // Optional - for order-related tickets
}