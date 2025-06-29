# API Documentation

## REST API Endpoints

### Core Endpoints
- GET /api/health: Health check endpoint
- GET /api/info: Service information
- GET /api/metrics: Service metrics

### Service-Specific Endpoints
[Service-specific endpoints will be documented here]

## Authentication
All endpoints require JWT authentication via Authorization header:
```
Authorization: Bearer <jwt-token>
```

## Error Handling
Standard HTTP status codes are used:
- 200: Success
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error
