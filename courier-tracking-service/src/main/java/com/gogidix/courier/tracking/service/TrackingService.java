package com.gogidix.courierservices.tracking.$1;

import com.gogidix.courierservices.tracking.dto.CreatePackageRequest;
import com.gogidix.courierservices.tracking.dto.PackageDTO;
import com.gogidix.courierservices.tracking.dto.TrackingEventDTO;
import com.gogidix.courierservices.tracking.dto.UpdatePackageStatusRequest;
import com.gogidix.courierservices.tracking.model.TrackingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for package tracking operations.
 */
public interface TrackingService {

    /**
     * Create a new package in the tracking system.
     *
     * @param request the package creation request
     * @return the created package DTO with tracking number
     */
    PackageDTO createPackage(CreatePackageRequest request);

    /**
     * Get a package by its tracking number.
     *
     * @param trackingNumber the tracking number
     * @return the package if found
     */
    Optional<PackageDTO> getPackageByTrackingNumber(String trackingNumber);

    /**
     * Get a package by its ID.
     *
     * @param id the package ID
     * @return the package if found
     */
    Optional<PackageDTO> getPackageById(Long id);

    /**
     * Update the status of a package.
     *
     * @param trackingNumber the tracking number
     * @param request the status update request
     * @return the updated package
     */
    PackageDTO updatePackageStatus(String trackingNumber, UpdatePackageStatusRequest request);

    /**
     * Record a delivery attempt for a package.
     *
     * @param trackingNumber the tracking number
     * @param description the description of the attempt
     * @param location the location of the attempt
     * @return the updated package
     */
    PackageDTO recordDeliveryAttempt(String trackingNumber, String description, String location);

    /**
     * Mark a package as delivered.
     *
     * @param trackingNumber the tracking number
     * @param description the delivery description
     * @param signatureImage the signature image (base64 encoded)
     * @param location the delivery location
     * @return the updated package
     */
    PackageDTO markDelivered(String trackingNumber, String description, String signatureImage, String location);

    /**
     * Add a tracking event to a package.
     *
     * @param trackingNumber the tracking number
     * @param status the event status
     * @param description the event description
     * @param location the event location
     * @return the created tracking event
     */
    TrackingEventDTO addTrackingEvent(String trackingNumber, TrackingStatus status, String description, String location);

    /**
     * Add a tracking event with geolocation data.
     *
     * @param trackingNumber the tracking number
     * @param status the event status
     * @param description the event description
     * @param location the event location
     * @param latitude the latitude
     * @param longitude the longitude
     * @return the created tracking event
     */
    TrackingEventDTO addTrackingEventWithGeolocation(String trackingNumber, TrackingStatus status, 
                                                    String description, String location, 
                                                    Double latitude, Double longitude);

    /**
     * Get all tracking events for a package.
     *
     * @param trackingNumber the tracking number
     * @return list of tracking events
     */
    List<TrackingEventDTO> getTrackingEvents(String trackingNumber);

    /**
     * Get all packages with a specific status.
     *
     * @param status the status
     * @param pageable pagination information
     * @return page of packages
     */
    Page<PackageDTO> getPackagesByStatus(TrackingStatus status, Pageable pageable);

    /**
     * Get all packages assigned to a courier.
     *
     * @param courierId the courier ID
     * @param pageable pagination information
     * @return page of packages
     */
    Page<PackageDTO> getPackagesByCourier(Long courierId, Pageable pageable);

    /**
     * Get all packages on a route.
     *
     * @param routeId the route ID
     * @return list of packages
     */
    List<PackageDTO> getPackagesByRoute(Long routeId);

    /**
     * Get all packages for an order.
     *
     * @param orderId the order ID
     * @return list of packages
     */
    List<PackageDTO> getPackagesByOrder(String orderId);

    /**
     * Search for packages by recipient name.
     *
     * @param recipientName the recipient name (partial match)
     * @return list of packages
     */
    List<PackageDTO> searchPackagesByRecipientName(String recipientName);

    /**
     * Search for packages by recipient address.
     *
     * @param recipientAddress the recipient address (partial match)
     * @return list of packages
     */
    List<PackageDTO> searchPackagesByRecipientAddress(String recipientAddress);

    /**
     * Find packages that are out for delivery.
     *
     * @return list of packages
     */
    List<PackageDTO> getPackagesOutForDelivery();

    /**
     * Find packages that are delayed.
     *
     * @return list of packages
     */
    List<PackageDTO> getDelayedPackages();

    /**
     * Find packages with multiple delivery attempts.
     *
     * @param minAttempts minimum number of attempts
     * @return list of packages
     */
    List<PackageDTO> getPackagesWithMultipleAttempts(int minAttempts);

    /**
     * Find packages by estimated delivery date range.
     *
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of packages
     */
    List<PackageDTO> getPackagesByEstimatedDeliveryDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find packages that are overdue for delivery.
     *
     * @return list of packages
     */
    List<PackageDTO> getOverduePackages();

    /**
     * Get package delivery statistics.
     *
     * @return map of statistics
     */
    PackageStatistics getPackageStatistics();

    /**
     * Update courier assignment for a package.
     *
     * @param trackingNumber the tracking number
     * @param courierId the courier ID
     * @return the updated package
     */
    PackageDTO assignCourier(String trackingNumber, Long courierId);

    /**
     * Update route assignment for a package.
     *
     * @param trackingNumber the tracking number
     * @param routeId the route ID
     * @return the updated package
     */
    PackageDTO assignRoute(String trackingNumber, Long routeId);

    /**
     * Update delivery instructions for a package.
     *
     * @param trackingNumber the tracking number
     * @param instructions the delivery instructions
     * @return the updated package
     */
    PackageDTO updateDeliveryInstructions(String trackingNumber, String instructions);

    /**
     * Set signature requirement for a package.
     *
     * @param trackingNumber the tracking number
     * @param signatureRequired whether signature is required
     * @return the updated package
     */
    PackageDTO setSignatureRequired(String trackingNumber, boolean signatureRequired);

    /**
     * Statistics class for package delivery metrics.
     */
    class PackageStatistics {
        private long totalPackages;
        private long deliveredPackages;
        private long inTransitPackages;
        private long delayedPackages;
        private long returnedPackages;
        private double onTimeDeliveryRate;
        private double averageDeliveryAttempts;
        
        // Getters and setters
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
    }
} 
