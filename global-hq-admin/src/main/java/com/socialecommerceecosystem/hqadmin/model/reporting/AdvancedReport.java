package com.exalt.courier.hqadmin.model.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Model for advanced performance reports
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedReport {
    private String reportId;
    private String title;
    private String description;
    private ReportType type;
    private LocalDateTime generatedAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<String> regions;
    private List<String> branches;
    private List<ReportSection> sections;
    
    @Builder.Default
    private Map<String, Object> metadata = Map.of();
    
    /**
     * Enum for different types of reports
     */
    public enum ReportType {
        PERFORMANCE_SUMMARY,
        DELIVERY_EFFICIENCY,
        COURIER_PRODUCTIVITY,
        REGIONAL_COMPARISON,
        FINANCIAL_METRICS,
        OPERATIONAL_HEALTH,
        CUSTOM
    }
    
    /**
     * Adds a section to the report
     */
    public void addSection(ReportSection section) {
        if (sections == null) {
            sections = new ArrayList<>();
        }
        sections.add(section);
    }
}