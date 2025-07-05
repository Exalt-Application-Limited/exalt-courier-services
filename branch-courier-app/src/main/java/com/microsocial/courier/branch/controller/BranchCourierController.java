package com.gogidix.courier.courier.branch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsocial.courier.branch.dashboard.model.DeliveryMetrics;
import com.microsocial.courier.branch.dashboard.model.PerformanceMetrics;
import com.microsocial.courier.branch.dashboard.model.ResourceMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Branch Courier operations.
 * Provides endpoints for courier management and delivery tracking.
 */
@RestController
@RequestMapping("/api/branch")
public class BranchCourierController {

    private static final Logger logger = LoggerFactory.getLogger(BranchCourierController.class);

    /**
     * Get couriers assigned to this branch
     * 
     * @return List of couriers
     */
    @GetMapping("/couriers")
    public ResponseEntity<List<Map<String, Object>>> getCouriers() {
        logger.info("API call to get branch couriers");
        
        // This would be replaced with actual database calls in a real implementation
        List<Map<String, Object>> couriers = new ArrayList<>();
        
        // Sample courier 1
        Map<String, Object> courier1 = new HashMap<>();
        courier1.put("id", "courier-001");
        courier1.put("name", "John Smith");
        courier1.put("status", "ACTIVE");
        courier1.put("vehicleType", "MOTORCYCLE");
        courier1.put("rating", 4.7);
        couriers.add(courier1);
        
        // Sample courier 2
        Map<String, Object> courier2 = new HashMap<>();
        courier2.put("id", "courier-002");
        courier2.put("name", "Jane Doe");
        courier2.put("status", "ACTIVE");
        courier2.put("vehicleType", "CAR");
        courier2.put("rating", 4.9);
        couriers.add(courier2);
        
        // Sample courier 3
        Map<String, Object> courier3 = new HashMap<>();
        courier3.put("id", "courier-003");
        courier3.put("name", "Mike Johnson");
        courier3.put("status", "OFFLINE");
        courier3.put("vehicleType", "BICYCLE");
        courier3.put("rating", 4.5);
        couriers.add(courier3);
        
        return ResponseEntity.ok(couriers);
    }

    /**
     * Get deliveries assigned to this branch
     * 
     * @return List of deliveries
     */
    @GetMapping("/deliveries")
    public ResponseEntity<List<Map<String, Object>>> getDeliveries() {
        logger.info("API call to get branch deliveries");
        
        // This would be replaced with actual database calls in a real implementation
        List<Map<String, Object>> deliveries = new ArrayList<>();
        
        // Sample delivery 1
        Map<String, Object> delivery1 = new HashMap<>();
        delivery1.put("id", "delivery-001");
        delivery1.put("status", "IN_PROGRESS");
        delivery1.put("courierId", "courier-001");
        delivery1.put("estimatedDeliveryTime", "2023-08-15T14:30:00Z");
        delivery1.put("customerName", "Alice Brown");
        deliveries.add(delivery1);
        
        // Sample delivery 2
        Map<String, Object> delivery2 = new HashMap<>();
        delivery2.put("id", "delivery-002");
        delivery2.put("status", "PENDING");
        delivery2.put("courierId", null);
        delivery2.put("estimatedDeliveryTime", "2023-08-15T16:45:00Z");
        delivery2.put("customerName", "Bob Williams");
        deliveries.add(delivery2);
        
        // Sample delivery 3
        Map<String, Object> delivery3 = new HashMap<>();
        delivery3.put("id", "delivery-003");
        delivery3.put("status", "COMPLETED");
        delivery3.put("courierId", "courier-002");
        delivery3.put("estimatedDeliveryTime", "2023-08-15T10:15:00Z");
        delivery3.put("actualDeliveryTime", "2023-08-15T10:07:00Z");
        delivery3.put("customerName", "Charlie Davis");
        deliveries.add(delivery3);
        
        return ResponseEntity.ok(deliveries);
    }

    /**
     * Get branch resource status
     * 
     * @return Resource status information
     */
    @GetMapping("/resources")
    public ResponseEntity<ResourceMetrics> getResources() {
        logger.info("API call to get branch resources");
        
        // This would be replaced with actual data collection in a real implementation
        ResourceMetrics resources = new ResourceMetrics();
        resources.setVehiclesInUse(15);
        resources.setVehiclesAvailable(3);
        resources.setFuelConsumption(125.6);
        resources.setMaintenanceScheduled(2);
        
        return ResponseEntity.ok(resources);
    }
} 