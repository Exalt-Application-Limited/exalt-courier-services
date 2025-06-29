# Routing Service

Part of the Social E-commerce Ecosystem - Courier Services domain

## Description

The Routing Service provides optimal route calculation, geolocation integration, and delivery time estimation for courier services. It is a critical component in ensuring efficient delivery operations.

## Features

- **Route Management**: Create, retrieve, update, and optimize delivery routes
- **Waypoint Management**: Add, remove, and reorder stops on a route
- **Courier Assignment**: Assign couriers to routes and find nearest available couriers
- **Route Optimization**: Calculate the most efficient path to minimize delivery time and distance
- **ETA Calculation**: Estimate delivery times for shipments based on route data
- **Geolocation Integration**: Find locations and couriers within a specified radius

## API Endpoints

### Routes

- `POST /api/routing/routes` - Create a new route
- `GET /api/routing/routes/{routeId}` - Get a route by ID
- `GET /api/routing/routes/courier/{courierId}` - Get routes by courier
- `GET /api/routing/routes/status/{status}` - Get routes by status
- `GET /api/routing/routes/shipment/{shipmentId}` - Get routes containing a shipment
- `PUT /api/routing/routes/{routeId}/status` - Update route status
- `PUT /api/routing/routes/{routeId}/courier/{courierId}` - Assign courier to route
- `POST /api/routing/routes/{routeId}/start` - Start a route
- `POST /api/routing/routes/{routeId}/complete` - Complete a route
- `POST /api/routing/routes/{routeId}/optimize` - Optimize a route

### Waypoints

- `POST /api/routing/routes/{routeId}/waypoints` - Add a waypoint to a route
- `DELETE /api/routing/routes/{routeId}/waypoints/{waypointId}` - Remove a waypoint from a route

### Couriers

- `GET /api/routing/couriers/nearest` - Find nearest available couriers

### Shipments

- `GET /api/routing/shipments/{shipmentId}/eta` - Calculate ETA for a shipment

### Route Optimization

- `POST /api/routing/optimal-route` - Generate an optimal delivery route

## Technical Stack

- Java 11
- Spring Boot
- Spring Data JPA
- Spring Cloud (for service discovery)
- H2 Database (for development)
- Lombok
- SpringDoc (for API documentation)

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
1. Build the Docker image: `docker build -t routing-service .`
2. Run the container: `docker run -p 8084:8084 routing-service`

### API Documentation
Swagger UI is available at: http://localhost:8084/swagger-ui.html

## Integration with Other Services

- **Tracking Service**: Updates ETAs for shipments based on courier location
- **Courier Management**: Assigns optimal routes to couriers
- **Driver Mobile App**: Provides route information and navigation data
- **Order Service**: Receives delivery information for customer orders
