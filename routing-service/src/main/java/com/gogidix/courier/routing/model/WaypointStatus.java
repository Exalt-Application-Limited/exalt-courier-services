package com.gogidix.courier.routing.model;

/**
 * Enum representing the current status of a waypoint.
 */
public enum WaypointStatus {
    /**
     * The waypoint is pending and has not been visited
     */
    PENDING,
    
    /**
     * The courier is currently en route to this waypoint
     */
    EN_ROUTE,
    
    /**
     * The courier has arrived at this waypoint
     */
    ARRIVED,
    
    /**
     * The waypoint has been completed (pickup or delivery successful)
     */
    COMPLETED,
    
    /**
     * The waypoint has been skipped
     */
    SKIPPED,
    
    /**
     * There was an issue at this waypoint
     */
    FAILED
}
