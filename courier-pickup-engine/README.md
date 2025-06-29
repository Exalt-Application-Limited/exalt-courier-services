# Courier Pickup Engine Service

## Overview
The Courier Pickup Engine Service handles intelligent pickup scheduling, optimization, and coordination for courier operations.

## Features
- Pickup request processing
- Intelligent pickup scheduling
- Route optimization for pickups
- Capacity and availability management
- Real-time pickup tracking
- Integration with courier assignment

## API Endpoints
- `POST /api/pickups` - Create pickup request
- `GET /api/pickups` - List pickup requests
- `GET /api/pickups/{id}` - Get pickup details
- `PUT /api/pickups/{id}/assign` - Assign courier to pickup
- `PUT /api/pickups/{id}/status` - Update pickup status

## Configuration
Configure the service using environment variables:

```bash
NODE_ENV=production
PORT=3000
DATABASE_URL=mongodb://localhost:27017/courier_pickup
REDIS_URL=redis://localhost:6379
```

## Running the Service

### Using Docker
```bash
docker build -t courier-pickup-engine .
docker run -p 3000:3000 courier-pickup-engine
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
- Winston (logging)