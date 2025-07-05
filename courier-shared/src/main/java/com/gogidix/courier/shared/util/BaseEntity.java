package com.gogidix.courier.shared.util;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class providing common fields for all courier domain entities.
 * Uses UUID as String for cross-service compatibility.
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
     * Get the ID as a UUID object.
     * @return UUID representation of the ID, or null if ID is null
     */
    public UUID getIdAsUUID() {
        return id != null ? UUID.fromString(id) : null;
    }

    /**
     * Set the ID from a UUID object.
     * @param uuid the UUID to set as ID
     */
    public void setIdFromUUID(UUID uuid) {
        this.id = uuid != null ? uuid.toString() : null;
    }
}