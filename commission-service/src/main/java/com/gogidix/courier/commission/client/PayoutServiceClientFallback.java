package com.gogidix.courier.commission.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for PayoutServiceClient when the service is unavailable.
 */
@Component
@Slf4j
public class PayoutServiceClientFallback implements PayoutServiceClient {
    
    

    @Override
    public ResponseEntity<Map<String, Object>> notifyPartnerPayment(Long partnerId, Map<String, Object> paymentDetails) {
        log.warn("Fallback: Unable to notify payout service about partner payment for partner ID: {}", partnerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Payout service unavailable, notification will be retried later");
        response.put("partnerId", partnerId);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> validatePartnerAccount(Long partnerId) {
        log.warn("Fallback: Unable to validate partner account with payout service for partner ID: {}", partnerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true); // Assume valid to prevent blocking
        response.put("message", "Validation failed due to payout service unavailability, assuming valid");
        response.put("partnerId", partnerId);
        
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> reserveFundsForPartner(Long partnerId, BigDecimal amount) {
        log.warn("Fallback: Unable to reserve funds with payout service for partner ID: {}, amount: {}", 
                partnerId, amount);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Fund reservation failed due to payout service unavailability");
        response.put("partnerId", partnerId);
        response.put("amount", amount);
        
        return ResponseEntity.ok(response);
    }
}

