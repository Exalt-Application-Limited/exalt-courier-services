# Route Optimization Service Operations Documentation

## Deployment

### Prerequisites
- **Docker 20.10+**
- **Kubernetes cluster 1.24+**
- **PostgreSQL 14+** (route data and optimization results)
- **Redis 6.2+** (caching and session storage)
- **Neo4j 5.x** (graph database for road networks)
- **Apache Kafka 3.x** (event streaming)
- **Helm 3.8+** (package management)

### Environment Variables
```bash
# Database Configuration
export DB_HOST=route-postgres.default.svc.cluster.local
export DB_PORT=5432
export DB_NAME=route_optimization_db
export DB_USERNAME=route_user
export DB_PASSWORD=secure_password

# Redis Configuration
export REDIS_HOST=route-redis.default.svc.cluster.local
export REDIS_PORT=6379
export REDIS_PASSWORD=redis_password

# Neo4j Configuration
export NEO4J_HOST=neo4j.graph.svc.cluster.local
export NEO4J_PORT=7687
export NEO4J_USERNAME=neo4j
export NEO4J_PASSWORD=graph_password

# Kafka Configuration
export KAFKA_BOOTSTRAP_SERVERS=kafka-cluster.kafka.svc.cluster.local:9092
export KAFKA_SECURITY_PROTOCOL=PLAINTEXT

# External APIs
export GOOGLE_MAPS_API_KEY=your-google-maps-api-key
export HERE_MAPS_API_KEY=your-here-maps-api-key
export OPENROUTE_API_KEY=your-openroute-api-key

# Optimization Configuration
export MAX_OPTIMIZATION_TIME_SECONDS=300
export DEFAULT_ALGORITHM=GENETIC_ALGORITHM
export POPULATION_SIZE=100
export MAX_GENERATIONS=500
export MUTATION_RATE=0.01

# Security Configuration
export JWT_SECRET=your-256-bit-secret-key
export JWT_EXPIRATION=86400
export API_RATE_LIMIT=500

# Service Endpoints
export DELIVERY_SERVICE_URL=http://delivery-service:8080
export FLEET_SERVICE_URL=http://fleet-management:8080
export TRAFFIC_SERVICE_URL=http://traffic-monitor:8080
```

### Deployment Steps

#### 1. Build and Push Docker Image
```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t route-optimization-service:v1.0.0 .

# Tag and push to registry
docker tag route-optimization-service:v1.0.0 your-registry.com/route-optimization:v1.0.0
docker push your-registry.com/route-optimization:v1.0.0
```

#### 2. Deploy to Kubernetes
```bash
# Deploy using Helm
helm upgrade --install route-optimization ./helm-charts/route-optimization \
  --namespace courier-services \
  --create-namespace \
  --set image.tag=v1.0.0 \
  --set database.host=route-postgres \
  --set redis.host=route-redis \
  --set neo4j.host=neo4j

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
kubectl get pods -n courier-services -l app=route-optimization

# Check logs
kubectl logs -f deployment/route-optimization -n courier-services

# Verify health endpoints
kubectl port-forward svc/route-optimization 8080:8080 -n courier-services
curl http://localhost:8080/actuator/health
```

#### 4. Initialize Graph Database
```bash
# Load road network data into Neo4j
kubectl exec -it deployment/route-optimization -n courier-services -- \
  java -jar app.jar --spring.profiles.active=data-loader

# Verify graph data
curl http://localhost:8080/admin/graph/status
```

## Monitoring

### Health Checks

#### Application Health Endpoints
- **Overall Health**: `GET /actuator/health`
- **Liveness Probe**: `GET /actuator/health/liveness`
- **Readiness Probe**: `GET /actuator/health/readiness`
- **Database Health**: `GET /actuator/health/db`
- **Redis Health**: `GET /actuator/health/redis`
- **Neo4j Health**: `GET /actuator/health/neo4j`
- **External APIs Health**: `GET /actuator/health/traffic-apis`

