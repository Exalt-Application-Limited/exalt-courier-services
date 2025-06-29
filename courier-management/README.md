# Courier Management Service

Part of the Social E-commerce Ecosystem - courier-services domain

## Description

The Courier Management Service handles local courier operations as part of the hierarchical courier services domain. This service is responsible for managing courier profiles, assignments, and performance metrics at the local courier office level. It serves as the interface between the local courier office and their field staff (drivers/riders) who perform last-mile deliveries.

## Domain Architecture

The Courier Management Service operates within a larger hierarchical structure:

### Hierarchical Management Structure
1. **HQ Admin Dashboard** (Global Level)
   - Global oversight of all courier operations
   - Management of regional administrators
   - Global policy and pricing control

2. **Regional Admin Dashboard** (Country/Region Level)
   - Aggregates and manages local courier networks
   - Routes ALL courier requests to appropriate local networks
   - Regional compliance and performance monitoring

3. **Local Courier Management** (Local Office Level) <- **THIS SERVICE**
   - Local operations management
   - Driver/rider assignment and coordination
   - Walk-in customer service

4. **Field Staff** (Drivers/Riders)
   - Last-mile delivery execution
   - Mobile app-based assignment management
   - Real-time status updates

### Integration Flow
1. All requests (walk-in, corporate, individual) route through the regional system
2. Regional system allocates to appropriate local courier networks
3. Local networks (via this service) assign specific drivers/riders for last-mile delivery

## Features

- **Courier Management**: Create, update, and manage courier profiles, skills, and vehicles
- **Assignment Management**: Create and track delivery assignments, with status updates and notifications
- **Intelligent Assignment Algorithm**: Automatically assign deliveries to the most suitable couriers based on various factors
- **Performance Metrics**: Track and analyze courier performance with detailed metrics and reporting
- **Mobile App Integration**: Backend for the courier driver mobile application
- **Delivery Zone Management**: Define and manage delivery zones and courier service areas

## API Endpoints

### Courier Management
- `POST /api/couriers` - Create a new courier
- `GET /api/couriers` - Get all couriers (paginated)
- `GET /api/couriers/{id}` - Get courier by ID
- `PUT /api/couriers/{id}` - Update a courier
- `DELETE /api/couriers/{id}` - Delete a courier
- `PUT /api/couriers/{id}/status` - Update courier status
- `PUT /api/couriers/{id}/location` - Update courier location
- `GET /api/couriers/search` - Search couriers by name
- `GET /api/couriers/status/{status}` - Get couriers by status
- `GET /api/couriers/zone/{zoneId}` - Get couriers by delivery zone
- `GET /api/couriers/nearby` - Find available couriers near location

### Assignment Management
- `POST /api/assignments` - Create a new assignment
- `GET /api/assignments` - Get all assignments (paginated)
- `GET /api/assignments/{id}` - Get assignment by ID
- `PUT /api/assignments/{id}` - Update an assignment
- `DELETE /api/assignments/{id}` - Delete an assignment
- `PUT /api/assignments/{id}/courier/{courierId}` - Assign to courier
- `PUT /api/assignments/{id}/accept` - Accept an assignment
- `PUT /api/assignments/{id}/start` - Start an assignment
- `PUT /api/assignments/{id}/complete` - Complete an assignment
- `PUT /api/assignments/{id}/cancel` - Cancel an assignment
- `GET /api/assignments/courier/{courierId}` - Get assignments by courier
- `GET /api/assignments/status/{status}` - Get assignments by status
- `GET /api/assignments/overdue` - Get overdue assignments

### Assignment Algorithm
- `POST /api/algorithm/assign` - Assign optimal courier to an assignment
- `POST /api/algorithm/batch-assign` - Batch assign optimal couriers
- `GET /api/algorithm/recommend/{assignmentId}` - Recommend couriers for assignment
- `POST /api/algorithm/optimize-routes` - Optimize routes for assignments
- `POST /api/algorithm/balance-workload` - Balance workload across couriers

### Performance Metrics
- `GET /api/metrics/courier/{courierId}/date/{date}` - Get metrics for date
- `GET /api/metrics/courier/{courierId}/range` - Get metrics for date range
- `GET /api/metrics/top-performers` - Get top performing couriers
- `GET /api/metrics/trends` - Get performance metric trends

## Technical Stack

- Java 11
- Spring Boot
- Spring Data JPA
- Spring Cloud (for service discovery)
- H2 Database (for development)
- PostgreSQL (for production)
- Lombok
- SpringDoc (for API documentation)
- Firebase Cloud Messaging (for mobile notifications)

## Setup and Development

### Prerequisites
- JDK 11 or higher
- Maven
- Docker (optional)

### Local Development
1. Clone this repository
2. Run `mvn clean install` to build the application
3. Run `mvn spring-boot:run` to start the service locally

### Docker
1. Build the Docker image: `docker build -t courier-management-service .`
2. Run the container: `docker run -p 8085:8085 courier-management-service`

### API Documentation
Swagger UI is available at: http://localhost:8085/swagger-ui.html

## Integration with Other Services

- **Routing Service**: Gets optimal routes for assignments
- **Tracking Service**: Updates shipment statuses based on assignment progress
- **Driver Mobile App**: Provides assignment information and receives status updates
- **Order Service**: Receives delivery information for customer orders
