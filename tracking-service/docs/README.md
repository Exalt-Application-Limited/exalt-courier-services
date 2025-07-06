# Tracking Service Documentation

## Overview
The Tracking Service provides comprehensive package tracking functionality for the Courier Services ecosystem. It enables real-time location tracking, status updates, delivery confirmations, and provides tracking information to customers, drivers, and administrative staff across multiple courier networks.

## Components

### Core Components
- **TrackingApplication**: The main tracking application class providing real-time package monitoring, location updates, and status management.
- **SecurityConfig**: Security configuration for tracking operations including authentication, authorization, and data privacy protection.

### Feature Components
- **Real-time Tracking**: Component for live GPS tracking, location updates, and route monitoring for packages and drivers.
- **Status Management**: Comprehensive package status tracking from pickup to delivery with automated status transitions.
- **Delivery Confirmation**: Digital proof of delivery with signatures, photos, and recipient verification.
- **Route Optimization**: Integration with routing services for efficient delivery path tracking and optimization.
- **Notification Engine**: Automated customer notifications for tracking updates, delivery alerts, and exception handling.

### Data Access Layer
- **Repository**: Common abstraction for tracking data operations including package history and location data.
- **JpaRepository**: JPA implementation for tracking, delivery, and location database operations.

### Utility Services
- **Validator**: Input validation for tracking numbers, GPS coordinates, and delivery confirmations.
- **Logger**: Comprehensive logging for tracking operations, location updates, and delivery events.

### Integration Components
- **RestClient**: HTTP client for communication with courier services, GPS providers, and 3PL tracking APIs.
- **MessageBroker**: Event publishing for tracking updates, delivery events, and real-time notifications.

## Getting Started
To use the Tracking Service, follow these steps:

1. Create a tracking application that extends TrackingApplication
2. Configure security settings using SecurityConfig
3. Add required components (Real-time Tracking, Status Management, Delivery Confirmation)
4. Use the data access layer for tracking and delivery operations
5. Integrate with GPS providers and notification services

## Examples

### Creating a Tracking Application
```java
import com.gogidix.courier.tracking.core.TrackingApplication;
import com.gogidix.courier.tracking.core.SecurityConfig;
import com.gogidix.courier.tracking.components.realtime.RealtimeTracking;
import com.gogidix.courier.tracking.components.status.StatusManagement;
import com.gogidix.courier.tracking.components.delivery.DeliveryConfirmation;
import com.gogidix.courier.tracking.components.notification.NotificationEngine;

@SpringBootApplication
public class CourierTrackingService extends TrackingApplication {
    private final SecurityConfig securityConfig;
    private final RealtimeTracking realtimeTracking;
    private final StatusManagement statusManagement;
    private final DeliveryConfirmation deliveryConfirmation;
    private final NotificationEngine notificationEngine;
    
    public CourierTrackingService() {
        super("Courier Tracking Service", "Real-time package tracking and delivery management");
        
        this.securityConfig = new SecurityConfig();
        this.realtimeTracking = new RealtimeTracking("Real-time Tracking", "Live GPS tracking and location updates");
        this.statusManagement = new StatusManagement("Status Management", "Package status tracking and transitions");
        this.deliveryConfirmation = new DeliveryConfirmation("Delivery Confirmation", "Digital proof of delivery");
        this.notificationEngine = new NotificationEngine("Notification Engine", "Automated tracking notifications");
    }
    
    // Add custom tracking logic here
}
```

### Using Real-time Tracking
```java
import com.gogidix.courier.tracking.service.RealtimeTrackingService;
import com.gogidix.courier.tracking.model.TrackingUpdate;
import com.gogidix.courier.tracking.model.GpsLocation;

@Service
public class RealtimeTrackingService {
    private final TrackingRepository trackingRepository;
    private final LocationRepository locationRepository;
    private final NotificationService notificationService;
    
    public RealtimeTrackingService(TrackingRepository trackingRepository,
                                 LocationRepository locationRepository,
                                 NotificationService notificationService) {
        this.trackingRepository = trackingRepository;
        this.locationRepository = locationRepository;
        this.notificationService = notificationService;
    }
    
    public TrackingUpdate updatePackageLocation(String trackingNumber, GpsLocation location) {
        Package packageInfo = trackingRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + trackingNumber));
        
        // Create location update
        LocationUpdate locationUpdate = new LocationUpdate();
        locationUpdate.setPackageId(packageInfo.getId());
        locationUpdate.setLatitude(location.getLatitude());
        locationUpdate.setLongitude(location.getLongitude());
        locationUpdate.setTimestamp(LocalDateTime.now());
        locationUpdate.setAccuracy(location.getAccuracy());
        
        locationRepository.save(locationUpdate);
        
        // Update package status if needed
        PackageStatus newStatus = determineStatusFromLocation(packageInfo, location);
        if (newStatus != packageInfo.getStatus()) {
            updatePackageStatus(packageInfo, newStatus);
        }
        
        // Create tracking update
        TrackingUpdate trackingUpdate = new TrackingUpdate();
        trackingUpdate.setTrackingNumber(trackingNumber);
        trackingUpdate.setCurrentLocation(location);
        trackingUpdate.setStatus(packageInfo.getStatus());
        trackingUpdate.setEstimatedDelivery(calculateEstimatedDelivery(packageInfo, location));
        trackingUpdate.setLastUpdated(LocalDateTime.now());
        
        // Send real-time notification
        notificationService.sendTrackingUpdate(packageInfo.getCustomerId(), trackingUpdate);
        
        return trackingUpdate;
    }
    
    public List<TrackingHistory> getTrackingHistory(String trackingNumber) {
        Package packageInfo = trackingRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + trackingNumber));
        
        return locationRepository.findByPackageIdOrderByTimestamp(packageInfo.getId())
                .stream()
                .map(this::convertToTrackingHistory)
                .collect(Collectors.toList());
    }
}
```

