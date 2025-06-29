package com.exalt.courier.tracking.util;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class providing common fields and functionality for all tracking service entities.
 * Uses UUID as String for better cloud compatibility and standardized across the ecosystem.
 */
@Data
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Helper method to get ID as UUID for compatibility with legacy code
     * 
     * @return UUID representation of the string ID
     */
    public UUID getIdAsUUID() {
        return id != null ? id instanceof String ? UUID.fromString((String)id) : (UUID)id : null;
    }

    /**
     * Helper method to set ID from UUID for compatibility with legacy code
     * 
     * @param uuid UUID to convert to string ID
     */
    public void setIdFromUUID(UUID uuid) {
        this.id = uuid != null ? uuid.toString() : null;
    }
}