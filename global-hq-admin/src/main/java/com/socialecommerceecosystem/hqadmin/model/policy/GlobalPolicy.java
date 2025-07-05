package com.gogidix.courier.hqadmin.model.policy;

import com.socialecommerceecosystem.hqadmin.model.GlobalRegion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Represents a global policy that defines rules and guidelines for courier operations.
 * Policies can be applied globally or to specific regions.
 */
@Data
@Entity
@Table(name = "global_policies")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "policy_key", nullable = false, unique = true)
    private String policyKey;
    
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(name = "policy_content", columnDefinition = "TEXT", nullable = false)
    private String policyContent;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "policy_type", nullable = false)
    private PolicyType policyType;
    
    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @NotNull
    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory;
    
    @Column(name = "effective_date")
    private LocalDateTime effectiveDate;
    
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "global_region_id")
    private GlobalRegion globalRegion;
    
    @Column(name = "version_number")
    private String versionNumber;
    
    @Column(name = "last_updated_by")
    private String lastUpdatedBy;
    
    @Column(name = "approval_status")
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @NotNull
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @NotNull
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Version
    private Integer version;
}
