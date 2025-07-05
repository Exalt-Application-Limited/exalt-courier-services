package com.gogidix.courier.routing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.gogidix.courier.routing.util.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an optimized delivery route with multiple stops.
 */
@Entity
@Table(name = "routes")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route extends BaseEntity {
    
    @Column(name = "courier_id")
    private String courierId;
    
    @Column(name = "vehicle_id")
    private String vehicleId;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "estimated_duration_minutes")
    private Integer estimatedDurationMinutes;
    
    @Column(name = "total_distance_km")
    private Double totalDistanceKm;
    
    @Column(name = "status")
    private RouteStatus status;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "route_waypoints",
        joinColumns = @JoinColumn(name = "route_id"),
        inverseJoinColumns = @JoinColumn(name = "waypoint_id")
    )
    private List<Waypoint> waypoints = new ArrayList<>();
    
    public void addWaypoint(Waypoint waypoint) {
        waypoints.add(waypoint);
        recalculateRouteMetrics();
    }
    
    public void clearWaypoints() {
        waypoints.clear();
        estimatedDurationMinutes = 0;
        totalDistanceKm = 0.0;
    }
    
    public void recalculateRouteMetrics() {
        if (waypoints.size() < 2) {
            estimatedDurationMinutes = 0;
            totalDistanceKm = 0.0;
            return;
        }
        
        double totalDistance = 0.0;
        int totalDuration = 0;
        
        // Calculate distance and estimated time between each consecutive waypoint
        for (int i = 0; i < waypoints.size() - 1; i++) {
            Waypoint current = waypoints.get(i);
            Waypoint next = waypoints.get(i + 1);
            
            double distance = current.getLocation().distanceTo(next.getLocation());
            totalDistance += distance;
            
            // Estimate time based on distance (assume average speed of 30 km/h)
            int estimatedMinutes = (int) (distance / 30.0 * 60);
            totalDuration += estimatedMinutes;
            
            // Add stop duration at each waypoint
            totalDuration += current.getEstimatedStopDurationMinutes();
        }
        
        // Add the last waypoint's stop duration
        if (!waypoints.isEmpty()) {
            totalDuration += waypoints.get(waypoints.size() - 1).getEstimatedStopDurationMinutes();
        }
        
        this.totalDistanceKm = totalDistance;
        this.estimatedDurationMinutes = totalDuration;
        
        // Calculate end time if start time is set
        if (startTime != null) {
            this.endTime = startTime.plus(Duration.ofMinutes(totalDuration));
        }
    }
    
    public void sortWaypointsBySequence() {
        waypoints.sort((w1, w2) -> w1.getSequence() - w2.getSequence());
    }
} 
