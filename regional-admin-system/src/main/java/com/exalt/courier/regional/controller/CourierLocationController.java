package com.exalt.courier.regional.controller;

import com.socialecommerceecosystem.regional.model.CourierLocation;
import com.socialecommerceecosystem.regional.service.CourierLocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing courier locations.
 */
@RestController
@RequestMapping("/api/v1/courier-locations")
@RequiredArgsConstructor
@Slf4j
public class CourierLocationController {

    private final CourierLocationService courierLocationService;

    /**
     * GET /api/v1/courier-locations : Get all courier locations
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations
     */
    @GetMapping
    public ResponseEntity<List<CourierLocation>> getAllCourierLocations() {
        log.debug("REST request to get all courier locations");
        return ResponseEntity.ok(courierLocationService.getAllCourierLocations());
    }

    /**
     * GET /api/v1/courier-locations/paginated : Get all courier locations with pagination
     * 
     * @param pageable pagination information
     * @return the ResponseEntity with status 200 (OK) and a page of courier locations
     */
    @GetMapping("/paginated")
    public ResponseEntity<Page<CourierLocation>> getAllCourierLocationsPaginated(Pageable pageable) {
        log.debug("REST request to get a page of courier locations");
        return ResponseEntity.ok(courierLocationService.getAllCourierLocations(pageable));
    }

    /**
     * GET /api/v1/courier-locations/active : Get all active courier locations
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of active courier locations
     */
    @GetMapping("/active")
    public ResponseEntity<List<CourierLocation>> getActiveCourierLocations() {
        log.debug("REST request to get all active courier locations");
        return ResponseEntity.ok(courierLocationService.getActiveCourierLocations());
    }

    /**
     * GET /api/v1/courier-locations/{id} : Get a courier location by id
     * 
     * @param id the id of the courier location to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the courier location, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourierLocation> getCourierLocation(@PathVariable Long id) {
        log.debug("REST request to get courier location : {}", id);
        return courierLocationService.getCourierLocationById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier location not found with id: " + id));
    }

    /**
     * GET /api/v1/courier-locations/code/{locationCode} : Get a courier location by location code
     * 
     * @param locationCode the code of the courier location to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the courier location, or with status 404 (Not Found)
     */
    @GetMapping("/code/{locationCode}")
    public ResponseEntity<CourierLocation> getCourierLocationByCode(@PathVariable String locationCode) {
        log.debug("REST request to get courier location by code : {}", locationCode);
        return courierLocationService.getCourierLocationByCode(locationCode)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Courier location not found with code: " + locationCode));
    }

