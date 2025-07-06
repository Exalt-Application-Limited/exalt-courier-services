# Tracking Service Setup Documentation

## Development Setup

### Prerequisites
- **Java 17+**: Required for Spring Boot 3.x compatibility
- **Maven 3.9+**: Build and dependency management
- **Docker & Docker Compose**: Container orchestration for local development
- **PostgreSQL 14+**: Primary database for tracking data
- **Redis 6.2+**: Caching layer for performance optimization
- **Elasticsearch 8.x**: Search engine for tracking history and analytics
- **Apache Kafka 3.x**: Event streaming platform
- **IDE**: IntelliJ IDEA or Eclipse recommended

### Local Development

#### 1. Clone Repository
```bash
git clone <repository-url>
cd tracking-service
```

#### 2. Start Dependencies
```bash
# Start all required services
docker-compose up -d postgres redis elasticsearch kafka zookeeper

# Verify services are running
docker-compose ps
```

#### 3. Configure Environment
```bash
# Database Configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=tracking_db
export DB_USERNAME=tracking_user
export DB_PASSWORD=tracking_pass

# Redis Configuration
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=redis_pass

# Elasticsearch Configuration
export ELASTICSEARCH_URL=http://localhost:9200
export ELASTICSEARCH_USERNAME=elastic
export ELASTICSEARCH_PASSWORD=elastic_pass

# Kafka Configuration
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_SECURITY_PROTOCOL=PLAINTEXT

# Security Configuration
export JWT_SECRET=your-256-bit-secret-key-for-tracking-service
export JWT_EXPIRATION=86400
export API_RATE_LIMIT=1000

# External Services
export GPS_PROVIDER_API_KEY=your-gps-provider-key
export NOTIFICATION_SERVICE_URL=http://localhost:8081
export COURIER_MANAGEMENT_URL=http://localhost:8082
export FILE_STORAGE_SERVICE_URL=http://localhost:8083
```

#### 4. Build and Run
```bash
# Build the application
mvn clean install

# Run locally
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 5. Verify Installation
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Test tracking endpoint
curl http://localhost:8080/api/tracking/TR123456789

# Verify WebSocket connection
wscat -c ws://localhost:8080/tracking/realtime
```

### Database Setup

#### PostgreSQL Schema Creation
```sql
-- Create database and user
CREATE DATABASE tracking_db;
CREATE USER tracking_user WITH PASSWORD 'tracking_pass';
GRANT ALL PRIVILEGES ON DATABASE tracking_db TO tracking_user;

-- Connect to tracking_db
\c tracking_db;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO tracking_user;
```

#### Database Migration
```bash
# Run Flyway migrations
mvn flyway:migrate

# Check migration status
mvn flyway:info

# Clean database (development only)
mvn flyway:clean
```

#### Sample Data Loading
```bash
# Load test data
mvn exec:java -Dexec.mainClass="com.gogidix.courier.tracking.util.TestDataLoader"

# Load production sample data
mvn exec:java -Dexec.mainClass="com.gogidix.courier.tracking.util.SampleDataLoader" -Dexec.args="production"
```

### Redis Setup

#### Redis Configuration
```bash
# Connect to Redis
redis-cli

# Set basic configuration
CONFIG SET maxmemory 1gb
CONFIG SET maxmemory-policy allkeys-lru
CONFIG SET save "900 1 300 10 60 10000"

# Verify configuration
CONFIG GET maxmemory
CONFIG GET maxmemory-policy
```

#### Cache Warming
```bash
# Warm up cache with frequently accessed data
curl -X POST http://localhost:8080/admin/cache/warm

# Verify cache status
curl http://localhost:8080/actuator/metrics/cache.size
```

### Elasticsearch Setup

#### Index Creation
```bash
# Create tracking index
curl -X PUT "localhost:9200/tracking-data" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "tracking_number": {"type": "keyword"},
      "location": {"type": "geo_point"},
      "status": {"type": "keyword"},
      "timestamp": {"type": "date"},
      "customer_id": {"type": "keyword"},
      "driver_id": {"type": "keyword"}
    }
  }
}'

# Create delivery confirmations index
curl -X PUT "localhost:9200/delivery-confirmations" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "tracking_number": {"type": "keyword"},
      "delivery_location": {"type": "geo_point"},
      "delivery_time": {"type": "date"},
      "recipient_name": {"type": "text"},
      "driver_id": {"type": "keyword"}
    }
  }
}'
```

### Kafka Setup

#### Topic Creation
```bash
# Create tracking topics
kafka-topics.sh --create --topic tracking-location-updates \
  --bootstrap-server localhost:9092 --partitions 6 --replication-factor 1

kafka-topics.sh --create --topic tracking-status-changes \
  --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic delivery-confirmations \
  --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Verify topics
kafka-topics.sh --list --bootstrap-server localhost:9092
```

