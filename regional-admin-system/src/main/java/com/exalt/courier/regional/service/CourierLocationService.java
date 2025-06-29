package com.exalt.courier.regional.service;

import com.socialecommerceecosystem.regional.model.CourierLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing courier location operations.
 */
public interface CourierLocationService {

    /**
     * Get all courier locations.
     *
     * @return List of all courier locations
     */
    List<CourierLocation> getAllCourierLocations();

    /**
     * Get all courier locations with pagination.
     *
     * @param pageable Pagination information
     * @return Page of courier locations
     */
    Page<CourierLocation> getAllCourierLocations(Pageable pageable);

    /**
     * Get a courier location by ID.
     *
     * @param id The ID of the courier location
     * @return Optional containing the courier location if found, or empty otherwise
     */
    Optional<CourierLocation> getCourierLocationById(Long id);

    /**
     * Get a courier location by its location code.
     *
     * @param locationCode The unique code of the location
     * @return Optional containing the courier location if found, or empty otherwise
     */
    Optional<CourierLocation> getCourierLocationByCode(String locationCode);

    /**
     * Get all active courier locations.
     *
     * @return List of active courier locations
     */
    List<CourierLocation> getActiveCourierLocations();

    /**
     * Get courier locations by regional settings ID.
     *
     * @param regionalSettingsId The ID of the regional settings
     * @return List of courier locations associated with the regional settings
     */
    List<CourierLocation> getCourierLocationsByRegionalSettingsId(Long regionalSettingsId);

    /**
     * Get pageable courier locations by regional settings ID.
     *
     * @param regionalSettingsId The ID of the regional settings
     * @param pageable Pagination information
     * @return Page of courier locations associated with the regional settings
     */
    Page<CourierLocation> getCourierLocationsByRegionalSettingsId(Long regionalSettingsId, Pageable pageable);

    /**
     * Create a new courier location.
     *
     * @param courierLocation The courier location to create
     * @return The created courier location with its ID
     * @throws IllegalArgumentException if the location code already exists
     */
    CourierLocation createCourierLocation(CourierLocation courierLocation);

    /**
     * Update an existing courier location.
     *
     * @param id The ID of the courier location to update
     * @param courierLocation The updated courier location data
     * @return The updated courier location
     * @throws IllegalArgumentException if the courier location is not found
     */
    CourierLocation updateCourierLocation(Long id, CourierLocation courierLocation);

    /**
     * Delete a courier location by its ID.
     *
     * @param id The ID of the courier location to delete
     * @throws IllegalArgumentException if the courier location is not found
     */
    void deleteCourierLocation(Long id);

    /**
     * Get courier locations by location type.
     *
     * @param locationType The type of location to filter by
     * @return List of courier locations of the specified type
     */
    List<CourierLocation> getCourierLocationsByType(String locationType);

    /**
     * Get courier locations by city.
     *
     * @param city The city to filter by
     * @return List of courier locations in the specified city
     */
    List<CourierLocation> getCourierLocationsByCity(String city);

    /**
     * Get courier locations by state or province.
     *
     * @param stateProvince The state or province to filter by
     * @return List of courier locations in the specified state or province
     */
    List<CourierLocation> getCourierLocationsByStateProvince(String stateProvince);

    /**
     * Get courier locations by country.
     *
     * @param country The country to filter by
     * @return List of courier locations in the specified country
     */
    List<CourierLocation> getCourierLocationsByCountry(String country);

    /**
     * Get courier locations by manager ID.
     *
     * @param managerId The ID of the manager
     * @return List of courier locations managed by the specified manager
     */
    List<CourierLocation> getCourierLocationsByManager(Long managerId);

    /**
     * Get courier locations that are hubs.
     *
     * @return List of courier locations that are hubs
     */
    List<CourierLocation> getHubLocations();

    /**
     * Get courier locations by parent location ID.
     *
     * @param parentLocationId The ID of the parent location
     * @return List of courier locations with the specified parent location
     */
    List<CourierLocation> getCourierLocationsByParentLocation(Long parentLocationId);

    /**
     * Search for courier locations by name (case-insensitive, partial match).
     *
     * @param searchText The text to search for in location names
     * @return List of matching courier locations
     */
    List<CourierLocation> searchCourierLocationsByName(String searchText);

