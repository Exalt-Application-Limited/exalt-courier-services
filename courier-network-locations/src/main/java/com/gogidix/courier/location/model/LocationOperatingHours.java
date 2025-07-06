package com.gogidix.courier.location.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the operating hours for a physical courier network location.
 * Each location can have different operating hours for different days of the week.
 */
@Entity
@Table(name = "location_operating_hours")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationOperatingHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_location_id", nullable = false)
    private PhysicalLocation physicalLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "is_closed")
    private boolean isClosed;

    @Column(name = "special_hours")
    private boolean specialHours;

    @Column(name = "special_description")
    private String specialDescription;

    /**
     * Checks if the location is open at the specified time on this day.
     * 
     * @param time The time to check
     * @return true if the location is open, false otherwise
     */
    public boolean isOpenAt(LocalTime time) {
        if (isClosed) {
            return false;
        }
        
        // If time is exactly at opening or closing time, consider it open
        return !time.isBefore(openingTime) && !time.isAfter(closingTime);
    }

    /**
     * Gets the operating duration in hours for this day.
     * 
     * @return the number of hours the location is open, or 0 if closed
     */
    public double getOperatingDuration() {
        if (isClosed) {
            return 0.0;
        }
        
        int openingMinutes = openingTime.getHour() * 60 + openingTime.getMinute();
        int closingMinutes = closingTime.getHour() * 60 + closingTime.getMinute();
        
        // Handle case where closing time is next day (e.g., 1:00 AM)
        if (closingMinutes < openingMinutes) {
            closingMinutes += 24 * 60; // Add 24 hours
        }
        
        return (closingMinutes - openingMinutes) / 60.0;
    }


}
