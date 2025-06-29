# Branch Courier App

## Overview

The Branch Courier App is the operational interface for branch-level courier operations in the Micro-Social-Ecommerce Ecosystem. It provides tools for managing couriers, deliveries, and branch resources while integrating with the overall courier services hierarchy.

## Features

- Branch-level courier management
- Delivery tracking and management
- Resource allocation and monitoring
- Real-time dashboard metrics and reporting
- Integration with Regional Admin Dashboard

## Dashboard Integration

The Branch Courier App includes a comprehensive dashboard integration component that connects with the Regional Admin Dashboard to enable seamless communication and data flow across the courier service hierarchy.

### Key Components

1. **BranchDashboardConfig**: Configuration for the Branch-level dashboard communication.
2. **BranchDashboardCommunicationHandler**: Handles bi-directional communication with the Regional Admin Dashboard.
3. **BranchMetricsDataProvider**: Collects and provides branch-specific metrics to the Regional Dashboard.
4. **BranchDataCacheService**: Provides caching capabilities for offline operations, ensuring data is preserved during connectivity issues.

### Metrics Collected

The Branch Courier App collects and reports the following metrics:

- **Delivery Metrics**: Tracks deliveries in progress, completed, and failed; calculates average delivery time and on-time performance.
- **Performance Metrics**: Monitors courier activity, efficiency scores, and customer ratings.
- **Resource Metrics**: Tracks vehicle usage, availability, fuel consumption, and maintenance schedules.

### Communication Features

- **Bi-directional Messaging**: Enables communication between Branch and Regional levels
- **Message Prioritization**: Supports different priority levels for messages
- **Offline Operation**: Caches data during connectivity issues for later synchronization
- **Real-time Metrics**: Scheduled reporting of operational metrics
- **On-demand Data**: Responds to data requests from the Regional Dashboard

## Configuration

Key configuration parameters in `application.properties`:

```properties
# Dashboard Integration Configuration
dashboard.branch.id=${BRANCH_ID:branch-001}
dashboard.region.id=${REGION_ID:region-001}

# Kafka Topics
dashboard.communication.topic.branch-to-regional=branch-to-regional-communication
dashboard.communication.topic.regional-to-branch=regional-to-branch-communication
dashboard.data.topic.branch-metrics=branch-metrics-data

# Metrics Reporting Schedule (in milliseconds) - default 5 minutes
dashboard.metrics.reporting.interval=300000
```

## Getting Started

1. Configure the branch and region IDs in the application properties
2. Ensure Kafka is properly configured for messaging
3. Start the application to begin automatic metrics collection and reporting

## Integration Testing

Run the provided integration tests to ensure proper connectivity with the Regional Admin Dashboard:

```bash
./mvnw test -Dtest=BranchDashboardCommunicationHandlerTest,BranchMetricsDataProviderTest
```

## Development

### Prerequisites
- **Java 17** - For Spring Boot backend
- **Node.js 18+** - For React Native mobile components
- **Maven 3.8+** - For Java dependency management
- **npm/yarn** - For Node.js package management
- **Docker** - For containerization
- **Android Studio** - For Android development (optional)
- **Xcode** - For iOS development (macOS only, optional)

### Technology Stack
This is a **hybrid project** combining:
- **Backend**: Java 17 + Spring Boot 3.x + Maven
- **Mobile**: React Native 0.71.11 + TypeScript + npm

### Setup
1. Clone this repository
2. **Backend setup**:
   ```bash
   mvn clean install
   ```
3. **Mobile setup**:
   ```bash
   npm install
   ```
4. **Docker setup**:
   ```bash
   docker-compose up --build
   ```

### Running the Application

#### Backend (Spring Boot)
```bash
mvn spring-boot:run
```
The backend will be available at http://localhost:8080

#### Mobile (React Native)
```bash
# Start Metro bundler
npm start

# Run on Android
npm run android

# Run on iOS (macOS only)
npm run ios
```

### Testing
- **Java backend tests**: `mvn test`
- **React Native tests**: `npm test`
- **Integration tests**: `mvn verify`
- **All tests**: Run both Maven and npm test suites

## API Documentation

API documentation can be found in the `api-docs` directory.

## Deployment

Deployment instructions can be found in the `docs/operations` directory.
