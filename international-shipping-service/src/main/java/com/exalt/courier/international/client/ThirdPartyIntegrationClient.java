package com.exalt.courierservices.international-shipping.$1;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Feign client for communicating with the Third-Party Integration Service.
 * This client enables the International Shipping Service to interface with
 * external shipping carriers through the integration service.
 */
@FeignClient(name = "third-party-integration-service", 
        url = "${app.services.third-party-integration.url}", 
        fallback = ThirdPartyIntegrationClientFallback.class)
public interface ThirdPartyIntegrationClient {
    
    /**
     * Create a shipment with a specific carrier
     * 
     * @param carrierId The carrier ID (e.g., "dhl", "fedex", "ups")
     * @param shipmentRequest The shipment request payload
     * @return The shipment response
     */
    @PostMapping("/api/shipments/carrier/{carrierId}")
    ResponseEntity<Map<String, Object>> createShipment(
            @PathVariable("carrierId") String carrierId, 
            @RequestBody Map<String, Object> shipmentRequest);
    
    /**
     * Cancel a shipment with a specific carrier
     * 
     * @param carrierId The carrier ID
     * @param trackingNumber The tracking number of the shipment to cancel
     * @return The cancellation response
     */
    @DeleteMapping("/api/shipments/carrier/{carrierId}/{trackingNumber}")
    ResponseEntity<Map<String, Object>> cancelShipment(
            @PathVariable("carrierId") String carrierId,
            @PathVariable("trackingNumber") String trackingNumber);
    
    /**
     * Track a shipment with a specific carrier
     * 
     * @param carrierId The carrier ID
     * @param trackingRequest The tracking request payload
     * @return The tracking response
     */
    @PostMapping("/api/tracking/carrier/{carrierId}")
    ResponseEntity<Map<String, Object>> trackShipment(
            @PathVariable("carrierId") String carrierId,
            @RequestBody Map<String, Object> trackingRequest);
    
    /**
     * Check service availability between two locations
     * 
     * @param originCode Origin location code
     * @param destinationCode Destination location code
     * @return Available services between the locations
     */
    @GetMapping("/api/shipments/service-availability")
    ResponseEntity<List<Map<String, Object>>> checkServiceAvailability(
            @RequestParam("originCode") String originCode,
            @RequestParam("destinationCode") String destinationCode);
    
    /**
     * Get carriers that support a specific feature
     * 
     * @param feature The feature to check for (e.g., "international", "customs", "hazmat")
     * @return List of carriers supporting the feature
     */
    @GetMapping("/api/shipments/carriers-by-feature")
    ResponseEntity<List<String>> getCarriersByFeature(@RequestParam("feature") String feature);
    
    /**
     * Get all available carriers
     * 
     * @return List of all available carriers
     */
    @GetMapping("/api/shipments/carriers")
    ResponseEntity<List<Map<String, Object>>> getAllCarriers();
    
    /**
     * Get carrier details
     * 
     * @param carrierId The carrier ID
     * @return The carrier details
     */
    @GetMapping("/api/shipments/carrier/{carrierId}")
    ResponseEntity<Map<String, Object>> getCarrierDetails(@PathVariable("carrierId") String carrierId);
    
    /**
     * Generate a shipping label
     * 
     * @param carrierId The carrier ID
     * @param shipmentId The shipment ID
     * @return The shipping label data
     */
    @GetMapping("/api/shipments/carrier/{carrierId}/{shipmentId}/label")
    ResponseEntity<Map<String, Object>> generateLabel(
            @PathVariable("carrierId") String carrierId,
            @PathVariable("shipmentId") String shipmentId);
    
    /**
     * Generate customs documentation
     * 
     * @param carrierId The carrier ID
     * @param shipmentId The shipment ID
     * @param documentType The type of customs document to generate
     * @return The customs document data
     */
    @GetMapping("/api/shipments/carrier/{carrierId}/{shipmentId}/customs-document")
    ResponseEntity<Map<String, Object>> generateCustomsDocument(
            @PathVariable("carrierId") String carrierId,
            @PathVariable("shipmentId") String shipmentId,
            @RequestParam("documentType") String documentType);
}
