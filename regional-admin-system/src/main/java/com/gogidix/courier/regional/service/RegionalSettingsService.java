package com.gogidix.courier.regional.service;

import com.socialecommerceecosystem.regional.model.RegionalSettings;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for managing regional settings operations.
 */
public interface RegionalSettingsService {

    /**
     * Get all regional settings.
     *
     * @return List of all regional settings
     */
    List<RegionalSettings> getAllRegionalSettings();

    /**
     * Get a regional settings by its ID.
     *
     * @param id The ID of the regional settings to retrieve
     * @return Optional containing the regional settings if found, or empty otherwise
     */
    Optional<RegionalSettings> getRegionalSettingsById(Long id);

    /**
     * Get a regional settings by its region code.
     *
     * @param regionCode The unique code of the region
     * @return Optional containing the regional settings if found, or empty otherwise
     */
    Optional<RegionalSettings> getRegionalSettingsByCode(String regionCode);

    /**
     * Get all active regional settings.
     *
     * @return List of active regional settings
     */
    List<RegionalSettings> getActiveRegionalSettings();

    /**
     * Get regional settings by global region ID.
     *
     * @param globalRegionId The ID of the global region
     * @return List of regional settings associated with the global region
     */
    List<RegionalSettings> getRegionalSettingsByGlobalRegionId(Long globalRegionId);

    /**
     * Create a new regional settings.
     *
     * @param regionalSettings The regional settings to create
     * @return The created regional settings with its ID
     * @throws IllegalArgumentException if the region code already exists
     */
    RegionalSettings createRegionalSettings(RegionalSettings regionalSettings);

    /**
     * Update an existing regional settings.
     *
     * @param id The ID of the regional settings to update
     * @param regionalSettings The updated regional settings data
     * @return The updated regional settings
     * @throws IllegalArgumentException if the regional settings is not found
     */
    RegionalSettings updateRegionalSettings(Long id, RegionalSettings regionalSettings);

    /**
     * Delete a regional settings by its ID.
     *
     * @param id The ID of the regional settings to delete
     * @throws IllegalArgumentException if the regional settings is not found
     */
    void deleteRegionalSettings(Long id);

    /**
     * Search for regional settings by name (case-insensitive, partial match).
     *
     * @param searchText The text to search for in region names
     * @return List of matching regional settings
     */
    List<RegionalSettings> searchRegionalSettingsByName(String searchText);

    /**
     * Get regional settings by timezone.
     *
     * @param timezone The timezone to filter by
     * @return List of regional settings in the specified timezone
     */
    List<RegionalSettings> getRegionalSettingsByTimezone(String timezone);

    /**
     * Get regional settings by currency code.
     *
     * @param currencyCode The currency code to filter by
     * @return List of regional settings using the specified currency
     */
    List<RegionalSettings> getRegionalSettingsByCurrencyCode(String currencyCode);

    /**
     * Get regional settings by locale.
     *
     * @param locale The locale to filter by
     * @return List of regional settings with the specified locale
     */
    List<RegionalSettings> getRegionalSettingsByLocale(String locale);

    /**
     * Get regional settings by measurement system.
     *
     * @param measurementSystem The measurement system to filter by
     * @return List of regional settings using the specified measurement system
     */
    List<RegionalSettings> getRegionalSettingsByMeasurementSystem(String measurementSystem);

    /**
     * Get regional settings by regional manager name (partial match).
     *
     * @param managerName The manager name to search for
     * @return List of regional settings managed by the specified manager
     */
    List<RegionalSettings> getRegionalSettingsByManager(String managerName);

    /**
     * Get regional settings that sync with global settings.
     *
     * @return List of regional settings configured to sync with global settings
     */
    List<RegionalSettings> getRegionalSettingsWithGlobalSync();

    /**
     * Get regional settings that need emergency contact information update.
     *
     * @return List of regional settings with missing emergency contact information
     */
    List<RegionalSettings> getRegionalSettingsWithMissingEmergencyContact();

    /**
     * Synchronize regional settings with global settings.
     *
     * @param regionalSettingsId The ID of the regional settings to synchronize
     * @return The updated regional settings after synchronization
     * @throws IllegalArgumentException if the regional settings is not found
     */
    RegionalSettings synchronizeWithGlobalSettings(Long regionalSettingsId);

    /**
     * Check if a regional settings with the given region code exists.
     *
     * @param regionCode The region code to check
     * @return true if a regional settings with the code exists, false otherwise
     */
    boolean existsByRegionCode(String regionCode);

    /**
     * Count regional settings by global region ID.
     *
     * @param globalRegionId The ID of the global region
     * @return The count of regional settings for the global region
     */
    long countByGlobalRegionId(Long globalRegionId);

    /**
     * Update specific settings in a regional settings entity.
     *
     * @param id The ID of the regional settings to update
     * @param settings Map of settings to update (key-value pairs)
     * @return The updated regional settings
     * @throws IllegalArgumentException if the regional settings is not found
     */
    RegionalSettings updateSpecificSettings(Long id, Map<String, Object> settings);
}
