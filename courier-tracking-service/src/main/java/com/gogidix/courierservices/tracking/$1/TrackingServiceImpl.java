package com.gogidix.courierservices.tracking.$1;

import com.gogidix.courierservices.tracking.client.CourierManagementClient;
import com.gogidix.courierservices.tracking.client.RoutingServiceClient;
import com.gogidix.courierservices.tracking.dto.CreatePackageRequest;
import com.gogidix.courierservices.tracking.dto.PackageDTO;
import com.gogidix.courierservices.tracking.dto.TrackingEventDTO;
import com.gogidix.courierservices.tracking.dto.UpdatePackageStatusRequest;
import com.gogidix.courierservices.tracking.event.TrackingEventPublisher;
import com.gogidix.courierservices.tracking.mapper.TrackingMapper;
import com.gogidix.courierservices.tracking.model.Package;
import com.gogidix.courierservices.tracking.model.TrackingEvent;
import com.gogidix.courierservices.tracking.model.TrackingStatus;
import com.gogidix.courierservices.tracking.repository.PackageRepository;
import com.gogidix.courierservices.tracking.repository.TrackingEventRepository;
import com.gogidix.courierservices.tracking.webhook.WebhookNotifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the TrackingService interface.
 */
@Service
@Slf4j
public class TrackingServiceImpl implements TrackingService {

    private final PackageRepository packageRepository;
    private final TrackingEventRepository eventRepository;
    private final TrackingMapper trackingMapper;
    private final TrackingEventPublisher eventPublisher;
    private final WebhookNotifier webhookNotifier;
    private final CourierManagementClient courierManagementClient;
    private final RoutingServiceClient routingServiceClient;

