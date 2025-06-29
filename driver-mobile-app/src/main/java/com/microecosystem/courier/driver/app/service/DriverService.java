package com.microecosystem.courier.driver.app.service;

import com.microecosystem.courier.driver.app.dto.DriverDto;
import com.microecosystem.courier.driver.app.dto.LocationUpdateRequest;
import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for driver operations.
 */
public interface DriverService {

    /**
     * Create a new driver.
     *
     * @param driverDto driver data
     * @return created driver
     */
    Driver createDriver(DriverDto driverDto);

    /**
     * Update an existing driver.
     *
     * @param id driver ID
     * @param driverDto driver data
     * @return updated driver
     */
    Driver updateDriver(Long id, DriverDto driverDto);

    /**
     * Get a driver by ID.
     *
     * @param id driver ID
     * @return driver if found
     */
    Optional<Driver> getDriverById(Long id);

    /**
     * Get a driver by user ID.
     *
     * @param userId user ID
     * @return driver if found
     */
    Optional<Driver> getDriverByUserId(Long userId);

    /**
     * Get a driver by email.
     *
     * @param email driver email
     * @return driver if found
     */
    Optional<Driver> getDriverByEmail(String email);

    /**
     * Get all drivers with pagination.
     *
     * @param pageable pagination information
     * @return page of drivers
     */
    Page<Driver> getAllDrivers(Pageable pageable);

    /**
     * Get drivers by status with pagination.
     *
     * @param status driver status
     * @param pageable pagination information
     * @return page of drivers
     */
    Page<Driver> getDriversByStatus(DriverStatus status, Pageable pageable);

    /**
     * Update driver status.
     *
     * @param id driver ID
     * @param status new status
     * @return updated driver
     */
    Driver updateDriverStatus(Long id, DriverStatus status);

    /**
     * Update driver location.
     *
     * @param id driver ID
     * @param locationRequest location data
     * @return updated driver
     */
    Driver updateDriverLocation(Long id, LocationUpdateRequest locationRequest);

    /**
     * Find nearby drivers.
     *
     * @param latitude central latitude
     * @param longitude central longitude
     * @param radiusInKm radius in kilometers
     * @param status optional status filter
     * @return list of nearby drivers
     */
    List<Driver> findNearbyDrivers(BigDecimal latitude, BigDecimal longitude, 
                                  Double radiusInKm, DriverStatus status);

    /**
     * Search drivers by name.
     *
     * @param query search query
     * @param pageable pagination information
     * @return page of drivers
     */
    Page<Driver> searchDriversByName(String query, Pageable pageable);

    /**
     * Delete a driver.
     *
     * @param id driver ID
     */
    void deleteDriver(Long id);

    /**
     * Update driver device token.
     *
     * @param id driver ID
     * @param deviceToken new device token
     * @return updated driver
     */
    Driver updateDeviceToken(Long id, String deviceToken);
} 