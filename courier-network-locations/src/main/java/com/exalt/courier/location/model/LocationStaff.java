package com.exalt.courier.location.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a staff member assigned to a physical courier network location.
 * Includes roles, contact information, and assignment details.
 */
@Entity
@Table(name = "location_staff")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_location_id", nullable = false)
    private PhysicalLocation physicalLocation;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StaffRole role;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "assignment_date")
    private LocalDateTime assignmentDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    /**
     * Full name of the staff member.
     * 
     * @return concatenated first and last name
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Checks if the staff member is currently assigned to the location.
     * 
     * @return true if the staff member is active and not past end date
     */
    @Transient
    public boolean isCurrentlyAssigned() {
        if (!active) {
            return false;
        }
        
        if (endDate == null) {
            return true;
        }
        
        return LocalDateTime.now().isBefore(endDate);
    }

    /**
     * Updates the role of the staff member.
     * 
     * @param newRole the new role to assign
     */
    public void updateRole(StaffRole newRole) {
        this.role = newRole;
    }

    /**
     * Ends the staff assignment to this location.
     * 
     * @param endDateTime the date and time when the assignment ends
     */
    public void endAssignment(LocalDateTime endDateTime) {
        this.active = false;
        this.endDate = endDateTime;
    }

    @PrePersist
    protected void onCreate() {
        if (assignmentDate == null) {
            assignmentDate = LocalDateTime.now();
        }
        
        if (active && endDate != null && LocalDateTime.now().isAfter(endDate)) {
            active = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (active && endDate != null && LocalDateTime.now().isAfter(endDate)) {
            active = false;
        }
    }
}
