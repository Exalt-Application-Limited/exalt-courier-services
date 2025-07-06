package com.gogidix.courier.routing.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response model for shipment estimated time of arrival.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ETAResponse {
    
    private String shipmentId;
    
    private LocalDateTime estimatedTimeOfArrival;
} 