package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import com.exalt.courier.management.util.Location;

/**
 * Represents a task within an assignment.
 * A task can be a pickup, delivery, or any other action that needs to be performed
 * by a courier as part of an assignment.
 */
@Entity
@Table(name = "assignment_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentTask extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @NotNull(message = "Task type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    @NotNull(message = "Task status cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @NotNull(message = "Task sequence cannot be null")
    @Column(name = "sequence", nullable = false)
    private Integer sequence;

    @Size(min = 1, max = 255, message = "Address must be between 1 and 255 characters")
    @Column(name = "address", nullable = false)
    private String address;

    @Size(max = 100, message = "Address line 2 must be less than 100 characters")
    @Column(name = "address_line2")
    private String addressLine2;

    @Size(max = 100, message = "City must be less than 100 characters")
    @Column(name = "city")
    private String city;

    @Size(max = 20, message = "Postal code must be less than 20 characters")
    @Column(name = "postal_code")
    private String postalCode;

    @Size(max = 100, message = "State/province must be less than 100 characters")
    @Column(name = "state_province")
    private String stateProvince;

    @Size(max = 100, message = "Country must be less than 100 characters")
    @Column(name = "country")
    private String country;

    @NotNull(message = "Latitude cannot be null")
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;

    @Column(name = "actual_duration_minutes")
    private Integer actualDurationMinutes;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    @Size(max = 255, message = "Contact name must be less than 255 characters")
    @Column(name = "contact_name")
    private String contactName;

    @Size(max = 50, message = "Contact phone must be less than 50 characters")
    @Column(name = "contact_phone")
    private String contactPhone;

    @Size(max = 500, message = "Instructions must be less than 500 characters")
    @Column(name = "instructions", length = 500)
    private String instructions;

    @Column(name = "time_window_start")
    private LocalDateTime timeWindowStart;

    @Column(name = "time_window_end")
    private LocalDateTime timeWindowEnd;

    @Size(max = 255, message = "Reference code must be less than 255 characters")
    @Column(name = "reference_code")
    private String referenceCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Update the status of the task
     * 
     * @param newStatus the new status to set
     * @return the task instance for method chaining
     */
    public AssignmentTask updateStatus(TaskStatus newStatus) {
        this.status = newStatus;
        
        if (newStatus == TaskStatus.COMPLETED) {
            this.completedTime = LocalDateTime.now();
            if (this.scheduledTime != null) {
                this.actualDurationMinutes = calculateDurationInMinutes(this.scheduledTime, this.completedTime);
            }
        }
        
        return this;
    }
    
    /**
     * Calculate duration in minutes between two timestamps
     * 
     * @param start the start time
     * @param end the end time
     * @return the duration in minutes
     */
    private int calculateDurationInMinutes(LocalDateTime start, LocalDateTime end) {
        return (int) java.time.Duration.between(start, end).toMinutes();
    }
    
    /**
     * Checks if the task is overdue based on the scheduled time
     * 
     * @return true if the task is overdue, false otherwise
     */
    public boolean isOverdue() {
        return scheduledTime != null && 
               LocalDateTime.now().isAfter(scheduledTime) && 
               !status.isTerminal();
    }
    
    /**
     * Gets the start time window for backward compatibility
     * 
     * @return the start time window
     */
    public LocalDateTime getStartTimeWindow() {
        return timeWindowStart;
    }
    
    /**
     * Gets the end time window for backward compatibility
     * 
     * @return the end time window
     */
    public LocalDateTime getEndTimeWindow() {
        return timeWindowEnd;
    }
    
    /**
     * Gets the estimated duration for backward compatibility
     * 
     * @return the estimated duration in minutes
     */
    public Integer getEstimatedDuration() {
        return estimatedDurationMinutes;
    }
    
    /**
     * Sets the sequence number for backward compatibility
     * 
     * @param sequenceNumber the sequence number to set
     */
    public void setSequenceNumber(int sequenceNumber) {
        this.sequence = sequenceNumber;
    }
    
    /**
     * Gets the sequence number for backward compatibility
     * 
     * @return the sequence number
     */
    public int getSequenceNumber() {
        return sequence != null ? sequence : 0;
    }
    
    /**
     * Gets the location as a Location object
     * 
     * @return Location object with latitude and longitude
     */
    public Location getLocation() {
        if (latitude != null && longitude != null) {
            return Location.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
        }
        return null;
    }
    
    /**
     * Sets the location from a Location object
     * 
     * @param location the location to set
     */
    public void setLocation(Location location) {
        if (location != null) {
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        } else {
            this.latitude = null;
            this.longitude = null;
        }
    }
    
    /**
     * Gets the task title for backward compatibility
     * 
     * @return the task title (based on task type and address)
     */
    public String getTitle() {
        return taskType != null ? taskType.name() + " - " + (address != null ? address : "Unknown location") : "Unknown task";
    }
    
    /**
     * Gets the task description for backward compatibility
     * 
     * @return the task description
     */
    public String getDescription() {
        return notes != null ? notes : "No description available";
    }
    
    /**
     * Gets the task type for backward compatibility
     * 
     * @return the task type
     */
    public TaskType getType() {
        return taskType;
    }
    
    /**
     * Sets the start time window for backward compatibility
     * 
     * @param startTime the start time to set
     */
    public void setStartTimeWindow(LocalDateTime startTime) {
        this.timeWindowStart = startTime;
    }
    
    /**
     * Sets the end time window for backward compatibility
     * 
     * @param endTime the end time to set
     */
    public void setEndTimeWindow(LocalDateTime endTime) {
        this.timeWindowEnd = endTime;
    }
    
    /**
     * Sets the estimated duration for backward compatibility
     * 
     * @param duration the estimated duration in minutes
     */
    public void setEstimatedDuration(Integer duration) {
        this.estimatedDurationMinutes = duration;
    }
    
    /**
     * Gets additional data (mock for backward compatibility)
     * 
     * @return empty map
     */
    public java.util.Map<String, Object> getAdditionalData() {
        return new java.util.HashMap<>();
    }
    
    /**
     * Sets the started timestamp for backward compatibility
     * 
     * @param startedAt the timestamp when task was started
     */
    public void setStartedAt(LocalDateTime startedAt) {
        this.scheduledTime = startedAt;
    }
    
    /**
     * Sets the completed timestamp for backward compatibility
     * 
     * @param completedAt the timestamp when task was completed
     */
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedTime = completedAt;
    }
    
    /**
     * Sets the failed timestamp for backward compatibility
     * 
     * @param failedAt the timestamp when task failed
     */
    public void setFailedAt(LocalDateTime failedAt) {
        this.completedTime = failedAt; // Use the same field for now
    }
    
    /**
     * Gets the scheduled time for backward compatibility
     * 
     * @return the scheduled time
     */
    public LocalDateTime getStartedAt() {
        return scheduledTime;
    }
    
    /**
     * Gets the completed time for backward compatibility
     * 
     * @return the completed time
     */
    public LocalDateTime getCompletedAt() {
        return completedTime;
    }
} 