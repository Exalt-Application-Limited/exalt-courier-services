package com.exalt.courier.hqadmin.dto.policy;

import com.socialecommerceecosystem.hqadmin.model.policy.PolicyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for policy creation and updates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDTO {

    private Long id;
    
    @NotBlank
    private String policyKey;
    
    @NotBlank
    private String name;
    
    private String description;
    
    @NotBlank
    private String policyContent;
    
    @NotNull
    private PolicyType policyType;
    
    @NotNull
    private Boolean isActive;
    
    @NotNull
    private Boolean isMandatory;
    
    private LocalDateTime effectiveDate;
    
    private LocalDateTime expirationDate;
    
    private Long globalRegionId;
    
    private String versionNumber;
    
    private String lastUpdatedBy;
}
