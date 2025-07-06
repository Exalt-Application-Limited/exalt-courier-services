package com.gogidix.courier.courier.drivermobileapp.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.Map;

/**
 * DTO for storing synchronization state information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncStateDTO {
    
    @NotBlank(message = "Courier ID is required")
    private String courierId;
    
    private String syncToken;
    
    private String syncTimestamp;
    
    private String deviceId;
    
    private String appVersion;
    
    private Map<String, String> networkInfo;
    
    private Map<String, Integer> syncCounts;
}
