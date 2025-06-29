package com.exalt.courier.hqadmin.controller;

import com.socialecommerceecosystem.hqadmin.model.GlobalServiceConfig;
import com.socialecommerceecosystem.hqadmin.service.GlobalServiceConfigService;
import com.socialecommerceecosystem.hqadmin.service.GlobalRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing global service configurations.
 */
@RestController
@RequestMapping("/api/v1/service-configs")
@RequiredArgsConstructor
@Slf4j
public class GlobalServiceConfigController {

    private final GlobalServiceConfigService globalServiceConfigService;
    private final GlobalRegionService globalRegionService;

    /**
     * GET /api/v1/service-configs : Get all service configurations
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of service configurations
     */
    @GetMapping
    public ResponseEntity<List<GlobalServiceConfig>> getAllServiceConfigs() {
        log.debug("REST request to get all global service configurations");
        return ResponseEntity.ok(globalServiceConfigService.getAllServiceConfigs());
    }

    /**
     * GET /api/v1/service-configs/active : Get all active service configurations
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active service configurations
     */
    @GetMapping("/active")
    public ResponseEntity<List<GlobalServiceConfig>> getAllActiveServiceConfigs() {
        log.debug("REST request to get all active global service configurations");
        return ResponseEntity.ok(globalServiceConfigService.getAllActiveServiceConfigs());
    }

    /**
     * GET /api/v1/service-configs/{id} : Get a service configuration by id
     * 
     * @param id the id of the service configuration to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the service configuration, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GlobalServiceConfig> getServiceConfig(@PathVariable Long id) {
        log.debug("REST request to get global service configuration : {}", id);
        return globalServiceConfigService.getServiceConfigById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service configuration not found with id: " + id));
    }

    /**
     * GET /api/v1/service-configs/key/{serviceKey} : Get a service configuration by key
     * 
     * @param serviceKey the key of the service configuration to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the service configuration, or with status 404 (Not Found)
     */
    @GetMapping("/key/{serviceKey}")
    public ResponseEntity<GlobalServiceConfig> getServiceConfigByKey(@PathVariable String serviceKey) {
        log.debug("REST request to get global service configuration by key: {}", serviceKey);
        return globalServiceConfigService.getServiceConfigByKey(serviceKey)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service configuration not found with key: " + serviceKey));
    }

