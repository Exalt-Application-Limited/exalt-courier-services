package com.exalt.courier.routing.service;

import com.exalt.courier.routing.event.RouteStatusChangedEvent;
import com.exalt.courier.routing.event.WaypointCompletedEvent;
import com.exalt.courier.routing.model.Route;
import com.exalt.courier.routing.model.Waypoint;

/**
 * Service for publishing events related to routing operations.
 * This service is responsible for creating and publishing events
 * that other services can subscribe to for real-time updates.
 */
public interface EventPublisherService {
    
    /**
     * Publish a route created event
     *
     * @param route the newly created route
     * @return true if the event was published successfully
     */
    boolean publishRouteCreatedEvent(Route route);
    
    /**
     * Publish a route assigned event
     *
     * @param route the assigned route
     * @return true if the event was published successfully
     */
    boolean publishRouteAssignedEvent(Route route);
    
    /**
     * Publish a route started event
     *
     * @param route the started route
     * @return true if the event was published successfully
     */
    boolean publishRouteStartedEvent(Route route);
    
    /**
     * Publish a route completed event
     *
     * @param route the completed route
     * @return true if the event was published successfully
     */
    boolean publishRouteCompletedEvent(Route route);
    
    /**
     * Publish a route status changed event
     *
     * @param route the route with the updated status
     * @param previousStatus the previous status
     * @return true if the event was published successfully
     */
    boolean publishRouteStatusChangedEvent(Route route, String previousStatus);
    
    /**
     * Publish a waypoint completed event
     *
     * @param waypoint the completed waypoint
     * @param routeId the ID of the route
     * @param courierId the ID of the courier
     * @param notes any notes added by the courier
     * @return true if the event was published successfully
     */
    boolean publishWaypointCompletedEvent(Waypoint waypoint, Long routeId, String courierId, String notes);
}
