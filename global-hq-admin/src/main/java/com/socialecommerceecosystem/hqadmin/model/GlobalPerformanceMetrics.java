package com.gogidix.courier.courier.hqadmin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents global performance metrics that aggregate data from all regional systems.
 * This entity enables HQ administrators to monitor courier operations performance
 * across the entire organization.
 */
@Data
@Entity
@Table(name = "global_performance_metrics")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalPerformanceMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "metric_date", nullable = false)
    private LocalDate metricDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "global_region_id")
    private GlobalRegion globalRegion;
    
    @Column(name = "metric_type")
    private String metricType;
    
    @Column(name = "metric_category")
    private String metricCategory;
    
    // Total delivery volume
    @Column(name = "total_deliveries")
    private Integer totalDeliveries;
    
    @Column(name = "successful_deliveries")
    private Integer successfulDeliveries;
    
    @Column(name = "failed_deliveries")
    private Integer failedDeliveries;
    
    @Column(name = "on_time_deliveries")
    private Integer onTimeDeliveries;
    
    @Column(name = "delayed_deliveries")
    private Integer delayedDeliveries;
    
    // Delivery KPIs
    @Column(name = "average_delivery_time_minutes", precision = 10, scale = 2)
    private BigDecimal averageDeliveryTimeMinutes;
    
    @Column(name = "median_delivery_time_minutes", precision = 10, scale = 2)
    private BigDecimal medianDeliveryTimeMinutes;
    
    @Column(name = "average_distance_km", precision = 10, scale = 2)
    private BigDecimal averageDistanceKm;
    
    // Customer satisfaction
    @Column(name = "average_rating", precision = 5, scale = 2)
    private BigDecimal averageRating;
    
    @Column(name = "total_ratings")
    private Integer totalRatings;
    
    @Column(name = "customer_complaints")
    private Integer customerComplaints;
    
    // Financial metrics
    @Column(name = "total_revenue", precision = 14, scale = 2)
    private BigDecimal totalRevenue;
    
    @Column(name = "total_cost", precision = 14, scale = 2)
    private BigDecimal totalCost;
    
    @Column(name = "profit_margin", precision = 10, scale = 2)
    private BigDecimal profitMargin;
    
    // Courier metrics
    @Column(name = "active_couriers")
    private Integer activeCouriers;
    
    @Column(name = "average_deliveries_per_courier", precision = 10, scale = 2)
    private BigDecimal averageDeliveriesPerCourier;
    
    @Column(name = "courier_utilization_percentage", precision = 5, scale = 2)
    private BigDecimal courierUtilizationPercentage;
    
    // Walk-in customer metrics
    @Column(name = "walk_in_customers")
    private Integer walkInCustomers;
    
    @Column(name = "walk_in_shipments")
    private Integer walkInShipments;
    
    // Regional request routing metrics
    @Column(name = "total_requests_routed")
    private Integer totalRequestsRouted;
    
    @Column(name = "routing_success_rate", precision = 5, scale = 2)
    private BigDecimal routingSuccessRate;
    
    @NotNull
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @NotNull
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
    
    /**
     * Calculate the percentage of successful deliveries
     * 
     * @return Percentage of successful deliveries or null if total deliveries is zero
     */
    @Transient
    public BigDecimal getSuccessRate() {
        if (totalDeliveries == null || totalDeliveries == 0) {
            return null;
        }
        return BigDecimal.valueOf(successfulDeliveries)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(totalDeliveries), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    /**
     * Calculate the percentage of on-time deliveries
     * 
     * @return Percentage of on-time deliveries or null if total deliveries is zero
     */
    @Transient
    public BigDecimal getOnTimeRate() {
        if (totalDeliveries == null || totalDeliveries == 0) {
            return null;
        }
        return BigDecimal.valueOf(onTimeDeliveries)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(totalDeliveries), 2, BigDecimal.ROUND_HALF_UP);
    }
}
