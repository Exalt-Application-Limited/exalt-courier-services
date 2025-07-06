package com.gogidix.courier.billing.repository;

import com.gogidix.courier.billing.model.BillingAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Billing Audit operations.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Repository
public interface BillingAuditRepository extends JpaRepository<BillingAudit, UUID> {

    /**
     * Find audit trail by entity ID.
     */
    List<BillingAudit> findByEntityIdOrderByPerformedAtDesc(UUID entityId);

    /**
     * Find audit trail by entity type.
     */
    List<BillingAudit> findByEntityTypeOrderByPerformedAtDesc(String entityType);

    /**
     * Find audit trail by action.
     */
    List<BillingAudit> findByActionOrderByPerformedAtDesc(String action);

    /**
     * Find audit trail by performer.
     */
    List<BillingAudit> findByPerformedByOrderByPerformedAtDesc(String performedBy);

    /**
     * Find audit trail by date range.
     */
    @Query("SELECT a FROM BillingAudit a WHERE a.performedAt BETWEEN :fromDate AND :toDate ORDER BY a.performedAt DESC")
    List<BillingAudit> findByDateRange(@Param("fromDate") LocalDateTime fromDate, 
                                     @Param("toDate") LocalDateTime toDate);

    /**
     * Find audit trail by entity and action.
     */
    List<BillingAudit> findByEntityIdAndActionOrderByPerformedAtDesc(UUID entityId, String action);
}