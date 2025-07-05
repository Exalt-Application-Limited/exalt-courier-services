package com.gogidix.courierservices.management.$1;

/**
 * Enum representing different types of performance metrics for couriers.
 */
public enum MetricType {
    /**
     * Percentage of deliveries completed on time.
     */
    ON_TIME_DELIVERY_RATE,
    
    /**
     * Average time taken to complete deliveries.
     */
    AVERAGE_DELIVERY_TIME,
    
    /**
     * Number of deliveries completed per day.
     */
    DELIVERIES_PER_DAY,
    
    /**
     * Average customer rating (1-5 scale).
     */
    CUSTOMER_RATING,
    
    /**
     * Percentage of successful first-attempt deliveries.
     */
    FIRST_ATTEMPT_SUCCESS_RATE,
    
    /**
     * Average time to accept an assignment.
     */
    ASSIGNMENT_ACCEPTANCE_TIME,
    
    /**
     * Percentage of assignments accepted.
     */
    ASSIGNMENT_ACCEPTANCE_RATE,
    
    /**
     * Number of kilometers traveled per delivery.
     */
    DISTANCE_PER_DELIVERY,
    
    /**
     * Fuel efficiency (deliveries per liter/gallon).
     */
    FUEL_EFFICIENCY,
    
    /**
     * Number of customer complaints received.
     */
    COMPLAINT_COUNT,
    
    /**
     * Percentage of idle time during active hours.
     */
    IDLE_TIME_PERCENTAGE,
    
    /**
     * Average time spent per pickup.
     */
    AVERAGE_PICKUP_TIME,
    
    /**
     * Average time spent per dropoff.
     */
    AVERAGE_DROPOFF_TIME,
    
    /**
     * Custom metric defined for specific use cases.
     */
    CUSTOM;
} 