package com.exalt.courier.hqadmin.model;

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
 * Entity for tracking changes to global service configurations.
 * Stores history of all configuration changes for audit and rollback purposes.
 */
@Data
@Entity
@Table(name = "config_audit_logs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_id")
    private Long configId;

    @NotBlank
    @Column(name = "service_key", nullable = false)
    private String serviceKey;

    @Column(name = "previous_value", columnDefinition = "TEXT")
    private String previousValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @NotBlank
    @Column(name = "change_type", nullable = false)
    private String changeType;  // CREATE, UPDATE, DELETE

    @Column(name = "global_region_id")
    private Long globalRegionId;

    @Column(name = "global_region_name")
    private String globalRegionName;

    @NotBlank
    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    @Column(name = "change_reason")
    private String changeReason;

    @NotNull
    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @Column(name = "additional_metadata", columnDefinition = "TEXT")
    private String additionalMetadata;
}
