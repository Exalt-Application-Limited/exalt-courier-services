package com.gogidix.courier.routing.model;

/**
 * Enum representing the different types of waypoints in a route.
 */
public enum WaypointType {
    /**
     * Starting point of the route
     */
    START,
    
    /**
     * Pickup location, such as a warehouse or vendor location
     */
    PICKUP,
    
    /**
     * Delivery location, typically a customer address
     */
    DELIVERY,
    
    /**
     * Drop-off location for returns or exchanges
     */
    RETURN,
    
    /**
     * Intermediate stop, such as a rest area or gas station
     */
    STOP,
    
    /**
     * Final destination of the route
     */
    END
}
