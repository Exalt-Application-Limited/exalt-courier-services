package com.gogidix.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.model.RegionalAdminSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link RegionalAdminSystem} entity that provides
 * data access operations for regional admin systems.
 */
@Repository
public interface RegionalAdminSystemRepository extends JpaRepository<RegionalAdminSystem, Long> {
    
    /**
     * Find a regional admin system by its unique code
     * 
     * @param systemCode The system code
     * @return The regional admin system if found
     */
    Optional<RegionalAdminSystem> findBySystemCode(String systemCode);
    
    /**
     * Find all active regional admin systems
     * 
     * @return List of active regional admin systems
     */
    List<RegionalAdminSystem> findByIsActiveTrue();
    
    /**
     * Find all regional admin systems belonging to a specific global region
     * 
     * @param globalRegion The global region
     * @return List of regional admin systems in the region
     */
    List<RegionalAdminSystem> findByGlobalRegion(GlobalRegion globalRegion);
    
    /**
     * Find regional admin systems by name containing the search text
     * 
     * @param searchText The search text
     * @return List of matching regional admin systems
     */
    List<RegionalAdminSystem> findByNameContainingIgnoreCase(String searchText);
    
    /**
     * Find all regional admin systems with health checks older than a specific time
     * 
     * @param checkTime The reference time
     * @return List of regional admin systems with stale health checks
     */
    List<RegionalAdminSystem> findByLastHealthCheckBefore(LocalDateTime checkTime);
    
    /**
     * Find regional admin systems by health status
     * 
     * @param status The health status to search for
     * @return List of regional admin systems with the specified health status
     */
    List<RegionalAdminSystem> findByHealthStatus(String status);
    
    /**
     * Count active regional admin systems per global region
     * 
     * @return List of count results by region
     */
    @Query("SELECT r.globalRegion.name as regionName, COUNT(r) as systemCount FROM RegionalAdminSystem r " +
           "WHERE r.isActive = true GROUP BY r.globalRegion.id, r.globalRegion.name")
    List<Object[]> countActiveSystemsByRegion();
    
    /**
     * Find regional admin systems that need health check updates
     * 
     * @param cutoffTime The cutoff time for health checks
     * @return List of systems that need health updates
     */
    @Query("SELECT r FROM RegionalAdminSystem r WHERE r.isActive = true AND " +
           "(r.lastHealthCheck IS NULL OR r.lastHealthCheck < :cutoffTime)")
    List<RegionalAdminSystem> findSystemsNeedingHealthUpdate(@Param("cutoffTime") LocalDateTime cutoffTime);
}
