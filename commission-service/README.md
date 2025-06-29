# Commission Service

Part of the Courier Services Domain in the Micro-Social-Ecommerce-Ecosystem project.

## Overview

The Commission Service is responsible for managing commission rules, calculating commissions for partners, and handling payment distribution. It works closely with the Payout Service to ensure accurate financial transactions.

## Features

- Partner management for different types of partnerships (Courier, Vendor, Franchise, etc.)
- Flexible commission rule configuration with support for different rate types
- Commission calculation based on order value and partner type
- Automated commission recalculation when rules change
- Payment generation and processing for partner commissions
- Comprehensive reporting and historical tracking

## Tech Stack

- Java 11
- Spring Boot 2.7.x
- Spring Data JPA
- MySQL Database
- Flyway for database migrations
- Spring Cloud for microservice integration

## API Endpoints

### Partner Management

- `POST /api/v1/partners` - Create a new partner
- `GET /api/v1/partners/{id}` - Get partner details
- `PUT /api/v1/partners/{id}` - Update partner information
- `PUT /api/v1/partners/{id}/status` - Update partner status
- `DELETE /api/v1/partners/{id}` - Delete a partner
- `GET /api/v1/partners` - Get all partners (with optional type and status filters)
- `GET /api/v1/partners/search` - Search partners by name

### Commission Rule Management

- `POST /api/v1/commission-rules` - Create a new commission rule
- `GET /api/v1/commission-rules/{id}` - Get rule details
- `PUT /api/v1/commission-rules/{id}` - Update rule information
- `PUT /api/v1/commission-rules/{id}/status` - Update rule status
- `DELETE /api/v1/commission-rules/{id}` - Delete a rule
- `GET /api/v1/commission-rules` - Get all rules (with optional filters)
- `GET /api/v1/commission-rules/active` - Get active rules for a specific date
- `GET /api/v1/commission-rules/applicable` - Find applicable rule for a specific partner and amount

### Commission Management

- `POST /api/v1/commissions/calculate` - Calculate commission for an order
- `GET /api/v1/commissions/{id}` - Get commission entry details
- `PUT /api/v1/commissions/{id}/status` - Update commission status
- `DELETE /api/v1/commissions/{id}` - Delete a commission entry
- `GET /api/v1/commissions/partner/{partnerId}` - Get commissions for a specific partner
- `GET /api/v1/commissions/partner/{partnerId}/date-range` - Get commissions by date range
- `GET /api/v1/commissions/status/{status}` - Get commissions by status
- `GET /api/v1/commissions/order/{orderId}` - Get commissions for a specific order
- `GET /api/v1/commissions/unpaid` - Get all unpaid commissions
- `POST /api/v1/commissions/recalculate` - Recalculate commissions for a date range
- `POST /api/v1/commissions/bulk-calculate` - Bulk calculate commissions for multiple orders

### Payment Management

- `POST /api/v1/payments` - Create a new payment
- `GET /api/v1/payments/{id}` - Get payment details
- `PUT /api/v1/payments/{id}` - Update payment information
- `PUT /api/v1/payments/{id}/status` - Update payment status
- `DELETE /api/v1/payments/{id}` - Delete a payment
- `POST /api/v1/payments/{id}/process` - Process a specific payment
- `GET /api/v1/payments/partner/{partnerId}` - Get payments for a specific partner
- `GET /api/v1/payments/status/{status}` - Get payments by status
- `GET /api/v1/payments/date-range` - Get payments by date range
- `GET /api/v1/payments/period` - Get payments by period
- `GET /api/v1/payments/pending` - Get all pending payments
- `POST /api/v1/payments/generate/{partnerId}` - Generate payment for a partner
- `POST /api/v1/payments/generate-all` - Generate payments for all partners
- `POST /api/v1/payments/process-pending` - Process all pending payments
- `GET /api/v1/payments/{id}/details` - Get detailed breakdown of a payment

## Database Structure

- **partner** - Stores partner information and status
- **commission_rule** - Defines rules for commission calculation
- **commission_entry** - Tracks calculated commissions for orders
- **partner_payment** - Manages payment records to partners
- **payment_details** - Links commission entries to payments

## Scheduled Jobs

- Daily payment processing (Every day at 2 AM)

## Getting Started

1. Ensure MySQL is running
2. Run the application: `./mvnw spring-boot:run`
3. Access the API at http://localhost:8085

## Configuration

See `application.yml` for configuration options, including:
- Database connection details
- Payment processing schedule
- Integration with other services
- Commission calculation parameters

## Integration Points

- **Payout Service** - For coordinating partner payments with courier payouts
- **Courier Management** - For accessing partner/courier information
- **Order Service** - For receiving order events and calculating commissions
