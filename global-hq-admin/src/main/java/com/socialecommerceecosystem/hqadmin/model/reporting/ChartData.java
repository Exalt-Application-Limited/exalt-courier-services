package com.gogidix.courier.hqadmin.model.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Model for chart data in a report section
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartData {
    private String id;
    private String title;
    private String description;
    private ChartType type;
    private List<String> labels;
    private List<DataSeries> series;
    private Map<String, Object> options;
    private String xAxisLabel;
    private String yAxisLabel;
    private Boolean showLegend;
    private Boolean showValues;
    private String colorScheme;
    private Integer height;
    private Integer width;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private Map<String, String> metadata;
    private Boolean isExportable;
    private Boolean isRealTime;
    private String dataSource;
    private List<String> filters;
    
    // Constants for common configurations
    public static final String DEFAULT_COLOR_SCHEME = "default";
    public static final Integer DEFAULT_HEIGHT = 400;
    public static final Integer DEFAULT_WIDTH = 600;
    public static final List<String> AVAILABLE_COLOR_SCHEMES = Arrays.asList(
        "default", "vibrant", "pastel", "monochrome", "cool", "warm", "earth"
    );
    
    /**
     * Validates if the chart data is properly configured
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        if (id == null || id.trim().isEmpty()) return false;
        if (title == null || title.trim().isEmpty()) return false;
        if (type == null) return false;
        if (series == null || series.isEmpty()) return false;
        
        // Validate series data
        for (DataSeries dataSeries : series) {
            if (dataSeries.getData() == null || dataSeries.getData().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Gets the total number of data points across all series
     * @return total data points
     */
    public int getTotalDataPoints() {
        if (series == null) return 0;
        return series.stream()
                .mapToInt(s -> s.getData() != null ? s.getData().size() : 0)
                .sum();
    }
    
    /**
     * Gets all unique colors used in the chart
     * @return set of colors
     */
    public Set<String> getUsedColors() {
        if (series == null) return new HashSet<>();
        return series.stream()
                .map(DataSeries::getColor)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
    
    /**
     * Checks if the chart has real-time data
     * @return true if real-time
     */
    public boolean hasRealTimeData() {
        return Boolean.TRUE.equals(isRealTime);
    }
    
    /**
     * Gets the maximum value across all numeric data series
     * @return maximum value or null if no numeric data
     */
    public Double getMaxValue() {
        if (series == null) return null;
        
        return series.stream()
                .flatMap(s -> s.getData().stream())
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .mapToDouble(Number::doubleValue)
                .max()
                .orElse(0.0);
    }
    
    /**
     * Gets the minimum value across all numeric data series
     * @return minimum value or null if no numeric data
     */
    public Double getMinValue() {
        if (series == null) return null;
        
        return series.stream()
                .flatMap(s -> s.getData().stream())
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .mapToDouble(Number::doubleValue)
                .min()
                .orElse(0.0);
    }
    
    /**
     * Adds a new data series to the chart
     * @param dataSeries the series to add
     */
    public void addDataSeries(DataSeries dataSeries) {
        if (this.series == null) {
            this.series = new ArrayList<>();
        }
        this.series.add(dataSeries);
    }
    
    /**
     * Removes a data series by name
     * @param seriesName the name of the series to remove
     * @return true if removed, false if not found
     */
    public boolean removeDataSeries(String seriesName) {
        if (series == null) return false;
        return series.removeIf(s -> Objects.equals(s.getName(), seriesName));
    }
    
    /**
     * Gets a data series by name
     * @param seriesName the name of the series
     * @return the series or null if not found
     */
    public DataSeries getDataSeries(String seriesName) {
        if (series == null) return null;
        return series.stream()
                .filter(s -> Objects.equals(s.getName(), seriesName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Sets default values for optional fields
     */
    public void setDefaults() {
        if (height == null) height = DEFAULT_HEIGHT;
        if (width == null) width = DEFAULT_WIDTH;
        if (colorScheme == null) colorScheme = DEFAULT_COLOR_SCHEME;
        if (showLegend == null) showLegend = true;
        if (showValues == null) showValues = false;
        if (isExportable == null) isExportable = true;
        if (isRealTime == null) isRealTime = false;
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (metadata == null) metadata = new HashMap<>();
        if (filters == null) filters = new ArrayList<>();
    }
    
    /**
     * Creates a summary of the chart data
     * @return summary string
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Chart: ").append(title)
               .append(" (").append(type).append(")")
               .append("\nSeries: ").append(series != null ? series.size() : 0)
               .append("\nTotal Data Points: ").append(getTotalDataPoints())
               .append("\nDimensions: ").append(width).append("x").append(height);
        
        if (hasRealTimeData()) {
            summary.append("\nReal-time: Yes");
        }
        
        return summary.toString();
    }
    
    // Static factory methods for common chart types
    
    /**
     * Creates a simple line chart with basic configuration
     * @param id chart identifier
     * @param title chart title
     * @param labels x-axis labels
     * @param seriesName name of the data series
     * @param data the data points
     * @return configured ChartData
     */
    public static ChartData createLineChart(String id, String title, List<String> labels, 
                                          String seriesName, List<Object> data) {
        DataSeries series = DataSeries.builder()
                .name(seriesName)
                .data(data)
                .build();
        
        ChartData chart = ChartData.builder()
                .id(id)
                .title(title)
                .type(ChartType.LINE)
                .labels(labels)
                .series(Arrays.asList(series))
                .build();
        
        chart.setDefaults();
        return chart;
    }
    
    /**
     * Creates a bar chart for social ecommerce metrics
     * @param id chart identifier
     * @param title chart title
     * @param categories category labels
     * @param values the values for each category
     * @return configured ChartData
     */
    public static ChartData createSocialEcommerceBarChart(String id, String title, 
                                                        List<String> categories, List<Object> values) {
        DataSeries series = DataSeries.builder()
                .name("Social Ecommerce Metrics")
                .data(values)
                .color("#3498db")
                .backgroundColor("#3498db")
                .build();
        
        ChartData chart = ChartData.builder()
                .id(id)
                .title(title)
                .type(ChartType.BAR)
                .labels(categories)
                .series(Arrays.asList(series))
                .xAxisLabel("Categories")
                .yAxisLabel("Values")
                .build();
        
        chart.setDefaults();
        return chart;
    }
    
    /**
     * Creates a pie chart for distribution analysis
     * @param id chart identifier
     * @param title chart title
     * @param segments segment labels
     * @param values segment values
     * @return configured ChartData
     */
    public static ChartData createDistributionPieChart(String id, String title, 
                                                     List<String> segments, List<Object> values) {
        List<String> colors = Arrays.asList("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40");
        
        DataSeries series = DataSeries.builder()
                .name("Distribution")
                .data(values)
                .build();
        
        ChartData chart = ChartData.builder()
                .id(id)
                .title(title)
                .type(ChartType.PIE)
                .labels(segments)
                .series(Arrays.asList(series))
                .colorScheme("vibrant")
                .build();
        
        // Add color options
        Map<String, Object> options = new HashMap<>();
        options.put("colors", colors);
        chart.setOptions(options);
        
        chart.setDefaults();
        return chart;
    }
    
    /**
     * Creates a real-time dashboard chart for courier services
     * @param id chart identifier
     * @param title chart title
     * @param dataSource the data source identifier
     * @return configured ChartData for real-time updates
     */
    public static ChartData createRealTimeCourierChart(String id, String title, String dataSource) {
        ChartData chart = ChartData.builder()
                .id(id)
                .title(title)
                .type(ChartType.LINE)
                .dataSource(dataSource)
                .isRealTime(true)
                .labels(new ArrayList<>())
                .series(new ArrayList<>())
                .xAxisLabel("Time")
                .yAxisLabel("Deliveries")
                .build();
        
        chart.setDefaults();
        
        // Add metadata for real-time configuration
        chart.getMetadata().put("updateInterval", "30000"); // 30 seconds
        chart.getMetadata().put("maxDataPoints", "50");
        chart.getMetadata().put("autoScroll", "true");
        
        return chart;
    }
    
    /**
     * Creates a multi-series comparison chart
     * @param id chart identifier
     * @param title chart title
     * @param labels x-axis labels
     * @param seriesDataMap map of series names to their data
     * @return configured ChartData
     */
    public static ChartData createComparisonChart(String id, String title, List<String> labels, 
                                                Map<String, List<Object>> seriesDataMap) {
        List<DataSeries> seriesList = new ArrayList<>();
        List<String> defaultColors = Arrays.asList("#3498db", "#e74c3c", "#2ecc71", "#f39c12", "#9b59b6");
        
        int colorIndex = 0;
        for (Map.Entry<String, List<Object>> entry : seriesDataMap.entrySet()) {
            String color = defaultColors.get(colorIndex % defaultColors.size());
            DataSeries series = DataSeries.builder()
                    .name(entry.getKey())
                    .data(entry.getValue())
                    .color(color)
                    .backgroundColor(color)
                    .build();
            seriesList.add(series);
            colorIndex++;
        }
        
        ChartData chart = ChartData.builder()
                .id(id)
                .title(title)
                .type(ChartType.LINE)
                .labels(labels)
                .series(seriesList)
                .showLegend(true)
                .build();
        
        chart.setDefaults();
        return chart;
    }
    
    /**
     * Enum for different types of charts
     */
    public enum ChartType {
        LINE,
        BAR,
        HORIZONTAL_BAR,
        STACKED_BAR,
        PIE,
        DOUGHNUT,
        AREA,
        SCATTER,
        RADAR,
        HEATMAP,
        BUBBLE,
        TIMELINE,
        GEOSPATIAL
    }
    
    /**
     * Model for a data series in a chart
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataSeries {
        private String name;
        private List<Object> data;
        private String color;
        private String borderColor;
        private String backgroundColor;
        private Boolean fill;
        private String dashStyle;
        private Integer markerSize;
        private String markerShape;
        private String stackGroup;
        private List<Integer> borderDash;
        private Integer borderWidth;
        private String pointBackgroundColor;
        private Boolean showPoint;
        
        /**
         * Gets the number of data points in this series
         * @return number of data points
         */
        public int getDataPointCount() {
            return data != null ? data.size() : 0;
        }
        
        /**
         * Validates if the data series is properly configured
         * @return true if valid
         */
        public boolean isValid() {
            return name != null && !name.trim().isEmpty() && 
                   data != null && !data.isEmpty();
        }
        
        /**
         * Adds a data point to the series
         * @param value the value to add
         */
        public void addDataPoint(Object value) {
            if (this.data == null) {
                this.data = new ArrayList<>();
            }
            this.data.add(value);
        }
        
        /**
         * Removes a data point at the specified index
         * @param index the index to remove
         * @return true if removed successfully
         */
        public boolean removeDataPoint(int index) {
            if (data == null || index < 0 || index >= data.size()) {
                return false;
            }
            data.remove(index);
            return true;
        }
        
        /**
         * Gets the maximum numeric value in this series
         * @return maximum value or null if no numeric data
         */
        public Double getMaxValue() {
            if (data == null) return null;
            
            return data.stream()
                    .filter(Number.class::isInstance)
                    .map(Number.class::cast)
                    .mapToDouble(Number::doubleValue)
                    .max()
                    .orElse(0.0);
        }
        
        /**
         * Gets the minimum numeric value in this series
         * @return minimum value or null if no numeric data
         */
        public Double getMinValue() {
            if (data == null) return null;
            
            return data.stream()
                    .filter(Number.class::isInstance)
                    .map(Number.class::cast)
                    .mapToDouble(Number::doubleValue)
                    .min()
                    .orElse(0.0);
        }
        
        /**
         * Calculates the average of numeric values in this series
         * @return average value or null if no numeric data
         */
        public Double getAverage() {
            if (data == null) return null;
            
            OptionalDouble average = data.stream()
                    .filter(Number.class::isInstance)
                    .map(Number.class::cast)
                    .mapToDouble(Number::doubleValue)
                    .average();
            
            return average.isPresent() ? average.getAsDouble() : null;
        }
        
        /**
         * Gets the sum of all numeric values in this series
         * @return sum of values
         */
        public Double getSum() {
            if (data == null) return null;
            
            return data.stream()
                    .filter(Number.class::isInstance)
                    .map(Number.class::cast)
                    .mapToDouble(Number::doubleValue)
                    .sum();
        }
        
        /**
         * Sets default styling for the series
         */
        public void setDefaultStyling() {
            if (color == null) color = "#3498db";
            if (backgroundColor == null) backgroundColor = color;
            if (borderColor == null) borderColor = color;
            if (fill == null) fill = false;
            if (borderWidth == null) borderWidth = 2;
            if (showPoint == null) showPoint = true;
        }
        
        /**
         * Builder class with additional helper methods
         */
        public static class DataSeriesBuilder {
            // Additional builder methods for method chaining
            
            public DataSeriesBuilder borderDash(List<Integer> borderDash) {
                this.borderDash = borderDash;
                return this;
            }
            
            public DataSeriesBuilder borderWidth(int borderWidth) {
                this.borderWidth = borderWidth;
                return this;
            }
            
            public DataSeriesBuilder pointBackgroundColor(String pointBackgroundColor) {
                this.pointBackgroundColor = pointBackgroundColor;
                return this;
            }
            
            public DataSeriesBuilder showPoint(boolean showPoint) {
                this.showPoint = showPoint;
                return this;
            }
            
            /**
             * Sets styling for a sales performance series
             * @return builder instance
             */
            public DataSeriesBuilder salesPerformanceStyle() {
                this.color = "#2ecc71";
                this.backgroundColor = "rgba(46, 204, 113, 0.1)";
                this.borderColor = "#2ecc71";
                this.borderWidth = 3;
                this.fill = true;
                return this;
            }
            
            /**
             * Sets styling for a courier delivery series
             * @return builder instance
             */
            public DataSeriesBuilder courierDeliveryStyle() {
                this.color = "#3498db";
                this.backgroundColor = "rgba(52, 152, 219, 0.1)";
                this.borderColor = "#3498db";
                this.borderWidth = 2;
                this.fill = false;
                this.showPoint = true;
                return this;
            }
            
            /**
             * Sets styling for an error/alert series
             * @return builder instance
             */
            public DataSeriesBuilder alertStyle() {
                this.color = "#e74c3c";
                this.backgroundColor = "rgba(231, 76, 60, 0.1)";
                this.borderColor = "#e74c3c";
                this.borderWidth = 2;
                this.borderDash = Arrays.asList(5, 5);
                this.fill = false;
                return this;
            }
            
            /**
             * Sets styling for a trend prediction series
             * @return builder instance
             */
            public DataSeriesBuilder trendPredictionStyle() {
                this.color = "#9b59b6";
                this.backgroundColor = "rgba(155, 89, 182, 0.05)";
                this.borderColor = "#9b59b6";
                this.borderWidth = 1;
                this.borderDash = Arrays.asList(10, 5);
                this.fill = true;
                this.showPoint = false;
                return this;
            }
            
            /**
             * Sets styling for a target/goal series
             * @return builder instance
             */
            public DataSeriesBuilder targetStyle() {
                this.color = "#f39c12";
                this.backgroundColor = "rgba(243, 156, 18, 0.1)";
                this.borderColor = "#f39c12";
                this.borderWidth = 3;
                this.borderDash = Arrays.asList(2, 2);
                this.fill = false;
                this.showPoint = false;
                return this;
            }
            
            /**
             * Sets styling for social engagement metrics
             * @return builder instance
             */
            public DataSeriesBuilder socialEngagementStyle() {
                this.color = "#e91e63";
                this.backgroundColor = "rgba(233, 30, 99, 0.1)";
                this.borderColor = "#e91e63";
                this.borderWidth = 2;
                this.fill = true;
                this.markerSize = 6;
                this.markerShape = "circle";
                return this;
            }
            
            /**
             * Applies preset styling based on series type
             * @param styleType the type of styling to apply
             * @return builder instance
             */
            public DataSeriesBuilder applyPresetStyle(String styleType) {
                switch (styleType.toLowerCase()) {
                    case "sales":
                        return salesPerformanceStyle();
                    case "courier":
                    case "delivery":
                        return courierDeliveryStyle();
                    case "alert":
                    case "error":
                        return alertStyle();
                    case "trend":
                    case "prediction":
                        return trendPredictionStyle();
                    case "target":
                    case "goal":
                        return targetStyle();
                    case "social":
                    case "engagement":
                        return socialEngagementStyle();
                    default:
                        // Default styling
                        this.color = "#3498db";
                        this.borderWidth = 2;
                        return this;
                }
            }
        }
    }
    
    /**
     * Helper class for chart configuration presets
     */
    public static class ChartPresets {
        
        /**
         * Creates a dashboard summary chart configuration
         * @return map of dashboard options
         */
        public static Map<String, Object> getDashboardOptions() {
            Map<String, Object> options = new HashMap<>();
            options.put("responsive", true);
            options.put("maintainAspectRatio", false);
            options.put("animation", Map.of("duration", 500));
            options.put("legend", Map.of("position", "bottom"));
            options.put("tooltips", Map.of("enabled", true, "mode", "nearest"));
            return options;
        }
        
        /**
         * Creates real-time chart configuration
         * @return map of real-time options
         */
        public static Map<String, Object> getRealTimeOptions() {
            Map<String, Object> options = new HashMap<>();
            options.put("responsive", true);
            options.put("animation", Map.of("duration", 0));
            options.put("scales", Map.of(
                "x", Map.of("type", "realtime", "realtime", Map.of("duration", 60000, "refresh", 1000)),
                "y", Map.of("beginAtZero", true)
            ));
            return options;
        }
        
        /**
         * Creates export-friendly chart configuration
         * @return map of export options
         */
        public static Map<String, Object> getExportOptions() {
            Map<String, Object> options = new HashMap<>();
            options.put("responsive", false);
            options.put("animation", Map.of("duration", 0));
            options.put("legend", Map.of("display", true));
            options.put("title", Map.of("display", true));
            return options;
        }
    }
    
    /**
     * Utility class for color schemes and palettes
     */
    public static class ColorUtils {
        
        public static final Map<String, List<String>> COLOR_SCHEMES = Map.of(
            "default", Arrays.asList("#3498db", "#e74c3c", "#2ecc71", "#f39c12", "#9b59b6"),
            "vibrant", Arrays.asList("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF"),
            "pastel", Arrays.asList("#FFB6C1", "#87CEEB", "#98FB98", "#F0E68C", "#DDA0DD"),
            "monochrome", Arrays.asList("#2C3E50", "#34495E", "#7F8C8D", "#95A5A6", "#BDC3C7"),
            "cool", Arrays.asList("#3498DB", "#2ECC71", "#1ABC9C", "#9B59B6", "#34495E"),
            "warm", Arrays.asList("#E74C3C", "#F39C12", "#F1C40F", "#E67E22", "#D35400"),
            "earth", Arrays.asList("#8B4513", "#A0522D", "#CD853F", "#DEB887", "#F4A460")
        );
        
        /**
         * Gets colors for a specific scheme
         * @param scheme the color scheme name
         * @return list of color codes
         */
        public static List<String> getColors(String scheme) {
            return COLOR_SCHEMES.getOrDefault(scheme, COLOR_SCHEMES.get("default"));
        }
        
        /**
         * Gets a color at a specific index for a scheme
         * @param scheme the color scheme name
         * @param index the color index
         * @return color code
         */
        public static String getColor(String scheme, int index) {
            List<String> colors = getColors(scheme);
            return colors.get(index % colors.size());
        }
    }
}