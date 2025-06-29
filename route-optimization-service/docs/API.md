# Route Optimization Service API Documentation

## Core API

### RouteOptimizationApplication
- RouteOptimizationApplication(): Default constructor
- RouteOptimizationApplication(String name, String description): Constructor with name and description
- UUID getId(): Get the application ID
- String getName(): Get the application name
- void setName(String name): Set the application name
- String getDescription(): Get the application description
- void setDescription(String description): Set the application description
- LocalDateTime getCreatedAt(): Get the creation timestamp
- LocalDateTime getUpdatedAt(): Get the last update timestamp

### SecurityConfig
- SecurityConfig(): Default constructor with secure route optimization defaults
- List<String> getAllowedOrigins(): Get the allowed origins for CORS
- void setAllowedOrigins(List<String> allowedOrigins): Set the allowed origins
- boolean isCsrfEnabled(): Check if CSRF protection is enabled
- void setCsrfEnabled(boolean csrfEnabled): Enable or disable CSRF protection
- String getTokenExpirationSeconds(): Get the token expiration time in seconds
- void setTokenExpirationSeconds(String tokenExpirationSeconds): Set the token expiration time
- String getJwtSecret(): Get the JWT secret for route optimization authentication
- void setJwtSecret(String jwtSecret): Set the JWT secret

## Route Planning API

### IntelligentRoutePlanning
- IntelligentRoutePlanning(): Default constructor
- IntelligentRoutePlanning(String name, String description): Constructor with name and description
- OptimizedRoute calculateOptimalRoute(OptimizationRequest request): Calculate optimal delivery route
- List<RouteOption> generateRouteAlternatives(OptimizationRequest request): Generate multiple route options
- RouteMetrics analyzeRoutePerformance(OptimizedRoute route): Analyze route performance metrics
- void optimizeMultiObjective(OptimizationRequest request): Multi-objective route optimization

### OptimizationRequest
- OptimizationRequest(): Default constructor
- UUID getId(): Get the optimization request ID
- void setId(UUID id): Set the optimization request ID
- UUID getDriverId(): Get the driver ID
- void setDriverId(UUID driverId): Set the driver ID
- UUID getVehicleId(): Get the vehicle ID
- void setVehicleId(UUID vehicleId): Set the vehicle ID
- List<DeliveryPoint> getDeliveryPoints(): Get delivery points
- void setDeliveryPoints(List<DeliveryPoint> deliveryPoints): Set delivery points
- GeoLocation getStartLocation(): Get route start location
- void setStartLocation(GeoLocation startLocation): Set route start location
- List<OptimizationObjective> getObjectives(): Get optimization objectives
- void setObjectives(List<OptimizationObjective> objectives): Set optimization objectives
- RouteConstraints getConstraints(): Get route constraints
- void setConstraints(RouteConstraints constraints): Set route constraints

### OptimizedRoute
- OptimizedRoute(): Default constructor
- UUID getRouteId(): Get the route ID
- void setRouteId(UUID routeId): Set the route ID
- List<RouteSegment> getSegments(): Get route segments
- void setSegments(List<RouteSegment> segments): Set route segments
- Duration getEstimatedTravelTime(): Get estimated travel time
- void setEstimatedTravelTime(Duration estimatedTravelTime): Set estimated travel time
- double getTotalDistance(): Get total route distance
- void setTotalDistance(double totalDistance): Set total route distance
- RouteMetrics getMetrics(): Get route performance metrics
- void setMetrics(RouteMetrics metrics): Set route performance metrics
- LocalDateTime getOptimizationTime(): Get optimization timestamp
- void setOptimizationTime(LocalDateTime optimizationTime): Set optimization timestamp

## Real-time Route Adjustment API

### RealtimeRouteAdjustment
- RealtimeRouteAdjustment(): Default constructor
- RealtimeRouteAdjustment(String name, String description): Constructor with name and description
- OptimizedRoute adjustRouteForTraffic(UUID routeId, TrafficCondition traffic): Adjust route for traffic conditions
- OptimizedRoute insertDelivery(UUID routeId, DeliveryPoint newDelivery): Insert new delivery into existing route
- OptimizedRoute removeDelivery(UUID routeId, UUID deliveryId): Remove delivery from route
- RouteUpdate recalculateRoute(UUID routeId, RouteUpdateReason reason): Recalculate entire route

### TrafficCondition
- TrafficCondition(): Default constructor
- String getIncidentId(): Get traffic incident ID
- void setIncidentId(String incidentId): Set traffic incident ID
- GeoLocation getLocation(): Get incident location
- void setLocation(GeoLocation location): Set incident location
- TrafficSeverity getSeverity(): Get traffic severity level
- void setSeverity(TrafficSeverity severity): Set traffic severity level
- Duration getEstimatedDelay(): Get estimated delay
- void setEstimatedDelay(Duration estimatedDelay): Set estimated delay
- LocalDateTime getStartTime(): Get incident start time
- void setStartTime(LocalDateTime startTime): Set incident start time
- LocalDateTime getEstimatedEndTime(): Get estimated end time
- void setEstimatedEndTime(LocalDateTime estimatedEndTime): Set estimated end time

