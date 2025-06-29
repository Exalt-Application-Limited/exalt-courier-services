# High Availability Configuration

This directory contains the configuration necessary to deploy the Courier Network Locations service in a high availability mode.

## Components

### Kubernetes Deployment

The Kubernetes deployment configuration in `deployment.yaml` provides:

- Multiple replicas of the service (3 by default)
- Pod anti-affinity to ensure replicas are distributed across nodes
- Rolling update strategy with zero downtime
- Resource requests and limits
- Health probes (liveness, readiness, startup)
- Detailed environment configuration

### Database High Availability

The `database-statefulset.yaml` file configures a PostgreSQL cluster with:

- Primary/replica architecture using StatefulSets
- Automated replication setup
- Read/write splitting
- Persistent storage
- Health monitoring
- Backup configuration

### Redis Cluster

The `redis-cluster.yaml` file sets up a distributed Redis cluster for:

- Caching
- Session storage
- Distributed locking
- High availability through redundancy

### Ingress Configuration

The `ingress.yaml` file configures external access with:

- TLS termination
- Load balancing
- Path-based routing
- Health checks
- Rate limiting

### Network Policies

The `network-policy.yaml` file secures the service with:

- Ingress rules to restrict inbound traffic
- Egress rules to control outbound traffic
- Pod-to-pod communication policies

## Application Configuration

The application is configured for high availability through:

- Read/write splitting with `HighAvailabilityDatabaseConfig`
- Circuit breaker pattern with `ResilienceService`
- Health monitoring with `HighAvailabilityHealthIndicator`
- Distributed caching

## Deployment Instructions

1. **Configure Secrets**
   ```bash
   kubectl create secret generic courier-locations-db-credentials \
     --from-literal=username=postgres \
     --from-literal=password=<secure-password> \
     --from-literal=url=jdbc:postgresql://courier-locations-db:5432/courier_locations
   ```

2. **Deploy Database**
   ```bash
   kubectl apply -f database-statefulset.yaml
   ```

3. **Deploy Redis Cluster**
   ```bash
   kubectl apply -f redis-cluster.yaml
   ```

4. **Deploy Application**
   ```bash
   kubectl apply -f deployment.yaml
   ```

5. **Configure Network Policies**
   ```bash
   kubectl apply -f network-policy.yaml
   ```

6. **Configure Ingress**
   ```bash
   kubectl apply -f ingress.yaml
   ```

## Monitoring

- The application exposes health and metrics endpoints at `/actuator/health` and `/actuator/prometheus`
- Database replication lag is monitored through the health endpoint
- Detailed metrics are available for monitoring system load

## Failover

The system is designed to handle:

- Application instance failures through multiple replicas
- Database primary failures with automatic failover to replicas
- Node failures through pod rescheduling
- Network issues with retry mechanisms and circuit breakers

## Disaster Recovery

In case of catastrophic failure:

1. The database can be restored from backups
2. Application state is stored in Redis and the database, not in the application instances
3. Configuration is stored in Kubernetes ConfigMaps and Secrets

## Testing High Availability

Instructions for testing failover scenarios:

```bash
# Test application instance failure
kubectl delete pod courier-network-locations-xyz

# Test database primary failure
kubectl delete pod courier-locations-db-0

# Test network partition
kubectl cordon <node-name>
```

## Security Considerations

- All sensitive configuration is stored in Kubernetes Secrets
- Network policies restrict pod-to-pod communication
- TLS is used for all external communication
- Database credentials are rotated regularly
