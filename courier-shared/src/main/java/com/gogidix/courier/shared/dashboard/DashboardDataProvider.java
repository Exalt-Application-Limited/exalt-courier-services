package com.gogidix.courier.shared.dashboard;

import java.util.Map;

/**
 * Interface for providing data to the dashboard data aggregation service.
 */
@FunctionalInterface
public interface DashboardDataProvider {
    
    /**
     * Provide data for the requested data type and filter criteria.
     * 
     * @param dataType The type of data requested
     * @param filterCriteria Filter criteria for the data (can be null)
     * @return The requested data as a map, or null if not available
     */
    Map<String, Object> provideData(String dataType, String filterCriteria);
}
