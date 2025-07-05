package com.gogidix.courierservices.management.$1;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity for courier management domain following com.exalt standards.
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
    
    // Helper method for UUID conversion
    public UUID getIdAsUUID() {
        return id != null ? id instanceof String ? UUID.fromString((String)id) : (UUID)id : null;
    }
    // Additional shared fields for courier domain can be added here.
}
