package com.gogidix.courierservices.payout.$1;

/**
 * Enum representing different types of payment methods.
 */
public enum PaymentMethod {
    /**
     * Direct bank transfer
     */
    BANK_TRANSFER,
    
    /**
     * Digital wallet (e.g., PayPal, Stripe, etc.)
     */
    DIGITAL_WALLET,
    
    /**
     * Mobile money
     */
    MOBILE_MONEY,
    
    /**
     * Prepaid card
     */
    PREPAID_CARD,
    
    /**
     * Credit to account balance
     */
    ACCOUNT_CREDIT,
    
    /**
     * Cash payment
     */
    CASH,
    
    /**
     * Check payment
     */
    CHECK,
    
    /**
     * Other payment method not listed
     */
    OTHER
}
