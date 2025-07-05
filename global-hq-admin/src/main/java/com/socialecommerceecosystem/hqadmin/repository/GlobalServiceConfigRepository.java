package com.gogidix.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.GlobalServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link GlobalServiceConfig} entity that provides
 * data access operations for global service configurations.
 */
@Repository
public interface GlobalServiceConfigRepository extends JpaRepository<GlobalServiceConfig, Long> {
    
    /**
     * Find a global service configuration by its service key
     * 
     * @param serviceKey The service key
     * @return The service configuration if found
     */
    Optional<GlobalServiceConfig> findByServiceKey(String serviceKey);
    
    /**
     * Find all active global service configurations
     * 
     * @return List of active service configurations
     */
    List<GlobalServiceConfig> findByIsActiveTrue();
    
    /**
     * Find all service configurations in a specific category
     * 
     * @param category The service category
     * @return List of service configurations in the category
     */
    List<GlobalServiceConfig> findByServiceCategory(String category);
    
    /**
     * Find all global service configurations for a specific region
     * 
     * @param globalRegion The global region
     * @return List of service configurations for the region
     */
    List<GlobalServiceConfig> findByGlobalRegion(GlobalRegion globalRegion);
    
    /**
     * Find all service configurations that allow regional overrides
     * 
     * @return List of service configurations that allow regional overrides
     */
    List<GlobalServiceConfig> findByAllowRegionalOverrideTrue();
    
    /**
     * Find service configurations for a specific region and category
     * 
     * @param region The global region
     * @param category The service category
     * @return List of matching service configurations
     */
    List<GlobalServiceConfig> findByGlobalRegionAndServiceCategory(GlobalRegion region, String category);
    
    /**
     * Find service configurations by name containing the search text
     * 
     * @param searchText The search text
     * @return List of matching service configurations
     */
    List<GlobalServiceConfig> findByNameContainingIgnoreCase(String searchText);
    
    /**
     * Find global default configurations (configurations without region assignment)
     * 
     * @return List of global default configurations
     */
    List<GlobalServiceConfig> findByGlobalRegionIsNull();
    
    /**
     * Count configurations by category
     * 
     * @return List of count results by category
     */
    @Query("SELECT g.serviceCategory as category, COUNT(g) as configCount FROM GlobalServiceConfig g " +
           "GROUP BY g.serviceCategory")
    List<Object[]> countConfigurationsByCategory();
    
    /**
     * Find service configurations that must be applied globally
     * 
     * @return List of service configurations that must be applied globally
     */
    @Query("SELECT g FROM GlobalServiceConfig g WHERE g.allowRegionalOverride = false AND g.isActive = true")
    List<GlobalServiceConfig> findMandatoryGlobalConfigurations();
    
    /**
     * Find configurations by type
     * 
     * @param configType The configuration type
     * @return List of configurations of the specified type
     */
    List<GlobalServiceConfig> findByConfigType(String configType);
}
