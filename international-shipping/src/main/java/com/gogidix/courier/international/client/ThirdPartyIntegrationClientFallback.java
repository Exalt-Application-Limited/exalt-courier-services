package com.gogidix.courier.international.client;

import com.gogidix.courier.international.exception.CarrierCommunicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fallback implementation for ThirdPartyIntegrationClient to handle service unavailability.
 */
@Component
public class ThirdPartyIntegrationClientFallback implements ThirdPartyIntegrationClient {
    
    private static final Logger log = LoggerFactory.getLogger(ThirdPartyIntegrationClientFallback.class);

    @Override
    public ResponseEntity<Map<String, Object>> createShipment(String carrierId, Map<String, Object> shipmentRequest) {
        log.error("Fallback: Unable to create shipment with carrier {}", carrierId);
        throw new CarrierCommunicationException("Unable to connect to carrier: " + carrierId);
    }

    @Override
    public ResponseEntity<Map<String, Object>> cancelShipment(String carrierId, String trackingNumber) {
        log.error("Fallback: Unable to cancel shipment with tracking number {} from carrier {}", 
                trackingNumber, carrierId);
        throw new CarrierCommunicationException(
                "Unable to cancel shipment with tracking number: " + trackingNumber + 
                " from carrier: " + carrierId);
    }

    @Override
    public ResponseEntity<Map<String, Object>> trackShipment(String carrierId, Map<String, Object> trackingRequest) {
        log.error("Fallback: Unable to track shipment with carrier {}", carrierId);
        
        // Return a minimal response with unknown status
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UNKNOWN");
        response.put("trackingNumber", trackingRequest.get("trackingNumber"));
        response.put("message", "Tracking information unavailable due to carrier service disruption");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> checkServiceAvailability(
            String originCode, String destinationCode) {
        log.error("Fallback: Unable to check service availability between {} and {}", originCode, destinationCode);
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getCarriersByFeature(String feature) {
        log.error("Fallback: Unable to get carriers supporting feature {}", feature);
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getAllCarriers() {
        log.error("Fallback: Unable to get all carriers");
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getCarrierDetails(String carrierId) {
        log.error("Fallback: Unable to get details for carrier {}", carrierId);
        
        // Return minimal carrier info
        Map<String, Object> response = new HashMap<>();
        response.put("id", carrierId);
        response.put("name", carrierId.toUpperCase());
        response.put("available", false);
        response.put("message", "Carrier information unavailable due to service disruption");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Map<String, Object>> generateLabel(String carrierId, String shipmentId) {
        log.error("Fallback: Unable to generate label for shipment {} with carrier {}", shipmentId, carrierId);
        throw new CarrierCommunicationException(
                "Unable to generate label for shipment: " + shipmentId + " with carrier: " + carrierId);
    }

    @Override
    public ResponseEntity<Map<String, Object>> generateCustomsDocument(
            String carrierId, String shipmentId, String documentType) {
        log.error("Fallback: Unable to generate customs document {} for shipment {} with carrier {}", 
                documentType, shipmentId, carrierId);
        throw new CarrierCommunicationException(
                "Unable to generate customs document for shipment: " + shipmentId + " with carrier: " + carrierId);
    }
}
