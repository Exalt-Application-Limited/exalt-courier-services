package com.microecosystem.globalhq.reporting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents chart data for visual reporting.
 * Converted to use Lombok annotations for reduced boilerplate.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChartData {
    private String chartType; // LINE, BAR, PIE, etc.
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private Map<String, Object> data;
    
    /**
     * Factory method to create line chart data
     *
     * @param title the chart title
     * @param xAxisLabel the x-axis label
     * @param yAxisLabel the y-axis label
     * @param data the chart data
     * @return a new ChartData instance for a line chart
     */
    public static ChartData createLineChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Object> data) {
        return ChartData.builder()
                .chartType("LINE")
                .title(title)
                .xAxisLabel(xAxisLabel)
                .yAxisLabel(yAxisLabel)
                .data(data)
                .build();
    }
    
    /**
     * Factory method to create bar chart data
     *
     * @param title the chart title
     * @param xAxisLabel the x-axis label
     * @param yAxisLabel the y-axis label
     * @param data the chart data
     * @return a new ChartData instance for a bar chart
     */
    public static ChartData createBarChart(String title, String xAxisLabel, String yAxisLabel, Map<String, Object> data) {
        return ChartData.builder()
                .chartType("BAR")
                .title(title)
                .xAxisLabel(xAxisLabel)
                .yAxisLabel(yAxisLabel)
                .data(data)
                .build();
    }
}