    @Autowired
    public TrackingServiceImpl(
            PackageRepository packageRepository,
            TrackingEventRepository eventRepository,
            TrackingMapper trackingMapper,
            TrackingEventPublisher eventPublisher,
            WebhookNotifier webhookNotifier,
            CourierManagementClient courierManagementClient,
            RoutingServiceClient routingServiceClient) {
        this.packageRepository = packageRepository;
        this.eventRepository = eventRepository;
        this.trackingMapper = trackingMapper;
        this.eventPublisher = eventPublisher;
        this.webhookNotifier = webhookNotifier;
        this.courierManagementClient = courierManagementClient;
        this.routingServiceClient = routingServiceClient;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"packages", "packagesByStatus", "packageStatistics"}, allEntries = true)
    public PackageDTO createPackage(CreatePackageRequest request) {
        Package newPackage = trackingMapper.createPackageRequestToPackage(request);
        newPackage.setTrackingNumber(generateTrackingNumber());
        newPackage.setStatus(TrackingStatus.CREATED);
        newPackage.setCreatedAt(LocalDateTime.now());
        newPackage.setUpdatedAt(LocalDateTime.now());

        Package savedPackage = packageRepository.save(newPackage);
        
        // Create initial tracking event
        TrackingEvent initialEvent = new TrackingEvent();
        initialEvent.setPack(savedPackage);
        initialEvent.setStatus(TrackingStatus.CREATED);
        initialEvent.setDescription("Package created in system");
        initialEvent.setEventTime(LocalDateTime.now());
        initialEvent.setCreatedAt(LocalDateTime.now());
        initialEvent.setUpdatedAt(LocalDateTime.now());
        eventRepository.save(initialEvent);
        
        PackageDTO packageDTO = trackingMapper.packageToPackageDTO(savedPackage);
        
        // Publish event
        eventPublisher.publishPackageStatusChange(packageDTO);
        
        return packageDTO;
    }

    @Override
    @Cacheable(value = "packages", key = "#trackingNumber")
    public Optional<PackageDTO> getPackageByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
                .map(trackingMapper::packageToPackageDTO);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"packages", "packagesByStatus", "packageStatistics"}, allEntries = true)
    public PackageDTO updatePackageStatus(String trackingNumber, UpdatePackageStatusRequest request) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Package not found with tracking number: " + trackingNumber));
        
        pkg.setStatus(request.getStatus());
        pkg.setUpdatedAt(LocalDateTime.now());
        
        // Create tracking event
        TrackingEvent event = new TrackingEvent();
        event.setPack(pkg);
        event.setStatus(request.getStatus());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setCourierId(request.getCourierId());
        event.setFacilityId(request.getFacilityId());
        event.setScanType(request.getScanType());
        event.setNotes(request.getNotes());
        event.setEventTime(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        eventRepository.save(event);
        Package updatedPackage = packageRepository.save(pkg);
        
        PackageDTO packageDTO = trackingMapper.packageToPackageDTO(updatedPackage);
        TrackingEventDTO eventDTO = trackingMapper.trackingEventToTrackingEventDTO(event);
        
        // Publish events
        eventPublisher.publishPackageStatusChange(packageDTO);
        eventPublisher.publishTrackingEvent(trackingNumber, eventDTO);
        
        // Send webhook notifications
        webhookNotifier.notifyPackageStatusChange(packageDTO);
        webhookNotifier.notifyTrackingEvent(trackingNumber, eventDTO);
        
        // Notify courier if assigned
        if (pkg.getCourierId() != null) {
            try {
                Map<String, Object> statusChangeInfo = new HashMap<>();
                statusChangeInfo.put("trackingNumber", trackingNumber);
                statusChangeInfo.put("status", request.getStatus().name());
                statusChangeInfo.put("description", request.getDescription());
                courierManagementClient.notifyPackageStatusChange(pkg.getCourierId(), statusChangeInfo);
            } catch (Exception e) {
                // Log but don't fail the operation
                // log.warn("Failed to notify courier {}: {}", pkg.getCourierId(), e.getMessage());
            }
        }
        
        return packageDTO;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"packages", "packagesByStatus", "packageStatistics"}, allEntries = true)
    public PackageDTO recordDeliveryAttempt(String trackingNumber, String description, String location) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Package not found with tracking number: " + trackingNumber));
        
        pkg.setStatus(TrackingStatus.DELIVERY_ATTEMPTED);
        pkg.setDeliveryAttempts(pkg.getDeliveryAttempts() + 1);
        pkg.setUpdatedAt(LocalDateTime.now());
        
        // Create tracking event
        TrackingEvent event = new TrackingEvent();
        event.setPack(pkg);
        event.setStatus(TrackingStatus.DELIVERY_ATTEMPTED);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventTime(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        eventRepository.save(event);
        Package updatedPackage = packageRepository.save(pkg);
        
        PackageDTO packageDTO = trackingMapper.packageToPackageDTO(updatedPackage);
        TrackingEventDTO eventDTO = trackingMapper.trackingEventToTrackingEventDTO(event);
        
        // Publish events
        eventPublisher.publishPackageStatusChange(packageDTO);
        eventPublisher.publishTrackingEvent(trackingNumber, eventDTO);
        
        // Send webhook notifications
        webhookNotifier.notifyPackageStatusChange(packageDTO);
        webhookNotifier.notifyTrackingEvent(trackingNumber, eventDTO);
        
        return packageDTO;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"packages", "packagesByStatus", "packageStatistics"}, allEntries = true)
    public PackageDTO markDelivered(String trackingNumber, String description, String signatureImage, String location) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Package not found with tracking number: " + trackingNumber));
        
        pkg.setStatus(TrackingStatus.DELIVERED);
        pkg.setActualDeliveryDate(LocalDateTime.now());
        pkg.setSignatureImage(signatureImage);
        pkg.setUpdatedAt(LocalDateTime.now());
        
        // Create tracking event
        TrackingEvent event = new TrackingEvent();
        event.setPack(pkg);
        event.setStatus(TrackingStatus.DELIVERED);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventTime(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        eventRepository.save(event);
        Package updatedPackage = packageRepository.save(pkg);
        
        PackageDTO packageDTO = trackingMapper.packageToPackageDTO(updatedPackage);
        TrackingEventDTO eventDTO = trackingMapper.trackingEventToTrackingEventDTO(event);
        
        // Publish events
        eventPublisher.publishPackageStatusChange(packageDTO);
        eventPublisher.publishTrackingEvent(trackingNumber, eventDTO);
        eventPublisher.publishDeliveryEvent(packageDTO);
        
        // Send webhook notifications
        webhookNotifier.notifyPackageStatusChange(packageDTO);
        webhookNotifier.notifyTrackingEvent(trackingNumber, eventDTO);
        webhookNotifier.notifyDelivery(packageDTO);
        
        return packageDTO;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"packages", "packagesByStatus", "trackingEvents"}, allEntries = true)
    public TrackingEventDTO addTrackingEvent(String trackingNumber, TrackingStatus status, String description, String location) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Package not found with tracking number: " + trackingNumber));
        
        // Update package status
        pkg.setStatus(status);
        pkg.setUpdatedAt(LocalDateTime.now());
        packageRepository.save(pkg);
        
        // Create tracking event
        TrackingEvent event = new TrackingEvent();
        event.setPack(pkg);
        event.setStatus(status);
        event.setDescription(description);
        event.setLocation(location);
        event.setEventTime(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        TrackingEvent savedEvent = eventRepository.save(event);
        
        PackageDTO packageDTO = trackingMapper.packageToPackageDTO(pkg);
        TrackingEventDTO eventDTO = trackingMapper.trackingEventToTrackingEventDTO(savedEvent);
        
        // Publish events
        eventPublisher.publishPackageStatusChange(packageDTO);
        eventPublisher.publishTrackingEvent(trackingNumber, eventDTO);
        
        // Send webhook notifications
        webhookNotifier.notifyPackageStatusChange(packageDTO);
        webhookNotifier.notifyTrackingEvent(trackingNumber, eventDTO);
        
        return eventDTO;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"packages", "packagesByStatus", "trackingEvents"}, allEntries = true)
    public TrackingEventDTO addTrackingEventWithGeolocation(String trackingNumber, TrackingStatus status, 
                                                         String description, String location, 
                                                         Double latitude, Double longitude) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Package not found with tracking number: " + trackingNumber));
        
        // Update package status
        pkg.setStatus(status);
        pkg.setUpdatedAt(LocalDateTime.now());
        packageRepository.save(pkg);
        
        // Create tracking event
        TrackingEvent event = new TrackingEvent();
        event.setPack(pkg);
        event.setStatus(status);
        event.setDescription(description);
        event.setLocation(location);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        event.setEventTime(LocalDateTime.now());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        TrackingEvent savedEvent = eventRepository.save(event);
        
        PackageDTO packageDTO = trackingMapper.packageToPackageDTO(pkg);
        TrackingEventDTO eventDTO = trackingMapper.trackingEventToTrackingEventDTO(savedEvent);
        
        // Publish events
        eventPublisher.publishPackageStatusChange(packageDTO);
        eventPublisher.publishTrackingEvent(trackingNumber, eventDTO);
        
        // Send webhook notifications
        webhookNotifier.notifyPackageStatusChange(packageDTO);
        webhookNotifier.notifyTrackingEvent(trackingNumber, eventDTO);
        
        // Check if location is on route (if route is assigned)
        if (pkg.getRouteId() != null && latitude != null && longitude != null) {
            try {
                boolean isOnRoute = routingServiceClient.isLocationOnRoute(pkg.getRouteId(), latitude, longitude);
                if (!isOnRoute) {
                    // Log potential route deviation
                    // log.warn("Package {} location ({}, {}) is not on assigned route {}", 
                    //        trackingNumber, latitude, longitude, pkg.getRouteId());
                }
            } catch (Exception e) {
                // Log but don't fail the operation
                // log.warn("Failed to check if location is on route: {}", e.getMessage());
            }
        }
        
        return eventDTO;
    }

    @Override
    @Cacheable(value = "trackingEvents", key = "#trackingNumber")
    public List<TrackingEventDTO> getTrackingEvents(String trackingNumber) {
        Package pkg = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Package not found with tracking number: " + trackingNumber));
        
        List<TrackingEvent> events = eventRepository.findByPackOrderByEventTimeDesc(pkg);
        return events.stream()
                .map(trackingMapper::trackingEventToTrackingEventDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "packagesByStatus", key = "T(java.util.Objects).hash(#status, #pageable)")
    public Page<PackageDTO> getPackagesByStatus(TrackingStatus status, Pageable pageable) {
        Page<Package> packages = packageRepository.findByStatus(status, pageable);
        return packages.map(trackingMapper::packageToPackageDTO);
    }

    @Override
    public PackageDTO setSignatureRequired(String trackingNumber, boolean signatureRequired) {
        Package pack = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Package not found: " + trackingNumber));
        
        pack.setSignatureRequired(signatureRequired);
        Package updatedPackage = packageRepository.save(pack);
        
        // Create tracking event
        TrackingEvent event = TrackingEvent.createBasicEvent(
                pack,
                pack.getStatus(),
                signatureRequired ? "Signature requirement enabled" : "Signature requirement disabled",
                LocalDateTime.now()
        );
        event.setPack(pack);
        eventRepository.save(event);
        
        return trackingMapper.packageToPackageDTO(updatedPackage);
    }

    @Override
    public PackageDTO updateDeliveryInstructions(String trackingNumber, String instructions) {
        Package pack = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Package not found: " + trackingNumber));
        
        pack.setDeliveryInstructions(instructions);
        Package updatedPackage = packageRepository.save(pack);
        
        // Create tracking event
        TrackingEvent event = TrackingEvent.createBasicEvent(
                pack,
                pack.getStatus(),
                "Delivery instructions updated",
                LocalDateTime.now()
        );
        event.setPack(pack);
        eventRepository.save(event);
        
        return trackingMapper.packageToPackageDTO(updatedPackage);
    }

    @Override
    public PackageDTO assignRoute(String trackingNumber, Long routeId) {
        Package pack = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Package not found: " + trackingNumber));
        
        pack.setRouteId(routeId);
        Package updatedPackage = packageRepository.save(pack);
        
        // Create tracking event
        TrackingEvent event = TrackingEvent.createBasicEvent(
                pack,
                pack.getStatus(),
                "Route assigned: " + routeId,
                LocalDateTime.now()
        );
        event.setPack(pack);
        eventRepository.save(event);
        
        return trackingMapper.packageToPackageDTO(updatedPackage);
    }

    @Override
    public PackageDTO assignCourier(String trackingNumber, Long courierId) {
        Package pack = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Package not found: " + trackingNumber));
        
        pack.setCourierId(courierId);
        Package updatedPackage = packageRepository.save(pack);
        
        // Create tracking event
        TrackingEvent event = TrackingEvent.createBasicEvent(
                pack,
                pack.getStatus(),
                "Courier assigned: " + courierId,
                LocalDateTime.now()
        );
        event.setPack(pack);
        eventRepository.save(event);
        
        return trackingMapper.packageToPackageDTO(updatedPackage);
    }

    @Override
    public TrackingService.PackageStatistics getPackageStatistics() {
        TrackingService.PackageStatistics stats = new TrackingService.PackageStatistics();
        
        // Get counts by status
        long totalPackages = packageRepository.count();
        long deliveredPackages = packageRepository.countByStatus(TrackingStatus.DELIVERED);
        long inTransitPackages = packageRepository.countByStatus(TrackingStatus.IN_TRANSIT);
        long delayedPackages = packageRepository.countByStatus(TrackingStatus.DELAYED);
        long returnedPackages = packageRepository.countByStatus(TrackingStatus.RETURNED_TO_SENDER);
        
        // Calculate metrics
        double onTimeDeliveryRate = totalPackages > 0 ? (double) deliveredPackages / totalPackages * 100 : 0.0;
        double averageDeliveryAttempts = totalPackages > 0 ? packageRepository.findAll().stream()
                .mapToInt(pkg -> pkg.getDeliveryAttempts() != null ? pkg.getDeliveryAttempts() : 0)
                .average().orElse(0.0) : 0.0;
        
        // Set statistics
        stats.setTotalPackages(totalPackages);
        stats.setDeliveredPackages(deliveredPackages);
        stats.setInTransitPackages(inTransitPackages);
        stats.setDelayedPackages(delayedPackages);
        stats.setReturnedPackages(returnedPackages);
        stats.setOnTimeDeliveryRate(onTimeDeliveryRate);
        stats.setAverageDeliveryAttempts(averageDeliveryAttempts);
        
        return stats;
    }

    @Override
    public List<PackageDTO> getOverduePackages() {
        LocalDateTime cutoffDate = LocalDateTime.now(); // Packages overdue if past estimated delivery date
        List<Package> overduePackages = packageRepository.findOverduePackages(cutoffDate);
        return overduePackages.stream()
                .map(trackingMapper::packageToPackageDTO)
                .collect(Collectors.toList());
    }

    // Other methods would be similarly updated with caching and event publishing...

    private String generateTrackingNumber() {
        // Simple implementation for demo purposes
        return "TRK" + System.currentTimeMillis();
    }
} 
