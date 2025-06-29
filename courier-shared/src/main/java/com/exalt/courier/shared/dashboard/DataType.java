package com.exalt.courier.shared.dashboard;

/**
 * Enumeration of data types used in dashboard data aggregation.
 */
public class DataType {
    
    public static final String DELIVERY_METRICS = "DELIVERY_METRICS";
    public static final String DRIVER_PERFORMANCE = "DRIVER_PERFORMANCE";
    public static final String OPERATIONAL_METRICS = "OPERATIONAL_METRICS";
    public static final String CUSTOMER_SATISFACTION = "CUSTOMER_SATISFACTION";
    public static final String FINANCIAL_DATA = "FINANCIAL_DATA";
    public static final String FINANCIAL_METRICS = "FINANCIAL_METRICS";  // Added for global-hq-admin compatibility
    public static final String SYSTEM_HEALTH = "SYSTEM_HEALTH";  // Added for global-hq-admin compatibility
    public static final String INVENTORY_STATUS = "INVENTORY_STATUS";
    public static final String ROUTE_OPTIMIZATION = "ROUTE_OPTIMIZATION";
    public static final String VEHICLE_TRACKING = "VEHICLE_TRACKING";
    
    private DataType() {
        // Utility class - prevent instantiation
    }
}
