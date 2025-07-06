package com.gogidix.courier.courier.hqadmin.controller;

import com.socialecommerceecosystem.hqadmin.model.GlobalSettings;
import com.socialecommerceecosystem.hqadmin.service.GlobalSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing global settings.
 */
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Slf4j
public class GlobalSettingsController {

    private final GlobalSettingsService globalSettingsService;

    /**
     * GET /api/v1/settings : Get all settings
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of settings
     */
    @GetMapping
    public ResponseEntity<List<GlobalSettings>> getAllSettings() {
        log.debug("REST request to get all global settings");
        return ResponseEntity.ok(globalSettingsService.getAllSettings());
    }

    /**
     * GET /api/v1/settings/{id} : Get a setting by id
     * 
     * @param id the id of the setting to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the setting, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GlobalSettings> getSetting(@PathVariable Long id) {
        log.debug("REST request to get setting : {}", id);
        return globalSettingsService.getSettingById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found with id: " + id));
    }

    /**
     * GET /api/v1/settings/key/{key} : Get a setting by key
     * 
     * @param key the key of the setting to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the setting, or with status 404 (Not Found)
     */
    @GetMapping("/key/{key}")
    public ResponseEntity<GlobalSettings> getSettingByKey(@PathVariable String key) {
        log.debug("REST request to get setting by key: {}", key);
        return globalSettingsService.getSettingByKey(key)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setting not found with key: " + key));
    }

    /**
     * GET /api/v1/settings/category/{category} : Get all settings in a category
     * 
     * @param category the category of settings to retrieve
     * @return the ResponseEntity with status 200 (OK) and the list of settings
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<GlobalSettings>> getSettingsByCategory(@PathVariable String category) {
        log.debug("REST request to get settings by category: {}", category);
        return ResponseEntity.ok(globalSettingsService.getSettingsByCategory(category));
    }

    /**
     * GET /api/v1/settings/mutable : Get all mutable settings
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of mutable settings
     */
    @GetMapping("/mutable")
    public ResponseEntity<List<GlobalSettings>> getMutableSettings() {
        log.debug("REST request to get all mutable settings");
        return ResponseEntity.ok(globalSettingsService.getMutableSettings());
    }

    /**
     * GET /api/v1/settings/search : Search for settings by key
     * 
     * @param searchText the text to search for in setting keys
     * @return the ResponseEntity with status 200 (OK) and the list of matching settings
     */
    @GetMapping("/search")
    public ResponseEntity<List<GlobalSettings>> searchSettingsByKey(@RequestParam String searchText) {
        log.debug("REST request to search settings by key containing: {}", searchText);
        return ResponseEntity.ok(globalSettingsService.searchSettingsByKey(searchText));
    }

    /**
     * POST /api/v1/settings : Create a new setting
     * 
     * @param setting the setting to create
     * @return the ResponseEntity with status 201 (Created) and with body the new setting
     */
    @PostMapping
    public ResponseEntity<GlobalSettings> createSetting(@Valid @RequestBody GlobalSettings setting) {
        log.debug("REST request to save setting : {}", setting);
        if (setting.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new setting cannot already have an ID");
        }
        
        try {
            GlobalSettings result = globalSettingsService.createSetting(setting);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/settings/{id} : Update an existing setting
     * 
     * @param id the id of the setting to update
     * @param setting the setting to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated setting
     */
    @PutMapping("/{id}")
    public ResponseEntity<GlobalSettings> updateSetting(
            @PathVariable Long id, 
            @Valid @RequestBody GlobalSettings setting) {
        log.debug("REST request to update setting : {}", setting);
        if (setting.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Setting ID must not be null");
        }
        if (!id.equals(setting.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            GlobalSettings result = globalSettingsService.updateSetting(id, setting);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/settings/{id} : Delete a setting
     * 
     * @param id the id of the setting to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        log.debug("REST request to delete setting : {}", id);
        try {
            globalSettingsService.deleteSetting(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/settings/bulk : Bulk update settings
     * 
     * @param settings the list of settings to update
     * @return the ResponseEntity with status 200 (OK) and with body the list of updated settings
     */
    @PostMapping("/bulk")
    public ResponseEntity<List<GlobalSettings>> bulkUpdateSettings(@Valid @RequestBody List<GlobalSettings> settings) {
        log.debug("REST request to bulk update {} settings", settings.size());
        List<GlobalSettings> result = globalSettingsService.bulkUpdateSettings(settings);
        return ResponseEntity.ok(result);
    }
}
