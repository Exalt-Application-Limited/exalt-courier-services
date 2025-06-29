package com.exalt.courierservices.commission.$1;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Feign client for integration with the Payout Service.
 */
@FeignClient(name = "payout-service", fallback = PayoutServiceClientFallback.class)
public interface PayoutServiceClient {

    /**
     * Notifies the Payout Service about a new partner payment.
     *
     * @param partnerId The ID of the partner
     * @param paymentDetails The payment details
     * @return ResponseEntity with status
     */
    @PostMapping("/api/v1/payouts/partner/{partnerId}/notify-payment")
    ResponseEntity<Map<String, Object>> notifyPartnerPayment(
            @PathVariable("partnerId") Long partnerId,
            @RequestBody Map<String, Object> paymentDetails
    );
    
    /**
     * Validates a partner's payment account with Payout Service.
     *
     * @param partnerId The ID of the partner
     * @return ResponseEntity with validation result
     */
    @PostMapping("/api/v1/payouts/partner/{partnerId}/validate-account")
    ResponseEntity<Map<String, Object>> validatePartnerAccount(
            @PathVariable("partnerId") Long partnerId
    );
    
    /**
     * Reserves funds for a partner payment.
     *
     * @param partnerId The ID of the partner
     * @param amount The amount to reserve
     * @return ResponseEntity with reservation details
     */
    @PostMapping("/api/v1/payouts/partner/{partnerId}/reserve-funds")
    ResponseEntity<Map<String, Object>> reserveFundsForPartner(
            @PathVariable("partnerId") Long partnerId,
            @RequestBody BigDecimal amount
    );
}
