package com.exalt.integration.fedex.service;

import com.exalt.integration.common.model.*;
import com.exalt.integration.common.model.ShipmentResponse.ShipmentError;
import com.exalt.integration.common.model.ShipmentResponse.ShipmentError.ErrorSeverity;
import com.exalt.integration.common.service.AbstractShippingProviderService;
import com.exalt.integration.fedex.client.FedExApiClient;
import com.exalt.integration.fedex.config.FedExProperties;
import com.exalt.integration.fedex.mapper.FedExRequestMapper;
import com.exalt.integration.fedex.mapper.FedExResponseMapper;
import com.exalt.integration.fedex.model.FedExErrorResponse;
import com.exalt.integration.fedex.model.FedExShipmentRequest;
import com.exalt.integration.fedex.model.FedExShipmentResponse;
import com.exalt.integration.fedex.model.FedExTrackingResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FedEx implementation of the ShippingProviderService interface.
 * This service handles communication with the FedEx API for shipment creation,
 * tracking, and other shipping operations.
 */
@Service
@Slf4j
public class FedExShippingProviderService extends AbstractShippingProviderService {

    private static final String CARRIER_ID = "FEDEX";
    private static final String CARRIER_NAME = "FedEx";
    
    private final FedExApiClient apiClient;
    private final FedExProperties properties;
    private final FedExRequestMapper requestMapper;
    private final FedExResponseMapper responseMapper;
    
    @Autowired
    public FedExShippingProviderService(
            FedExApiClient apiClient,
            FedExProperties properties,
            FedExRequestMapper requestMapper,
            FedExResponseMapper responseMapper) {
        this.apiClient = apiClient;
        this.properties = properties;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }
    
    @Override
    public String getCarrierId() {
        return CARRIER_ID;
    }
    
    @Override
    public String getCarrierName() {
        return CARRIER_NAME;
    }
    
    @Override
    protected ShipmentResponse createShipmentInternal(ShipmentRequest request) throws Exception {
        log.info("Creating FedEx shipment for reference ID: {}", request.getReferenceId());
        
        try {
            // Convert to FedEx-specific request format
            FedExShipmentRequest fedExRequest = requestMapper.toFedExShipmentRequest(request);
            
            // Call FedEx API
            FedExShipmentResponse fedExResponse = apiClient.createShipment(fedExRequest);
            
            // Convert to standardized response
            return responseMapper.toStandardShipmentResponse(fedExResponse, request);
        } catch (Exception e) {
            log.error("Error creating FedEx shipment: {}", e.getMessage(), e);
            
            // Create error response
            List<ShipmentError> errors = createErrorResponse("Failed to create FedEx shipment: " + e.getMessage(), 
                    ErrorSeverity.ERROR);
            
            // Return failure response
            return ShipmentResponse.builder()
                    .success(false)
                    .referenceId(request.getReferenceId())
                    .carrierId(CARRIER_ID)
                    .createdAt(LocalDateTime.now())
                    .errors(errors)
                    .build();
        }
    }
    
    @Override
    protected TrackingResponse trackShipmentInternal(TrackingRequest request) throws Exception {
        log.info("Tracking FedEx shipment with tracking number: {}", request.getTrackingNumber());
        
        try {
            // Call FedEx tracking API
            FedExTrackingResponse fedExTrackingResponse = apiClient.trackShipment(request.getTrackingNumber());
            
            // Convert to standardized response
            return responseMapper.toStandardTrackingResponse(fedExTrackingResponse, request);
        } catch (Exception e) {
            log.error("Error tracking FedEx shipment: {}", e.getMessage(), e);
            
            // Create error response
            List<ShipmentError> errors = createErrorResponse("Failed to track FedEx shipment: " + e.getMessage(), 
                    ErrorSeverity.ERROR);
            
            // Return failure response
            return TrackingResponse.builder()
                    .success(false)
                    .trackingNumber(request.getTrackingNumber())
                    .carrierId(CARRIER_ID)
                    .carrierName(CARRIER_NAME)
                    .referenceId(request.getReferenceId())
                    .status(TrackingResponse.TrackingStatus.UNKNOWN)
                    .errors(errors)
                    .build();
        }
    }
    
