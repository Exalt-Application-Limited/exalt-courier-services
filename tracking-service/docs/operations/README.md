# Tracking Service Operations Documentation

## Deployment

### Prerequisites
- **Docker 20.10+**
- **Kubernetes cluster 1.24+**
- **PostgreSQL 14+** (primary database)
- **Redis 6.2+** (caching layer)
- **Elasticsearch 8.x** (search and analytics)
- **Apache Kafka 3.x** (event streaming)
- **Helm 3.8+** (package management)

### Environment Variables
```bash
# Database Configuration
export DB_HOST=tracking-postgres.default.svc.cluster.local
export DB_PORT=5432
export DB_NAME=tracking_db
export DB_USERNAME=tracking_user
export DB_PASSWORD=secure_password

# Redis Configuration
export REDIS_HOST=tracking-redis.default.svc.cluster.local
export REDIS_PORT=6379
export REDIS_PASSWORD=redis_password

# Kafka Configuration
export KAFKA_BOOTSTRAP_SERVERS=kafka-cluster.kafka.svc.cluster.local:9092
export KAFKA_SECURITY_PROTOCOL=PLAINTEXT

# Elasticsearch Configuration
export ELASTICSEARCH_HOST=elasticsearch.elastic.svc.cluster.local
export ELASTICSEARCH_PORT=9200
export ELASTICSEARCH_USERNAME=elastic
export ELASTICSEARCH_PASSWORD=elastic_password

# Security Configuration
export JWT_SECRET=your-256-bit-secret-key
export JWT_EXPIRATION=86400
export API_RATE_LIMIT=1000

# External Services
export GPS_PROVIDER_API_KEY=your-gps-provider-key
export NOTIFICATION_SERVICE_URL=http://notification-service:8080
export COURIER_MANAGEMENT_URL=http://courier-management:8080
```

### Deployment Steps

#### 1. Build and Push Docker Image
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t tracking-service:v1.0.0 .

# Tag and push to registry
docker tag tracking-service:v1.0.0 your-registry.com/tracking-service:v1.0.0
docker push your-registry.com/tracking-service:v1.0.0
```

#### 2. Deploy to Kubernetes
```bash
# Deploy using Helm
helm upgrade --install tracking-service ./helm-charts/tracking-service \
  --namespace courier-services \
  --create-namespace \
  --set image.tag=v1.0.0 \
  --set database.host=tracking-postgres \
  --set redis.host=tracking-redis

# Or deploy using kubectl
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/ingress.yaml
```

#### 3. Verify Deployment
```bash
# Check pod status
kubectl get pods -n courier-services -l app=tracking-service

# Check logs
kubectl logs -f deployment/tracking-service -n courier-services

# Verify health endpoints
kubectl port-forward svc/tracking-service 8080:8080 -n courier-services
curl http://localhost:8080/actuator/health
```

#### 4. Database Migration
```bash
# Run database migrations
kubectl exec -it deployment/tracking-service -n courier-services -- \
  java -jar app.jar --spring.flyway.migrate-on-startup=true

# Verify migration status
curl http://localhost:8080/actuator/flyway
```

## Monitoring

### Health Checks

#### Application Health Endpoints
- **Overall Health**: `GET /actuator/health`
- **Liveness Probe**: `GET /actuator/health/liveness`
- **Readiness Probe**: `GET /actuator/health/readiness`
- **Database Health**: `GET /actuator/health/db`
- **Redis Health**: `GET /actuator/health/redis`
- **Kafka Health**: `GET /actuator/health/kafka`

#### Custom Health Indicators
```yaml
# Health check configuration
management:
  health:
    tracking:
      enabled: true
      show-details: always
    redis:
      enabled: true
    elasticsearch:
      enabled: true
    kafka:
      enabled: true
```

### Metrics Collection

#### Application Metrics
```yaml
# Micrometer configuration
management:
  metrics:
    export:
      prometheus:
        enabled: true
    web:
      server:
        request:
          autotime:
            enabled: true
