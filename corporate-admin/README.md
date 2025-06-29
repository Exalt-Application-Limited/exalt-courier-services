# Corporate Admin Dashboard

**Part of the Social E-commerce Ecosystem - Courier Services Domain**

A comprehensive corporate-level admin dashboard for managing courier services operations, built with React and Material-UI.

## 🚀 Features

- **Branch Management**: Oversee and manage all branch operations
- **Courier Management**: Monitor and manage courier workforce
- **Performance Analytics**: Real-time analytics and reporting
- **Commission Tracking**: Track and manage commission structures
- **Multi-language Support**: Arabic, English, French, Spanish, German
- **Real-time Updates**: WebSocket-based live data updates
- **Responsive Design**: Mobile-first approach with Material-UI

## 🛠 Technology Stack

- **Frontend**: React 18, Material-UI, Redux Toolkit
- **Backend**: Java Spring Boot
- **Charts**: Recharts, MUI X Charts
- **Build Tool**: Create React App
- **Testing**: Jest, React Testing Library
- **Deployment**: Docker, Nginx

## 📋 Prerequisites

- **Node.js**: v18 or higher
- **npm**: v8 or higher
- **Docker**: v20 or higher
- **Java**: JDK 17 (for backend integration)

## 🚀 Quick Start

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd courier-services/corporate-admin
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Set up environment variables**
   ```bash
   cp .env.template .env
   # Edit .env file with your configuration
   ```

4. **Start development server**
   ```bash
   npm start
   ```

The application will be available at `http://localhost:3000`

### Docker Development

1. **Build and run with Docker**
   ```bash
   docker-compose up --build
   ```

2. **Build Docker image only**
   ```bash
   npm run docker:build
   ```

3. **Run Docker container**
   ```bash
   npm run docker:run
   ```

## 🧪 Testing

### Unit Tests
```bash
npm test                    # Run tests in watch mode
npm run test:coverage       # Run tests with coverage report
```

### Code Quality
```bash
npm run lint                # Check code quality
npm run lint:fix            # Fix auto-fixable issues
npm run format              # Format code with Prettier
```

## 📁 Project Structure

```
src/
├── components/            # Reusable UI components
├── pages/                # Page-level components
├── services/             # API service layer
├── store/                # Redux store and slices
├── hooks/                # Custom React hooks
├── utils/                # Utility functions
├── assets/               # Static assets
└── styles/               # Global styles
```

## 🔧 Configuration

### Environment Variables

Key environment variables (see `.env.template` for complete list):

```env
REACT_APP_API_BASE_URL=http://localhost:8080
REACT_APP_WEBSOCKET_URL=ws://localhost:8080/ws
REACT_APP_GOOGLE_MAPS_API_KEY=your_api_key
```

### Available Scripts

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm test` - Run tests
- `npm run lint` - Check code quality
- `npm run format` - Format code
- `npm run analyze` - Analyze bundle size

## 🌐 Features in Detail

### Dashboard
- Real-time KPI monitoring
- Branch performance overview
- Courier activity tracking
- Revenue analytics

### Branch Management
- Branch creation and configuration
- Performance monitoring
- Resource allocation
- Operational insights

### Courier Management
- Courier onboarding and management
- Performance tracking
- Commission calculation
- Activity monitoring

### Analytics
- Advanced reporting dashboard
- Custom date range filtering
- Export capabilities (PDF, Excel, CSV)
- Performance metrics visualization

## 🔗 API Integration

The frontend communicates with the Java Spring Boot backend via REST APIs:

- **Base URL**: `http://localhost:8080/api`
- **Authentication**: JWT-based
- **WebSocket**: Real-time updates via WebSocket connection

## 🚀 Deployment

### Production Build

1. **Create production build**
   ```bash
   npm run build
   ```

2. **Deploy with Docker**
   ```bash
   docker build -t corporate-admin .
   docker run -p 80:80 corporate-admin
   ```

### Kubernetes Deployment

Kubernetes manifests are available in the `k8s/` directory:

```bash
kubectl apply -f k8s/
```

## 🤝 Contributing

1. Follow the coding standards defined in `.eslintrc.json`
2. Write tests for new features
3. Ensure all tests pass before submitting PR
4. Use conventional commit messages

## 📚 Documentation

- **API Documentation**: Located in `api-docs/openapi.yaml`
- **Architecture**: See `docs/architecture/`
- **Operations**: See `docs/operations/`

## 🔍 Monitoring

- **Health Check**: `/health`
- **Metrics**: Prometheus metrics available
- **Logs**: Structured logging with correlation IDs

## 📄 License

This project is part of the Social E-commerce Ecosystem.

## 📞 Support

For support and questions, please contact the development team.