    /**
     * GET /api/v1/courier-locations/region/{regionalSettingsId} : Get courier locations by regional settings ID
     * 
     * @param regionalSettingsId the ID of the regional settings
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations for the regional settings
     */
    @GetMapping("/region/{regionalSettingsId}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByRegionalSettingsId(@PathVariable Long regionalSettingsId) {
        log.debug("REST request to get courier locations for regional settings : {}", regionalSettingsId);
        try {
            return ResponseEntity.ok(courierLocationService.getCourierLocationsByRegionalSettingsId(regionalSettingsId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/courier-locations/region/{regionalSettingsId}/paginated : Get courier locations by regional settings ID with pagination
     * 
     * @param regionalSettingsId the ID of the regional settings
     * @param pageable pagination information
     * @return the ResponseEntity with status 200 (OK) and a page of courier locations for the regional settings
     */
    @GetMapping("/region/{regionalSettingsId}/paginated")
    public ResponseEntity<Page<CourierLocation>> getCourierLocationsByRegionalSettingsIdPaginated(
            @PathVariable Long regionalSettingsId, 
            Pageable pageable) {
        log.debug("REST request to get a page of courier locations for regional settings : {}", regionalSettingsId);
        try {
            return ResponseEntity.ok(courierLocationService.getCourierLocationsByRegionalSettingsId(regionalSettingsId, pageable));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * GET /api/v1/courier-locations/type/{locationType} : Get courier locations by type
     * 
     * @param locationType the type of location to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations of the specified type
     */
    @GetMapping("/type/{locationType}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByType(@PathVariable String locationType) {
        log.debug("REST request to get courier locations by type : {}", locationType);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByType(locationType));
    }

    /**
     * GET /api/v1/courier-locations/city/{city} : Get courier locations by city
     * 
     * @param city the city to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations in the city
     */
    @GetMapping("/city/{city}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByCity(@PathVariable String city) {
        log.debug("REST request to get courier locations by city : {}", city);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByCity(city));
    }

    /**
     * GET /api/v1/courier-locations/state/{stateProvince} : Get courier locations by state/province
     * 
     * @param stateProvince the state or province to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations in the state/province
     */
    @GetMapping("/state/{stateProvince}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByStateProvince(@PathVariable String stateProvince) {
        log.debug("REST request to get courier locations by state/province : {}", stateProvince);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByStateProvince(stateProvince));
    }

    /**
     * GET /api/v1/courier-locations/country/{country} : Get courier locations by country
     * 
     * @param country the country to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations in the country
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByCountry(@PathVariable String country) {
        log.debug("REST request to get courier locations by country : {}", country);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByCountry(country));
    }

    /**
     * GET /api/v1/courier-locations/manager/{managerId} : Get courier locations by manager
     * 
     * @param managerId the ID of the manager
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations managed by the manager
     */
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByManager(@PathVariable Long managerId) {
        log.debug("REST request to get courier locations by manager : {}", managerId);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByManager(managerId));
    }

    /**
     * GET /api/v1/courier-locations/hubs : Get hub courier locations
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations that are hubs
     */
    @GetMapping("/hubs")
    public ResponseEntity<List<CourierLocation>> getHubLocations() {
        log.debug("REST request to get hub courier locations");
        return ResponseEntity.ok(courierLocationService.getHubLocations());
    }

    /**
     * GET /api/v1/courier-locations/parent/{parentLocationId} : Get courier locations by parent location
     * 
     * @param parentLocationId the ID of the parent location
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations with the parent location
     */
    @GetMapping("/parent/{parentLocationId}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByParentLocation(@PathVariable Long parentLocationId) {
        log.debug("REST request to get courier locations by parent location : {}", parentLocationId);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByParentLocation(parentLocationId));
    }

    /**
     * GET /api/v1/courier-locations/search : Search courier locations by name
     * 
     * @param searchText the text to search for in courier location names
     * @return the ResponseEntity with status 200 (OK) and the list of matching courier locations
     */
    @GetMapping("/search")
    public ResponseEntity<List<CourierLocation>> searchCourierLocationsByName(@RequestParam String searchText) {
        log.debug("REST request to search courier locations by name containing : {}", searchText);
        return ResponseEntity.ok(courierLocationService.searchCourierLocationsByName(searchText));
    }

    /**
     * GET /api/v1/courier-locations/postal-code/{postalCode} : Get courier locations by postal code
     * 
     * @param postalCode the postal code to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations with the postal code
     */
    @GetMapping("/postal-code/{postalCode}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByPostalCode(@PathVariable String postalCode) {
        log.debug("REST request to get courier locations by postal code : {}", postalCode);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByPostalCode(postalCode));
    }

