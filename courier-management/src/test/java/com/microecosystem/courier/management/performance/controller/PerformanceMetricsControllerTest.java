package com.microecosystem.courier.management.performance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microecosystem.courier.management.performance.dto.PerformanceMetricDTO;
import com.microecosystem.courier.management.performance.model.MetricType;
import com.microecosystem.courier.management.performance.service.PerformanceMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PerformanceMetricsController.class)
class PerformanceMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PerformanceMetricsService metricsService;

    private PerformanceMetricDTO metricDTO;
    private final String metricId = "test-metric-id";
    private final String courierId = "1";
    private final LocalDate testDate = LocalDate.now();

    @BeforeEach
    void setUp() {
        metricDTO = PerformanceMetricDTO.builder()
                .id(1L)
                .metricId(metricId)
                .courierId(courierId)
                .courierName("Test Courier")
                .metricType(MetricType.ON_TIME_DELIVERY_RATE)
                .date(testDate)
                .value(85.5)
                .description("On-time delivery rate")
                .targetValue(90.0)
                .isTargetMet(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createMetric() throws Exception {
        when(metricsService.createMetric(any(PerformanceMetricDTO.class))).thenReturn(metricDTO);

        mockMvc.perform(post("/api/v1/performance-metrics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(metricDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metricId").value(metricId))
                .andExpect(jsonPath("$.courierId").value(courierId))
                .andExpect(jsonPath("$.metricType").value(MetricType.ON_TIME_DELIVERY_RATE.toString()));

        verify(metricsService, times(1)).createMetric(any(PerformanceMetricDTO.class));
    }

    @Test
    void updateMetric() throws Exception {
        when(metricsService.updateMetric(eq(metricId), any(PerformanceMetricDTO.class))).thenReturn(metricDTO);

        mockMvc.perform(put("/api/v1/performance-metrics/{metricId}", metricId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(metricDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricId").value(metricId))
                .andExpect(jsonPath("$.courierId").value(courierId));

        verify(metricsService, times(1)).updateMetric(eq(metricId), any(PerformanceMetricDTO.class));
    }

    @Test
    void getMetric() throws Exception {
        when(metricsService.getMetricById(metricId)).thenReturn(Optional.of(metricDTO));

        mockMvc.perform(get("/api/v1/performance-metrics/{metricId}", metricId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metricId").value(metricId))
                .andExpect(jsonPath("$.courierId").value(courierId));

        verify(metricsService, times(1)).getMetricById(metricId);
    }

    @Test
    void getMetricNotFound() throws Exception {
        when(metricsService.getMetricById(metricId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/performance-metrics/{metricId}", metricId))
                .andExpect(status().isNotFound());

        verify(metricsService, times(1)).getMetricById(metricId);
    }

    @Test
    void deleteMetric() throws Exception {
        when(metricsService.deleteMetric(metricId)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/performance-metrics/{metricId}", metricId))
                .andExpect(status().isNoContent());

        verify(metricsService, times(1)).deleteMetric(metricId);
    }

    @Test
    void deleteMetricNotFound() throws Exception {
        when(metricsService.deleteMetric(metricId)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/performance-metrics/{metricId}", metricId))
                .andExpect(status().isNotFound());

        verify(metricsService, times(1)).deleteMetric(metricId);
    }

    @Test
    void getMetricsByCourier() throws Exception {
        List<PerformanceMetricDTO> metrics = Collections.singletonList(metricDTO);
        when(metricsService.getMetricsByCourier(courierId)).thenReturn(metrics);

        mockMvc.perform(get("/api/v1/performance-metrics/courier/{courierId}", courierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].metricId").value(metricId));

        verify(metricsService, times(1)).getMetricsByCourier(courierId);
    }

    @Test
    void getMetricsByCourierPaginated() throws Exception {
        List<PerformanceMetricDTO> metrics = Collections.singletonList(metricDTO);
        Page<PerformanceMetricDTO> page = new PageImpl<>(metrics);
        when(metricsService.getMetricsByCourier(eq(courierId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/performance-metrics/courier/{courierId}/paginated", courierId)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].metricId").value(metricId));

        verify(metricsService, times(1)).getMetricsByCourier(eq(courierId), any(Pageable.class));
    }

    @Test
    void getMetricsByCourierAndType() throws Exception {
        List<PerformanceMetricDTO> metrics = Collections.singletonList(metricDTO);
        when(metricsService.getMetricsByCourierAndType(courierId, MetricType.ON_TIME_DELIVERY_RATE)).thenReturn(metrics);

        mockMvc.perform(get("/api/v1/performance-metrics/courier/{courierId}/type/{metricType}", 
                courierId, MetricType.ON_TIME_DELIVERY_RATE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].metricId").value(metricId));

        verify(metricsService, times(1)).getMetricsByCourierAndType(courierId, MetricType.ON_TIME_DELIVERY_RATE);
    }

    @Test
    void getMetricsForDateRange() throws Exception {
        List<PerformanceMetricDTO> metrics = Collections.singletonList(metricDTO);
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        when(metricsService.getMetricsForDateRange(any(LocalDate.class), any(LocalDate.class))).thenReturn(metrics);

        mockMvc.perform(get("/api/v1/performance-metrics/date-range")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].metricId").value(metricId));

        verify(metricsService, times(1)).getMetricsForDateRange(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void getMetricsByCourierAndDateRange() throws Exception {
        List<PerformanceMetricDTO> metrics = Collections.singletonList(metricDTO);
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        when(metricsService.getMetricsByCourierAndDateRange(
                eq(courierId), any(LocalDate.class), any(LocalDate.class))).thenReturn(metrics);

        mockMvc.perform(get("/api/v1/performance-metrics/courier/{courierId}/date-range", courierId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].metricId").value(metricId));

        verify(metricsService, times(1)).getMetricsByCourierAndDateRange(
                eq(courierId), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void calculateAverageMetrics() throws Exception {
        Map<MetricType, Double> averages = Map.of(MetricType.ON_TIME_DELIVERY_RATE, 85.5);
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        when(metricsService.calculateAverageMetrics(any(LocalDate.class), any(LocalDate.class))).thenReturn(averages);

        mockMvc.perform(get("/api/v1/performance-metrics/averages")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ON_TIME_DELIVERY_RATE").value(85.5));

        verify(metricsService, times(1)).calculateAverageMetrics(any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void calculateAverageMetricsForCourier() throws Exception {
        Map<MetricType, Double> averages = Map.of(MetricType.ON_TIME_DELIVERY_RATE, 85.5);
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        when(metricsService.calculateAverageMetricsForCourier(
                eq(courierId), any(LocalDate.class), any(LocalDate.class))).thenReturn(averages);

        mockMvc.perform(get("/api/v1/performance-metrics/courier/{courierId}/averages", courierId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ON_TIME_DELIVERY_RATE").value(85.5));

        verify(metricsService, times(1)).calculateAverageMetricsForCourier(
                eq(courierId), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    void calculatePerformanceTrends() throws Exception {
        List<PerformanceMetricDTO> trends = Arrays.asList(metricDTO, metricDTO, metricDTO);
        when(metricsService.calculatePerformanceTrends(
                eq(courierId), eq(MetricType.ON_TIME_DELIVERY_RATE), anyInt(), anyString())).thenReturn(trends);

        mockMvc.perform(get("/api/v1/performance-metrics/courier/{courierId}/trends", courierId)
                .param("metricType", MetricType.ON_TIME_DELIVERY_RATE.toString())
                .param("numberOfPeriods", "7")
                .param("periodType", "day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].metricId").value(metricId));

        verify(metricsService, times(1)).calculatePerformanceTrends(
                eq(courierId), eq(MetricType.ON_TIME_DELIVERY_RATE), anyInt(), anyString());
    }

    @Test
    void generatePerformanceReport() throws Exception {
        Map<String, Object> report = new HashMap<>();
        report.put("courierId", courierId);
        report.put("courierName", "Test Courier");
        report.put("reportPeriod", Map.of("startDate", testDate.minusDays(7), "endDate", testDate));
        
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        when(metricsService.generatePerformanceReport(
                eq(courierId), any(LocalDate.class), any(LocalDate.class))).thenReturn(report);

        mockMvc.perform(get("/api/v1/performance-metrics/courier/{courierId}/report", courierId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courierId").value(courierId))
                .andExpect(jsonPath("$.courierName").value("Test Courier"));

        verify(metricsService, times(1)).generatePerformanceReport(
                eq(courierId), any(LocalDate.class), any(LocalDate.class));
    }
} 