    /**
     * Get courier locations by postal code.
     *
     * @param postalCode The postal code to filter by
     * @return List of courier locations with the specified postal code
     */
    List<CourierLocation> getCourierLocationsByPostalCode(String postalCode);

    /**
     * Get courier locations with a location rating greater than or equal to the specified value.
     *
     * @param rating The minimum rating value
     * @return List of courier locations with ratings at or above the specified value
     */
    List<CourierLocation> getCourierLocationsWithMinimumRating(BigDecimal rating);

    /**
     * Get courier locations that have refrigeration capabilities.
     *
     * @return List of courier locations with refrigeration capabilities
     */
    List<CourierLocation> getCourierLocationsWithRefrigeration();

    /**
     * Get courier locations that have security systems.
     *
     * @return List of courier locations with security systems
     */
    List<CourierLocation> getCourierLocationsWithSecurity();

    /**
     * Get courier locations with a daily package capacity greater than or equal to the specified value.
     *
     * @param capacity The minimum daily package capacity
     * @return List of courier locations with capacities at or above the specified value
     */
    List<CourierLocation> getCourierLocationsWithMinimumDailyCapacity(Integer capacity);

    /**
     * Get courier locations with a storage capacity greater than or equal to the specified value.
     *
     * @param capacity The minimum storage capacity
     * @return List of courier locations with storage capacities at or above the specified value
     */
    List<CourierLocation> getCourierLocationsWithMinimumStorageCapacity(Integer capacity);

    /**
     * Get courier locations by property size range.
     *
     * @param minSize The minimum property size in square feet
     * @param maxSize The maximum property size in square feet
     * @return List of courier locations within the specified property size range
     */
    List<CourierLocation> getCourierLocationsByPropertySizeRange(Integer minSize, Integer maxSize);

    /**
     * Get courier locations with specific services offered.
     *
     * @param service The service to search for
     * @return List of courier locations that offer the specified service
     */
    List<CourierLocation> getCourierLocationsByServiceOffered(String service);

    /**
     * Get courier locations within a certain radius of specified coordinates.
     *
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param radiusKm The radius in kilometers
     * @return List of courier locations within the specified radius
     */
    List<CourierLocation> getCourierLocationsWithinRadius(BigDecimal latitude, BigDecimal longitude, Double radiusKm);

    /**
     * Check if a courier location with the given location code exists.
     *
     * @param locationCode The location code to check
     * @return true if a courier location with the code exists, false otherwise
     */
    boolean existsByLocationCode(String locationCode);

    /**
     * Count courier locations by location type.
     *
     * @param locationType The location type to count
     * @return The count of courier locations of the specified type
     */
    long countByLocationType(String locationType);

    /**
     * Count courier locations by regional settings ID.
     *
     * @param regionalSettingsId The ID of the regional settings
     * @return The count of courier locations associated with the regional settings
     */
    long countByRegionalSettingsId(Long regionalSettingsId);

    /**
     * Update specific attributes of a courier location.
     *
     * @param id The ID of the courier location to update
     * @param attributes Map of attributes to update (key-value pairs)
     * @return The updated courier location
     * @throws IllegalArgumentException if the courier location is not found
     */
    CourierLocation updateSpecificAttributes(Long id, Map<String, Object> attributes);

    /**
     * Activate a courier location.
     *
     * @param id The ID of the courier location to activate
     * @return The activated courier location
     * @throws IllegalArgumentException if the courier location is not found
     */
    CourierLocation activateCourierLocation(Long id);

    /**
     * Deactivate a courier location.
     *
     * @param id The ID of the courier location to deactivate
     * @return The deactivated courier location
     * @throws IllegalArgumentException if the courier location is not found
     */
    CourierLocation deactivateCourierLocation(Long id);

    /**
     * Reassign a courier location to a different manager.
     *
     * @param id The ID of the courier location to reassign
     * @param managerId The ID of the new manager
     * @return The updated courier location
     * @throws IllegalArgumentException if the courier location is not found
     */
    CourierLocation reassignCourierLocationManager(Long id, Long managerId);

    /**
     * Update the services offered by a courier location.
     *
     * @param id The ID of the courier location to update
     * @param services The updated services offered
     * @return The updated courier location
     * @throws IllegalArgumentException if the courier location is not found
     */
    CourierLocation updateCourierLocationServices(Long id, String services);
}
