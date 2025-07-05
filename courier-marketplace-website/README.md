# Gogidix Courier Marketplace Website

A modern React-based courier marketplace web application for the Gogidix Courier Services ecosystem, enabling customers to discover, compare, and book reliable delivery services online.

## 🚚 Architecture Overview

The Courier Marketplace Website serves as the primary customer-facing platform for the Gogidix courier ecosystem, providing:

- **Courier Discovery**: Location-based search with real-time availability
- **Service Booking**: Complete delivery order workflow with instant quotes
- **Live Tracking**: Real-time package tracking with GPS coordinates
- **Payment Processing**: Secure payment handling with multiple options
- **Customer Dashboard**: Order history and account management

## 🚀 Features

### Core Marketplace Features
- **Intelligent Courier Search**: GPS-powered location detection with service area filtering
- **Instant Quotes**: Real-time pricing calculations based on distance, urgency, and package type
- **Service Type Selection**: Standard, Express, Same-day, Overnight, White-glove services
- **Vehicle Type Filtering**: Bikes, motorcycles, cars, vans, trucks for different package sizes
- **Courier Profiles**: Detailed information, ratings, reviews, and service areas
- **Interactive Maps**: Real-time courier locations and route visualization

### Advanced Delivery Features
- **Live Package Tracking**: Real-time GPS tracking with delivery status updates
- **Route Optimization**: AI-powered route planning for efficient deliveries
- **Proof of Delivery**: Photo confirmation and digital signatures
- **Scheduled Pickups**: Advanced scheduling with time window selection
- **Multi-Package Support**: Bulk shipments and consolidated deliveries
- **Special Handling**: Fragile, temperature-controlled, and hazmat options

### Business Features
- **Corporate Accounts**: Business billing and enterprise features
- **White-Glove Service**: Premium handling for high-value items
- **International Shipping**: Cross-border delivery capabilities
- **Insurance Options**: Package protection and courier liability coverage
- **API Integration**: Third-party platform integrations
- **Analytics Dashboard**: Delivery performance and cost analysis

### Technical Features
- **Real-Time Communication**: WebSocket integration for live updates
- **Offline Capability**: PWA with offline browsing and caching
- **Performance Optimized**: Sub-2s load times with lazy loading
- **Mobile-First**: Responsive design with touch-optimized interface
- **Accessibility**: WCAG 2.1 AA compliant with screen reader support

## 🛠️ Technology Stack

### Frontend Core
- **React** 18.2.0 - Modern UI with concurrent features
- **TypeScript** 4.9.5 - Type-safe development
- **Material-UI** 5.x - Consistent design system
- **Redux Toolkit** - Predictable state management
- **React Query** - Server state management and caching

### Real-Time & Maps
- **Socket.IO Client** 4.5.4 - Real-time tracking and notifications
- **React Leaflet** 4.2.1 - Interactive maps and route visualization
- **Google Maps API** - Geocoding and place autocomplete
- **Geolocation API** - GPS location services

### Development & Testing
- **ESLint** - Code quality enforcement
- **Prettier** - Consistent code formatting
- **Jest** - Unit and integration testing
- **React Testing Library** - Component testing
- **Cypress** - End-to-end testing

### Deployment
- **Docker** - Containerized deployment
- **Nginx** - High-performance web server
- **Kubernetes** - Container orchestration with auto-scaling
- **GitHub Actions** - CI/CD pipeline

## 📦 Quick Start

### Prerequisites
- Node.js 18+ and npm
- Docker (for containerization)
- Kubernetes cluster (for deployment)

### Local Development

```bash
# Clone and install
git clone <repository-url>
cd courier-marketplace-website
npm install

# Environment setup
cp .env.example .env
# Configure API endpoints and keys

# Start development server
npm start
```

### Docker Deployment

```bash
# Build and run container
docker build -t gogidix-courier-marketplace .
docker run -p 3002:80 gogidix-courier-marketplace

# Using docker-compose
docker-compose up
```

### Kubernetes Deployment

```bash
# Deploy to cluster
kubectl apply -f k8s/

# Check deployment
kubectl get pods -n gogidix-production -l app=courier-marketplace-website
```

## 🏗️ Project Structure