    /**
     * GET /api/v1/courier-locations/min-rating/{rating} : Get courier locations with minimum rating
     * 
     * @param rating the minimum rating value
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations with ratings at or above the specified value
     */
    @GetMapping("/min-rating/{rating}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsWithMinimumRating(@PathVariable BigDecimal rating) {
        log.debug("REST request to get courier locations with minimum rating : {}", rating);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsWithMinimumRating(rating));
    }

    /**
     * GET /api/v1/courier-locations/with-refrigeration : Get courier locations with refrigeration
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations with refrigeration capabilities
     */
    @GetMapping("/with-refrigeration")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsWithRefrigeration() {
        log.debug("REST request to get courier locations with refrigeration");
        return ResponseEntity.ok(courierLocationService.getCourierLocationsWithRefrigeration());
    }

    /**
     * GET /api/v1/courier-locations/with-security : Get courier locations with security
     * 
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations with security systems
     */
    @GetMapping("/with-security")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsWithSecurity() {
        log.debug("REST request to get courier locations with security");
        return ResponseEntity.ok(courierLocationService.getCourierLocationsWithSecurity());
    }

    /**
     * GET /api/v1/courier-locations/min-daily-capacity/{capacity} : Get courier locations with minimum daily capacity
     * 
     * @param capacity the minimum daily package capacity
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations with capacities at or above the specified value
     */
    @GetMapping("/min-daily-capacity/{capacity}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsWithMinimumDailyCapacity(@PathVariable Integer capacity) {
        log.debug("REST request to get courier locations with minimum daily capacity : {}", capacity);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsWithMinimumDailyCapacity(capacity));
    }

    /**
     * GET /api/v1/courier-locations/min-storage-capacity/{capacity} : Get courier locations with minimum storage capacity
     * 
     * @param capacity the minimum storage capacity
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations with storage capacities at or above the specified value
     */
    @GetMapping("/min-storage-capacity/{capacity}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsWithMinimumStorageCapacity(@PathVariable Integer capacity) {
        log.debug("REST request to get courier locations with minimum storage capacity : {}", capacity);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsWithMinimumStorageCapacity(capacity));
    }

    /**
     * GET /api/v1/courier-locations/property-size-range : Get courier locations by property size range
     * 
     * @param minSize the minimum property size in square feet
     * @param maxSize the maximum property size in square feet
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations within the specified property size range
     */
    @GetMapping("/property-size-range")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByPropertySizeRange(
            @RequestParam Integer minSize, 
            @RequestParam Integer maxSize) {
        log.debug("REST request to get courier locations with property size between {} and {} sq ft", minSize, maxSize);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByPropertySizeRange(minSize, maxSize));
    }

    /**
     * GET /api/v1/courier-locations/service/{service} : Get courier locations offering a specific service
     * 
     * @param service the service to search for
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations that offer the specified service
     */
    @GetMapping("/service/{service}")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsByServiceOffered(@PathVariable String service) {
        log.debug("REST request to get courier locations offering service : {}", service);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsByServiceOffered(service));
    }

    /**
     * GET /api/v1/courier-locations/within-radius : Get courier locations within a radius
     * 
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @param radiusKm the radius in kilometers
     * @return the ResponseEntity with status 200 (OK) and the list of courier locations within the specified radius
     */
    @GetMapping("/within-radius")
    public ResponseEntity<List<CourierLocation>> getCourierLocationsWithinRadius(
            @RequestParam BigDecimal latitude, 
            @RequestParam BigDecimal longitude, 
            @RequestParam Double radiusKm) {
        log.debug("REST request to get courier locations within {} km of ({}, {})", radiusKm, latitude, longitude);
        return ResponseEntity.ok(courierLocationService.getCourierLocationsWithinRadius(latitude, longitude, radiusKm));
    }

    /**
     * GET /api/v1/courier-locations/count-by-type/{locationType} : Count courier locations by type
     * 
     * @param locationType the location type to count
     * @return the ResponseEntity with status 200 (OK) and the count of courier locations of the specified type
     */
    @GetMapping("/count-by-type/{locationType}")
    public ResponseEntity<Long> countByLocationType(@PathVariable String locationType) {
        log.debug("REST request to count courier locations by type : {}", locationType);
        return ResponseEntity.ok(courierLocationService.countByLocationType(locationType));
    }

    /**
     * GET /api/v1/courier-locations/count-by-region/{regionalSettingsId} : Count courier locations by regional settings ID
     * 
     * @param regionalSettingsId the ID of the regional settings
     * @return the ResponseEntity with status 200 (OK) and the count of courier locations for the regional settings
     */
    @GetMapping("/count-by-region/{regionalSettingsId}")
    public ResponseEntity<Long> countByRegionalSettingsId(@PathVariable Long regionalSettingsId) {
        log.debug("REST request to count courier locations by regional settings ID : {}", regionalSettingsId);
        return ResponseEntity.ok(courierLocationService.countByRegionalSettingsId(regionalSettingsId));
    }

    /**
     * POST /api/v1/courier-locations : Create a new courier location
     * 
     * @param courierLocation the courier location to create
     * @return the ResponseEntity with status 201 (Created) and with body the new courier location
     */
    @PostMapping
    public ResponseEntity<CourierLocation> createCourierLocation(@Valid @RequestBody CourierLocation courierLocation) {
        log.debug("REST request to save courier location : {}", courierLocation);
        if (courierLocation.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A new courier location cannot already have an ID");
        }
        
        try {
            CourierLocation result = courierLocationService.createCourierLocation(courierLocation);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PUT /api/v1/courier-locations/{id} : Update an existing courier location
     * 
     * @param id the id of the courier location to update
     * @param courierLocation the courier location to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated courier location
     */
    @PutMapping("/{id}")
    public ResponseEntity<CourierLocation> updateCourierLocation(
            @PathVariable Long id, 
            @Valid @RequestBody CourierLocation courierLocation) {
        log.debug("REST request to update courier location : {}", courierLocation);
        if (courierLocation.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Courier location ID must not be null");
        }
        if (!id.equals(courierLocation.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IDs don't match");
        }
        
        try {
            CourierLocation result = courierLocationService.updateCourierLocation(id, courierLocation);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * DELETE /api/v1/courier-locations/{id} : Delete a courier location
     * 
     * @param id the id of the courier location to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourierLocation(@PathVariable Long id) {
        log.debug("REST request to delete courier location : {}", id);
        try {
            courierLocationService.deleteCourierLocation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/courier-locations/{id}/attributes : Update specific attributes of a courier location
     * 
     * @param id the id of the courier location to update
     * @param attributes the attributes to update (key-value pairs)
     * @return the ResponseEntity with status 200 (OK) and with body the updated courier location
     */
    @PatchMapping("/{id}/attributes")
    public ResponseEntity<CourierLocation> updateSpecificAttributes(
            @PathVariable Long id, 
            @RequestBody Map<String, Object> attributes) {
        log.debug("REST request to update specific attributes for courier location : {}", id);
        try {
            CourierLocation result = courierLocationService.updateSpecificAttributes(id, attributes);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/courier-locations/{id}/activate : Activate a courier location
     * 
     * @param id the id of the courier location to activate
     * @return the ResponseEntity with status 200 (OK) and with body the activated courier location
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<CourierLocation> activateCourierLocation(@PathVariable Long id) {
        log.debug("REST request to activate courier location : {}", id);
        try {
            CourierLocation result = courierLocationService.activateCourierLocation(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/courier-locations/{id}/deactivate : Deactivate a courier location
     * 
     * @param id the id of the courier location to deactivate
     * @return the ResponseEntity with status 200 (OK) and with body the deactivated courier location
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CourierLocation> deactivateCourierLocation(@PathVariable Long id) {
        log.debug("REST request to deactivate courier location : {}", id);
        try {
            CourierLocation result = courierLocationService.deactivateCourierLocation(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/courier-locations/{id}/reassign-manager/{managerId} : Reassign a courier location to a different manager
     * 
     * @param id the id of the courier location to reassign
     * @param managerId the ID of the new manager
     * @return the ResponseEntity with status 200 (OK) and with body the updated courier location
     */
    @PatchMapping("/{id}/reassign-manager/{managerId}")
    public ResponseEntity<CourierLocation> reassignCourierLocationManager(
            @PathVariable Long id, 
            @PathVariable Long managerId) {
        log.debug("REST request to reassign courier location : {} to manager : {}", id, managerId);
        try {
            CourierLocation result = courierLocationService.reassignCourierLocationManager(id, managerId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * PATCH /api/v1/courier-locations/{id}/services : Update the services offered by a courier location
     * 
     * @param id the id of the courier location to update
     * @param services the updated services offered
     * @return the ResponseEntity with status 200 (OK) and with body the updated courier location
     */
    @PatchMapping("/{id}/services")
    public ResponseEntity<CourierLocation> updateCourierLocationServices(
            @PathVariable Long id, 
            @RequestBody String services) {
        log.debug("REST request to update services for courier location : {} to : {}", id, services);
        try {
            CourierLocation result = courierLocationService.updateCourierLocationServices(id, services);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * HEAD /api/v1/courier-locations/code/{locationCode} : Check if a courier location with the given location code exists
     * 
     * @param locationCode the location code to check
     * @return the ResponseEntity with status 200 (OK) if exists, or 404 (Not Found) if not
     */
    @RequestMapping(method = RequestMethod.HEAD, path = "/code/{locationCode}")
    public ResponseEntity<Void> checkLocationCodeExists(@PathVariable String locationCode) {
        log.debug("REST request to check if courier location exists with code : {}", locationCode);
        return courierLocationService.existsByLocationCode(locationCode) 
            ? ResponseEntity.ok().build() 
            : ResponseEntity.notFound().build();
    }
}
