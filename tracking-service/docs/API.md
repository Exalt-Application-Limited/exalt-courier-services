# Tracking Service API Documentation

## Core API

### TrackingApplication
- TrackingApplication(): Default constructor
- TrackingApplication(String name, String description): Constructor with name and description
- UUID getId(): Get the application ID
- String getName(): Get the application name
- void setName(String name): Set the application name
- String getDescription(): Get the application description
- void setDescription(String description): Set the application description
- LocalDateTime getCreatedAt(): Get the creation timestamp
- LocalDateTime getUpdatedAt(): Get the last update timestamp

### SecurityConfig
- SecurityConfig(): Default constructor with secure tracking defaults
- List<String> getAllowedOrigins(): Get the allowed origins for CORS
- void setAllowedOrigins(List<String> allowedOrigins): Set the allowed origins
- boolean isCsrfEnabled(): Check if CSRF protection is enabled
- void setCsrfEnabled(boolean csrfEnabled): Enable or disable CSRF protection
- String getTokenExpirationSeconds(): Get the token expiration time in seconds
- void setTokenExpirationSeconds(String tokenExpirationSeconds): Set the token expiration time
- String getJwtSecret(): Get the JWT secret for tracking authentication
- void setJwtSecret(String jwtSecret): Set the JWT secret

## Real-time Tracking API

### RealtimeTracking
- RealtimeTracking(): Default constructor
- RealtimeTracking(String name, String description): Constructor with name and description
- void updateLocation(String trackingNumber, GpsLocation location): Update package location
- TrackingUpdate getCurrentLocation(String trackingNumber): Get current package location
- List<TrackingHistory> getLocationHistory(String trackingNumber): Get location history
- void enableRealTimeUpdates(String trackingNumber): Enable real-time updates
- void disableRealTimeUpdates(String trackingNumber): Disable real-time updates

### GpsLocation
- GpsLocation(): Default constructor
- GpsLocation(double latitude, double longitude): Constructor with coordinates
- double getLatitude(): Get the latitude coordinate
- void setLatitude(double latitude): Set the latitude coordinate
- double getLongitude(): Get the longitude coordinate
- void setLongitude(double longitude): Set the longitude coordinate
- double getAccuracy(): Get the GPS accuracy in meters
- void setAccuracy(double accuracy): Set the GPS accuracy
- LocalDateTime getTimestamp(): Get the location timestamp
- void setTimestamp(LocalDateTime timestamp): Set the location timestamp
- double getAltitude(): Get the altitude
- void setAltitude(double altitude): Set the altitude
- double getSpeed(): Get the speed in km/h
- void setSpeed(double speed): Set the speed

### TrackingUpdate
- TrackingUpdate(): Default constructor
- TrackingUpdate(String trackingNumber, GpsLocation location, PackageStatus status): Constructor with details
- String getTrackingNumber(): Get the tracking number
- void setTrackingNumber(String trackingNumber): Set the tracking number
- GpsLocation getCurrentLocation(): Get the current location
- void setCurrentLocation(GpsLocation location): Set the current location
- PackageStatus getStatus(): Get the package status
- void setStatus(PackageStatus status): Set the package status
- LocalDateTime getEstimatedDelivery(): Get estimated delivery time
- void setEstimatedDelivery(LocalDateTime estimatedDelivery): Set estimated delivery time
- LocalDateTime getLastUpdated(): Get last update timestamp
- String getStatusMessage(): Get human-readable status message
- void setStatusMessage(String message): Set status message

## Status Management API

### StatusManagement
- StatusManagement(): Default constructor
- StatusManagement(String name, String description): Constructor with name and description
- void updateStatus(String trackingNumber, PackageStatus status, String reason): Update package status
- PackageStatus getCurrentStatus(String trackingNumber): Get current package status
- List<StatusHistory> getStatusHistory(String trackingNumber): Get status change history
- boolean isValidTransition(PackageStatus from, PackageStatus to): Validate status transition

### PackageStatus (Enum)
- CREATED: Package created in system
- PICKED_UP: Package picked up from sender
- IN_TRANSIT: Package in transit to destination
- OUT_FOR_DELIVERY: Package out for delivery
- DELIVERED: Package successfully delivered
- DELIVERY_ATTEMPTED: Delivery attempt made but failed
- RETURNED_TO_SENDER: Package returned to sender
- LOST: Package lost in transit
- DAMAGED: Package damaged during transit
- CANCELLED: Package delivery cancelled

### StatusHistory
- StatusHistory(): Default constructor
- UUID getId(): Get the status history ID
- UUID getPackageId(): Get the package ID
- PackageStatus getPreviousStatus(): Get the previous status
- PackageStatus getNewStatus(): Get the new status
- String getReason(): Get the reason for status change
- LocalDateTime getTimestamp(): Get the change timestamp
- String getUpdatedBy(): Get the user who made the change
- String getLocation(): Get the location where status changed
- String getNotes(): Get additional notes

## Delivery Confirmation API