```
src/
├── components/
│   ├── common/              # Reusable UI components
│   ├── layout/              # Layout components (Header, Footer, Navigation)
│   ├── courier/             # Courier-related components
│   │   ├── CourierCard.tsx
│   │   ├── CourierSearch.tsx
│   │   ├── CourierProfile.tsx
│   │   └── CourierComparison.tsx
│   ├── booking/             # Booking process components
│   │   ├── BookingForm.tsx
│   │   ├── ServiceSelector.tsx
│   │   ├── AddressForm.tsx
│   │   └── PaymentForm.tsx
│   ├── tracking/            # Package tracking components
│   │   ├── PackageTracker.tsx
│   │   ├── TrackingMap.tsx
│   │   ├── DeliveryStatus.tsx
│   │   └── TrackingHistory.tsx
│   ├── quotes/              # Quote generation components
│   │   ├── QuoteForm.tsx
│   │   ├── QuoteResults.tsx
│   │   ├── PriceBreakdown.tsx
│   │   └── ServiceComparison.tsx
│   └── map/                 # Map-related components
│       ├── CourierMap.tsx
│       ├── RouteVisualization.tsx
│       └── LocationPicker.tsx
├── pages/                   # Page components
│   ├── HomePage.tsx
│   ├── CourierSearchPage.tsx
│   ├── BookingPage.tsx
│   ├── TrackingPage.tsx
│   ├── QuotePage.tsx
│   └── DashboardPage.tsx
├── services/                # API integration
│   ├── courierService.ts
│   ├── bookingService.ts
│   ├── trackingService.ts
│   ├── quoteService.ts
│   └── paymentService.ts
├── store/                   # Redux store
│   ├── api/                 # RTK Query API definitions
│   └── slices/              # Redux slices
├── types/                   # TypeScript definitions
│   ├── courier.ts
│   ├── delivery.ts
│   ├── tracking.ts
│   └── common.ts
├── utils/                   # Utility functions
├── hooks/                   # Custom React hooks
└── styles/                  # Themes and global styles
```

## 🔌 API Integration

The website integrates with multiple courier microservices:

### Core Services
- **Courier Management API** (Port 8081) - Courier search, profiles, and availability
- **Tracking Service** (Port 8082) - Real-time package tracking and location updates
- **Fare Calculator** (Port 8083) - Dynamic pricing and quote generation
- **Geo-Routing Service** (Port 8086) - Route optimization and distance calculations

### Supporting Services
- **Customer Onboarding** (Port 8085) - Customer registration and profile management
- **Pickup Engine** (Port 8087) - Pickup scheduling and optimization
- **Location Tracker** (Port 8088) - Real-time GPS tracking integration
- **Notification Service** (Port 8089) - Push notifications and alerts

### WebSocket Integration

```typescript
// Real-time tracking updates
import { io } from 'socket.io-client';

const trackingSocket = io(process.env.REACT_APP_TRACKING_WEBSOCKET_URL);

trackingSocket.on('locationUpdate', (data) => {
  updateCourierLocation(data.courierId, data.coordinates);
});

trackingSocket.on('statusUpdate', (data) => {
  updateDeliveryStatus(data.orderId, data.status);
});
```

## 🗺️ Real-Time Tracking

### GPS Tracking Implementation
```typescript
// Live tracking component
import { MapContainer, TileLayer, Marker, Polyline } from 'react-leaflet';

const LiveTrackingMap = ({ orderId, courierLocation, route }) => {
  return (
    <MapContainer center={courierLocation} zoom={15}>
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
      <Marker position={courierLocation} icon={courierIcon}>
        <Popup>Your courier is here</Popup>
      </Marker>
      <Polyline positions={route} color="blue" />
    </MapContainer>
  );
};
```

### Status Updates
```typescript
// Delivery status tracking
const useDeliveryTracking = (orderId: string) => {
  const [status, setStatus] = useState<DeliveryStatus>();
  const [location, setLocation] = useState<Coordinates>();
  
  useEffect(() => {
    const socket = io(TRACKING_WEBSOCKET_URL);
    
    socket.emit('trackOrder', { orderId });
    
    socket.on('statusUpdate', (data) => {
      setStatus(data.status);
      setLocation(data.location);
    });
    
    return () => socket.disconnect();
  }, [orderId]);
  
  return { status, location };
};
```

## 💳 Payment Integration

### Stripe Integration
```typescript
// Payment processing with Stripe
import { loadStripe } from '@stripe/stripe-js';
import { Elements, CardElement, useStripe, useElements } from '@stripe/react-stripe-js';

const PaymentForm = ({ total, onSuccess }) => {
  const stripe = useStripe();
  const elements = useElements();
  
  const handleSubmit = async (event) => {
    event.preventDefault();
    
    const result = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
        billing_details: { name: customerName }
      }
    });
    
    if (result.error) {
      showError(result.error.message);
    } else {
      onSuccess(result.paymentIntent);
    }
  };
  
  return (
    <form onSubmit={handleSubmit}>
      <CardElement />
      <button type="submit" disabled={!stripe}>
        Pay ${total}
      </button>
    </form>
  );
};
```

## 🧪 Testing

### Unit Testing
```bash
# Run tests
npm test

# Run with coverage
npm run test:coverage

# Run in CI mode
npm run test:ci
```

### Integration Testing
```typescript
// Example courier search test
describe('CourierSearch', () => {
  it('should find couriers by location', async () => {
    render(<CourierSearch />);
    
    const locationInput = screen.getByPlaceholderText('Enter pickup location');
    const searchButton = screen.getByText('Find Couriers');
    
    fireEvent.change(locationInput, { target: { value: 'New York, NY' } });
    fireEvent.click(searchButton);
    
    await waitFor(() => {
      expect(screen.getByText('Available couriers in New York, NY')).toBeInTheDocument();
    });
  });
});
```

