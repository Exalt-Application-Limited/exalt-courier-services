package com.gogidix.courier.location.model;

/**
 * Represents the payment methods available for walk-in customers
 * at physical courier network locations.
 */
public enum PaymentMethod {
    /**
     * Payment made in cash.
     */
    CASH("Cash"),
    
    /**
     * Payment made using a credit card.
     */
    CREDIT_CARD("Credit Card"),
    
    /**
     * Payment made using a debit card.
     */
    DEBIT_CARD("Debit Card"),
    
    /**
     * Payment made through mobile wallet or payment app.
     */
    MOBILE_PAYMENT("Mobile Payment"),
    
    /**
     * Payment by check.
     */
    CHECK("Check"),
    
    /**
     * Payment through bank transfer.
     */
    BANK_TRANSFER("Bank Transfer"),
    
    /**
     * Payment charged to a corporate account.
     */
    CORPORATE_ACCOUNT("Corporate Account"),
    
    /**
     * Payment to be collected from the recipient (Cash on Delivery).
     */
    COD("Cash on Delivery"),
    
    /**
     * Prepaid shipping label or credit.
     */
    PREPAID("Prepaid"),
    
    /**
     * Payment processed through a third-party payment processor.
     */
    PAYMENT_PROCESSOR("Payment Processor"),
    
    /**
     * No payment required (e.g., return shipping).
     */
    FREE("Free");
    
    private final String displayName;
    
    /**
     * Constructor for PaymentMethod enum.
     * 
     * @param displayName The human-readable name for the payment method
     */
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the payment method.
     * 
     * @return The human-readable name for UI display
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Checks if this payment method is considered immediate payment.
     * 
     * @return true if payment is collected immediately, false otherwise
     */
    public boolean isImmediatePayment() {
        return this == CASH || this == CREDIT_CARD || this == DEBIT_CARD || 
               this == MOBILE_PAYMENT || this == PREPAID;
    }
    
    /**
     * Checks if this payment method requires additional verification.
     * 
     * @return true if additional verification is needed, false otherwise
     */
    public boolean requiresVerification() {
        return this == CREDIT_CARD || this == DEBIT_CARD || 
               this == BANK_TRANSFER || this == CHECK;
    }
    
    /**
     * Checks if this payment method is electronic (non-cash).
     * 
     * @return true if electronic payment, false otherwise
     */
    public boolean isElectronic() {
        return this != CASH && this != CHECK && this != COD;
    }
    
    /**
     * Checks if this payment method is delayed (payment happens later).
     * 
     * @return true if payment is delayed, false otherwise
     */
    public boolean isDelayedPayment() {
        return this == COD || this == CORPORATE_ACCOUNT || this == BANK_TRANSFER;
    }
}
