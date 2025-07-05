package com.gogidix.integration.common.controller;

import com.gogidix.integration.common.exception.IntegrationException;
import com.gogidix.integration.common.model.TrackingRequest;
import com.gogidix.integration.common.model.TrackingResponse;
import com.gogidix.integration.common.service.ShippingProviderAdapter;
import com.gogidix.integration.common.service.ShippingProviderAdapterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * REST controller for tracking operations through third-party providers
 */
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
@Slf4j
public class TrackingController {

    private final ShippingProviderAdapterRegistry adapterRegistry;

    /**
     * Track a shipment using a specific carrier/provider
     * @param carrierId The carrier/provider ID to use (should match the enum in TrackingRequest)
     * @param trackingRequest Tracking request with tracking number
     * @return Tracking response with status and history
     */
    @PostMapping("/carrier/{carrierId}")
    public ResponseEntity<TrackingResponse> trackShipment(
            @PathVariable String carrierId,
            @Valid @RequestBody TrackingRequest trackingRequest) {
        log.info("REST request to track shipment with carrier: {}, tracking number: {}", 
                carrierId, trackingRequest.getTrackingNumber());
        
        // Set or override the carrierId in the request based on the path variable
        try {
            TrackingRequest.CarrierId carrierEnum = TrackingRequest.CarrierId.valueOf(carrierId.toUpperCase());
            trackingRequest.setCarrierId(carrierEnum);
        } catch (IllegalArgumentException e) {
            log.error("Invalid carrier ID: {}", carrierId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("Error", "Invalid carrier ID: " + carrierId)
                    .build();
        }
        
        // Find the adapter matching the carrier ID
        Optional<ShippingProviderAdapter> adapter = adapterRegistry.getAdapter(carrierId);
        if (adapter.isEmpty()) {
            log.error("Provider not found for carrier: {}", carrierId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("Error", "Provider not found for carrier: " + carrierId)
                    .build();
        }
        
        try {
            TrackingResponse response = adapter.get().trackShipment(trackingRequest);
            return ResponseEntity.ok(response);
        } catch (IntegrationException e) {
            log.error("Error tracking shipment with carrier {}: {}", carrierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error", e.getMessage())
                    .build();
        }
    }

    /**
     * Track a shipment using the carrier specified in the request
     * @param trackingRequest Tracking request with tracking number and carrier ID
     * @return Tracking response with status and history
     */
    @PostMapping
    public ResponseEntity<TrackingResponse> trackShipmentWithRequestedCarrier(
            @Valid @RequestBody TrackingRequest trackingRequest) {
        log.info("REST request to track shipment with carrier: {}, tracking number: {}", 
                trackingRequest.getCarrierId(), trackingRequest.getTrackingNumber());
        
        String carrierId = trackingRequest.getCarrierId().name();
        Optional<ShippingProviderAdapter> adapter = adapterRegistry.getAdapter(carrierId);
        
        if (adapter.isEmpty()) {
            // Try to use the default adapter if the specific carrier adapter is not found
            adapter = adapterRegistry.getDefaultAdapter();
            if (adapter.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("Error", "Provider not found for carrier: " + carrierId + " and no default provider is configured")
                        .build();
            }
            log.warn("Provider not found for carrier: {}. Using default provider.", carrierId);
        }
        
        try {
            TrackingResponse response = adapter.get().trackShipment(trackingRequest);
            return ResponseEntity.ok(response);
        } catch (IntegrationException e) {
            log.error("Error tracking shipment with carrier {}: {}", carrierId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error", e.getMessage())
                    .build();
        }
    }

    /**
     * Simple tracking endpoint by tracking number (auto-detects carrier or uses default)
     * @param trackingNumber The tracking number to track
     * @return Tracking response with status and history
     */
    @GetMapping("/{trackingNumber}")
    public ResponseEntity<TrackingResponse> trackByTrackingNumber(@PathVariable String trackingNumber) {
        log.info("REST request to track shipment by tracking number: {}", trackingNumber);
        
        // Determine the carrier based on the tracking number format (this is a simplified example)
        TrackingRequest.CarrierId detectedCarrier = detectCarrierFromTrackingNumber(trackingNumber);
        log.info("Detected carrier for tracking number {}: {}", trackingNumber, detectedCarrier);
        
        String carrierId = detectedCarrier.name();
        Optional<ShippingProviderAdapter> adapter = adapterRegistry.getAdapter(carrierId);
        
        if (adapter.isEmpty()) {
            // Try to use the default adapter if the detected carrier adapter is not found
            adapter = adapterRegistry.getDefaultAdapter();
            if (adapter.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .header("Error", "No provider available for tracking")
                        .build();
            }
            log.warn("Provider not found for detected carrier: {}. Using default provider.", carrierId);
        }
        
        try {
            // Create a tracking request with the detected carrier
            TrackingRequest request = new TrackingRequest();
            request.setTrackingNumber(trackingNumber);
            request.setCarrierId(detectedCarrier);
            
            TrackingResponse response = adapter.get().trackShipment(request);
            return ResponseEntity.ok(response);
        } catch (IntegrationException e) {
            log.error("Error tracking shipment with tracking number {}: {}", trackingNumber, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Error", e.getMessage())
                    .build();
        }
    }
    
    /**
     * Simple carrier detection based on tracking number format patterns
     * In a real-world implementation, this would be more sophisticated
     */
    private TrackingRequest.CarrierId detectCarrierFromTrackingNumber(String trackingNumber) {
        // This is a simplified detection logic and should be more comprehensive in a production system
        if (trackingNumber == null || trackingNumber.isEmpty()) {
            return TrackingRequest.CarrierId.CUSTOM;
        }
        
        // Sample logic based on common carrier tracking number formats
        if (trackingNumber.matches("\\d{12}")) {
            return TrackingRequest.CarrierId.FEDEX;
        } else if (trackingNumber.matches("1Z[A-Z0-9]{16}")) {
            return TrackingRequest.CarrierId.UPS;
        } else if (trackingNumber.matches("[0-9]{10}") || trackingNumber.matches("[0-9]{20}")) {
            return TrackingRequest.CarrierId.DHL;
        } else if (trackingNumber.matches("9[0-9]{15,21}")) {
            return TrackingRequest.CarrierId.USPS;
        } else if (trackingNumber.matches("[A-Z]{2}[0-9]{9}[A-Z]{2}")) {
            return TrackingRequest.CarrierId.ROYAL_MAIL;
        }
        
        // Default to custom carrier if format is not recognized
        return TrackingRequest.CarrierId.CUSTOM;
    }
}
