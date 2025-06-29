# Regional Courier Service

## Overview
The Regional Courier Service manages courier operations within specific geographic regions, handling regional routing, capacity planning, and courier assignment optimization.

## Features
- Regional courier management and assignment
- Territory-based routing optimization
- Regional capacity planning
- Performance tracking by region
- Cross-regional coordination

## API Endpoints
- `GET /api/regional-couriers` - List regional couriers
- `POST /api/regional-couriers` - Create regional courier
- `GET /api/regional-couriers/{id}` - Get regional courier details
- `PUT /api/regional-couriers/{id}` - Update regional courier
- `DELETE /api/regional-couriers/{id}` - Delete regional courier

## Configuration
Configure the service using environment variables or application.yml:

```yaml
server:
  port: 8080

spring:
  application:
    name: regional-courier-service
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

## Running the Service

### Using Docker
```bash
docker build -t regional-courier-service .
docker run -p 8080:8080 regional-courier-service
```

### Using Maven
```bash
mvn spring-boot:run
```

## Health Check
The service provides health check endpoint at `/actuator/health`

## Dependencies
- Spring Boot 2.7.x
- Spring Data JPA
- MySQL/PostgreSQL
- Apache Kafka (for event messaging)