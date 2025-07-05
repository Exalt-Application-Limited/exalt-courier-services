package com.gogidix.courier.hqadmin.repository;

import com.socialecommerceecosystem.hqadmin.model.ConfigurationAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data repository for the ConfigurationAuditLog entity.
 */
@Repository
public interface ConfigurationAuditLogRepository extends JpaRepository<ConfigurationAuditLog, Long> {

    /**
     * Find audit logs by config ID
     */
    List<ConfigurationAuditLog> findByConfigIdOrderByTimestampDesc(Long configId);

    /**
     * Find audit logs by service key
     */
    List<ConfigurationAuditLog> findByServiceKeyOrderByTimestampDesc(String serviceKey);

    /**
     * Find audit logs by global region ID
     */
    List<ConfigurationAuditLog> findByGlobalRegionIdOrderByTimestampDesc(Long globalRegionId);

    /**
     * Find audit logs by change type
     */
    List<ConfigurationAuditLog> findByChangeTypeOrderByTimestampDesc(String changeType);

    /**
     * Find audit logs by user who made the change
     */
    List<ConfigurationAuditLog> findByChangedByOrderByTimestampDesc(String changedBy);

    /**
     * Find audit logs in a date range
     */
    List<ConfigurationAuditLog> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count configuration changes by day over the last month
     */
    @Query(value = "SELECT DATE(timestamp) as change_date, COUNT(*) as change_count " +
            "FROM config_audit_logs " +
            "WHERE timestamp >= DATEADD('DAY', -30, CURRENT_DATE) " +
            "GROUP BY DATE(timestamp) " +
            "ORDER BY change_date DESC", nativeQuery = true)
    List<Object[]> countChangesByDayLastMonth();

    /**
     * Find recent changes by a specific user
     */
    List<ConfigurationAuditLog> findTop10ByChangedByOrderByTimestampDesc(String changedBy);

    /**
     * Find recent changes for a specific configuration ID
     */
    List<ConfigurationAuditLog> findTop10ByConfigIdOrderByTimestampDesc(Long configId);

    /**
     * Find changes by region and service key
     */
    List<ConfigurationAuditLog> findByGlobalRegionIdAndServiceKeyOrderByTimestampDesc(
            Long globalRegionId, String serviceKey);
}
