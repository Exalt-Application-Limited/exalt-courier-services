package com.exalt.courier.regionaladmin.service.capacity;

import com.socialecommerceecosystem.regionaladmin.model.AllocationStatus;
import com.socialecommerceecosystem.regionaladmin.model.ResourceAllocation;
import com.socialecommerceecosystem.regionaladmin.repository.ResourceAllocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for capacity planning and resource forecasting.
 * Provides predictive analytics for future resource needs.
 */
@Service
public class CapacityPlanningService {

    private static final Logger logger = LoggerFactory.getLogger(CapacityPlanningService.class);

    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;

    /**
     * Generate resource forecasts for the specified number of months.
     * 
     * @param months Number of months to forecast (1-12)
     * @return Map containing resource forecasts
     */
    public Map<String, Object> generateResourceForecast(int months) {
        if (months < 1 || months > 12) {
            throw new IllegalArgumentException("Forecast months must be between 1 and 12");
        }
        
        logger.info("Generating resource forecast for {} months", months);
        
        Map<String, Object> forecast = new HashMap<>();
        
        // Get current active allocations
        List<ResourceAllocation> activeAllocations = resourceAllocationRepository.findEffectiveAllocations(
                AllocationStatus.ACTIVE, LocalDateTime.now());
        
        // Calculate current allocated resources by type
        Map<String, Integer> currentByType = activeAllocations.stream()
                .collect(Collectors.groupingBy(
                    ResourceAllocation::getResourceType,
                    Collectors.summingInt(ResourceAllocation::getQuantity)
                ));
        
        // Calculate allocations that will expire in each month
        Map<String, Map<Integer, Integer>> expiringByMonth = new HashMap<>();
        
        for (ResourceAllocation allocation : activeAllocations) {
            // Skip allocations without expiry dates
            if (allocation.getEffectiveTo() == null) {
                continue;
            }
            
            // Skip allocations that expire after the forecast period
            LocalDateTime forecastEnd = LocalDateTime.now().plusMonths(months);
            if (allocation.getEffectiveTo().isAfter(forecastEnd)) {
                continue;
            }
            
            // Calculate which month this allocation expires in (0-based, where 0 is current month)
            LocalDateTime now = LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            LocalDateTime expiryDate = allocation.getEffectiveTo().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
            int expiryMonth = (int) ChronoUnit.MONTHS.between(now, expiryDate);
            
            // Ensure we only consider upcoming expirations
            if (expiryMonth < 0) {
                continue;
            }
            
            // Add to the expiring allocations
            String resourceType = allocation.getResourceType();
            expiringByMonth.computeIfAbsent(resourceType, k -> new HashMap<>());
            Map<Integer, Integer> byMonth = expiringByMonth.get(resourceType);
            byMonth.put(expiryMonth, byMonth.getOrDefault(expiryMonth, 0) + allocation.getQuantity());
        }
        
        // Calculate projected resources for each month
        Map<String, List<Integer>> projectedByType = new HashMap<>();
        
        for (String resourceType : currentByType.keySet()) {
            List<Integer> monthlyProjections = new ArrayList<>();
            int currentQuantity = currentByType.getOrDefault(resourceType, 0);
            
            for (int month = 0; month < months; month++) {
                // Subtract resources expiring this month
                if (expiringByMonth.containsKey(resourceType)) {
                    int expiring = expiringByMonth.get(resourceType).getOrDefault(month, 0);
                    currentQuantity -= expiring;
                }
                
                // Add projected resource growth (in a real system, this would use historical data)
                double growthRate = getResourceGrowthRate(resourceType, month);
                int growth = (int) (currentQuantity * growthRate);
                currentQuantity += growth;
                
                monthlyProjections.add(currentQuantity);
            }
            
            projectedByType.put(resourceType, monthlyProjections);
        }
        
        // Prepare forecast data
        forecast.put("currentAllocation", currentByType);
        forecast.put("projectedAllocation", projectedByType);
        
        // Generate monthly labels
        List<String> monthLabels = new ArrayList<>();
        LocalDateTime current = LocalDateTime.now();
        for (int i = 0; i < months; i++) {
            LocalDateTime month = current.plusMonths(i);
            monthLabels.add(month.getMonth().toString() + " " + month.getYear());
        }
        forecast.put("months", monthLabels);
        
        return forecast;
    }
    
