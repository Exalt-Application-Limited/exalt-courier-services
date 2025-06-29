package com.exalt.courier.hqadmin.model.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing an audit log for configuration changes.
 */
@Data
@Entity
@Table(name = "global_configuration_audit_logs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalConfigurationAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "component", nullable = false)
    private String component;
    
    @NotBlank
    @Column(name = "action", nullable = false)
    private String action;
    
    @Column(name = "entity_id")
    private String entityId;
    
    @Column(name = "entity_type")
    private String entityType;
    
    @Column(name = "entity_name")
    private String entityName;
    
    @Column(name = "field_name")
    private String fieldName;
    
    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;
    
    @NotBlank
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(name = "region_code")
    private String regionCode;
    
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
    
    @NotNull
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
