package com.gogidix.courier.location.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a walk-in customer who visits physical courier locations
 * for shipping or pickup services without using the mobile app or being
 * registered in the social network platform.
 */
@Entity
@Table(name = "walk_in_customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkInCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phone;

    private String email;

    @Column(nullable = false)
    private String address;

    private String city;

    private String state;

    private String country;

    private String zipCode;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_visit")
    private LocalDateTime lastVisit;

    @Column(name = "visit_count")
    private Integer visitCount = 0;

    private String notes;

    @Column(name = "marketing_consent")
    private boolean marketingConsent = false;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private Set<WalkInShipment> shipments = new HashSet<>();

    /**
     * Full name of the customer.
     * 
     * @return concatenated first and last name
     */
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Records a new visit by this customer.
     * Updates the last visit timestamp and increments the visit count.
     */
    public void recordVisit() {
        this.lastVisit = LocalDateTime.now();
        this.visitCount = (this.visitCount == null ? 0 : this.visitCount) + 1;
    }

    /**
     * Adds a new shipment for this customer.
     * 
     * @param shipment the shipment to add
     */
    public void addShipment(WalkInShipment shipment) {
        shipments.add(shipment);
        shipment.setCustomer(this);
    }

    /**
     * Removes a shipment from this customer.
     * 
     * @param shipment the shipment to remove
     */
    public void removeShipment(WalkInShipment shipment) {
        shipments.remove(shipment);
        shipment.setCustomer(null);
    }

    /**
     * Checks if this customer is a frequent visitor.
     * A customer is considered frequent if they have visited 5 or more times.
     * 
     * @return true if this is a frequent customer, false otherwise
     */
    @Transient
    public boolean isFrequentCustomer() {
        return visitCount != null && visitCount >= 5;
    }

    /**
     * Checks if this customer has visited recently.
     * A customer is considered recent if their last visit was within 30 days.
     * 
     * @return true if this customer has visited recently, false otherwise
     */
    @Transient
    public boolean hasRecentVisit() {
        if (lastVisit == null) {
            return false;
        }
        return lastVisit.isAfter(LocalDateTime.now().minusDays(30));
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (lastVisit == null) {
            lastVisit = LocalDateTime.now();
        }
        if (visitCount == null) {
            visitCount = 1;
        }
    }
}
