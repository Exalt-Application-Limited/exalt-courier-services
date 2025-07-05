# Route Optimization Service Setup Documentation

## Development Setup

### Prerequisites
- **Java 17+**: Required for Spring Boot 3.x compatibility
- **Maven 3.9+**: Build and dependency management
- **Docker & Docker Compose**: Container orchestration for local development
- **PostgreSQL 14+**: Primary database for route data and optimization results
- **Redis 6.2+**: Caching layer for performance optimization
- **Neo4j 5.x**: Graph database for road network and routing calculations
- **Apache Kafka 3.x**: Event streaming platform
- **IDE**: IntelliJ IDEA or Eclipse recommended (with graph visualization plugins)

### Local Development

#### 1. Clone Repository
```bash
git clone <repository-url>
cd route-optimization-service
```

#### 2. Start Dependencies
```bash
# Start all required services
docker-compose up -d postgres redis neo4j kafka zookeeper

# Verify services are running
docker-compose ps

# Check Neo4j browser is accessible
open http://localhost:7474
```

#### 3. Configure Environment
```bash
# Database Configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=route_optimization_db
export DB_USERNAME=route_user
export DB_PASSWORD=route_pass

# Redis Configuration
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=redis_pass

# Neo4j Configuration
export NEO4J_HOST=localhost
export NEO4J_PORT=7687
export NEO4J_USERNAME=neo4j
export NEO4J_PASSWORD=neo4j_pass

# Kafka Configuration
export KAFKA_BOOTSTRAP_SERVERS=localhost:9092
export KAFKA_SECURITY_PROTOCOL=PLAINTEXT

# External APIs (get your own API keys)
export GOOGLE_MAPS_API_KEY=your-google-maps-api-key
export HERE_MAPS_API_KEY=your-here-maps-api-key
export OPENROUTE_API_KEY=your-openroute-api-key

# Optimization Configuration
export MAX_OPTIMIZATION_TIME_SECONDS=60
export DEFAULT_ALGORITHM=GENETIC_ALGORITHM
export POPULATION_SIZE=50
export MAX_GENERATIONS=100
export MUTATION_RATE=0.01

# Security Configuration
export JWT_SECRET=your-256-bit-secret-key-for-route-optimization
export JWT_EXPIRATION=86400
export API_RATE_LIMIT=100

# External Services
export DELIVERY_SERVICE_URL=http://localhost:8081
export FLEET_SERVICE_URL=http://localhost:8082
export TRAFFIC_SERVICE_URL=http://localhost:8083
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

# Test route optimization endpoint
curl -X POST http://localhost:8080/api/routes/optimize \
  -H "Content-Type: application/json" \
  -d @test-optimization-request.json

# Verify Neo4j connection
curl http://localhost:8080/admin/graph/status
```

### Database Setup

#### PostgreSQL Schema Creation
```sql
-- Create database and user
CREATE DATABASE route_optimization_db;
CREATE USER route_user WITH PASSWORD 'route_pass';
GRANT ALL PRIVILEGES ON DATABASE route_optimization_db TO route_user;

-- Connect to route_optimization_db
\c route_optimization_db;

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO route_user;
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
# Load test route data
mvn exec:java -Dexec.mainClass="com.gogidix.courier.routing.util.TestDataLoader"

# Load sample road network data
mvn exec:java -Dexec.mainClass="com.gogidix.courier.routing.util.RoadNetworkLoader" -Dexec.args="sample"
```

### Neo4j Setup

#### Graph Database Configuration
```bash
# Connect to Neo4j browser
open http://localhost:7474

# Login with credentials (neo4j/neo4j_pass)
# Create constraints and indexes
```

