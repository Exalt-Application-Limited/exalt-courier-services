package com.gogidix.courier.location.model;

/**
 * Represents the role or position of a staff member at a courier network location.
 * Different roles have different responsibilities and access permissions.
 */
public enum StaffRole {
    /**
     * Manager responsible for overall location operations.
     */
    LOCATION_MANAGER("Location Manager", true, true, true),
    
    /**
     * Assistant manager with elevated permissions.
     */
    ASSISTANT_MANAGER("Assistant Manager", true, true, true),
    
    /**
     * Front desk staff handling customer service.
     */
    CUSTOMER_SERVICE_REPRESENTATIVE("Customer Service Representative", false, true, false),
    
    /**
     * Staff handling package logistics.
     */
    LOGISTICS_OFFICER("Logistics Officer", false, false, true),
    
    /**
     * Staff focused on sorting and organizing packages.
     */
    SORTING_STAFF("Sorting Staff", false, false, false),
    
    /**
     * Temporary or probationary staff member.
     */
    TRAINEE("Trainee", false, false, false),
    
    /**
     * Security personnel for the location.
     */
    SECURITY_OFFICER("Security Officer", false, false, false),
    
    /**
     * Administrative staff handling paperwork.
     */
    ADMINISTRATIVE_STAFF("Administrative Staff", false, true, false),
    
    /**
     * Technical support staff for systems.
     */
    TECHNICAL_SUPPORT("Technical Support", false, false, true);
    
    private final String displayName;
    private final boolean canManageStaff;
    private final boolean canHandlePayments;
    private final boolean canAccessInventory;
    
    /**
     * Constructor for StaffRole enum.
     * 
     * @param displayName The human-readable name for the role
     * @param canManageStaff Whether this role can manage other staff
     * @param canHandlePayments Whether this role can handle customer payments
     * @param canAccessInventory Whether this role can access and modify inventory
     */
    StaffRole(String displayName, boolean canManageStaff, boolean canHandlePayments, boolean canAccessInventory) {
        this.displayName = displayName;
        this.canManageStaff = canManageStaff;
        this.canHandlePayments = canHandlePayments;
        this.canAccessInventory = canAccessInventory;
    }
    
    /**
     * Gets the display name of the staff role.
     * 
     * @return The human-readable name for UI display
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this role can manage other staff members.
     * 
     * @return true if staff management is allowed, false otherwise
     */
    public boolean canManageStaff() {
        return canManageStaff;
    }
    
    /**
     * Checks if this role can handle customer payments.
     * 
     * @return true if payment handling is allowed, false otherwise
     */
    public boolean canHandlePayments() {
        return canHandlePayments;
    }
    
    /**
     * Checks if this role can access and modify inventory.
     * 
     * @return true if inventory access is allowed, false otherwise
     */
    public boolean canAccessInventory() {
        return canAccessInventory;
    }
    
    /**
     * Checks if this role is a management role.
     * 
     * @return true if this is a management position, false otherwise
     */
    public boolean isManagement() {
        return this == LOCATION_MANAGER || this == ASSISTANT_MANAGER;
    }
}
