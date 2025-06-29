package com.exalt.courierservices.tracking.$1;

import com.exalt.courierservices.tracking.model.Package;
import com.exalt.courierservices.tracking.model.TrackingEvent;
import com.exalt.courierservices.tracking.model.TrackingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for TrackingEvent entity operations.
 */
@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEvent, Long> {

    /**
     * Find events by package ID.
     *
     * @param packageId the package ID
     * @return list of events for the package
     */
    List<TrackingEvent> findByPackId(Long packageId);

    /**
     * Find events by package ID with pagination.
     *
     * @param packageId the package ID
     * @param pageable pagination information
     * @return page of events for the package
     */
    Page<TrackingEvent> findByPackId(Long packageId, Pageable pageable);

    /**
     * Find events by package ID ordered by event time descending.
     *
     * @param packageId the package ID
     * @return list of events for the package ordered by event time descending
     */
    List<TrackingEvent> findByPackIdOrderByEventTimeDesc(Long packageId);

    /**
     * Find events by package tracking number.
     *
     * @param trackingNumber the package tracking number
     * @return list of events for the package
     */
    @Query("SELECT e FROM TrackingEvent e WHERE e.pack.trackingNumber = :trackingNumber ORDER BY e.eventTime DESC")
    List<TrackingEvent> findByPackTrackingNumber(@Param("trackingNumber") String trackingNumber);

    /**
     * Find events by status.
     *
     * @param status the status
     * @return list of events with the given status
     */
    List<TrackingEvent> findByStatus(TrackingStatus status);

    /**
     * Find events by courier ID.
     *
     * @param courierId the courier ID
     * @return list of events associated with the courier
     */
    List<TrackingEvent> findByCourierId(Long courierId);

    /**
     * Find events by facility ID.
     *
     * @param facilityId the facility ID
     * @return list of events associated with the facility
     */
    List<TrackingEvent> findByFacilityId(Long facilityId);

    /**
     * Find events by location (case insensitive, partial match).
     *
     * @param location the location to search for
     * @return list of events with matching location
     */
    List<TrackingEvent> findByLocationContainingIgnoreCase(String location);

    /**
     * Find events by event time range.
     *
     * @param startTime start of time range
     * @param endTime end of time range
     * @return list of events with event time in the range
     */
    List<TrackingEvent> findByEventTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find events by package ID and status.
     *
     * @param packageId the package ID
     * @param status the status
     * @return list of events for the package with the given status
     */
    List<TrackingEvent> findByPackIdAndStatus(Long packageId, TrackingStatus status);

    /**
     * Find events by package ID and event time range.
     *
     * @param packageId the package ID
     * @param startTime start of time range
     * @param endTime end of time range
     * @return list of events for the package with event time in the range
     */
    List<TrackingEvent> findByPackIdAndEventTimeBetween(Long packageId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find events by courier ID and event time range.
     *
     * @param courierId the courier ID
     * @param startTime start of time range
     * @param endTime end of time range
     * @return list of events associated with the courier with event time in the range
     */
    List<TrackingEvent> findByCourierIdAndEventTimeBetween(Long courierId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find events by facility ID and event time range.
     *
     * @param facilityId the facility ID
     * @param startTime start of time range
     * @param endTime end of time range
     * @return list of events associated with the facility with event time in the range
     */
    List<TrackingEvent> findByFacilityIdAndEventTimeBetween(Long facilityId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find the latest event for a package.
     *
     * @param packageId the package ID
     * @return the latest event for the package
     */
    @Query("SELECT e FROM TrackingEvent e WHERE e.pack.id = :packageId ORDER BY e.eventTime DESC")
    List<TrackingEvent> findLatestEventByPackageId(@Param("packageId") Long packageId, Pageable pageable);

    /**
     * Count events by status.
     *
     * @param status the status
     * @return count of events with the given status
     */
    long countByStatus(TrackingStatus status);

    /**
     * Count events by courier.
     *
     * @param courierId the courier ID
     * @return count of events associated with the courier
     */
    long countByCourierId(Long courierId);

    /**
     * Count events by facility.
     *
     * @param facilityId the facility ID
     * @return count of events associated with the facility
     */
    long countByFacilityId(Long facilityId);

    /**
     * Find events by package ordered by event time descending.
     *
     * @param pack the package
     * @return list of events for the package ordered by event time descending
     */
    List<TrackingEvent> findByPackOrderByEventTimeDesc(Package pack);
} 