package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Request DTO for creating a shipment invoice.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request to create a shipment invoice")
public record CreateShipmentInvoiceRequest(
    
    @NotBlank(message = "Customer ID is required")
    @Schema(description = "Customer identifier", example = "CUST-12345")
    String customerId,
    
    @NotBlank(message = "Customer name is required")
    @Schema(description = "Customer name", example = "Acme Corporation")
    String customerName,
    
    @NotBlank(message = "Customer email is required")
    @Schema(description = "Customer email address", example = "billing@acmecorp.com")
    String customerEmail,
    
    @Schema(description = "Billing address")
    String billingAddress,
    
    @NotBlank(message = "Shipment ID is required")
    @Schema(description = "Shipment identifier", example = "SHIP-67890")
    String shipmentId,
    
    @NotBlank(message = "Service type is required")
    @Schema(description = "Type of courier service", example = "NEXT_DAY")
    String serviceType,
    
    @Schema(description = "Shipment description", example = "Express delivery to New York")
    String description,
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    @Schema(description = "Package weight in kg", example = "2.5")
    BigDecimal weight,
    
    @Schema(description = "Package dimensions (L x W x H in cm)", example = "30x20x15")
    String dimensions,
    
    @NotBlank(message = "Origin is required")
    @Schema(description = "Origin address", example = "Los Angeles, CA")
    String origin,
    
    @NotBlank(message = "Destination is required")
    @Schema(description = "Destination address", example = "New York, NY")
    String destination,
    
    @Schema(description = "Declared value for insurance", example = "500.00")
    BigDecimal declaredValue,
    
    @Schema(description = "Currency code", example = "USD")
    String currency
) {}