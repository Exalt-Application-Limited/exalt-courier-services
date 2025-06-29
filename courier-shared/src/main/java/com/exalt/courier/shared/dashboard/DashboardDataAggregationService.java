package com.exalt.courier.shared.dashboard;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for dashboard data aggregation service that enables
 * data to flow between different dashboard levels with aggregation.
 */
public interface DashboardDataAggregationService {

    /**
     * Send data from a lower level to a higher level with aggregation.
     * 
     * @param data The data to send
     * @return CompletableFuture with true if sent successfully, false otherwise
     */
    CompletableFuture<Boolean> sendDataUp(DashboardDataTransfer data);
    
    /**
     * Request data from lower levels and aggregate the results.
     * 
     * @param dataType The type of data to request
     * @param filterCriteria Filter criteria for the data (can be null)
     * @param targetLevel The level to request data from
     * @param targetIds List of target IDs to request data from (can be null for all)
     * @param timeoutMs Timeout in milliseconds for the request
     * @return CompletableFuture with the aggregated data
     */
    CompletableFuture<DashboardDataTransfer> requestAndAggregateData(
            String dataType, 
            String filterCriteria, 
            String targetLevel, 
            List<String> targetIds, 
            long timeoutMs);
    
    /**
     * Register a data provider that can respond to data requests.
     * 
     * @param dataType The type of data this provider can provide
     * @param provider The data provider to register
     * @return The registration ID
     */
    String registerDataProvider(String dataType, DashboardDataProvider provider);
    
    /**
     * Unregister a previously registered data provider.
     * 
     * @param registrationId The registration ID returned from registerDataProvider
     * @return True if unregistered successfully, false otherwise
     */
    boolean unregisterDataProvider(String registrationId);
    
    /**
     * Get a list of available data types from lower levels.
     * 
     * @param targetLevel The level to query
     * @return List of available data types
     */
    List<String> getAvailableDataTypes(String targetLevel);
    
    /**
     * Aggregate multiple data transfers into a single one.
     * 
     * @param dataTransfers List of data transfers to aggregate
     * @return The aggregated data transfer
     */
    DashboardDataTransfer aggregateData(List<DashboardDataTransfer> dataTransfers);
    
    /**
     * Schedule periodic data aggregation from lower levels.
     * 
     * @param dataType The type of data to aggregate
     * @param targetLevel The level to aggregate from
     * @param intervalMs The interval in milliseconds
     * @param handler The handler for the aggregated data
     * @return The schedule ID
     */
    String schedulePeriodicAggregation(String dataType, String targetLevel, 
                                    long intervalMs, DashboardDataHandler handler);
    
    /**
     * Cancel a previously scheduled periodic aggregation.
     * 
     * @param scheduleId The schedule ID returned from schedulePeriodicAggregation
     * @return True if cancelled successfully, false otherwise
     */
    boolean cancelPeriodicAggregation(String scheduleId);
}
