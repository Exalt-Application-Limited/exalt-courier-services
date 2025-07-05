package com.gogidix.courier.billing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Request DTO for processing a payment.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Schema(description = "Request to process a payment")
public record ProcessPaymentRequest(
    
    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be positive")
    @Schema(description = "Payment amount", example = "98.50")
    BigDecimal amount,
    
    @NotBlank(message = "Payment method ID is required")
    @Schema(description = "Payment method identifier", example = "PM_1234567890")
    String paymentMethodId,
    
    @NotBlank(message = "Payment method type is required")
    @Schema(description = "Type of payment method", example = "CREDIT_CARD", 
            allowableValues = {"CREDIT_CARD", "DEBIT_CARD", "ACH", "WIRE_TRANSFER", "DIGITAL_WALLET"})
    String paymentMethodType,
    
    @Schema(description = "Additional payment metadata")
    String paymentMetadata,
    
    @Schema(description = "Customer confirmation token")
    String confirmationToken,
    
    @Schema(description = "Whether to save payment method for future use")
    Boolean savePaymentMethod
) {}