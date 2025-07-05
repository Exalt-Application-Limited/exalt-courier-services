package com.gogidix.integration.common.controller;

import com.gogidix.integration.common.exception.UnsupportedCarrierException;
import com.gogidix.integration.common.model.ShipmentRequest;
import com.gogidix.integration.common.model.ShipmentResponse;
import com.gogidix.integration.common.model.TrackingRequest;
import com.gogidix.integration.common.model.TrackingResponse;
import com.gogidix.integration.common.service.ShippingIntegrationService;
import com.gogidix.integration.common.service.ShippingProviderFactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller that exposes the shipping integration functionality
 * as HTTP endpoints for client applications.
 */
@RestController
@RequestMapping("/api/shipping")
@Slf4j
public class ShippingIntegrationController {
    
    private final ShippingIntegrationService shippingService;
    private final ShippingProviderFactory providerFactory;
    
    @Autowired
    public ShippingIntegrationController(
            ShippingIntegrationService shippingService,
            ShippingProviderFactory providerFactory) {
        this.shippingService = shippingService;
        this.providerFactory = providerFactory;
    }
    
    /**
     * Create a shipment with a specific carrier.
     *
     * @param request the shipment request
     * @param carrierId the carrier ID
     * @return the shipment response
     */
    @PostMapping("/carriers/{carrierId}/shipments")
    public ResponseEntity<ShipmentResponse> createShipment(
            @Valid @RequestBody ShipmentRequest request,
            @PathVariable String carrierId) {
        
        log.info("API: Creating shipment with carrier: {}", carrierId);
        
        try {
            ShipmentResponse response = shippingService.createShipment(request, carrierId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error creating shipment: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error creating shipment: " + e.getMessage());
        }
    }
    
    /**
     * Track a shipment.
     *
     * @param request the tracking request
     * @return the tracking response
     */
    @PostMapping("/track")
    public ResponseEntity<TrackingResponse> trackShipment(@Valid @RequestBody TrackingRequest request) {
        log.info("API: Tracking shipment with number: {} and carrier: {}", 
                request.getTrackingNumber(), request.getCarrierId());
        
        try {
            TrackingResponse response = shippingService.trackShipment(request);
            return ResponseEntity.ok(response);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error tracking shipment: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error tracking shipment: " + e.getMessage());
        }
    }
    
    /**
     * Track a shipment by tracking number and carrier ID.
     *
     * @param trackingNumber the tracking number
     * @param carrierId the carrier ID
     * @return the tracking response
     */
    @GetMapping("/carriers/{carrierId}/track/{trackingNumber}")
    public ResponseEntity<TrackingResponse> trackShipment(
            @PathVariable String trackingNumber,
            @PathVariable String carrierId) {
        
        log.info("API: Tracking shipment with number: {} and carrier: {}", trackingNumber, carrierId);
        
        try {
            TrackingResponse response = shippingService.trackShipment(trackingNumber, carrierId);
            return ResponseEntity.ok(response);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error tracking shipment: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error tracking shipment: " + e.getMessage());
        }
    }
    
    /**
     * Cancel a shipment.
     *
     * @param shipmentId the shipment ID
     * @param carrierId the carrier ID
     * @return true if cancelled successfully
     */
    @DeleteMapping("/carriers/{carrierId}/shipments/{shipmentId}")
    public ResponseEntity<Map<String, Object>> cancelShipment(
            @PathVariable String shipmentId,
            @PathVariable String carrierId) {
        
        log.info("API: Cancelling shipment with ID: {} and carrier: {}", shipmentId, carrierId);
        
        try {
            boolean cancelled = shippingService.cancelShipment(shipmentId, carrierId);
            Map<String, Object> response = Map.of(
                    "success", cancelled,
                    "shipmentId", shipmentId,
                    "carrierId", carrierId,
                    "message", cancelled ? "Shipment cancelled successfully" : "Failed to cancel shipment"
            );
            
            return ResponseEntity.ok(response);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error cancelling shipment: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error cancelling shipment: " + e.getMessage());
        }
    }
    
    /**
     * Compare shipping rates across all available carriers.
     *
     * @param request the shipment request
     * @return map of carrier IDs to rate information
     */
    @PostMapping("/rates/compare")
    public ResponseEntity<Map<String, Map<String, Object>>> compareRates(
            @Valid @RequestBody ShipmentRequest request) {
        
        log.info("API: Comparing rates across all carriers for reference ID: {}", request.getReferenceId());
        
        try {
            Map<String, Map<String, Object>> rates = shippingService.compareRates(request);
            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            log.error("Error comparing rates: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error comparing rates: " + e.getMessage());
        }
    }
    
    /**
     * Calculate shipping rates for a specific carrier.
     *
     * @param request the shipment request
     * @param carrierId the carrier ID
     * @return map with rate options
     */
    @PostMapping("/carriers/{carrierId}/rates")
    public ResponseEntity<Map<String, Object>> calculateRates(
            @Valid @RequestBody ShipmentRequest request,
            @PathVariable String carrierId) {
        
        log.info("API: Calculating rates for carrier: {} and reference ID: {}", 
                carrierId, request.getReferenceId());
        
        try {
            Map<String, Object> rates = providerFactory.getProviderByCarrierId(carrierId)
                    .calculateRates(request);
            return ResponseEntity.ok(rates);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error calculating rates: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error calculating rates: " + e.getMessage());
        }
    }
    
    /**
     * Get cost optimization suggestions from all carriers.
     *
     * @param shipmentData historical shipment data for analysis
     * @return list of cost optimization suggestions from all carriers
     */
    @PostMapping("/optimization/suggestions")
    public ResponseEntity<List<Map<String, Object>>> getCostOptimizationSuggestions(
            @RequestBody Map<String, Object> shipmentData) {
        
        log.info("API: Getting cost optimization suggestions from all carriers");
        
        try {
            List<Map<String, Object>> suggestions = 
                    shippingService.getAggregatedCostOptimizationSuggestions(shipmentData);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            log.error("Error getting cost optimization suggestions: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error getting cost optimization suggestions: " + e.getMessage());
        }
    }
    
    /**
     * Validate an address with a specific carrier.
     *
     * @param addressData the address data
     * @param carrierId the carrier ID
     * @return true if the address is valid
     */
    @PostMapping("/carriers/{carrierId}/validate-address")
    public ResponseEntity<Map<String, Object>> validateAddress(
            @RequestBody Map<String, String> addressData,
            @PathVariable String carrierId) {
        
        log.info("API: Validating address with carrier: {}", carrierId);
        
        try {
            boolean valid = shippingService.validateAddress(addressData, carrierId);
            Map<String, Object> response = Map.of(
                    "valid", valid,
                    "carrierId", carrierId
            );
            return ResponseEntity.ok(response);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error validating address: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error validating address: " + e.getMessage());
        }
    }
    
    /**
     * Get the health status of all shipping providers.
     *
     * @return map of carrier IDs to operational status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Boolean>> getHealthStatus() {
        log.info("API: Getting health status of all shipping providers");
        
        try {
            Map<String, Boolean> healthStatus = shippingService.getProvidersHealthStatus();
            return ResponseEntity.ok(healthStatus);
        } catch (Exception e) {
            log.error("Error getting health status: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error getting health status: " + e.getMessage());
        }
    }
    
    /**
     * Schedule a pickup with a specific carrier.
     *
     * @param pickupData the pickup data
     * @param carrierId the carrier ID
     * @return map with pickup confirmation details
     */
    @PostMapping("/carriers/{carrierId}/pickups")
    public ResponseEntity<Map<String, Object>> schedulePickup(
            @RequestBody Map<String, Object> pickupData,
            @PathVariable String carrierId) {
        
        log.info("API: Scheduling pickup with carrier: {}", carrierId);
        
        try {
            Map<String, Object> response = shippingService.schedulePickup(pickupData, carrierId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error scheduling pickup: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error scheduling pickup: " + e.getMessage());
        }
    }
    
    /**
     * Get SLA metrics for all carriers.
     *
     * @return map of carrier IDs to SLA metrics
     */
    @GetMapping("/metrics/sla")
    public ResponseEntity<Map<String, Map<String, Object>>> getAllSlaMetrics() {
        log.info("API: Getting SLA metrics for all carriers");
        
        try {
            Map<String, Map<String, Object>> metrics = shippingService.getAllSlaMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error getting SLA metrics: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error getting SLA metrics: " + e.getMessage());
        }
    }
    
    /**
     * Get SLA metrics for a specific carrier.
     *
     * @param carrierId the carrier ID
     * @return map with SLA metrics
     */
    @GetMapping("/carriers/{carrierId}/metrics/sla")
    public ResponseEntity<Map<String, Object>> getSlaMetrics(@PathVariable String carrierId) {
        log.info("API: Getting SLA metrics for carrier: {}", carrierId);
        
        try {
            Map<String, Object> metrics = shippingService.getSlaMetrics(carrierId);
            return ResponseEntity.ok(metrics);
        } catch (UnsupportedCarrierException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Error getting SLA metrics: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error getting SLA metrics: " + e.getMessage());
        }
    }
} 
