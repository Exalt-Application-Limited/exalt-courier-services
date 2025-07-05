package com.gogidix.courier.shared.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Data structure for transferring data between different dashboard levels.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataTransfer {

    private String id;
    private String sourceLevel;
    private String sourceId;
    private String targetLevel;
    private String targetId;
    private String dataType;
    private Map<String, Object> data;
    private Map<String, String> metadata;
    
    @Builder.Default
    private boolean aggregated = false;
    
    @Builder.Default
    private boolean filtered = false;
    
    private String filterCriteria;
}
