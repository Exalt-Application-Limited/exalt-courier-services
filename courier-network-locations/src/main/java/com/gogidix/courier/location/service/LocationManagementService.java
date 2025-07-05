package com.gogidix.courier.location.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.socialecommerceecosystem.location.model.LocationOperatingHours;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;

/**
 * Service interface for managing physical courier locations.
 * Provides high-level business functions for location management.
 */
public interface LocationManagementService {
    
    /**
     * Get all physical locations.
     * 
     * @return list of all locations
     */
    List<PhysicalLocation> getAllLocations();
    
    /**
     * Get all physical locations with pagination.
     * 
     * @param pageable pagination parameters
     * @return page of locations
     */
    Page<PhysicalLocation> getAllLocations(Pageable pageable);
    
    /**
     * Get a location by ID.
     * 
     * @param locationId the ID of the location
     * @return optional containing the location if found
     */
    Optional<PhysicalLocation> getLocationById(Long locationId);
    
    /**
     * Get a location by name.
     * 
     * @param locationName the name of the location
     * @return optional containing the location if found
     */
    Optional<PhysicalLocation> getLocationByName(String locationName);
    
    /**
     * Create a new location.
     * 
     * @param location the location to create
     * @return the created location
     */
    PhysicalLocation createLocation(PhysicalLocation location);
    
    /**
     * Update an existing location.
     * 
     * @param locationId the ID of the location to update
     * @param location the updated location details
     * @return the updated location
     */
    PhysicalLocation updateLocation(Long locationId, PhysicalLocation location);
    
    /**
     * Deactivate a location.
     * 
     * @param locationId the ID of the location to deactivate
     * @return the deactivated location
     */
    PhysicalLocation deactivateLocation(Long locationId);
    
    /**
     * Activate a location.
     * 
     * @param locationId the ID of the location to activate
     * @return the activated location
     */
    PhysicalLocation activateLocation(Long locationId);
    
    /**
     * Delete a location.
     * 
     * @param locationId the ID of the location to delete
     */
    void deleteLocation(Long locationId);
    
    /**
     * Get all active locations.
     * 
     * @return list of active locations
     */
    List<PhysicalLocation> getActiveLocations();
    
    /**
     * Get locations by type.
     * 
     * @param locationType the type of locations to find
     * @return list of locations of the specified type
     */
    List<PhysicalLocation> getLocationsByType(LocationType locationType);
    
    /**
     * Get locations by region.
     * 
     * @param country the country to search in
     * @param state the state or province to search in
     * @return list of locations in the specified region
     */
    List<PhysicalLocation> getLocationsByRegion(String country, String state);
    
    /**
     * Get locations by city.
     * 
     * @param city the city to search in
     * @return list of locations in the specified city
     */
    List<PhysicalLocation> getLocationsByCity(String city);
    
    /**
     * Get locations by postal/zip code.
     * 
     * @param zipCode the postal/zip code to search
     * @return list of locations in the specified postal/zip code area
     */
    List<PhysicalLocation> getLocationsByZipCode(String zipCode);
    
    /**
     * Get locations managed by a specific regional admin.
     * 
     * @param regionalAdminId the ID of the regional admin
     * @return list of locations managed by the specified admin
     */
    List<PhysicalLocation> getLocationsByRegionalAdmin(Long regionalAdminId);
    
    /**
     * Search locations by name.
     * 
     * @param nameQuery the name query to search for
     * @return list of locations with names containing the query
     */
    List<PhysicalLocation> searchLocationsByName(String nameQuery);
    
    /**
     * Find locations near coordinates within a radius.
     * 
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @param radiusInKm the search radius in kilometers
     * @return list of locations within the specified radius
     */
    List<PhysicalLocation> findLocationsNearCoordinates(Double latitude, Double longitude, Double radiusInKm);
    
    /**
     * Find locations with available capacity.
     * 
     * @return list of locations with available capacity
     */
    List<PhysicalLocation> findLocationsWithAvailableCapacity();
    
    /**
     * Find locations with high capacity utilization.
     * 
     * @param utilizationThreshold the threshold percentage (0-100)
     * @return list of locations with high capacity utilization
     */
    List<PhysicalLocation> findLocationsWithHighCapacityUtilization(Double utilizationThreshold);
    
    /**
     * Update the capacity of a location.
     * 
     * @param locationId the ID of the location
     * @param maxCapacity the new maximum capacity
     * @param currentCapacityUsage the new current capacity usage
     * @return the updated location
     */
    PhysicalLocation updateLocationCapacity(Long locationId, Integer maxCapacity, Integer currentCapacityUsage);
    
    /**
     * Adjust the capacity usage of a location.
     * 
     * @param locationId the ID of the location
     * @param delta the change in capacity usage (positive for increase, negative for decrease)
     * @return the updated location or null if adjustment failed
     */
    PhysicalLocation adjustCapacityUsage(Long locationId, Integer delta);
    
    /**
     * Get the operating hours for a location.
     * 
     * @param locationId the ID of the location
     * @return list of operating hours for the specified location
     */
    List<LocationOperatingHours> getLocationOperatingHours(Long locationId);
    
    /**
     * Get the operating hours for a location on a specific day.
     * 
     * @param locationId the ID of the location
     * @param dayOfWeek the day of week
     * @return list of operating hours for the specified location and day
     */
    List<LocationOperatingHours> getLocationOperatingHours(Long locationId, DayOfWeek dayOfWeek);
    
    /**
     * Set operating hours for a location.
     * 
     * @param locationId the ID of the location
     * @param operatingHours list of operating hours to set
     * @return list of saved operating hours
     */
    List<LocationOperatingHours> setLocationOperatingHours(Long locationId, List<LocationOperatingHours> operatingHours);
    
    /**
     * Check if a location is open at a specific time on a specific day.
     * 
     * @param locationId the ID of the location
     * @param dayOfWeek the day of week
     * @param time the time to check
     * @return true if the location is open, false otherwise
     */
    boolean isLocationOpenAt(Long locationId, DayOfWeek dayOfWeek, LocalTime time);
    
    /**
     * Find locations open at a specific time on a specific day.
     * 
     * @param dayOfWeek the day of week
     * @param time the time to check
     * @return list of locations open at the specified time
     */
    List<PhysicalLocation> findLocationsOpenAt(DayOfWeek dayOfWeek, LocalTime time);
    
    /**
     * Get capacity statistics for all locations.
     * 
     * @return map of capacity statistics by location type
     */
    Map<LocationType, Map<String, Object>> getCapacityStatisticsByLocationType();
    
    /**
     * Count locations by type.
     * 
     * @return map of location counts by type
     */
    Map<LocationType, Long> countLocationsByType();
    
    /**
     * Check if a location exists by name.
     * 
     * @param locationName the name to check
     * @return true if a location with the specified name exists, false otherwise
     */
    boolean existsByName(String locationName);
    
    /**
     * Count locations by country.
     * 
     * @return map of location counts by country
     */
    Map<String, Long> countLocationsByCountry();
}
