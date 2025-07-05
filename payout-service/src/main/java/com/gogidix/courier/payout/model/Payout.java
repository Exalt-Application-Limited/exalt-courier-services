package com.gogidix.courierservices.payout.$1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a payout to a courier.
 */
@Entity
@Table(name = "payout")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique payout reference number
     */
    @Column(name = "reference_number", unique = true, nullable = false)
    private String referenceNumber;

    /**
     * ID of the courier receiving the payout
     */
    @NotBlank
    @Column(name = "courier_id", nullable = false)
    private String courierId;

    /**
     * Total amount to be paid out
     */
    @NotNull
    @PositiveOrZero
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Currency of the payout (ISO currency code)
     */
    @NotBlank
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    /**
     * Current status of the payout
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PayoutStatus status;

    /**
     * Method used for the payout
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    /**
     * Start of the period covered by this payout
     */
    @NotNull
    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;

    /**
     * End of the period covered by this payout
     */
    @NotNull
    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;

    /**
     * Scheduled date for the payout
     */
    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    /**
     * Date when the payout was processed
     */
    @Column(name = "processed_date")
    private LocalDateTime processedDate;

    /**
     * Notes or comments about the payout
     */
    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * Payment provider transaction reference (if applicable)
     */
    @Column(name = "external_reference")
    private String externalReference;

    /**
     * Set of earnings entries that make up this payout
     */
    @OneToMany(mappedBy = "payout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<EarningsEntry> earningsEntries = new HashSet<>();

    /**
     * Date when the entity was created
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Date when the entity was last updated
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Add an earnings entry to this payout.
     *
     * @param entry the earnings entry to add
     * @return the payout with the added entry
     */
    public Payout addEarningsEntry(EarningsEntry entry) {
        earningsEntries.add(entry);
        entry.setPayout(this);
        return this;
    }

    /**
     * Remove an earnings entry from this payout.
     *
     * @param entry the earnings entry to remove
     * @return the payout with the entry removed
     */
    public Payout removeEarningsEntry(EarningsEntry entry) {
        earningsEntries.remove(entry);
        entry.setPayout(null);
        return this;
    }
}
