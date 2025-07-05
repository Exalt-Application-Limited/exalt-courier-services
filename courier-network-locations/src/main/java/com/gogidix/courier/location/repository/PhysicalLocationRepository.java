package com.gogidix.courier.location.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;

/**
 * Repository interface for PhysicalLocation entity.
 * Provides methods for accessing and querying physical location data.
 */
@Repository
public interface PhysicalLocationRepository extends JpaRepository<PhysicalLocation, Long> {
    
    /**
     * Find all active locations.
     * 
     * @return list of active locations
     */
    List<PhysicalLocation> findByActiveTrue();
    
    /**
     * Find all active locations with pagination.
     * 
     * @param pageable pagination parameters
     * @return page of active locations
     */
    Page<PhysicalLocation> findByActiveTrue(Pageable pageable);
    
    /**
     * Find locations by location type.
     * 
     * @param locationType the type of location to find
     * @return list of locations of the specified type
     */
    List<PhysicalLocation> findByLocationType(LocationType locationType);
    
    /**
     * Find locations by location type and active status with pagination.
     * 
     * @param locationType the type of location to find
     * @param active the active status to match
     * @param pageable pagination parameters
     * @return page of matching locations
     */
    Page<PhysicalLocation> findByLocationTypeAndActive(LocationType locationType, boolean active, Pageable pageable);
    
    /**
     * Find locations by region (country and state).
     * 
     * @param country the country to search in
     * @param state the state or province to search in
     * @return list of locations in the specified region
     */
    List<PhysicalLocation> findByCountryAndState(String country, String state);
    
    /**
     * Find locations by city.
     * 
     * @param city the city to search in
     * @return list of locations in the specified city
     */
    List<PhysicalLocation> findByCity(String city);
    
    /**
     * Find locations by postal/zip code.
     * 
     * @param zipCode the postal/zip code to search
     * @return list of locations matching the postal/zip code
     */
    List<PhysicalLocation> findByZipCode(String zipCode);
    
    /**
     * Find locations managed by a specific regional admin.
     * 
     * @param regionalAdminId the ID of the regional admin
     * @return list of locations managed by the specified admin
     */
    List<PhysicalLocation> findByRegionalAdminId(Long regionalAdminId);
    
    /**
     * Find location by name.
     * 
     * @param name the name to search for (exact match)
     * @return optional location with the specified name
     */
    Optional<PhysicalLocation> findByName(String name);
    
    /**
     * Search locations by name containing the given text.
     * 
     * @param nameContains the text to search for within names
     * @return list of locations with names containing the specified text
     */
    List<PhysicalLocation> findByNameContainingIgnoreCase(String nameContains);
    
    /**
     * Find locations with available capacity (less than max capacity).
     * 
     * @return list of locations with available capacity
     */
    @Query("SELECT p FROM PhysicalLocation p WHERE p.active = true AND (p.maxCapacity IS NULL OR p.currentCapacityUsage < p.maxCapacity)")
    List<PhysicalLocation> findLocationsWithAvailableCapacity();
    
    /**
     * Find locations close to coordinates within a radius.
     * Uses the Haversine formula to calculate distances.
     * 
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @param radiusInKm the search radius in kilometers
     * @return list of locations within the specified radius
     */
    @Query(value = "SELECT * FROM physical_locations p " +
            "WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(p.latitude)) * " +
            "cos(radians(p.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(p.latitude)))) < :radiusInKm " +
            "AND p.active = true", nativeQuery = true)
    List<PhysicalLocation> findLocationsNearCoordinates(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusInKm") Double radiusInKm);
    
    /**
     * Find locations with high capacity utilization (above specified threshold).
     * 
     * @param utilizationThreshold the threshold percentage (0-100)
     * @return list of locations with high capacity utilization
     */
    @Query("SELECT p FROM PhysicalLocation p WHERE p.active = true AND p.maxCapacity IS NOT NULL " +
            "AND (p.currentCapacityUsage * 100.0 / p.maxCapacity) >= :utilizationThreshold")
    List<PhysicalLocation> findLocationsWithHighCapacityUtilization(@Param("utilizationThreshold") Double utilizationThreshold);
    
    /**
     * Count the number of active locations by type.
     * 
     * @param locationType the type of location to count
     * @return the count of active locations of the specified type
     */
    long countByLocationTypeAndActiveTrue(LocationType locationType);
    
    /**
     * Count the number of active locations in a country.
     * 
     * @param country the country to count locations in
     * @return the count of active locations in the specified country
     */
    long countByCountryAndActiveTrue(String country);
}
