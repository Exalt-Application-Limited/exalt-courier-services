package com.gogidix.courier.courier.hqadmin.model.policy;

/**
 * Enum representing the different types of policies within the courier services domain.
 */
public enum PolicyType {
    SHIPPING_RATE("Shipping Rate Policy"),
    DELIVERY_SLA("Delivery Service Level Agreement"),
    REFUND("Refund Policy"),
    CUSTOMS_DECLARATION("Customs Declaration Policy"),
    PROHIBITED_ITEMS("Prohibited Items Policy"),
    PACKAGING("Packaging Guidelines"),
    INSURANCE("Insurance Policy"),
    DRIVER_CONDUCT("Driver Conduct Policy"),
    COMMISSION_STRUCTURE("Commission Structure Policy"),
    DATA_PRIVACY("Data Privacy Policy"),
    CUSTOMER_SERVICE("Customer Service Policy"),
    DISPUTE_RESOLUTION("Dispute Resolution Policy"),
    CANCELLATION("Cancellation Policy"),
    ENVIRONMENTAL("Environmental Policy"),
    SECURITY("Security Policy");

    private final String displayName;

    PolicyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
