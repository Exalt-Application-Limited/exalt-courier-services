# Database Query Optimization Plan - WalkInShipmentRepository

## Current Performance Analysis

The `WalkInShipmentRepository` interface contains numerous methods for querying shipment data. Many of these queries could benefit from indexing strategies and query optimizations to improve performance, especially as the data volume grows.

## Recommended Optimizations

### 1. Add Database Indexes

```sql
-- Add index for the tracking number (unique identifier)
CREATE INDEX idx_tracking_number ON walk_in_shipment (tracking_number);

-- Add index for customer ID (frequent lookup)
CREATE INDEX idx_customer_id ON walk_in_shipment (customer_id);

-- Add index for origin location
CREATE INDEX idx_origin_id ON walk_in_shipment (origin_id);

-- Add index for shipment status
CREATE INDEX idx_status ON walk_in_shipment (status);

-- Add index for creation date (for date range queries)
CREATE INDEX idx_creation_date ON walk_in_shipment (creation_date);

-- Add index for estimated delivery date
CREATE INDEX idx_estimated_delivery_date ON walk_in_shipment (estimated_delivery_date);

-- Add compound index for status and creation date (for filtered date ranges)
CREATE INDEX idx_status_creation_date ON walk_in_shipment (status, creation_date);

-- Add index for international field
CREATE INDEX idx_international ON walk_in_shipment (international);

-- Add index for service type
CREATE INDEX idx_service_type ON walk_in_shipment (service_type);

-- Add index for staff handling
CREATE INDEX idx_handled_by_staff ON walk_in_shipment (handled_by_staff_id);
```

### 2. Repository Method Optimizations

#### Method: `findByCustomerId`
For frequent customer-specific queries, consider implementing pagination by default or adding result limits.

```java
Page<WalkInShipment> findByCustomerIdOrderByCreationDateDesc(Long customerId, Pageable pageable);
List<WalkInShipment> findTop10ByCustomerIdOrderByCreationDateDesc(Long customerId);
```

#### Method: `findShipmentsRequiringAction`
Optimize the JPQL query to use indexed fields:

```java
@Query("SELECT s FROM WalkInShipment s WHERE s.status IN :actionStatuses")
List<WalkInShipment> findShipmentsRequiringAction(@Param("actionStatuses") List<ShipmentStatus> actionStatuses);
```

Then pass the appropriate statuses as a parameter to leverage the status index.

#### Method: `calculateTotalRevenueByOriginAndDateRange`
Add a cached version for common date ranges:

```java
@Cacheable(value = "revenueStats", key = "'origin_' + #originId + '_daily_' + #date.toLocalDate()")
@Query("SELECT SUM(s.totalCost) FROM WalkInShipment s WHERE s.origin.id = :originId " +
       "AND DATE(s.creationDate) = DATE(:date)")
BigDecimal calculateDailyRevenueByOrigin(
        @Param("originId") Long originId,
        @Param("date") LocalDateTime date);
```

### 3. Pagination and Limit Implementation

Modify service layer methods to use pagination or result limits for potentially large result sets:

```java
// In service implementation
public List<WalkInShipment> findShipmentsByStatus(ShipmentStatus status) {
    PageRequest limit = PageRequest.of(0, 100); // Limit to 100 results
    return shipmentRepository.findByStatus(status, limit).getContent();
}
```

### 4. Strategic Data Fetching

Implement specific methods for common dashboard queries to reduce data transfer:

```java
// For dashboard counts
@Query("SELECT s.status as status, COUNT(s) as count FROM WalkInShipment s " +
       "WHERE s.creationDate >= :cutoffDate GROUP BY s.status")
List<ShipmentStatusCount> getShipmentStatusCounts(@Param("cutoffDate") LocalDateTime cutoffDate);

// For revenue summary
@Query("SELECT FUNCTION('date_trunc', 'day', s.creationDate) as day, " +
       "SUM(s.totalCost) as revenue FROM WalkInShipment s " +
       "WHERE s.creationDate BETWEEN :startDate AND :endDate " +
       "GROUP BY FUNCTION('date_trunc', 'day', s.creationDate)")
List<DailyRevenue> getDailyRevenueBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
```

### 5. Database Migrations for Indexing

Create a Flyway or Liquibase migration to add these indexes without disrupting the application:

```java
// V2__Add_Shipment_Indexes.sql
ALTER TABLE walk_in_shipment ADD INDEX idx_tracking_number (tracking_number);
ALTER TABLE walk_in_shipment ADD INDEX idx_customer_id (customer_id);
ALTER TABLE walk_in_shipment ADD INDEX idx_status (status);
ALTER TABLE walk_in_shipment ADD INDEX idx_creation_date (creation_date);
ALTER TABLE walk_in_shipment ADD INDEX idx_status_creation_date (status, creation_date);
```

### 6. Result Set Projection

Use projections to return only the required fields for specific use cases:

```java
public interface ShipmentSummary {
    Long getId();
    String getTrackingNumber();
    ShipmentStatus getStatus();
    LocalDateTime getCreationDate();
    String getRecipientName();
    String getDestinationCity();
}

@Query("SELECT s.id as id, s.trackingNumber as trackingNumber, " +
       "s.status as status, s.creationDate as creationDate, " +
       "s.recipientName as recipientName, s.destinationCity as destinationCity " +
       "FROM WalkInShipment s WHERE s.customerId = :customerId")
List<ShipmentSummary> findShipmentSummariesByCustomerId(@Param("customerId") Long customerId);
```

### 7. Batch Processing for Reports

For heavy reporting queries, consider implementing a batch processing approach:

```java
@Async
@Transactional(readOnly = true)
public void generateShipmentReport(LocalDateTime startDate, LocalDateTime endDate, 
                                 AsyncReportCallback callback) {
    int page = 0;
    boolean hasMore = true;
    
    while (hasMore) {
        PageRequest pageRequest = PageRequest.of(page, 500);
        Page<WalkInShipment> shipments = shipmentRepository
            .findByCreationDateBetween(startDate, endDate, pageRequest);
        
        // Process this batch
        processShipmentBatch(shipments.getContent(), callback);
        
        hasMore = !shipments.isLast();
        page++;
    }
    
    callback.reportComplete();
}
```

## Implementation Priorities

1. **First Priority**: Add database indexes for critical fields (tracking number, customer ID, status)
2. **Second Priority**: Implement pagination for all methods that could return large result sets
3. **Third Priority**: Create projections for dashboard and reporting queries
4. **Fourth Priority**: Add caching for frequently accessed queries
5. **Fifth Priority**: Implement batch processing for heavy reporting operations

## Expected Performance Improvements

- 50-80% reduction in query time for typical customer and tracking lookups
- 30-50% reduction in query time for status-based dashboards
- Improved server resource utilization
- Better scalability under high load
- More efficient reporting capabilities
