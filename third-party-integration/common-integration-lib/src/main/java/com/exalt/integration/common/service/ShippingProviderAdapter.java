package com.exalt.integration.common.service;

import com.exalt.integration.common.model.*;
import com.exalt.integration.common.exception.IntegrationException;
import org.springframework.stereotype.Component;

/**
 * Interface defining the standard operations that any shipping provider adapter must implement.
 * This adapter pattern allows for consistent interaction with different shipping providers
 * while encapsulating the specifics of each provider's API.
 */
public interface ShippingProviderAdapter {

    /**
     * Get the provider code this adapter supports (e.g., "DHL", "UPS", "FEDEX")
     * @return The provider code as a string
     */
    String getProviderCode();

    /**
     * Create a shipment with the shipping provider
     * @param shipmentRequest The standardized shipment request
     * @return Shipment response containing tracking numbers and labels
     * @throws IntegrationException if there's an error during shipment creation
     */
    ShipmentResponse createShipment(ShipmentRequest shipmentRequest) throws IntegrationException;

    /**
     * Track a shipment using its tracking number
     * @param trackingRequest The tracking request containing the tracking number
     * @return Tracking response with current status and history
     * @throws IntegrationException if there's an error during tracking
     */
    TrackingResponse trackShipment(TrackingRequest trackingRequest) throws IntegrationException;

    /**
     * Cancel a shipment if the provider supports it
     * @param referenceId The reference ID of the shipment to cancel
     * @return true if successfully canceled, false if cancellation is not supported or failed
     * @throws IntegrationException if there's an error during cancellation
     */
    boolean cancelShipment(String referenceId) throws IntegrationException;

    /**
     * Validate provider credentials by testing the connection
     * @param providerCredential Credentials to validate
     * @return true if credentials are valid, false otherwise
     */
    boolean validateCredentials(ProviderCredential providerCredential);

    /**
     * Check if the provider supports a specific feature
     * @param feature The feature to check support for
     * @return true if the provider supports the feature, false otherwise
     */
    boolean supportsFeature(ProviderFeature feature);

    /**
     * Refresh the access token if needed (for OAuth-based providers)
     * @param providerCredential The credentials containing the refresh token
     * @return Updated provider credential with new access token and expiry
     * @throws IntegrationException if token refresh fails
     */
    ProviderCredential refreshAccessToken(ProviderCredential providerCredential) throws IntegrationException;

    /**
     * Get service availability for a specific origin-destination pair
     * @param origin The origin address
     * @param destination The destination address
     * @return true if service is available for the route, false otherwise
     * @throws IntegrationException if availability check fails
     */
    boolean checkServiceAvailability(Address origin, Address destination) throws IntegrationException;

    /**
     * Features that may be supported by shipping providers
     */
    enum ProviderFeature {
        SHIPMENT_CREATION,
        TRACKING,
        LABEL_GENERATION,
        RATE_CALCULATION,
        SHIPMENT_CANCELLATION,
        ADDRESS_VALIDATION,
        INTERNATIONAL_SHIPPING,
        CUSTOMS_DOCUMENTATION,
        PICKUP_SCHEDULING,
        SIGNATURE_REQUIRED,
        SATURDAY_DELIVERY,
        DELIVERY_NOTIFICATION,
        CARBON_NEUTRAL_SHIPPING,
        INSURANCE
    }
}
