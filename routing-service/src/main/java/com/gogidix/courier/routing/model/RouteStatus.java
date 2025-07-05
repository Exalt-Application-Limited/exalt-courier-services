package com.gogidix.courier.routing.model;

/**
 * Enum representing the current status of a route.
 */
public enum RouteStatus {
    /**
     * The route has been created but not yet started
     */
    CREATED,
    
    /**
     * The route has been assigned to a courier
     */
    ASSIGNED,
    
    /**
     * The route is being optimized
     */
    OPTIMIZING,
    
    /**
     * The route is currently in progress
     */
    IN_PROGRESS,
    
    /**
     * The route has been completed successfully
     */
    COMPLETED,
    
    /**
     * The route has been cancelled
     */
    CANCELLED,
    
    /**
     * The route has been delayed
     */
    DELAYED,
    
    /**
     * The route has been optimized and is ready for assignment
     */
    OPTIMIZED
}
