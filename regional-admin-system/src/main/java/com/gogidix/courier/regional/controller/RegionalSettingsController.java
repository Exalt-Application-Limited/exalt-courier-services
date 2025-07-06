package com.gogidix.courier.regional.controller;

import com.socialecommerceecosystem.regional.model.RegionalSettings;
import com.socialecommerceecosystem.regional.service.RegionalSettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing regional settings.
 */
@RestController
@RequestMapping("/api/v1/regional-settings")
@RequiredArgsConstructor
@Slf4j
public class RegionalSettingsController {

    private final RegionalSettingsService regionalSettingsService;

    /**
     * GET /api/v1/regional-settings : Get all regional settings
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings
     */
    @GetMapping
    public ResponseEntity<List<RegionalSettings>> getAllRegionalSettings() {
        log.debug("REST request to get all regional settings");
        return ResponseEntity.ok(regionalSettingsService.getAllRegionalSettings());
    }

    /**
     * GET /api/v1/regional-settings/active : Get all active regional settings
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active regional settings
     */
    @GetMapping("/active")
    public ResponseEntity<List<RegionalSettings>> getActiveRegionalSettings() {
        log.debug("REST request to get all active regional settings");
        return ResponseEntity.ok(regionalSettingsService.getActiveRegionalSettings());
    }

    /**
     * GET /api/v1/regional-settings/{id} : Get a regional settings by id
     * 
     * @param id the id of the regional settings to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regional settings, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionalSettings> getRegionalSettings(@PathVariable Long id) {
        log.debug("REST request to get regional settings : {}", id);
        return regionalSettingsService.getRegionalSettingsById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regional settings not found with id: " + id));
    }

    /**
     * GET /api/v1/regional-settings/code/{regionCode} : Get a regional settings by region code
     * 
     * @param regionCode the code of the regional settings to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regional settings, or with status 404 (Not Found)
     */
    @GetMapping("/code/{regionCode}")
    public ResponseEntity<RegionalSettings> getRegionalSettingsByCode(@PathVariable String regionCode) {
        log.debug("REST request to get regional settings by code : {}", regionCode);
        return regionalSettingsService.getRegionalSettingsByCode(regionCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regional settings not found with code: " + regionCode));
    }

    /**
     * GET /api/v1/regional-settings/global-region/{globalRegionId} : Get regional settings by global region ID
     * 
     * @param globalRegionId the ID of the global region
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings for the global region
     */
    @GetMapping("/global-region/{globalRegionId}")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsByGlobalRegionId(@PathVariable Long globalRegionId) {
        log.debug("REST request to get regional settings for global region : {}", globalRegionId);
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsByGlobalRegionId(globalRegionId));
    }

    /**
     * GET /api/v1/regional-settings/search : Search regional settings by name
     * 
     * @param searchText the text to search for in regional settings names
     * @return the ResponseEntity with status 200 (OK) and the list of matching regional settings
     */
    @GetMapping("/search")
    public ResponseEntity<List<RegionalSettings>> searchRegionalSettingsByName(@RequestParam String searchText) {
        log.debug("REST request to search regional settings by name containing : {}", searchText);
        return ResponseEntity.ok(regionalSettingsService.searchRegionalSettingsByName(searchText));
    }

