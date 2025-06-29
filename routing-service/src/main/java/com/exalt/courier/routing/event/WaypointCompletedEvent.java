package com.exalt.courier.routing.event;

import com.exalt.courier.routing.model.Location;
import com.exalt.courier.routing.model.Waypoint;
import com.exalt.courier.routing.model.WaypointStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Event published when a waypoint is visited or completed.
 * This event is consumed by the Tracking Service to update package statuses.
 * 
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaypointCompletedEvent {
    
    private Long waypointId;
    private Long routeId;
    private String courierId;
    private String packageId;
    private WaypointStatus status;
    private LocalDateTime completionTime;
    private Location location;
    private String notes;
    
    /**
     * Factory method to create a new waypoint completed event
     *
     * @param waypoint the completed waypoint
     * @param routeId the ID of the route
     * @param courierId the ID of the courier
     * @param notes any notes added by the courier
     * @return the created event
     */
    public static WaypointCompletedEvent from(Waypoint waypoint, Long routeId, String courierId, String notes) {
        return WaypointCompletedEvent.builder()
                .waypointId(waypoint.getId())
                .routeId(routeId)
                .courierId(courierId)
                .packageId(waypoint.getPackageId())
                .status(waypoint.getStatus())
                .completionTime(LocalDateTime.now())
                .location(waypoint.getLocation())
                .notes(notes)
                .build();
    }
}