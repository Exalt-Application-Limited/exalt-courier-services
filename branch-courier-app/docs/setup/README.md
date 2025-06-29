# Branch Courier App Setup Guide

## Overview
This guide provides step-by-step instructions for setting up the Branch Courier App in development, staging, and production environments.

## Prerequisites

### System Requirements
- **Node.js**: Version 18.x or higher
- **npm**: Version 9.x or higher (or Yarn 3.x)
- **Docker**: Version 20.x or higher
- **PostgreSQL**: Version 14.x or higher
- **Redis**: Version 6.x or higher

### Development Tools
- **Git**: Version 2.30 or higher
- **VS Code**: Recommended IDE with extensions
- **Postman**: For API testing
- **Docker Compose**: For local development

### Required VS Code Extensions
```json
{
  "recommendations": [
    "bradlc.vscode-tailwindcss",
    "esbenp.prettier-vscode",
    "dbaeumer.vscode-eslint",
    "ms-vscode.vscode-typescript-next",
    "formulahendry.auto-rename-tag",
    "christian-kohler.path-intellisense"
  ]
}
```

## Environment Setup

### 1. Clone the Repository
```bash
# Clone the repository
git clone https://github.com/courier-services/branch-courier-app.git
cd branch-courier-app

# Switch to development branch
git checkout develop
```

### 2. Install Dependencies
```bash
# Install Node.js dependencies
npm install

# Or using Yarn
yarn install

# Install global tools
npm install -g @types/node typescript ts-node
```

### 3. Environment Configuration

#### Development Environment
Create `.env.development` file:
```bash
# Application Configuration
NODE_ENV=development
PORT=3000
API_BASE_URL=http://localhost:8080/api/v1

# Database Configuration
DATABASE_URL=postgresql://courier_user:courier_pass@localhost:5432/branch_courier_dev
DB_HOST=localhost
DB_PORT=5432
DB_NAME=branch_courier_dev
DB_USER=courier_user
DB_PASSWORD=courier_pass

# Redis Configuration
REDIS_URL=redis://localhost:6379
REDIS_PASSWORD=

# Authentication
JWT_SECRET=your-super-secret-jwt-key-for-development
JWT_EXPIRY=24h
SESSION_SECRET=your-session-secret-for-development

# External Services (Development)
TRACKING_SERVICE_URL=http://localhost:8081
NOTIFICATION_SERVICE_URL=http://localhost:8082
PAYMENT_GATEWAY_URL=http://localhost:8083

# Feature Flags
ENABLE_REAL_TIME_TRACKING=true
ENABLE_DEBUG_MODE=true
ENABLE_HOT_RELOAD=true

# Logging
LOG_LEVEL=debug
```

#### Production Environment
Create `.env.production` file:
```bash
# Application Configuration
NODE_ENV=production
PORT=3000
API_BASE_URL=https://api.courier.com/branch/v1

# Database Configuration (Use environment variables)
DATABASE_URL=${DATABASE_URL}
DB_SSL_MODE=require

# Redis Configuration
REDIS_URL=${REDIS_URL}
REDIS_PASSWORD=${REDIS_PASSWORD}

# Authentication
JWT_SECRET=${JWT_SECRET}
JWT_EXPIRY=24h
SESSION_SECRET=${SESSION_SECRET}

# External Services
TRACKING_SERVICE_URL=https://tracking.courier.com
NOTIFICATION_SERVICE_URL=https://notifications.courier.com
PAYMENT_GATEWAY_URL=https://payments.courier.com

# Monitoring
SENTRY_DSN=${SENTRY_DSN}
NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}

# Feature Flags
ENABLE_REAL_TIME_TRACKING=true
ENABLE_ADVANCED_ANALYTICS=true
```

## Database Setup

### 1. PostgreSQL Installation

#### Using Docker (Recommended for Development)
```bash
# Create and start PostgreSQL container
docker run --name branch-courier-postgres \
  -e POSTGRES_DB=branch_courier_dev \
  -e POSTGRES_USER=courier_user \
  -e POSTGRES_PASSWORD=courier_pass \
  -p 5432:5432 \
  -d postgres:14

# Verify connection
docker exec -it branch-courier-postgres psql -U courier_user -d branch_courier_dev
```

