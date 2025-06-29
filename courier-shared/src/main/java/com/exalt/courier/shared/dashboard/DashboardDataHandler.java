package com.exalt.courier.shared.dashboard;

/**
 * Interface for handling aggregated dashboard data.
 */
@FunctionalInterface
public interface DashboardDataHandler {
    
    /**
     * Handle aggregated data.
     * 
     * @param data The aggregated data
     */
    void handleData(DashboardDataTransfer data);
}