    /**
     * GET /api/v1/service-configs/category/{category} : Get service configurations by category
     * 
     * @param category the category of service configurations to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of service configurations in the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<GlobalServiceConfig>> getServiceConfigsByCategory(@PathVariable String category) {
        log.debug("REST request to get global service configurations by category: {}", category);
        return ResponseEntity.ok(globalServiceConfigService.getServiceConfigsByCategory(category));
    }

    /**
     * GET /api/v1/service-configs/region/{regionId} : Get service configurations for a region
     * 
     * @param regionId the id of the region to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of service configurations for the region
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<GlobalServiceConfig>> getServiceConfigsByRegion(@PathVariable Long regionId) {
        log.debug("REST request to get global service configurations for region: {}", regionId);
        
        try {
            List<GlobalServiceConfig> configs = globalServiceConfigService.getServiceConfigsByRegion(regionId);
            return ResponseEntity.ok(configs);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/service-configs/region/{regionId}/category/{category} : Get service configurations for a region and category
     * 
     * @param regionId the id of the region to filter by
     * @param category the category to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of service configurations for the region and category
     */
    @GetMapping("/region/{regionId}/category/{category}")
    public ResponseEntity<List<GlobalServiceConfig>> getServiceConfigsByRegionAndCategory(
            @PathVariable Long regionId,
            @PathVariable String category) {
        log.debug("REST request to get global service configurations for region: {} and category: {}", regionId, category);
        
        try {
            List<GlobalServiceConfig> configs = globalServiceConfigService.getServiceConfigsByRegionAndCategory(regionId, category);
            return ResponseEntity.ok(configs);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/service-configs/with-overrides : Get service configurations that allow regional overrides
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of service configurations that allow regional overrides
     */
    @GetMapping("/with-overrides")
    public ResponseEntity<List<GlobalServiceConfig>> getServiceConfigsWithRegionalOverrides() {
        log.debug("REST request to get global service configurations with regional overrides");
        return ResponseEntity.ok(globalServiceConfigService.getServiceConfigsWithRegionalOverrides());
    }

    /**
     * GET /api/v1/service-configs/global-defaults : Get global default configurations
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of global default configurations
     */
    @GetMapping("/global-defaults")
    public ResponseEntity<List<GlobalServiceConfig>> getGlobalDefaultConfigs() {
        log.debug("REST request to get global default service configurations");
        return ResponseEntity.ok(globalServiceConfigService.getGlobalDefaultConfigs());
    }

    /**
     * GET /api/v1/service-configs/search : Search service configurations by name
     * 
     * @param searchText the text to search for in service configuration names
     * @return the ResponseEntity with status 200 (OK) and the list of matching service configurations
     */
    @GetMapping("/search")
    public ResponseEntity<List<GlobalServiceConfig>> searchServiceConfigsByName(@RequestParam String searchText) {
        log.debug("REST request to search global service configurations by name containing: {}", searchText);
        return ResponseEntity.ok(globalServiceConfigService.searchServiceConfigsByName(searchText));
    }

    /**
     * GET /api/v1/service-configs/type/{configType} : Get service configurations by type
     * 
     * @param configType the type of service configurations to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of service configurations of the specified type
     */
    @GetMapping("/type/{configType}")
    public ResponseEntity<List<GlobalServiceConfig>> getServiceConfigsByType(@PathVariable String configType) {
        log.debug("REST request to get global service configurations by type: {}", configType);
        return ResponseEntity.ok(globalServiceConfigService.getServiceConfigsByType(configType));
    }

    /**
     * GET /api/v1/service-configs/mandatory-global : Get service configurations that must be applied globally
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of service configurations that must be applied globally
     */
    @GetMapping("/mandatory-global")
    public ResponseEntity<List<GlobalServiceConfig>> getMandatoryGlobalConfigs() {
        log.debug("REST request to get mandatory global service configurations");
        return ResponseEntity.ok(globalServiceConfigService.getMandatoryGlobalConfigs());
    }

    /**
     * GET /api/v1/service-configs/count-by-category : Count configurations by category
     * 
     * @return the ResponseEntity with status 200 (OK) and the map of category to configuration count
     */
    @GetMapping("/count-by-category")
    public ResponseEntity<Map<String, Long>> countConfigurationsByCategory() {
        log.debug("REST request to count global service configurations by category");
        return ResponseEntity.ok(globalServiceConfigService.countConfigurationsByCategory());
    }

    /**
     * POST /api/v1/service-configs : Create a new service configuration
     * 
     * @param serviceConfig the service configuration to create
     * @return the ResponseEntity with status 201 (Created) and with body the new service configuration
     */
    @PostMapping
    public ResponseEntity<GlobalServiceConfig> createServiceConfig(@Valid @RequestBody GlobalServiceConfig serviceConfig) {
        log.debug("REST request to save global service configuration : {}", serviceConfig);
        if (serviceConfig.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new service configuration cannot already have an ID");
        }
        
        try {
            GlobalServiceConfig result = globalServiceConfigService.createServiceConfig(serviceConfig);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/service-configs/{id} : Update an existing service configuration
     * 
     * @param id the id of the service configuration to update
     * @param serviceConfig the service configuration to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated service configuration
     */
    @PutMapping("/{id}")
    public ResponseEntity<GlobalServiceConfig> updateServiceConfig(
            @PathVariable Long id, 
            @Valid @RequestBody GlobalServiceConfig serviceConfig) {
        log.debug("REST request to update global service configuration : {}", serviceConfig);
        if (serviceConfig.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service configuration ID must not be null");
        }
        if (!id.equals(serviceConfig.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            GlobalServiceConfig result = globalServiceConfigService.updateServiceConfig(id, serviceConfig);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/service-configs/{id} : Delete a service configuration
     * 
     * @param id the id of the service configuration to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceConfig(@PathVariable Long id) {
        log.debug("REST request to delete global service configuration : {}", id);
        try {
            globalServiceConfigService.deleteServiceConfig(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * POST /api/v1/service-configs/apply-to-region/{regionId} : Apply global configurations to a region
     * 
     * @param regionId the id of the region to apply configurations to
     * @param serviceKeys the list of service keys to apply
     * @return the ResponseEntity with status 200 (OK) and with body the list of newly created regional service configurations
     */
    @PostMapping("/apply-to-region/{regionId}")
    public ResponseEntity<List<GlobalServiceConfig>> applyGlobalConfigToRegion(
            @PathVariable Long regionId,
            @RequestBody List<String> serviceKeys) {
        log.debug("REST request to apply global configurations to region {}: {}", regionId, serviceKeys);
        
        try {
            List<GlobalServiceConfig> result = globalServiceConfigService.applyGlobalConfigToRegion(regionId, serviceKeys);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
