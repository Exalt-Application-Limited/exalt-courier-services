package com.exalt.courierservices.commission.$1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payment_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PaymentDetailsId.class)
public class PaymentDetails {
    
    @Id
    @Column(name = "payment_id")
    private String paymentId;
    
    @Id
    @Column(name = "commission_entry_id")
    private String commissionEntryId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", insertable = false, updatable = false)
    private PartnerPayment payment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_entry_id", insertable = false, updatable = false)
    private CommissionEntry commissionEntry;
}
