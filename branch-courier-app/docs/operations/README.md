# Branch Courier App Operations Guide

## Overview
This document provides comprehensive operational guidance for deploying, monitoring, and maintaining the Branch Courier App in production environments.

## Deployment Operations

### Production Deployment Process

#### Pre-Deployment Checklist
- [ ] Environment variables configured
- [ ] Database migrations executed
- [ ] SSL certificates updated
- [ ] Load balancer health checks configured
- [ ] Monitoring alerts enabled
- [ ] Backup procedures verified
- [ ] Rollback plan prepared

#### Deployment Steps
```bash
# 1. Build the application
npm run build:production

# 2. Create Docker image
docker build -t branch-courier-app:latest .

# 3. Tag and push to registry
docker tag branch-courier-app:latest registry.courier.com/branch-courier-app:v1.0.0
docker push registry.courier.com/branch-courier-app:v1.0.0

# 4. Deploy to Kubernetes
kubectl apply -f k8s/deployment.yaml
kubectl rollout status deployment/branch-courier-app

# 5. Verify deployment
kubectl get pods -l app=branch-courier-app
kubectl logs -l app=branch-courier-app --tail=100
```

### Environment Configuration

#### Production Environment Variables
```bash
# Application Configuration
NODE_ENV=production
PORT=3000
API_BASE_URL=https://api.courier.com/branch/v1

# Authentication
JWT_SECRET=${JWT_SECRET}
JWT_EXPIRY=24h
SESSION_SECRET=${SESSION_SECRET}

# Database Configuration
DB_HOST=${DB_HOST}
DB_PORT=5432
DB_NAME=branch_courier_prod
DB_USER=${DB_USER}
DB_PASSWORD=${DB_PASSWORD}
DB_SSL_MODE=require

# Redis Configuration
REDIS_URL=${REDIS_URL}
REDIS_PASSWORD=${REDIS_PASSWORD}

# External Services
TRACKING_SERVICE_URL=https://tracking.courier.com
NOTIFICATION_SERVICE_URL=https://notifications.courier.com
PAYMENT_GATEWAY_URL=https://payments.courier.com

# Monitoring and Logging
LOG_LEVEL=info
SENTRY_DSN=${SENTRY_DSN}
NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}

# Feature Flags
ENABLE_REAL_TIME_TRACKING=true
ENABLE_ADVANCED_ANALYTICS=true
ENABLE_EXPERIMENTAL_FEATURES=false
```

## Monitoring and Observability

### Application Metrics

#### Key Performance Indicators (KPIs)
1. **Response Time**: Average API response time < 200ms
2. **Throughput**: Requests per second handled
3. **Error Rate**: Error rate < 1%
4. **Availability**: Uptime > 99.9%
5. **User Engagement**: Active users per hour

#### Monitoring Dashboard
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-dashboard-branch-app
data:
  dashboard.json: |
    {
      "dashboard": {
        "title": "Branch Courier App",
        "panels": [
          {
            "title": "Response Time",
            "type": "graph",
            "targets": [
              {
                "expr": "avg(http_request_duration_seconds) by (endpoint)"
              }
            ]
          },
          {
            "title": "Error Rate",
            "type": "singlestat",
            "targets": [
              {
                "expr": "rate(http_requests_total{status=~\"5..\"}[5m])"
              }
            ]
          }
        ]
      }
    }
```

### Health Checks

#### Application Health Check
```javascript
// /health endpoint implementation
app.get('/health', async (req, res) => {
  const healthCheck = {
    status: 'UP',
    timestamp: new Date().toISOString(),
    checks: {
      database: await checkDatabase(),
      redis: await checkRedis(),
      externalServices: await checkExternalServices()
    }
  };
  
  const allHealthy = Object.values(healthCheck.checks)
    .every(check => check.status === 'UP');
  
  res.status(allHealthy ? 200 : 503).json(healthCheck);
});
```

#### Kubernetes Health Check Configuration
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: branch-courier-app
spec:
  template:
    spec:
      containers:
      - name: app
        livenessProbe:
          httpGet:
            path: /health
            port: 3000
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /ready
            port: 3000
          initialDelaySeconds: 5
          periodSeconds: 5
```

### Logging Strategy

#### Structured Logging
```javascript
// Logger configuration
const winston = require('winston');

const logger = winston.createLogger({
  level: process.env.LOG_LEVEL || 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.errors({ stack: true }),
    winston.format.json()
  ),
  defaultMeta: {
    service: 'branch-courier-app',
    environment: process.env.NODE_ENV
  },
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: 'logs/error.log', level: 'error' }),
    new winston.transports.File({ filename: 'logs/combined.log' })
  ]
});
```

