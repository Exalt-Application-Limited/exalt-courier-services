# International Shipping Service

Part of the Social E-commerce Ecosystem - Courier Services Domain

## Description

The International Shipping Service handles all aspects of cross-border shipping, customs documentation, and international logistics. It integrates with the Third-Party Integration Service to provide seamless access to multiple shipping carriers for international shipments.

## Key Features

- **International Shipment Management**: Create, track, and manage international shipments
- **Customs Documentation**: Generate and manage customs declarations and documentation
- **Country Restrictions**: Manage country-specific shipping restrictions and requirements
- **Tariff Calculation**: Estimate duties and taxes based on HS codes and destination countries
- **Compliance Approval**: Workflow for approving shipments that require export compliance review
- **Provider Integration**: Seamless integration with the Third-Party Integration Service for carrier operations

## Architecture

The service follows a layered architecture:

- **API Layer**: RESTful controllers for various operations
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access interfaces for persistence
- **Model Layer**: Domain entities and value objects
- **Client Layer**: Communication with external services

## Domain Models

- **InternationalShipment**: Core entity for international shipments
- **CustomsDeclaration**: For customs documentation and clearance
- **CustomsItem**: Item-level details required by customs
- **CountryRestriction**: For managing shipping restrictions by country
- **TariffRate**: For calculating duties and taxes based on HS codes

## External Integrations

- **Third-Party Integration Service**: For carrier operations (DHL, FedEx, UPS)

## Development

### Prerequisites
- Java 11+
- PostgreSQL
- Docker
- Spring Boot

### Setup
1. Clone this repository
2. Configure database connection in `application.properties`
3. Run PostgreSQL: `docker run --name postgres-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=international_shipping -p 5432:5432 -d postgres`
4. Build the project: `./mvnw clean install`
5. Run the application: `./mvnw spring-boot:run`

### API Endpoints

#### International Shipment Endpoints
- `POST /api/international/shipments`: Create a new international shipment
- `GET /api/international/shipments/{referenceId}`: Get shipment by reference ID
- `PUT /api/international/shipments/{referenceId}`: Update an existing shipment
- `POST /api/international/shipments/{referenceId}/submit`: Submit shipment to carrier
- `GET /api/international/shipments/check-eligibility`: Check shipping eligibility

#### Customs Declaration Endpoints
- `POST /api/international/customs`: Create a new customs declaration
- `PUT /api/international/customs/{referenceId}`: Update a customs declaration
- `POST /api/international/customs/{referenceId}/submit`: Submit customs declaration

#### Country Restriction Endpoints
- `GET /api/international/restrictions/{countryCode}`: Get restrictions for a country
- `GET /api/international/restrictions/embargoed`: Get all embargoed countries

#### Tariff Rate Endpoints
- `POST /api/international/tariffs/calculate`: Calculate duties and taxes
- `GET /api/international/tariffs/country/{countryCode}`: Get tariffs for a country

### Testing
- Unit tests: `./mvnw test`
- Integration tests: `./mvnw verify`

## Deployment

The service can be deployed using Docker:

```bash
# Build Docker image
docker build -t international-shipping-service .

# Run container
docker run -p 8084:8084 international-shipping-service
```

## Dependencies

- Spring Boot: Web framework
- Spring Data JPA: Data access
- Spring Cloud OpenFeign: Service-to-service communication
- PostgreSQL: Database
- Lombok: Reduce boilerplate code
- Jakarta Validation: Input validation
