package com.exalt.courierservices.commission.$1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_entry")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionEntry {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private CommissionRule rule;
    
    @Column(name = "order_id", nullable = false)
    private String orderId;
    
    @Column(name = "base_amount", nullable = false)
    private BigDecimal baseAmount;
    
    @Column(name = "commission_amount", nullable = false)
    private BigDecimal commissionAmount;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommissionStatus status;
    
    @Column(name = "payment_id")
    private String paymentId;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
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
    public static class CommissionEntryBuilder {
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public CommissionEntryBuilder() {
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }
}
