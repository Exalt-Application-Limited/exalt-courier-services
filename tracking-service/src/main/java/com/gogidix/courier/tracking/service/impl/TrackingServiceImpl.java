package com.gogidix.courier.tracking.service.impl;

import com.gogidix.courier.tracking.dto.CreatePackageRequest;
import com.gogidix.courier.tracking.dto.PackageDTO;
import com.gogidix.courier.tracking.dto.TrackingEventDTO;
import com.gogidix.courier.tracking.dto.UpdatePackageStatusRequest;
import com.gogidix.courier.tracking.mapper.TrackingMapper;
import com.gogidix.courier.tracking.model.Package;
import com.gogidix.courier.tracking.model.TrackingEvent;
import com.gogidix.courier.tracking.model.TrackingStatus;
import com.gogidix.courier.tracking.repository.PackageRepository;
import com.gogidix.courier.tracking.repository.TrackingEventRepository;
import com.gogidix.courier.tracking.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the TrackingService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingServiceImpl implements TrackingService {

    private final PackageRepository packageRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final TrackingMapper trackingMapper;

    @Override
    @Transactional
    public PackageDTO createPackage(CreatePackageRequest request) {
        log.info("Creating new package for recipient: {}", request.getRecipientName());
        
        Package pack = trackingMapper.toPackage(request);
        Package savedPackage = packageRepository.save(pack);
        
        log.info("Package created with tracking number: {}", savedPackage.getTrackingNumber());
        return trackingMapper.toPackageDTO(savedPackage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PackageDTO> getPackageByTrackingNumber(String trackingNumber) {
        log.debug("Fetching package with tracking number: {}", trackingNumber);
        
        return packageRepository.findByTrackingNumber(trackingNumber)
                .map(trackingMapper::toPackageDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PackageDTO> getPackageById(Long id) {
        log.debug("Fetching package with ID: {}", id);
        
        return packageRepository.findById(id)
                .map(trackingMapper::toPackageDTO);
    }

    @Override
    @Transactional
    public PackageDTO updatePackageStatus(String trackingNumber, UpdatePackageStatusRequest request) {
        log.info("Updating status for package with tracking number: {} to {}", trackingNumber, request.getStatus());
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        pack.updateStatus(request.getStatus(), request.getDescription());
        
        // Update additional fields if provided
        TrackingEvent event = pack.getEvents().get(pack.getEvents().size() - 1);
        event.setLocation(request.getLocation());
        event.setLatitude(request.getLatitude());
        event.setLongitude(request.getLongitude());
        event.setCourierId(request.getCourierId());
        event.setFacilityId(request.getFacilityId());
        event.setScanType(request.getScanType());
        event.setNotes(request.getNotes());
        
        Package savedPackage = packageRepository.save(pack);
        log.info("Package status updated successfully");
        
        return trackingMapper.toPackageDTO(savedPackage);
    }

    @Override
    @Transactional
    public PackageDTO recordDeliveryAttempt(String trackingNumber, String description, String location) {
        log.info("Recording delivery attempt for package with tracking number: {}", trackingNumber);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        pack.recordDeliveryAttempt(description);
        
        // Update the event with location
        TrackingEvent event = pack.getEvents().get(pack.getEvents().size() - 1);
        event.setLocation(location);
        
        Package savedPackage = packageRepository.save(pack);
        log.info("Delivery attempt recorded successfully. Attempt count: {}", savedPackage.getDeliveryAttempts());
        
        return trackingMapper.toPackageDTO(savedPackage);
    }

    @Override
    @Transactional
    public PackageDTO markDelivered(String trackingNumber, String description, String signatureImage, String location) {
        log.info("Marking package as delivered with tracking number: {}", trackingNumber);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        pack.markDelivered(description, signatureImage);
        
        // Update the event with location
        TrackingEvent event = pack.getEvents().get(pack.getEvents().size() - 1);
        event.setLocation(location);
        
        Package savedPackage = packageRepository.save(pack);
        log.info("Package marked as delivered successfully");
        
        return trackingMapper.toPackageDTO(savedPackage);
    }

    @Override
    @Transactional
    public TrackingEventDTO addTrackingEvent(String trackingNumber, TrackingStatus status, String description, String location) {
        log.info("Adding tracking event for package with tracking number: {}, status: {}", trackingNumber, status);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        
        TrackingEvent event = trackingMapper.createTrackingEvent(pack, status, description, location, null, null);
        pack.getEvents().add(event);
        pack.setStatus(status);
        
        Package savedPackage = packageRepository.save(pack);
        TrackingEvent savedEvent = savedPackage.getEvents().get(savedPackage.getEvents().size() - 1);
        
        log.info("Tracking event added successfully");
        return trackingMapper.toTrackingEventDTO(savedEvent);
    }

    @Override
    @Transactional
    public TrackingEventDTO addTrackingEventWithGeolocation(String trackingNumber, TrackingStatus status, 
                                                          String description, String location, 
                                                          Double latitude, Double longitude) {
        log.info("Adding tracking event with geolocation for package with tracking number: {}, status: {}", 
                trackingNumber, status);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        
        TrackingEvent event = trackingMapper.createTrackingEvent(pack, status, description, location, latitude, longitude);
        pack.getEvents().add(event);
        pack.setStatus(status);
        
        Package savedPackage = packageRepository.save(pack);
        TrackingEvent savedEvent = savedPackage.getEvents().get(savedPackage.getEvents().size() - 1);
        
        log.info("Tracking event with geolocation added successfully");
        return trackingMapper.toTrackingEventDTO(savedEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackingEventDTO> getTrackingEvents(String trackingNumber) {
        log.debug("Fetching tracking events for package with tracking number: {}", trackingNumber);
        
        List<TrackingEvent> events = trackingEventRepository.findByPackTrackingNumber(trackingNumber);
        return trackingMapper.toTrackingEventDTOList(events);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PackageDTO> getPackagesByStatus(TrackingStatus status, Pageable pageable) {
        log.debug("Fetching packages with status: {}", status);
        
        Page<Package> packages = packageRepository.findByStatus(status, pageable);
        return packages.map(trackingMapper::toPackageDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PackageDTO> getPackagesByCourier(Long courierId, Pageable pageable) {
        log.debug("Fetching packages assigned to courier with ID: {}", courierId);
        
        Page<Package> packages = packageRepository.findByCourierId(courierId, pageable);
        return packages.map(trackingMapper::toPackageDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> getPackagesByRoute(Long routeId) {
        log.debug("Fetching packages on route with ID: {}", routeId);
        
        List<Package> packages = packageRepository.findByRouteId(routeId);
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> getPackagesByOrder(String orderId) {
        log.debug("Fetching packages for order with ID: {}", orderId);
        
        List<Package> packages = packageRepository.findByOrderId(orderId);
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> searchPackagesByRecipientName(String recipientName) {
        log.debug("Searching packages by recipient name: {}", recipientName);
        
        List<Package> packages = packageRepository.findByRecipientNameContainingIgnoreCase(recipientName);
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> searchPackagesByRecipientAddress(String recipientAddress) {
        log.debug("Searching packages by recipient address: {}", recipientAddress);
        
        List<Package> packages = packageRepository.findByRecipientAddressContainingIgnoreCase(recipientAddress);
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> getPackagesOutForDelivery() {
        log.debug("Fetching packages that are out for delivery");
        
        List<Package> packages = packageRepository.findPackagesOutForDelivery();
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> getDelayedPackages() {
        log.debug("Fetching delayed packages");
        
        List<Package> packages = packageRepository.findDelayedPackages();
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> getPackagesWithMultipleAttempts(int minAttempts) {
        log.debug("Fetching packages with at least {} delivery attempts", minAttempts);
        
        List<Package> packages = packageRepository.findPackagesWithDeliveryAttempts(minAttempts);
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> getPackagesByEstimatedDeliveryDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching packages with estimated delivery date between {} and {}", startDate, endDate);
        
        List<Package> packages = packageRepository.findByEstimatedDeliveryDateBetween(startDate, endDate);
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageDTO> getOverduePackages() {
        log.debug("Fetching overdue packages");
        
        List<Package> packages = packageRepository.findOverduePackages(LocalDateTime.now());
        return trackingMapper.toPackageDTOList(packages);
    }

    @Override
    @Transactional(readOnly = true)
    public PackageStatistics getPackageStatistics() {
        log.debug("Calculating package delivery statistics");
        
        PackageStatistics stats = new PackageStatistics();
        
        stats.setTotalPackages(packageRepository.count());
        stats.setDeliveredPackages(packageRepository.countByStatus(TrackingStatus.DELIVERED));
        stats.setInTransitPackages(packageRepository.countByStatus(TrackingStatus.IN_TRANSIT) + 
                                   packageRepository.countByStatus(TrackingStatus.OUT_FOR_DELIVERY));
        stats.setDelayedPackages(packageRepository.countByStatus(TrackingStatus.DELAYED));
        stats.setReturnedPackages(packageRepository.countByStatus(TrackingStatus.RETURNED_TO_SENDER));
        
        // Calculate on-time delivery rate
        if (stats.getDeliveredPackages() > 0) {
            long onTimeDeliveries = packageRepository.count() - packageRepository.findOverduePackages(LocalDateTime.now()).size();
            stats.setOnTimeDeliveryRate((double) onTimeDeliveries / stats.getTotalPackages());
        } else {
            stats.setOnTimeDeliveryRate(0.0);
        }
        
        // Calculate average delivery attempts
        List<Package> deliveredPackages = packageRepository.findByStatus(TrackingStatus.DELIVERED);
        if (!deliveredPackages.isEmpty()) {
            double totalAttempts = deliveredPackages.stream()
                    .mapToInt(Package::getDeliveryAttempts)
                    .sum();
            stats.setAverageDeliveryAttempts(totalAttempts / deliveredPackages.size());
        } else {
            stats.setAverageDeliveryAttempts(0.0);
        }
        
        return stats;
    }

    @Override
    @Transactional
    public PackageDTO assignCourier(String trackingNumber, Long courierId) {
        log.info("Assigning courier with ID: {} to package with tracking number: {}", courierId, trackingNumber);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        pack.setCourierId(courierId);
        pack.addEvent(TrackingStatus.PROCESSING, "Assigned to courier ID: " + courierId);
        
        Package savedPackage = packageRepository.save(pack);
        log.info("Courier assigned successfully");
        
        return trackingMapper.toPackageDTO(savedPackage);
    }

    @Override
    @Transactional
    public PackageDTO assignRoute(String trackingNumber, Long routeId) {
        log.info("Assigning route with ID: {} to package with tracking number: {}", routeId, trackingNumber);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        pack.setRouteId(routeId);
        pack.addEvent(TrackingStatus.PROCESSING, "Assigned to route ID: " + routeId);
        
        Package savedPackage = packageRepository.save(pack);
        log.info("Route assigned successfully");
        
        return trackingMapper.toPackageDTO(savedPackage);
    }

    @Override
    @Transactional
    public PackageDTO updateDeliveryInstructions(String trackingNumber, String instructions) {
        log.info("Updating delivery instructions for package with tracking number: {}", trackingNumber);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        pack.setDeliveryInstructions(instructions);
        
        Package savedPackage = packageRepository.save(pack);
        log.info("Delivery instructions updated successfully");
        
        return trackingMapper.toPackageDTO(savedPackage);
    }

    @Override
    @Transactional
    public PackageDTO setSignatureRequired(String trackingNumber, boolean signatureRequired) {
        log.info("Setting signature requirement to {} for package with tracking number: {}", 
                signatureRequired, trackingNumber);
        
        Package pack = getPackageEntityByTrackingNumber(trackingNumber);
        pack.setSignatureRequired(signatureRequired);
        
        Package savedPackage = packageRepository.save(pack);
        log.info("Signature requirement updated successfully");
        
        return trackingMapper.toPackageDTO(savedPackage);
    }

    /**
     * Helper method to get a Package entity by tracking number.
     *
     * @param trackingNumber the tracking number
     * @return the Package entity
     * @throws EntityNotFoundException if the package is not found
     */
    private Package getPackageEntityByTrackingNumber(String trackingNumber) {
        return packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new EntityNotFoundException("Package not found with tracking number: " + trackingNumber));
    }
} 