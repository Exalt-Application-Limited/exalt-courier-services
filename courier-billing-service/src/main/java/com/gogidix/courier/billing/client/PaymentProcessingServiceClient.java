package com.gogidix.courier.billing.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

/**
 * Feign client for Payment Processing Service integration.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@FeignClient(name = "payment-processing-service", path = "/api/v1/payments")
public interface PaymentProcessingServiceClient {

    @PostMapping("/process")
    PaymentResult processPayment(@RequestBody ProcessPaymentRequest request);

    // Request DTOs
    record ProcessPaymentRequest(
            BigDecimal amount,
            String currency,
            String paymentMethodId,
            String customerId,
            String referenceId,
            String paymentType
    ) {}

    // Response DTOs
    record PaymentResult(
            String paymentId,
            String status,
            String transactionId,
            String gatewayResponse,
            String failureReason
    ) {}
}