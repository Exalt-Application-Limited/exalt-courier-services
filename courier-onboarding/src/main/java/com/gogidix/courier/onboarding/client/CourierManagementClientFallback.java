package com.gogidix.courier.onboarding.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback implementation for CourierManagementClient.
 * Used when the Courier Management Service is unavailable.
 */
@Component
public class CourierManagementClientFallback implements CourierManagementClient {
    
    private static final Logger logger = LoggerFactory.getLogger(CourierManagementClientFallback.class);

    @Override
    public ResponseEntity<Map<String, Object>> createCourierProfile(Map<String, Object> courierData) {
        logger.error("Fallback: Unable to create courier profile in Courier Management Service");
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Courier Management Service is currently unavailable");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateCourierStatus(String courierId, Map<String, Object> statusData) {
        logger.error("Fallback: Unable to update status for courier {} in Courier Management Service", courierId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Courier Management Service is currently unavailable");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> assignCourierToZone(String courierId, Map<String, Object> assignmentData) {
        logger.error("Fallback: Unable to assign courier {} to zone in Courier Management Service", courierId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Courier Management Service is currently unavailable");
        
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}
