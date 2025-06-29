package com.exalt.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.GlobalSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link GlobalSettings} entity that provides
 * data access operations for global system settings.
 */
@Repository
public interface GlobalSettingsRepository extends JpaRepository<GlobalSettings, Long> {
    
    /**
     * Find a global setting by its key
     * 
     * @param key The setting key
     * @return The setting if found
     */
    Optional<GlobalSettings> findByKey(String key);
    
    /**
     * Find all settings in a specific category
     * 
     * @param category The category name
     * @return List of settings in the category
     */
    List<GlobalSettings> findByCategory(String category);
    
    /**
     * Find all mutable settings
     * 
     * @return List of mutable settings
     */
    List<GlobalSettings> findByIsMutableTrue();
    
    /**
     * Find settings by key that contains the search text
     * 
     * @param searchText The search text to look for in the key
     * @return List of matching settings
     */
    List<GlobalSettings> findByKeyContainingIgnoreCase(String searchText);
}
