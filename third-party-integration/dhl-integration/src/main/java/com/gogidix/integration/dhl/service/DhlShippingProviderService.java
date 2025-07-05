package com.gogidix.integration.dhl.service;

import com.gogidix.integration.common.model.*;
import com.gogidix.integration.common.model.ShipmentResponse.ShipmentError;
import com.gogidix.integration.common.model.ShipmentResponse.ShipmentError.ErrorSeverity;
import com.gogidix.integration.common.service.AbstractShippingProviderService;
import com.gogidix.integration.dhl.client.DhlApiClient;
import com.gogidix.integration.dhl.config.DhlProperties;
import com.gogidix.integration.dhl.mapper.DhlRequestMapper;
import com.gogidix.integration.dhl.mapper.DhlResponseMapper;
import com.gogidix.integration.dhl.model.DhlShipmentRequest;
import com.gogidix.integration.dhl.model.DhlShipmentResponse;
import com.gogidix.integration.dhl.model.DhlTrackingResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DHL implementation of the ShippingProviderService interface.
 * This service handles communication with the DHL API for shipment creation,
 * tracking, and other shipping operations.
 */
@Service
@Slf4j
public class DhlShippingProviderService extends AbstractShippingProviderService {

    private static final String CARRIER_ID = "DHL";
    private static final String CARRIER_NAME = "DHL";
    
    private final DhlApiClient apiClient;
    private final DhlProperties properties;
    private final DhlRequestMapper requestMapper;
    private final DhlResponseMapper responseMapper;
    
    @Autowired
    public DhlShippingProviderService(
            DhlApiClient apiClient,
            DhlProperties properties,
            DhlRequestMapper requestMapper,
            DhlResponseMapper responseMapper) {
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
        log.info("Creating DHL shipment for reference ID: {}", request.getReferenceId());
        
        try {
            // Convert to DHL-specific request format
            DhlShipmentRequest dhlRequest = requestMapper.toDhlShipmentRequest(request);
            
            // Call DHL API
            DhlShipmentResponse dhlResponse = apiClient.createShipment(dhlRequest);
            
            // Convert to standardized response
            return responseMapper.toStandardShipmentResponse(dhlResponse, request);
        } catch (Exception e) {
            log.error("Error creating DHL shipment: {}", e.getMessage(), e);
            
            // Create error response
            List<ShipmentError> errors = createErrorResponse("Failed to create DHL shipment: " + e.getMessage(), 
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
        log.info("Tracking DHL shipment with tracking number: {}", request.getTrackingNumber());
        
        try {
            // Call DHL tracking API
            DhlTrackingResponse dhlTrackingResponse = apiClient.trackShipment(request.getTrackingNumber());
            
            // Convert to standardized response
            return responseMapper.toStandardTrackingResponse(dhlTrackingResponse, request);
        } catch (Exception e) {
            log.error("Error tracking DHL shipment: {}", e.getMessage(), e);
            
            // Create error response
            List<ShipmentError> errors = createErrorResponse("Failed to track DHL shipment: " + e.getMessage(), 
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
        log.info("Cancelling DHL shipment with ID: {}", shipmentId);
        
        try {
            return apiClient.cancelShipment(shipmentId);
        } catch (Exception e) {
            log.error("Error cancelling DHL shipment: {}", e.getMessage(), e);
            throw new Exception("Failed to cancel DHL shipment: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected boolean validateAddressInternal(Map<String, String> addressData) throws Exception {
        log.info("Validating DHL address: {}", addressData);
        
        try {
            // Convert map to address object
            Address address = convertMapToAddress(addressData);
            
            // Call DHL address validation API
            return apiClient.validateAddress(requestMapper.toDhlAddressValidationRequest(address));
        } catch (Exception e) {
            log.error("Error validating DHL address: {}", e.getMessage(), e);
            throw new Exception("Failed to validate DHL address: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected Map<String, Object> calculateRatesInternal(ShipmentRequest request) throws Exception {
        log.info("Calculating DHL rates for reference ID: {}", request.getReferenceId());
        
        try {
            // Convert to DHL-specific rate request
            DhlShipmentRequest rateRequest = requestMapper.toDhlRateRequest(request);
            
            // Call DHL rate API
            Map<String, Object> dhlRates = apiClient.calculateRates(rateRequest);
            
            // Convert to standardized rate response
            return responseMapper.toStandardRateResponse(dhlRates, request);
        } catch (Exception e) {
            log.error("Error calculating DHL rates: {}", e.getMessage(), e);
            throw new Exception("Failed to calculate DHL rates: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected Map<String, Object> schedulePickupInternal(Map<String, Object> pickupData) throws Exception {
        log.info("Scheduling DHL pickup: {}", pickupData);
        
        try {
            // Convert to DHL-specific pickup request
            Map<String, Object> dhlPickupRequest = requestMapper.toDhlPickupRequest(pickupData);
            
            // Call DHL pickup API
            Map<String, Object> dhlPickupResponse = apiClient.schedulePickup(dhlPickupRequest);
            
            // Convert to standardized pickup response
            return responseMapper.toStandardPickupResponse(dhlPickupResponse);
        } catch (Exception e) {
            log.error("Error scheduling DHL pickup: {}", e.getMessage(), e);
            throw new Exception("Failed to schedule DHL pickup: " + e.getMessage(), e);
        }
    }
    
    @Override
    protected boolean isProviderAvailable() {
        try {
            return apiClient.checkHealth();
        } catch (Exception e) {
            log.error("DHL provider health check failed: {}", e.getMessage(), e);
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
     * Get cost optimization suggestions specific to DHL.
     * Overrides the default implementation to provide DHL-specific suggestions.
     */
    @Override
    public Map<String, Object> getCostOptimizationSuggestions(Map<String, Object> shipmentData) {
        Map<String, Object> suggestions = new HashMap<>();
        suggestions.put("carrierId", getCarrierId());
        suggestions.put("carrierName", getCarrierName());
        
        List<Map<String, Object>> optimizationTips = new ArrayList<>();
        
        // Add DHL-specific optimization tips
        Map<String, Object> tip1 = new HashMap<>();
        tip1.put("type", "INTERNATIONAL");
        tip1.put("description", "Use DHL Express for international shipments for better customs clearance");
        tip1.put("potentialSavings", "Reduced customs delays and associated costs");
        optimizationTips.add(tip1);
        
        Map<String, Object> tip2 = new HashMap<>();
        tip2.put("type", "PAPERLESS");
        tip2.put("description", "Utilize paperless trade options for international shipments");
        tip2.put("potentialSavings", "Reduced documentation fees");
        optimizationTips.add(tip2);
        
        Map<String, Object> tip3 = new HashMap<>();
        tip3.put("type", "ACCOUNT");
        tip3.put("description", "Enroll in DHL's Business Account program for volume discounts");
        tip3.put("potentialSavings", "5-20% depending on volume");
        optimizationTips.add(tip3);
        
        Map<String, Object> tip4 = new HashMap<>();
        tip4.put("type", "CONSOLIDATION");
        tip4.put("description", "Consolidate multiple packages to same destination");
        tip4.put("potentialSavings", "15-25% on shipping costs");
        optimizationTips.add(tip4);
        
        suggestions.put("suggestions", optimizationTips);
        return suggestions;
    }
} 