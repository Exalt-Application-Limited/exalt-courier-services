package com.gogidix.courier.management.assignment.service;

import com.microecosystem.courier.management.courier.model.Courier;
import com.microecosystem.courier.management.courier.repository.CourierRepository;
import com.microecosystem.courier.management.performance.dto.PerformanceMetricDTO;
import com.microecosystem.courier.management.performance.mapper.PerformanceMetricMapper;
import com.microecosystem.courier.management.performance.model.MetricType;
import com.microecosystem.courier.management.performance.model.PerformanceMetric;
import com.microecosystem.courier.management.performance.repository.PerformanceMetricRepository;
import com.microecosystem.courier.management.performance.service.impl.PerformanceMetricsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceMetricsServiceImplTest {

    @Mock
    private PerformanceMetricRepository metricRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private PerformanceMetricMapper metricMapper;

    @InjectMocks
    private PerformanceMetricsServiceImpl metricsService;

    private Courier courier;
    private PerformanceMetric metric;
    private PerformanceMetricDTO metricDTO;
    private final String metricId = "test-metric-id";
    private final String courierId = "1";
    private final LocalDate testDate = LocalDate.now();

    @BeforeEach
    void setUp() {
        // Set up courier
        courier = new Courier();
        courier.setId(1L);
        courier.setName("Test Courier");

        // Set up metric
        metric = new PerformanceMetric();
        metric.setId(1L);
        metric.setMetricId(metricId);
        metric.setCourier(courier);
        metric.setMetricType(MetricType.ON_TIME_DELIVERY_RATE);
        metric.setDate(testDate);
        metric.setValue(85.5);
        metric.setDescription("On-time delivery rate");
        metric.setTargetValue(90.0);
        metric.setIsTargetMet(false);
        metric.setCreatedAt(LocalDateTime.now());
        metric.setUpdatedAt(LocalDateTime.now());

        // Set up DTO
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
    void createMetric() {
        // Arrange
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricMapper.toEntity(any(PerformanceMetricDTO.class))).thenReturn(metric);
        when(metricRepository.save(any(PerformanceMetric.class))).thenReturn(metric);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        PerformanceMetricDTO result = metricsService.createMetric(metricDTO);

        // Assert
        assertNotNull(result);
        assertEquals(metricDTO.getMetricId(), result.getMetricId());
        assertEquals(metricDTO.getCourierId(), result.getCourierId());
        assertEquals(metricDTO.getMetricType(), result.getMetricType());
        verify(metricRepository, times(1)).save(any(PerformanceMetric.class));
    }

    @Test
    void updateMetric() {
        // Arrange
        when(metricRepository.findByMetricId(anyString())).thenReturn(Optional.of(metric));
        when(metricRepository.save(any(PerformanceMetric.class))).thenReturn(metric);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        PerformanceMetricDTO result = metricsService.updateMetric(metricId, metricDTO);

        // Assert
        assertNotNull(result);
        assertEquals(metricDTO.getMetricId(), result.getMetricId());
        verify(metricRepository, times(1)).save(any(PerformanceMetric.class));
    }

    @Test
    void getMetricById() {
        // Arrange
        when(metricRepository.findByMetricId(anyString())).thenReturn(Optional.of(metric));
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        Optional<PerformanceMetricDTO> result = metricsService.getMetricById(metricId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(metricDTO.getMetricId(), result.get().getMetricId());
    }

    @Test
    void deleteMetric() {
        // Arrange
        when(metricRepository.findByMetricId(anyString())).thenReturn(Optional.of(metric));
        doNothing().when(metricRepository).delete(any(PerformanceMetric.class));

        // Act
        boolean result = metricsService.deleteMetric(metricId);

        // Assert
        assertTrue(result);
        verify(metricRepository, times(1)).delete(any(PerformanceMetric.class));
    }

    @Test
    void getMetricsByCourier() {
        // Arrange
        List<PerformanceMetric> metrics = Collections.singletonList(metric);
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricRepository.findByCourier(any(Courier.class))).thenReturn(metrics);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        List<PerformanceMetricDTO> result = metricsService.getMetricsByCourier(courierId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(metricDTO.getMetricId(), result.get(0).getMetricId());
    }

    @Test
    void getMetricsByCourierPaginated() {
        // Arrange
        List<PerformanceMetric> metrics = Collections.singletonList(metric);
        Page<PerformanceMetric> page = new PageImpl<>(metrics);
        Pageable pageable = PageRequest.of(0, 10);
        
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricRepository.findByCourier(any(Courier.class), any(Pageable.class))).thenReturn(page);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        Page<PerformanceMetricDTO> result = metricsService.getMetricsByCourier(courierId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(metricDTO.getMetricId(), result.getContent().get(0).getMetricId());
    }

    @Test
    void getMetricsByCourierAndType() {
        // Arrange
        List<PerformanceMetric> metrics = Collections.singletonList(metric);
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricRepository.findByCourierAndMetricType(any(Courier.class), any(MetricType.class)))
                .thenReturn(metrics);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        List<PerformanceMetricDTO> result = metricsService.getMetricsByCourierAndType(
                courierId, MetricType.ON_TIME_DELIVERY_RATE);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(metricDTO.getMetricId(), result.get(0).getMetricId());
    }

    @Test
    void getMetricsForDateRange() {
        // Arrange
        List<PerformanceMetric> metrics = Collections.singletonList(metric);
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        
        when(metricRepository.findByDateBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(metrics);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        List<PerformanceMetricDTO> result = metricsService.getMetricsForDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(metricDTO.getMetricId(), result.get(0).getMetricId());
    }

    @Test
    void getMetricsByCourierAndDateRange() {
        // Arrange
        List<PerformanceMetric> metrics = Collections.singletonList(metric);
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricRepository.findByCourierAndDateBetween(
                any(Courier.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(metrics);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        List<PerformanceMetricDTO> result = metricsService.getMetricsByCourierAndDateRange(
                courierId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(metricDTO.getMetricId(), result.get(0).getMetricId());
    }

    @Test
    void calculateAverageMetrics() {
        // Arrange
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        Double averageValue = 85.5;
        
        when(metricRepository.calculateAverageForMetricType(
                any(MetricType.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(averageValue);

        // Act
        Map<MetricType, Double> result = metricsService.calculateAverageMetrics(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey(MetricType.ON_TIME_DELIVERY_RATE));
        assertEquals(averageValue, result.get(MetricType.ON_TIME_DELIVERY_RATE));
    }

    @Test
    void calculateAverageMetricsForCourier() {
        // Arrange
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        Double averageValue = 85.5;
        
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricRepository.calculateAverageForCourierAndMetricType(
                any(Courier.class), any(MetricType.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(averageValue);

        // Act
        Map<MetricType, Double> result = metricsService.calculateAverageMetricsForCourier(
                courierId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey(MetricType.ON_TIME_DELIVERY_RATE));
        assertEquals(averageValue, result.get(MetricType.ON_TIME_DELIVERY_RATE));
    }

    @Test
    void calculatePerformanceTrends() {
        // Arrange
        List<PerformanceMetric> metrics = Arrays.asList(
                createMetricWithValue(80.0, testDate.minusDays(2)),
                createMetricWithValue(85.0, testDate.minusDays(1)),
                createMetricWithValue(90.0, testDate)
        );
        
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricRepository.findByCourierAndMetricTypeAndDateBetween(
                any(Courier.class), any(MetricType.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(metrics);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        List<PerformanceMetricDTO> result = metricsService.calculatePerformanceTrends(
                courierId, MetricType.ON_TIME_DELIVERY_RATE, 7, "day");

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void generatePerformanceReport() {
        // Arrange
        List<PerformanceMetric> metrics = Arrays.asList(
                createMetricWithValue(80.0, testDate.minusDays(2)),
                createMetricWithValue(85.0, testDate.minusDays(1)),
                createMetricWithValue(90.0, testDate)
        );
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        
        when(courierRepository.findById(anyLong())).thenReturn(Optional.of(courier));
        when(metricRepository.findByCourierAndDateBetween(
                any(Courier.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(metrics);
        when(metricMapper.toDto(any(PerformanceMetric.class))).thenReturn(metricDTO);

        // Act
        Map<String, Object> result = metricsService.generatePerformanceReport(courierId, startDate, endDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("courierId"));
        assertTrue(result.containsKey("courierName"));
        assertTrue(result.containsKey("reportPeriod"));
    }

    private PerformanceMetric createMetricWithValue(double value, LocalDate date) {
        PerformanceMetric m = new PerformanceMetric();
        m.setId(1L);
        m.setMetricId(UUID.randomUUID().toString());
        m.setCourier(courier);
        m.setMetricType(MetricType.ON_TIME_DELIVERY_RATE);
        m.setDate(date);
        m.setValue(value);
        m.setDescription("On-time delivery rate");
        m.setTargetValue(90.0);
        m.setIsTargetMet(value >= 90.0);
        m.setCreatedAt(LocalDateTime.now());
        m.setUpdatedAt(LocalDateTime.now());
        return m;
    }
} 