#### Road Network Data Import
```cypher
// Create constraints
CREATE CONSTRAINT location_id IF NOT EXISTS FOR (l:Location) REQUIRE l.id IS UNIQUE;
CREATE CONSTRAINT road_id IF NOT EXISTS FOR (r:Road) REQUIRE r.id IS UNIQUE;

// Create indexes for spatial queries
CREATE INDEX location_coordinates IF NOT EXISTS FOR (l:Location) ON (l.latitude, l.longitude);
CREATE INDEX road_distance IF NOT EXISTS FOR ()-[r:CONNECTS]-() ON (r.distance);

// Import sample road network (replace with actual data)
LOAD CSV WITH HEADERS FROM 'file:///sample-locations.csv' AS row
CREATE (l:Location {
  id: row.id,
  latitude: toFloat(row.latitude),
  longitude: toFloat(row.longitude),
  name: row.name,
  type: row.type
});

LOAD CSV WITH HEADERS FROM 'file:///sample-roads.csv' AS row
MATCH (start:Location {id: row.start_location_id})
MATCH (end:Location {id: row.end_location_id})
CREATE (start)-[:CONNECTS {
  id: row.road_id,
  distance: toFloat(row.distance),
  travel_time: toInteger(row.travel_time),
  road_type: row.road_type,
  speed_limit: toInteger(row.speed_limit)
}]->(end);
```

#### Verify Graph Data
```cypher
// Check data counts
MATCH (l:Location) RETURN count(l) as locations;
MATCH ()-[r:CONNECTS]-() RETURN count(r) as roads;

// Test shortest path query
MATCH (start:Location {name: 'Depot A'}), (end:Location {name: 'Customer 1'})
CALL gds.shortestPath.dijkstra.stream({
  sourceNode: id(start),
  targetNode: id(end),
  relationshipTypes: ['CONNECTS'],
  relationshipWeightProperty: 'distance'
})
YIELD sourceNode, targetNode, totalCost
RETURN totalCost;
```

### Redis Setup

#### Redis Configuration
```bash
# Connect to Redis
redis-cli

# Set basic configuration
CONFIG SET maxmemory 2gb
CONFIG SET maxmemory-policy allkeys-lru
CONFIG SET save "900 1 300 10 60 10000"

# Verify configuration
CONFIG GET maxmemory
CONFIG GET maxmemory-policy
```

#### Cache Structure Setup
```bash
# Set up cache namespaces
redis-cli EVAL "
for i=1,10 do
  redis.call('SET', 'route:cache:' .. i, '')
  redis.call('SET', 'distance:cache:' .. i, '')
  redis.call('SET', 'optimization:cache:' .. i, '')
end
return 'Cache namespaces created'
" 0
```

### Kafka Setup

#### Topic Creation
```bash
# Create route optimization topics
kafka-topics.sh --create --topic route-optimization-requests \
  --bootstrap-server localhost:9092 --partitions 6 --replication-factor 1

kafka-topics.sh --create --topic route-optimization-results \
  --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic traffic-updates \
  --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

kafka-topics.sh --create --topic route-adjustments \
  --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1

# Verify topics
kafka-topics.sh --list --bootstrap-server localhost:9092
```

### External API Setup

#### Google Maps API Configuration
```bash
# Test Google Maps API
curl -G "https://maps.googleapis.com/maps/api/directions/json" \
  -d "origin=San+Francisco,CA" \
  -d "destination=Los+Angeles,CA" \
  -d "key=$GOOGLE_MAPS_API_KEY"

# Test Distance Matrix API
curl -G "https://maps.googleapis.com/maps/api/distancematrix/json" \
  -d "origins=San+Francisco,CA" \
  -d "destinations=Los+Angeles,CA|San+Diego,CA" \
  -d "key=$GOOGLE_MAPS_API_KEY"
```

#### HERE Maps API Configuration
```bash
# Test HERE Maps API
curl -G "https://route.ls.hereapi.com/routing/7.2/calculateroute.json" \
  -d "waypoint0=52.5,13.4" \
  -d "waypoint1=52.5,13.45" \
  -d "mode=fastest;car;traffic:disabled" \
  -d "apiKey=$HERE_MAPS_API_KEY"
```

## Testing

### Unit Tests
```bash
# Run all unit tests
mvn test

# Run specific test classes
mvn test -Dtest=RouteOptimizationServiceTest
mvn test -Dtest=GeneticAlgorithmTest
mvn test -Dtest=VRPSolverTest

# Run tests with coverage
mvn test jacoco:report
```