### DeliveryConfirmation
- DeliveryConfirmation(): Default constructor
- DeliveryConfirmation(String trackingNumber, DeliveryProof proof): Constructor with proof
- void confirmDelivery(String trackingNumber, DeliveryProof proof): Confirm package delivery
- DeliveryConfirmation getDeliveryConfirmation(String trackingNumber): Get delivery confirmation
- boolean isDelivered(String trackingNumber): Check if package is delivered
- void uploadDeliveryPhoto(String trackingNumber, byte[] photo): Upload delivery photo

### DeliveryProof
- DeliveryProof(): Default constructor
- UUID getDriverId(): Get the driver ID
- void setDriverId(UUID driverId): Set the driver ID
- String getRecipientName(): Get the recipient name
- void setRecipientName(String name): Set the recipient name
- byte[] getSignature(): Get the digital signature
- void setSignature(byte[] signature): Set the digital signature
- byte[] getDeliveryPhoto(): Get the delivery photo
- void setDeliveryPhoto(byte[] photo): Set the delivery photo
- GpsLocation getDeliveryLocation(): Get the delivery location
- void setDeliveryLocation(GpsLocation location): Set the delivery location
- LocalDateTime getDeliveryTime(): Get the delivery timestamp
- String getDeliveryNotes(): Get delivery notes
- void setDeliveryNotes(String notes): Set delivery notes

## REST API Endpoints

### Package Tracking Endpoints
- **GET /api/tracking/{trackingNumber}**: Get package tracking information
- **GET /api/tracking/{trackingNumber}/location**: Get current package location
- **GET /api/tracking/{trackingNumber}/history**: Get complete tracking history
- **POST /api/tracking/{trackingNumber}/location**: Update package location (driver only)
- **PUT /api/tracking/{trackingNumber}/status**: Update package status (staff only)

### Real-time Tracking Endpoints
- **GET /api/tracking/{trackingNumber}/realtime**: Enable real-time tracking updates
- **DELETE /api/tracking/{trackingNumber}/realtime**: Disable real-time tracking
- **GET /api/tracking/realtime/driver/{driverId}**: Get all packages for driver with real-time updates
- **POST /api/tracking/batch/location**: Batch update multiple package locations

### Delivery Confirmation Endpoints
- **POST /api/tracking/{trackingNumber}/delivery**: Confirm package delivery
- **GET /api/tracking/{trackingNumber}/delivery**: Get delivery confirmation
- **POST /api/tracking/{trackingNumber}/delivery/photo**: Upload delivery photo
- **GET /api/tracking/{trackingNumber}/delivery/proof**: Download delivery proof

### Status Management Endpoints
- **GET /api/tracking/status/{status}**: Get all packages with specific status
- **PUT /api/tracking/{trackingNumber}/status/{status}**: Update package status
- **GET /api/tracking/{trackingNumber}/status/history**: Get status change history
- **POST /api/tracking/status/bulk**: Bulk status updates

### Driver Endpoints
- **GET /api/tracking/driver/{driverId}/packages**: Get packages assigned to driver
- **PUT /api/tracking/driver/{driverId}/location**: Update driver location
- **GET /api/tracking/driver/{driverId}/route**: Get optimized delivery route
- **POST /api/tracking/driver/{driverId}/delivery-attempt**: Record delivery attempt

### Customer Endpoints
- **GET /api/tracking/customer/{customerId}/packages**: Get customer's packages
- **GET /api/tracking/customer/search**: Search packages by criteria
- **POST /api/tracking/notifications/subscribe**: Subscribe to tracking notifications
- **DELETE /api/tracking/notifications/unsubscribe**: Unsubscribe from notifications

### Administrative Endpoints
- **GET /api/tracking/admin/metrics**: Get tracking service metrics
- **GET /api/tracking/admin/packages/delayed**: Get delayed packages
- **GET /api/tracking/admin/packages/lost**: Get lost packages
- **POST /api/tracking/admin/packages/{trackingNumber}/investigate**: Start package investigation

## Authentication & Authorization

### Required Headers
```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
X-API-Version: v1
```

### Roles and Permissions
- **CUSTOMER**: Can track own packages, receive notifications
- **DRIVER**: Can update package locations, confirm deliveries
- **STAFF**: Can update package status, view all packages
- **ADMIN**: Full access to all tracking operations
- **SYSTEM**: Internal service-to-service communication

## Error Handling

### Standard Error Response
```json
{
  "error": {
    "code": "PACKAGE_NOT_FOUND",
    "message": "Package with tracking number TR123456789 was not found",
    "timestamp": "2023-10-15T14:30:00Z",
    "path": "/api/tracking/TR123456789"
  }
}
```

### Common Error Codes
- **PACKAGE_NOT_FOUND** (404): Package not found
- **INVALID_TRACKING_NUMBER** (400): Invalid tracking number format
- **UNAUTHORIZED** (401): Authentication required
- **FORBIDDEN** (403): Insufficient permissions
- **INVALID_STATUS_TRANSITION** (400): Invalid status change
- **LOCATION_UPDATE_FAILED** (500): Failed to update location
- **DELIVERY_ALREADY_CONFIRMED** (409): Package already delivered