    /**
     * Analyze capacity needs based on current usage and projected growth.
     * 
     * @return Map containing capacity analysis
     */
    public Map<String, Object> analyzeCapacityNeeds() {
        logger.info("Analyzing capacity needs");
        
        Map<String, Object> analysis = new HashMap<>();
        
        // Get current active allocations
        List<ResourceAllocation> activeAllocations = resourceAllocationRepository.findEffectiveAllocations(
                AllocationStatus.ACTIVE, LocalDateTime.now());
        
        // Calculate current allocated resources by type
        Map<String, Integer> currentByType = activeAllocations.stream()
                .collect(Collectors.groupingBy(
                    ResourceAllocation::getResourceType,
                    Collectors.summingInt(ResourceAllocation::getQuantity)
                ));
        
        // In a real system, these would be retrieved from a capacity planning database
        // For simulation, we'll use hard-coded values
        Map<String, Integer> capacityLimits = Map.of(
            "COURIER", 500,
            "VEHICLE", 200,
            "WAREHOUSE_SPACE", 10000,
            "DISTRIBUTION_CENTER", 20
        );
        
        // Calculate current utilization percentages
        Map<String, Double> utilizationPercentages = new HashMap<>();
        for (String resourceType : currentByType.keySet()) {
            int current = currentByType.get(resourceType);
            int limit = capacityLimits.getOrDefault(resourceType, 0);
            
            double utilizationPercent = limit > 0 ? ((double) current / limit) * 100 : 0;
            utilizationPercentages.put(resourceType, utilizationPercent);
        }
        
        // Calculate months until capacity limit reached
        Map<String, Integer> monthsUntilLimit = new HashMap<>();
        for (String resourceType : currentByType.keySet()) {
            int current = currentByType.get(resourceType);
            int limit = capacityLimits.getOrDefault(resourceType, 0);
            
            // Skip if no limit defined or already over limit
            if (limit == 0 || current >= limit) {
                monthsUntilLimit.put(resourceType, 0);
                continue;
            }
            
            double growthRate = getResourceGrowthRate(resourceType, 0);
            
            // Calculate months until limit reached
            int months = 0;
            int projected = current;
            while (projected < limit && months < 24) {
                projected += (int) (projected * growthRate);
                months++;
            }
            
            monthsUntilLimit.put(resourceType, months);
        }
        
        // Calculate capacity recommendations
        Map<String, String> recommendations = new HashMap<>();
        for (String resourceType : currentByType.keySet()) {
            double utilization = utilizationPercentages.getOrDefault(resourceType, 0.0);
            int monthsLeft = monthsUntilLimit.getOrDefault(resourceType, 0);
            
            if (utilization > 90) {
                recommendations.put(resourceType, "Critical: Immediate capacity increase needed");
            } else if (utilization > 75) {
                recommendations.put(resourceType, "Warning: Plan for capacity increase within 3 months");
            } else if (monthsLeft < 6 && monthsLeft > 0) {
                recommendations.put(resourceType, "Planning: Capacity increase needed within " + monthsLeft + " months");
            } else {
                recommendations.put(resourceType, "Healthy: No immediate capacity increase needed");
            }
        }
        
        // Compile analysis
        analysis.put("currentAllocation", currentByType);
        analysis.put("capacityLimits", capacityLimits);
        analysis.put("utilizationPercentages", utilizationPercentages);
        analysis.put("monthsUntilLimit", monthsUntilLimit);
        analysis.put("recommendations", recommendations);
        
        return analysis;
    }
    