### RouteUpdate
- RouteUpdate(): Default constructor
- UUID getRouteId(): Get the route ID
- void setRouteId(UUID routeId): Set the route ID
- List<RouteSegment> getUpdatedSegments(): Get updated route segments
- void setUpdatedSegments(List<RouteSegment> segments): Set updated route segments
- RouteUpdateReason getReason(): Get update reason
- void setReason(RouteUpdateReason reason): Set update reason
- Duration getTimeSaving(): Get time saving from update
- void setTimeSaving(Duration timeSaving): Set time saving
- LocalDateTime getUpdateTime(): Get update timestamp
- void setUpdateTime(LocalDateTime updateTime): Set update timestamp

## VRP Solver API

### VRPSolver
- VRPSolver(): Default constructor
- VRPSolver(String name, String description): Constructor with name and description
- VRPSolution solve(VRPProblem problem): Solve Vehicle Routing Problem
- List<OptimizedRoute> generateOptimalRoutes(VRPRequest request): Generate optimal routes for fleet
- SolutionMetrics analyzeSolution(VRPSolution solution): Analyze VRP solution quality
- boolean validateSolution(VRPSolution solution, VRPProblem problem): Validate solution feasibility

### VRPProblem
- VRPProblem(): Default constructor
- List<Vehicle> getVehicles(): Get fleet vehicles
- void setVehicles(List<Vehicle> vehicles): Set fleet vehicles
- List<Depot> getDepots(): Get depot locations
- void setDepots(List<Depot> depots): Set depot locations
- List<Customer> getCustomers(): Get customer delivery points
- void setCustomers(List<Customer> customers): Set customer delivery points
- VRPConstraints getConstraints(): Get problem constraints
- void setConstraints(VRPConstraints constraints): Set problem constraints
- OptimizationObjectives getObjectives(): Get optimization objectives
- void setObjectives(OptimizationObjectives objectives): Set optimization objectives

### VRPSolution
- VRPSolution(): Default constructor
- List<OptimizedRoute> getRoutes(): Get optimized routes
- void setRoutes(List<OptimizedRoute> routes): Set optimized routes
- SolutionMetrics getMetrics(): Get solution metrics
- void setMetrics(SolutionMetrics metrics): Set solution metrics
- SolverType getSolverType(): Get solver algorithm used
- void setSolverType(SolverType solverType): Set solver algorithm
- Duration getSolutionTime(): Get time taken to solve
- void setSolutionTime(Duration solutionTime): Set solution time
- double getObjectiveValue(): Get objective function value
- void setObjectiveValue(double objectiveValue): Set objective value

## Traffic Integration API

### TrafficIntegration
- TrafficIntegration(): Default constructor
- TrafficIntegration(String name, String description): Constructor with name and description
- TrafficData getCurrentTrafficData(GeographicBounds bounds): Get current traffic data for area
- List<TrafficIncident> getActiveIncidents(GeographicBounds bounds): Get active traffic incidents
- TravelTimeEstimate estimateTravelTime(GeoLocation origin, GeoLocation destination): Estimate travel time
- void subscribeToTrafficUpdates(GeographicBounds bounds, TrafficUpdateCallback callback): Subscribe to traffic updates

### TrafficData
- TrafficData(): Default constructor
- GeographicBounds getBounds(): Get geographic bounds
- void setBounds(GeographicBounds bounds): Set geographic bounds
- List<TrafficSegment> getTrafficSegments(): Get traffic segments
- void setTrafficSegments(List<TrafficSegment> segments): Set traffic segments
- LocalDateTime getDataTimestamp(): Get data timestamp
- void setDataTimestamp(LocalDateTime timestamp): Set data timestamp
- TrafficDataSource getSource(): Get data source
- void setSource(TrafficDataSource source): Set data source

### TrafficSegment
- TrafficSegment(): Default constructor
- String getSegmentId(): Get segment ID
- void setSegmentId(String segmentId): Set segment ID
- GeoLocation getStartPoint(): Get segment start point
- void setStartPoint(GeoLocation startPoint): Set segment start point
- GeoLocation getEndPoint(): Get segment end point
- void setEndPoint(GeoLocation endPoint): Set segment end point
- TrafficFlow getCurrentFlow(): Get current traffic flow
- void setCurrentFlow(TrafficFlow currentFlow): Set current traffic flow
- double getSpeedKmh(): Get current speed in km/h
- void setSpeedKmh(double speedKmh): Set current speed
- TrafficCongestionLevel getCongestionLevel(): Get congestion level
- void setCongestionLevel(TrafficCongestionLevel level): Set congestion level

## REST API Endpoints

