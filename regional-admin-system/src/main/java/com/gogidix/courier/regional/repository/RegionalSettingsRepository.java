package com.gogidix.courier.regional.repository;

import com.socialecommerceecosystem.regional.model.RegionalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing RegionalSettings entities.
 */
@Repository
public interface RegionalSettingsRepository extends JpaRepository<RegionalSettings, Long> {

    /**
     * Find a regional settings by its unique region code.
     * 
     * @param regionCode The unique code assigned to the region
     * @return An Optional containing the RegionalSettings if found, or empty otherwise
     */
    Optional<RegionalSettings> findByRegionCode(String regionCode);

    /**
     * Find all active regional settings.
     * 
     * @return List of active regional settings
     */
    List<RegionalSettings> findByIsActiveTrue();

    /**
     * Find regional settings by global region ID.
     * 
     * @param globalRegionId The ID of the global region
     * @return List of regional settings associated with the global region
     */
    List<RegionalSettings> findByGlobalRegionId(Long globalRegionId);

    /**
     * Find regional settings by name containing the given text (case-insensitive).
     * 
     * @param regionName Text to search for in region names
     * @return List of matching regional settings
     */
    List<RegionalSettings> findByRegionNameContainingIgnoreCase(String regionName);

    /**
     * Find regional settings by timezone.
     * 
     * @param timezone The timezone to filter by
     * @return List of regional settings in the specified timezone
     */
    List<RegionalSettings> findByTimezone(String timezone);

    /**
     * Find regional settings by currency code.
     * 
     * @param currencyCode The currency code to filter by
     * @return List of regional settings using the specified currency
     */
    List<RegionalSettings> findByCurrencyCode(String currencyCode);

    /**
     * Find all regional settings that are configured to sync with global settings.
     * 
     * @return List of regional settings that sync with global settings
     */
    List<RegionalSettings> findByGlobalSettingsSyncTrue();

    /**
     * Count the number of regional settings by global region ID.
     * 
     * @param globalRegionId The ID of the global region
     * @return The count of regional settings for the global region
     */
    long countByGlobalRegionId(Long globalRegionId);

    /**
     * Find regional settings with a specific locale.
     * 
     * @param locale The locale to filter by
     * @return List of regional settings with the specified locale
     */
    List<RegionalSettings> findByLocale(String locale);

    /**
     * Check if a regional settings with the given region code exists.
     * 
     * @param regionCode The region code to check
     * @return true if a regional settings with the code exists, false otherwise
     */
    boolean existsByRegionCode(String regionCode);

    /**
     * Find regional settings by measurement system.
     * 
     * @param measurementSystem The measurement system to filter by (e.g., "metric", "imperial")
     * @return List of regional settings using the specified measurement system
     */
    List<RegionalSettings> findByMeasurementSystem(String measurementSystem);

    /**
     * Find regional settings by regional manager name (partial match, case-insensitive).
     * 
     * @param managerName The manager name to search for
     * @return List of regional settings managed by the specified manager
     */
    @Query("SELECT rs FROM RegionalSettings rs WHERE LOWER(rs.regionalManager) LIKE LOWER(CONCAT('%', :managerName, '%'))")
    List<RegionalSettings> findByRegionalManagerContainingIgnoreCase(@Param("managerName") String managerName);

    /**
     * Find regional settings that need emergency contact information update.
     * 
     * @return List of regional settings with missing or empty emergency contact information
     */
    @Query("SELECT rs FROM RegionalSettings rs WHERE rs.emergencyContact IS NULL OR rs.emergencyContact = ''")
    List<RegionalSettings> findWithMissingEmergencyContact();
}
