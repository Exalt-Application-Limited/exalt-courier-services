package com.exalt.courier.location.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.JsonType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

/**
 * Represents a shipment created by a walk-in customer at a physical courier location.
 * This entity tracks all shipment details including recipient information, package details,
 * tracking, and payment information.
 */
@Entity
@Table(name = "walk_in_shipments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "json", typeClass = JsonType.class)
public class WalkInShipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private WalkInCustomer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physical_location_id", nullable = false)
    private PhysicalLocation origin;

    @Column(name = "tracking_number", nullable = false, unique = true)
    private String trackingNumber;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false)
    private String recipientPhone;

    private String recipientEmail;

    @Column(name = "recipient_address", nullable = false)
    private String recipientAddress;

    @Column(name = "recipient_city", nullable = false)
    private String recipientCity;

    @Column(name = "recipient_state")
    private String recipientState;

    @Column(name = "recipient_country", nullable = false)
    private String recipientCountry;

    @Column(name = "recipient_zip_code")
    private String recipientZipCode;

    @Column(nullable = false)
    private Double weight;

    @Column(name = "weight_unit", nullable = false)
    private String weightUnit;

    private Double length;
    private Double width;
    private Double height;

    @Column(name = "dimension_unit")
    private String dimensionUnit;

    @Column(name = "declared_value")
    private BigDecimal declaredValue;

    @Column(name = "insurance_amount")
    private BigDecimal insuranceAmount;

    @Column(name = "shipping_cost", nullable = false)
    private BigDecimal shippingCost;

    @Column(name = "insurance_cost")
    private BigDecimal insuranceCost;

    @Column(name = "tax_amount")
    private BigDecimal taxAmount;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "service_type", nullable = false)
    private String serviceType;

    @Column(name = "special_instructions")
    private String specialInstructions;

    @Column(name = "is_international")
    private boolean international;

    @Type(type = "json")
    @Column(columnDefinition = "jsonb")
    private String additionalDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_staff_id")
    private LocationStaff handledByStaff;

    /**
     * Updates the status of this shipment.
     * 
     * @param newStatus the new status to set
     */
    public void updateStatus(ShipmentStatus newStatus) {
        this.status = newStatus;
    }

    /**
     * Calculates the volume of the package in cubic units.
     * 
     * @return the volume or null if dimensions are not complete
     */
    @Transient
    public Double getVolume() {
        if (length == null || width == null || height == null) {
            return null;
        }
        return length * width * height;
    }

    /**
     * Gets the additional details as a JsonNode object.
     * 
     * @return JsonNode representation of additional details
     */
    @Transient
    public JsonNode getAdditionalDetailsAsJson() {
        try {
            if (additionalDetails == null || additionalDetails.isEmpty()) {
                return new ObjectMapper().createObjectNode();
            }
            return new ObjectMapper().readTree(additionalDetails);
        } catch (Exception e) {
            return new ObjectMapper().createObjectNode();
        }
    }

    /**
     * Checks if this shipment is eligible for SMS notifications.
     * 
     * @return true if SMS notifications can be sent, false otherwise
     */
    @Transient
    public boolean canSendSmsNotifications() {
        return recipientPhone != null && !recipientPhone.isEmpty();
    }

    /**
     * Checks if this shipment is eligible for email notifications.
     * 
     * @return true if email notifications can be sent, false otherwise
     */
    @Transient
    public boolean canSendEmailNotifications() {
        return recipientEmail != null && !recipientEmail.isEmpty();
    }

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (status == null) {
            status = ShipmentStatus.CREATED;
        }
    }
}
