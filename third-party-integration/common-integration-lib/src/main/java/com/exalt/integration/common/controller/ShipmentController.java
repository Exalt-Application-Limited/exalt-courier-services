package com.exalt.integration.common.controller;

import com.exalt.integration.common.exception.IntegrationException;
import com.exalt.integration.common.model.Address;
import com.exalt.integration.common.model.ShipmentRequest;
import com.exalt.integration.common.model.ShipmentResponse;
import com.exalt.integration.common.service.ShippingProviderAdapter;
import com.exalt.integration.common.service.ShippingProviderAdapterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for shipment operations through third-party providers
 */
@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@Slf4j
public class ShipmentController {

    private final ShippingProviderAdapterRegistry adapterRegistry;

    /**
     * Create a new shipment using a specific carrier
     * @param carrierId The carrier ID to use (e.g. DHL, FEDEX, UPS)
     * @param shipmentRequest Shipment request details
     * @return Shipment response containing tracking information
     */
    @PostMapping("/carrier/{carrierId}")
    public ResponseEntity<ShipmentResponse> createShipment(
            @PathVariable String carrierId,
            @Valid @RequestBody ShipmentRequest shipmentRequest) {
        log.info("REST request to create shipment with carrier: {}", carrierId);
        
        Optional<ShippingProviderAdapter> adapter = adapterRegistry.getAdapter(carrierId);
        if (adapter.isEmpty()) {
            log.error("Provider not found for carrier: {}", carrierId);
            
            ShipmentResponse errorResponse = new ShipmentResponse();
            errorResponse.setSuccess(false);
            errorResponse.setCarrierId(carrierId);
            errorResponse.setReferenceId(shipmentRequest.getReferenceId());
            
            ShipmentResponse.ShipmentError error = new ShipmentResponse.ShipmentError();
            error.setCode("PROVIDER_NOT_FOUND");
            error.setMessage("Provider not found for carrier: " + carrierId);
            error.setSeverity(ShipmentResponse.ShipmentError.ErrorSeverity.ERROR);
            errorResponse.setErrors(List.of(error));
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        try {
            ShipmentResponse response = adapter.get().createShipment(shipmentRequest);
            return ResponseEntity.ok(response);
        } catch (IntegrationException e) {
            log.error("Error creating shipment with carrier {}: {}", carrierId, e.getMessage(), e);
            
            ShipmentResponse errorResponse = new ShipmentResponse();
            errorResponse.setSuccess(false);
            errorResponse.setCarrierId(carrierId);
            errorResponse.setReferenceId(shipmentRequest.getReferenceId());
            
            ShipmentResponse.ShipmentError error = new ShipmentResponse.ShipmentError();
            error.setCode("INTEGRATION_ERROR");
            error.setMessage(e.getMessage());
            error.setSeverity(ShipmentResponse.ShipmentError.ErrorSeverity.ERROR);
            errorResponse.setErrors(List.of(error));
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Create a shipment using the default carrier
     * @param shipmentRequest Shipment request details
     * @return Shipment response containing tracking information
     */
    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipmentWithDefaultProvider(
            @Valid @RequestBody ShipmentRequest shipmentRequest) {
        log.info("REST request to create shipment with default carrier");
        
        try {
            Optional<ShippingProviderAdapter> adapter = adapterRegistry.getDefaultAdapter();
            if (adapter.isEmpty()) {
                ShipmentResponse errorResponse = new ShipmentResponse();
                errorResponse.setSuccess(false);
                errorResponse.setReferenceId(shipmentRequest.getReferenceId());
                
                ShipmentResponse.ShipmentError error = new ShipmentResponse.ShipmentError();
                error.setCode("NO_DEFAULT_PROVIDER");
                error.setMessage("No default carrier is configured");
                error.setSeverity(ShipmentResponse.ShipmentError.ErrorSeverity.ERROR);
                errorResponse.setErrors(List.of(error));
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            
            ShipmentResponse response = adapter.get().createShipment(shipmentRequest);
            return ResponseEntity.ok(response);
        } catch (IntegrationException e) {
            log.error("Error creating shipment with default carrier: {}", e.getMessage(), e);
            
            ShipmentResponse errorResponse = new ShipmentResponse();
            errorResponse.setSuccess(false);
            errorResponse.setReferenceId(shipmentRequest.getReferenceId());
            
            ShipmentResponse.ShipmentError error = new ShipmentResponse.ShipmentError();
            error.setCode("INTEGRATION_ERROR");
            error.setMessage(e.getMessage());
            error.setSeverity(ShipmentResponse.ShipmentError.ErrorSeverity.ERROR);
            errorResponse.setErrors(List.of(error));
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Cancel a shipment
     * @param carrierId Carrier ID
     * @param shipmentId Shipment/tracking ID to cancel
     * @return Success or failure response
     */
    @DeleteMapping("/carrier/{carrierId}/shipment/{shipmentId}")
    public ResponseEntity<Map<String, Object>> cancelShipment(
            @PathVariable String carrierId,
            @PathVariable String shipmentId) {
        log.info("REST request to cancel shipment with ID: {} using carrier: {}", shipmentId, carrierId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("carrierId", carrierId);
        response.put("shipmentId", shipmentId);
        
        Optional<ShippingProviderAdapter> adapter = adapterRegistry.getAdapter(carrierId);
        if (adapter.isEmpty()) {
            log.error("Provider not found for carrier: {}", carrierId);
            response.put("success", false);
            response.put("error", "Provider not found for carrier: " + carrierId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        try {
            boolean canceled = adapter.get().cancelShipment(shipmentId);
            response.put("success", canceled);
            if (!canceled) {
                response.put("message", "Shipment could not be canceled");
                return ResponseEntity.ok(response);
            }
            response.put("message", "Shipment successfully canceled");
            return ResponseEntity.ok(response);
        } catch (IntegrationException e) {
            log.error("Error canceling shipment with carrier {}: {}", carrierId, e.getMessage(), e);
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check service availability for a specific route and carrier
     * @param carrierId Carrier ID
     * @param origin Origin address
     * @param destination Destination address
     * @return Availability status and details
     */
    @PostMapping("/carrier/{carrierId}/check-availability")
    public ResponseEntity<Map<String, Object>> checkServiceAvailability(
            @PathVariable String carrierId,
            @RequestBody Address origin,
            @RequestBody Address destination) {
        log.info("REST request to check service availability with carrier {} from {} to {}", 
                carrierId, origin.getCountryCode(), destination.getCountryCode());
        
        Map<String, Object> response = new HashMap<>();
        response.put("carrierId", carrierId);
        response.put("origin", origin.getCountryCode());
        response.put("destination", destination.getCountryCode());
        
        Optional<ShippingProviderAdapter> adapter = adapterRegistry.getAdapter(carrierId);
        if (adapter.isEmpty()) {
            log.error("Provider not found for carrier: {}", carrierId);
            response.put("available", false);
            response.put("error", "Provider not found for carrier: " + carrierId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        try {
            boolean available = adapter.get().checkServiceAvailability(origin, destination);
            response.put("available", available);
            return ResponseEntity.ok(response);
        } catch (IntegrationException e) {
            log.error("Error checking service availability with carrier {}: {}", carrierId, e.getMessage(), e);
            response.put("available", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get carriers that support a specific feature
     * @param feature The feature to check (e.g. INTERNATIONAL_SHIPPING, PICKUP_SCHEDULING)
     * @return List of carrier IDs supporting the feature
     */
    @GetMapping("/carriers-supporting/{feature}")
    public ResponseEntity<Map<String, Object>> getCarriersSupportingFeature(
            @PathVariable ShippingProviderAdapter.ProviderFeature feature) {
        log.info("REST request to get carriers supporting feature: {}", feature);
        
        List<String> supportingCarriers = adapterRegistry.getAdaptersSupportingFeature(feature)
                .stream()
                .map(ShippingProviderAdapter::getProviderCode)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("feature", feature.name());
        response.put("supportedBy", supportingCarriers);
        response.put("count", supportingCarriers.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all available carriers in the system
     * @return List of all registered carrier IDs and their supported features
     */
    @GetMapping("/carriers")
    public ResponseEntity<List<Map<String, Object>>> getAllCarriers() {
        log.info("REST request to get all available carriers");
        
        List<Map<String, Object>> carriers = adapterRegistry.getAllAdapters().stream()
                .map(adapter -> {
                    Map<String, Object> carrierInfo = new HashMap<>();
                    carrierInfo.put("carrierId", adapter.getProviderCode());
                    
                    // Get supported features
                    List<String> supportedFeatures = adapter.getSupportedFeatures().stream()
                            .map(Enum::name)
                            .collect(Collectors.toList());
                    carrierInfo.put("supportedFeatures", supportedFeatures);
                    
                    // Check if this is the default adapter
                    boolean isDefault = adapterRegistry.getDefaultAdapter().map(defaultAdapter -> 
                            defaultAdapter.getProviderCode().equals(adapter.getProviderCode())).orElse(false);
                    carrierInfo.put("isDefault", isDefault);
                    
                    return carrierInfo;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(carriers);
    }
}
