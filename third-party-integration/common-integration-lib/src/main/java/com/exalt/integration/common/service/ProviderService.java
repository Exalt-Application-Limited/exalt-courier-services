package com.exalt.integration.common.service;

import com.exalt.integration.common.model.Provider;
import com.exalt.integration.common.model.Provider.ProviderType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing third-party shipping providers
 */
public interface ProviderService {

    /**
     * Get all providers
     * @return List of all providers
     */
    List<Provider> getAllProviders();

    /**
     * Get a provider by ID
     * @param id The provider ID
     * @return Optional provider
     */
    Optional<Provider> getProviderById(Long id);

    /**
     * Get a provider by code
     * @param providerCode The provider code
     * @return Optional provider
     */
    Optional<Provider> getProviderByCode(String providerCode);

    /**
     * Get all enabled providers
     * @return List of enabled providers
     */
    List<Provider> getEnabledProviders();

    /**
     * Create a new provider
     * @param provider The provider to create
     * @return The created provider
     */
    Provider createProvider(Provider provider);

    /**
     * Update an existing provider
     * @param id The provider ID
     * @param provider The updated provider information
     * @return The updated provider
     */
    Provider updateProvider(Long id, Provider provider);

    /**
     * Delete a provider
     * @param id The provider ID
     */
    void deleteProvider(Long id);

    /**
     * Set a provider as the default
     * @param providerId The ID of the provider to set as default
     * @return The updated provider
     */
    Provider setDefaultProvider(Long providerId);

    /**
     * Get providers by type
     * @param providerType The provider type
     * @return List of providers of the specified type
     */
    List<Provider> getProvidersByType(ProviderType providerType);

    /**
     * Get providers that support a specific country
     * @param countryCode ISO country code
     * @return List of providers supporting the country
     */
    List<Provider> getProvidersSupportingCountry(String countryCode);

    /**
     * Update a provider's service mappings
     * @param providerId The provider ID
     * @param serviceMappings Map of service type to provider-specific service code
     * @return The updated provider
     */
    Provider updateServiceMappings(Long providerId, Map<String, String> serviceMappings);

    /**
     * Update a provider's supported countries
     * @param providerId The provider ID
     * @param supportedCountries Map of country codes to country names
     * @return The updated provider
     */
    Provider updateSupportedCountries(Long providerId, Map<String, String> supportedCountries);
    
    /**
     * Get the default provider
     * @return Optional provider
     */
    Optional<Provider> getDefaultProvider();
    
    /**
     * Enable or disable a provider
     * @param providerId The provider ID
     * @param enabled true to enable, false to disable
     * @return The updated provider
     */
    Provider setProviderEnabled(Long providerId, boolean enabled);
}