    /**
     * GET /api/v1/regional-settings/timezone/{timezone} : Get regional settings by timezone
     * 
     * @param timezone the timezone to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings in the timezone
     */
    @GetMapping("/timezone/{timezone}")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsByTimezone(@PathVariable String timezone) {
        log.debug("REST request to get regional settings by timezone : {}", timezone);
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsByTimezone(timezone));
    }

    /**
     * GET /api/v1/regional-settings/currency/{currencyCode} : Get regional settings by currency code
     * 
     * @param currencyCode the currency code to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings using the currency
     */
    @GetMapping("/currency/{currencyCode}")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsByCurrencyCode(@PathVariable String currencyCode) {
        log.debug("REST request to get regional settings by currency code : {}", currencyCode);
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsByCurrencyCode(currencyCode));
    }

    /**
     * GET /api/v1/regional-settings/locale/{locale} : Get regional settings by locale
     * 
     * @param locale the locale to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings with the locale
     */
    @GetMapping("/locale/{locale}")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsByLocale(@PathVariable String locale) {
        log.debug("REST request to get regional settings by locale : {}", locale);
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsByLocale(locale));
    }

    /**
     * GET /api/v1/regional-settings/measurement-system/{measurementSystem} : Get regional settings by measurement system
     * 
     * @param measurementSystem the measurement system to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings using the measurement system
     */
    @GetMapping("/measurement-system/{measurementSystem}")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsByMeasurementSystem(@PathVariable String measurementSystem) {
        log.debug("REST request to get regional settings by measurement system : {}", measurementSystem);
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsByMeasurementSystem(measurementSystem));
    }

    /**
     * GET /api/v1/regional-settings/manager/{managerName} : Get regional settings by manager name
     * 
     * @param managerName the manager name to search for
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings managed by the manager
     */
    @GetMapping("/manager/{managerName}")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsByManager(@PathVariable String managerName) {
        log.debug("REST request to get regional settings by manager : {}", managerName);
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsByManager(managerName));
    }

    /**
     * GET /api/v1/regional-settings/global-sync : Get regional settings with global sync enabled
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings with global sync enabled
     */
    @GetMapping("/global-sync")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsWithGlobalSync() {
        log.debug("REST request to get regional settings with global sync enabled");
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsWithGlobalSync());
    }

    /**
     * GET /api/v1/regional-settings/missing-emergency-contact : Get regional settings with missing emergency contact
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regional settings with missing emergency contact
     */
    @GetMapping("/missing-emergency-contact")
    public ResponseEntity<List<RegionalSettings>> getRegionalSettingsWithMissingEmergencyContact() {
        log.debug("REST request to get regional settings with missing emergency contact");
        return ResponseEntity.ok(regionalSettingsService.getRegionalSettingsWithMissingEmergencyContact());
    }

    /**
     * GET /api/v1/regional-settings/count-by-global-region/{globalRegionId} : Count regional settings by global region ID
     * 
     * @param globalRegionId the ID of the global region
     * @return the ResponseEntity with status 200 (OK) and the count of regional settings for the global region
     */
    @GetMapping("/count-by-global-region/{globalRegionId}")
    public ResponseEntity<Long> countByGlobalRegionId(@PathVariable Long globalRegionId) {
        log.debug("REST request to count regional settings by global region ID : {}", globalRegionId);
        return ResponseEntity.ok(regionalSettingsService.countByGlobalRegionId(globalRegionId));
    }

    /**
     * POST /api/v1/regional-settings : Create a new regional settings
     * 
     * @param regionalSettings the regional settings to create
     * @return the ResponseEntity with status 201 (Created) and with body the new regional settings
     */
    @PostMapping
    public ResponseEntity<RegionalSettings> createRegionalSettings(@Valid @RequestBody RegionalSettings regionalSettings) {
        log.debug("REST request to save regional settings : {}", regionalSettings);
        if (regionalSettings.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new regional settings cannot already have an ID");
        }
        
        try {
            RegionalSettings result = regionalSettingsService.createRegionalSettings(regionalSettings);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/regional-settings/{id} : Update an existing regional settings
     * 
     * @param id the id of the regional settings to update
     * @param regionalSettings the regional settings to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional settings
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionalSettings> updateRegionalSettings(
            @PathVariable Long id, 
            @Valid @RequestBody RegionalSettings regionalSettings) {
        log.debug("REST request to update regional settings : {}", regionalSettings);
        if (regionalSettings.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Regional settings ID must not be null");
        }
        if (!id.equals(regionalSettings.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            RegionalSettings result = regionalSettingsService.updateRegionalSettings(id, regionalSettings);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/regional-settings/{id} : Delete a regional settings
     * 
     * @param id the id of the regional settings to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegionalSettings(@PathVariable Long id) {
        log.debug("REST request to delete regional settings : {}", id);
        try {
            regionalSettingsService.deleteRegionalSettings(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-settings/{id}/sync : Synchronize regional settings with global settings
     * 
     * @param id the id of the regional settings to synchronize
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional settings
     */
    @PatchMapping("/{id}/sync")
    public ResponseEntity<RegionalSettings> synchronizeWithGlobalSettings(@PathVariable Long id) {
        log.debug("REST request to synchronize regional settings : {} with global settings", id);
        try {
            RegionalSettings result = regionalSettingsService.synchronizeWithGlobalSettings(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-settings/{id}/settings : Update specific settings in a regional settings
     * 
     * @param id the id of the regional settings to update
     * @param settings the settings to update (key-value pairs)
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional settings
     */
    @PatchMapping("/{id}/settings")
    public ResponseEntity<RegionalSettings> updateSpecificSettings(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> settings) {
        log.debug("REST request to update specific settings for regional settings : {}", id);
        try {
            RegionalSettings result = regionalSettingsService.updateSpecificSettings(id, settings);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * HEAD /api/v1/regional-settings/code/{regionCode} : Check if a regional settings with the given region code exists
     * 
     * @param regionCode the region code to check
     * @return the ResponseEntity with status 200 (OK) if exists, or 404 (Not Found) if not
     */
    @RequestMapping(method = RequestMethod.HEAD, path = "/code/{regionCode}")
    public ResponseEntity<Void> checkRegionCodeExists(@PathVariable String regionCode) {
        log.debug("REST request to check if regional settings exists with code : {}", regionCode);
        return regionalSettingsService.existsByRegionCode(regionCode) 
            ? ResponseEntity.ok().build() 
            : ResponseEntity.notFound().build();
    }
}