#### Log Aggregation
```yaml
# Fluentd configuration for log collection
apiVersion: v1
kind: ConfigMap
metadata:
  name: fluentd-config
data:
  fluent.conf: |
    <source>
      @type tail
      path /var/log/containers/branch-courier-app*.log
      pos_file /var/log/fluentd-containers.log.pos
      tag kubernetes.branch-courier-app
      format json
    </source>
    
    <match kubernetes.branch-courier-app>
      @type elasticsearch
      host elasticsearch.logging.svc.cluster.local
      port 9200
      index_name branch-courier-app
    </match>
```

## Performance Optimization

### Caching Strategy

#### Redis Caching Configuration
```javascript
const redis = require('redis');
const client = redis.createClient({
  url: process.env.REDIS_URL,
  password: process.env.REDIS_PASSWORD,
  retryDelayOnFailover: 100,
  maxRetriesPerRequest: 3
});

// Cache middleware
const cacheMiddleware = (duration = 300) => {
  return async (req, res, next) => {
    const key = `cache:${req.originalUrl}`;
    
    try {
      const cached = await client.get(key);
      if (cached) {
        return res.json(JSON.parse(cached));
      }
      
      res.sendResponse = res.json;
      res.json = (body) => {
        client.setex(key, duration, JSON.stringify(body));
        res.sendResponse(body);
      };
      
      next();
    } catch (error) {
      next();
    }
  };
};
```

#### CDN Configuration
```nginx
# Nginx configuration for static asset caching
server {
    listen 80;
    server_name branch.courier.com;
    
    location /static/ {
        alias /app/build/static/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    location /api/ {
        proxy_pass http://branch-app-backend;
        proxy_cache api_cache;
        proxy_cache_valid 200 5m;
        proxy_cache_key "$scheme$request_method$host$request_uri";
    }
}
```

### Database Optimization

#### Connection Pooling
```javascript
const { Pool } = require('pg');

const pool = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  database: process.env.DB_NAME,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  max: 20,                    // Maximum number of connections
  idleTimeoutMillis: 30000,   // Close idle connections after 30 seconds
  connectionTimeoutMillis: 2000, // Return error after 2 seconds if connection could not be established
});
```

#### Query Optimization
```sql
-- Index optimization for frequently queried columns
CREATE INDEX CONCURRENTLY idx_packages_status_branch_id 
ON packages(status, branch_id) 
WHERE status IN ('RECEIVED', 'IN_TRANSIT');

CREATE INDEX CONCURRENTLY idx_customer_inquiries_created_at 
ON customer_inquiries(created_at DESC) 
WHERE status = 'OPEN';

-- Query performance monitoring
EXPLAIN (ANALYZE, BUFFERS) 
SELECT p.* FROM packages p 
WHERE p.branch_id = $1 
AND p.status = $2 
ORDER BY p.created_at DESC 
LIMIT 50;
```

## Security Operations

### Security Monitoring

#### Authentication Monitoring
```javascript
// Failed login attempt monitoring
app.post('/auth/login', async (req, res) => {
  try {
    const result = await authenticateUser(req.body);
    
    logger.info('Login successful', {
      userId: result.userId,
      ip: req.ip,
      userAgent: req.get('User-Agent')
    });
    
    res.json(result);
  } catch (error) {
    logger.warn('Login failed', {
      email: req.body.email,
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      error: error.message
    });
    
    // Rate limiting for failed attempts
    await incrementFailedAttempts(req.ip);
    
    res.status(401).json({ error: 'Authentication failed' });
  }
});
```

#### Security Headers
```javascript
const helmet = require('helmet');

app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      styleSrc: ["'self'", "'unsafe-inline'", "https://fonts.googleapis.com"],
      fontSrc: ["'self'", "https://fonts.gstatic.com"],
      imgSrc: ["'self'", "data:", "https:"],
      scriptSrc: ["'self'"],
      connectSrc: ["'self'", "https://api.courier.com"]
    }
  },
  hsts: {
    maxAge: 31536000,
    includeSubDomains: true,
    preload: true
  }
}));
```

### SSL/TLS Management

#### Certificate Management
```bash
# Let's Encrypt certificate renewal
#!/bin/bash
# Script: renew-ssl-cert.sh

# Check certificate expiry
openssl x509 -in /etc/ssl/certs/branch.courier.com.crt -noout -dates

# Renew certificate
certbot renew --nginx --dry-run

# Restart nginx if renewal successful
if [ $? -eq 0 ]; then
    systemctl reload nginx
    echo "SSL certificate renewed successfully"
else
    echo "SSL certificate renewal failed"
    exit 1
fi
```

## Backup and Recovery

### Database Backup Strategy

