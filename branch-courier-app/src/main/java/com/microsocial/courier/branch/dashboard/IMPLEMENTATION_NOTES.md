# Branch Dashboard Integration Implementation Notes

## Overview

This implementation provides the branch-level component for the dashboard integration in the Micro-Social-Ecommerce Ecosystems courier services. It enables the Branch Courier App to communicate with the Regional Admin Dashboard through a message-based architecture, and to send metrics data for aggregation.

## Key Components

### 1. Dashboard Communication

The dashboard communication is implemented through:

- **BranchDashboardCommunicationHandler**: Responsible for sending and receiving messages to/from the Regional Admin Dashboard
- **BranchDataCacheService**: Caches messages and metrics when offline for later synchronization
- **DashboardMessage**: Message model with type, priority, and content

Messages are communicated via Kafka topics:
- `branch-to-regional-communication`: For messages sent from Branch to Regional
- `regional-to-branch-communication`: For messages sent from Regional to Branch

### 2. Metrics Collection and Reporting

Metrics collection is handled by:

- **BranchMetricsDataProvider**: Collects branch metrics and sends them to the Regional dashboard
- **DeliveryMetrics**: Delivery-related statistics
- **PerformanceMetrics**: Courier performance metrics
- **ResourceMetrics**: Resource utilization metrics

Metrics are sent periodically (default: 5 minutes) and also on-demand when requested.

### 3. API Endpoints

The implementation exposes the following REST endpoints:

- `GET /api/branch/metrics`: Retrieves current branch metrics
- `GET /api/branch/dashboard/status`: Gets dashboard connection status
- `POST /api/branch/dashboard/sync`: Forces synchronization with Regional dashboard
- `GET /api/branch/couriers`: Gets couriers assigned to branch
- `GET /api/branch/deliveries`: Gets deliveries assigned to branch
- `GET /api/branch/resources`: Gets branch resource status

## Data Flow

1. Branch collects metrics automatically every 5 minutes
2. Metrics are sent to the Regional dashboard via Kafka
3. Regional dashboard can request specific data from the Branch
4. Branch responds to Regional requests and sends acknowledgments
5. All messages are cached when connectivity is lost
6. When connectivity is restored, cached messages are automatically sent

## Offline Operation

The implementation includes offline operation support:

1. When connectivity is lost, messages and metrics are stored in the BranchDataCacheService
2. When connectivity is restored (or manually triggered), cached data is sent to the Regional dashboard
3. Cache entries older than 24 hours are automatically pruned to prevent memory issues

## Testing

The implementation includes comprehensive unit tests:

- **BranchDashboardCommunicationHandlerTest**: Tests message sending/receiving
- **BranchMetricsDataProviderTest**: Tests metrics collection and sending

## Configuration Options

Key configuration properties (in application.properties):

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

## Next Steps

1. **Integration with Branch Operations**: Connect the dashboard integration with actual branch operations
2. **Security Enhancements**: Add authentication and authorization for dashboard messages
3. **Advanced Metrics**: Implement more sophisticated metrics collection from real branch data
4. **Monitoring & Alerting**: Add monitoring for communication failures and auto-recovery
5. **End-to-End Testing**: Test with the actual Regional Admin Dashboard 