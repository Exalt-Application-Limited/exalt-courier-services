# Courier Onboarding Service

Part of the Social E-commerce Ecosystem - Courier Services Domain

## Description

The Courier Onboarding Service manages the complete lifecycle of courier onboarding from application submission to account activation. This service handles application processing, document verification, background checks, courier profile creation, and integration with third-party shipping providers.

## Features

- **Application Management**: Create, update, and track onboarding applications
- **Document Verification**: Upload, validate, and verify identification and qualification documents
- **Background Checks**: Automate and manage background verification processes
- **Courier Profile Creation**: Generate courier profiles upon application approval
- **Rating System**: Manage courier ratings and performance metrics
- **Third-Party Integration**: Connect with shipping providers for additional courier services

## Tech Stack

- **Framework**: Spring Boot 2.6.x
- **Language**: Java 11
- **Database**: PostgreSQL (Production), H2 (Development/Testing)
- **API Documentation**: OpenAPI 3.0/Swagger
- **Testing**: JUnit 5, Mockito, Spring Test
- **Performance Testing**: JMH Benchmarking

## Architecture

The service follows a layered architecture:

- **Controller Layer**: REST API endpoints
- **Service Layer**: Business logic implementation
- **Repository Layer**: Data access layer
- **Model Layer**: Domain entities and DTOs
- **Client Layer**: Integration with other services

## API Endpoints

### Application Management
```
POST   /api/v1/applications                       # Create new application
GET    /api/v1/applications                       # List all applications
GET    /api/v1/applications/{referenceId}         # Get specific application
PUT    /api/v1/applications/{referenceId}         # Update application
POST   /api/v1/applications/{referenceId}/submit  # Submit application
POST   /api/v1/applications/{referenceId}/review  # Start application review
POST   /api/v1/applications/{referenceId}/approve # Approve application
POST   /api/v1/applications/{referenceId}/reject  # Reject application
```

### Document Management
```
POST   /api/v1/documents/{applicationReferenceId}/upload               # Upload document
GET    /api/v1/documents/{applicationReferenceId}                      # List documents
GET    /api/v1/documents/{applicationReferenceId}/{documentId}         # Download document
PUT    /api/v1/documents/{applicationReferenceId}/document-type/{type}/verify  # Verify document
```

### Background Check
```
POST   /api/v1/background-checks/{applicationReferenceId}/initiate     # Initiate check
GET    /api/v1/background-checks/{applicationReferenceId}/status       # Check status
POST   /api/v1/background-checks/{checkId}/manual-approve              # Manual approval
```

### Courier Profile
```
POST   /api/v1/couriers/from-application/{applicationReferenceId}      # Create profile
GET    /api/v1/couriers/{courierId}                                    # Get profile
PUT    /api/v1/couriers/{courierId}                                    # Update profile
```

### Rating System
```
POST   /api/v1/ratings                                                 # Create rating
GET    /api/v1/ratings/courier/{courierId}                             # Get ratings
POST   /api/v1/ratings/{ratingId}/verify                               # Verify rating
POST   /api/v1/ratings/{ratingId}/hide                                 # Hide rating
POST   /api/v1/ratings/{ratingId}/show                                 # Show rating
```

### Shipping Integration
```
GET    /api/v1/shipping-integration/couriers/{courierId}/shipping-providers  # Get providers
GET    /api/v1/shipping-integration/tracking/{trackingNumber}                # Track shipment
```

## Database Schema

The service uses the following main tables:

- `onboarding_application`: Stores application data and status
- `application_status_history`: Tracks application status changes
- `verification_document`: Manages uploaded documents
- `background_check`: Tracks background verification processes
- `courier_profile`: Stores approved courier information
- `courier_rating`: Manages rating data for couriers

## Integration Points

- **Courier Management Service**: Creates and updates courier records
- **Notification Service**: Sends alerts and updates to applicants
- **Third-Party Integration Service**: Connects with shipping providers
- **Background Check API**: External service for verification

## Development

### Prerequisites
- Java 11 or higher
- Maven 3.6+
- Docker and Docker Compose
- PostgreSQL (optional, for local development without Docker)

### Setup
1. Clone the repository
2. Create `.env` file with required environment variables (see `.env.example`)
3. Run `mvn clean install` to build the project
4. Run `docker-compose up` to start the service with its dependencies

### Running Locally
```bash
# Without Docker
mvn spring-boot:run -Dspring.profiles.active=dev

# With Docker
docker-compose up -d
```

### Testing
```bash
# Unit and integration tests
mvn test

# End-to-end tests only
mvn test -Dtest=*E2ETest

# Performance tests
mvn test -Dtest=OnboardingPerformanceTest
```

## Monitoring and Observability

The service exposes the following monitoring endpoints:

- `/actuator/health`: Health check endpoint
- `/actuator/metrics`: Metrics endpoint
- `/actuator/prometheus`: Prometheus metrics endpoint

## Deployment

### Kubernetes Deployment

Kubernetes deployment files are located in the `k8s/` directory.

```bash
# Apply configurations
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -l app=courier-onboarding
```

### CI/CD Pipeline

The service uses GitHub Actions for CI/CD. The workflows are defined in `.github/workflows/`.

## Documentation

- API documentation: `/swagger-ui/index.html` (when service is running)
- JavaDocs: Generated in `target/site/apidocs/` after running `mvn javadoc:javadoc`
- Additional documentation in `docs/` directory
