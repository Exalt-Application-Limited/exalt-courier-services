package com.exalt.courier.hqadmin.dto.policy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for policy rejection actions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyRejectionDTO {

    @NotBlank
    private String rejectedBy;
    
    @NotBlank
    private String rejectionReason;
}
