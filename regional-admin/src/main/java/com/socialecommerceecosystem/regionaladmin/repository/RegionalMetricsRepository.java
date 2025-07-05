package com.gogidix.courier.regionaladmin.repository;

import com.socialecommerceecosystem.regionaladmin.model.RegionalMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for accessing and managing RegionalMetrics entities.
 * Provides methods to query metrics data by various criteria.
 */
@Repository
public interface RegionalMetricsRepository extends JpaRepository<RegionalMetrics, Long> {

    /**
     * Find metrics by region code
     */
    List<RegionalMetrics> findByRegionCode(String regionCode);
    
    /**
     * Find metrics by region code and category
     */
    List<RegionalMetrics> findByRegionCodeAndMetricCategory(String regionCode, String metricCategory);
    
    /**
     * Find metrics by name, region, and time range
     */
    List<RegionalMetrics> findByMetricNameAndRegionCodeAndDataTimestampBetween(
            String metricName, String regionCode, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * Find the most recent metrics for a specific region and category
     */
    @Query("SELECT rm FROM RegionalMetrics rm WHERE rm.regionCode = :regionCode " +
           "AND rm.metricCategory = :category " +
           "AND rm.dataTimestamp = (SELECT MAX(r.dataTimestamp) FROM RegionalMetrics r " +
           "WHERE r.regionCode = :regionCode AND r.metricCategory = :category)")
    List<RegionalMetrics> findMostRecentMetricsByRegionAndCategory(
            @Param("regionCode") String regionCode,
            @Param("category") String category);
    
    /**
     * Find metrics for a specific region within a date range
     */
    @Query("SELECT rm FROM RegionalMetrics rm WHERE rm.regionCode = :regionCode " +
           "AND rm.dataTimestamp BETWEEN :startDate AND :endDate " +
           "ORDER BY rm.dataTimestamp DESC")
    List<RegionalMetrics> findMetricsForRegionInTimeRange(
            @Param("regionCode") String regionCode,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get aggregated metric value (average) for a given metric across all regions
     */
    @Query("SELECT AVG(rm.metricValue) FROM RegionalMetrics rm WHERE rm.metricName = :metricName " +
           "AND rm.dataTimestamp BETWEEN :startDate AND :endDate")
    Double getAverageMetricValueAcrossRegions(
            @Param("metricName") String metricName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