### Route Optimization Endpoints
- **POST /api/routes/optimize**: Optimize delivery route
- **GET /api/routes/{routeId}**: Get optimized route details
- **GET /api/routes/driver/{driverId}**: Get routes assigned to driver
- **PUT /api/routes/{routeId}/adjust**: Adjust existing route
- **DELETE /api/routes/{routeId}**: Cancel route optimization

### Route Planning Endpoints
- **POST /api/routes/plan**: Create route plan for deliveries
- **GET /api/routes/plan/{planId}**: Get route plan details
- **POST /api/routes/alternatives**: Generate alternative routes
- **PUT /api/routes/plan/{planId}/update**: Update route plan
- **GET /api/routes/metrics/{routeId}**: Get route performance metrics

### Real-time Adjustment Endpoints
- **POST /api/routes/{routeId}/traffic-adjustment**: Adjust route for traffic
- **POST /api/routes/{routeId}/add-delivery**: Add delivery to existing route
- **DELETE /api/routes/{routeId}/deliveries/{deliveryId}**: Remove delivery from route
- **GET /api/routes/{routeId}/real-time-status**: Get real-time route status
- **POST /api/routes/{routeId}/recalculate**: Force route recalculation

### VRP Solver Endpoints
- **POST /api/vrp/solve**: Solve Vehicle Routing Problem
- **GET /api/vrp/solution/{solutionId}**: Get VRP solution details
- **POST /api/vrp/multi-depot**: Solve multi-depot VRP
- **GET /api/vrp/solutions/fleet/{fleetId}**: Get solutions for fleet
- **POST /api/vrp/validate**: Validate VRP solution

### Traffic Integration Endpoints
- **GET /api/traffic/current**: Get current traffic data
- **GET /api/traffic/incidents**: Get active traffic incidents
- **POST /api/traffic/estimate**: Estimate travel time
- **GET /api/traffic/historical**: Get historical traffic patterns
- **POST /api/traffic/subscribe**: Subscribe to traffic updates

### Fleet Management Endpoints
- **GET /api/fleet/{fleetId}/routes**: Get all routes for fleet
- **POST /api/fleet/{fleetId}/optimize**: Optimize entire fleet routes
- **GET /api/fleet/{fleetId}/performance**: Get fleet performance metrics
- **PUT /api/fleet/{fleetId}/constraints**: Update fleet constraints
- **GET /api/fleet/{fleetId}/utilization**: Get vehicle utilization metrics

### Driver Endpoints
- **GET /api/drivers/{driverId}/routes**: Get driver's assigned routes
- **PUT /api/drivers/{driverId}/preferences**: Update driver preferences
- **GET /api/drivers/{driverId}/performance**: Get driver performance metrics
- **POST /api/drivers/{driverId}/route-feedback**: Submit route feedback
- **GET /api/drivers/{driverId}/work-schedule**: Get driver work schedule

### Analytics Endpoints
- **GET /api/analytics/route-efficiency**: Get route efficiency analytics
- **GET /api/analytics/fuel-consumption**: Get fuel consumption metrics
- **GET /api/analytics/delivery-performance**: Get delivery performance data
- **GET /api/analytics/optimization-trends**: Get optimization trends
- **POST /api/analytics/custom-report**: Generate custom analytics report

## Authentication & Authorization

### Required Headers
```http
Authorization: Bearer <jwt-token>
Content-Type: application/json
X-API-Version: v1
```

### Roles and Permissions
- **DRIVER**: Can view assigned routes, submit feedback, update preferences
- **DISPATCHER**: Can create and modify routes, assign deliveries to routes
- **FLEET_MANAGER**: Can optimize fleet routes, view performance metrics
- **ADMIN**: Full access to all route optimization operations
- **SYSTEM**: Internal service-to-service communication

## Error Handling

### Standard Error Response
```json
{
  "error": {
    "code": "OPTIMIZATION_FAILED",
    "message": "Route optimization failed due to insufficient delivery time windows",
    "timestamp": "2023-10-15T14:30:00Z",
    "path": "/api/routes/optimize"
  }
}
```

### Common Error Codes
- **OPTIMIZATION_FAILED** (500): Route optimization algorithm failed
- **INVALID_DELIVERY_POINTS** (400): Invalid or incomplete delivery point data
- **VEHICLE_NOT_FOUND** (404): Specified vehicle not found
- **DRIVER_NOT_AVAILABLE** (409): Driver not available for assignment
- **TRAFFIC_DATA_UNAVAILABLE** (503): Traffic data service unavailable
- **ROUTE_NOT_FOUND** (404): Route not found
- **INSUFFICIENT_VEHICLE_CAPACITY** (400): Vehicle capacity exceeded
- **TIME_WINDOW_VIOLATION** (400): Delivery time window constraints violated
- **GEOGRAPHIC_BOUNDS_INVALID** (400): Invalid geographic bounds specified
- **VRP_SOLUTION_INFEASIBLE** (422): VRP problem has no feasible solution