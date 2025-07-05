package com.gogidix.courier.location.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Projection interface for daily revenue data.
 * Used for reporting and dashboard revenue charts.
 */
public interface DailyRevenue {
    
    /**
     * Get the date.
     * 
     * @return the date
     */
    LocalDate getDay();
    
    /**
     * Get the total revenue for the day.
     * 
     * @return the revenue
     */
    BigDecimal getRevenue();
}
