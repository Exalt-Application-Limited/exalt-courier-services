# Regional Admin Dashboard

**Part of the Social E-commerce Ecosystem - Courier Services Domain**

A comprehensive regional-level admin dashboard that serves as a bridge between corporate administration and branch operations, featuring both React frontend and Spring Boot backend.

## 🚀 Features

### Frontend Features
- **Branch Overview**: Monitor and manage multiple branches in the region
- **Resource Allocation**: Optimize resource distribution across branches
- **Performance Analytics**: Advanced regional performance metrics
- **Policy Management**: Create and enforce regional policies
- **Cross-Branch Reporting**: Comparative analysis between branches
- **Real-time Dashboards**: Live operational data and metrics
- **Multi-language Support**: Arabic, English, French, Spanish, German

### Backend Features
- **Cross-Service Data Aggregation**: Aggregates data from multiple services to provide comprehensive regional views
- **Performance Metrics Dashboard**: Visualizes key performance indicators for regional operations
- **Real-Time Tracking Integration**: Integrates with the Real-Time Tracking Service for shipment monitoring
- **Advanced Reporting Integration**: Connects with the Advanced Reporting System for detailed reports
- **Distributed Tracing Integration**: Utilizes the Distributed Tracing System for service monitoring
- **Regional Metrics Collection**: Collects and aggregates metrics from branch/local levels

## 🛠 Technology Stack

### Frontend
- **Framework**: React 18, Material-UI, Redux Toolkit
- **Charts**: Recharts, MUI X Charts
- **Build Tool**: Create React App
- **Testing**: Jest, React Testing Library
- **State Management**: Redux Toolkit

### Backend
- **Framework**: Spring Boot, Spring Cloud
- **Database**: PostgreSQL
- **Security**: Spring Security, JWT
- **Monitoring**: Zipkin/Sleuth, Prometheus
- **Documentation**: Swagger/OpenAPI
- **Caching**: Caffeine

## 📋 Prerequisites

- **Node.js**: v18 or higher
- **npm**: v8 or higher
- **Java**: JDK 17 or higher
- **PostgreSQL**: v13 or higher
- **Docker**: v20 or higher

## 🚀 Quick Start

### Frontend Development

1. **Install dependencies**
   ```bash
   npm install
   ```

2. **Set up environment variables**
   ```bash
   cp .env.template .env
   # Edit .env file with your configuration
   ```

3. **Start frontend development server**
   ```bash
   npm start
   ```

The React application will be available at `http://localhost:3001`

### Backend Development

1. **Ensure PostgreSQL is running and create database**
   ```sql
   CREATE DATABASE regional_admin;
   ```

2. **Configure application properties**
   ```bash
   # Edit src/main/resources/application.properties
   ```

3. **Run the Spring Boot application**
   ```bash
   ./mvnw spring-boot:run
   ```

The backend API will be available at `http://localhost:8081`

### Full Stack Development with Docker

```bash
docker-compose up --build
```

## 🧪 Testing

### Frontend Tests
```bash
npm test                    # Run tests in watch mode
npm run test:coverage       # Run tests with coverage report
npm run lint                # Check code quality
npm run format              # Format code with Prettier
```

### Backend Tests
```bash
./mvnw test                 # Run unit tests
./mvnw verify               # Run integration tests
```

## 📁 Project Structure

```
├── src/                          # React frontend source
│   ├── components/              # Reusable UI components
│   ├── pages/                   # Page-level components
│   ├── services/                # API service layer
│   ├── store/                   # Redux store and slices
│   ├── hooks/                   # Custom React hooks
│   ├── utils/                   # Utility functions
│   └── styles/                  # Global styles
├── src/main/java/               # Spring Boot backend source
│   ├── config/                  # Configuration classes
│   ├── controller/              # REST controllers
│   ├── service/                 # Business logic
│   ├── repository/              # Data access layer
│   └── dto/                     # Data transfer objects
├── public/                      # React public assets
├── k8s/                         # Kubernetes manifests
├── docs/                        # Documentation
└── api-docs/                    # OpenAPI specifications
```

## 🔧 Configuration

### Frontend Environment Variables

Key environment variables (see `.env.template` for complete list):

```env
REACT_APP_API_BASE_URL=http://localhost:8081
REACT_APP_WEBSOCKET_URL=ws://localhost:8081/ws
REACT_APP_CORPORATE_ADMIN_URL=http://localhost:3000
```

### Backend Configuration

The application can be configured using properties in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/regional_admin
spring.datasource.username=your_username
spring.datasource.password=your_password
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
metrics.aggregation.interval=300000
```

## 🌐 API Endpoints

The backend exposes several REST API endpoints:

- `/api/metrics/*`: Regional metrics management
- `/api/dashboard/*`: Dashboard data retrieval
- `/api/integration/*`: Integration with other services
- `/api/aggregation/*`: Aggregated data retrieval
- `/api/branches/*`: Branch management endpoints
- `/api/resources/*`: Resource allocation endpoints
- `/api/policies/*`: Policy management endpoints

## 🔗 Integration

### Service Integration
- **Corporate Admin**: Data synchronization and policy enforcement
- **Branch Apps**: Real-time data collection and management
- **Tracking Service**: Shipment and delivery monitoring
- **Reporting Service**: Advanced analytics and reporting

### Frontend-Backend Communication
- **REST APIs**: Primary communication method
- **WebSocket**: Real-time data updates
- **JWT Authentication**: Secure API access

## 🚀 Deployment

### Production Build

1. **Build frontend**
   ```bash
   npm run build
   ```

2. **Build backend**
   ```bash
   ./mvnw clean package
   ```

3. **Deploy with Docker**
   ```bash
   docker build -t regional-admin .
   docker run -p 3001:80 -p 8081:8081 regional-admin
   ```

### Kubernetes Deployment

```bash
kubectl apply -f k8s/
```

## 🔍 Monitoring and Observability

- **Health Check**: `/actuator/health`
- **Metrics**: Prometheus metrics at `/actuator/prometheus`
- **API Documentation**: Swagger UI at `/swagger-ui.html`
- **Distributed Tracing**: Zipkin integration
- **Logs**: Structured logging with correlation IDs

## 🔐 Security

### Frontend Security
- JWT-based authentication
- Secure HTTP-only cookies
- CSRF protection
- XSS protection

### Backend Security
- Spring Security with JWT
- API rate limiting
- CORS configuration
- SQL injection prevention

Default development credentials:
- Username: admin
- Password: regional-admin-password

**⚠️ Change these credentials in production!**

## 🤝 Contributing

1. Follow the coding standards defined in `.eslintrc.json` (frontend) and Checkstyle (backend)
2. Write tests for new features
3. Ensure all tests pass before submitting PR
4. Use conventional commit messages

## 📚 Documentation

- **API Documentation**: Located in `api-docs/openapi.yaml`
- **Architecture**: See `docs/architecture/`
- **Operations**: See `docs/operations/`
- **Integration Status**: See `INTEGRATION_STATUS.md`

## 📄 License

This project is part of the Social E-commerce Ecosystem.

## 📞 Support

For support and questions, please contact the development team.
