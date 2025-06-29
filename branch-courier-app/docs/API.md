# Branch Courier App API Documentation

## Overview
The Branch Courier App provides RESTful APIs for branch operations including package processing, customer service, and branch management.

## Base URL
```
https://api.courier.example.com/branch/v1
```

## Authentication
All API endpoints require authentication using Bearer tokens.

```http
Authorization: Bearer <your-jwt-token>
```

## Package Processing APIs

### Process Incoming Package
Process a new package at the branch.

**Endpoint:** `POST /packages/incoming`

**Request Body:**
```json
{
  "sendingAddress": {
    "name": "John Doe",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "phone": "+1234567890"
  },
  "receivingAddress": {
    "name": "Jane Smith",
    "address": "456 Oak Ave",
    "city": "Los Angeles",
    "state": "CA",
    "zipCode": "90210",
    "phone": "+0987654321"
  },
  "packageDetails": {
    "weight": 2.5,
    "dimensions": {
      "length": 12.0,
      "width": 8.0,
      "height": 4.0
    },
    "value": 150.00,
    "description": "Electronics",
    "serviceType": "STANDARD"
  }
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "packageId": "PKG123456789",
    "trackingNumber": "TRK987654321",
    "status": "RECEIVED",
    "estimatedDelivery": "2024-01-15T14:30:00Z",
    "cost": 15.50,
    "labelUrl": "https://labels.courier.com/PKG123456789.pdf"
  }
}
```

### Dispatch Package
Mark a package as dispatched for delivery.

**Endpoint:** `PUT /packages/{packageId}/dispatch`

**Request Body:**
```json
{
  "driverId": "DRV12345",
  "routeId": "RT67890",
  "dispatchTime": "2024-01-14T09:00:00Z"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "packageId": "PKG123456789",
    "status": "IN_TRANSIT",
    "dispatchTime": "2024-01-14T09:00:00Z",
    "estimatedDelivery": "2024-01-15T14:30:00Z"
  }
}
```

### Get Package Status
Retrieve the current status of a package.

**Endpoint:** `GET /packages/{packageId}/status`

**Response:**
```json
{
  "success": true,
  "data": {
    "packageId": "PKG123456789",
    "trackingNumber": "TRK987654321",
    "status": "IN_TRANSIT",
    "lastUpdate": "2024-01-14T09:15:00Z",
    "location": "Local Distribution Center",
    "estimatedDelivery": "2024-01-15T14:30:00Z",
    "statusHistory": [
      {
        "status": "RECEIVED",
        "timestamp": "2024-01-13T10:30:00Z",
        "location": "Main Branch"
      },
      {
        "status": "IN_TRANSIT",
        "timestamp": "2024-01-14T09:00:00Z",
        "location": "Local Distribution Center"
      }
    ]
  }
}
```

## Customer Service APIs

### Create Customer Inquiry
Handle customer inquiries and complaints.

**Endpoint:** `POST /customer/inquiries`

**Request Body:**
```json
{
  "customerId": "CUST789012",
  "inquiryType": "PACKAGE_INQUIRY",
  "subject": "Package not delivered",
  "message": "My package was supposed to be delivered yesterday but I haven't received it yet.",
  "trackingNumber": "TRK987654321",
  "priority": "MEDIUM"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "inquiryId": "INQ345678",
    "status": "OPEN",
    "assignedAgent": "AGT001",
    "createdAt": "2024-01-14T11:00:00Z",
    "expectedResponse": "2024-01-14T17:00:00Z"
  }
}
```

### Update Inquiry Status
Update the status of a customer inquiry.

**Endpoint:** `PUT /customer/inquiries/{inquiryId}/status`

**Request Body:**
```json
{
  "status": "RESOLVED",
  "resolution": "Package was delivered to neighbor as requested by customer",
  "agentId": "AGT001"
}
```

### Get Customer Package History
Retrieve package history for a customer.

**Endpoint:** `GET /customer/{customerId}/packages`

**Query Parameters:**
- `limit`: Number of packages to return (default: 10)
- `offset`: Number of packages to skip (default: 0)
- `status`: Filter by package status (optional)