```

#### Key Performance Indicators (KPIs)
- **Location Updates per Second**: `tracking.location.updates.rate`
- **Tracking Query Response Time**: `tracking.query.response.time`
- **Delivery Confirmation Rate**: `tracking.delivery.confirmation.rate`
- **GPS Update Latency**: `tracking.gps.latency`
- **Status Transition Success Rate**: `tracking.status.transition.success`

#### Business Metrics
- **Active Packages**: Number of packages currently being tracked
- **Delivery Success Rate**: Percentage of successful deliveries
- **Average Delivery Time**: Mean time from pickup to delivery
- **Customer Satisfaction**: Tracking experience ratings
- **Driver Efficiency**: Deliveries per driver per hour

### Logging

#### Structured Logging Configuration
```yaml
# Logback configuration
logging:
  level:
    com.gogidix.courier.tracking: INFO
    org.springframework.kafka: WARN
    org.elasticsearch: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/tracking-service.log
```

#### Log Categories
1. **Application Logs**: General application events and errors
2. **Audit Logs**: Security events, user actions, data changes
3. **Performance Logs**: Response times, resource usage
4. **Business Logs**: Tracking events, deliveries, status changes
5. **Integration Logs**: External service calls, API responses

#### Log Aggregation
```yaml
# ELK Stack Integration
filebeat:
  inputs:
    - type: log
      paths:
        - /var/log/tracking-service/*.log
      fields:
        service: tracking-service
        environment: production
  output:
    elasticsearch:
      hosts: ["elasticsearch:9200"]
      index: "tracking-service-%{+yyyy.MM.dd}"
```

### Alerting Rules

#### Critical Alerts
```yaml
# Prometheus alerting rules
groups:
  - name: tracking-service-critical
    rules:
      - alert: TrackingServiceDown
        expr: up{job="tracking-service"} == 0
        for: 1m
        annotations:
          summary: "Tracking Service is down"
          
      - alert: HighErrorRate
        expr: rate(http_requests_total{status=~"5.."}[5m]) > 0.1
        for: 2m
        annotations:
          summary: "High error rate detected"
          
      - alert: DatabaseConnectionFailure
        expr: tracking_database_connections_active == 0
        for: 30s
        annotations:
          summary: "Database connection failure"
```

#### Warning Alerts
```yaml
  - name: tracking-service-warning
    rules:
      - alert: HighLatency
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2
        for: 5m
        annotations:
          summary: "High response latency"
          
      - alert: LowCacheHitRate
        expr: tracking_cache_hit_rate < 0.8
        for: 10m
        annotations:
          summary: "Low cache hit rate"
```

## Security Operations

### Access Control
- **Authentication**: JWT token validation for all API endpoints
- **Authorization**: Role-based access control (RBAC)
- **API Security**: Rate limiting and DDoS protection
- **Data Encryption**: TLS 1.3 for data in transit

### Security Monitoring
```bash
# Monitor failed authentication attempts
grep "Authentication failed" /var/log/tracking-service/security.log

# Check for suspicious activity
grep "Rate limit exceeded" /var/log/tracking-service/access.log

# Monitor privilege escalation attempts
grep "Access denied" /var/log/tracking-service/security.log
```

### Backup and Recovery

#### Database Backup
```bash
# Automated daily backup
pg_dump -h $DB_HOST -U $DB_USERNAME -d $DB_NAME > backup_$(date +%Y%m%d).sql

# Point-in-time recovery setup
echo "archive_mode = on" >> postgresql.conf
echo "archive_command = 'cp %p /var/lib/postgresql/archive/%f'" >> postgresql.conf
```

#### Redis Backup
```bash
# Enable Redis persistence
redis-cli CONFIG SET save "60 1000"
redis-cli BGSAVE
```

## Troubleshooting

### Common Issues

#### 1. High Location Update Latency
**Symptoms**: GPS updates taking > 5 seconds to process
**Diagnosis**:
```bash
# Check Kafka lag
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group tracking-location-consumer

# Check database performance
SELECT * FROM pg_stat_activity WHERE query LIKE '%location%';
```
**Solutions**:
- Scale up Kafka partitions
- Optimize database indexes
- Increase consumer instances

#### 2. Tracking Query Timeout
**Symptoms**: API responses taking > 30 seconds
**Diagnosis**:
```bash
# Check Redis cache hit rate
redis-cli INFO stats | grep keyspace_hits

# Check database connection pool
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
```
**Solutions**:
- Increase Redis memory allocation
- Optimize database queries
- Implement query result caching

#### 3. Failed Delivery Confirmations
**Symptoms**: Delivery confirmations not being saved
**Diagnosis**:
```bash
# Check file storage service
curl http://file-storage-service:8080/health

# Check Kafka producer metrics
curl http://localhost:8080/actuator/metrics/kafka.producer.record-send-rate
```
**Solutions**:
- Verify file storage connectivity
- Check Kafka producer configuration
- Implement retry mechanisms

### Emergency Procedures

#### Service Outage Response
1. **Immediate Actions** (0-5 minutes):
   ```bash
   # Check service status
   kubectl get pods -n courier-services
   
   # Scale up replicas if needed
   kubectl scale deployment tracking-service --replicas=5
   
   # Check dependencies
   curl http://postgres:5432/health
   curl http://redis:6379/ping
   ```

2. **Short-term Mitigation** (5-30 minutes):
   ```bash
   # Enable maintenance mode
   kubectl patch configmap tracking-config \
     --patch '{"data":{"maintenance.enabled":"true"}}'
   
   # Redirect traffic to backup region
   kubectl patch ingress tracking-ingress \
     --patch '{"spec":{"rules":[{"host":"tracking-backup.example.com"}]}}'
   ```

3. **Recovery Actions** (30+ minutes):
   ```bash
   # Restore from backup
   psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME < backup_latest.sql
   
   # Verify data integrity
   curl http://localhost:8080/admin/verify-data
   
   # Disable maintenance mode
   kubectl patch configmap tracking-config \
     --patch '{"data":{"maintenance.enabled":"false"}}'
   ```

#### Data Corruption Response
1. **Immediate Assessment**:
   ```bash
   # Check data consistency
   curl http://localhost:8080/admin/data-integrity-check
   
   # Identify affected packages
   psql -c "SELECT tracking_number FROM packages WHERE last_updated > NOW() - INTERVAL '1 hour'"
   ```

2. **Rollback Procedures**:
   ```bash
   # Stop all writes
   kubectl scale deployment tracking-service --replicas=0
   
   # Restore from point-in-time backup
   pg_restore -h $DB_HOST -U $DB_USERNAME -d $DB_NAME backup_before_incident.sql
   
   # Restart services
   kubectl scale deployment tracking-service --replicas=3
   ```

### Performance Optimization

#### Database Optimization
```sql
-- Create indexes for common queries
CREATE INDEX CONCURRENTLY idx_packages_tracking_number ON packages(tracking_number);
CREATE INDEX CONCURRENTLY idx_location_updates_package_timestamp ON location_updates(package_id, timestamp);
CREATE INDEX CONCURRENTLY idx_status_history_package_timestamp ON status_history(package_id, timestamp);

-- Partition large tables
CREATE TABLE location_updates_2023_q4 PARTITION OF location_updates
FOR VALUES FROM ('2023-10-01') TO ('2024-01-01');
```

#### Cache Optimization
```bash
# Monitor Redis memory usage
redis-cli INFO memory

# Set appropriate eviction policy
redis-cli CONFIG SET maxmemory-policy allkeys-lru

# Monitor cache hit ratio
redis-cli INFO stats | grep keyspace_hits
```

#### JVM Tuning
```bash
# Optimize garbage collection
export JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms2g -Xmx4g"

# Enable JVM monitoring
export JAVA_OPTS="$JAVA_OPTS -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=tracking-service.jfr"
```

## Maintenance Procedures

### Regular Maintenance Tasks
1. **Daily**: Monitor system health, check error logs, verify backups
2. **Weekly**: Review performance metrics, update security patches
3. **Monthly**: Database maintenance, cleanup old data, capacity planning
4. **Quarterly**: Disaster recovery testing, security audits, performance reviews

### Capacity Planning
- Monitor CPU, memory, and storage usage trends
- Plan for peak traffic periods (holidays, events)
- Scale resources based on business growth projections
- Implement auto-scaling policies for dynamic load management