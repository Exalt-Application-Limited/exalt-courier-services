package com.gogidix.courier.hqadmin.repository.audit;

import com.socialecommerceecosystem.hqadmin.model.audit.GlobalConfigurationAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for managing GlobalConfigurationAuditLog entities.
 */
@Repository
public interface GlobalConfigurationAuditLogRepository extends JpaRepository<GlobalConfigurationAuditLog, Long> {

    /**
     * Find audit logs by component.
     */
    List<GlobalConfigurationAuditLog> findByComponent(String component);
    
    /**
     * Find audit logs by action.
     */
    List<GlobalConfigurationAuditLog> findByAction(String action);
    
    /**
     * Find audit logs by entity type and ID.
     */
    List<GlobalConfigurationAuditLog> findByEntityTypeAndEntityId(String entityType, String entityId);
    
    /**
     * Find audit logs by user ID.
     */
    List<GlobalConfigurationAuditLog> findByUserId(String userId);
    
    /**
     * Find audit logs by region code.
     */
    List<GlobalConfigurationAuditLog> findByRegionCode(String regionCode);
    
    /**
     * Find audit logs created within a date range.
     */
    List<GlobalConfigurationAuditLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Search audit logs by entity name (case-insensitive).
     */
    List<GlobalConfigurationAuditLog> findByEntityNameContainingIgnoreCase(String searchText);
    
    /**
     * Search audit logs by details (case-insensitive).
     */
    @Query("SELECT a FROM GlobalConfigurationAuditLog a WHERE LOWER(a.details) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<GlobalConfigurationAuditLog> findByDetailsContainingIgnoreCase(String searchText);
    
    /**
     * Find recent audit logs with pagination.
     */
    Page<GlobalConfigurationAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find audit logs for a specific entity with pagination.
     */
    Page<GlobalConfigurationAuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, String entityId, Pageable pageable);
}
