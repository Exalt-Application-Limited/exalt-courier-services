package com.gogidix.integration.common.service;

import com.gogidix.integration.common.exception.UnsupportedCarrierException;
import com.gogidix.integration.common.model.ShipmentRequest;
import com.gogidix.integration.common.model.ShipmentResponse;
import com.gogidix.integration.common.model.TrackingRequest;
import com.gogidix.integration.common.model.TrackingResponse;
import com.gogidix.integration.common.model.TrackingRequest.CarrierId;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Unified service for interacting with different shipping providers.
 * This service acts as a facade over all shipping provider implementations and
 * provides additional functionality like rate comparison across multiple carriers.
 */
@Service
@Slf4j
public class ShippingIntegrationService {
    
    private final ShippingProviderFactory providerFactory;
    private final Executor asyncExecutor;
    
    @Autowired
    public ShippingIntegrationService(ShippingProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
        this.asyncExecutor = Executors.newFixedThreadPool(10);
    }
    
    /**
     * Create a shipment with a specific carrier.
     *
     * @param request the shipment request
     * @param carrierId the carrier ID
     * @return the shipment response
     */
    public ShipmentResponse createShipment(ShipmentRequest request, String carrierId) {
        log.info("Creating shipment with carrier: {}", carrierId);
        return providerFactory.getProviderByCarrierId(carrierId).createShipment(request);
    }
    
    /**
     * Create a shipment with a specific carrier.
     *
     * @param request the shipment request
     * @param carrierId the carrier ID enum
     * @return the shipment response
     */
    public ShipmentResponse createShipment(ShipmentRequest request, CarrierId carrierId) {
        return createShipment(request, carrierId.name());
    }
    
    /**
     * Track a shipment.
     *
     * @param request the tracking request
     * @return the tracking response
     */
    public TrackingResponse trackShipment(TrackingRequest request) {
        log.info("Tracking shipment with number: {} and carrier: {}", 
                request.getTrackingNumber(), request.getCarrierId());
        return providerFactory.getProviderByCarrierId(request.getCarrierId().name())
                .trackShipment(request);
    }
    
    /**
     * Track a shipment by tracking number and carrier ID.
     *
     * @param trackingNumber the tracking number
     * @param carrierId the carrier ID
     * @return the tracking response
     */
    public TrackingResponse trackShipment(String trackingNumber, String carrierId) {
        TrackingRequest request = TrackingRequest.builder()
                .trackingNumber(trackingNumber)
                .carrierId(CarrierId.valueOf(carrierId))
                .includeDetailedEvents(true)
                .build();
        
        return trackShipment(request);
    }
    
    /**
     * Cancel a shipment.
     *
     * @param shipmentId the shipment ID
     * @param carrierId the carrier ID
     * @return true if cancelled successfully
     */
    public boolean cancelShipment(String shipmentId, String carrierId) {
        log.info("Cancelling shipment with ID: {} and carrier: {}", shipmentId, carrierId);
        return providerFactory.getProviderByCarrierId(carrierId).cancelShipment(shipmentId);
    }
    
    /**
     * Compare shipping rates across all available carriers.
     * This method will query rates from all supported carriers in parallel.
     *
     * @param request the shipment request
     * @return map of carrier IDs to rate information
     */
    public Map<String, Map<String, Object>> compareRates(ShipmentRequest request) {
        log.info("Comparing rates across all carriers for reference ID: {}", request.getReferenceId());
        
        Map<String, ShippingProviderService> providers = providerFactory.getAllProviders();
        Map<String, Map<String, Object>> results = new HashMap<>();
        
        // Submit rate calculation tasks for all carriers in parallel
        List<CompletableFuture<Map.Entry<String, Map<String, Object>>>> futures = 
            providers.entrySet().stream()
                .map(entry -> CompletableFuture.supplyAsync(() -> {
                    try {
                        String carrierId = entry.getKey();
                        ShippingProviderService provider = entry.getValue();
                        
                        // Skip providers that are not operational
                        if (!provider.isOperational()) {
                            log.warn("Skipping rate calculation for {} - provider not operational", 
                                    provider.getCarrierName());
                            return Map.entry(carrierId, createErrorRateResponse(
                                    "Provider not operational", carrierId, provider.getCarrierName()));
                        }
                        
                        // Get rates from this provider
                        Map<String, Object> rates = provider.calculateRates(request);
                        return Map.entry(carrierId, rates);
                    } catch (Exception e) {
                        log.error("Error calculating rates for carrier {}: {}", 
                                entry.getKey(), e.getMessage(), e);
                        return Map.entry(entry.getKey(), createErrorRateResponse(
                                "Error calculating rates: " + e.getMessage(), 
                                entry.getKey(), entry.getValue().getCarrierName()));
                    }
                }, asyncExecutor))
                .collect(Collectors.toList());
        
        // Collect all results
        for (CompletableFuture<Map.Entry<String, Map<String, Object>>> future : futures) {
            try {
                Map.Entry<String, Map<String, Object>> result = future.get();
                results.put(result.getKey(), result.getValue());
            } catch (Exception e) {
                log.error("Error collecting rate comparison results: {}", e.getMessage(), e);
            }
        }
        
        return results;
    }
    
