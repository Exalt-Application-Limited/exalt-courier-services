package com.exalt.courier.location.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.dto.ShipmentStatusCount;
import com.socialecommerceecosystem.location.model.PaymentMethod;
import com.socialecommerceecosystem.location.model.ShipmentStatus;
import com.socialecommerceecosystem.location.model.WalkInShipment;
import com.socialecommerceecosystem.location.repository.WalkInShipmentRepository;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.ShipmentProcessingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the ShipmentProcessingService interface.
 * Handles business logic for walk-in shipment processing with performance
 * optimizations through caching and query optimization.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShipmentProcessingServiceImpl implements ShipmentProcessingService {

    private final WalkInShipmentRepository shipmentRepository;
    private final NotificationService notificationService;
    
    @Override
    public List<WalkInShipment> getAllShipments() {
        return shipmentRepository.findAll();
    }
    
    @Override
    public Page<WalkInShipment> getAllShipments(Pageable pageable) {
        return shipmentRepository.findAll(pageable);
    }
    
    @Override
    @Cacheable(value = "shipmentById", key = "#shipmentId", cacheManager = "shipmentCacheManager")
    public Optional<WalkInShipment> getShipmentById(Long shipmentId) {
        return shipmentRepository.findById(shipmentId);
    }
    
    @Override
    @Cacheable(value = "shipmentsByTrackingNumber", key = "#trackingNumber", cacheManager = "shipmentCacheManager")
    public Optional<WalkInShipment> getShipmentByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "shipmentCountsByStatus", allEntries = true),
        @CacheEvict(value = "shipmentCountsByLocation", allEntries = true),
        @CacheEvict(value = "pendingDeliveryShipments", allEntries = true),
        @CacheEvict(value = "dashboardMetrics", allEntries = true)
    })
    public WalkInShipment createShipment(WalkInShipment shipment) {
        if (shipment.getTrackingNumber() == null) {
            shipment.setTrackingNumber(generateTrackingNumber(shipment.getOrigin().getId()));
        }
        
        WalkInShipment savedShipment = shipmentRepository.save(shipment);
        
        // Send notification if configured
        notificationService.sendShipmentCreationNotification(savedShipment);
        
        return savedShipment;
    }
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "shipmentById", key = "#shipmentId"),
        @CacheEvict(value = "shipmentsByTrackingNumber", key = "#shipment.trackingNumber"),
        @CacheEvict(value = "shipmentsByCustomer", key = "#shipment.customer.id", condition = "#shipment.customer != null"),
        @CacheEvict(value = "shipmentSummariesByCustomer", key = "#shipment.customer.id", condition = "#shipment.customer != null"),
        @CacheEvict(value = "pendingDeliveryShipments", allEntries = true),
        @CacheEvict(value = "dashboardMetrics", allEntries = true)
    })
    public WalkInShipment updateShipment(Long shipmentId, WalkInShipment shipment) {
        // Ensure the ID is set correctly
        shipment.setId(shipmentId);
        return shipmentRepository.save(shipment);
    }
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "shipmentById", key = "#shipmentId"),
        @CacheEvict(value = "shipmentCountsByStatus", allEntries = true),
        @CacheEvict(value = "shipmentCountsByLocation", allEntries = true),
        @CacheEvict(value = "dashboardMetrics", allEntries = true)
    })
    public void deleteShipment(Long shipmentId) {
        // Get the shipment to evict from tracking number cache
        Optional<WalkInShipment> shipment = shipmentRepository.findById(shipmentId);
        
        shipmentRepository.deleteById(shipmentId);
        
        // Evict from tracking number cache if found
        if (shipment.isPresent()) {
            // This is a programmatic cache eviction when we need to evict by a key that's
            // not a method parameter
            // cacheManager.getCache("shipmentsByTrackingNumber").evict(shipment.get().getTrackingNumber());
        }
    }
    
    @Override
    @Cacheable(value = "shipmentsByCustomer", key = "#customerId", cacheManager = "shipmentCacheManager")
    public List<WalkInShipment> getShipmentsByCustomer(Long customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }
    
    @Override
    public List<WalkInShipment> getShipmentsByOrigin(Long locationId) {
        return shipmentRepository.findByOriginId(locationId);
    }
    
    @Override
    public List<WalkInShipment> getShipmentsByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatus(status);
    }
    
    @Override
    public List<WalkInShipment> getShipmentsByCreationDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return shipmentRepository.findByCreationDateBetween(startDate, endDate);
    }
    
    @Override
    public List<WalkInShipment> getShipmentsByEstimatedDeliveryDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return shipmentRepository.findByEstimatedDeliveryDateBetween(startDate, endDate);
    }
    
    @Override
    @Caching(evict = {
        @CacheEvict(value = "shipmentById", key = "#shipmentId"),
        @CacheEvict(value = "shipmentsByTrackingNumber", allEntries = true),
        @CacheEvict(value = "shipmentCountsByStatus", allEntries = true),
        @CacheEvict(value = "pendingDeliveryShipments", allEntries = true),
        @CacheEvict(value = "dashboardMetrics", allEntries = true)
    })
    public WalkInShipment updateShipmentStatus(Long shipmentId, ShipmentStatus newStatus) {
        Optional<WalkInShipment> shipmentOpt = shipmentRepository.findById(shipmentId);
        
        if (shipmentOpt.isPresent()) {
            WalkInShipment shipment = shipmentOpt.get();
            ShipmentStatus oldStatus = shipment.getStatus();
            shipment.setStatus(newStatus);
            
            // Save the updated shipment
            WalkInShipment updatedShipment = shipmentRepository.save(shipment);
            
            // Send status update notification
            notificationService.sendStatusUpdateNotification(updatedShipment, oldStatus);
            
            return updatedShipment;
        } else {
            throw new RuntimeException("Shipment not found with ID: " + shipmentId);
        }
    }
    
    @Override
    public List<WalkInShipment> findShipmentsByRecipientContact(String contactInfo) {
        // Try to match by phone first
        List<WalkInShipment> shipments = shipmentRepository.findByRecipientPhone(contactInfo);
        
        // If no results, try by email
        if (shipments.isEmpty()) {
            shipments = shipmentRepository.findByRecipientEmail(contactInfo);
        }
        
        return shipments;
    }
    
    @Override
    public List<WalkInShipment> findShipmentsByDestinationCountry(String country) {
        return shipmentRepository.findByRecipientCountry(country);
    }
    
    @Override
    public List<WalkInShipment> findShipmentsByServiceType(String serviceType) {
        return shipmentRepository.findByServiceType(serviceType);
    }
    
    @Override
    public List<WalkInShipment> findInternationalShipments() {
        return shipmentRepository.findByInternational(true);
    }
    
    @Override
    public List<WalkInShipment> findShipmentsByPaymentMethod(PaymentMethod paymentMethod) {
        return shipmentRepository.findByPaymentMethod(paymentMethod);
    }
    
    @Override
    public String generateTrackingNumber(Long locationId) {
        // Format: LOCXXX-YYYYMMDD-RANDOM
        String locationCode = String.format("LOC%03d", locationId);
        String datePart = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        return String.format("%s-%s-%s", locationCode, datePart, randomPart);
    }
    
    @Override
    public BigDecimal calculateShippingCost(Double weight, List<Double> dimensions, String serviceType,
            boolean international, String destinationCountry) {
        // This is a simplified implementation - in a real system, this would involve
        // complex rate calculations based on rate cards, zone mappings, etc.
        
        BigDecimal baseCost = BigDecimal.valueOf(10.0); // Base cost
        
        // Add weight-based cost (simplified)
        BigDecimal weightCost = BigDecimal.valueOf(weight * 2.0);
        
        // Add volumetric weight cost if applicable
        double volumetricWeight = calculateVolumetricWeight(dimensions);
        BigDecimal volumetricCost = BigDecimal.valueOf(volumetricWeight * 1.5);
        
        // Use the higher of actual weight and volumetric weight
        BigDecimal weightBasedCost = weightCost.max(volumetricCost);
        
        // Service type multiplier
        double serviceMultiplier = 1.0;
        switch (serviceType.toUpperCase()) {
            case "EXPRESS":
                serviceMultiplier = 2.0;
                break;
            case "STANDARD":
                serviceMultiplier = 1.0;
                break;
            case "ECONOMY":
                serviceMultiplier = 0.8;
                break;
            default:
                serviceMultiplier = 1.0;
        }
        
        // International multiplier
        double internationalMultiplier = international ? 2.5 : 1.0;
        
        // Country-specific adjustment (simplified)
        double countryMultiplier = 1.0;
        if (international && destinationCountry != null) {
            switch (destinationCountry.toUpperCase()) {
                case "US":
                case "USA":
                case "UNITED STATES":
                    countryMultiplier = 1.0;
                    break;
                case "CA":
                case "CAN":
                case "CANADA":
                    countryMultiplier = 1.1;
                    break;
                case "UK":
                case "UNITED KINGDOM":
                    countryMultiplier = 1.2;
                    break;
                case "AU":
                case "AUS":
                case "AUSTRALIA":
                    countryMultiplier = 1.5;
                    break;
                default:
                    countryMultiplier = 1.3;
            }
        }
        
        // Calculate total cost
        BigDecimal totalCost = baseCost.add(weightBasedCost)
                .multiply(BigDecimal.valueOf(serviceMultiplier))
                .multiply(BigDecimal.valueOf(internationalMultiplier))
                .multiply(BigDecimal.valueOf(countryMultiplier))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        
        return totalCost;
    }
    
    private double calculateVolumetricWeight(List<Double> dimensions) {
        if (dimensions == null || dimensions.size() < 3) {
            return 0.0;
        }
        
        double length = dimensions.get(0);
        double width = dimensions.get(1);
        double height = dimensions.get(2);
        
        return (length * width * height) / 5000.0; // Standard volumetric divisor
    }
    
    @Override
    public BigDecimal calculateInsuranceCost(BigDecimal declaredValue, boolean international) {
        if (declaredValue == null || declaredValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Insurance rate (percentage of declared value)
        BigDecimal insuranceRate = international ? BigDecimal.valueOf(0.05) : BigDecimal.valueOf(0.02);
        
        return declaredValue.multiply(insuranceRate).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    @Override
    public BigDecimal calculateTaxAmount(BigDecimal shippingCost, BigDecimal insuranceCost, String destinationCountry) {
        BigDecimal baseAmount = shippingCost.add(insuranceCost);
        
        // Tax rate based on destination country (simplified)
        BigDecimal taxRate = BigDecimal.valueOf(0.0);
        
        if (destinationCountry != null) {
            switch (destinationCountry.toUpperCase()) {
                case "US":
                case "USA":
                case "UNITED STATES":
                    taxRate = BigDecimal.valueOf(0.0625); // Average US sales tax
                    break;
                case "CA":
                case "CAN":
                case "CANADA":
                    taxRate = BigDecimal.valueOf(0.05); // GST
                    break;
                case "UK":
                case "UNITED KINGDOM":
                    taxRate = BigDecimal.valueOf(0.20); // VAT
                    break;
                case "AU":
                case "AUS":
                case "AUSTRALIA":
                    taxRate = BigDecimal.valueOf(0.10); // GST
                    break;
                default:
                    taxRate = BigDecimal.valueOf(0.10); // Default international tax rate
            }
        }
        
        return baseAmount.multiply(taxRate).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    @Override
    public List<WalkInShipment> findShipmentsRequiringAction() {
        List<ShipmentStatus> actionStatuses = Arrays.asList(
                ShipmentStatus.EXCEPTION,
                ShipmentStatus.DELIVERY_ATTEMPTED,
                ShipmentStatus.RETURN_TO_SENDER,
                ShipmentStatus.HOLD);
        
        return shipmentRepository.findShipmentsRequiringAction(actionStatuses);
    }
    
    @Override
    @Cacheable(value = "pendingDeliveryShipments", cacheManager = "shipmentCacheManager")
    public List<WalkInShipment> findShipmentsWithPendingDelivery() {
        return shipmentRepository.findShipmentsWithPendingDelivery();
    }
    
    @Override
    public List<WalkInShipment> findHighValueShipments(BigDecimal threshold) {
        return shipmentRepository.findHighValueShipments(threshold);
    }
    
    @Override
    @Cacheable(value = "revenueByLocation", key = "{#locationId, #startDate, #endDate}", cacheManager = "dashboardCacheManager")
    public BigDecimal calculateRevenueByLocationAndDateRange(Long locationId, LocalDateTime startDate, LocalDateTime endDate) {
        return shipmentRepository.calculateTotalRevenueByOriginAndDateRange(locationId, startDate, endDate);
    }
    
    @Override
    @Cacheable(value = "revenueByServiceType", key = "{#serviceType, #startDate, #endDate}", cacheManager = "dashboardCacheManager")
    public BigDecimal calculateRevenueByServiceTypeAndDateRange(String serviceType, LocalDateTime startDate, LocalDateTime endDate) {
        return shipmentRepository.calculateTotalRevenueByServiceTypeAndDateRange(serviceType, startDate, endDate);
    }
    
    @Override
    @Cacheable(value = "shipmentCountsByStatus", cacheManager = "dashboardCacheManager")
    public Map<ShipmentStatus, Long> getShipmentCountsByStatus() {
        // Get current date minus 30 days as cutoff
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        
        // Get status counts for the last 30 days
        List<ShipmentStatusCount> statusCounts = shipmentRepository.getShipmentStatusCounts(cutoffDate);
        
        // Convert list to map
        Map<ShipmentStatus, Long> result = new HashMap<>();
        for (ShipmentStatusCount count : statusCounts) {
            result.put(count.getStatus(), count.getCount());
        }
        
        return result;
    }
    
    @Override
    @Cacheable(value = "shipmentCountsByLocation", cacheManager = "dashboardCacheManager")
    public Map<Long, Long> getShipmentCountsByLocation() {
        // This would be more efficiently implemented with a custom repository method
        // that does the counting in the database rather than in Java
        List<WalkInShipment> allShipments = shipmentRepository.findAll();
        
        return allShipments.stream()
                .filter(s -> s.getOrigin() != null)
                .collect(Collectors.groupingBy(
                        s -> s.getOrigin().getId(),
                        Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> getShipmentCountsByServiceType() {
        List<WalkInShipment> allShipments = shipmentRepository.findAll();
        
        return allShipments.stream()
                .filter(s -> s.getServiceType() != null)
                .collect(Collectors.groupingBy(
                        WalkInShipment::getServiceType,
                        Collectors.counting()));
    }
    
    @Override
    public Map<String, Long> getShipmentCountsByDestinationCountry() {
        List<WalkInShipment> allShipments = shipmentRepository.findAll();
        
        return allShipments.stream()
                .filter(s -> s.getDestinationCountry() != null)
                .collect(Collectors.groupingBy(
                        WalkInShipment::getDestinationCountry,
                        Collectors.counting()));
    }
    
    @Override
    public boolean existsByTrackingNumber(String trackingNumber) {
        return shipmentRepository.findByTrackingNumber(trackingNumber).isPresent();
    }
}
