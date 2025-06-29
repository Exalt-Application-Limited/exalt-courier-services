package com.exalt.courierservices.tracking.$1;

import com.exalt.courierservices.tracking.util.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Entity representing a tracking event in the package delivery lifecycle.
 * Enhanced with Lombok annotations to reduce boilerplate code.
 */
@Entity
@Table(name = "tracking_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package pack;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TrackingStatus status;

    @NotBlank
    @Size(max = 500)
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @Column(name = "location")
    private String location;

    @Column(name = "latitude")
    private Double latitude;    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "courier_id")
    private Long courierId;

    @Column(name = "facility_id")
    private Long facilityId;

    @Column(name = "scan_type")
    private String scanType;

    @Column(name = "notes")
    @Size(max = 1000)
    private String notes;

    /**
     * Creates a new basic tracking event.
     */
    public static TrackingEvent createBasicEvent(Package pack, TrackingStatus status, 
                                                String description, LocalDateTime eventTime) {
        return TrackingEvent.builder()
                .pack(pack)
                .status(status)
                .description(description)
                .eventTime(eventTime)
                .build();
    }    /**
     * Creates a new tracking event with location information.
     */
    public static TrackingEvent createWithLocation(Package pack, TrackingStatus status, String description, 
                                                 LocalDateTime eventTime, String location, 
                                                 Double latitude, Double longitude) {
        return TrackingEvent.builder()
                .pack(pack)
                .status(status)
                .description(description)
                .eventTime(eventTime)
                .location(location)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }

    /**
     * Creates a new tracking event with courier information.
     */
    public static TrackingEvent createWithCourier(Package pack, TrackingStatus status, String description, 
                                                LocalDateTime eventTime, String location, Long courierId) {
        return TrackingEvent.builder()
                .pack(pack)
                .status(status)
                .description(description)
                .eventTime(eventTime)
                .location(location)
                .courierId(courierId)
                .build();
    }    /**
     * Creates a new tracking event with facility information.
     */
    public static TrackingEvent createWithFacility(Package pack, TrackingStatus status, String description, 
                                                 LocalDateTime eventTime, String location, 
                                                 Long facilityId, String scanType) {
        return TrackingEvent.builder()
                .pack(pack)
                .status(status)
                .description(description)
                .eventTime(eventTime)
                .location(location)
                .facilityId(facilityId)
                .scanType(scanType)
                .build();
    }

    /**
     * Determines if this event represents a location change.
     */
    public boolean isLocationChange() {
        return location != null && !location.isEmpty();
    }

    /**
     * Determines if this event has geolocation data.
     */
    public boolean hasGeolocation() {
        return latitude != null && longitude != null;
    }

    /**
     * Determines if this event is a courier event.
     */
    public boolean isCourierEvent() {
        return courierId != null;
    }

    /**
     * Determines if this event is a facility event.
     */
    public boolean isFacilityEvent() {
        return facilityId != null;
    }
}