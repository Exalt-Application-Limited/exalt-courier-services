# Courier Events Service

## Overview
The Courier Events Service handles real-time event processing and notifications for the courier ecosystem. It manages event streaming, processing, and distribution to various system components.

## Features
- Real-time event processing
- Event streaming and distribution
- Event history and replay
- Custom event filtering
- Integration with notification systems

## API Endpoints
- `POST /api/events` - Publish an event
- `GET /api/events` - Get event history
- `GET /api/events/{id}` - Get specific event
- `POST /api/events/subscribe` - Subscribe to event types
- `DELETE /api/events/subscribe/{id}` - Unsubscribe from events

## Configuration
Configure the service using environment variables:

```bash
NODE_ENV=production
PORT=3000
DATABASE_URL=mongodb://localhost:27017/courier_events
KAFKA_BROKERS=localhost:9092
REDIS_URL=redis://localhost:6379
```

## Running the Service

### Using Docker
```bash
docker build -t courier-events-service .
docker run -p 3000:3000 courier-events-service
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
- Apache Kafka
- Redis
- Winston (logging)