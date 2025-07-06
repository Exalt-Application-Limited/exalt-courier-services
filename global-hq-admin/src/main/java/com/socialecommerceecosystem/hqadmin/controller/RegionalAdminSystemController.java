package com.gogidix.courier.hqadmin.controller;

import com.socialecommerceecosystem.hqadmin.model.RegionalAdminSystem;
import com.socialecommerceecosystem.hqadmin.service.RegionalAdminSystemService;
import com.socialecommerceecosystem.hqadmin.service.GlobalRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing regional admin systems.
 */
@RestController
@RequestMapping("/api/v1/regional-systems")
@RequiredArgsConstructor
@Slf4j
public class RegionalAdminSystemController {

    private final RegionalAdminSystemService regionalAdminSystemService;
    private final GlobalRegionService globalRegionService;

    /**
     * GET /api/v1/regional-systems : Get all regional admin systems
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regional admin systems
     */
    @GetMapping
    public ResponseEntity<List<RegionalAdminSystem>> getAllRegionalAdminSystems() {
        log.debug("REST request to get all regional admin systems");
        return ResponseEntity.ok(regionalAdminSystemService.getAllRegionalAdminSystems());
    }

    /**
     * GET /api/v1/regional-systems/active : Get all active regional admin systems
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active regional admin systems
     */
    @GetMapping("/active")
    public ResponseEntity<List<RegionalAdminSystem>> getAllActiveRegionalAdminSystems() {
        log.debug("REST request to get all active regional admin systems");
        return ResponseEntity.ok(regionalAdminSystemService.getAllActiveRegionalAdminSystems());
    }