**Response:**
```json
{
  "success": true,
  "data": {
    "packages": [
      {
        "packageId": "PKG123456789",
        "trackingNumber": "TRK987654321",
        "status": "DELIVERED",
        "shippingDate": "2024-01-13T10:30:00Z",
        "deliveryDate": "2024-01-15T14:30:00Z",
        "cost": 15.50
      }
    ],
    "totalCount": 25,
    "hasMore": true
  }
}
```

## Branch Management APIs

### Get Branch Statistics
Retrieve daily operational statistics for the branch.

**Endpoint:** `GET /branch/statistics`

**Query Parameters:**
- `date`: Specific date (YYYY-MM-DD format, default: today)

**Response:**
```json
{
  "success": true,
  "data": {
    "date": "2024-01-14",
    "packagesReceived": 156,
    "packagesDispatched": 142,
    "packagesDelivered": 138,
    "customerInquiries": 23,
    "revenue": 2475.80,
    "staffPerformance": {
      "totalStaff": 8,
      "presentStaff": 7,
      "avgProcessingTime": 4.2
    }
  }
}
```

### Schedule Pickup
Schedule a package pickup from customer location.

**Endpoint:** `POST /branch/pickups`

**Request Body:**
```json
{
  "customerId": "CUST789012",
  "pickupAddress": {
    "name": "John Doe",
    "address": "123 Business Ave",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "phone": "+1234567890"
  },
  "preferredTime": "2024-01-15T14:00:00Z",
  "packageCount": 3,
  "specialInstructions": "Ring doorbell twice"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "pickupId": "PU789012",
    "scheduledTime": "2024-01-15T14:00:00Z",
    "driverId": "DRV12345",
    "routeId": "RT67890",
    "confirmationCode": "CONF456789"
  }
}
```

## Staff Management APIs

### Get Staff Schedule
Retrieve staff schedules for the branch.

**Endpoint:** `GET /branch/staff/schedule`

**Query Parameters:**
- `date`: Specific date (YYYY-MM-DD format)
- `week`: Get weekly schedule (boolean)

**Response:**
```json
{
  "success": true,
  "data": {
    "schedules": [
      {
        "staffId": "STF001",
        "name": "Alice Johnson",
        "role": "BRANCH_MANAGER",
        "shift": "MORNING",
        "startTime": "08:00",
        "endTime": "16:00",
        "status": "PRESENT"
      },
      {
        "staffId": "STF002",
        "name": "Bob Smith",
        "role": "PACKAGE_HANDLER",
        "shift": "AFTERNOON",
        "startTime": "12:00",
        "endTime": "20:00",
        "status": "PRESENT"
      }
    ]
  }
}
```

## Error Handling

### Standard Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human readable error message",
    "details": "Additional error details if available"
  }
}
```

### Common Error Codes
- `AUTH_REQUIRED`: Authentication required
- `AUTH_INVALID`: Invalid authentication token
- `PACKAGE_NOT_FOUND`: Package not found
- `CUSTOMER_NOT_FOUND`: Customer not found
- `VALIDATION_ERROR`: Request validation failed
- `INTERNAL_ERROR`: Internal server error

## Rate Limiting
API requests are limited to 1000 requests per hour per authentication token.

## Webhooks
The system supports webhooks for real-time notifications:

### Package Status Updates
```json
{
  "event": "package.status.updated",
  "timestamp": "2024-01-14T09:15:00Z",
  "data": {
    "packageId": "PKG123456789",
    "trackingNumber": "TRK987654321",
    "previousStatus": "RECEIVED",
    "currentStatus": "IN_TRANSIT",
    "location": "Local Distribution Center"
  }
}
```

### Customer Inquiry Updates
```json
{
  "event": "inquiry.status.updated",
  "timestamp": "2024-01-14T15:30:00Z",
  "data": {
    "inquiryId": "INQ345678",
    "customerId": "CUST789012",
    "previousStatus": "OPEN",
    "currentStatus": "RESOLVED",
    "agentId": "AGT001"
  }
}
```