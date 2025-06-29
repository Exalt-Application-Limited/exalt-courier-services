package com.microecosystem.globalhq.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a section within an advanced report.
 * Part of the HQ Admin Dashboard reporting structure.
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportSection {
    private String id;
    private String title;
    private int order;
    private String summary;
    
    @Builder.Default
    private List<ReportMetric> metrics = new ArrayList<>();
    
    @Builder.Default
    private List<ChartData> charts = new ArrayList<>();
    
    /**
     * Add a metric to this report section
     * 
     * @param metric the metric to add
     * @return this report section (for method chaining)
     */
    public ReportSection addMetric(ReportMetric metric) {
        if (metrics == null) {
            metrics = new ArrayList<>();
        }
        metrics.add(metric);
        return this;
    }
    
    /**
     * Add a chart to this report section
     * 
     * @param chart the chart to add
     * @return this report section (for method chaining)
     */
    public ReportSection addChart(ChartData chart) {
        if (charts == null) {
            charts = new ArrayList<>();
        }
        charts.add(chart);
        return this;
    }
}