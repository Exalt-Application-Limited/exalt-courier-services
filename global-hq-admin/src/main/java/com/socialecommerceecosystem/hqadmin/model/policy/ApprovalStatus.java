package com.gogidix.courier.hqadmin.model.policy;

/**
 * Enum representing the possible approval statuses for policies.
 */
public enum ApprovalStatus {
    DRAFT("Draft"),
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    EXPIRED("Expired"),
    SUPERSEDED("Superseded");

    private final String displayName;

    ApprovalStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
