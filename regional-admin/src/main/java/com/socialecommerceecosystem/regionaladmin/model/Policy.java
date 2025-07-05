package com.gogidix.courier.regionaladmin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing a policy in the Regional Admin application.
 * Policies are created at HQ Admin and synchronized to Regional Admin.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Entity
@Table(name = "policies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_id", nullable = false, unique = true)
    private Long policyId;

    @Column(name = "policy_type", nullable = false)
    private String policyType;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "priority")
    private Integer priority;
    
    @Column(name = "effective_from")
    private LocalDateTime effectiveFrom;
    
    @Column(name = "effective_to")
    private LocalDateTime effectiveTo;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PolicyStatus status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Pre-persist hook to set createdAt.
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
