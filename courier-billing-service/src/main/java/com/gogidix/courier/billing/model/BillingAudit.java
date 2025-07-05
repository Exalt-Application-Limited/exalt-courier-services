package com.gogidix.courier.billing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing billing audit trail.
 * 
 * @author Exalt Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "billing_audit", indexes = {
    @Index(name = "idx_entity_id", columnList = "entity_id"),
    @Index(name = "idx_entity_type", columnList = "entity_type"),
    @Index(name = "idx_action", columnList = "action"),
    @Index(name = "idx_performed_at", columnList = "performed_at"),
    @Index(name = "idx_performed_by", columnList = "performed_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "entity_id", nullable = false)
    @NotNull(message = "Entity ID is required")
    private UUID entityId;

    @Column(name = "entity_type", nullable = false)
    @NotBlank(message = "Entity type is required")
    private String entityType;

    @Column(name = "action", nullable = false)
    @NotBlank(message = "Action is required")
    private String action;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "old_values", columnDefinition = "TEXT")
    private String oldValues;

    @Column(name = "new_values", columnDefinition = "TEXT")
    private String newValues;

    @Column(name = "performed_by", nullable = false)
    @NotBlank(message = "Performed by is required")
    private String performedBy;

    @Column(name = "performed_at", nullable = false)
    @Builder.Default
    private LocalDateTime performedAt = LocalDateTime.now();

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "session_id")
    private String sessionId;
}