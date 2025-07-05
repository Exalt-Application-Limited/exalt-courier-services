package com.gogidix.courier.routing.repository;

import com.gogidix.courier.routing.model.Route;
import com.gogidix.courier.routing.model.RouteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing Route entities.
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, String> {

    /**
     * Find routes by courier ID
     *
     * @param courierId the courier ID
     * @return list of routes assigned to the courier
     */
    List<Route> findByCourierId(String courierId);
    
    /**
     * Find routes by status
     *
     * @param status the route status
     * @return list of routes with the specified status
     */
    List<Route> findByStatus(RouteStatus status);
    
    /**
     * Find routes by courier ID and status
     *
     * @param courierId the courier ID
     * @param status the route status
     * @return list of routes assigned to the courier with the specified status
     */
    List<Route> findByCourierIdAndStatus(String courierId, RouteStatus status);
    
    /**
     * Find routes that contain a shipment
     *
     * @param shipmentId the shipment ID
     * @return list of routes containing the shipment
     */
    @Query("SELECT r FROM Route r JOIN r.waypoints w WHERE w.shipmentId = :shipmentId")
    List<Route> findRoutesByShipmentId(@Param("shipmentId") String shipmentId);
    
    /**
     * Find routes that contain an order
     *
     * @param orderId the order ID
     * @return list of routes containing the order
     */
    @Query("SELECT r FROM Route r JOIN r.waypoints w WHERE w.orderId = :orderId")
    List<Route> findRoutesByOrderId(@Param("orderId") String orderId);
    
    /**
     * Find active routes for a specific time period
     *
     * @param startDateTime the start of the time period
     * @param endDateTime the end of the time period
     * @return list of routes active during the specified time period
     */
    @Query("SELECT r FROM Route r WHERE " +
           "r.status = 'IN_PROGRESS' AND " +
           "((r.startTime <= :endDateTime AND r.endTime >= :startDateTime) OR " +
           "(r.startTime BETWEEN :startDateTime AND :endDateTime))")
    List<Route> findActiveRoutesDuringTimePeriod(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
    
    /**
     * Find routes created within a specific time period
     *
     * @param startDateTime the start of the time period
     * @param endDateTime the end of the time period
     * @return list of routes created during the specified time period
     */
    List<Route> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
} 