package com.microecosystem.courier.driver.app.service;

import com.microecosystem.courier.driver.app.dto.LocationUpdateRequest;
import com.microecosystem.courier.driver.app.model.Driver;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for location tracking operations.
 */
public interface LocationTrackingService {

    /**
     * Update a driver's location.
     *
     * @param driverId driver ID
     * @param request location update request
     * @return updated driver
     */
    Driver updateDriverLocation(Long driverId, LocationUpdateRequest request);

    /**
     * Find nearby drivers within a radius.
     *
     * @param latitude central latitude
     * @param longitude central longitude
     * @param radiusInKm radius in kilometers
     * @param status optional driver status filter
     * @return list of nearby drivers
     */
    List<Driver> findNearbyDrivers(BigDecimal latitude, BigDecimal longitude, Double radiusInKm, String status);

    /**
     * Calculate distance between two points in kilometers.
     *
     * @param lat1 first latitude
     * @param lon1 first longitude
     * @param lat2 second latitude
     * @param lon2 second longitude
     * @return distance in kilometers
     */
    double calculateDistance(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2);

    /**
     * Get estimated time of arrival in minutes.
     *
     * @param lat1 first latitude
     * @param lon1 first longitude
     * @param lat2 second latitude
     * @param lon2 second longitude
     * @param averageSpeedKmh average speed in km/h
     * @return estimated time in minutes
     */
    double getEstimatedTimeOfArrival(BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2, double averageSpeedKmh);
} 