package com.gogidix.courier.routing.repository;

import com.gogidix.courier.routing.model.Waypoint;
import com.gogidix.courier.routing.model.WaypointStatus;
import com.gogidix.courier.routing.model.WaypointType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing Waypoint entities.
 */
@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, String> {

    /**
     * Find waypoints by shipment ID
     *
     * @param shipmentId the shipment ID
     * @return list of waypoints associated with the shipment
     */
    List<Waypoint> findByShipmentId(String shipmentId);
    
    /**
     * Find waypoints by order ID
     *
     * @param orderId the order ID
     * @return list of waypoints associated with the order
     */
    List<Waypoint> findByOrderId(String orderId);
    
    /**
     * Find waypoints by customer ID
     *
     * @param customerId the customer ID
     * @return list of waypoints associated with the customer
     */
    List<Waypoint> findByCustomerId(String customerId);
    
    /**
     * Find waypoints by waypoint type
     *
     * @param type the waypoint type
     * @return list of waypoints of the specified type
     */
    List<Waypoint> findByType(WaypointType type);
    
    /**
     * Find waypoints by status
     *
     * @param status the waypoint status
     * @return list of waypoints with the specified status
     */
    List<Waypoint> findByStatus(WaypointStatus status);
    
    /**
     * Find waypoints by shipment ID and waypoint type
     *
     * @param shipmentId the shipment ID
     * @param type the waypoint type
     * @return list of waypoints of the specified type associated with the shipment
     */
    List<Waypoint> findByShipmentIdAndType(String shipmentId, WaypointType type);
    
    /**
     * Find waypoints scheduled for a specific time period
     *
     * @param startDateTime the start of the time period
     * @param endDateTime the end of the time period
     * @return list of waypoints scheduled during the specified time period
     */
    List<Waypoint> findByEstimatedArrivalTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
    
    /**
     * Find pending waypoints for a shipment
     *
     * @param shipmentId the shipment ID
     * @return list of pending waypoints for the shipment
     */
    List<Waypoint> findByShipmentIdAndStatus(String shipmentId, WaypointStatus status);
    
    /**
     * Find waypoints by location within a radius
     *
     * @param latitude the latitude of the center point
     * @param longitude the longitude of the center point
     * @param radiusKm the radius in kilometers
     * @return list of waypoints within the radius
     */
    @Query("SELECT w FROM Waypoint w JOIN w.location l WHERE " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(l.latitude)) * " +
           "cos(radians(l.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(l.latitude))) <= :radiusKm")
    List<Waypoint> findWaypointsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm);
} 