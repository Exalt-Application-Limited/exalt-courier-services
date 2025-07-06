package com.gogidix.courier.management.courier.repository;

import com.gogidix.courier.management.courier.model.Courier;
import com.gogidix.courier.management.courier.model.CourierStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Courier entity operations.
 */
@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {

    /**
     * Find a courier by its unique courier ID.
     *
     * @param courierId the courier ID
     * @return an Optional containing the courier if found
     */
    Optional<Courier> findByCourierId(String courierId);

    /**
     * Find couriers by status.
     *
     * @param status the status
     * @return a list of couriers with the specified status
     */
    List<Courier> findByStatus(CourierStatus status);

    /**
     * Find couriers by status and active flag.
     *
     * @param status the status
     * @param active the active flag
     * @return a list of couriers with the specified status and active flag
     */
    List<Courier> findByStatusAndActive(CourierStatus status, boolean active);

    /**
     * Find active couriers.
     *
     * @return a list of active couriers
     */
    List<Courier> findByActiveTrue();

    /**
     * Find couriers by status with pagination.
     *
     * @param status the status
     * @param pageable pagination information
     * @return a page of couriers with the specified status
     */
    Page<Courier> findByStatus(CourierStatus status, Pageable pageable);

    /**
     * Find couriers near a specific location.
     *
     * @param latitude the latitude
     * @param longitude the longitude
     * @param radiusInKm the radius in kilometers
     * @return a list of couriers near the specified location
     */
    @Query(value = "SELECT c.* FROM couriers c " +
            "WHERE ST_DWithin(ST_MakePoint(c.current_longitude, c.current_latitude)::geography, " +
            "ST_MakePoint(:longitude, :latitude)::geography, :radiusInKm * 1000) " +
            "AND c.active = true AND c.status = 'AVAILABLE'", nativeQuery = true)
    List<Courier> findAvailableCouriersNearLocation(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInKm") double radiusInKm);

    /**
     * Find active couriers with a specific status near a location.
     *
     * @param latitude the latitude
     * @param longitude the longitude
     * @param radiusInKm the radius in kilometers
     * @param status the status
     * @return a list of active couriers with the specified status near the location
     */
    @Query(value = "SELECT c.* FROM couriers c " +
            "WHERE ST_DWithin(ST_MakePoint(c.current_longitude, c.current_latitude)::geography, " +
            "ST_MakePoint(:longitude, :latitude)::geography, :radiusInKm * 1000) " +
            "AND c.active = true AND c.status = :status", nativeQuery = true)
    List<Courier> findActiveCouriersNearLocationWithStatus(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusInKm") double radiusInKm,
            @Param("status") String status);

    /**
     * Find couriers by partial name match.
     *
     * @param name the name pattern to match
     * @return a list of couriers with matching names
     */
    @Query("SELECT c FROM Courier c WHERE CONCAT(c.firstName, ' ', c.lastName) LIKE %:name%")
    List<Courier> findByNameContaining(@Param("name") String name);

    /**
     * Count couriers by status.
     *
     * @param status the status
     * @return the count of couriers with the specified status
     */
    long countByStatus(CourierStatus status);
} 