### End-to-End Testing
```typescript
// Cypress E2E test for booking flow
describe('Booking Flow', () => {
  it('should complete a delivery booking', () => {
    cy.visit('/');
    cy.get('[data-testid="book-delivery-button"]').click();
    
    // Fill pickup details
    cy.get('[data-testid="pickup-address"]').type('123 Main St, New York, NY');
    cy.get('[data-testid="dropoff-address"]').type('456 Broadway, New York, NY');
    
    // Select service type
    cy.get('[data-testid="service-express"]').click();
    
    // Select courier
    cy.get('[data-testid="courier-card"]').first().click();
    
    // Complete payment
    cy.get('[data-testid="payment-form"]').within(() => {
      cy.get('[data-testid="card-number"]').type('4242424242424242');
      cy.get('[data-testid="card-expiry"]').type('1225');
      cy.get('[data-testid="card-cvc"]').type('123');
    });
    
    cy.get('[data-testid="submit-booking"]').click();
    
    cy.url().should('include', '/booking-confirmation');
    cy.get('[data-testid="tracking-number"]').should('be.visible');
  });
});
```

## 🚀 Performance

### Optimization Strategies
- **Code Splitting**: Route-based and component-based splitting
- **Image Optimization**: WebP format with progressive loading
- **Caching**: Service Worker with background sync
- **Bundle Analysis**: Webpack bundle analyzer integration

### Performance Metrics
- **Lighthouse Score**: 95+ (Performance, Accessibility, Best Practices, SEO)
- **First Contentful Paint**: < 1.0s
- **Time to Interactive**: < 2.0s
- **Cumulative Layout Shift**: < 0.1

### Real-Time Performance
- **WebSocket Connection**: < 100ms connection time
- **Location Updates**: < 500ms update frequency
- **Map Rendering**: 60fps smooth animations
- **Search Results**: < 300ms response time

## 🔒 Security

### Security Measures
- **Authentication**: JWT tokens with automatic refresh
- **Input Validation**: Client and server-side validation
- **Data Encryption**: HTTPS enforcement and data encryption
- **Rate Limiting**: API rate limiting and DDoS protection
- **Content Security Policy**: Comprehensive CSP headers

### Real-Time Security
```typescript
// Secure WebSocket connection
const socket = io(WEBSOCKET_URL, {
  auth: {
    token: getAuthToken()
  },
  transports: ['websocket'],
  secure: true
});

socket.on('connect_error', (error) => {
  if (error.message === 'Authentication error') {
    redirectToLogin();
  }
});
```

## 🌍 Internationalization

### Multi-language Support
```typescript
// i18n configuration for courier services
const resources = {
  en: {
    courier: {
      searchPlaceholder: 'Enter pickup location',
      bookNow: 'Book Now',
      trackPackage: 'Track Package',
      getQuote: 'Get Quote'
    }
  },
  es: {
    courier: {
      searchPlaceholder: 'Ingrese la ubicación de recogida',
      bookNow: 'Reservar Ahora',
      trackPackage: 'Rastrear Paquete',
      getQuote: 'Obtener Cotización'
    }
  }
};
```

## 📊 Analytics

### Tracking Implementation
```typescript
// Enhanced analytics for courier marketplace
import { gtag } from 'ga-gtag';

// Track courier search
const trackCourierSearch = (location, serviceType, filters) => {
  gtag('event', 'courier_search', {
    event_category: 'search',
    event_label: location,
    service_type: serviceType,
    custom_parameters: filters
  });
};

// Track booking completion
const trackBookingComplete = (courierId, serviceType, price, distance) => {
  gtag('event', 'booking_complete', {
    event_category: 'conversion',
    value: price,
    currency: 'USD',
    courier_id: courierId,
    service_type: serviceType,
    distance: distance
  });
};

// Track delivery completion
const trackDeliveryComplete = (orderId, rating, deliveryTime) => {
  gtag('event', 'delivery_complete', {
    event_category: 'fulfillment',
    order_id: orderId,
    rating: rating,
    delivery_time: deliveryTime
  });
};
```

## 🚀 Deployment

### Environment Configuration

| Environment | Domain | Purpose |
|-------------|--------|---------|
| Development | localhost:3002 | Local development |
| Staging | staging-courier.exaltapp.com | QA testing |
| Production | courier.exaltapp.com | Live customer traffic |

### Deployment Pipeline
1. **Build**: TypeScript compilation and optimization
2. **Test**: Unit, integration, and E2E testing
3. **Security Scan**: Dependency and code scanning
4. **Docker Build**: Multi-stage container creation
5. **Deploy**: Kubernetes rolling deployment
6. **Health Check**: Automated health verification
7. **Monitoring**: Performance and error monitoring

## 📞 Support

### Development Team
- **Frontend Team**: frontend@exaltapp.com
- **Backend Integration**: courier-api@exaltapp.com
- **Real-Time Systems**: realtime@exaltapp.com
- **DevOps**: devops@exaltapp.com

### Documentation
- **API Documentation**: https://docs.exaltapp.com/courier
- **Real-Time API**: https://docs.exaltapp.com/tracking
- **Design System**: https://design.exaltapp.com
- **Deployment Guide**: https://deploy.exaltapp.com

## 📄 License

This project is proprietary software owned by Gogidix Application Limited. All rights reserved.