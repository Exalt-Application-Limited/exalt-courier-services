package com.gogidix.courier.regional.repository;

import com.socialecommerceecosystem.regional.model.CourierLocation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing CourierLocation entities.
 */
@Repository
public interface CourierLocationRepository extends JpaRepository<CourierLocation, Long> {

    /**
     * Find a courier location by its unique location code.
     * 
     * @param locationCode The unique code assigned to the location
     * @return An Optional containing the CourierLocation if found, or empty otherwise
     */
    Optional<CourierLocation> findByLocationCode(String locationCode);

    /**
     * Find all active courier locations.
     * 
     * @return List of active courier locations
     */
    List<CourierLocation> findByIsActiveTrue();

    /**
     * Find courier locations by regional settings ID.
     * 
     * @param regionalSettingsId The ID of the regional settings
     * @return List of courier locations associated with the regional settings
     */
    List<CourierLocation> findByRegionalSettingsId(Long regionalSettingsId);

    /**
     * Find courier locations by location type.
     * 
     * @param locationType The type of location to filter by
     * @return List of courier locations of the specified type
     */
    List<CourierLocation> findByLocationType(String locationType);

    /**
     * Find courier locations by city.
     * 
     * @param city The city to filter by
     * @return List of courier locations in the specified city
     */
    List<CourierLocation> findByCity(String city);

    /**
     * Find courier locations by state or province.
     * 
     * @param stateProvince The state or province to filter by
     * @return List of courier locations in the specified state or province
     */
    List<CourierLocation> findByStateProvince(String stateProvince);

    /**
     * Find courier locations by country.
     * 
     * @param country The country to filter by
     * @return List of courier locations in the specified country
     */
    List<CourierLocation> findByCountry(String country);

    /**
     * Find courier locations by manager ID.
     * 
     * @param managerId The ID of the manager
     * @return List of courier locations managed by the specified manager
     */
    List<CourierLocation> findByManagerId(Long managerId);

    /**
     * Find courier locations that are hubs.
     * 
     * @return List of courier locations that are hubs
     */
    List<CourierLocation> findByIsHubTrue();

    /**
     * Find courier locations by parent location ID.
     * 
     * @param parentLocationId The ID of the parent location
     * @return List of courier locations with the specified parent location
     */
    List<CourierLocation> findByParentLocationId(Long parentLocationId);

    /**
     * Find courier locations by name containing the given text (case-insensitive).
     * 
     * @param locationName Text to search for in location names
     * @return List of matching courier locations
     */
    List<CourierLocation> findByLocationNameContainingIgnoreCase(String locationName);

    /**
     * Find courier locations by postal code.
     * 
     * @param postalCode The postal code to filter by
     * @return List of courier locations with the specified postal code
     */
    List<CourierLocation> findByPostalCode(String postalCode);

    /**
     * Find courier locations with a location rating greater than or equal to the specified value.
     * 
     * @param rating The minimum rating value
     * @return List of courier locations with ratings at or above the specified value
     */
    List<CourierLocation> findByLocationRatingGreaterThanEqual(BigDecimal rating);

    /**
     * Find courier locations that have refrigeration capabilities.
     * 
     * @return List of courier locations with refrigeration capabilities
     */
    List<CourierLocation> findByHasRefrigerationTrue();

    /**
     * Find courier locations that have security systems.
     * 
     * @return List of courier locations with security systems
     */
    List<CourierLocation> findByHasSecurityTrue();

    /**
     * Find courier locations with a daily package capacity greater than or equal to the specified value.
     * 
     * @param capacity The minimum daily package capacity
     * @return List of courier locations with capacities at or above the specified value
     */
    List<CourierLocation> findByMaxDailyPackagesGreaterThanEqual(Integer capacity);

    /**
     * Find courier locations with a storage capacity greater than or equal to the specified value.
     * 
     * @param capacity The minimum storage capacity
     * @return List of courier locations with storage capacities at or above the specified value
     */
    List<CourierLocation> findByStorageCapacityGreaterThanEqual(Integer capacity);

    /**
     * Find courier locations by property size range.
     * 
     * @param minSize The minimum property size in square feet
     * @param maxSize The maximum property size in square feet
     * @return List of courier locations within the specified property size range
     */
    @Query("SELECT cl FROM CourierLocation cl WHERE cl.propertySizeSqft >= :minSize AND cl.propertySizeSqft <= :maxSize")
    List<CourierLocation> findByPropertySizeRange(@Param("minSize") Integer minSize, @Param("maxSize") Integer maxSize);

    /**
     * Find courier locations with specific services offered.
     * 
     * @param service The service to search for
     * @return List of courier locations that offer the specified service
     */
    @Query("SELECT cl FROM CourierLocation cl WHERE cl.servicesOffered LIKE %:service%")
    List<CourierLocation> findByServiceOffered(@Param("service") String service);

    /**
     * Find courier locations within a certain radius of specified coordinates.
     * 
     * @param latitude The latitude coordinate
     * @param longitude The longitude coordinate
     * @param radiusKm The radius in kilometers
     * @return List of courier locations within the specified radius
     */
    @Query(value = "SELECT * FROM courier_location cl WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(cl.latitude)) * " +
            "cos(radians(cl.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(cl.latitude)))) <= :radiusKm", 
            nativeQuery = true)
    List<CourierLocation> findLocationsWithinRadius(
            @Param("latitude") BigDecimal latitude, 
            @Param("longitude") BigDecimal longitude, 
            @Param("radiusKm") Double radiusKm);

    /**
     * Check if a courier location with the given location code exists.
     * 
     * @param locationCode The location code to check
     * @return true if a courier location with the code exists, false otherwise
     */
    boolean existsByLocationCode(String locationCode);

    /**
     * Find pageable courier locations by regional settings ID.
     * 
     * @param regionalSettingsId The ID of the regional settings
     * @param pageable Pagination information
     * @return Page of courier locations associated with the regional settings
     */
    Page<CourierLocation> findByRegionalSettingsId(Long regionalSettingsId, Pageable pageable);

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
}
