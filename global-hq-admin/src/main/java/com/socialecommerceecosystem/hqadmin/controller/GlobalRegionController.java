package com.exalt.courier.hqadmin.controller;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import com.socialecommerceecosystem.hqadmin.service.GlobalRegionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing global regions.
 */
@RestController
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
@Slf4j
public class GlobalRegionController {

    private final GlobalRegionService globalRegionService;

    /**
     * GET /api/v1/regions : Get all global regions
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of regions
     */
    @GetMapping
    public ResponseEntity<List<GlobalRegion>> getAllRegions() {
        log.debug("REST request to get all global regions");
        return ResponseEntity.ok(globalRegionService.getAllRegions());
    }

    /**
     * GET /api/v1/regions/active : Get all active global regions
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active regions
     */
    @GetMapping("/active")
    public ResponseEntity<List<GlobalRegion>> getAllActiveRegions() {
        log.debug("REST request to get all active global regions");
        return ResponseEntity.ok(globalRegionService.getAllActiveRegions());
    }

    /**
     * GET /api/v1/regions/{id} : Get a global region by id
     * 
     * @param id the id of the region to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the region, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GlobalRegion> getRegion(@PathVariable Long id) {
        log.debug("REST request to get global region : {}", id);
        return globalRegionService.getRegionById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found with id: " + id));
    }

    /**
     * GET /api/v1/regions/code/{regionCode} : Get a global region by code
     * 
     * @param regionCode the code of the region to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the region, or with status 404 (Not Found)
     */
    @GetMapping("/code/{regionCode}")
    public ResponseEntity<GlobalRegion> getRegionByCode(@PathVariable String regionCode) {
        log.debug("REST request to get global region by code: {}", regionCode);
        return globalRegionService.getRegionByCode(regionCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Region not found with code: " + regionCode));
    }

    /**
     * GET /api/v1/regions/top-level : Get all top-level regions
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of top-level regions
     */
    @GetMapping("/top-level")
    public ResponseEntity<List<GlobalRegion>> getTopLevelRegions() {
        log.debug("REST request to get all top-level global regions");
        return ResponseEntity.ok(globalRegionService.getTopLevelRegions());
    }

    /**
     * GET /api/v1/regions/{parentId}/children : Get all child regions of a parent region
     * 
     * @param parentId the id of the parent region
     * @return the ResponseEntity with status 200 (OK) and the list of child regions
     */
    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<GlobalRegion>> getChildRegions(@PathVariable Long parentId) {
        log.debug("REST request to get child regions of parent region: {}", parentId);
        try {
            List<GlobalRegion> childRegions = globalRegionService.getChildRegions(parentId);
            return ResponseEntity.ok(childRegions);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/regions/search : Search regions by name
     * 
     * @param searchText the text to search for in region names
     * @return the ResponseEntity with status 200 (OK) and the list of matching regions
     */
    @GetMapping("/search")
    public ResponseEntity<List<GlobalRegion>> searchRegionsByName(@RequestParam String searchText) {
        log.debug("REST request to search global regions by name containing: {}", searchText);
        return ResponseEntity.ok(globalRegionService.searchRegionsByName(searchText));
    }

    /**
     * GET /api/v1/regions/currency/{currencyCode} : Find regions by currency code
     * 
     * @param currencyCode the currency code to search for
     * @return the ResponseEntity with status 200 (OK) and the list of regions using the specified currency
     */
    @GetMapping("/currency/{currencyCode}")
    public ResponseEntity<List<GlobalRegion>> getRegionsByCurrencyCode(@PathVariable String currencyCode) {
        log.debug("REST request to get global regions by currency code: {}", currencyCode);
        return ResponseEntity.ok(globalRegionService.findRegionsByCurrencyCode(currencyCode));
    }

    /**
     * GET /api/v1/regions/geo-box : Find regions within a geographic bounding box
     * 
     * @param minLat Minimum latitude
     * @param maxLat Maximum latitude
     * @param minLong Minimum longitude
     * @param maxLong Maximum longitude
     * @return the ResponseEntity with status 200 (OK) and the list of regions within the specified bounding box
     */
    @GetMapping("/geo-box")
    public ResponseEntity<List<GlobalRegion>> getRegionsInBoundingBox(
            @RequestParam Double minLat,
            @RequestParam Double maxLat,
            @RequestParam Double minLong,
            @RequestParam Double maxLong) {
        log.debug("REST request to get global regions in bounding box: [{}, {}, {}, {}]", minLat, maxLat, minLong, maxLong);
        return ResponseEntity.ok(globalRegionService.findRegionsInBoundingBox(minLat, maxLat, minLong, maxLong));
    }

    /**
     * GET /api/v1/regions/{parentId}/child-count : Count the number of child regions for a parent region
     * 
     * @param parentId the id of the parent region
     * @return the ResponseEntity with status 200 (OK) and the count of child regions
     */
    @GetMapping("/{parentId}/child-count")
    public ResponseEntity<Long> countChildRegions(@PathVariable Long parentId) {
        log.debug("REST request to count child regions of parent region: {}", parentId);
        try {
            Long count = globalRegionService.countChildRegions(parentId);
            return ResponseEntity.ok(count);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * POST /api/v1/regions : Create a new global region
     * 
     * @param region the global region to create
     * @return the ResponseEntity with status 201 (Created) and with body the new global region
     */
    @PostMapping
    public ResponseEntity<GlobalRegion> createRegion(@Valid @RequestBody GlobalRegion region) {
        log.debug("REST request to save global region : {}", region);
        if (region.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new region cannot already have an ID");
        }
        
        try {
            GlobalRegion result = globalRegionService.createRegion(region);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/regions/{id} : Update an existing global region
     * 
     * @param id the id of the global region to update
     * @param region the global region to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated global region
     */
    @PutMapping("/{id}")
    public ResponseEntity<GlobalRegion> updateRegion(
            @PathVariable Long id, 
            @Valid @RequestBody GlobalRegion region) {
        log.debug("REST request to update global region : {}", region);
        if (region.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Region ID must not be null");
        }
        if (!id.equals(region.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            GlobalRegion result = globalRegionService.updateRegion(id, region);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/regions/{id} : Delete a global region
     * 
     * @param id the id of the global region to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegion(@PathVariable Long id) {
        log.debug("REST request to delete global region : {}", id);
        try {
            globalRegionService.deleteRegion(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * POST /api/v1/regions/{parentId}/children/{childId} : Add a child region to a parent region
     * 
     * @param parentId the id of the parent region
     * @param childId the id of the child region to add
     * @return the ResponseEntity with status 200 (OK) and with body the updated parent region
     */
    @PostMapping("/{parentId}/children/{childId}")
    public ResponseEntity<GlobalRegion> addChildToParentRegion(
            @PathVariable Long parentId,
            @PathVariable Long childId) {
        log.debug("REST request to add child region {} to parent region {}", childId, parentId);
        try {
            GlobalRegion result = globalRegionService.addChildToParentRegion(parentId, childId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/regions/{parentId}/children/{childId} : Remove a child region from a parent region
     * 
     * @param parentId the id of the parent region
     * @param childId the id of the child region to remove
     * @return the ResponseEntity with status 200 (OK) and with body the updated parent region
     */
    @DeleteMapping("/{parentId}/children/{childId}")
    public ResponseEntity<GlobalRegion> removeChildFromParentRegion(
            @PathVariable Long parentId,
            @PathVariable Long childId) {
        log.debug("REST request to remove child region {} from parent region {}", childId, parentId);
        try {
            GlobalRegion result = globalRegionService.removeChildFromParentRegion(parentId, childId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
