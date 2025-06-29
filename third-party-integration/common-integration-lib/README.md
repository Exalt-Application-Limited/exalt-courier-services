# Third-Party Logistics (3PL) Integration Library

This library provides a standardized interface for integrating with various third-party logistics (3PL) providers like FedEx, UPS, and DHL. It abstracts away the differences between carrier APIs and provides a unified way to interact with shipping services.

## Features

- Standardized interfaces for shipping operations (create shipment, track, cancel, etc.)
- Support for multiple carriers (FedEx, UPS, DHL) with a common API
- Carrier-agnostic data models for requests and responses
- Automatic retry mechanisms and error handling
- Performance monitoring and SLA tracking
- Cost optimization suggestions
- Health status monitoring
- REST API for external clients

## Architecture

The library follows a layered architecture:

1. **Common Models**: Standardized data models (ShipmentRequest, ShipmentResponse, TrackingRequest, etc.)
2. **Provider Interfaces**: Common interfaces for all shipping providers (ShippingProviderService)
3. **Abstract Base Implementation**: Common functionality for all providers (AbstractShippingProviderService)
4. **Provider Implementations**: Carrier-specific implementations (FedExShippingProviderService, UpsShippingProviderService, etc.)
5. **Integration Service**: Unified facade over all providers (ShippingIntegrationService)
6. **REST API**: HTTP endpoints for client applications (ShippingIntegrationController)

### Key Components

- **ShippingProviderService**: Interface that all carrier implementations must implement
- **AbstractShippingProviderService**: Base class with common functionality (metrics, error handling, retry logic)
- **ShippingProviderFactory**: Factory for obtaining provider implementations by carrier ID
- **ShippingIntegrationService**: Unified service for interacting with all carriers
- **ShippingIntegrationController**: REST controller exposing the integration functionality

## Supported Carriers

- **FedEx**: Full integration with the FedEx Web Services API
- **UPS**: Integration with UPS API services
- **DHL**: Support for DHL Express API

## Usage

### Creating a Shipment

```java
// Option 1: Using the integration service
ShipmentRequest request = ShipmentRequest.builder()
    .referenceId("ORDER-12345")
    .sender(senderAddress)
    .recipient(recipientAddress)
    .packages(packages)
    .serviceType(ShipmentRequest.ServiceType.EXPRESS)
    .shipmentDate(LocalDate.now())
    .build();

ShipmentResponse response = shippingIntegrationService.createShipment(request, "FEDEX");

// Option 2: Using REST API
POST /api/shipping/carriers/FEDEX/shipments
{
  "referenceId": "ORDER-12345",
  "sender": { ... },
  "recipient": { ... },
  "packages": [ ... ],
  "serviceType": "EXPRESS",
  "shipmentDate": "2023-04-15"
}
```

### Tracking a Shipment

```java
// Option 1: Using the integration service
TrackingResponse response = shippingIntegrationService.trackShipment("1Z999AA10123456784", "UPS");

// Option 2: Using REST API
GET /api/shipping/carriers/UPS/track/1Z999AA10123456784
```

### Comparing Rates Across Carriers

```java
// Option 1: Using the integration service
Map<String, Map<String, Object>> rates = shippingIntegrationService.compareRates(shipmentRequest);

// Option 2: Using REST API
POST /api/shipping/rates/compare
{
  "referenceId": "ORDER-12345",
  "sender": { ... },
  "recipient": { ... },
  "packages": [ ... ],
  "serviceType": "EXPRESS",
  "shipmentDate": "2023-04-15"
}
```

## Error Handling

The library provides standardized error handling. For example, the `ShipmentResponse` includes an `errors` field that contains any errors that occurred during shipment creation. Each error has a severity level (INFO, WARNING, ERROR, FATAL) and a message.

## Metrics and SLA Tracking

The library automatically tracks performance metrics for each carrier:

- Request count
- Error count
- Average response time
- SLA violations

You can access these metrics through the API:

```
GET /api/shipping/metrics/sla
```

## Health Monitoring

The library includes health checks for each carrier. You can access the health status through the API:

```
GET /api/shipping/health
```

## Extending the Library

### Adding a New Carrier

1. Create carrier-specific models (e.g., `NewCarrierShipmentRequest`)
2. Implement the `ShippingProviderService` interface or extend `AbstractShippingProviderService`
3. Create request/response mappers for converting between standardized and carrier-specific formats
4. Implement the API client for communicating with the carrier's API

Example:

```java
@Service
public class NewCarrierShippingProviderService extends AbstractShippingProviderService {
    
    private static final String CARRIER_ID = "NEW_CARRIER";
    private static final String CARRIER_NAME = "New Carrier";
    
    // Implement required methods
    
    @Override
    public String getCarrierId() {
        return CARRIER_ID;
    }
    
    @Override
    public String getCarrierName() {
        return CARRIER_NAME;
    }
    
    // Implement abstract methods from AbstractShippingProviderService
}
```

## Configuration

The library supports configuration through Spring properties:

```properties
# Common configuration
integration.shipment.max-retries=3
integration.shipment.retry-delay-ms=2000
integration.shipment.enable-metrics=true
integration.shipment.sla-threshold-ms=5000

# FedEx-specific configuration
integration.fedex.api-key=your-api-key
integration.fedex.api-password=your-password
integration.fedex.account-number=your-account-number

# UPS-specific configuration
integration.ups.api-key=your-api-key
integration.ups.user-id=your-user-id
integration.ups.password=your-password

# DHL-specific configuration
integration.dhl.api-key=your-api-key
integration.dhl.site-id=your-site-id
```

## Dependencies

- Spring Boot 2.6+
- Spring Cloud (for service discovery)
- Lombok
- Jackson
- Java 11+

## License

This library is proprietary software owned by the Social Ecommerce Ecosystem.
