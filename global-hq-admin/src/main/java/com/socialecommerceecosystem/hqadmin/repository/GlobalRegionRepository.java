package com.gogidix.courier.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link GlobalRegion} entity that provides
 * data access operations for global regions.
 */
@Repository
public interface GlobalRegionRepository extends JpaRepository<GlobalRegion, Long> {
    
    /**
     * Find a global region by its unique code
     * 
     * @param regionCode The region code
     * @return The region if found
     */
    Optional<GlobalRegion> findByRegionCode(String regionCode);
    
    /**
     * Find all active global regions
     * 
     * @return List of active regions
     */
    List<GlobalRegion> findByIsActiveTrue();
    
    /**
     * Find all top-level regions (regions without parent)
     * 
     * @return List of top-level regions
     */
    List<GlobalRegion> findByParentRegionIsNull();
    
    /**
     * Find all child regions of a specific parent region
     * 
     * @param parentRegion The parent region
     * @return List of child regions
     */
    List<GlobalRegion> findByParentRegion(GlobalRegion parentRegion);
    
    /**
     * Find regions by name containing the search text
     * 
     * @param searchText The search text
     * @return List of matching regions
     */
    List<GlobalRegion> findByNameContainingIgnoreCase(String searchText);
    
    /**
     * Find regions by currency code
     * 
     * @param currencyCode The currency code
     * @return List of regions using the specified currency
     */
    List<GlobalRegion> findByCurrencyCode(String currencyCode);
    
    /**
     * Count the number of child regions for a parent region
     * 
     * @param parentId The parent region ID
     * @return Count of child regions
     */
    @Query("SELECT COUNT(r) FROM GlobalRegion r WHERE r.parentRegion.id = :parentId")
    Long countChildRegions(@Param("parentId") Long parentId);
    
    /**
     * Find regions within a geographic bounding box
     * 
     * @param minLat Minimum latitude
     * @param maxLat Maximum latitude
     * @param minLong Minimum longitude
     * @param maxLong Maximum longitude
     * @return List of regions within the specified bounding box
     */
    @Query("SELECT r FROM GlobalRegion r WHERE r.latitude >= :minLat AND r.latitude <= :maxLat " +
           "AND r.longitude >= :minLong AND r.longitude <= :maxLong")
    List<GlobalRegion> findRegionsInBoundingBox(
            @Param("minLat") Double minLat, 
            @Param("maxLat") Double maxLat, 
            @Param("minLong") Double minLong, 
            @Param("maxLong") Double maxLong);
}
