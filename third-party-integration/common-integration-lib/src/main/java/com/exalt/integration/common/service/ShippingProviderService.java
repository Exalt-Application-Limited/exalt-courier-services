package com.exalt.integration.common.service;

import com.exalt.integration.common.model.ShipmentRequest;
import com.exalt.integration.common.model.ShipmentResponse;
import com.exalt.integration.common.model.TrackingRequest;
import com.exalt.integration.common.model.TrackingResponse;

import java.util.Map;

/**
 * Common interface for all shipping provider integrations.
 * Any 3PL provider implementation should implement this interface.
 */
public interface ShippingProviderService {
    
    /**
     * Get the carrier ID for this provider.
     *
     * @return the carrier ID
     */
    String getCarrierId();
    
    /**
     * Get the carrier name for this provider.
     *
     * @return the carrier name
     */
    String getCarrierName();
    
    /**
     * Create a shipment with the shipping provider.
     *
     * @param request the shipment request
     * @return the shipment response
     */
    ShipmentResponse createShipment(ShipmentRequest request);
    
    /**
     * Track a shipment using its tracking number.
     *
     * @param request the tracking request
     * @return the tracking response
     */
    TrackingResponse trackShipment(TrackingRequest request);
    
    /**
     * Cancel a shipment.
     *
     * @param shipmentId the shipment ID
     * @return true if cancelled successfully
     */
    boolean cancelShipment(String shipmentId);
    
    /**
     * Validate an address with the shipping provider.
     *
     * @param addressData map containing address details
     * @return true if the address is valid
     */
    boolean validateAddress(Map<String, String> addressData);
    
    /**
     * Calculate shipping rates for a shipment.
     *
     * @param request the shipment request
     * @return map with rate options
     */
    Map<String, Object> calculateRates(ShipmentRequest request);
    
    /**
     * Schedule a pickup for shipments.
     *
     * @param pickupData map containing pickup details
     * @return map with pickup confirmation details
     */
    Map<String, Object> schedulePickup(Map<String, Object> pickupData);
    
    /**
     * Get service level agreement (SLA) metrics.
     *
     * @return map with SLA metrics
     */
    Map<String, Object> getSlaMetrics();
    
    /**
     * Get cost optimization suggestions.
     *
     * @param shipmentData historical shipment data for analysis
     * @return map with cost optimization suggestions
     */
    Map<String, Object> getCostOptimizationSuggestions(Map<String, Object> shipmentData);
    
    /**
     * Check if the provider is available and operational.
     *
     * @return true if the provider is operational
     */
    boolean isOperational();
} 