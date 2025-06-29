# Regional Admin System

## Overview

The Regional Admin System is a core component of the Courier Services Domain, responsible for managing regional operations, administrators, and courier locations. It serves as the middle tier in the three-tiered management structure, connecting the Global HQ Admin Dashboard with local courier facilities.

## Features

- **Regional Settings Management**: Configure and customize courier services for specific geographical regions
- **Regional Admin Management**: Create and manage user accounts for regional administrators with role-based access control
- **Courier Location Management**: Maintain a network of courier locations (branches, hubs, sorting facilities)
- **Service Configuration**: Define region-specific service parameters and operational rules
- **Regional Performance Tracking**: Monitor KPIs and metrics at the regional level

## Architecture

The Regional Admin System follows a layered architecture:

1. **Controller Layer**: RESTful API endpoints for client interactions
2. **Service Layer**: Business logic and workflow implementation
3. **Repository Layer**: Data access and persistence
4. **Model Layer**: Core domain entities and data structures

## Key Components

### Models

- **RegionalSettings**: Configuration settings for specific geographical regions
- **RegionalAdmin**: User accounts for regional administrators with role-based permissions
- **CourierLocation**: Physical branch locations within a region's courier network

### Services

- **RegionalSettingsService**: Manage regional configuration and settings
- **RegionalAdminService**: User account and permission management for regional staff
- **CourierLocationService**: Maintain the network of courier facilities in a region

### RESTful APIs

The following API endpoints are available:

- `/api/v1/regional-settings`: Manage regional configuration settings
- `/api/v1/regional-admins`: Manage regional administrator accounts
- `/api/v1/courier-locations`: Manage courier facilities and branches

## Integration Points

- **Global HQ Admin Dashboard**: Receives global configurations and policies
- **Local Courier Facilities**: Provides operational parameters and metrics collection
- **Customer-Facing Applications**: Supplies location data for customer pickup and delivery options

## Getting Started

### Prerequisites

- Java 11+
- Maven or Gradle
- PostgreSQL database
- Spring Boot 2.5+

### Configuration

1. Configure the database connection in `application.properties` or `application.yml`
2. Set up security parameters for authentication and authorization
3. Configure integration endpoints with Global HQ Admin and local facilities

### Running the Application

```bash
./mvnw spring-boot:run
```

## Security

The Regional Admin System implements several security measures:

- Role-Based Access Control (RBAC)
- JWT-based authentication
- Two-factor authentication (optional)
- Secure API endpoints with OAuth2

## Monitoring and Management

The application exposes actuator endpoints for monitoring:

- `/actuator/health`: System health information
- `/actuator/metrics`: Runtime metrics and statistics
- `/actuator/info`: Application information

## Development

### Building the Application

```bash
./mvnw clean package
```

### Running Tests

```bash
./mvnw test
```

## API Documentation

API documentation is available via Swagger UI at `/swagger-ui.html` when the application is running.

## License

This project is part of the Social Ecommerce Ecosystem and is subject to its licensing terms.
