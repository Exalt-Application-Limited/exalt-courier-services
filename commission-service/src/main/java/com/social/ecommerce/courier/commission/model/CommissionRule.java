package com.gogidix.courierservices.commission.$1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_rule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionRule {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "partner_type", nullable = false)
    private PartnerType partnerType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rate_type", nullable = false)
    private RateType rateType;
    
    @Column(name = "rate_value", nullable = false)
    private BigDecimal rateValue;
    
    @Column(name = "min_amount")
    private BigDecimal minAmount;
    
    @Column(name = "max_amount")
    private BigDecimal maxAmount;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    private Integer priority = 0;
    
    @Column(columnDefinition = "TEXT")
    private String conditions; // JSON structure for complex conditions
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommissionStatus status;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Custom builder to ensure created and updated timestamps are set
     */
    public static class CommissionRuleBuilder {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public CommissionRuleBuilder() {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    // Helper method to check if rule is active at a given date
    public boolean isActiveAt(LocalDate date) {
        if (status != CommissionStatus.APPROVED) {
            return false;
        }
        
        boolean afterStartDate = date.isEqual(startDate) || date.isAfter(startDate);
        boolean beforeEndDate = endDate == null || date.isEqual(endDate) || date.isBefore(endDate);
        
        return afterStartDate && beforeEndDate;
    }
    
    // Helper method to calculate commission amount
    public BigDecimal calculateCommission(BigDecimal baseAmount) {
        if (rateType == RateType.FIXED) {
            return rateValue;
        } else if (rateType == RateType.PERCENTAGE) {
            return baseAmount.multiply(rateValue).divide(new BigDecimal("100.0"));
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Backward compatibility getter for minimum amount
     */
    public BigDecimal getMinimumAmount() {
        return minAmount;
    }
    
    /**
     * Backward compatibility getter for maximum amount
     */
    public BigDecimal getMaximumAmount() {
        return maxAmount;
    }
}
