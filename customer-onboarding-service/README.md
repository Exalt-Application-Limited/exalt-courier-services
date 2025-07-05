# Customer Onboarding Service

Part of the Gogidix Social E-commerce Ecosystem - Courier Services Domain

## Description

The Customer Onboarding Service manages the complete lifecycle of customer onboarding for individual customers accessing courier services via www.gogidixcourier.com. This service handles application processing, KYC verification, document validation, customer account creation, and integration with shared infrastructure services.

## Features

- **Customer Registration**: Complete registration workflow for individual customers
- **KYC Integration**: Seamless integration with shared KYC service for identity verification
- **Document Management**: Upload, validate, and verify customer identification documents
- **Account Creation**: Integration with auth-service for customer account management
- **Status Tracking**: Real-time application status tracking and history
- **Notification Integration**: Customer communication via shared notification service
- **Billing Integration**: Customer billing setup coordination with payment processing service

## Tech Stack

- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **Database**: PostgreSQL (Production), H2 (Development/Testing)
- **API Documentation**: OpenAPI 3.0/Swagger
- **Testing**: JUnit 5, Mockito, Spring Test, Testcontainers
- **Cloud Integration**: Spring Cloud Netflix (Eureka, Feign)
- **Security**: Spring Security with OAuth2 Resource Server

## Architecture

The service follows a layered architecture with integration to shared infrastructure:

- **Controller Layer**: REST API endpoints for customer onboarding
- **Service Layer**: Business logic implementation and orchestration
- **Repository Layer**: Data access layer with JPA
- **Model Layer**: Domain entities and DTOs
- **Client Layer**: Feign clients for shared infrastructure integration

## Shared Infrastructure Integration

This service leverages the following shared infrastructure services:

- **auth-service**: JWT authentication and customer account management
- **kyc-service**: Customer identity verification and KYC compliance
- **document-verification**: Document upload and verification processing
- **notification-service**: Customer communications and alerts
- **payment-processing-service**: Customer billing and payment setup
- **user-profile-service**: Customer profile management

## API Endpoints

### Customer Application Management
```
POST   /api/v1/customer-onboarding/applications                           # Create new customer application
GET    /api/v1/customer-onboarding/applications/{referenceId}            # Get specific application
PUT    /api/v1/customer-onboarding/applications/{referenceId}            # Update application
POST   /api/v1/customer-onboarding/applications/{referenceId}/submit     # Submit application for review
GET    /api/v1/customer-onboarding/applications                          # List all applications (admin)
```

### KYC Verification
```
POST   /api/v1/customer-onboarding/applications/{referenceId}/kyc/initiate  # Start KYC verification
GET    /api/v1/customer-onboarding/applications/{referenceId}/kyc/status    # Get KYC status
```

### Application Decision
```
POST   /api/v1/customer-onboarding/applications/{referenceId}/approve    # Approve application
POST   /api/v1/customer-onboarding/applications/{referenceId}/reject     # Reject application
```

### Customer Account Management
```
POST   /api/v1/customer-onboarding/applications/{referenceId}/activate   # Activate customer account
POST   /api/v1/customer-onboarding/applications/{referenceId}/suspend    # Suspend customer account
```

### Status and History
```
GET    /api/v1/customer-onboarding/applications/{referenceId}/status-history  # Get status history
```

## Database Schema

The service uses the following main tables:

- `customer_onboarding_applications`: Main application data and status
- `customer_application_status_history`: Tracks application status changes
- `customer_verification_documents`: Manages uploaded documents and verification status

## Integration Points

- **Auth Service**: Customer user account creation and management
- **KYC Service**: Identity verification and compliance processing
- **Document Verification Service**: Document upload and verification
- **Notification Service**: Customer communication and alerts
- **Payment Processing Service**: Billing and payment setup
- **User Profile Service**: Customer profile management

## Development

### Prerequisites
- Java 17 or higher
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
mvn test -Dtest=CustomerOnboardingPerformanceTest
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
kubectl get pods -l app=customer-onboarding-service
```

### CI/CD Pipeline

The service uses GitHub Actions for CI/CD. The workflows are defined in `.github/workflows/`.

## Documentation

- API documentation: `/swagger-ui/index.html` (when service is running)
- JavaDocs: Generated in `target/site/apidocs/` after running `mvn javadoc:javadoc`
- Additional documentation in `docs/` directory

## Environment Variables

Key environment variables for configuration:

- `SPRING_PROFILES_ACTIVE`: Application profile (dev, staging, prod)
- `SERVER_PORT`: Server port (default: 8310)
- `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE`: Eureka server URL
- `SPRING_DATASOURCE_URL`: Database connection URL
- `SPRING_DATASOURCE_USERNAME`: Database username
- `SPRING_DATASOURCE_PASSWORD`: Database password

## Business Logic

### Customer Onboarding Workflow

1. **Application Creation**: Customer creates onboarding application
2. **Information Validation**: Basic validation of customer information
3. **KYC Initiation**: Start identity verification process
4. **Document Collection**: Customer uploads required documents
5. **Verification Process**: Automated and manual verification
6. **Account Creation**: Create customer account in auth-service
7. **Billing Setup**: Setup customer billing profile
8. **Account Activation**: Activate customer account for service access

### Status Flow

```
DRAFT → SUBMITTED → KYC_IN_PROGRESS → KYC_APPROVED → UNDER_REVIEW → APPROVED → ACTIVATED
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.