#### Using Local Installation
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# macOS
brew install postgresql
brew services start postgresql

# Create database and user
sudo -u postgres psql
CREATE DATABASE branch_courier_dev;
CREATE USER courier_user WITH PASSWORD 'courier_pass';
GRANT ALL PRIVILEGES ON DATABASE branch_courier_dev TO courier_user;
```

### 2. Database Migration
```bash
# Run database migrations
npm run db:migrate

# Seed development data
npm run db:seed

# Reset database (development only)
npm run db:reset
```

### 3. Database Schema Verification
```sql
-- Verify tables are created
\dt

-- Check sample data
SELECT COUNT(*) FROM packages;
SELECT COUNT(*) FROM customers;
SELECT COUNT(*) FROM staff;
```

## Redis Setup

### 1. Redis Installation

#### Using Docker (Recommended)
```bash
# Start Redis container
docker run --name branch-courier-redis \
  -p 6379:6379 \
  -d redis:6-alpine

# Test Redis connection
docker exec -it branch-courier-redis redis-cli ping
```

#### Using Local Installation
```bash
# Ubuntu/Debian
sudo apt install redis-server

# macOS
brew install redis
brew services start redis

# Test connection
redis-cli ping
```

## Development Environment

### 1. Start Development Server
```bash
# Start all services using Docker Compose
docker-compose up -d

# Start development server
npm run dev

# Or start with hot reload
npm run dev:hot
```

### 2. Development Scripts
```bash
# Build for development
npm run build:dev

# Run tests
npm run test

# Run tests in watch mode
npm run test:watch

# Run ESLint
npm run lint

# Fix ESLint issues
npm run lint:fix

# Format code with Prettier
npm run format

# Type checking
npm run type-check
```

### 3. Docker Compose Setup
Create `docker-compose.yml`:
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:14
    container_name: branch-courier-postgres
    environment:
      POSTGRES_DB: branch_courier_dev
      POSTGRES_USER: courier_user
      POSTGRES_PASSWORD: courier_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:6-alpine
    container_name: branch-courier-redis
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  app:
    build:
      context: .
      dockerfile: Dockerfile.dev
    container_name: branch-courier-app
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=development
    volumes:
      - .:/app
      - /app/node_modules
    depends_on:
      - postgres
      - redis

volumes:
  postgres_data:
  redis_data:
```

## Production Deployment

### 1. Build for Production
```bash
# Install production dependencies only
npm ci --only=production

# Build optimized bundle
npm run build:production

# Create Docker image
docker build -t branch-courier-app:latest .
```

### 2. Kubernetes Deployment

#### Deployment Configuration
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: branch-courier-app
  labels:
    app: branch-courier-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: branch-courier-app
  template:
    metadata:
      labels:
        app: branch-courier-app
    spec:
      containers:
      - name: app
        image: registry.courier.com/branch-courier-app:latest
        ports:
        - containerPort: 3000
        env:
        - name: NODE_ENV
          value: "production"
        - name: DATABASE_URL
          valueFrom:
            secretKeyRef:
              name: database-secret
              key: url
        - name: REDIS_URL
          valueFrom:
            secretKeyRef:
              name: redis-secret
              key: url
        resources:
          requests:
            memory: "256Mi"
            cpu: "250m"
          limits:
            memory: "512Mi"
            cpu: "500m"
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

#### Service Configuration
```yaml
# k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: branch-courier-app-service
spec:
  selector:
    app: branch-courier-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 3000
  type: ClusterIP
```

#### Ingress Configuration
```yaml
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: branch-courier-app-ingress
  annotations:
    nginx.ingress.kubernetes.io/rewrite-target: /
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
spec:
  tls:
  - hosts:
    - branch.courier.com
    secretName: branch-courier-tls
  rules:
  - host: branch.courier.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: branch-courier-app-service
            port:
              number: 80
```

