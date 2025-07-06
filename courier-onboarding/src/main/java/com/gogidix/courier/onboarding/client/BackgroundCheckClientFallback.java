package com.gogidix.courier.onboarding.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for BackgroundCheckClient.
 * Used when the Background Check API is unavailable.
 */
@Component
public class BackgroundCheckClientFallback implements BackgroundCheckClient {
    
    private static final Logger logger = LoggerFactory.getLogger(BackgroundCheckClientFallback.class);

    @Override
    public ResponseEntity<Map<String, Object>> initiateBackgroundCheck(Map<String, Object> checkData) {
        logger.error("Fallback: Unable to initiate background check with external service");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Background Check API is currently unavailable");
        response.put("checkStatus", "PENDING_MANUAL_REVIEW");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getBackgroundCheckStatus(String checkId) {
        logger.error("Fallback: Unable to get status for background check {}", checkId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Background Check API is currently unavailable");
        response.put("checkId", checkId);
        response.put("checkStatus", "UNKNOWN");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> manuallyApproveBackgroundCheck(
            String checkId, Map<String, Object> approvalData) {
        logger.error("Fallback: Unable to manually approve background check {}", checkId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Background Check API is currently unavailable");
        response.put("checkId", checkId);
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> cancelBackgroundCheck(String checkId) {
        logger.error("Fallback: Unable to cancel background check {}", checkId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Background Check API is currently unavailable");
        response.put("checkId", checkId);
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
