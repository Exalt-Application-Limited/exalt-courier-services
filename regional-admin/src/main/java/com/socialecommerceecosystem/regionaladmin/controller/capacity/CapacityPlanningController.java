package com.gogidix.courier.regionaladmin.controller.capacity;

import com.socialecommerceecosystem.regionaladmin.service.capacity.CapacityPlanningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for capacity planning.
 * Provides endpoints for resource forecasting and allocation planning.
 */
@RestController
@RequestMapping("/api/capacity")
public class CapacityPlanningController {

    private static final Logger logger = LoggerFactory.getLogger(CapacityPlanningController.class);

    @Autowired
    private CapacityPlanningService capacityPlanningService;

    /**
     * Get resource forecast for the specified number of months.
     * 
     * @param months Number of months to forecast (default: 6)
     * @return Map containing resource forecasts
     */
    @GetMapping("/forecast")
    public ResponseEntity<Map<String, Object>> getResourceForecast(
            @RequestParam(name = "months", defaultValue = "6") int months) {
        logger.info("Getting resource forecast for {} months", months);
        
        // Validate input
        if (months < 1 || months > 12) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Forecast months must be between 1 and 12"
            ));
        }
        
        Map<String, Object> forecast = capacityPlanningService.generateResourceForecast(months);
        return ResponseEntity.ok(forecast);
    }

    /**
     * Get capacity needs analysis.
     * 
     * @return Map containing capacity analysis
     */
    @GetMapping("/needs")
    public ResponseEntity<Map<String, Object>> getCapacityNeeds() {
        logger.info("Getting capacity needs analysis");
        Map<String, Object> analysis = capacityPlanningService.analyzeCapacityNeeds();
        return ResponseEntity.ok(analysis);
    }

    /**
     * Get optimal resource allocation plan.
     * 
     * @return Map containing allocation recommendations
     */
    @GetMapping("/optimal-plan")
    public ResponseEntity<Map<String, Object>> getOptimalAllocationPlan() {
        logger.info("Getting optimal allocation plan");
        Map<String, Object> plan = capacityPlanningService.generateOptimalAllocationPlan();
        return ResponseEntity.ok(plan);
    }

    /**
     * Get capacity planning dashboard with key metrics.
     * 
     * @return Map containing key capacity planning metrics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getCapacityDashboard() {
        logger.info("Getting capacity planning dashboard");
        
        // Get capacity needs analysis
        Map<String, Object> needs = capacityPlanningService.analyzeCapacityNeeds();
        
        // Get 3-month forecast
        Map<String, Object> forecast = capacityPlanningService.generateResourceForecast(3);
        
        // Create dashboard with key metrics
        Map<String, Object> dashboard = Map.of(
            "currentAllocation", needs.get("currentAllocation"),
            "utilizationPercentages", needs.get("utilizationPercentages"),
            "recommendations", needs.get("recommendations"),
            "forecast", forecast.get("projectedAllocation"),
            "forecastMonths", forecast.get("months")
        );
        
        return ResponseEntity.ok(dashboard);
    }
}
