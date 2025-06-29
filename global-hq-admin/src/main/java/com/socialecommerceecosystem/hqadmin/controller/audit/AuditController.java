package com.exalt.courier.hqadmin.controller.audit;

import com.socialecommerceecosystem.hqadmin.model.audit.GlobalConfigurationAuditLog;
import com.socialecommerceecosystem.hqadmin.service.audit.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for managing audit logs.
 */
@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;

    /**
     * GET /api/v1/audit-logs : Get all audit logs with pagination
     * 
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of audit logs in body
     */
    @GetMapping
    public ResponseEntity<Page<GlobalConfigurationAuditLog>> getAllAuditLogs(Pageable pageable) {
        log.debug("REST request to get a page of audit logs");
        Page<GlobalConfigurationAuditLog> page = auditService.getRecentAuditLogs(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /api/v1/audit-logs/{id} : Get audit log by id
     * 
     * @param id the id of the audit log to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the audit log, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    public ResponseEntity<GlobalConfigurationAuditLog> getAuditLog(@PathVariable Long id) {
        log.debug("REST request to get audit log : {}", id);
        GlobalConfigurationAuditLog auditLog = auditService.getAuditLogById(id);
        if (auditLog == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Audit log not found with id: " + id);
        }
        return ResponseEntity.ok(auditLog);
    }

    /**
     * GET /api/v1/audit-logs/component/{component} : Get audit logs by component
     * 
     * @param component the component to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of audit logs
     */
    @GetMapping("/component/{component}")
    public ResponseEntity<List<GlobalConfigurationAuditLog>> getAuditLogsByComponent(@PathVariable String component) {
        log.debug("REST request to get audit logs by component : {}", component);
        return ResponseEntity.ok(auditService.getAuditLogsByComponent(component));
    }

    /**
     * GET /api/v1/audit-logs/action/{action} : Get audit logs by action
     * 
     * @param action the action to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of audit logs
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<List<GlobalConfigurationAuditLog>> getAuditLogsByAction(@PathVariable String action) {
        log.debug("REST request to get audit logs by action : {}", action);
        return ResponseEntity.ok(auditService.getAuditLogsByAction(action));
    }

    /**
     * GET /api/v1/audit-logs/entity/{entityType}/{entityId} : Get audit logs by entity type and id
     * 
     * @param entityType the entity type to filter by
     * @param entityId the entity id to filter by
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of audit logs
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Page<GlobalConfigurationAuditLog>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            Pageable pageable) {
        log.debug("REST request to get audit logs by entity type : {} and id : {}", entityType, entityId);
        return ResponseEntity.ok(auditService.getAuditLogsForEntity(entityType, entityId, pageable));
    }

    /**
     * GET /api/v1/audit-logs/user/{userId} : Get audit logs by user
     * 
     * @param userId the user id to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of audit logs
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GlobalConfigurationAuditLog>> getAuditLogsByUser(@PathVariable String userId) {
        log.debug("REST request to get audit logs by user : {}", userId);
        return ResponseEntity.ok(auditService.getAuditLogsByUser(userId));
    }

    /**
     * GET /api/v1/audit-logs/region/{regionCode} : Get audit logs by region
     * 
     * @param regionCode the region code to filter by
     * @return the ResponseEntity with status 200 (OK) and the list of audit logs
     */
    @GetMapping("/region/{regionCode}")
    public ResponseEntity<List<GlobalConfigurationAuditLog>> getAuditLogsByRegion(@PathVariable String regionCode) {
        log.debug("REST request to get audit logs by region : {}", regionCode);
        return ResponseEntity.ok(auditService.getAuditLogsByRegion(regionCode));
    }

    /**
     * GET /api/v1/audit-logs/date-range : Get audit logs by date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return the ResponseEntity with status 200 (OK) and the list of audit logs
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<GlobalConfigurationAuditLog>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to get audit logs by date range : {} to {}", startDate, endDate);
        return ResponseEntity.ok(auditService.getAuditLogsByDateRange(startDate, endDate));
    }

    /**
     * GET /api/v1/audit-logs/search/entity-name : Search audit logs by entity name
     * 
     * @param searchText the text to search for
     * @return the ResponseEntity with status 200 (OK) and the list of matching audit logs
     */
    @GetMapping("/search/entity-name")
    public ResponseEntity<List<GlobalConfigurationAuditLog>> searchAuditLogsByEntityName(@RequestParam String searchText) {
        log.debug("REST request to search audit logs by entity name : {}", searchText);
        return ResponseEntity.ok(auditService.searchAuditLogsByEntityName(searchText));
    }

    /**
     * GET /api/v1/audit-logs/search/details : Search audit logs by details
     * 
     * @param searchText the text to search for
     * @return the ResponseEntity with status 200 (OK) and the list of matching audit logs
     */
    @GetMapping("/search/details")
    public ResponseEntity<List<GlobalConfigurationAuditLog>> searchAuditLogsByDetails(@RequestParam String searchText) {
        log.debug("REST request to search audit logs by details : {}", searchText);
        return ResponseEntity.ok(auditService.searchAuditLogsByDetails(searchText));
    }
}