### Integration Tests
```bash
# Run integration tests
mvn verify -P integration-tests

# Run integration tests with test containers
mvn verify -P integration-tests -Dspring.profiles.active=testcontainers

# Run specific integration test suites
mvn verify -P integration-tests -Dtest=OptimizationEngineIntegrationTest
```

### Load Testing

#### Using Apache Bench
```bash
# Test route optimization endpoint
ab -n 100 -c 5 -H "Authorization: Bearer <token>" \
  -T "application/json" \
  -p optimization-request.json \
  http://localhost:8080/api/routes/optimize

# Test route query endpoint
ab -n 500 -c 10 \
  http://localhost:8080/api/routes/123e4567-e89b-12d3-a456-426614174000
```

#### Using K6
```bash
# Run optimization load test
k6 run scripts/optimization-load-test.js

# Run with specific parameters
k6 run --vus 20 --duration 60s scripts/optimization-load-test.js
```

#### Load Test Scripts
```javascript
// scripts/optimization-load-test.js
import http from 'k6/http';
import { check } from 'k6';

export let options = {
  vus: 10,
  duration: '60s',
};

export default function() {
  // Generate test optimization request
  let optimizationRequest = {
    deliveryPoints: [
      { id: '1', latitude: 40.7128, longitude: -74.0060 },
      { id: '2', latitude: 40.7589, longitude: -73.9851 },
      { id: '3', latitude: 40.6782, longitude: -73.9442 }
    ],
    vehicle: {
      id: 'vehicle-1',
      capacity: 1000,
      startLocation: { latitude: 40.7128, longitude: -74.0060 }
    },
    objectives: ['MINIMIZE_DISTANCE', 'MINIMIZE_TIME'],
    constraints: {
      maxRouteDuration: 480,
      timeWindows: true
    }
  };
  
  // Test route optimization
  let response = http.post(
    'http://localhost:8080/api/routes/optimize',
    JSON.stringify(optimizationRequest),
    {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer test-token'
      }
    }
  );
  
  check(response, {
    'optimization status is 200 or 202': (r) => r.status === 200 || r.status === 202,
    'response time < 30s': (r) => r.timings.duration < 30000,
    'response contains route': (r) => r.body.includes('optimizedRoute'),
  });
  
  // Test route retrieval if optimization was successful
  if (response.status === 200) {
    let result = JSON.parse(response.body);
    if (result.routeId) {
      let routeResponse = http.get(`http://localhost:8080/api/routes/${result.routeId}`);
      check(routeResponse, {
        'route retrieval status is 200': (r) => r.status === 200,
      });
    }
  }
}
```

### API Testing

#### Health Check
```bash
# Application health
curl http://localhost:8080/actuator/health

# Detailed health checks
curl http://localhost:8080/actuator/health/db
curl http://localhost:8080/actuator/health/redis
curl http://localhost:8080/actuator/health/neo4j
curl http://localhost:8080/actuator/health/traffic-apis
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
  http://localhost:8080/api/routes/optimize
```

#### Route Optimization Testing
```bash
# Basic optimization request
curl -X POST http://localhost:8080/api/routes/optimize \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "deliveryPoints": [
      {"id": "1", "latitude": 40.7128, "longitude": -74.0060, "address": "New York, NY"},
      {"id": "2", "latitude": 40.7589, "longitude": -73.9851, "address": "Times Square, NY"},
      {"id": "3", "latitude": 40.6782, "longitude": -73.9442, "address": "Brooklyn, NY"}
    ],
    "vehicle": {
      "id": "vehicle-1",
      "capacity": 1000,
      "startLocation": {"latitude": 40.7128, "longitude": -74.0060}
    },
    "objectives": ["MINIMIZE_DISTANCE", "MINIMIZE_TIME"],
    "constraints": {
      "maxRouteDuration": 480,
      "timeWindows": true,
      "vehicleCapacity": true
    }
  }'

