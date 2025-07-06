package com.gogidix.courierservices.payout.$1;

import com.gogidix.courier.payout.model.Payout;
import com.gogidix.courier.payout.model.PayoutStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payout entity operations.
 */
@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {

    /**
     * Find a payout by its reference number.
     *
     * @param referenceNumber the reference number
     * @return the payout if found
     */
    Optional<Payout> findByReferenceNumber(String referenceNumber);

    /**
     * Find all payouts for a specific courier.
     *
     * @param courierId the courier ID
     * @param pageable pagination information
     * @return page of payouts
     */
    Page<Payout> findByCourierId(String courierId, Pageable pageable);

    /**
     * Find all payouts with a specific status.
     *
     * @param status the payout status
     * @param pageable pagination information
     * @return page of payouts
     */
    Page<Payout> findByStatus(PayoutStatus status, Pageable pageable);

    /**
     * Find all payouts for a courier with a specific status.
     *
     * @param courierId the courier ID
     * @param status the payout status
     * @param pageable pagination information
     * @return page of payouts
     */
    Page<Payout> findByCourierIdAndStatus(String courierId, PayoutStatus status, Pageable pageable);

    /**
     * Find all payouts scheduled for a specific date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return page of payouts
     */
    Page<Payout> findByScheduledDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find all payouts for a specific period.
     *
     * @param periodStart the period start date
     * @param periodEnd the period end date
     * @param pageable pagination information
     * @return page of payouts
     */
    Page<Payout> findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
            LocalDateTime periodStart, LocalDateTime periodEnd, Pageable pageable);

    /**
     * Find all payouts by payment method.
     *
     * @param paymentMethod the payment method
     * @param pageable pagination information
     * @return page of payouts
     */
    @Query("SELECT p FROM Payout p WHERE p.paymentMethod = :paymentMethod")
    Page<Payout> findByPaymentMethod(@Param("paymentMethod") String paymentMethod, Pageable pageable);

    /**
     * Find all payouts with amount greater than or equal to the specified value.
     *
     * @param amount the minimum amount
     * @param pageable pagination information
     * @return page of payouts
     */
    Page<Payout> findByAmountGreaterThanEqual(BigDecimal amount, Pageable pageable);

    /**
     * Find all payouts with amount less than or equal to the specified value.
     *
     * @param amount the maximum amount
     * @param pageable pagination information
     * @return page of payouts
     */
    Page<Payout> findByAmountLessThanEqual(BigDecimal amount, Pageable pageable);

    /**
     * Find all pending payouts scheduled before a specific date.
     *
     * @param date the date
     * @return list of payouts
     */
    @Query("SELECT p FROM Payout p WHERE p.status = 'PENDING' AND p.scheduledDate <= :date")
    List<Payout> findPendingPayoutsScheduledBefore(@Param("date") LocalDateTime date);

    /**
     * Get the total amount of payouts for a courier within a specific date range.
     *
     * @param courierId the courier ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the total amount
     */
    @Query("SELECT SUM(p.amount) FROM Payout p WHERE p.courierId = :courierId " +
            "AND p.periodStart >= :startDate AND p.periodEnd <= :endDate AND p.status = 'COMPLETED'")
    BigDecimal getTotalPayoutAmountForCourierInPeriod(
            @Param("courierId") String courierId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Count the number of payouts for each status.
     *
     * @return map of status to count
     */
    @Query("SELECT p.status, COUNT(p) FROM Payout p GROUP BY p.status")
    List<Object[]> countPayoutsByStatus();
}