    @Override
    protected boolean cancelShipmentInternal(String shipmentId) throws Exception {
        log.info("Cancelling FedEx shipment with ID: {}", shipmentId);
        
        try {
            return apiClient.cancelShipment(shipmentId);
        } catch (Exception e) {
            log.error("Error cancelling FedEx shipment: {}", e.getMessage(), e);
            throw new Exception("Failed to cancel FedEx shipment: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected boolean validateAddressInternal(Map<String, String> addressData) throws Exception {
        log.info("Validating FedEx address: {}", addressData);
        
        try {
            // Convert map to address object
            Address address = convertMapToAddress(addressData);
            
            // Call FedEx address validation API
            return apiClient.validateAddress(requestMapper.toFedExAddressValidationRequest(address));
        } catch (Exception e) {
            log.error("Error validating FedEx address: {}", e.getMessage(), e);
            throw new Exception("Failed to validate FedEx address: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected Map<String, Object> calculateRatesInternal(ShipmentRequest request) throws Exception {
        log.info("Calculating FedEx rates for reference ID: {}", request.getReferenceId());
        
        try {
            // Convert to FedEx-specific rate request
            FedExShipmentRequest rateRequest = requestMapper.toFedExRateRequest(request);
            
            // Call FedEx rate API
            Map<String, Object> fedExRates = apiClient.calculateRates(rateRequest);
            
            // Convert to standardized rate response
            return responseMapper.toStandardRateResponse(fedExRates, request);
        } catch (Exception e) {
            log.error("Error calculating FedEx rates: {}", e.getMessage(), e);
            throw new Exception("Failed to calculate FedEx rates: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected Map<String, Object> schedulePickupInternal(Map<String, Object> pickupData) throws Exception {
        log.info("Scheduling FedEx pickup: {}", pickupData);
        
        try {
            // Convert to FedEx-specific pickup request
            Map<String, Object> fedExPickupRequest = requestMapper.toFedExPickupRequest(pickupData);
            
            // Call FedEx pickup API
            Map<String, Object> fedExPickupResponse = apiClient.schedulePickup(fedExPickupRequest);
            
            // Convert to standardized pickup response
            return responseMapper.toStandardPickupResponse(fedExPickupResponse);
        } catch (Exception e) {
            log.error("Error scheduling FedEx pickup: {}", e.getMessage(), e);
            throw new Exception("Failed to schedule FedEx pickup: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected boolean isProviderAvailable() {
        try {
            return apiClient.checkHealth();
        } catch (Exception e) {
            log.error("FedEx provider health check failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Convert a map of address data to an Address object.
     *
     * @param addressData the map of address data
     * @return the Address object
     */
    private Address convertMapToAddress(Map<String, String> addressData) {
        return Address.builder()
                .contactName(addressData.get("contactName"))
                .companyName(addressData.get("companyName"))
                .street1(addressData.get("street1"))
                .street2(addressData.get("street2"))
                .city(addressData.get("city"))
                .stateProvince(addressData.get("stateProvince"))
                .postalCode(addressData.get("postalCode"))
                .countryCode(addressData.get("countryCode"))
                .phoneNumber(addressData.get("phoneNumber"))
                .email(addressData.get("email"))
                .residential(Boolean.parseBoolean(addressData.getOrDefault("residential", "false")))
                .taxId(addressData.get("taxId"))
                .vatNumber(addressData.get("vatNumber"))
                .eoriNumber(addressData.get("eoriNumber"))
                .build();
    }
    
    /**
     * Get cost optimization suggestions specific to FedEx.
     * Overrides the default implementation to provide FedEx-specific suggestions.
     */
    @Override
    public Map<String, Object> getCostOptimizationSuggestions(Map<String, Object> shipmentData) {
        Map<String, Object> suggestions = new HashMap<>();
        suggestions.put("carrierId", getCarrierId());
        suggestions.put("carrierName", getCarrierName());
        
        List<Map<String, Object>> optimizationTips = new ArrayList<>();
        
        // Add FedEx-specific optimization tips
        Map<String, Object> tip1 = new HashMap<>();
        tip1.put("type", "PACKAGING");
        tip1.put("description", "Use FedEx packaging for better rates");
        tip1.put("potentialSavings", "5-10%");
        optimizationTips.add(tip1);
        
        Map<String, Object> tip2 = new HashMap<>();
        tip2.put("type", "TIMING");
        tip2.put("description", "Ship on weekdays to avoid weekend surcharges");
        tip2.put("potentialSavings", "Up to 15%");
        optimizationTips.add(tip2);
        
        Map<String, Object> tip3 = new HashMap<>();
        tip3.put("type", "ACCOUNT");
        tip3.put("description", "Consider negotiating volume discounts with FedEx");
        tip3.put("potentialSavings", "10-20% for high volume");
        optimizationTips.add(tip3);
        
        suggestions.put("suggestions", optimizationTips);
        return suggestions;
    }
} 