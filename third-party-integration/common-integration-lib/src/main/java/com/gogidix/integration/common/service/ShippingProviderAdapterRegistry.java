package com.gogidix.integration.common.service;

import com.gogidix.integration.common.exception.IntegrationException;
import com.gogidix.integration.common.model.Provider;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing shipping provider adapter registry.
 * This service is responsible for registering, retrieving, and managing shipping provider adapters.
 */
public interface ShippingProviderAdapterRegistry {

    /**
     * Register a shipping provider adapter in the registry
     * @param adapter The adapter to register
     */
    void registerAdapter(ShippingProviderAdapter adapter);

    /**
     * Get a shipping provider adapter by provider code
     * @param providerCode The provider code
     * @return Optional containing the adapter if found
     */
    Optional<ShippingProviderAdapter> getAdapter(String providerCode);

    /**
     * Get a shipping provider adapter for a provider
     * @param provider The provider
     * @return Optional containing the adapter if found
     */
    Optional<ShippingProviderAdapter> getAdapter(Provider provider);

    /**
     * Get all registered adapters
     * @return List of all registered adapters
     */
    List<ShippingProviderAdapter> getAllAdapters();

    /**
     * Get adapters that support a specific feature
     * @param feature The feature to check for
     * @return List of adapters supporting the feature
     */
    List<ShippingProviderAdapter> getAdaptersSupportingFeature(ShippingProviderAdapter.ProviderFeature feature);

    /**
     * Get an adapter for the default provider
     * @return Optional containing the default adapter if found
     * @throws IntegrationException if no default provider is configured
     */
    Optional<ShippingProviderAdapter> getDefaultAdapter() throws IntegrationException;

    /**
     * Check if an adapter exists for a provider code
     * @param providerCode The provider code
     * @return true if an adapter exists, false otherwise
     */
    boolean hasAdapter(String providerCode);

    /**
     * Unregister an adapter
     * @param providerCode The provider code
     * @return true if successfully unregistered, false if not found
     */
    boolean unregisterAdapter(String providerCode);
}
