package com.gogidix.courier.courier.hqadmin.service;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;

import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for managing global regions.
 */
public interface GlobalRegionService {
    
    /**
     * Get all global regions
     * 
     * @return List of all global regions
     */
    List<GlobalRegion> getAllRegions();
    
    /**
     * Get a global region by ID
     * 
     * @param id The region ID
     * @return The global region if found
     */
    Optional<GlobalRegion> getRegionById(Long id);
    
    /**
     * Get a global region by its unique code
     * 
     * @param regionCode The region code
     * @return The global region if found
     */
    Optional<GlobalRegion> getRegionByCode(String regionCode);
    
    /**
     * Create a new global region
     * 
     * @param region The global region to create
     * @return The created global region
     */
    GlobalRegion createRegion(GlobalRegion region);
    
    /**
     * Update an existing global region
     * 
     * @param id The region ID to update
     * @param regionDetails The updated region details
     * @return The updated global region
     * @throws RuntimeException if region not found
     */
    GlobalRegion updateRegion(Long id, GlobalRegion regionDetails);
    
    /**
     * Delete a global region
     * 
     * @param id The region ID to delete
     * @throws RuntimeException if region not found or has dependencies
     */
    void deleteRegion(Long id);
    
    /**
     * Get all active global regions
     * 
     * @return List of active global regions
     */
    List<GlobalRegion> getAllActiveRegions();
    
    /**
     * Get all top-level regions (regions without parent)
     * 
     * @return List of top-level regions
     */
    List<GlobalRegion> getTopLevelRegions();
    
    /**
     * Get all child regions of a specific parent region
     * 
     * @param parentRegionId The parent region ID
     * @return List of child regions
     * @throws RuntimeException if parent region not found
     */
    List<GlobalRegion> getChildRegions(Long parentRegionId);
    
    /**
     * Search regions by name
     * 
     * @param searchText The search text
     * @return List of regions matching the search
     */
    List<GlobalRegion> searchRegionsByName(String searchText);
    
    /**
     * Add a child region to a parent region
     * 
     * @param parentId The parent region ID
     * @param childId The child region ID
     * @return The updated parent region
     * @throws RuntimeException if either region not found
     */
    GlobalRegion addChildToParentRegion(Long parentId, Long childId);
    
    /**
     * Remove a child region from a parent region
     * 
     * @param parentId The parent region ID
     * @param childId The child region ID
     * @return The updated parent region
     * @throws RuntimeException if either region not found
     */
    GlobalRegion removeChildFromParentRegion(Long parentId, Long childId);
    
    /**
     * Find regions by currency code
     * 
     * @param currencyCode The currency code
     * @return List of regions using the specified currency
     */
    List<GlobalRegion> findRegionsByCurrencyCode(String currencyCode);
    
    /**
     * Find regions within a geographic bounding box
     * 
     * @param minLat Minimum latitude
     * @param maxLat Maximum latitude
     * @param minLong Minimum longitude
     * @param maxLong Maximum longitude
     * @return List of regions within the specified bounding box
     */
    List<GlobalRegion> findRegionsInBoundingBox(
            Double minLat, Double maxLat, Double minLong, Double maxLong);
    
    /**
     * Count the number of child regions for a parent region
     * 
     * @param parentId The parent region ID
     * @return Count of child regions
     * @throws RuntimeException if parent region not found
     */
    Long countChildRegions(Long parentId);
}