    /**
     * Generate optimal resource allocation plan based on capacity needs.
     * 
     * @return Map containing allocation recommendations
     */
    public Map<String, Object> generateOptimalAllocationPlan() {
        logger.info("Generating optimal allocation plan");
        
        Map<String, Object> plan = new HashMap<>();
        
        // Get current active allocations
        List<ResourceAllocation> activeAllocations = resourceAllocationRepository.findEffectiveAllocations(
                AllocationStatus.ACTIVE, LocalDateTime.now());
        
        // Calculate current allocated resources by type
        Map<String, Integer> currentByType = activeAllocations.stream()
                .collect(Collectors.groupingBy(
                    ResourceAllocation::getResourceType,
                    Collectors.summingInt(ResourceAllocation::getQuantity)
                ));
        
        // In a real system, these would be calculated based on historical data and business rules
        // For simulation, we'll use fixed values
        
        // Calculate optimal allocations
        Map<String, Integer> optimalAllocations = new HashMap<>();
        for (String resourceType : currentByType.keySet()) {
            int current = currentByType.get(resourceType);
            
            // Simulate optimal allocation calculation
            // In reality, this would use complex optimization algorithms
            double adjustmentFactor = getOptimalAdjustmentFactor(resourceType);
            int optimal = (int) (current * adjustmentFactor);
            
            optimalAllocations.put(resourceType, optimal);
        }
        
        // Calculate allocation adjustments needed
        Map<String, Integer> adjustmentsNeeded = new HashMap<>();
        for (String resourceType : currentByType.keySet()) {
            int current = currentByType.get(resourceType);
            int optimal = optimalAllocations.getOrDefault(resourceType, 0);
            
            adjustmentsNeeded.put(resourceType, optimal - current);
        }
        
        // Compile plan
        plan.put("currentAllocation", currentByType);
        plan.put("optimalAllocation", optimalAllocations);
        plan.put("adjustmentsNeeded", adjustmentsNeeded);
        
        // Add phased implementation plan
        List<Map<String, Object>> implementations = new ArrayList<>();
        
        for (String resourceType : adjustmentsNeeded.keySet()) {
            int adjustment = adjustmentsNeeded.get(resourceType);
            
            // Skip if no adjustment needed
            if (adjustment == 0) {
                continue;
            }
            
            Map<String, Object> implementation = new HashMap<>();
            implementation.put("resourceType", resourceType);
            implementation.put("totalAdjustment", adjustment);
            
            // Create phased implementation
            List<Map<String, Object>> phases = new ArrayList<>();
            
            if (adjustment > 0) {
                // Increasing resources - phase over 3 months
                int perPhase = (adjustment + 2) / 3; // Ceiling division
                
                for (int i = 0; i < 3; i++) {
                    int phaseAdjustment = Math.min(perPhase, adjustment);
                    adjustment -= phaseAdjustment;
                    
                    if (phaseAdjustment > 0) {
                        Map<String, Object> phase = new HashMap<>();
                        phase.put("month", i + 1);
                        phase.put("adjustment", phaseAdjustment);
                        phases.add(phase);
                    }
                }
            } else {
                // Decreasing resources - phase over 2 months
                int perPhase = (Math.abs(adjustment) + 1) / 2; // Ceiling division
                adjustment = Math.abs(adjustment);
                
                for (int i = 0; i < 2; i++) {
                    int phaseAdjustment = Math.min(perPhase, adjustment);
                    adjustment -= phaseAdjustment;
                    
                    if (phaseAdjustment > 0) {
                        Map<String, Object> phase = new HashMap<>();
                        phase.put("month", i + 1);
                        phase.put("adjustment", -phaseAdjustment);
                        phases.add(phase);
                    }
                }
            }
            
            implementation.put("phases", phases);
            implementations.add(implementation);
        }
        
        plan.put("implementationPlan", implementations);
        
        return plan;
    }
    
    /**
     * Helper method to get the growth rate for a resource type and month.
     * In a real system, this would be calculated using historical data and forecasting models.
     * 
     * @param resourceType The resource type
     * @param month The forecast month (0-based)
     * @return The monthly growth rate
     */
    private double getResourceGrowthRate(String resourceType, int month) {
        // Simulate seasonal variations based on the month
        LocalDateTime targetMonth = LocalDateTime.now().plusMonths(month);
        Month monthEnum = targetMonth.getMonth();
        
        // Base growth rates by resource type
        Map<String, Double> baseRates = Map.of(
            "COURIER", 0.03, // 3% monthly growth
            "VEHICLE", 0.02, // 2% monthly growth
            "WAREHOUSE_SPACE", 0.015, // 1.5% monthly growth
            "DISTRIBUTION_CENTER", 0.01  // 1% monthly growth
        );
        
        double baseRate = baseRates.getOrDefault(resourceType, 0.02);
        
        // Apply seasonal adjustments
        switch (monthEnum) {
            case NOVEMBER:
            case DECEMBER: // Holiday season - higher growth
                return baseRate * 1.5;
            case JANUARY:
            case FEBRUARY: // Post-holiday slowdown
                return baseRate * 0.7;
            case JUNE:
            case JULY:
            case AUGUST: // Summer peak
                return baseRate * 1.2;
            default:
                return baseRate;
        }
    }
    
    /**
     * Helper method to get the optimal adjustment factor for a resource type.
     * In a real system, this would be calculated using optimization algorithms.
     * 
     * @param resourceType The resource type
     * @return The adjustment factor
     */
    private double getOptimalAdjustmentFactor(String resourceType) {
        // Simulate optimization results
        return switch (resourceType) {
            case "COURIER" -> 1.15; // 15% increase
            case "VEHICLE" -> 0.95; // 5% decrease
            case "WAREHOUSE_SPACE" -> 1.25; // 25% increase
            case "DISTRIBUTION_CENTER" -> 1.0; // No change
            default -> 1.1; // 10% increase
        };
    }
}