## Testing

### Unit Tests
```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=RealtimeTrackingServiceTest

# Run tests with coverage
mvn test jacoco:report
```

### Integration Tests
```bash
# Run integration tests
mvn verify -P integration-tests

# Run integration tests with test containers
mvn verify -P integration-tests -Dspring.profiles.active=testcontainers
```

### Load Testing

#### Using Apache Bench
```bash
# Test location updates endpoint
ab -n 1000 -c 10 -H "Authorization: Bearer <token>" \
  -T "application/json" \
  -p location-update.json \
  http://localhost:8080/api/tracking/TR123456789/location

# Test tracking queries
ab -n 2000 -c 20 \
  http://localhost:8080/api/tracking/TR123456789
```

#### Using K6
```bash
# Run load test script
k6 run scripts/tracking-load-test.js

# Run with specific parameters
k6 run --vus 50 --duration 30s scripts/tracking-load-test.js
```

#### Load Test Scripts
```javascript
// scripts/tracking-load-test.js
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  vus: 10,
  duration: '30s',
};

export default function() {
  let trackingNumber = 'TR' + Math.floor(Math.random() * 1000000);
  
  // Test tracking query
  let response = http.get(`http://localhost:8080/api/tracking/${trackingNumber}`);
  check(response, {
    'tracking query status is 200 or 404': (r) => r.status === 200 || r.status === 404,
    'response time < 500ms': (r) => r.timings.duration < 500,
  });
  
  // Test location update
  let locationUpdate = {
    latitude: 40.7128 + Math.random() * 0.01,
    longitude: -74.0060 + Math.random() * 0.01,
    accuracy: 5.0,
    timestamp: new Date().toISOString()
  };
  
  let updateResponse = http.post(
    `http://localhost:8080/api/tracking/${trackingNumber}/location`,
    JSON.stringify(locationUpdate),
    {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer test-token'
      }
    }
  );
  
  check(updateResponse, {
    'location update status is 200 or 201': (r) => r.status === 200 || r.status === 201,
  });
}
```

### API Testing

#### Health Check
```bash
# Application health
curl http://localhost:8080/actuator/health

# Detailed health check
curl http://localhost:8080/actuator/health/db
curl http://localhost:8080/actuator/health/redis
curl http://localhost:8080/actuator/health/kafka
```

#### Authentication Testing
```bash
# Login to get JWT token
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass"}'

# Use token for authenticated requests
export TOKEN="your-jwt-token-here"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/tracking/TR123456789
```

#### Tracking API Testing
```bash
# Create test package
curl -X POST http://localhost:8080/api/tracking/packages \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "TR123456789",
    "customerId": "uuid",
    "origin": {"address": "123 Main St", "coordinates": [40.7128, -74.0060]},
    "destination": {"address": "456 Oak Ave", "coordinates": [40.7589, -73.9851]}
  }'

# Update location
curl -X POST http://localhost:8080/api/tracking/TR123456789/location \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "latitude": 40.7128,
    "longitude": -74.0060,
    "accuracy": 5.0,
    "timestamp": "2023-10-15T14:30:00Z"
  }'

# Get tracking information
curl http://localhost:8080/api/tracking/TR123456789

# Get location history
curl http://localhost:8080/api/tracking/TR123456789/history

# Confirm delivery
curl -X POST http://localhost:8080/api/tracking/TR123456789/delivery \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "driverId": "uuid",
    "recipientName": "John Doe",
    "deliveryLocation": {"latitude": 40.7589, "longitude": -73.9851},
    "deliveryNotes": "Left at front door"
  }'
```

#### WebSocket Testing
```bash
# Test WebSocket connection using wscat
npm install -g wscat

# Connect to real-time tracking
wscat -c ws://localhost:8080/tracking/realtime?token=$TOKEN

# Send subscription message
{"action": "subscribe", "trackingNumber": "TR123456789"}

# Send location update
{"action": "location", "trackingNumber": "TR123456789", "latitude": 40.7128, "longitude": -74.0060}
```

## Production Setup

### Infrastructure Requirements

#### Hardware Specifications
- **CPU**: 4+ cores per instance (8+ for high-load environments)
- **Memory**: 8+ GB per instance (16+ GB recommended)
- **Storage**: 100+ GB SSD (500+ GB for high-volume tracking)
- **Network**: 1+ Gbps bandwidth with low latency
- **Load Balancer**: Nginx, HAProxy, or cloud load balancer

#### Scaling Recommendations
- **Small deployment**: 2-3 instances, 50K packages/day
- **Medium deployment**: 5-8 instances, 500K packages/day
- **Large deployment**: 10+ instances, 5M+ packages/day

### Database Configuration

#### PostgreSQL Production Settings
```conf
# postgresql.conf
max_connections = 200
shared_buffers = 4GB
effective_cache_size = 12GB
work_mem = 64MB
maintenance_work_mem = 1GB
checkpoint_segments = 64
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
random_page_cost = 1.1
effective_io_concurrency = 200
```

#### Connection Pooling
```yaml
# HikariCP configuration
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