#### Automated Backup Script
```bash
#!/bin/bash
# Script: backup-database.sh

BACKUP_DIR="/backups/branch-courier-app"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
DB_NAME="branch_courier_prod"

# Create backup directory
mkdir -p $BACKUP_DIR

# Perform database backup
pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME \
  --no-password --clean --create \
  | gzip > $BACKUP_DIR/db_backup_$TIMESTAMP.sql.gz

# Upload to S3
aws s3 cp $BACKUP_DIR/db_backup_$TIMESTAMP.sql.gz \
  s3://courier-backups/branch-app/db/

# Clean up old local backups (keep last 7 days)
find $BACKUP_DIR -name "db_backup_*.sql.gz" -mtime +7 -delete

echo "Database backup completed: db_backup_$TIMESTAMP.sql.gz"
```

#### Backup Verification
```bash
#!/bin/bash
# Script: verify-backup.sh

BACKUP_FILE="$1"
TEST_DB="branch_courier_test_restore"

# Create test database
createdb $TEST_DB

# Restore backup to test database
gunzip -c $BACKUP_FILE | psql -d $TEST_DB

# Verify data integrity
RECORD_COUNT=$(psql -d $TEST_DB -t -c "SELECT COUNT(*) FROM packages;")
echo "Restored record count: $RECORD_COUNT"

# Cleanup test database
dropdb $TEST_DB
```

### Disaster Recovery Plan

#### Recovery Time Objectives (RTO)
- **Critical Components**: 15 minutes
- **Non-Critical Components**: 4 hours
- **Complete System Recovery**: 8 hours

#### Recovery Procedures
1. **Application Recovery**:
   ```bash
   # Rollback to previous version
   kubectl rollout undo deployment/branch-courier-app
   
   # Scale up replicas
   kubectl scale deployment/branch-courier-app --replicas=3
   ```

2. **Database Recovery**:
   ```bash
   # Restore from latest backup
   gunzip -c latest_backup.sql.gz | psql -d branch_courier_prod
   
   # Verify data integrity
   ./verify-database-integrity.sh
   ```

## Incident Response

### Incident Classification

#### Severity Levels
- **Critical (P1)**: Complete service outage
- **High (P2)**: Major functionality impaired
- **Medium (P3)**: Minor functionality issues
- **Low (P4)**: Cosmetic or documentation issues

#### Response Procedures

##### Critical Incident Response
```bash
# Immediate Response (within 5 minutes)
1. Acknowledge incident in monitoring system
2. Page on-call engineer
3. Create incident war room
4. Begin diagnostics

# Diagnostics Commands
kubectl get pods -l app=branch-courier-app
kubectl logs -l app=branch-courier-app --tail=500
kubectl describe deployment/branch-courier-app

# Communication
- Update status page
- Notify stakeholders
- Document actions taken
```

### Maintenance Windows

#### Scheduled Maintenance Process
```yaml
# Maintenance notification
apiVersion: v1
kind: ConfigMap
metadata:
  name: maintenance-notification
data:
  message: |
    Scheduled maintenance: 2024-01-15 02:00-04:00 UTC
    Expected downtime: 30 minutes
    Contact: ops@courier.com
```

#### Zero-Downtime Deployment
```bash
# Rolling update with health checks
kubectl set image deployment/branch-courier-app \
  app=registry.courier.com/branch-courier-app:v1.1.0

# Monitor rollout
kubectl rollout status deployment/branch-courier-app

# Verify new version
kubectl get pods -l app=branch-courier-app -o wide
```

## Cost Optimization

### Resource Management

#### Horizontal Pod Autoscaler
```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: branch-courier-app-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: branch-courier-app
  minReplicas: 2
  maxReplicas: 10
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

#### Resource Optimization
```yaml
# Resource requests and limits
resources:
  requests:
    memory: "256Mi"
    cpu: "250m"
  limits:
    memory: "512Mi"
    cpu: "500m"
```

## Compliance and Auditing

### Audit Logging
```javascript
// Audit log middleware
const auditMiddleware = (req, res, next) => {
  const auditLog = {
    timestamp: new Date().toISOString(),
    userId: req.user?.id,
    action: `${req.method} ${req.path}`,
    ip: req.ip,
    userAgent: req.get('User-Agent'),
    requestId: req.id
  };
  
  // Log to audit system
  auditLogger.info('User action', auditLog);
  
  next();
};
```

### Compliance Checks
```bash
# Daily compliance check script
#!/bin/bash
# Script: compliance-check.sh

echo "Running compliance checks..."

# Check SSL certificate expiry
SSL_EXPIRY=$(openssl x509 -in /etc/ssl/certs/branch.courier.com.crt -noout -enddate)
echo "SSL Certificate: $SSL_EXPIRY"

# Check backup integrity
./verify-latest-backup.sh

# Check security updates
apt list --upgradable | grep -i security

# Generate compliance report
./generate-compliance-report.sh
```