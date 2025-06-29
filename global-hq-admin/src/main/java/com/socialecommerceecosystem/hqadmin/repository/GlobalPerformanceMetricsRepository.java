package com.exalt.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.GlobalPerformanceMetrics;
import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for {@link GlobalPerformanceMetrics} entity that provides
 * data access operations for global performance metrics.
 */
@Repository
public interface GlobalPerformanceMetricsRepository extends JpaRepository<GlobalPerformanceMetrics, Long> {
    
    /**
     * Find all performance metrics for a specific date
     * 
     * @param metricDate The date to search for
     * @return List of performance metrics for the specified date
     */
    List<GlobalPerformanceMetrics> findByMetricDate(LocalDate metricDate);
    
    /**
     * Find all performance metrics for a specific global region
     * 
     * @param globalRegion The global region
     * @return List of performance metrics for the region
     */
    List<GlobalPerformanceMetrics> findByGlobalRegion(GlobalRegion globalRegion);
    
    /**
     * Find all performance metrics for a specific metric type
     * 
     * @param metricType The metric type
     * @return List of performance metrics of the specified type
     */
    List<GlobalPerformanceMetrics> findByMetricType(String metricType);
    
    /**
     * Find all performance metrics for a specific metric category
     * 
     * @param metricCategory The metric category
     * @return List of performance metrics in the specified category
     */
    List<GlobalPerformanceMetrics> findByMetricCategory(String metricCategory);
    
    /**
     * Find all performance metrics for a specific date range
     * 
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of performance metrics within the date range
     */
    List<GlobalPerformanceMetrics> findByMetricDateBetween(LocalDate startDate, LocalDate endDate);
    
    /**
     * Find metrics for a region within a date range
     * 
     * @param globalRegion The global region
     * @param startDate The start date
     * @param endDate The end date
     * @return List of metrics for the region within the date range
     */
    List<GlobalPerformanceMetrics> findByGlobalRegionAndMetricDateBetween(
            GlobalRegion globalRegion, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find all metrics with average rating above a threshold
     * 
     * @param ratingThreshold The minimum rating threshold
     * @return List of metrics with average rating above the threshold
     */
    List<GlobalPerformanceMetrics> findByAverageRatingGreaterThanEqual(BigDecimal ratingThreshold);
    
    /**
     * Find all metrics with on-time delivery percentage above a threshold
     * 
     * @param threshold The on-time percentage threshold
     * @return List of metrics with on-time delivery percentage above the threshold
     */
    @Query("SELECT m FROM GlobalPerformanceMetrics m WHERE " +
           "(CAST(m.onTimeDeliveries AS float) / NULLIF(m.totalDeliveries, 0)) * 100 >= :threshold")
    List<GlobalPerformanceMetrics> findByOnTimePercentageAbove(@Param("threshold") float threshold);
    
    /**
     * Find all metrics with profit margin above a threshold
     * 
     * @param marginThreshold The profit margin threshold
     * @return List of metrics with profit margin above the threshold
     */
    List<GlobalPerformanceMetrics> findByProfitMarginGreaterThanEqual(BigDecimal marginThreshold);
    
    /**
     * Calculate average metrics across all regions for a date range
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return Average metrics for the date range
     */
    @Query("SELECT AVG(m.averageRating) as avgRating, " +
           "AVG(m.averageDeliveryTimeMinutes) as avgDeliveryTime, " +
           "AVG(m.profitMargin) as avgProfitMargin, " +
           "SUM(m.totalDeliveries) as totalDeliveries, " +
           "SUM(m.successfulDeliveries) as successfulDeliveries, " +
           "SUM(m.failedDeliveries) as failedDeliveries, " +
           "SUM(m.walkInCustomers) as walkInCustomers, " +
           "AVG(m.routingSuccessRate) as avgRoutingSuccessRate " +
           "FROM GlobalPerformanceMetrics m " +
           "WHERE m.metricDate BETWEEN :startDate AND :endDate")
    Object aggregateMetricsForDateRange(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * Get trend of metrics by date for a specific region
     * 
     * @param region The global region
     * @param startDate The start date
     * @param endDate The end date
     * @return List of metric trends by date
     */
    @Query("SELECT m.metricDate as date, " +
           "SUM(m.totalDeliveries) as totalDeliveries, " +
           "SUM(m.successfulDeliveries) as successfulDeliveries, " +
           "AVG(m.averageRating) as avgRating, " +
           "SUM(m.totalRevenue) as totalRevenue, " +
           "SUM(m.totalCost) as totalCost, " +
           "AVG(m.profitMargin) as avgProfitMargin " +
           "FROM GlobalPerformanceMetrics m " +
           "WHERE m.globalRegion = :region AND m.metricDate BETWEEN :startDate AND :endDate " +
           "GROUP BY m.metricDate " +
           "ORDER BY m.metricDate ASC")
    List<Object[]> getMetricsTrendByDate(
            @Param("region") GlobalRegion region, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
    
    /**
     * Find performance metrics for walk-in customers above a threshold
     * 
     * @param threshold The walk-in customers threshold
     * @return List of metrics with walk-in customers above the threshold
     */
    List<GlobalPerformanceMetrics> findByWalkInCustomersGreaterThanEqual(Integer threshold);
    
    /**
     * Get comparison of metrics between regions for a specific date
     * 
     * @param metricDate The date to compare
     * @return List of metrics by region for comparison
     */
    @Query("SELECT m.globalRegion.name as regionName, " +
           "SUM(m.totalDeliveries) as totalDeliveries, " +
           "SUM(m.successfulDeliveries) as successfulDeliveries, " +
           "AVG(m.averageRating) as avgRating, " +
           "SUM(m.walkInCustomers) as walkInCustomers, " +
           "AVG(m.routingSuccessRate) as routingSuccessRate " +
           "FROM GlobalPerformanceMetrics m " +
           "WHERE m.metricDate = :metricDate AND m.globalRegion IS NOT NULL " +
           "GROUP BY m.globalRegion.id, m.globalRegion.name " +
           "ORDER BY totalDeliveries DESC")
    List<Object[]> compareRegionsForDate(@Param("metricDate") LocalDate metricDate);
}
