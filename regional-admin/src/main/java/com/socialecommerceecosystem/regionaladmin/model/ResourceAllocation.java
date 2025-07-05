package com.gogidix.courier.regionaladmin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing a resource allocation in the Regional Admin application.
 * Resource allocations are created at HQ Admin and synchronized to Regional Admin.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Entity
@Table(name = "resource_allocations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "allocation_id", nullable = false, unique = true)
    private Long allocationId;

    @Column(name = "resource_pool_id", nullable = false)
    private Long resourcePoolId;
    
    @Column(name = "resource_type", nullable = false)
    private String resourceType;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "allocated_at")
    private LocalDateTime allocatedAt;
    
    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;
    
    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
    
    @Column(name = "notes")
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AllocationStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Pre-persist hook to set createdAt and updatedAt.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Pre-update hook to set updatedAt.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}