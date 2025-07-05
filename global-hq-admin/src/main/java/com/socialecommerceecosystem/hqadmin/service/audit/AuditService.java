package com.gogidix.courier.hqadmin.service.audit;

import com.socialecommerceecosystem.hqadmin.model.audit.GlobalConfigurationAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing audit logs.
 */
public interface AuditService {

    /**
     * Create an audit log entry.
     */
    GlobalConfigurationAuditLog createAuditLog(GlobalConfigurationAuditLog auditLog);
    
    /**
     * Get an audit log by ID.
     */
    GlobalConfigurationAuditLog getAuditLogById(Long id);
    
    /**
     * Get audit logs by component.
     */
    List<GlobalConfigurationAuditLog> getAuditLogsByComponent(String component);
    
    /**
     * Get audit logs by action.
     */
    List<GlobalConfigurationAuditLog> getAuditLogsByAction(String action);
    
    /**
     * Get audit logs by entity type and ID.
     */
    List<GlobalConfigurationAuditLog> getAuditLogsByEntityTypeAndId(String entityType, String entityId);
    
    /**
     * Get audit logs by user ID.
     */
    List<GlobalConfigurationAuditLog> getAuditLogsByUser(String userId);
    
    /**
     * Get audit logs by region code.
     */
    List<GlobalConfigurationAuditLog> getAuditLogsByRegion(String regionCode);
    
    /**
     * Get audit logs created within a date range.
     */
    List<GlobalConfigurationAuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Search audit logs by entity name.
     */
    List<GlobalConfigurationAuditLog> searchAuditLogsByEntityName(String searchText);
    
    /**
     * Search audit logs by details.
     */
    List<GlobalConfigurationAuditLog> searchAuditLogsByDetails(String searchText);
    
    /**
     * Get recent audit logs with pagination.
     */
    Page<GlobalConfigurationAuditLog> getRecentAuditLogs(Pageable pageable);
    
    /**
     * Get audit logs for a specific entity with pagination.
     */
    Page<GlobalConfigurationAuditLog> getAuditLogsForEntity(String entityType, String entityId, Pageable pageable);
    
    /**
     * Log a configuration change.
     */
    GlobalConfigurationAuditLog logConfigurationChange(
            String component,
            String action,
            String entityId,
            String entityType,
            String entityName,
            String userId,
            String ipAddress,
            String userAgent,
            String regionCode,
            String details);
    
    /**
     * Log a field change.
     */
    GlobalConfigurationAuditLog logFieldChange(
            String component,
            String entityId,
            String entityType,
            String entityName,
            String fieldName,
            String oldValue,
            String newValue,
            String userId,
            String ipAddress,
            String userAgent,
            String regionCode);
}
