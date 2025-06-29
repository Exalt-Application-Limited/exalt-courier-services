package com.exalt.courier.hqadmin.service;

import com.socialecommerceecosystem.hqadmin.model.GlobalSettings;

import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for managing global settings.
 */
public interface GlobalSettingsService {
    
    /**
     * Get all global settings
     * 
     * @return List of all global settings
     */
    List<GlobalSettings> getAllSettings();
    
    /**
     * Get a global setting by its ID
     * 
     * @param id The setting ID
     * @return The global setting if found
     */
    Optional<GlobalSettings> getSettingById(Long id);
    
    /**
     * Get a global setting by its key
     * 
     * @param key The setting key
     * @return The global setting if found
     */
    Optional<GlobalSettings> getSettingByKey(String key);
    
    /**
     * Get all settings in a specific category
     * 
     * @param category The category name
     * @return List of settings in the category
     */
    List<GlobalSettings> getSettingsByCategory(String category);
    
    /**
     * Create a new global setting
     * 
     * @param setting The global setting to create
     * @return The created global setting
     */
    GlobalSettings createSetting(GlobalSettings setting);
    
    /**
     * Update an existing global setting
     * 
     * @param id The setting ID to update
     * @param settingDetails The updated setting details
     * @return The updated global setting
     * @throws RuntimeException if setting not found or is immutable
     */
    GlobalSettings updateSetting(Long id, GlobalSettings settingDetails);
    
    /**
     * Delete a global setting
     * 
     * @param id The setting ID to delete
     * @throws RuntimeException if setting not found
     */
    void deleteSetting(Long id);
    
    /**
     * Get a setting value by key, returning a default value if not found
     * 
     * @param key The setting key
     * @param defaultValue The default value to return if setting not found
     * @return The setting value or default value
     */
    String getSettingValueOrDefault(String key, String defaultValue);
    
    /**
     * Search for settings by key containing a search text
     * 
     * @param searchText The text to search for in the setting keys
     * @return List of matching settings
     */
    List<GlobalSettings> searchSettingsByKey(String searchText);
    
    /**
     * Get all mutable settings that can be changed
     * 
     * @return List of mutable settings
     */
    List<GlobalSettings> getMutableSettings();
    
    /**
     * Bulk update of settings
     * 
     * @param settings List of settings to update
     * @return List of updated settings
     */
    List<GlobalSettings> bulkUpdateSettings(List<GlobalSettings> settings);
}
