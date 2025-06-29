package com.microecosystem.courier.driver.app.repository;

import com.microecosystem.courier.driver.app.model.Driver;
import com.microecosystem.courier.driver.app.model.DriverStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Driver entity operations.
 */
@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    /**
     * Find a driver by email.
     *
     * @param email driver's email
     * @return optional driver
     */
    Optional<Driver> findByEmail(String email);

    /**
     * Find a driver by phone number.
     *
     * @param phoneNumber driver's phone number
     * @return optional driver
     */
    Optional<Driver> findByPhoneNumber(String phoneNumber);

    /**
     * Find a driver by user ID.
     *
     * @param userId user ID
     * @return optional driver
     */
    Optional<Driver> findByUserId(Long userId);

    /**
     * Find all drivers by status.
     *
     * @param status driver status
     * @param pageable pagination information
     * @return page of drivers
     */
    Page<Driver> findByStatus(DriverStatus status, Pageable pageable);

    /**
     * Find all active drivers.
     *
     * @param isActive active status
     * @param pageable pagination information
     * @return page of drivers
     */
    Page<Driver> findByIsActive(Boolean isActive, Pageable pageable);

    /**
     * Find all verified drivers.
     *
     * @param isVerified verification status
     * @param pageable pagination information
     * @return page of drivers
     */
    Page<Driver> findByIsVerified(Boolean isVerified, Pageable pageable);

    /**
     * Find drivers near a specific location.
     *
     * @param latitude central latitude
     * @param longitude central longitude
     * @param radiusInKm radius in kilometers
     * @param status driver status (optional)
     * @return list of nearby drivers
     */
    @Query(value = "SELECT d FROM Driver d WHERE " +
            "(:status IS NULL OR d.status = :status) AND " +
            "d.isActive = true AND " +
            "d.currentLatitude IS NOT NULL AND " +
            "d.currentLongitude IS NOT NULL AND " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(d.currentLatitude)) * " +
            "cos(radians(d.currentLongitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(d.currentLatitude)))) <= :radiusInKm " +
            "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(d.currentLatitude)) * " +
            "cos(radians(d.currentLongitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(d.currentLatitude))))")
    List<Driver> findNearbyDrivers(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusInKm") Double radiusInKm,
            @Param("status") DriverStatus status);

    /**
     * Count drivers by status.
     *
     * @param status driver status
     * @return count of drivers
     */
    long countByStatus(DriverStatus status);

    /**
     * Search drivers by name.
     *
     * @param firstName first name pattern
     * @param lastName last name pattern
     * @param pageable pagination information
     * @return page of drivers
     */
    @Query("SELECT d FROM Driver d WHERE " +
            "LOWER(d.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) OR " +
            "LOWER(d.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    Page<Driver> searchByName(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable pageable);
} 