package com.microecosystem.courier.driver.app.controller;

import com.microecosystem.courier.driver.app.dto.DriverDto;
import com.microecosystem.courier.driver.app.dto.LocationUpdateRequest;
import com.microecosystem.courier.driver.app.exception.ResourceNotFoundException;
import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import com.microecosystem.courier.driver.app.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for driver operations.
 */
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Driver Management", description = "APIs for managing drivers")
public class DriverController {

    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Get all drivers", description = "Retrieves all drivers with pagination")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<Driver>> getAllDrivers(Pageable pageable) {
        log.info("REST request to get all drivers");
        Page<Driver> drivers = driverService.getAllDrivers(pageable);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get driver by ID", description = "Retrieves a driver by their ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @securityService.isCurrentDriver(#id)")
    public ResponseEntity<Driver> getDriverById(
            @Parameter(description = "Driver ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get driver with ID: {}", id);
        return driverService.getDriverById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get driver by user ID", description = "Retrieves a driver by their user account ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Driver> getDriverByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        log.info("REST request to get driver with user ID: {}", userId);
        return driverService.getDriverByUserId(userId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with user id: " + userId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get drivers by status", description = "Retrieves all drivers with a specific status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<Driver>> getDriversByStatus(
            @Parameter(description = "Driver status", required = true)
            @PathVariable DriverStatus status,
            Pageable pageable) {
        log.info("REST request to get drivers with status: {}", status);
        Page<Driver> drivers = driverService.getDriversByStatus(status, pageable);
        return ResponseEntity.ok(drivers);
    }

    @PostMapping
    @Operation(summary = "Create a new driver", description = "Creates a new driver account")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Driver> createDriver(
            @Parameter(description = "Driver data", required = true)
            @Valid @RequestBody DriverDto driverDto) {
        log.info("REST request to create a new driver");
        Driver driver = driverService.createDriver(driverDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(driver);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a driver", description = "Updates an existing driver's information")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or @securityService.isCurrentDriver(#id)")
    public ResponseEntity<Driver> updateDriver(
            @Parameter(description = "Driver ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated driver data", required = true)
            @Valid @RequestBody DriverDto driverDto) {
        log.info("REST request to update driver with ID: {}", id);
        Driver driver = driverService.updateDriver(id, driverDto);
        return ResponseEntity.ok(driver);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update driver status", description = "Updates a driver's status")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Driver> updateDriverStatus(
            @Parameter(description = "Driver ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New driver status", required = true)
            @RequestParam DriverStatus status) {
        log.info("REST request to update status to {} for driver with ID: {}", status, id);
        Driver driver = driverService.updateDriverStatus(id, status);
        return ResponseEntity.ok(driver);
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update driver location", description = "Updates a driver's current location")
    @PreAuthorize("hasRole('DRIVER') and @securityService.isCurrentDriver(#id)")
    public ResponseEntity<Driver> updateDriverLocation(
            @Parameter(description = "Driver ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Location data", required = true)
            @Valid @RequestBody LocationUpdateRequest locationRequest) {
        log.info("REST request to update location for driver with ID: {}", id);
        Driver driver = driverService.updateDriverLocation(id, locationRequest);
        return ResponseEntity.ok(driver);
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find nearby drivers", description = "Finds drivers near a specific location")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<Driver>> findNearbyDrivers(
            @Parameter(description = "Latitude", required = true)
            @RequestParam BigDecimal latitude,
            @Parameter(description = "Longitude", required = true)
            @RequestParam BigDecimal longitude,
            @Parameter(description = "Radius in kilometers", required = true)
            @RequestParam Double radiusInKm,
            @Parameter(description = "Driver status (optional)")
            @RequestParam(required = false) DriverStatus status) {
        log.info("REST request to find drivers near lat: {}, lon: {} within {}km", latitude, longitude, radiusInKm);
        List<Driver> drivers = driverService.findNearbyDrivers(latitude, longitude, radiusInKm, status);
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/search")
    @Operation(summary = "Search drivers by name", description = "Searches for drivers by name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<Driver>> searchDriversByName(
            @Parameter(description = "Search query", required = true)
            @RequestParam String query,
            Pageable pageable) {
        log.info("REST request to search drivers by name: {}", query);
        Page<Driver> drivers = driverService.searchDriversByName(query, pageable);
        return ResponseEntity.ok(drivers);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a driver", description = "Deletes a driver account")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteDriver(
            @Parameter(description = "Driver ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete driver with ID: {}", id);
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/device-token")
    @Operation(summary = "Update device token", description = "Updates a driver's device token for push notifications")
    @PreAuthorize("hasRole('DRIVER') and @securityService.isCurrentDriver(#id)")
    public ResponseEntity<Driver> updateDeviceToken(
            @Parameter(description = "Driver ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Device token", required = true)
            @RequestParam String deviceToken) {
        log.info("REST request to update device token for driver with ID: {}", id);
        Driver driver = driverService.updateDeviceToken(id, deviceToken);
        return ResponseEntity.ok(driver);
    }
} 