package com.exalt.courierservices.payout.$1;

import com.exalt.courier.payout.model.EarningsEntry;
import com.exalt.courier.payout.model.EarningsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for EarningsEntry entity operations.
 */
@Repository
public interface EarningsEntryRepository extends JpaRepository<EarningsEntry, Long> {

    /**
     * Find all earnings entries for a specific courier.
     *
     * @param courierId the courier ID
     * @param pageable pagination information
     * @return page of earnings entries
     */
    Page<EarningsEntry> findByCourierId(String courierId, Pageable pageable);

    /**
     * Find all earnings entries for a specific payout.
     *
     * @param payoutId the payout ID
     * @param pageable pagination information
     * @return page of earnings entries
     */
    @Query("SELECT e FROM EarningsEntry e WHERE e.payout.id = :payoutId")
    Page<EarningsEntry> findByPayoutId(@Param("payoutId") Long payoutId, Pageable pageable);

    /**
     * Find all earnings entries by type.
     *
     * @param type the earnings type
     * @param pageable pagination information
     * @return page of earnings entries
     */
    Page<EarningsEntry> findByType(EarningsType type, Pageable pageable);

    /**
     * Find all earnings entries for a courier by type.
     *
     * @param courierId the courier ID
     * @param type the earnings type
     * @param pageable pagination information
     * @return page of earnings entries
     */
    Page<EarningsEntry> findByCourierIdAndType(String courierId, EarningsType type, Pageable pageable);

    /**
     * Find all earnings entries for a specific assignment.
     *
     * @param assignmentId the assignment ID
     * @param pageable pagination information
     * @return page of earnings entries
     */
    Page<EarningsEntry> findByAssignmentId(String assignmentId, Pageable pageable);

    /**
     * Find all earnings entries for a courier within a specific date range.
     *
     * @param courierId the courier ID
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return page of earnings entries
     */
    Page<EarningsEntry> findByCourierIdAndEarnedAtBetween(
            String courierId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Find all earnings entries within a specific date range.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination information
     * @return page of earnings entries
     */
    Page<EarningsEntry> findByEarnedAtBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * Get the total earnings amount for a courier within a specific date range.
     *
     * @param courierId the courier ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the total amount
     */
    @Query("SELECT SUM(e.amount) FROM EarningsEntry e WHERE e.courierId = :courierId " +
            "AND e.earnedAt >= :startDate AND e.earnedAt <= :endDate")
    BigDecimal getTotalEarningsForCourierInPeriod(
            @Param("courierId") String courierId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Get the total earnings amount for a courier by type within a specific date range.
     *
     * @param courierId the courier ID
     * @param type the earnings type
     * @param startDate the start date
     * @param endDate the end date
     * @return the total amount
     */
    @Query("SELECT SUM(e.amount) FROM EarningsEntry e WHERE e.courierId = :courierId " +
            "AND e.type = :type AND e.earnedAt >= :startDate AND e.earnedAt <= :endDate")
    BigDecimal getTotalEarningsForCourierByTypeInPeriod(
            @Param("courierId") String courierId,
            @Param("type") EarningsType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find all earnings entries for a courier that haven't been assigned to a payout yet.
     *
     * @param courierId the courier ID
     * @return list of earnings entries
     */
    @Query("SELECT e FROM EarningsEntry e WHERE e.courierId = :courierId AND e.payout IS NULL")
    List<EarningsEntry> findUnpaidEarningsForCourier(@Param("courierId") String courierId);

    /**
     * Find all earnings entries that haven't been assigned to a payout yet and were earned before a specific date.
     *
     * @param date the date
     * @return list of earnings entries
     */
    @Query("SELECT e FROM EarningsEntry e WHERE e.payout IS NULL AND e.earnedAt <= :date")
    List<EarningsEntry> findUnpaidEarningsBeforeDate(@Param("date") LocalDateTime date);

    /**
     * Calculate earnings statistics by type for a courier within a specific date range.
     *
     * @param courierId the courier ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of type and amount pairs
     */
    @Query("SELECT e.type, SUM(e.amount) FROM EarningsEntry e WHERE e.courierId = :courierId " +
            "AND e.earnedAt >= :startDate AND e.earnedAt <= :endDate GROUP BY e.type")
    List<Object[]> getEarningsStatisticsByTypeForCourier(
            @Param("courierId") String courierId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
