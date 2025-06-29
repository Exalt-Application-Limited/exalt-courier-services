package com.exalt.courier.courier.branch.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing delivery-related metrics for a branch.
 * This includes counts of deliveries in various states and performance indicators.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryMetrics {
    
    // Current number of deliveries in progress
    private int totalDeliveriesInProgress;
    
    // Number of deliveries completed in the current reporting period
    private int totalDeliveriesCompleted;
    
    // Number of deliveries that failed in the current reporting period
    private int totalDeliveriesFailed;
    
    // Average delivery time in minutes
    private double averageDeliveryTime;
    
    // Percentage of deliveries completed on time
    private double onTimeDeliveryPercentage;
    
    /**
     * Calculates the total number of deliveries (in progress, completed, and failed).
     *
     * @return Total number of deliveries
     */
    public int getTotalDeliveries() {
        return totalDeliveriesInProgress + totalDeliveriesCompleted + totalDeliveriesFailed;
    }
    
    /**
     * Calculates the success rate of deliveries.
     *
     * @return Success rate as a percentage
     */
    public double getSuccessRate() {
        int total = totalDeliveriesCompleted + totalDeliveriesFailed;
        if (total == 0) {
            return 0;
        }
        return (double) totalDeliveriesCompleted / total * 100;
    }
    
    @Override
    public String toString() {
        return "DeliveryMetrics{" +
                "totalDeliveriesInProgress=" + totalDeliveriesInProgress +
                ", totalDeliveriesCompleted=" + totalDeliveriesCompleted +
                ", totalDeliveriesFailed=" + totalDeliveriesFailed +
                ", averageDeliveryTime=" + averageDeliveryTime +
                ", onTimeDeliveryPercentage=" + onTimeDeliveryPercentage +
                '}';
    }
} 