### Redis Configuration

#### Production Settings
```conf
# redis.conf
maxmemory 8gb
maxmemory-policy allkeys-lru
save 900 1 300 10 60 10000
tcp-keepalive 300
timeout 0
tcp-backlog 511
databases 16
```

#### Redis Cluster Setup
```bash
# Create Redis cluster
redis-cli --cluster create \
  127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 \
  127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005 \
  --cluster-replicas 1
```

### Kubernetes Deployment

#### Deployment Configuration
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tracking-service
  namespace: courier-services
spec:
  replicas: 5
  selector:
    matchLabels:
      app: tracking-service
  template:
    metadata:
      labels:
        app: tracking-service
    spec:
      containers:
      - name: tracking-service
        image: tracking-service:v1.0.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "4Gi"
            cpu: "2000m"
          limits:
            memory: "8Gi"
            cpu: "4000m"
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: tracking-secrets
              key: db-host
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

#### Service Configuration
```yaml
apiVersion: v1
kind: Service
metadata:
  name: tracking-service
  namespace: courier-services
spec:
  selector:
    app: tracking-service
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

#### Horizontal Pod Autoscaler
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: tracking-service-hpa
  namespace: courier-services
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: tracking-service
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
```

### Security Configuration

#### SSL/TLS Setup
```yaml
# ingress-tls.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: tracking-service-ingress
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - tracking.example.com
    secretName: tracking-tls
  rules:
  - host: tracking.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: tracking-service
            port:
              number: 80
```

#### Security Best Practices
- Enable SSL/TLS encryption for all communications
- Configure firewall rules to restrict access
- Implement proper authentication and authorization
- Set up monitoring and alerting for security events
- Regular security audits and penetration testing
- Implement backup and disaster recovery procedures
- Configure log rotation and secure log storage

### Monitoring and Observability

#### Prometheus Configuration
```yaml
# prometheus-config.yaml
global:
  scrape_interval: 15s
scrape_configs:
- job_name: 'tracking-service'
  static_configs:
  - targets: ['tracking-service:8080']
  metrics_path: '/actuator/prometheus'
```

#### Grafana Dashboard
```json
{
  "dashboard": {
    "title": "Tracking Service Dashboard",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_requests_total{job=\"tracking-service\"}[5m])"
          }
        ]
      },
      {
        "title": "Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_request_duration_seconds_bucket{job=\"tracking-service\"}[5m]))"
          }
        ]
      }
    ]
  }
}
```

### Backup and Recovery

#### Database Backup Strategy
```bash
# Daily backup script
#!/bin/bash
BACKUP_DIR="/backups/tracking-service"
DATE=$(date +%Y%m%d_%H%M%S)

# Create backup
pg_dump -h $DB_HOST -U $DB_USERNAME -d $DB_NAME | gzip > "$BACKUP_DIR/tracking_backup_$DATE.sql.gz"

# Retain last 30 days
find $BACKUP_DIR -name "tracking_backup_*.sql.gz" -mtime +30 -delete
```

#### Recovery Procedures
```bash
# Restore from backup
gunzip -c tracking_backup_20231015_143000.sql.gz | psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME

# Verify data integrity
psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME -c "SELECT COUNT(*) FROM packages;"
```

## Troubleshooting

### Common Issues

#### Application Won't Start
1. Check Java version: `java --version`
2. Verify database connectivity: `pg_isready -h $DB_HOST -p $DB_PORT`
3. Check Redis connection: `redis-cli -h $REDIS_HOST ping`
4. Review application logs: `docker logs tracking-service`

#### Performance Issues
1. Monitor JVM metrics: `curl localhost:8080/actuator/metrics/jvm.memory.used`
2. Check database performance: `SELECT * FROM pg_stat_activity;`
3. Monitor Redis performance: `redis-cli INFO stats`
4. Review slow queries: `SELECT query FROM pg_stat_statements ORDER BY total_time DESC;`

#### WebSocket Connection Issues
1. Check firewall rules for WebSocket traffic
2. Verify load balancer WebSocket support
3. Test direct connection: `wscat -c ws://localhost:8080/tracking/realtime`
4. Review WebSocket logs in application

### Getting Help
- Check the operations documentation for monitoring and troubleshooting
- Review API documentation for endpoint specifications
- Consult architecture documentation for system design details
- Contact the development team for critical issues
