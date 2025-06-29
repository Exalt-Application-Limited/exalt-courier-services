package com.exalt.courier.courier.branch.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing courier performance metrics for a branch.
 * This includes efficiency metrics, ratings, and courier activity.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetrics {
    
    // Number of active couriers in the branch
    private int activeCouriers;
    
    // Average number of deliveries per active courier
    private double averageDeliveriesPerCourier;
    
    // Average customer rating (out of 5)
    private double averageRating;
    
    // Overall courier efficiency score (0-100)
    private double efficiencyScore;
    
    /**
     * Categorizes the branch performance based on efficiency score.
     *
     * @return Performance category as a string
     */
    public String getPerformanceCategory() {
        if (efficiencyScore >= 90) {
            return "Excellent";
        } else if (efficiencyScore >= 80) {
            return "Good";
        } else if (efficiencyScore >= 70) {
            return "Average";
        } else if (efficiencyScore >= 60) {
            return "Below Average";
        } else {
            return "Needs Improvement";
        }
    }
    
    @Override
    public String toString() {
        return "PerformanceMetrics{" +
                "activeCouriers=" + activeCouriers +
                ", averageDeliveriesPerCourier=" + averageDeliveriesPerCourier +
                ", averageRating=" + averageRating +
                ", efficiencyScore=" + efficiencyScore +
                '}';
    }
} 