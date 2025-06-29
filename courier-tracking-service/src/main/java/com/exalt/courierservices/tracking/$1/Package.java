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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a package being tracked in the courier system.
 * Enhanced with Lombok annotations to reduce boilerplate code.
 */
@Entity
@Table(name = "packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Package extends BaseEntity {

    @NotBlank
    @Size(min = 10, max = 30)
    @Column(name = "tracking_number", unique = true, nullable = false)
    private String trackingNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TrackingStatus status = TrackingStatus.CREATED;

    @NotBlank
    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @NotBlank
    @Column(name = "sender_address", nullable = false)
    private String senderAddress;

    @NotBlank
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;    @NotBlank
    @Column(name = "recipient_address", nullable = false)
    private String recipientAddress;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @NotNull
    @Column(name = "estimated_delivery_date", nullable = false)
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "actual_delivery_date")
    private LocalDateTime actualDeliveryDate;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "dimensions")
    private String dimensions;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "courier_id")
    private Long courierId;

    @Column(name = "route_id")
    private Long routeId;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrackingEvent> events = new ArrayList<>();

    @Column(name = "signature_required")
    @Builder.Default
    private boolean signatureRequired = false;

    @Column(name = "signature_image")
    private String signatureImage;

    @Column(name = "delivery_instructions")
    @Size(max = 500)
    private String deliveryInstructions;

    @Column(name = "delivery_attempts")
    @Builder.Default
    private Integer deliveryAttempts = 0;    /**
     * Creates a new package with a generated tracking number.
     */
    public static Package createNewPackage(String senderName, String senderAddress, 
                                          String recipientName, String recipientAddress, 
                                          LocalDateTime estimatedDeliveryDate) {
        Package pkg = Package.builder()
                .trackingNumber(generateTrackingNumber())
                .senderName(senderName)
                .senderAddress(senderAddress)
                .recipientName(recipientName)
                .recipientAddress(recipientAddress)
                .estimatedDeliveryDate(estimatedDeliveryDate)
                .status(TrackingStatus.CREATED)
                .build();
        
        // Add initial tracking event
        pkg.addEvent(TrackingStatus.CREATED, "Package created in the system");
        return pkg;
    }

    /**
     * Generates a unique tracking number.
     */
    private static String generateTrackingNumber() {
        // Format: ECO-XXXXX-XXXXX (where X is alphanumeric)
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
        return "ECO-" + uuid.substring(0, 5) + "-" + uuid.substring(5, 10);
    }

    /**
     * Updates the package status and adds a tracking event.
     */
    public void updateStatus(TrackingStatus newStatus, String description) {
        this.status = newStatus;
        addEvent(newStatus, description);
    }    /**
     * Adds a tracking event to this package.
     */
    public void addEvent(TrackingStatus status, String description) {
        TrackingEvent event = TrackingEvent.createBasicEvent(this, status, description, LocalDateTime.now());
        this.events.add(event);
    }

    /**
     * Records a delivery attempt.
     */
    public void recordDeliveryAttempt(String description) {
        this.deliveryAttempts++;
        addEvent(TrackingStatus.DELIVERY_ATTEMPTED, description);
    }

    /**
     * Marks the package as delivered.
     */
    public void markDelivered(String description, String signatureImage) {
        this.status = TrackingStatus.DELIVERED;
        this.actualDeliveryDate = LocalDateTime.now();
        this.signatureImage = signatureImage;
        addEvent(TrackingStatus.DELIVERED, description);
    }
}