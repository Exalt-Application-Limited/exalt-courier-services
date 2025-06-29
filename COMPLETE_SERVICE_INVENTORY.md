# Complete Courier Services Inventory

## Summary
- **Total Services**: 30
- **Backend Services**: 23
- **Frontend Applications**: 7

## Backend Services (23)

### Core Business Services (9)
1. `courier-shared` - Shared utilities and common code
2. `courier-management` - Main courier management service
3. `tracking-service` - Package tracking functionality
4. `routing-service` - Route optimization service
5. `courier-onboarding` - Courier registration/onboarding
6. `courier-subscription` - Subscription management
7. `commission-service` - Commission calculations
8. `payout-service` - Payment disbursements
9. `international-shipping` - Cross-border shipping

### Branch Operations (2)
10. `Corporate Courier Branch app` - Corporate branch management (Spring Boot)
11. `Courier Branch App` - Branch operations service (Spring Boot)

### Third-Party Integrations (4)
12. `third-party-integration/common-integration-lib` - Common integration utilities
13. `third-party-integration/dhl-integration` - DHL API integration
14. `third-party-integration/fedex-integration` - FedEx API integration
15. `third-party-integration/ups-integration` - UPS API integration

### Infrastructure Services (8)
16. `courier-network-locations` - Network location management
17. `courier-production` - Production environment configs
18. `courier-staging` - Staging environment configs
19. `infrastructure` - Infrastructure utilities
20. `readiness-reports` - Service readiness monitoring
21. `tools archives` - Archived tools and utilities
22. `driver-mobile-app` - Mobile app backend (Node.js)
23. `regional-admin-system` - Regional administration backend

## Frontend Applications (7)

### Web Applications (3)
1. `corporate-admin` - Corporate administration dashboard (React)
2. `global-hq-admin` - Global HQ administration interface (React)
3. `regional-admin` - Regional administration portal (React)

### Mobile Applications (4)
4. `branch-courier-app` - Branch operations mobile app (React Native)
5. `user-mobile-app` - Customer mobile application (React Native)
6. `driver-mobile-app` - Driver mobile application (includes backend)
7. `Courier Branch App` - Additional branch app (Note: also has backend component)

## Architecture Overview
```
┌─────────────────────────────────────────────────────────┐
│                   Frontend Layer (7)                     │
├─────────────────────────────────────────────────────────┤
│                   Backend Services (23)                  │
├─────────────────────────────────────────────────────────┤
│              Shared Infrastructure & Utilities           │
└─────────────────────────────────────────────────────────┘
```

## Deployment Status
- ✅ All services have Dockerfiles
- ✅ Frontend applications configured
- ✅ Development environments set up
- 🔄 Ready for Week 6: Cloud Infrastructure

## Notes
- Services with spaces in folder names: "Corporate Courier Branch app", "Courier Branch App"
- Some services serve dual purposes (e.g., driver-mobile-app has both frontend and backend)
- All backend services use Spring Boot 3.1.0 with Java 17
- Frontend apps use React 18.2.0 or React Native 0.71.11

Last Updated: June 10, 2025