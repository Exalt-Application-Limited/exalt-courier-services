package com.exalt.courierservices.payout.$1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "earnings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Earning {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;
    
    @Column(name = "courier_id", nullable = false)
    private String courierId;
    
    @Column(name = "order_id", nullable = false)
    private String orderId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EarningStatus status;
    
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
}
