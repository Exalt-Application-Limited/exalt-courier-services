package com.gogidix.courier.regionaladmin.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity class for storing regional performance metrics.
 * Aggregates data from multiple branches in a region and provides 
 * a consolidated view of performance.
 */
@Entity
@Table(name = "regional_metrics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionalMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String regionCode;
    
    @Column(nullable = false)
    private String regionName;
    
    private String metricCategory; // DELIVERY, FINANCIAL, OPERATIONAL, CUSTOMER_SATISFACTION
    
    @Column(nullable = false)
    private String metricName;
    
    private Double metricValue;
    
    @Column(name = "metric_unit")
    private String unit;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "regional_metric_attributes", 
                    joinColumns = @JoinColumn(name = "metric_id"))
    @MapKeyColumn(name = "attribute_name")
    @Column(name = "attribute_value")
    private Map<String, String> attributes = new HashMap<>();
    
    @Column(nullable = false)
    private LocalDateTime dataTimestamp;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    private Boolean isActive;
    
    @Column(name = "branch_count")
    private Integer branchCount;
    
    @Column(name = "aggregation_method")
    private String aggregationMethod; // SUM, AVG, MIN, MAX
}
