package com.gogidix.courierservices.tracking.$1;

import com.gogidix.courierservices.tracking.dto.CreatePackageRequest;
import com.gogidix.courierservices.tracking.dto.PackageDTO;
import com.gogidix.courierservices.tracking.dto.TrackingEventDTO;
import com.gogidix.courierservices.tracking.model.Package;
import com.gogidix.courierservices.tracking.model.TrackingEvent;
import com.gogidix.courierservices.tracking.model.TrackingStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between entities and DTOs.
 */
@Component
public class TrackingMapper {

    /**
     * Convert a Package entity to a PackageDTO.
     *
     * @param pack the Package entity
     * @return the PackageDTO
     */
    public PackageDTO toPackageDTO(Package pack) {
        if (pack == null) {
            return null;
        }
        
        return PackageDTO.builder()
                .id(pack.getId())
                .trackingNumber(pack.getTrackingNumber())
                .status(pack.getStatus())
                .senderName(pack.getSenderName())
                .senderAddress(pack.getSenderAddress())
                .recipientName(pack.getRecipientName())
                .recipientAddress(pack.getRecipientAddress())
                .recipientPhone(pack.getRecipientPhone())
                .recipientEmail(pack.getRecipientEmail())
                .estimatedDeliveryDate(pack.getEstimatedDeliveryDate())
                .actualDeliveryDate(pack.getActualDeliveryDate())
                .weight(pack.getWeight())
                .dimensions(pack.getDimensions())
                .orderId(pack.getOrderId())
                .courierId(pack.getCourierId())
                .routeId(pack.getRouteId())
                .signatureRequired(pack.isSignatureRequired())
                .signatureImage(pack.getSignatureImage())
                .deliveryInstructions(pack.getDeliveryInstructions())
                .deliveryAttempts(pack.getDeliveryAttempts())
                .events(toTrackingEventDTOList(pack.getEvents()))
                .createdAt(pack.getCreatedAt())
                .updatedAt(pack.getUpdatedAt())
                .build();
    }

    /**
     * Convert a TrackingEvent entity to a TrackingEventDTO.
     *
     * @param event the TrackingEvent entity
     * @return the TrackingEventDTO
     */
    public TrackingEventDTO toTrackingEventDTO(TrackingEvent event) {
        if (event == null) {
            return null;
        }
        
        return TrackingEventDTO.builder()
                .id(event.getId())
                .status(event.getStatus())
                .description(event.getDescription())
                .eventTime(event.getEventTime())
                .location(event.getLocation())
                .latitude(event.getLatitude())
                .longitude(event.getLongitude())
                .courierId(event.getCourierId())
                .facilityId(event.getFacilityId())
                .scanType(event.getScanType())
                .notes(event.getNotes())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    /**
     * Convert a list of TrackingEvent entities to a list of TrackingEventDTOs.
     *
     * @param events the list of TrackingEvent entities
     * @return the list of TrackingEventDTOs
     */
    public List<TrackingEventDTO> toTrackingEventDTOList(List<TrackingEvent> events) {
        if (events == null) {
            return List.of();
        }
        
        return events.stream()
                .map(this::toTrackingEventDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of Package entities to a list of PackageDTOs.
     *
     * @param packages the list of Package entities
     * @return the list of PackageDTOs
     */
    public List<PackageDTO> toPackageDTOList(List<Package> packages) {
        if (packages == null) {
            return List.of();
        }
        
        return packages.stream()
                .map(this::toPackageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create a Package entity from a CreatePackageRequest.
     *
     * @param request the CreatePackageRequest
     * @return the Package entity
     */
    public Package toPackage(CreatePackageRequest request) {
        if (request == null) {
            return null;
        }
        
        Package pack = Package.createNewPackage(
                request.getSenderName(),
                request.getSenderAddress(),
                request.getRecipientName(),
                request.getRecipientAddress(),
                request.getEstimatedDeliveryDate()
        );
        
        pack.setRecipientPhone(request.getRecipientPhone());
        pack.setRecipientEmail(request.getRecipientEmail());
        pack.setWeight(request.getWeight());
        pack.setDimensions(request.getDimensions());
        pack.setOrderId(request.getOrderId());
        pack.setCourierId(request.getCourierId());
        pack.setRouteId(request.getRouteId());
        pack.setSignatureRequired(request.isSignatureRequired());
        pack.setDeliveryInstructions(request.getDeliveryInstructions());
        
        return pack;
    }

    /**
     * Create a TrackingEvent entity.
     *
     * @param pack the Package entity
     * @param status the status
     * @param description the description
     * @param location the location
     * @param latitude the latitude
     * @param longitude the longitude
     * @return the TrackingEvent entity
     */
    public TrackingEvent createTrackingEvent(Package pack, 
                                           TrackingStatus status,
                                           String description, 
                                           String location,
                                           Double latitude,
                                           Double longitude) {
        TrackingEvent event = TrackingEvent.createBasicEvent(pack, status, description, LocalDateTime.now());
        event.setLocation(location);
        event.setLatitude(latitude);
        event.setLongitude(longitude);
        return event;
    }

    /**
     * Alias method for backward compatibility.
     */
    public Package createPackageRequestToPackage(CreatePackageRequest request) {
        return toPackage(request);
    }

    /**
     * Alias method for backward compatibility.
     */
    public PackageDTO packageToPackageDTO(Package pack) {
        return toPackageDTO(pack);
    }

    /**
     * Alias method for backward compatibility.
     */
    public TrackingEventDTO trackingEventToTrackingEventDTO(TrackingEvent event) {
        return toTrackingEventDTO(event);
    }
} 