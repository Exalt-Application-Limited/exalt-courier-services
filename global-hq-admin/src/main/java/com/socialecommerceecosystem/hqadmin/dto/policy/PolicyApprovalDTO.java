package com.gogidix.courier.hqadmin.dto.policy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for policy approval actions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyApprovalDTO {

    @NotBlank
    private String approvedBy;
}
