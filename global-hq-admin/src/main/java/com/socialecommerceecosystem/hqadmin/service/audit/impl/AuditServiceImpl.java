package com.exalt.courier.hqadmin.service.audit.impl;

import com.socialecommerceecosystem.hqadmin.model.audit.GlobalConfigurationAuditLog;
import com.socialecommerceecosystem.hqadmin.repository.audit.GlobalConfigurationAuditLogRepository;
import com.socialecommerceecosystem.hqadmin.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the AuditService interface.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final GlobalConfigurationAuditLogRepository auditLogRepository;

    @Override
    public GlobalConfigurationAuditLog createAuditLog(GlobalConfigurationAuditLog auditLog) {
        log.debug("Creating audit log: {}", auditLog);
        return auditLogRepository.save(auditLog);
    }

    @Override
    public GlobalConfigurationAuditLog getAuditLogById(Long id) {
        log.debug("Getting audit log by id: {}", id);
        return auditLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<GlobalConfigurationAuditLog> getAuditLogsByComponent(String component) {
        log.debug("Getting audit logs by component: {}", component);
        return auditLogRepository.findByComponent(component);
    }

    @Override
    public List<GlobalConfigurationAuditLog> getAuditLogsByAction(String action) {
        log.debug("Getting audit logs by action: {}", action);
        return auditLogRepository.findByAction(action);
    }

    @Override
    public List<GlobalConfigurationAuditLog> getAuditLogsByEntityTypeAndId(String entityType, String entityId) {
        log.debug("Getting audit logs by entity type: {} and id: {}", entityType, entityId);
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    @Override
    public List<GlobalConfigurationAuditLog> getAuditLogsByUser(String userId) {
        log.debug("Getting audit logs by user: {}", userId);
        return auditLogRepository.findByUserId(userId);
    }

    @Override
    public List<GlobalConfigurationAuditLog> getAuditLogsByRegion(String regionCode) {
        log.debug("Getting audit logs by region: {}", regionCode);
        return auditLogRepository.findByRegionCode(regionCode);
    }

    @Override
    public List<GlobalConfigurationAuditLog> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting audit logs between {} and {}", startDate, endDate);
        return auditLogRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<GlobalConfigurationAuditLog> searchAuditLogsByEntityName(String searchText) {
        log.debug("Searching audit logs by entity name: {}", searchText);
        return auditLogRepository.findByEntityNameContainingIgnoreCase(searchText);
    }

    @Override
    public List<GlobalConfigurationAuditLog> searchAuditLogsByDetails(String searchText) {
        log.debug("Searching audit logs by details: {}", searchText);
        return auditLogRepository.findByDetailsContainingIgnoreCase(searchText);
    }

    @Override
    public Page<GlobalConfigurationAuditLog> getRecentAuditLogs(Pageable pageable) {
        log.debug("Getting recent audit logs with pagination: {}", pageable);
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Override
    public Page<GlobalConfigurationAuditLog> getAuditLogsForEntity(String entityType, String entityId, Pageable pageable) {
        log.debug("Getting audit logs for entity type: {} and id: {} with pagination: {}", entityType, entityId, pageable);
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId, pageable);
    }

    @Override
    public GlobalConfigurationAuditLog logConfigurationChange(
            String component,
            String action,
            String entityId,
            String entityType,
            String entityName,
            String userId,
            String ipAddress,
            String userAgent,
            String regionCode,
            String details) {
        
        log.debug("Logging configuration change: component={}, action={}, entityId={}, entityType={}, user={}", 
                component, action, entityId, entityType, userId);
        
        GlobalConfigurationAuditLog auditLog = GlobalConfigurationAuditLog.builder()
                .component(component)
                .action(action)
                .entityId(entityId)
                .entityType(entityType)
                .entityName(entityName)
                .userId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .regionCode(regionCode)
                .details(details)
                .build();
        
        return createAuditLog(auditLog);
    }

    @Override
    public GlobalConfigurationAuditLog logFieldChange(
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
            String regionCode) {
        
        log.debug("Logging field change: component={}, entityId={}, entityType={}, field={}, user={}", 
                component, entityId, entityType, fieldName, userId);
        
        String details = String.format("Changed field '%s' from '%s' to '%s'", fieldName, oldValue, newValue);
        
        GlobalConfigurationAuditLog auditLog = GlobalConfigurationAuditLog.builder()
                .component(component)
                .action("UPDATE")
                .entityId(entityId)
                .entityType(entityType)
                .entityName(entityName)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .userId(userId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .regionCode(regionCode)
                .details(details)
                .build();
        
        return createAuditLog(auditLog);
    }
}
