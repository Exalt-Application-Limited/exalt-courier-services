# Notification Service

## Overview
The Notification Service manages all courier-related notifications including SMS, email, and push notifications for the Exalt courier ecosystem.

## Features
- Multi-channel notification delivery (SMS, Email, Push)
- Template-based messaging
- Notification scheduling and queuing
- Delivery status tracking
- Failed notification retry mechanism

## API Endpoints
- `POST /api/notifications/send` - Send a notification
- `GET /api/notifications/{id}/status` - Check notification status
- `GET /api/notifications/templates` - Get notification templates
- `POST /api/notifications/templates` - Create notification template

## Configuration
Configure the service using environment variables or application.yml:

```yaml
server:
  port: 8080

spring:
  application:
    name: notification-service
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
```

## Running the Service

### Using Docker
```bash
docker build -t notification-service .
docker run -p 8080:8080 notification-service
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