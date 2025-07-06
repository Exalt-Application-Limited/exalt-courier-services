package com.gogidix.courier.hqadmin.model.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for a section in an advanced report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSection {
    private String id;
    private String title;
    private String description;
    private SectionType type;
    private List<ReportMetric> metrics;
    private List<ChartData> charts;
    private List<TableData> tables;
    private String htmlContent;
    private String conclusion;
    
    /**
     * Enum for different types of report sections
     */
    public enum SectionType {
        SUMMARY,
        KEY_METRICS,
        TREND_ANALYSIS,
        COMPARISON,
        GEOGRAPHIC,
        CUSTOM,
        ANALYSIS,
        RECOMMENDATIONS
    }
    
    /**
     * Adds a metric to the section
     */
    public void addMetric(ReportMetric metric) {
        if (metrics == null) {
            metrics = new ArrayList<>();
        }
        metrics.add(metric);
    }
    
    /**
     * Adds a chart to the section
     */
    public void addChart(ChartData chart) {
        if (charts == null) {
            charts = new ArrayList<>();
        }
        charts.add(chart);
    }
    
    /**
     * Adds a table to the section
     */
    public void addTable(TableData table) {
        if (tables == null) {
            tables = new ArrayList<>();
        }
        tables.add(table);
    }
    
    /**
     * Sets HTML content for the section
     */
    public void addHtmlContent(String content) {
        this.htmlContent = content;
    }
}