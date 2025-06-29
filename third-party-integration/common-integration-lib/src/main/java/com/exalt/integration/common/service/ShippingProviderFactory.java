package com.exalt.integration.common.service;

import com.exalt.integration.common.exception.UnsupportedCarrierException;
import com.exalt.integration.common.model.TrackingRequest.CarrierId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for obtaining the appropriate ShippingProviderService based on carrier ID.
 * This class will automatically discover all implementations of ShippingProviderService
 * and make them available by carrier ID.
 */
@Component
@Slf4j
public class ShippingProviderFactory {
    
    private final Map<String, ShippingProviderService> providerMap = new HashMap<>();
    
    /**
     * Constructor that automatically maps all available shipping provider implementations
     * by their carrier ID.
     *
     * @param providers the list of all available shipping provider services
     */
    @Autowired
    public ShippingProviderFactory(List<ShippingProviderService> providers) {
        for (ShippingProviderService provider : providers) {
            providerMap.put(provider.getCarrierId(), provider);
            log.info("Registered shipping provider: {} ({})", 
                    provider.getCarrierName(), provider.getCarrierId());
        }
        
        log.info("ShippingProviderFactory initialized with {} providers", providerMap.size());
    }
    
    /**
     * Get a shipping provider service by carrier ID string.
     *
     * @param carrierId the carrier ID string (e.g., "FEDEX", "UPS", "DHL")
     * @return the corresponding shipping provider service
     * @throws UnsupportedCarrierException if the carrier ID is not supported
     */
    public ShippingProviderService getProviderByCarrierId(String carrierId) {
        ShippingProviderService provider = providerMap.get(carrierId);
        
        if (provider == null) {
            throw new UnsupportedCarrierException("Unsupported carrier ID: " + carrierId);
        }
        
        return provider;
    }
    
    /**
     * Get a shipping provider service by CarrierId enum.
     *
     * @param carrierId the CarrierId enum value
     * @return the corresponding shipping provider service
     * @throws UnsupportedCarrierException if the carrier ID is not supported
     */
    public ShippingProviderService getProviderByCarrierId(CarrierId carrierId) {
        return getProviderByCarrierId(carrierId.name());
    }
    
    /**
     * Check if a carrier ID is supported.
     *
     * @param carrierId the carrier ID string
     * @return true if the carrier is supported, false otherwise
     */
    public boolean isCarrierSupported(String carrierId) {
        return providerMap.containsKey(carrierId);
    }
    
    /**
     * Get all registered shipping provider services.
     *
     * @return a map of carrier IDs to shipping provider services
     */
    public Map<String, ShippingProviderService> getAllProviders() {
        return new HashMap<>(providerMap);
    }
    
    /**
     * Get the count of registered shipping provider services.
     *
     * @return the number of registered shipping provider services
     */
    public int getProviderCount() {
        return providerMap.size();
    }
} 