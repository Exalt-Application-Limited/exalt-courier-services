package com.gogidix.courier.routing.service.geo;

import com.gogidix.courier.routing.model.Location;
import com.gogidix.courier.routing.model.Route;
import com.gogidix.courier.routing.model.RouteStatus;
import com.gogidix.courier.routing.model.Waypoint;
import com.gogidix.courier.routing.repository.LocationRepository;
import com.gogidix.courier.routing.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the GeoService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeoServiceImpl implements GeoService {

    private final LocationRepository locationRepository;
    private final RouteRepository routeRepository;
    
    private static final int CIRCLE_POLYGON_POINTS = 32; // Number of points to approximate a circle
    private static final double EARTH_RADIUS_KM = 6371.0; // Earth's radius in kilometers

    @Override
    public List<Location> findLocationsWithinRadius(Location centerLocation, double radiusKm) {
        log.info("Finding locations within {}km of {}", radiusKm, centerLocation.getAddress());
        
        return locationRepository.findLocationsWithinRadius(
                centerLocation.getLatitude(),
                centerLocation.getLongitude(),
                radiusKm
        );
    }

    @Override
    public List<Location> findNearestLocations(Location referenceLocation, int limit) {
        log.info("Finding {} nearest locations to {}", limit, referenceLocation.getAddress());
        
        return locationRepository.findNearestLocations(
                referenceLocation.getLatitude(),
                referenceLocation.getLongitude(),
                limit
        );
    }

    @Override
    public List<Location> findLocationsWithinBoundary(Location southWest, Location northEast) {
        log.info("Finding locations within boundary: SW={}, NE={}", southWest.getAddress(), northEast.getAddress());
        
        return locationRepository.findLocationsWithinBoundary(
                southWest.getLatitude(),
                southWest.getLongitude(),
                northEast.getLatitude(),
                northEast.getLongitude()
        );
    }

    @Override
    public double calculateDistance(Location location1, Location location2) {
        log.debug("Calculating distance between {} and {}", location1.getAddress(), location2.getAddress());
        
        return locationRepository.calculateDistance(
                location1.getLatitude(),
                location1.getLongitude(),
                location2.getLatitude(),
                location2.getLongitude()
        );
    }

    @Override
    public List<Location> findLocationsInZone(String zoneWkt) {
        log.info("Finding locations in zone: {}", zoneWkt);
        
        return locationRepository.findLocationsInZone(zoneWkt);
    }

    @Override
    public String generateCircleWkt(Location centerLocation, double radiusKm) {
        log.debug("Generating WKT circle with radius {}km at {}", 
                radiusKm, centerLocation.getAddress());
        
        double lat = Math.toRadians(centerLocation.getLatitude());
        double lng = Math.toRadians(centerLocation.getLongitude());
        
        // Convert radius from kilometers to radians
        double radius = radiusKm / EARTH_RADIUS_KM;
        
        StringBuilder wkt = new StringBuilder("POLYGON((");
        
        // Generate points approximating a circle
        for (int i = 0; i <= CIRCLE_POLYGON_POINTS; i++) {
            double angle = 2 * Math.PI * i / CIRCLE_POLYGON_POINTS;
            double dx = radius * Math.cos(angle);
            double dy = radius * Math.sin(angle);
            
            // Calculate new point
            double newLat = Math.asin(Math.sin(lat) * Math.cos(radius) + 
                    Math.cos(lat) * Math.sin(radius) * Math.cos(angle));
            double newLng = lng + Math.atan2(Math.sin(angle) * Math.sin(radius) * Math.cos(lat),
                    Math.cos(radius) - Math.sin(lat) * Math.sin(newLat));
            
            // Convert back to degrees and append to WKT
            newLat = Math.toDegrees(newLat);
            newLng = Math.toDegrees(newLng);
            
            wkt.append(newLng).append(" ").append(newLat);
            
            if (i < CIRCLE_POLYGON_POINTS) {
                wkt.append(", ");
            }
        }
        
        wkt.append("))");
        return wkt.toString();
    }

    @Override
    public List<String> findNearestCouriers(Location deliveryLocation, double radiusKm, int limit) {
        log.info("Finding {} nearest couriers within {}km of {}", 
                limit, radiusKm, deliveryLocation.getAddress());
        
        // Find active routes in the system
        List<Route> activeRoutes = routeRepository.findByStatus(RouteStatus.IN_PROGRESS);
        
        // For each route, find the courier's current location based on their latest completed waypoint
        return activeRoutes.stream()
                .filter(route -> !route.getWaypoints().isEmpty())
                .map(route -> {
                    // Get the courier's current location based on the most recently completed waypoint
                    Waypoint latestWaypoint = route.getWaypoints().stream()
                            .sorted(Comparator.comparing(Waypoint::getSequence).reversed())
                            .findFirst()
                            .orElse(null);
                    
                    if (latestWaypoint == null) {
                        return null;
                    }
                    
                    // Calculate distance to delivery location
                    double distance = calculateDistance(
                            latestWaypoint.getLocation(), 
                            deliveryLocation
                    );
                    
                    // Only include couriers within the radius
                    if (distance <= radiusKm * 1000) { // Convert km to meters
                        return new CourierDistance(route.getCourierId(), distance);
                    }
                    
                    return null;
                })
                .filter(courierDistance -> courierDistance != null)
                .sorted(Comparator.comparing(CourierDistance::getDistance))
                .limit(limit)
                .map(CourierDistance::getCourierId)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> createDeliveryZones(Location centerLocation, double maxRadiusKm, int numberOfZones) {
        log.info("Creating {} delivery zones with max radius {}km around {}", 
                numberOfZones, maxRadiusKm, centerLocation.getAddress());
        
        List<String> zones = new ArrayList<>();
        
        // Create concentric zones from the center outward
        double radiusStep = maxRadiusKm / numberOfZones;
        
        for (int i = 1; i <= numberOfZones; i++) {
            double outerRadius = i * radiusStep;
            double innerRadius = (i - 1) * radiusStep;
            
            // For the innermost zone, create a circle
            if (i == 1) {
                zones.add(generateCircleWkt(centerLocation, outerRadius));
            } 
            // For other zones, create rings (difference between outer and inner circles)
            else {
                String outerCircle = generateCircleWkt(centerLocation, outerRadius);
                String innerCircle = generateCircleWkt(centerLocation, innerRadius);
                
                // Use PostGIS "difference" operation in WKT format
                zones.add("ST_Difference(" + outerCircle + ", " + innerCircle + ")");
            }
        }
        
        return zones;
    }
    
    /**
     * Helper class to store courier ID and distance for sorting.
     */
    private static class CourierDistance {
        private final String courierId;
        private final double distance;
        
        public CourierDistance(String courierId, double distance) {
            this.courierId = courierId;
            this.distance = distance;
        }
        
        public String getCourierId() {
            return courierId;
        }
        
        public double getDistance() {
            return distance;
        }
    }
}
