package com.gogidix.courier.location.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.socialecommerceecosystem.location.model.LocationOperatingHours;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.service.LocationManagementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for physical location operations in the courier network.
 * Provides endpoints for CRUD operations on locations and their operating hours.
 */
@RestController
@RequestMapping("/api/locations")
@Tag(name = "Location Management", description = "API for managing physical locations in the courier network")
@Slf4j
public class LocationController {

    private final LocationManagementService locationService;

    @Autowired
    public LocationController(LocationManagementService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    @Operation(summary = "Get all locations", description = "Retrieves all physical locations in the courier network")
    @ApiResponse(responseCode = "200", description = "Locations retrieved successfully", 
            content = @Content(schema = @Schema(implementation = PhysicalLocation.class)))
    public ResponseEntity<List<PhysicalLocation>> getAllLocations() {
        log.debug("REST request to get all locations");
        List<PhysicalLocation> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/paginated")
    @Operation(summary = "Get paginated locations", description = "Retrieves physical locations with pagination")
    public ResponseEntity<Page<PhysicalLocation>> getPaginatedLocations(Pageable pageable) {
        log.debug("REST request to get paginated locations: {}", pageable);
        Page<PhysicalLocation> locations = locationService.getAllLocations(pageable);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID", description = "Retrieves a specific physical location by its ID")
    @ApiResponse(responseCode = "200", description = "Location found", 
            content = @Content(schema = @Schema(implementation = PhysicalLocation.class)))
    @ApiResponse(responseCode = "404", description = "Location not found")
    public ResponseEntity<PhysicalLocation> getLocationById(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id) {
        log.debug("REST request to get location with ID: {}", id);
        return locationService.getLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new location", description = "Creates a new physical location")
    @ApiResponse(responseCode = "201", description = "Location created successfully", 
            content = @Content(schema = @Schema(implementation = PhysicalLocation.class)))
    public ResponseEntity<PhysicalLocation> createLocation(
            @Parameter(description = "Location details", required = true) 
            @Valid @RequestBody PhysicalLocation location) {
        log.debug("REST request to create a new location: {}", location.getName());
        PhysicalLocation createdLocation = locationService.createLocation(location);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLocation);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a location", description = "Updates an existing physical location")
    @ApiResponse(responseCode = "200", description = "Location updated successfully", 
            content = @Content(schema = @Schema(implementation = PhysicalLocation.class)))
    @ApiResponse(responseCode = "404", description = "Location not found")
    public ResponseEntity<PhysicalLocation> updateLocation(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id,
            @Parameter(description = "Updated location details", required = true) 
            @Valid @RequestBody PhysicalLocation location) {
        log.debug("REST request to update location with ID: {}", id);
        try {
            PhysicalLocation updatedLocation = locationService.updateLocation(id, location);
            return ResponseEntity.ok(updatedLocation);
        } catch (IllegalArgumentException e) {
            log.error("Location not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a location", description = "Deletes a physical location")
    @ApiResponse(responseCode = "204", description = "Location deleted successfully")
    @ApiResponse(responseCode = "404", description = "Location not found")
    public ResponseEntity<Void> deleteLocation(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id) {
        log.debug("REST request to delete location with ID: {}", id);
        try {
            locationService.deleteLocation(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Location not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get locations by type", description = "Retrieves physical locations by their type")
    public ResponseEntity<List<PhysicalLocation>> getLocationsByType(
            @Parameter(description = "Type of location", required = true) @PathVariable LocationType type) {
        log.debug("REST request to get locations with type: {}", type);
        List<PhysicalLocation> locations = locationService.getLocationsByType(type);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/country/{country}/state/{state}")
    @Operation(summary = "Get locations by country and state", 
            description = "Retrieves physical locations by country and state")
    public ResponseEntity<List<PhysicalLocation>> getLocationsByCountryAndState(
            @Parameter(description = "Country", required = true) @PathVariable String country,
            @Parameter(description = "State", required = true) @PathVariable String state) {
        log.debug("REST request to get locations in country: {} and state: {}", country, state);
        List<PhysicalLocation> locations = locationService.getLocationsByCountryAndState(country, state);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get locations by city", description = "Retrieves physical locations by city")
    public ResponseEntity<List<PhysicalLocation>> getLocationsByCity(
            @Parameter(description = "City", required = true) @PathVariable String city) {
        log.debug("REST request to get locations in city: {}", city);
        List<PhysicalLocation> locations = locationService.getLocationsByCity(city);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/service/{serviceType}")
    @Operation(summary = "Get locations by service type", 
            description = "Retrieves physical locations offering a specific service")
    public ResponseEntity<List<PhysicalLocation>> findLocationsByServiceType(
            @Parameter(description = "Service type", required = true) @PathVariable String serviceType) {
        log.debug("REST request to get locations offering service: {}", serviceType);
        List<PhysicalLocation> locations = locationService.findLocationsByServiceType(serviceType);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby locations", 
            description = "Finds physical locations within a radius of a given coordinate")
    public ResponseEntity<List<PhysicalLocation>> findNearbyLocations(
            @Parameter(description = "Latitude", required = true) @RequestParam double latitude,
            @Parameter(description = "Longitude", required = true) @RequestParam double longitude,
            @Parameter(description = "Radius in kilometers", required = true) @RequestParam double radiusKm) {
        log.debug("REST request to find locations near [{}, {}] within {}km", latitude, longitude, radiusKm);
        List<PhysicalLocation> locations = locationService.findNearbyLocations(latitude, longitude, radiusKm);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active locations", description = "Retrieves all active physical locations")
    public ResponseEntity<List<PhysicalLocation>> getActiveLocations() {
        log.debug("REST request to get active locations");
        List<PhysicalLocation> locations = locationService.getActiveLocations();
        return ResponseEntity.ok(locations);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update location status", description = "Updates the active status of a location")
    @ApiResponse(responseCode = "200", description = "Status updated successfully", 
            content = @Content(schema = @Schema(implementation = PhysicalLocation.class)))
    @ApiResponse(responseCode = "404", description = "Location not found")
    public ResponseEntity<PhysicalLocation> updateLocationStatus(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id,
            @Parameter(description = "Active status", required = true) @RequestParam boolean active) {
        log.debug("REST request to update status of location with ID: {} to active: {}", id, active);
        try {
            PhysicalLocation updatedLocation = locationService.updateLocationStatus(id, active);
            return ResponseEntity.ok(updatedLocation);
        } catch (IllegalArgumentException e) {
            log.error("Location not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/operating-hours")
    @Operation(summary = "Add operating hours", description = "Adds operating hours to a location")
    @ApiResponse(responseCode = "201", description = "Operating hours added successfully", 
            content = @Content(schema = @Schema(implementation = LocationOperatingHours.class)))
    @ApiResponse(responseCode = "404", description = "Location not found")
    public ResponseEntity<LocationOperatingHours> addOperatingHours(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id,
            @Parameter(description = "Operating hours details", required = true) 
            @Valid @RequestBody LocationOperatingHours operatingHours) {
        log.debug("REST request to add operating hours to location with ID: {}", id);
        try {
            LocationOperatingHours addedHours = locationService.addOperatingHours(id, operatingHours);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedHours);
        } catch (IllegalArgumentException e) {
            log.error("Location not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/operating-hours")
    @Operation(summary = "Get operating hours", description = "Retrieves operating hours for a location")
    @ApiResponse(responseCode = "200", description = "Operating hours retrieved successfully", 
            content = @Content(schema = @Schema(implementation = LocationOperatingHours.class)))
    public ResponseEntity<List<LocationOperatingHours>> getOperatingHoursByLocation(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id) {
        log.debug("REST request to get operating hours for location with ID: {}", id);
        List<LocationOperatingHours> hours = locationService.getOperatingHoursByLocation(id);
        return ResponseEntity.ok(hours);
    }

    @PutMapping("/{id}/capacity-utilization")
    @Operation(summary = "Update capacity utilization", 
            description = "Updates the capacity utilization of a location")
    @ApiResponse(responseCode = "204", description = "Capacity utilization updated successfully")
    @ApiResponse(responseCode = "404", description = "Location not found")
    public ResponseEntity<Void> updateCapacityUtilization(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id,
            @Parameter(description = "Capacity utilization percentage", required = true) 
            @RequestParam Double capacityUtilization) {
        log.debug("REST request to update capacity utilization for location with ID: {} to: {}", 
                id, capacityUtilization);
        try {
            locationService.updateCapacityUtilization(id, capacityUtilization);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Location not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/is-open")
    @Operation(summary = "Check if location is open", 
            description = "Checks if a location is open at a specific time")
    public ResponseEntity<Boolean> isLocationOpen(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id,
            @Parameter(description = "Date and time to check (ISO format)", required = false) 
            @RequestParam(required = false) String dateTime) {
        log.debug("REST request to check if location with ID: {} is open at: {}", id, dateTime);
        
        java.time.LocalDateTime checkTime;
        if (dateTime == null || dateTime.isEmpty()) {
            checkTime = java.time.LocalDateTime.now();
        } else {
            checkTime = java.time.LocalDateTime.parse(dateTime);
        }
        
        boolean isOpen = locationService.isLocationOpen(id, checkTime);
        return ResponseEntity.ok(isOpen);
    }

    @GetMapping("/counts/by-type")
    @Operation(summary = "Get location counts by type", 
            description = "Retrieves the count of locations by type")
    public ResponseEntity<Map<String, Long>> getLocationCountsByType() {
        log.debug("REST request to get location counts by type");
        Map<String, Long> counts = locationService.getLocationCountsByType();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/counts/by-country")
    @Operation(summary = "Get location counts by country", 
            description = "Retrieves the count of locations by country")
    public ResponseEntity<Map<String, Long>> getLocationCountsByCountry() {
        log.debug("REST request to get location counts by country");
        Map<String, Long> counts = locationService.getLocationCountsByCountry();
        return ResponseEntity.ok(counts);
    }

    @GetMapping("/capacity-utilization")
    @Operation(summary = "Get locations by capacity utilization", 
            description = "Retrieves locations with capacity utilization in a specified range")
    public ResponseEntity<List<PhysicalLocation>> getLocationsByCapacityUtilizationRange(
            @Parameter(description = "Minimum utilization percentage", required = true) 
            @RequestParam Double minUtilization,
            @Parameter(description = "Maximum utilization percentage", required = true) 
            @RequestParam Double maxUtilization) {
        log.debug("REST request to get locations with capacity utilization between {} and {}", 
                minUtilization, maxUtilization);
        List<PhysicalLocation> locations = locationService
                .getLocationsByCapacityUtilizationRange(minUtilization, maxUtilization);
        return ResponseEntity.ok(locations);
    }

    @PutMapping("/{id}/services")
    @Operation(summary = "Update location services", 
            description = "Updates the services offered at a location")
    @ApiResponse(responseCode = "200", description = "Services updated successfully", 
            content = @Content(schema = @Schema(implementation = PhysicalLocation.class)))
    @ApiResponse(responseCode = "404", description = "Location not found")
    public ResponseEntity<PhysicalLocation> updateLocationServiceTypes(
            @Parameter(description = "ID of the location", required = true) @PathVariable Long id,
            @Parameter(description = "List of service types", required = true) 
            @RequestBody List<String> serviceTypes) {
        log.debug("REST request to update services for location with ID: {}", id);
        try {
            PhysicalLocation updatedLocation = locationService.updateLocationServiceTypes(id, serviceTypes);
            return ResponseEntity.ok(updatedLocation);
        } catch (IllegalArgumentException e) {
            log.error("Location not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/set-all-inactive")
    @Operation(summary = "Set all locations inactive", 
            description = "Sets all physical locations to inactive (emergency use only)")
    public ResponseEntity<Void> setAllLocationsInactive() {
        log.warn("REST request to set all locations to inactive");
        locationService.setAllLocationsInactive();
        return ResponseEntity.noContent().build();
    }
}
