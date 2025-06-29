# Payout Service

Part of the Courier Services Domain in the Micro-Social-Ecommerce-Ecosystem project.

## Overview

The Payout Service is responsible for calculating and processing payouts to couriers based on their earnings. It manages the entire payout lifecycle from calculating earnings to processing payments.

## Features

- Automatic weekly payout calculation
- Scheduled processing of pending payouts
- Tracking of earnings and payout history
- RESTful API for payout management
- Integration with payment providers (simulated for now)

## Tech Stack

- Java 11
- Spring Boot 2.7.x
- Spring Data JPA
- MySQL Database
- Flyway for database migrations
- Spring Cloud for microservice integration

## API Endpoints

- `GET /api/v1/payouts/courier/{courierId}` - Get payout history for a courier
- `GET /api/v1/payouts/{payoutId}` - Get detailed information about a specific payout
- `POST /api/v1/payouts/calculate?startDate=...&endDate=...` - Trigger payout calculation for a date range
- `POST /api/v1/payouts/process` - Trigger processing of pending payouts

## Database Structure

- **earnings** - Tracks individual earning entries for couriers
- **payout** - Manages payout records with status tracking
- **payout_items** - Links earnings to their associated payouts

## Scheduled Jobs

- Weekly payout calculation (Every Monday at 1 AM)
- Daily payout processing (Every day at 5 AM)

## Getting Started

1. Ensure MySQL is running
2. Run the application: `./mvnw spring-boot:run`
3. Access the API at http://localhost:8084

## Configuration

See `application.yml` for configuration options, including:
- Database connection details
- Scheduling configuration
- Payment provider settings
