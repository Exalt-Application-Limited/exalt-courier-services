package com.gogidix.courier.hqadmin.service;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.GlobalServiceConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface defining operations for managing global service configurations.
 */
public interface GlobalServiceConfigService {
    
    /**
     * Get all global service configurations
     * 
     * @return List of all global service configurations
     */
    List<GlobalServiceConfig> getAllServiceConfigs();
    
    /**
     * Get a global service configuration by ID
     * 
     * @param id The configuration ID
     * @return The global service configuration if found
     */
    Optional<GlobalServiceConfig> getServiceConfigById(Long id);
    
    /**
     * Get a global service configuration by its service key
     * 
     * @param serviceKey The service key
     * @return The global service configuration if found
     */
    Optional<GlobalServiceConfig> getServiceConfigByKey(String serviceKey);
    
    /**
     * Create a new global service configuration
     * 
     * @param serviceConfig The global service configuration to create
     * @return The created global service configuration
     */
    GlobalServiceConfig createServiceConfig(GlobalServiceConfig serviceConfig);
    
    /**
     * Update an existing global service configuration
     * 
     * @param id The configuration ID to update
     * @param serviceConfigDetails The updated configuration details
     * @return The updated global service configuration
     * @throws RuntimeException if configuration not found
     */
    GlobalServiceConfig updateServiceConfig(Long id, GlobalServiceConfig serviceConfigDetails);
    
    /**
     * Delete a global service configuration
     * 
     * @param id The configuration ID to delete
     * @throws RuntimeException if configuration not found
     */
    void deleteServiceConfig(Long id);
    
    /**
     * Get all active global service configurations
     * 
     * @return List of active global service configurations
     */
    List<GlobalServiceConfig> getAllActiveServiceConfigs();
    
    /**
     * Get all service configurations in a specific category
     * 
     * @param category The service category
     * @return List of service configurations in the category
     */
    List<GlobalServiceConfig> getServiceConfigsByCategory(String category);
    
    /**
     * Get all global service configurations for a specific region
     * 
     * @param regionId The global region ID
     * @return List of service configurations for the region
     * @throws RuntimeException if region not found
     */
    List<GlobalServiceConfig> getServiceConfigsByRegion(Long regionId);
    
    /**
     * Get all service configurations that allow regional overrides
     * 
     * @return List of service configurations that allow regional overrides
     */
    List<GlobalServiceConfig> getServiceConfigsWithRegionalOverrides();
    
    /**
     * Get all global default configurations (configurations without region assignment)
     * 
     * @return List of global default configurations
     */
    List<GlobalServiceConfig> getGlobalDefaultConfigs();
    
    /**
     * Search service configurations by name
     * 
     * @param searchText The search text
     * @return List of service configurations matching the search
     */
    List<GlobalServiceConfig> searchServiceConfigsByName(String searchText);
    
    /**
     * Count configurations by category
     * 
     * @return Map of category to configuration count
     */
    Map<String, Long> countConfigurationsByCategory();
    
    /**
     * Get all service configurations that must be applied globally
     * 
     * @return List of service configurations that must be applied globally
     */
    List<GlobalServiceConfig> getMandatoryGlobalConfigs();
    
    /**
     * Find configurations by type
     * 
     * @param configType The configuration type
     * @return List of configurations of the specified type
     */
    List<GlobalServiceConfig> getServiceConfigsByType(String configType);
    
    /**
     * Find service configurations for a specific region and category
     * 
     * @param regionId The global region ID
     * @param category The service category
     * @return List of matching service configurations
     * @throws RuntimeException if region not found
     */
    List<GlobalServiceConfig> getServiceConfigsByRegionAndCategory(Long regionId, String category);
    
    /**
     * Apply global configuration settings to a specific region
     * 
     * @param regionId The global region ID to apply settings to
     * @param serviceKeys List of service keys to apply
     * @return List of newly created regional service configurations
     * @throws RuntimeException if region not found
     */
    List<GlobalServiceConfig> applyGlobalConfigToRegion(Long regionId, List<String> serviceKeys);
}
