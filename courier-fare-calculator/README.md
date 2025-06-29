# Courier Fare Calculator Service

## Overview
The Courier Fare Calculator Service handles dynamic pricing and fare calculations for courier services based on distance, weight, priority, and other factors.

## Features
- Dynamic fare calculation
- Distance-based pricing
- Weight and size considerations
- Priority and urgency factors
- Regional pricing variations
- Bulk discount calculations

## API Endpoints
- `POST /api/fare/calculate` - Calculate fare for delivery
- `GET /api/fare/rates` - Get current rate structure
- `POST /api/fare/bulk-calculate` - Calculate fares for multiple deliveries
- `GET /api/fare/zones` - Get pricing zones

## Configuration
Configure the service using environment variables:

```bash
NODE_ENV=production
PORT=3000
DATABASE_URL=mongodb://localhost:27017/courier_fare
MAPS_API_KEY=your_maps_api_key
```

## Running the Service

### Using Docker
```bash
docker build -t courier-fare-calculator .
docker run -p 3000:3000 courier-fare-calculator
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
- Winston (logging)