package com.gogidix.courier.courier.branch.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing resource utilization metrics for a branch.
 * This includes vehicle usage, fuel consumption, and maintenance data.
 * Part of the Local Courier Management level in the courier services hierarchy.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceMetrics {
    
    // Number of vehicles currently in use
    private int vehiclesInUse;
    
    // Number of vehicles available for assignment
    private int vehiclesAvailable;
    
    // Total fuel consumption in liters
    private double fuelConsumption;
    
    // Number of vehicles scheduled for maintenance
    private int maintenanceScheduled;
    
    /**
     * Calculates the total number of vehicles in the branch.
     *
     * @return Total number of vehicles
     */
    public int getTotalVehicles() {
        return vehiclesInUse + vehiclesAvailable + maintenanceScheduled;
    }
    
    /**
     * Calculates the vehicle utilization percentage.
     *
     * @return Vehicle utilization as a percentage
     */
    public double getVehicleUtilizationPercentage() {
        int total = vehiclesInUse + vehiclesAvailable;
        if (total == 0) {
            return 0;
        }
        return (double) vehiclesInUse / total * 100;
    }
    
    /**
     * Calculates the average fuel consumption per vehicle in use.
     *
     * @return Average fuel consumption per vehicle
     */
    public double getAverageFuelConsumptionPerVehicle() {
        if (vehiclesInUse == 0) {
            return 0;
        }
        return fuelConsumption / vehiclesInUse;
    }
}