### Using Delivery Confirmation
```java
import com.gogidix.courier.tracking.service.DeliveryConfirmationService;
import com.gogidix.courier.tracking.model.DeliveryProof;

@Service
public class DeliveryConfirmationService {
    private final DeliveryRepository deliveryRepository;
    private final PackageRepository packageRepository;
    private final FileStorageService fileStorageService;
    
    @Transactional
    public DeliveryConfirmation confirmDelivery(String trackingNumber, DeliveryProof proof) {
        Package packageInfo = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + trackingNumber));
        
        if (packageInfo.getStatus() != PackageStatus.OUT_FOR_DELIVERY) {
            throw new InvalidDeliveryStateException("Package is not out for delivery");
        }
        
        DeliveryConfirmation confirmation = new DeliveryConfirmation();
        confirmation.setPackageId(packageInfo.getId());
        confirmation.setDriverId(proof.getDriverId());
        confirmation.setDeliveryTime(LocalDateTime.now());
        confirmation.setRecipientName(proof.getRecipientName());
        confirmation.setRecipientSignature(proof.getSignature());
        confirmation.setDeliveryLocation(proof.getDeliveryLocation());
        
        // Store delivery photo if provided
        if (proof.getDeliveryPhoto() != null) {
            String photoUrl = fileStorageService.storeDeliveryPhoto(
                trackingNumber, proof.getDeliveryPhoto());
            confirmation.setDeliveryPhotoUrl(photoUrl);
        }
        
        // Update package status
        packageInfo.setStatus(PackageStatus.DELIVERED);
        packageInfo.setDeliveredAt(LocalDateTime.now());
        packageRepository.save(packageInfo);
        
        // Save delivery confirmation
        deliveryRepository.save(confirmation);
        
        // Send delivery notification
        notificationService.sendDeliveryNotification(packageInfo.getCustomerId(), confirmation);
        
        // Update driver statistics
        updateDriverDeliveryStats(proof.getDriverId());
        
        return confirmation;
    }
    
    public DeliveryConfirmation getDeliveryConfirmation(String trackingNumber) {
        Package packageInfo = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + trackingNumber));
        
        return deliveryRepository.findByPackageId(packageInfo.getId())
                .orElseThrow(() -> new DeliveryConfirmationNotFoundException("Delivery confirmation not found"));
    }
}
```

### Using Status Management
```java
import com.gogidix.courier.tracking.service.StatusManagementService;
import com.gogidix.courier.tracking.model.StatusTransition;

@Service
public class StatusManagementService {
    private final PackageRepository packageRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final BusinessRuleEngine businessRuleEngine;
    
    public void updatePackageStatus(String trackingNumber, PackageStatus newStatus, String reason) {
        Package packageInfo = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + trackingNumber));
        
        PackageStatus currentStatus = packageInfo.getStatus();
        
        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new InvalidStatusTransitionException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
        
        // Create status history entry
        StatusHistory statusHistory = new StatusHistory();
        statusHistory.setPackageId(packageInfo.getId());
        statusHistory.setPreviousStatus(currentStatus);
        statusHistory.setNewStatus(newStatus);
        statusHistory.setReason(reason);
        statusHistory.setTimestamp(LocalDateTime.now());
        statusHistory.setUpdatedBy(getCurrentUser());
        
        statusHistoryRepository.save(statusHistory);
        
        // Update package status
        packageInfo.setStatus(newStatus);
        packageInfo.setLastStatusUpdate(LocalDateTime.now());
        packageRepository.save(packageInfo);
        
        // Execute business rules for status change
        businessRuleEngine.executeStatusChangeRules(packageInfo, currentStatus, newStatus);
        
        // Send status notification
        notificationService.sendStatusUpdateNotification(packageInfo.getCustomerId(), 
                trackingNumber, newStatus, reason);
        
        // Log status change
        logger.info("Package {} status changed from {} to {} by {} - Reason: {}", 
                trackingNumber, currentStatus, newStatus, getCurrentUser(), reason);
    }
    
    public List<StatusHistory> getStatusHistory(String trackingNumber) {
        Package packageInfo = packageRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new PackageNotFoundException("Package not found: " + trackingNumber));
        
        return statusHistoryRepository.findByPackageIdOrderByTimestamp(packageInfo.getId());
    }
    
    private boolean isValidStatusTransition(PackageStatus current, PackageStatus target) {
        return StatusTransition.isValid(current, target);
    }
}
```

## Best Practices
1. **Security**: Always use SecurityConfig for tracking data protection and customer privacy
2. **Validation**: Use the Validator utility for all tracking numbers and GPS coordinates
3. **Logging**: Use the Logger utility for comprehensive tracking operation logging
4. **Error Handling**: Handle tracking errors gracefully with customer-friendly messages
5. **Performance**: Use caching for frequently accessed tracking data
6. **Real-time Updates**: Ensure minimal latency for location and status updates
7. **Data Privacy**: Implement proper access controls for sensitive tracking information
8. **Integration**: Seamless integration with 3PL tracking systems and GPS providers
9. **Reliability**: Implement retry mechanisms for critical tracking operations
10. **Scalability**: Design for high-volume tracking requests and real-time updates