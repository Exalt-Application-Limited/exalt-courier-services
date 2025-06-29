package com.exalt.courier.hqadmin.model.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Model for table data in a report section
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableData {
    private String id;
    private String title;
    private String description;
    private List<Column> columns;
    private List<Map<String, Object>> rows;
    private String caption;
    private Boolean showHeader;
    private Boolean showFooter;
    private Boolean striped;
    private Boolean hoverable;
    private Boolean bordered;
    private Boolean responsive;
    private Map<String, Object> formatting;
    private List<String> totals;
    private String exportFormat;
    
    /**
     * Model for a column in a table
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column {
        private String id;
        private String name;
        private String dataKey;
        private String width;
        private Boolean sortable;
        private Boolean filterable;
        private String align;
        private String cellFormat;
        private Map<String, Object> cellFormatting;
        private List<ConditionalFormat> conditionalFormats;
    }
    
    /**
     * Model for conditional formatting in a table cell
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionalFormat {
        private String condition;
        private String backgroundColor;
        private String textColor;
        private String iconClass;
        private String format;
        private String fontWeight;
        
        /**
         * Builder class with additional helper methods
         */
        public static class ConditionalFormatBuilder {
            // Additional builder method for fontWeight
            
            public ConditionalFormatBuilder fontWeight(String fontWeight) {
                this.fontWeight = fontWeight;
                return this;
            }
        }
    }
}