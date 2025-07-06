package com.gogidix.courier.courier.hqadmin.service;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.RegionalAdminSystem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface defining operations for managing regional admin systems.
 */
public interface RegionalAdminSystemService {
    
    /**
     * Get all regional admin systems
     * 
     * @return List of all regional admin systems
     */
    List<RegionalAdminSystem> getAllRegionalAdminSystems();
    
    /**
     * Get a regional admin system by ID
     * 
     * @param id The system ID
     * @return The regional admin system if found
     */
    Optional<RegionalAdminSystem> getRegionalAdminSystemById(Long id);
    
    /**
     * Get a regional admin system by its unique code
     * 
     * @param systemCode The system code
     * @return The regional admin system if found
     */
    Optional<RegionalAdminSystem> getRegionalAdminSystemByCode(String systemCode);
    
    /**
     * Create a new regional admin system
     * 
     * @param system The regional admin system to create
     * @return The created regional admin system
     */
    RegionalAdminSystem createRegionalAdminSystem(RegionalAdminSystem system);
    
    /**
     * Update an existing regional admin system
     * 
     * @param id The system ID to update
     * @param systemDetails The updated system details
     * @return The updated regional admin system
     * @throws RuntimeException if system not found
     */
    RegionalAdminSystem updateRegionalAdminSystem(Long id, RegionalAdminSystem systemDetails);
    
    /**
     * Delete a regional admin system
     * 
     * @param id The system ID to delete
     * @throws RuntimeException if system not found
     */
    void deleteRegionalAdminSystem(Long id);
    
    /**
     * Get all active regional admin systems
     * 
     * @return List of active regional admin systems
     */
    List<RegionalAdminSystem> getAllActiveRegionalAdminSystems();
    
    /**
     * Get all regional admin systems for a specific global region
     * 
     * @param regionId The global region ID
     * @return List of regional admin systems in the region
     * @throws RuntimeException if region not found
     */
    List<RegionalAdminSystem> getRegionalAdminSystemsByRegion(Long regionId);
    
    /**
     * Search regional admin systems by name
     * 
     * @param searchText The search text
     * @return List of regional admin systems matching the search
     */
    List<RegionalAdminSystem> searchRegionalAdminSystemsByName(String searchText);
    
    /**
     * Update the health check information for a regional admin system
     * 
     * @param id The system ID
     * @param healthStatus The health status
     * @return The updated regional admin system
     * @throws RuntimeException if system not found
     */
    RegionalAdminSystem updateHealthCheck(Long id, String healthStatus);
    
    /**
     * Find all regional admin systems with health checks older than a specific time
     * 
     * @param checkTime The reference time
     * @return List of regional admin systems with stale health checks
     */
    List<RegionalAdminSystem> findSystemsWithStaleHealthChecks(LocalDateTime checkTime);
    
    /**
     * Find regional admin systems by health status
     * 
     * @param status The health status to search for
     * @return List of regional admin systems with the specified health status
     */
    List<RegionalAdminSystem> findSystemsByHealthStatus(String status);
    
    /**
     * Count active regional admin systems per global region
     * 
     * @return Map of region name to system count
     */
    Map<String, Long> countActiveSystemsByRegion();
    
    /**
     * Perform health checks on all active regional admin systems
     * 
     * @return List of updated regional admin systems
     */
    List<RegionalAdminSystem> performHealthChecksForAllActiveSystems();
    
    /**
     * Update API credentials for a regional admin system
     * 
     * @param id The system ID
     * @param apiKey The new API key
     * @param authToken The new auth token
     * @return The updated regional admin system
     * @throws RuntimeException if system not found
     */
    RegionalAdminSystem updateApiCredentials(Long id, String apiKey, String authToken);
    
    /**
     * Update contact information for a regional admin system
     * 
     * @param id The system ID
     * @param email The contact email
     * @param phone The contact phone
     * @param supportUrl The support URL
     * @return The updated regional admin system
     * @throws RuntimeException if system not found
     */
    RegionalAdminSystem updateContactInfo(Long id, String email, String phone, String supportUrl);
    
    /**
     * Transfer a regional admin system to a different global region
     * 
     * @param systemId The system ID
     * @param newRegionId The new region ID
     * @return The updated regional admin system
     * @throws RuntimeException if system or region not found
     */
    RegionalAdminSystem transferToRegion(Long systemId, Long newRegionId);
}