### 3. Deploy to Kubernetes
```bash
# Create namespace
kubectl create namespace courier-services

# Apply configurations
kubectl apply -f k8s/ -n courier-services

# Verify deployment
kubectl get pods -n courier-services
kubectl get services -n courier-services
kubectl get ingress -n courier-services

# Check application logs
kubectl logs -l app=branch-courier-app -n courier-services
```

## Testing Setup

### 1. Unit Testing
```bash
# Install testing dependencies
npm install --save-dev jest @testing-library/react @testing-library/jest-dom

# Run unit tests
npm run test

# Generate coverage report
npm run test:coverage
```

### 2. Integration Testing
```bash
# Install integration testing tools
npm install --save-dev supertest

# Run integration tests
npm run test:integration

# Run specific test suite
npm run test -- --testNamePattern="Package Processing"
```

### 3. End-to-End Testing
```bash
# Install Cypress
npm install --save-dev cypress

# Open Cypress Test Runner
npx cypress open

# Run E2E tests headlessly
npm run test:e2e
```

## Performance Optimization

### 1. Bundle Analysis
```bash
# Analyze bundle size
npm run analyze

# Generate bundle report
npm run build:analyze
```

### 2. Performance Monitoring Setup
```javascript
// Install performance monitoring
npm install @sentry/react @sentry/tracing

// Configure Sentry
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: process.env.SENTRY_DSN,
  integrations: [
    new Sentry.BrowserTracing(),
  ],
  tracesSampleRate: 1.0,
});
```

## Security Setup

### 1. SSL Certificate Setup
```bash
# Install cert-manager for automatic certificate management
kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v1.8.0/cert-manager.yaml

# Create ClusterIssuer
kubectl apply -f k8s/cluster-issuer.yaml
```

### 2. Security Headers Configuration
```javascript
// Configure security headers in Express.js
const helmet = require('helmet');

app.use(helmet({
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      styleSrc: ["'self'", "'unsafe-inline'"],
      scriptSrc: ["'self'"],
      connectSrc: ["'self'", "https://api.courier.com"]
    }
  }
}));
```

## Monitoring Setup

### 1. Application Monitoring
```bash
# Install monitoring stack
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts

# Install Prometheus
helm install prometheus prometheus-community/prometheus

# Install Grafana
helm install grafana grafana/grafana
```

### 2. Log Aggregation
```yaml
# Install Fluentd for log collection
apiVersion: apps/v1
kind: DaemonSet
metadata:
  name: fluentd
spec:
  template:
    spec:
      containers:
      - name: fluentd
        image: fluent/fluentd:v1.12-1
        env:
        - name: FLUENT_ELASTICSEARCH_HOST
          value: "elasticsearch.logging.svc.cluster.local"
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check database connectivity
npm run db:check

# Reset database connections
npm run db:reset-connections

# Check PostgreSQL logs
docker logs branch-courier-postgres
```

#### 2. Redis Connection Issues
```bash
# Test Redis connection
redis-cli -h localhost -p 6379 ping

# Check Redis logs
docker logs branch-courier-redis

# Clear Redis cache
redis-cli flushall
```

#### 3. Build Issues
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install

# Clear build cache
npm run clean
npm run build

# Check for TypeScript errors
npm run type-check
```

#### 4. Performance Issues
```bash
# Profile application
npm run profile

# Check memory usage
docker stats branch-courier-app

# Analyze bundle size
npm run analyze
```

### Debug Mode
```bash
# Start application in debug mode
npm run debug

# Start with VS Code debugger
npm run debug:vscode

# Enable verbose logging
DEBUG=* npm run dev
```

## Maintenance

### Regular Maintenance Tasks
```bash
# Update dependencies
npm update

# Security audit
npm audit
npm audit fix

# Clean up Docker
docker system prune

# Database maintenance
npm run db:analyze
npm run db:vacuum
```

### Backup Procedures
```bash
# Backup database
npm run db:backup

# Backup application data
npm run backup:app-data

# Verify backup integrity
npm run backup:verify
```