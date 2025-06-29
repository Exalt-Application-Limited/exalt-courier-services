package com.exalt.courier.hqadmin.service.impl;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.GlobalServiceConfig;
import com.socialecommerceecosystem.hqadmin.repository.GlobalRegionRepository;
import com.socialecommerceecosystem.hqadmin.repository.GlobalServiceConfigRepository;
import com.socialecommerceecosystem.hqadmin.service.GlobalServiceConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the GlobalServiceConfigService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GlobalServiceConfigServiceImpl implements GlobalServiceConfigService {

    private final GlobalServiceConfigRepository globalServiceConfigRepository;
    private final GlobalRegionRepository globalRegionRepository;

    @Override
    public List<GlobalServiceConfig> getAllServiceConfigs() {
        return globalServiceConfigRepository.findAll();
    }

    @Override
    public Optional<GlobalServiceConfig> getServiceConfigById(Long id) {
        return globalServiceConfigRepository.findById(id);
    }

    @Override
    public Optional<GlobalServiceConfig> getServiceConfigByKey(String serviceKey) {
        return globalServiceConfigRepository.findByServiceKey(serviceKey);
    }

    @Override
    @Transactional
    public GlobalServiceConfig createServiceConfig(GlobalServiceConfig serviceConfig) {
        log.info("Creating new global service configuration with key: {}", serviceConfig.getServiceKey());
        
        // Verify that the global region exists if provided
        if (serviceConfig.getGlobalRegion() != null && serviceConfig.getGlobalRegion().getId() != null) {
            GlobalRegion region = globalRegionRepository.findById(serviceConfig.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + serviceConfig.getGlobalRegion().getId()));
            serviceConfig.setGlobalRegion(region);
        }
        
        return globalServiceConfigRepository.save(serviceConfig);
    }

    @Override
    @Transactional
    public GlobalServiceConfig updateServiceConfig(Long id, GlobalServiceConfig serviceConfigDetails) {
        log.info("Updating global service configuration with id: {}", id);
        
        GlobalServiceConfig existingConfig = globalServiceConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global service configuration not found with id: " + id));
        
        // Update fields
        existingConfig.setServiceKey(serviceConfigDetails.getServiceKey());
        existingConfig.setName(serviceConfigDetails.getName());
        existingConfig.setDescription(serviceConfigDetails.getDescription());
        existingConfig.setConfigValue(serviceConfigDetails.getConfigValue());
        existingConfig.setConfigType(serviceConfigDetails.getConfigType());
        existingConfig.setIsActive(serviceConfigDetails.getIsActive());
        existingConfig.setAllowRegionalOverride(serviceConfigDetails.getAllowRegionalOverride());
        existingConfig.setServiceCategory(serviceConfigDetails.getServiceCategory());
        existingConfig.setLastUpdatedBy(serviceConfigDetails.getLastUpdatedBy());
        
        // Update global region if provided
        if (serviceConfigDetails.getGlobalRegion() != null && serviceConfigDetails.getGlobalRegion().getId() != null) {
            GlobalRegion newRegion = globalRegionRepository.findById(serviceConfigDetails.getGlobalRegion().getId())
                .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + serviceConfigDetails.getGlobalRegion().getId()));
            existingConfig.setGlobalRegion(newRegion);
        } else {
            existingConfig.setGlobalRegion(null);
        }
        
        return globalServiceConfigRepository.save(existingConfig);
    }

    @Override
    @Transactional
    public void deleteServiceConfig(Long id) {
        log.info("Deleting global service configuration with id: {}", id);
        
        GlobalServiceConfig config = globalServiceConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Global service configuration not found with id: " + id));
        
        globalServiceConfigRepository.delete(config);
    }

    @Override
    public List<GlobalServiceConfig> getAllActiveServiceConfigs() {
        return globalServiceConfigRepository.findByIsActiveTrue();
    }

    @Override
    public List<GlobalServiceConfig> getServiceConfigsByCategory(String category) {
        return globalServiceConfigRepository.findByServiceCategory(category);
    }

    @Override
    public List<GlobalServiceConfig> getServiceConfigsByRegion(Long regionId) {
        log.debug("Getting service configurations for region id: {}", regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalServiceConfigRepository.findByGlobalRegion(region);
    }

    @Override
    public List<GlobalServiceConfig> getServiceConfigsWithRegionalOverrides() {
        return globalServiceConfigRepository.findByAllowRegionalOverrideTrue();
    }

    @Override
    public List<GlobalServiceConfig> getGlobalDefaultConfigs() {
        return globalServiceConfigRepository.findByGlobalRegionIsNull();
    }

    @Override
    public List<GlobalServiceConfig> searchServiceConfigsByName(String searchText) {
        return globalServiceConfigRepository.findByNameContainingIgnoreCase(searchText);
    }

    @Override
    public Map<String, Long> countConfigurationsByCategory() {
        List<Object[]> results = globalServiceConfigRepository.countConfigurationsByCategory();
        Map<String, Long> countMap = new HashMap<>();
        
        for (Object[] result : results) {
            String category = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            countMap.put(category != null ? category : "Uncategorized", count);
        }
        
        return countMap;
    }

    @Override
    public List<GlobalServiceConfig> getMandatoryGlobalConfigs() {
        return globalServiceConfigRepository.findMandatoryGlobalConfigurations();
    }

    @Override
    public List<GlobalServiceConfig> getServiceConfigsByType(String configType) {
        return globalServiceConfigRepository.findByConfigType(configType);
    }

    @Override
    public List<GlobalServiceConfig> getServiceConfigsByRegionAndCategory(Long regionId, String category) {
        log.debug("Getting service configurations for region id: {} and category: {}", regionId, category);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        return globalServiceConfigRepository.findByGlobalRegionAndServiceCategory(region, category);
    }

    @Override
    @Transactional
    public List<GlobalServiceConfig> applyGlobalConfigToRegion(Long regionId, List<String> serviceKeys) {
        log.info("Applying global configurations to region id: {}", regionId);
        
        GlobalRegion region = globalRegionRepository.findById(regionId)
            .orElseThrow(() -> new IllegalArgumentException("Global region not found with id: " + regionId));
        
        List<GlobalServiceConfig> newConfigs = new ArrayList<>();
        
        for (String serviceKey : serviceKeys) {
            globalServiceConfigRepository.findByServiceKey(serviceKey)
                .filter(globalConfig -> globalConfig.getGlobalRegion() == null)
                .filter(globalConfig -> globalConfig.getAllowRegionalOverride())
                .ifPresent(globalConfig -> {
                    // Check if this config already exists for this region
                    boolean exists = globalServiceConfigRepository.findAll().stream()
                        .anyMatch(config -> config.getServiceKey().equals(serviceKey) && 
                                 region.equals(config.getGlobalRegion()));
                    
                    if (!exists) {
                        // Create a new regional config based on the global one
                        GlobalServiceConfig regionalConfig = GlobalServiceConfig.builder()
                            .serviceKey(globalConfig.getServiceKey())
                            .name(globalConfig.getName())
                            .description(globalConfig.getDescription())
                            .configValue(globalConfig.getConfigValue())
                            .configType(globalConfig.getConfigType())
                            .isActive(globalConfig.getIsActive())
                            .allowRegionalOverride(false) // Regional configurations cannot be overridden further
                            .globalRegion(region)
                            .serviceCategory(globalConfig.getServiceCategory())
                            .lastUpdatedBy(globalConfig.getLastUpdatedBy())
                            .build();
                            
                        newConfigs.add(globalServiceConfigRepository.save(regionalConfig));
                    }
                });
        }
        
        return newConfigs;
    }
}
