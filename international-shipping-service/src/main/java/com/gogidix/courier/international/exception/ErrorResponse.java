package com.gogidix.courierservices.international-shipping.$1;

import java.time.LocalDateTime;

/**
 * Represents the standard error response model for API errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;

    

    

    

    

    

    

    
}

