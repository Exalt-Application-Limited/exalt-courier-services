package com.exalt.courier.management.performance.repository;

import com.exalt.courier.management.courier.model.Courier;
import com.exalt.courier.management.performance.model.MetricType;
import com.exalt.courier.management.performance.model.PerformanceMetric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing and manipulating performance metrics data.
 */
@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Long> {

    /**
     * Find a metric by its unique ID.
     *
     * @param metricId the unique ID of the metric
     * @return an Optional containing the metric if found
     */
    Optional<PerformanceMetric> findByMetricId(String metricId);

    /**
     * Find all metrics for a specific courier.
     *
     * @param courier the courier
     * @return a list of metrics for the courier
     */
    List<PerformanceMetric> findByCourier(Courier courier);

    /**
     * Find all metrics for a specific courier with pagination.
     *
     * @param courier the courier
     * @param pageable pagination information
     * @return a page of metrics for the courier
     */
    Page<PerformanceMetric> findByCourier(Courier courier, Pageable pageable);

    /**
     * Find metrics for a specific courier and metric type.
     *
     * @param courier the courier
     * @param metricType the type of metric
     * @return a list of metrics matching the criteria
     */
    List<PerformanceMetric> findByCourierAndMetricType(Courier courier, MetricType metricType);

    /**
     * Find metrics for a specific date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of metrics within the date range
     */
    List<PerformanceMetric> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics for a specific courier and date range.
     *
     * @param courier the courier
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of metrics matching the criteria
     */
    List<PerformanceMetric> findByCourierAndDateBetween(Courier courier, LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics for a specific courier, metric type, and date range.
     *
     * @param courier the courier
     * @param metricType the type of metric
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return a list of metrics matching the criteria
     */
    List<PerformanceMetric> findByCourierAndMetricTypeAndDateBetween(
            Courier courier, MetricType metricType, LocalDate startDate, LocalDate endDate);

    /**
     * Find metrics where the target was met.
     *
     * @param isTargetMet whether the target was met
     * @return a list of metrics where the target was met or not met
     */
    List<PerformanceMetric> findByIsTargetMet(Boolean isTargetMet);

    /**
     * Calculate the average value for a specific metric type across all couriers.
     *
     * @param metricType the type of metric
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the average value
     */
    @Query("SELECT AVG(pm.value) FROM PerformanceMetric pm WHERE pm.metricType = :metricType AND pm.date BETWEEN :startDate AND :endDate")
    Double calculateAverageForMetricType(
            @Param("metricType") MetricType metricType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Calculate the average value for a specific metric type for a courier.
     *
     * @param courier the courier
     * @param metricType the type of metric
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return the average value
     */
    @Query("SELECT AVG(pm.value) FROM PerformanceMetric pm WHERE pm.courier = :courier AND pm.metricType = :metricType AND pm.date BETWEEN :startDate AND :endDate")
    Double calculateAverageForCourierAndMetricType(
            @Param("courier") Courier courier,
            @Param("metricType") MetricType metricType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
} 