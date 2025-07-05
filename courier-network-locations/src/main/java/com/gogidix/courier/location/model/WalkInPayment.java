package com.gogidix.courier.location.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * Represents a payment transaction for a walk-in shipment at a physical courier location.
 * This entity tracks payment details, including method, amount, and transaction status.
 */
@Entity
@Table(name = "walk_in_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class WalkInPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private WalkInShipment shipment;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @Column(name = "card_last_four")
    private String cardLastFour;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "authorization_code")
    private String authorizationCode;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "refund_date")
    private LocalDateTime refundDate;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "notes")
    private String notes;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private String paymentDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_staff_id")
    private LocationStaff processedByStaff;

    /**
     * Gets the payment details as a JsonNode object.
     * 
     * @return JsonNode representation of payment details
     */
    @Transient
    public JsonNode getPaymentDetailsAsJson() {
        try {
            if (paymentDetails == null || paymentDetails.isEmpty()) {
                return new ObjectMapper().createObjectNode();
            }
            return new ObjectMapper().readTree(paymentDetails);
        } catch (Exception e) {
            return new ObjectMapper().createObjectNode();
        }
    }

    /**
     * Processes a refund for this payment.
     * 
     * @param refundAmount the amount to refund
     * @param reason the reason for the refund
     * @return true if the refund was successful, false otherwise
     */
    public boolean processRefund(BigDecimal refundAmount, String reason) {
        if (refundAmount.compareTo(this.amount) > 0) {
            return false;
        }
        
        this.refundAmount = refundAmount;
        this.refundDate = LocalDateTime.now();
        this.refundReason = reason;
        this.status = PaymentStatus.REFUNDED;
        return true;
    }

    /**
     * Marks this payment as failed.
     * 
     * @param failureReason the reason for the failure
     */
    public void markAsFailed(String failureReason) {
        this.status = PaymentStatus.FAILED;
        this.notes = failureReason;
    }

    /**
     * Checks if this payment has been refunded.
     * 
     * @return true if refunded, false otherwise
     */
    @Transient
    public boolean isRefunded() {
        return status == PaymentStatus.REFUNDED && refundAmount != null;
    }

    /**
     * Checks if a receipt can be printed for this payment.
     * 
     * @return true if a receipt can be printed, false otherwise
     */
    @Transient
    public boolean canPrintReceipt() {
        return status == PaymentStatus.COMPLETED && receiptNumber != null;
    }

    /**
     * Generates a receipt number for this payment.
     */
    public void generateReceiptNumber() {
        if (receiptNumber == null) {
            // Format: LOCATION_ID-YYYYMMDD-TRANSACTION_ID_LAST_6
            String locationId = shipment.getOrigin().getId().toString();
            String dateStr = paymentDate.toString().replaceAll("[^0-9]", "").substring(0, 8);
            String txnSuffix = transactionId.substring(Math.max(0, transactionId.length() - 6));
            
            this.receiptNumber = String.format("RCT-%s-%s-%s", locationId, dateStr, txnSuffix);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (paymentDate == null) {
            paymentDate = LocalDateTime.now();
        }
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
        generateReceiptNumber();
    }
}