# Complex VRP request
curl -X POST http://localhost:8080/api/vrp/solve \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "vehicles": [
      {"id": "v1", "capacity": 1000, "depot": "depot1"},
      {"id": "v2", "capacity": 1500, "depot": "depot1"}
    ],
    "customers": [
      {"id": "c1", "latitude": 40.7128, "longitude": -74.0060, "demand": 200},
      {"id": "c2", "latitude": 40.7589, "longitude": -73.9851, "demand": 300},
      {"id": "c3", "latitude": 40.6782, "longitude": -73.9442, "demand": 150}
    ],
    "depots": [
      {"id": "depot1", "latitude": 40.7300, "longitude": -73.9950}
    ],
    "objectives": ["MINIMIZE_TOTAL_DISTANCE", "MINIMIZE_VEHICLE_COUNT"],
    "constraints": {
      "vehicleCapacity": true,
      "timeWindows": false,
      "maxRouteDistance": 100
    }
  }'

# Real-time route adjustment
curl -X PUT http://localhost:8080/api/routes/route-123/adjust \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "reason": "TRAFFIC_INCIDENT",
    "trafficIncident": {
      "location": {"latitude": 40.7500, "longitude": -73.9800},
      "severity": "MODERATE",
      "estimatedDelay": "PT15M"
    }
  }'
```

#### Algorithm Testing
```bash
# Test different algorithms
curl -X POST http://localhost:8080/api/routes/optimize \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "deliveryPoints": [...],
    "algorithm": "GENETIC_ALGORITHM",
    "algorithmParameters": {
      "populationSize": 100,
      "maxGenerations": 200,
      "mutationRate": 0.01
    }
  }'

# Test algorithm performance comparison
curl -X POST http://localhost:8080/api/routes/compare-algorithms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "deliveryPoints": [...],
    "algorithms": ["GENETIC_ALGORITHM", "SIMULATED_ANNEALING", "NEAREST_NEIGHBOR"]
  }'
```

## Production Setup

### Infrastructure Requirements

#### Hardware Specifications
- **CPU**: 8+ cores per instance (16+ for large-scale optimization)
- **Memory**: 16+ GB per instance (32+ GB for complex VRP problems)
- **Storage**: 200+ GB SSD (1+ TB for historical data)
- **Network**: 1+ Gbps bandwidth with low latency
- **GPU**: Optional CUDA-compatible GPU for parallel optimization

#### Scaling Recommendations
- **Small deployment**: 3-5 instances, 1K optimizations/day
- **Medium deployment**: 8-15 instances, 10K optimizations/day
- **Large deployment**: 20+ instances, 100K+ optimizations/day

### Database Configuration

#### PostgreSQL Production Settings
```conf
# postgresql.conf
max_connections = 300
shared_buffers = 8GB
effective_cache_size = 24GB
work_mem = 128MB
maintenance_work_mem = 2GB
checkpoint_segments = 128
checkpoint_completion_target = 0.9
wal_buffers = 32MB
default_statistics_target = 100
random_page_cost = 1.1
effective_io_concurrency = 200
```

#### Neo4j Production Settings
```conf
# neo4j.conf
dbms.memory.heap.initial_size=4G
dbms.memory.heap.max_size=8G
dbms.memory.pagecache.size=4G
dbms.connector.bolt.thread_pool_min_size=5
dbms.connector.bolt.thread_pool_max_size=400
```

### Redis Configuration

#### Production Settings
```conf
# redis.conf
maxmemory 16gb
maxmemory-policy allkeys-lru
save 900 1 300 10 60 10000
tcp-keepalive 300
timeout 0
tcp-backlog 511
databases 16
```

### Kubernetes Deployment

#### Deployment Configuration
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: route-optimization
  namespace: courier-services
spec:
  replicas: 8
  selector:
    matchLabels:
      app: route-optimization
  template:
    metadata:
      labels:
        app: route-optimization
    spec:
      containers:
      - name: route-optimization
        image: route-optimization:v1.0.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            memory: "8Gi"
            cpu: "4000m"
          limits:
            memory: "16Gi"
            cpu: "8000m"
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: MAX_OPTIMIZATION_TIME_SECONDS
          value: "300"
        - name: POPULATION_SIZE
          value: "200"
        livenessProbe:
          httpGet:
            path: /actuator/health/liveness
            port: 8080
          initialDelaySeconds: 90
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
```

