# Courier Location Tracker Service

## Overview
The Courier Location Tracker Service provides real-time location tracking and monitoring capabilities for couriers and deliveries in the field.

## Features
- Real-time GPS tracking
- Location history and trails
- Geofencing and zone alerts
- Delivery proximity notifications
- Route deviation detection
- Battery and connectivity monitoring

## API Endpoints
- `POST /api/tracking/update` - Update courier location
- `GET /api/tracking/courier/{id}` - Get courier current location
- `GET /api/tracking/courier/{id}/history` - Get location history
- `POST /api/tracking/geofence` - Set up geofence alerts
- `GET /api/tracking/delivery/{id}/status` - Get delivery tracking status

## Configuration
Configure the service using environment variables:

```bash
NODE_ENV=production
PORT=3000
DATABASE_URL=mongodb://localhost:27017/courier_tracking
REDIS_URL=redis://localhost:6379
WEBSOCKET_PORT=3001
```

## Running the Service

### Using Docker
```bash
docker build -t courier-location-tracker .
docker run -p 3000:3000 -p 3001:3001 courier-location-tracker
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
- Redis
- Socket.io (WebSocket)
- Winston (logging)