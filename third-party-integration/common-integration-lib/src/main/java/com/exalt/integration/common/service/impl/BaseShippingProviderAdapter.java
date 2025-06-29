package com.exalt.integration.common.service.impl;

import com.exalt.integration.common.exception.IntegrationException;
import com.exalt.integration.common.model.Address;
import com.exalt.integration.common.model.ProviderCredential;
import com.exalt.integration.common.model.ShipmentRequest;
import com.exalt.integration.common.model.ShipmentResponse;
import com.exalt.integration.common.model.TrackingRequest;
import com.exalt.integration.common.model.TrackingResponse;
import com.exalt.integration.common.service.ShippingProviderAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.EnumMap;
import java.util.Map;

/**
 * Abstract base implementation of ShippingProviderAdapter that provides common functionality
 * for all shipping provider adapters.
 */
@Slf4j
public abstract class BaseShippingProviderAdapter implements ShippingProviderAdapter {

    // Map to hold supported features
    private final Map<ProviderFeature, Boolean> supportedFeatures = new EnumMap<>(ProviderFeature.class);

    /**
     * Constructor that initializes supported features
     */
    protected BaseShippingProviderAdapter() {
        initializeSupportedFeatures();
    }

    /**
     * Initialize the map of supported features.
     * Each concrete adapter implementation should override this to specify which features it supports.
     */
    protected void initializeSupportedFeatures() {
        // By default, support basic features
        supportedFeatures.put(ProviderFeature.SHIPMENT_CREATION, true);
        supportedFeatures.put(ProviderFeature.TRACKING, true);
        
        // Default others to false
        for (ProviderFeature feature : ProviderFeature.values()) {
            if (!supportedFeatures.containsKey(feature)) {
                supportedFeatures.put(feature, false);
            }
        }
    }

    @Override
    public boolean supportsFeature(ProviderFeature feature) {
        return supportedFeatures.getOrDefault(feature, false);
    }

    @Override
    public boolean cancelShipment(String referenceId) throws IntegrationException {
        if (!supportsFeature(ProviderFeature.SHIPMENT_CANCELLATION)) {
            log.warn("Provider {} does not support shipment cancellation", getProviderCode());
            return false;
        }
        
        // This should be implemented by providers that support cancellation
        throw new IntegrationException("Shipment cancellation not implemented for provider: " + getProviderCode());
    }

    @Override
    public ProviderCredential refreshAccessToken(ProviderCredential providerCredential) throws IntegrationException {
        // Default implementation for providers that don't use OAuth
        log.info("Token refresh not applicable for provider: {}", getProviderCode());
        return providerCredential;
    }

    @Override
    public boolean checkServiceAvailability(Address origin, Address destination) throws IntegrationException {
        // Default implementation assumes service is available if the provider is enabled
        log.info("Service availability check not specifically implemented for provider: {}", getProviderCode());
        return true;
    }

    /**
     * Helper method to validate request before sending to provider
     * @param shipmentRequest The request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    protected void validateShipmentRequest(ShipmentRequest shipmentRequest) {
        if (shipmentRequest == null) {
            throw new IllegalArgumentException("Shipment request cannot be null");
        }
        
        if (shipmentRequest.getSender() == null) {
            throw new IllegalArgumentException("Sender information is required");
        }
        
        if (shipmentRequest.getRecipient() == null) {
            throw new IllegalArgumentException("Recipient information is required");
        }
        
        if (shipmentRequest.getPackages() == null || shipmentRequest.getPackages().isEmpty()) {
            throw new IllegalArgumentException("At least one package is required");
        }
    }

    /**
     * Helper method to validate tracking request before sending to provider
     * @param trackingRequest The request to validate
     * @throws IllegalArgumentException if the request is invalid
     */
    protected void validateTrackingRequest(TrackingRequest trackingRequest) {
        if (trackingRequest == null) {
            throw new IllegalArgumentException("Tracking request cannot be null");
        }
        
        if (trackingRequest.getTrackingNumber() == null || trackingRequest.getTrackingNumber().isEmpty()) {
            throw new IllegalArgumentException("Tracking number is required");
        }
    }

    /**
     * Set a feature's support status
     * @param feature The feature to set
     * @param supported Whether the feature is supported
     */
    protected void setFeatureSupport(ProviderFeature feature, boolean supported) {
        supportedFeatures.put(feature, supported);
    }

    /**
     * Log detailed error information for integration exceptions
     * @param message Error message
     * @param e Exception that occurred
     * @return IntegrationException with details
     */
    protected IntegrationException logAndCreateIntegrationException(String message, Exception e) {
        log.error("{} for provider {}: {}", message, getProviderCode(), e.getMessage(), e);
        return new IntegrationException(message + " for provider " + getProviderCode() + ": " + e.getMessage(), e);
    }
}