    /**
     * GET /api/v1/regional-systems/{id} : Get a regional admin system by id
     * 
     * @param id the id of the regional admin system to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regional admin system, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<RegionalAdminSystem> getRegionalAdminSystem(@PathVariable Long id) {
        log.debug("REST request to get regional admin system : {}", id);
        return regionalAdminSystemService.getRegionalAdminSystemById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regional admin system not found with id: " + id));
    }

    /**
     * GET /api/v1/regional-systems/code/{systemCode} : Get a regional admin system by code
     * 
     * @param systemCode the code of the regional admin system to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the regional admin system, or with status 404 (Not Found)
     */
    @GetMapping("/code/{systemCode}")
    public ResponseEntity<RegionalAdminSystem> getRegionalAdminSystemByCode(@PathVariable String systemCode) {
        log.debug("REST request to get regional admin system by code: {}", systemCode);
        return regionalAdminSystemService.getRegionalAdminSystemByCode(systemCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Regional admin system not found with code: " + systemCode));
    }

    /**
     * GET /api/v1/regional-systems/region/{regionId} : Get all regional admin systems in a region
     * 
     * @param regionId the id of the region to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of regional admin systems in the region
     */
    @GetMapping("/region/{regionId}")
    public ResponseEntity<List<RegionalAdminSystem>> getRegionalAdminSystemsByRegion(@PathVariable Long regionId) {
        log.debug("REST request to get regional admin systems in region: {}", regionId);
        
        try {
            List<RegionalAdminSystem> systems = regionalAdminSystemService.getRegionalAdminSystemsByRegion(regionId);
            return ResponseEntity.ok(systems);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/regional-systems/search : Search regional admin systems by name
     * 
     * @param searchText the text to search for in regional admin system names
     * @return the ResponseEntity with status 200 (OK) and the list of matching regional admin systems
     */
    @GetMapping("/search")
    public ResponseEntity<List<RegionalAdminSystem>> searchRegionalAdminSystemsByName(@RequestParam String searchText) {
        log.debug("REST request to search regional admin systems by name containing: {}", searchText);
        return ResponseEntity.ok(regionalAdminSystemService.searchRegionalAdminSystemsByName(searchText));
    }

    /**
     * GET /api/v1/regional-systems/health-status/{status} : Find regional admin systems by health status
     * 
     * @param status the health status to search for
     * @return the ResponseEntity with status 200 (OK) and the list of regional admin systems with the specified health status
     */
    @GetMapping("/health-status/{status}")
    public ResponseEntity<List<RegionalAdminSystem>> getRegionalAdminSystemsByHealthStatus(@PathVariable String status) {
        log.debug("REST request to get regional admin systems by health status: {}", status);
        return ResponseEntity.ok(regionalAdminSystemService.findSystemsByHealthStatus(status));
    }

    /**
     * GET /api/v1/regional-systems/stale-health : Find regional admin systems with stale health checks
     * 
     * @param hours the number of hours to consider a health check stale
     * @return the ResponseEntity with status 200 (OK) and the list of regional admin systems with stale health checks
     */
    @GetMapping("/stale-health")
    public ResponseEntity<List<RegionalAdminSystem>> getRegionalAdminSystemsWithStaleHealthChecks(@RequestParam(defaultValue = "24") int hours) {
        log.debug("REST request to get regional admin systems with stale health checks (> {} hours)", hours);
        LocalDateTime checkTime = LocalDateTime.now().minusHours(hours);
        return ResponseEntity.ok(regionalAdminSystemService.findSystemsWithStaleHealthChecks(checkTime));
    }

    /**
     * GET /api/v1/regional-systems/count-by-region : Count active regional admin systems per global region
     * 
     * @return the ResponseEntity with status 200 (OK) and the map of region name to system count
     */
    @GetMapping("/count-by-region")
    public ResponseEntity<Map<String, Long>> countActiveSystemsByRegion() {
        log.debug("REST request to count active regional admin systems by region");
        return ResponseEntity.ok(regionalAdminSystemService.countActiveSystemsByRegion());
    }

    /**
     * POST /api/v1/regional-systems : Create a new regional admin system
     * 
     * @param system the regional admin system to create
     * @return the ResponseEntity with status 201 (Created) and with body the new regional admin system
     */
    @PostMapping
    public ResponseEntity<RegionalAdminSystem> createRegionalAdminSystem(@Valid @RequestBody RegionalAdminSystem system) {
        log.debug("REST request to save regional admin system : {}", system);
        if (system.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new regional admin system cannot already have an ID");
        }
        
        try {
            RegionalAdminSystem result = regionalAdminSystemService.createRegionalAdminSystem(system);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/regional-systems/{id} : Update an existing regional admin system
     * 
     * @param id the id of the regional admin system to update
     * @param system the regional admin system to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin system
     */
    @PutMapping("/{id}")
    public ResponseEntity<RegionalAdminSystem> updateRegionalAdminSystem(
            @PathVariable Long id, 
            @Valid @RequestBody RegionalAdminSystem system) {
        log.debug("REST request to update regional admin system : {}", system);
        if (system.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Regional admin system ID must not be null");
        }
        if (!id.equals(system.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            RegionalAdminSystem result = regionalAdminSystemService.updateRegionalAdminSystem(id, system);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-systems/{id}/health : Update the health check for a regional admin system
     * 
     * @param id the id of the regional admin system to update
     * @param healthDetails the health details containing status
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin system
     */
    @PatchMapping("/{id}/health")
    public ResponseEntity<RegionalAdminSystem> updateHealthCheck(
            @PathVariable Long id, 
            @RequestBody Map<String, String> healthDetails) {
        log.debug("REST request to update health check for regional admin system : {}", id);
        
        if (!healthDetails.containsKey("healthStatus")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Health status is required");
        }
        
        try {
            RegionalAdminSystem result = regionalAdminSystemService.updateHealthCheck(id, healthDetails.get("healthStatus"));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-systems/{id}/api-credentials : Update API credentials for a regional admin system
     * 
     * @param id the id of the regional admin system to update
     * @param credentials the credentials containing apiKey and authToken
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin system
     */
    @PatchMapping("/{id}/api-credentials")
    public ResponseEntity<RegionalAdminSystem> updateApiCredentials(
            @PathVariable Long id, 
            @RequestBody Map<String, String> credentials) {
        log.debug("REST request to update API credentials for regional admin system : {}", id);
        
        if (!credentials.containsKey("apiKey") || !credentials.containsKey("authToken")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "API key and auth token are required");
        }
        
        try {
            RegionalAdminSystem result = regionalAdminSystemService.updateApiCredentials(
                id, credentials.get("apiKey"), credentials.get("authToken"));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/regional-systems/{id}/contact-info : Update contact information for a regional admin system
     * 
     * @param id the id of the regional admin system to update
     * @param contactInfo the contact information containing email, phone, and supportUrl
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin system
     */
    @PatchMapping("/{id}/contact-info")
    public ResponseEntity<RegionalAdminSystem> updateContactInfo(
            @PathVariable Long id, 
            @RequestBody Map<String, String> contactInfo) {
        log.debug("REST request to update contact info for regional admin system : {}", id);
        
        try {
            RegionalAdminSystem result = regionalAdminSystemService.updateContactInfo(
                id, 
                contactInfo.getOrDefault("email", null),
                contactInfo.getOrDefault("phone", null), 
                contactInfo.getOrDefault("supportUrl", null));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/regional-systems/{id} : Delete a regional admin system
     * 
     * @param id the id of the regional admin system to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegionalAdminSystem(@PathVariable Long id) {
        log.debug("REST request to delete regional admin system : {}", id);
        try {
            regionalAdminSystemService.deleteRegionalAdminSystem(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * POST /api/v1/regional-systems/{systemId}/transfer/{regionId} : Transfer a regional admin system to another region
     * 
     * @param systemId the id of the regional admin system to transfer
     * @param regionId the id of the new region
     * @return the ResponseEntity with status 200 (OK) and with body the updated regional admin system
     */
    @PostMapping("/{systemId}/transfer/{regionId}")
    public ResponseEntity<RegionalAdminSystem> transferToRegion(
            @PathVariable Long systemId,
            @PathVariable Long regionId) {
        log.debug("REST request to transfer regional admin system {} to region {}", systemId, regionId);
        try {
            RegionalAdminSystem result = regionalAdminSystemService.transferToRegion(systemId, regionId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * POST /api/v1/regional-systems/health-check-all : Perform health checks on all active systems
     * 
     * @return the ResponseEntity with status 200 (OK) and with body the list of updated regional admin systems
     */
    @PostMapping("/health-check-all")
    public ResponseEntity<List<RegionalAdminSystem>> performHealthChecksForAllActiveSystems() {
        log.debug("REST request to perform health checks for all active regional admin systems");
        return ResponseEntity.ok(regionalAdminSystemService.performHealthChecksForAllActiveSystems());
    }
}