#### Custom Health Indicators
```yaml
# Health check configuration
management:
  health:
    route-optimization:
      enabled: true
      show-details: always
    redis:
      enabled: true
    neo4j:
      enabled: true
    external-apis:
      enabled: true
      timeout: 5s
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
- **Optimization Requests per Second**: `route.optimization.requests.rate`
- **Average Optimization Time**: `route.optimization.duration.avg`
- **Route Quality Score**: `route.quality.score.avg`
- **Algorithm Success Rate**: `route.algorithm.success.rate`
- **Traffic API Response Time**: `route.traffic.api.response.time`

#### Business Metrics
- **Routes Optimized**: Number of routes optimized per day
- **Fuel Savings**: Estimated fuel saved through optimization
- **Time Savings**: Total time saved across all optimized routes
- **Driver Satisfaction**: Route satisfaction ratings from drivers
- **Optimization Efficiency**: Percentage of routes improved by optimization

### Logging

#### Structured Logging Configuration
```yaml
# Logback configuration
logging:
  level:
    com.gogidix.courier.routing: INFO
    org.springframework.kafka: WARN
    org.neo4j: WARN
    com.optaplanner: INFO
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/route-optimization-service.log
```

#### Log Categories
1. **Application Logs**: General application events and errors
2. **Optimization Logs**: Algorithm execution, performance metrics
3. **Traffic Logs**: External API calls, traffic data updates
4. **Performance Logs**: Response times, resource usage
5. **Business Logs**: Route optimization events, metrics
6. **Integration Logs**: External service calls, API responses

#### Log Aggregation
```yaml
# ELK Stack Integration
filebeat:
  inputs:
    - type: log
      paths:
        - /var/log/route-optimization/*.log
      fields:
        service: route-optimization-service
        environment: production
        component: optimization-engine
  output:
    elasticsearch:
      hosts: ["elasticsearch:9200"]
      index: "route-optimization-%{+yyyy.MM.dd}"
```

### Alerting Rules

#### Critical Alerts
```yaml
# Prometheus alerting rules
groups:
  - name: route-optimization-critical
    rules:
      - alert: RouteOptimizationServiceDown
        expr: up{job="route-optimization"} == 0
        for: 1m
        annotations:
          summary: "Route Optimization Service is down"
          
      - alert: OptimizationTimeoutHigh
        expr: rate(route_optimization_timeout_total[5m]) > 0.1
        for: 3m
        annotations:
          summary: "High optimization timeout rate"
          
      - alert: TrafficAPIDown
        expr: route_traffic_api_up == 0
        for: 2m
        annotations:
          summary: "Traffic API unavailable"
          
      - alert: Neo4jConnectionFailure
        expr: route_neo4j_connections_active == 0
        for: 30s
        annotations:
          summary: "Neo4j graph database connection failure"
```

#### Warning Alerts
```yaml
  - name: route-optimization-warning
    rules:
      - alert: OptimizationLatencyHigh
        expr: histogram_quantile(0.95, rate(route_optimization_duration_seconds_bucket[5m])) > 120
        for: 5m
        annotations:
          summary: "High optimization latency"
          
      - alert: LowRouteQualityScore
        expr: route_quality_score_avg < 0.7
        for: 10m
        annotations:
          summary: "Low route quality scores detected"
          
      - alert: CacheHitRateLow
        expr: route_cache_hit_rate < 0.6
        for: 15m
        annotations:
          summary: "Low cache hit rate"
```

## Security Operations

### Access Control
- **Authentication**: JWT token validation for all API endpoints
- **Authorization**: Role-based access control (RBAC)
- **API Security**: Rate limiting and DDoS protection
- **Data Encryption**: TLS 1.3 for data in transit, encryption at rest

### Security Monitoring
```bash
# Monitor failed authentication attempts
grep "Authentication failed" /var/log/route-optimization/security.log

# Check for suspicious optimization requests
grep "Rate limit exceeded" /var/log/route-optimization/access.log

# Monitor privilege escalation attempts
grep "Access denied" /var/log/route-optimization/security.log

# Check for unusual optimization patterns
grep "Suspicious optimization request" /var/log/route-optimization/business.log
```

### Backup and Recovery

#### Database Backup
```bash
# PostgreSQL backup
pg_dump -h $DB_HOST -U $DB_USERNAME -d $DB_NAME > backup_$(date +%Y%m%d).sql

# Neo4j backup
neo4j-admin dump --database=neo4j --to=/var/backups/neo4j_$(date +%Y%m%d).dump

# Point-in-time recovery setup
echo "archive_mode = on" >> postgresql.conf
echo "archive_command = 'cp %p /var/lib/postgresql/archive/%f'" >> postgresql.conf
```

#### Redis Backup
```bash
# Enable Redis persistence
redis-cli CONFIG SET save "60 1000"
redis-cli BGSAVE

# Backup Redis data
cp /var/lib/redis/dump.rdb /var/backups/redis_$(date +%Y%m%d).rdb
```

## Troubleshooting

### Common Issues

#### 1. Slow Optimization Performance
**Symptoms**: Route optimization taking > 5 minutes
**Diagnosis**:
```bash
# Check algorithm performance
curl http://localhost:8080/actuator/metrics/route.optimization.duration

# Check database performance
SELECT query, total_time, calls FROM pg_stat_statements 
WHERE query LIKE '%route%' ORDER BY total_time DESC;

# Check Neo4j performance
curl http://localhost:7474/db/data/monitor/queries
```
**Solutions**:
- Adjust algorithm parameters (population size, generations)
- Scale up Neo4j memory allocation
- Implement route caching strategies
- Use parallel optimization for large problems

#### 2. Traffic API Failures
**Symptoms**: Routes not considering current traffic
**Diagnosis**:
```bash
# Check API connectivity
curl -H "Authorization: Bearer $GOOGLE_MAPS_API_KEY" \
  "https://maps.googleapis.com/maps/api/directions/json?origin=test&destination=test"

# Check API quota usage
curl http://localhost:8080/admin/api-usage/google-maps

# Monitor API response times
curl http://localhost:8080/actuator/metrics/http.client.requests
```
**Solutions**:
- Implement API fallback mechanisms
- Use cached traffic data when APIs unavailable
- Distribute API calls across multiple keys
- Implement circuit breaker pattern

#### 3. Route Quality Degradation
**Symptoms**: Poor route optimization results, driver complaints
**Diagnosis**:
```bash
# Check route quality metrics
curl http://localhost:8080/admin/route-quality/analysis

# Analyze algorithm performance
curl http://localhost:8080/admin/algorithm/performance-stats

# Check traffic data freshness
curl http://localhost:8080/admin/traffic-data/status
```
**Solutions**:
- Retrain machine learning models
- Update road network data in Neo4j
- Adjust optimization objective weights
- Implement A/B testing for algorithms

#### 4. Memory Usage Issues
**Symptoms**: OutOfMemoryError, frequent GC pauses
**Diagnosis**:
```bash
# Check JVM memory usage
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Monitor GC performance
curl http://localhost:8080/actuator/metrics/jvm.gc.pause

# Check for memory leaks
jstack <pid> | grep -A 5 "route optimization"
```
**Solutions**:
- Increase JVM heap size
- Implement optimization result cleanup
- Use memory-efficient data structures
- Add memory monitoring alerts

### Emergency Procedures

#### Service Outage Response
1. **Immediate Actions** (0-5 minutes):
   ```bash
   # Check service status
   kubectl get pods -n courier-services
   
   # Scale up replicas if needed
   kubectl scale deployment route-optimization --replicas=5
   
   # Check dependencies
   curl http://postgres:5432/health
   curl http://redis:6379/ping
   curl http://neo4j:7474/db/data/
   ```

2. **Short-term Mitigation** (5-30 minutes):
   ```bash
   # Enable fallback mode (simple algorithms)
   kubectl patch configmap route-config \
     --patch '{"data":{"fallback.mode":"true"}}'
   
   # Redirect to backup optimization engine
   kubectl patch ingress route-optimization-ingress \
     --patch '{"spec":{"rules":[{"host":"route-backup.example.com"}]}}'
   ```

3. **Recovery Actions** (30+ minutes):
   ```bash
   # Restore from backup
   psql -h $DB_HOST -U $DB_USERNAME -d $DB_NAME < backup_latest.sql
   
   # Reload graph database
   neo4j-admin load --from=/var/backups/neo4j_latest.dump --database=neo4j
   
   # Verify optimization functionality
   curl -X POST http://localhost:8080/api/routes/optimize \
     -H "Content-Type: application/json" \
     -d @test-optimization-request.json
   ```

#### Algorithm Failure Response
1. **Immediate Assessment**:
   ```bash
   # Check algorithm status
   curl http://localhost:8080/admin/algorithm/status
   
   # Identify failed optimizations
   grep "Optimization failed" /var/log/route-optimization/application.log
   ```

2. **Fallback Procedures**:
   ```bash
   # Switch to fallback algorithm
   curl -X PUT http://localhost:8080/admin/algorithm/fallback \
     -H "Content-Type: application/json" \
     -d '{"algorithm": "NEAREST_NEIGHBOR"}'
   
   # Clear algorithm cache
   curl -X DELETE http://localhost:8080/admin/cache/algorithm
   ```

### Performance Optimization

#### Algorithm Tuning
```bash
# Genetic Algorithm Parameters
export GA_POPULATION_SIZE=200
export GA_MAX_GENERATIONS=1000
export GA_MUTATION_RATE=0.005
export GA_CROSSOVER_RATE=0.85

# Parallel Processing
export OPTIMIZATION_THREADS=8
export PARALLEL_POPULATION_SIZE=4

# Caching Configuration
export ROUTE_CACHE_TTL=3600
export DISTANCE_CACHE_SIZE=10000
```

#### Database Optimization
```sql
-- Create indexes for optimization queries
CREATE INDEX CONCURRENTLY idx_routes_optimization_time ON routes(optimization_time);
CREATE INDEX CONCURRENTLY idx_delivery_points_location ON delivery_points USING GIST(location);
CREATE INDEX CONCURRENTLY idx_route_segments_route_id ON route_segments(route_id);

-- Partition large tables
CREATE TABLE route_history_2023_q4 PARTITION OF route_history
FOR VALUES FROM ('2023-10-01') TO ('2024-01-01');
```

#### Neo4j Optimization
```cypher
// Create spatial index for location queries
CREATE INDEX location_index FOR (n:Location) ON (n.latitude, n.longitude);

// Create relationship index for routing
CREATE INDEX route_relationship_index FOR ()-[r:CONNECTS]-() ON (r.distance, r.travel_time);

// Optimize memory settings
CALL dbms.setConfigValue('dbms.memory.heap.initial_size', '4G');
CALL dbms.setConfigValue('dbms.memory.heap.max_size', '8G');
```

#### JVM Tuning
```bash
# Optimize garbage collection
export JAVA_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=100 -Xms4g -Xmx8g"

# Enable JFR for performance monitoring
export JAVA_OPTS="$JAVA_OPTS -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=route-optimization.jfr"

# Optimize for CPU-intensive workloads
export JAVA_OPTS="$JAVA_OPTS -XX:+UseNUMA -XX:+AggressiveOpts"
```

## Maintenance Procedures

### Regular Maintenance Tasks
1. **Daily**: Monitor optimization performance, check traffic API usage, verify route quality
2. **Weekly**: Update road network data, review algorithm performance, cleanup old optimization results
3. **Monthly**: Database maintenance, traffic pattern analysis, capacity planning
4. **Quarterly**: Algorithm retraining, disaster recovery testing, performance reviews

### Capacity Planning
- Monitor optimization request volume trends
- Plan for peak delivery periods (holidays, seasonal events)
- Scale resources based on geographic expansion
- Implement auto-scaling policies for dynamic load management