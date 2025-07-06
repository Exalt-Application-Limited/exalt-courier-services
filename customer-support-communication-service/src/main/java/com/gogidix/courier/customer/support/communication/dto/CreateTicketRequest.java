package com.gogidix.courier.customer.support.communication.dto;

import com.gogidix.courier.customer.support.communication.enums.TicketCategory;
import com.gogidix.courier.customer.support.communication.enums.TicketPriority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a new support ticket.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketRequest {

    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    private String customerId;

    @Email(message = "Customer email must be valid")
    @Size(max = 100, message = "Customer email must not exceed 100 characters")
    private String customerEmail;

    @Size(max = 100, message = "Customer name must not exceed 100 characters")
    private String customerName;

    @Size(max = 20, message = "Customer phone must not exceed 20 characters")
    private String customerPhone;

    @NotBlank(message = "Subject is required")
    @Size(max = 200, message = "Subject must not exceed 200 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private TicketCategory category;

    private TicketPriority priority;

    @Size(max = 50, message = "Shipment reference ID must not exceed 50 characters")
    private String shipmentReferenceId;

    @Size(max = 100, message = "Order reference ID must not exceed 100 characters")
    private String orderReferenceId;

    private Boolean isUrgent = false;

    private Boolean requiresCallback = false;

    @Size(max = 500, message = "Tags must not exceed 500 characters")
    private String tags;

    @Size(max = 200, message = "Source must not exceed 200 characters")
    private String source;

    private List<String> attachmentUrls;
}