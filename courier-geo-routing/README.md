# Courier Geo Routing Service

## Overview
The Courier Geo Routing Service provides intelligent routing and optimization for courier deliveries using geospatial data and advanced routing algorithms.

## Features
- Real-time route optimization
- Multi-stop delivery planning
- Traffic-aware routing
- Geographic zone management
- Route efficiency analytics
- Integration with mapping services

## API Endpoints
- `POST /api/routing/optimize` - Optimize delivery route
- `POST /api/routing/calculate` - Calculate route between points
- `GET /api/routing/zones` - Get delivery zones
- `POST /api/routing/batch-optimize` - Optimize multiple routes

## Configuration
Configure the service using environment variables:

```bash
NODE_ENV=production
PORT=3000
DATABASE_URL=mongodb://localhost:27017/courier_routing
MAPS_API_KEY=your_maps_api_key
TRAFFIC_API_KEY=your_traffic_api_key
```

## Running the Service

### Using Docker
```bash
docker build -t courier-geo-routing .
docker run -p 3000:3000 courier-geo-routing
```

### Using Node.js
```bash
npm install
npm start
```

## Health Check
The service provides health check endpoint at `/health`

## Dependencies
- Express.js
- MongoDB/Mongoose
- Google Maps API
- Here Maps API
- Winston (logging)