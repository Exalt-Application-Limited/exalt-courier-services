# Courier Network Locations Service

A microservice for managing physical courier network locations within the Micro-Social-Ecommerce-Ecosystem platform.

## Overview

The Courier Network Locations Service is responsible for managing physical courier locations such as branch offices, sorting centers, pickup points, and other physical infrastructure in the courier network. It provides APIs for location management, operating hours, staff management, walk-in customer management, shipment processing, payment processing, and notifications.

## Features

- **Physical Location Management**
  - Create, update, and delete physical locations
  - Search for locations by various criteria (type, country, state, city)
  - Find nearby locations based on coordinates
  - Track capacity utilization

- **Operating Hours Management**
  - Configure operating hours for each location
  - Check if a location is open at a specific time

- **Staff Management**
  - Assign staff to locations
  - Manage staff roles and responsibilities
  - Track staff schedules

- **Walk-in Customer Management**
  - Register and manage walk-in customers
  - Track customer history

- **Shipment Processing**
  - Process walk-in shipments
  - Track shipment status

- **Payment Processing**
  - Process payments for walk-in services
  - Support multiple payment methods

- **Notification Service**
  - Send notifications to staff and customers

## Architecture

The service follows a typical Spring Boot microservice architecture with the following layers:

- **Controller Layer**: REST controllers for exposing APIs
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access layer for database operations
- **Model Layer**: Domain entities
- **Exception Handling**: Centralized exception handling
- **Configuration**: Application configuration including security, database, etc.

## API Documentation

API documentation is available via Swagger/OpenAPI at the following endpoint:

```
http://localhost:8082/courier-locations/swagger-ui.html
```

## Dependencies

- Spring Boot: Core framework
- Spring Data JPA: Data access layer
- PostgreSQL: Database
- Spring Cloud: For service discovery and configuration
- Spring Security: For securing APIs
- Lombok: For reducing boilerplate code
- Swagger/OpenAPI: For API documentation

## Getting Started

### Prerequisites

- Java 11 or higher
- PostgreSQL database
- Maven

### Configuration

The application is configured via application.yml. Key configuration properties:

- Server port: 8082
- Context path: /courier-locations
- Database connection details
- Security settings
- Integration endpoints

### Building the Application

```bash
mvn clean package
```

### Running the Application

```bash
java -jar target/courier-network-locations-0.0.1-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

### Running Tests

```bash
mvn test
```

## Deployment

The service can be deployed in various environments:

- **Development**: Local development environment
- **Staging**: Pre-production testing environment
- **Production**: Live environment

Environment-specific configurations can be set using Spring profiles.

## Integration Points

The service integrates with several other services in the ecosystem:

- **Authentication Service**: For user authentication and authorization
- **Regional Admin Service**: For regional administration operations
- **Payment Gateway**: For processing payments
- **Notification Service**: For sending SMS and email notifications

## Contributing

1. Create a feature branch
2. Implement your changes
3. Write tests for your changes
4. Submit a pull request

## License

Proprietary

## Contact

For support or inquiries, contact support@example.com