### Security Configuration

#### SSL/TLS Setup
```yaml
# ingress-tls.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: route-optimization-ingress
  annotations:
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "300"
spec:
  tls:
  - hosts:
    - routes.example.com
    secretName: route-optimization-tls
  rules:
  - host: routes.example.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: route-optimization
            port:
              number: 80
```

#### Security Best Practices
- Enable SSL/TLS encryption for all communications
- Configure firewall rules to restrict access to optimization APIs
- Implement proper authentication and authorization for route data
- Set up monitoring and alerting for security events
- Regular security audits and algorithm validation
- Implement backup and disaster recovery procedures
- Configure log rotation and secure log storage

### Monitoring and Observability

#### Prometheus Configuration
```yaml
# prometheus-config.yaml
global:
  scrape_interval: 15s
scrape_configs:
- job_name: 'route-optimization'
  static_configs:
  - targets: ['route-optimization:8080']
  metrics_path: '/actuator/prometheus'
  scrape_interval: 10s
```

#### Grafana Dashboard
```json
{
  "dashboard": {
    "title": "Route Optimization Dashboard",
    "panels": [
      {
        "title": "Optimization Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(route_optimization_requests_total[5m])"
          }
        ]
      },
      {
        "title": "Average Optimization Time",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(route_optimization_duration_seconds_sum[5m]) / rate(route_optimization_duration_seconds_count[5m])"
          }
        ]
      },
      {
        "title": "Route Quality Score",
        "type": "graph",
        "targets": [
          {
            "expr": "route_quality_score_avg"
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
BACKUP_DIR="/backups/route-optimization"
DATE=$(date +%Y%m%d_%H%M%S)

# PostgreSQL backup
pg_dump -h $DB_HOST -U $DB_USERNAME -d $DB_NAME | gzip > "$BACKUP_DIR/postgres_backup_$DATE.sql.gz"

# Neo4j backup
neo4j-admin dump --database=neo4j --to="$BACKUP_DIR/neo4j_backup_$DATE.dump"

# Redis backup
redis-cli --rdb "$BACKUP_DIR/redis_backup_$DATE.rdb"

# Retain last 30 days
find $BACKUP_DIR -name "*_backup_*" -mtime +30 -delete
```

## Troubleshooting

### Common Issues

#### Application Won't Start
1. Check Java version: `java --version`
2. Verify database connectivity: `pg_isready -h $DB_HOST -p $DB_PORT`
3. Check Neo4j connection: `cypher-shell -a bolt://$NEO4J_HOST:$NEO4J_PORT -u $NEO4J_USERNAME -p $NEO4J_PASSWORD "RETURN 1;"`
4. Check Redis connection: `redis-cli -h $REDIS_HOST ping`
5. Review application logs: `docker logs route-optimization`

#### Optimization Performance Issues
1. Monitor JVM metrics: `curl localhost:8080/actuator/metrics/jvm.memory.used`
2. Check algorithm performance: `curl localhost:8080/admin/algorithm/performance`
3. Monitor Neo4j performance: `curl http://localhost:7474/db/data/monitor/queries`
4. Review optimization logs: `grep "Optimization completed" /var/log/route-optimization/application.log`

#### External API Issues
1. Test API connectivity: `curl -H "Authorization: Bearer $GOOGLE_MAPS_API_KEY" "https://maps.googleapis.com/maps/api/directions/json?origin=test&destination=test"`
2. Check API quota: `curl localhost:8080/admin/api-usage`
3. Monitor API response times: `curl localhost:8080/actuator/metrics/http.client.requests`

### Getting Help
- Check the operations documentation for monitoring and troubleshooting
- Review API documentation for endpoint specifications
- Consult architecture documentation for system design details
- Contact the development team for algorithm-specific issues