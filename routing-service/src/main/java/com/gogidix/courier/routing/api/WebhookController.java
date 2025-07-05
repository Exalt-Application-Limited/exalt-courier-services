package com.gogidix.courier.routing.api;

import com.gogidix.courier.routing.service.CacheService;
import com.gogidix.courier.routing.service.RoutingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling webhook callbacks from external services.
 */
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookController.class);
    
    private final RoutingService routingService;
    private final CacheService cacheService;
    
    @Autowired
    public WebhookController(RoutingService routingService, CacheService cacheService) {
        this.routingService = routingService;
        this.cacheService = cacheService;
    }
    
    /**
     * Handle traffic update webhook from external map service
     *
     * @param payload the traffic update payload
     * @return response entity
     */
    @PostMapping("/map-service/traffic-update")
    public ResponseEntity<Map<String, Object>> handleTrafficUpdate(@RequestBody Map<String, Object> payload) {
        try {
            LOGGER.info("Received traffic update webhook");
            
            // Extract affected areas and update route ETAs if needed
            if (payload.containsKey("affectedRoutes")) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "Traffic update processed");
                
                // In a real implementation, analyze the traffic data and update affected routes
                // For demonstration purposes, just invalidate the travel time cache
                cacheService.clearAllCaches();
                
                return ResponseEntity.ok(response);
            } else {
                LOGGER.warn("Invalid traffic update payload: missing affectedRoutes");
                return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid payload"
                ));
            }
        } catch (Exception e) {
            LOGGER.error("Error processing traffic update webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Internal server error"
            ));
        }
    }
    
    /**
     * Handle roadwork notification webhook from external map service
     *
     * @param payload the roadwork notification payload
     * @return response entity
     */
    @PostMapping("/map-service/roadwork-notification")
    public ResponseEntity<Map<String, Object>> handleRoadworkNotification(@RequestBody Map<String, Object> payload) {
        try {
            LOGGER.info("Received roadwork notification webhook");
            
            // Process roadwork notification and update routes if needed
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Roadwork notification processed");
            
            // In a real implementation, analyze the roadwork data and update affected routes
            // For demonstration purposes, just invalidate the travel time cache
            cacheService.clearAllCaches();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LOGGER.error("Error processing roadwork notification webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "Internal server error"
            ));
        }
    }
    
    /**
     * Verify webhook integration
     *
     * @param token the verification token
     * @return response entity
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyWebhook(@RequestParam String token) {
        // In a real implementation, validate the token against a stored secret
        // For demonstration purposes, accept any token
        LOGGER.info("Received webhook verification request with token: {}", token);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Webhook integration verified"
        ));
    }
}