    /**
     * Create an error response for rate calculation.
     *
     * @param errorMessage the error message
     * @param carrierId the carrier ID
     * @param carrierName the carrier name
     * @return the error response map
     */
    private Map<String, Object> createErrorRateResponse(String errorMessage, String carrierId, String carrierName) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("carrierId", carrierId);
        response.put("carrierName", carrierName);
        response.put("error", errorMessage);
        return response;
    }
    
    /**
     * Get aggregated cost optimization suggestions from all carriers.
     *
     * @param shipmentData historical shipment data for analysis
     * @return list of cost optimization suggestions from all carriers
     */
    public List<Map<String, Object>> getAggregatedCostOptimizationSuggestions(Map<String, Object> shipmentData) {
        log.info("Getting aggregated cost optimization suggestions from all carriers");
        
        List<Map<String, Object>> allSuggestions = new ArrayList<>();
        Map<String, ShippingProviderService> providers = providerFactory.getAllProviders();
        
        for (ShippingProviderService provider : providers.values()) {
            try {
                Map<String, Object> providerSuggestions = provider.getCostOptimizationSuggestions(shipmentData);
                allSuggestions.add(providerSuggestions);
            } catch (Exception e) {
                log.error("Error getting cost optimization suggestions from {}: {}", 
                        provider.getCarrierName(), e.getMessage(), e);
            }
        }
        
        return allSuggestions;
    }
    
    /**
     * Validate an address with a specific carrier.
     *
     * @param addressData the address data
     * @param carrierId the carrier ID
     * @return true if the address is valid
     */
    public boolean validateAddress(Map<String, String> addressData, String carrierId) {
        log.info("Validating address with carrier: {}", carrierId);
        return providerFactory.getProviderByCarrierId(carrierId).validateAddress(addressData);
    }
    
    /**
     * Get the health status of all shipping providers.
     *
     * @return map of carrier IDs to operational status
     */
    public Map<String, Boolean> getProvidersHealthStatus() {
        Map<String, Boolean> healthStatus = new HashMap<>();
        Map<String, ShippingProviderService> providers = providerFactory.getAllProviders();
        
        for (Map.Entry<String, ShippingProviderService> entry : providers.entrySet()) {
            try {
                healthStatus.put(entry.getKey(), entry.getValue().isOperational());
            } catch (Exception e) {
                log.error("Error checking health status for {}: {}", 
                        entry.getKey(), e.getMessage(), e);
                healthStatus.put(entry.getKey(), false);
            }
        }
        
        return healthStatus;
    }
    
    /**
     * Schedule a pickup with a specific carrier.
     *
     * @param pickupData the pickup data
     * @param carrierId the carrier ID
     * @return map with pickup confirmation details
     */
    public Map<String, Object> schedulePickup(Map<String, Object> pickupData, String carrierId) {
        log.info("Scheduling pickup with carrier: {}", carrierId);
        return providerFactory.getProviderByCarrierId(carrierId).schedulePickup(pickupData);
    }
    
    /**
     * Get SLA metrics for a specific carrier.
     *
     * @param carrierId the carrier ID
     * @return map with SLA metrics
     */
    public Map<String, Object> getSlaMetrics(String carrierId) {
        return providerFactory.getProviderByCarrierId(carrierId).getSlaMetrics();
    }
    
    /**
     * Get SLA metrics for all carriers.
     *
     * @return map of carrier IDs to SLA metrics
     */
    public Map<String, Map<String, Object>> getAllSlaMetrics() {
        Map<String, Map<String, Object>> allMetrics = new HashMap<>();
        Map<String, ShippingProviderService> providers = providerFactory.getAllProviders();
        
        for (Map.Entry<String, ShippingProviderService> entry : providers.entrySet()) {
            try {
                allMetrics.put(entry.getKey(), entry.getValue().getSlaMetrics());
            } catch (Exception e) {
                log.error("Error getting SLA metrics for {}: {}", 
                        entry.getKey(), e.getMessage(), e);
                
                Map<String, Object> errorMetrics = new HashMap<>();
                errorMetrics.put("error", "Failed to retrieve metrics: " + e.getMessage());
                errorMetrics.put("carrier", entry.getKey());
                allMetrics.put(entry.getKey(), errorMetrics);
            }
        }
        
        return allMetrics;
    }
} 