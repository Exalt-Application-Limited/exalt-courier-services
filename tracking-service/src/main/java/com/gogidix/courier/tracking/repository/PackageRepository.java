package com.gogidix.courier.tracking.repository;

import com.gogidix.courier.tracking.model.Package;
import com.gogidix.courier.tracking.model.TrackingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Package entity operations.
 */
@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {

    /**
     * Find a package by its tracking number.
     *
     * @param trackingNumber the tracking number
     * @return the package if found
     */
    Optional<Package> findByTrackingNumber(String trackingNumber);

    /**
     * Find packages by their current status.
     *
     * @param status the status to search for
     * @return list of packages with the given status
     */
    List<Package> findByStatus(TrackingStatus status);

    /**
     * Find packages by their current status with pagination.
     *
     * @param status the status to search for
     * @param pageable pagination information
     * @return page of packages with the given status
     */
    Page<Package> findByStatus(TrackingStatus status, Pageable pageable);

    /**
     * Find packages by courier ID.
     *
     * @param courierId the courier ID
     * @return list of packages assigned to the courier
     */
    List<Package> findByCourierId(Long courierId);

    /**
     * Find packages by courier ID with pagination.
     *
     * @param courierId the courier ID
     * @param pageable pagination information
     * @return page of packages assigned to the courier
     */
    Page<Package> findByCourierId(Long courierId, Pageable pageable);

    /**
     * Find packages by route ID.
     *
     * @param routeId the route ID
     * @return list of packages on the route
     */
    List<Package> findByRouteId(Long routeId);

    /**
     * Find packages by order ID.
     *
     * @param orderId the order ID
     * @return list of packages for the order
     */
    List<Package> findByOrderId(String orderId);

    /**
     * Find packages by recipient name (case insensitive, partial match).
     *
     * @param recipientName the recipient name to search for
     * @return list of packages with matching recipient name
     */
    List<Package> findByRecipientNameContainingIgnoreCase(String recipientName);

    /**
     * Find packages by recipient address (case insensitive, partial match).
     *
     * @param recipientAddress the recipient address to search for
     * @return list of packages with matching recipient address
     */
    List<Package> findByRecipientAddressContainingIgnoreCase(String recipientAddress);

    /**
     * Find packages by recipient phone.
     *
     * @param recipientPhone the recipient phone
     * @return list of packages with matching recipient phone
     */
    List<Package> findByRecipientPhone(String recipientPhone);

    /**
     * Find packages by recipient email.
     *
     * @param recipientEmail the recipient email
     * @return list of packages with matching recipient email
     */
    List<Package> findByRecipientEmail(String recipientEmail);

    /**
     * Find packages that are out for delivery.
     *
     * @return list of packages out for delivery
     */
    @Query("SELECT p FROM Package p WHERE p.status = 'OUT_FOR_DELIVERY'")
    List<Package> findPackagesOutForDelivery();

    /**
     * Find packages that are delayed.
     *
     * @return list of delayed packages
     */
    @Query("SELECT p FROM Package p WHERE p.status = 'DELAYED'")
    List<Package> findDelayedPackages();

    /**
     * Find packages with delivery attempts.
     *
     * @param attempts minimum number of attempts
     * @return list of packages with at least the specified number of delivery attempts
     */
    @Query("SELECT p FROM Package p WHERE p.deliveryAttempts >= :attempts")
    List<Package> findPackagesWithDeliveryAttempts(@Param("attempts") Integer attempts);

    /**
     * Find packages by estimated delivery date range.
     *
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of packages with estimated delivery date in the range
     */
    List<Package> findByEstimatedDeliveryDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find packages by actual delivery date range.
     *
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of packages with actual delivery date in the range
     */
    List<Package> findByActualDeliveryDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find packages that require signature.
     *
     * @return list of packages requiring signature
     */
    List<Package> findBySignatureRequiredTrue();

    /**
     * Find packages by courier and status.
     *
     * @param courierId the courier ID
     * @param status the status
     * @return list of packages assigned to the courier with the given status
     */
    List<Package> findByCourierIdAndStatus(Long courierId, TrackingStatus status);

    /**
     * Find packages that are overdue for delivery.
     *
     * @param currentDateTime the current date and time
     * @return list of packages that are overdue
     */
    @Query("SELECT p FROM Package p WHERE p.estimatedDeliveryDate < :currentDateTime AND p.status != 'DELIVERED' AND p.status != 'RETURNED_TO_SENDER' AND p.status != 'CANCELLED'")
    List<Package> findOverduePackages(@Param("currentDateTime") LocalDateTime currentDateTime);

    /**
     * Count packages by status.
     *
     * @param status the status
     * @return count of packages with the given status
     */
    long countByStatus(TrackingStatus status);

    /**
     * Count packages by courier.
     *
     * @param courierId the courier ID
     * @return count of packages assigned to the courier
     */
    long countByCourierId(Long courierId);

    /**
     * Find packages that are overdue (past estimated delivery date) and not in specified status.
     *
     * @param cutoffDate the cutoff date
     * @param status the status to exclude
     * @param pageable pagination information
     * @return page of overdue packages
     */
    Page<Package> findByEstimatedDeliveryDateBeforeAndStatusNot(LocalDateTime cutoffDate, TrackingStatus status, Pageable pageable);
} 