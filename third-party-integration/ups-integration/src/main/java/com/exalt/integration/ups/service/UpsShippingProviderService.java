package com.exalt.integration.ups.service;

import com.exalt.integration.common.model.*;
import com.exalt.integration.common.model.ShipmentResponse.ShipmentError;
import com.exalt.integration.common.model.ShipmentResponse.ShipmentError.ErrorSeverity;
import com.exalt.integration.common.service.AbstractShippingProviderService;
import com.exalt.integration.ups.client.UpsApiClient;
import com.exalt.integration.ups.config.UpsProperties;
import com.exalt.integration.ups.mapper.UpsRequestMapper;
import com.exalt.integration.ups.mapper.UpsResponseMapper;
import com.exalt.integration.ups.model.UpsShipmentRequest;
import com.exalt.integration.ups.model.UpsShipmentResponse;
import com.exalt.integration.ups.model.UpsTrackingResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UPS implementation of the ShippingProviderService interface.
 * This service handles communication with the UPS API for shipment creation,
 * tracking, and other shipping operations.
 */
@Service
@Slf4j
public class UpsShippingProviderService extends AbstractShippingProviderService {

    private static final String CARRIER_ID = "UPS";
    private static final String CARRIER_NAME = "UPS";
    
    private final UpsApiClient apiClient;
    private final UpsProperties properties;
    private final UpsRequestMapper requestMapper;
    private final UpsResponseMapper responseMapper;
    
    @Autowired
    public UpsShippingProviderService(
            UpsApiClient apiClient,
            UpsProperties properties,
            UpsRequestMapper requestMapper,
            UpsResponseMapper responseMapper) {
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
        log.info("Creating UPS shipment for reference ID: {}", request.getReferenceId());
        
        try {
            // Convert to UPS-specific request format
            UpsShipmentRequest upsRequest = requestMapper.toUpsShipmentRequest(request);
            
            // Call UPS API
            UpsShipmentResponse upsResponse = apiClient.createShipment(upsRequest);
            
            // Convert to standardized response
            return responseMapper.toStandardShipmentResponse(upsResponse, request);
        } catch (Exception e) {
            log.error("Error creating UPS shipment: {}", e.getMessage(), e);
            
            // Create error response
            List<ShipmentError> errors = createErrorResponse("Failed to create UPS shipment: " + e.getMessage(), 
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
        log.info("Tracking UPS shipment with tracking number: {}", request.getTrackingNumber());
        
        try {
            // Call UPS tracking API
            UpsTrackingResponse upsTrackingResponse = apiClient.trackShipment(request.getTrackingNumber());
            
            // Convert to standardized response
            return responseMapper.toStandardTrackingResponse(upsTrackingResponse, request);
        } catch (Exception e) {
            log.error("Error tracking UPS shipment: {}", e.getMessage(), e);
            
            // Create error response
            List<ShipmentError> errors = createErrorResponse("Failed to track UPS shipment: " + e.getMessage(), 
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
        log.info("Cancelling UPS shipment with ID: {}", shipmentId);
        
        try {
            return apiClient.voidShipment(shipmentId);
        } catch (Exception e) {
            log.error("Error cancelling UPS shipment: {}", e.getMessage(), e);
            throw new Exception("Failed to cancel UPS shipment: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected boolean validateAddressInternal(Map<String, String> addressData) throws Exception {
        log.info("Validating UPS address: {}", addressData);
        
        try {
            // Convert map to address object
            Address address = convertMapToAddress(addressData);
            
            // Call UPS address validation API
            return apiClient.validateAddress(requestMapper.toUpsAddressValidationRequest(address));
        } catch (Exception e) {
            log.error("Error validating UPS address: {}", e.getMessage(), e);
            throw new Exception("Failed to validate UPS address: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected Map<String, Object> calculateRatesInternal(ShipmentRequest request) throws Exception {
        log.info("Calculating UPS rates for reference ID: {}", request.getReferenceId());
        
        try {
            // Convert to UPS-specific rate request
            UpsShipmentRequest rateRequest = requestMapper.toUpsRateRequest(request);
            
            // Call UPS rate API
            Map<String, Object> upsRates = apiClient.calculateRates(rateRequest);
            
            // Convert to standardized rate response
            return responseMapper.toStandardRateResponse(upsRates, request);
        } catch (Exception e) {
            log.error("Error calculating UPS rates: {}", e.getMessage(), e);
            throw new Exception("Failed to calculate UPS rates: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected Map<String, Object> schedulePickupInternal(Map<String, Object> pickupData) throws Exception {
        log.info("Scheduling UPS pickup: {}", pickupData);
        
        try {
            // Convert to UPS-specific pickup request
            Map<String, Object> upsPickupRequest = requestMapper.toUpsPickupRequest(pickupData);
            
            // Call UPS pickup API
            Map<String, Object> upsPickupResponse = apiClient.schedulePickup(upsPickupRequest);
            
            // Convert to standardized pickup response
            return responseMapper.toStandardPickupResponse(upsPickupResponse);
        } catch (Exception e) {
            log.error("Error scheduling UPS pickup: {}", e.getMessage(), e);
            throw new Exception("Failed to schedule UPS pickup: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected boolean isProviderAvailable() {
        try {
            return apiClient.checkHealth();
        } catch (Exception e) {
            log.error("UPS provider health check failed: {}", e.getMessage(), e);
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
     * Get cost optimization suggestions specific to UPS.
     * Overrides the default implementation to provide UPS-specific suggestions.
     */
    @Override
    public Map<String, Object> getCostOptimizationSuggestions(Map<String, Object> shipmentData) {
        Map<String, Object> suggestions = new HashMap<>();
        suggestions.put("carrierId", getCarrierId());
        suggestions.put("carrierName", getCarrierName());
        
        List<Map<String, Object>> optimizationTips = new ArrayList<>();
        
        // Add UPS-specific optimization tips
        Map<String, Object> tip1 = new HashMap<>();
        tip1.put("type", "PACKAGING");
        tip1.put("description", "Use UPS packaging for better rates");
        tip1.put("potentialSavings", "3-8%");
        optimizationTips.add(tip1);
        
        Map<String, Object> tip2 = new HashMap<>();
        tip2.put("type", "SERVICE_SELECTION");
        tip2.put("description", "Consider UPS Ground instead of Air for non-urgent shipments");
        tip2.put("potentialSavings", "Up to 40%");
        optimizationTips.add(tip2);
        
        Map<String, Object> tip3 = new HashMap<>();
        tip3.put("type", "ACCOUNT");
        tip3.put("description", "Leverage UPS Connect for small business discounts");
        tip3.put("potentialSavings", "10-15% for small business accounts");
        optimizationTips.add(tip3);
        
        Map<String, Object> tip4 = new HashMap<>();
        tip4.put("type", "DIMENSIONS");
        tip4.put("description", "Optimize package dimensions to avoid dimensional weight charges");
        tip4.put("potentialSavings", "Up to 30% on oversized packages");
        optimizationTips.add(tip4);
        
        suggestions.put("suggestions", optimizationTips);
        return suggestions;
    }
} 