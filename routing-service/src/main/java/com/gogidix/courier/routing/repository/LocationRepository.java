package com.gogidix.courier.routing.repository;

import com.gogidix.courier.routing.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Location entities with spatial operations.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, String> {
    
    /**
     * Find locations within a specified radius of a point.
     * Uses PostGIS functions for accurate geospatial calculations.
     *
     * @param latitude The latitude of the center point
     * @param longitude The longitude of the center point
     * @param radiusKm The radius in kilometers
     * @return List of locations within the specified radius
     */
    @Query(value = "SELECT * FROM locations l WHERE " +
            "ST_DWithin(ST_SetSRID(ST_MakePoint(l.longitude, l.latitude), 4326)::geography, " +
            "ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radiusKm * 1000)",
            nativeQuery = true)
    List<Location> findLocationsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm);
    
    /**
     * Find the nearest locations to a point, ordered by proximity.
     *
     * @param latitude The latitude of the reference point
     * @param longitude The longitude of the reference point
     * @param limit The maximum number of results to return
     * @return List of locations ordered by proximity
     */
    @Query(value = "SELECT * FROM locations l " +
            "ORDER BY ST_Distance(ST_SetSRID(ST_MakePoint(l.longitude, l.latitude), 4326)::geography, " +
            "ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography) " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Location> findNearestLocations(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("limit") int limit);
    
    /**
     * Find locations within a bounding box.
     *
     * @param swLat The latitude of the southwest corner
     * @param swLng The longitude of the southwest corner
     * @param neLat The latitude of the northeast corner
     * @param neLng The longitude of the northeast corner
     * @return List of locations within the bounding box
     */
    @Query(value = "SELECT * FROM locations l WHERE " +
            "l.latitude BETWEEN :swLat AND :neLat AND " +
            "l.longitude BETWEEN :swLng AND :neLng",
            nativeQuery = true)
    List<Location> findLocationsWithinBoundary(
            @Param("swLat") double swLat,
            @Param("swLng") double swLng,
            @Param("neLat") double neLat,
            @Param("neLng") double neLng);
    
    /**
     * Calculate the distance between two locations.
     *
     * @param lat1 The latitude of the first location
     * @param lng1 The longitude of the first location
     * @param lat2 The latitude of the second location
     * @param lng2 The longitude of the second location
     * @return The distance in meters
     */
    @Query(value = "SELECT ST_Distance(" +
            "ST_SetSRID(ST_MakePoint(:lng1, :lat1), 4326)::geography, " +
            "ST_SetSRID(ST_MakePoint(:lng2, :lat2), 4326)::geography" +
            ")",
            nativeQuery = true)
    double calculateDistance(
            @Param("lat1") double lat1,
            @Param("lng1") double lng1,
            @Param("lat2") double lat2,
            @Param("lng2") double lng2);
    
    /**
     * Find locations within a polygon specified in WKT format.
     *
     * @param zoneWkt The WKT representation of the zone polygon
     * @return List of locations within the zone
     */
    @Query(value = "SELECT * FROM locations l WHERE " +
            "ST_Within(ST_SetSRID(ST_MakePoint(l.longitude, l.latitude), 4326), " +
            "ST_GeomFromText(:zoneWkt, 4326))",
            nativeQuery = true)
    List<Location> findLocationsInZone(@Param("zoneWkt") String zoneWkt);
    
    /**
     * Find locations by city.
     *
     * @param city The city name
     * @return List of locations in the specified city
     */
    List<Location> findByCity(String city);
    
    /**
     * Find locations by state or province.
     *
     * @param state The state or province name
     * @return List of locations in the specified state
     */
    List<Location> findByState(String state);
    
    /**
     * Find locations by country.
     *
     * @param country The country name
     * @return List of locations in the specified country
     */
    List<Location> findByCountry(String country);
    
    /**
     * Find locations by postal code.
     *
     * @param postalCode The postal code
     * @return List of locations with the specified postal code
     */
    List<Location> findByPostalCode(String postalCode);
    
    /**
     * Find locations by name containing the given string (case insensitive).
     *
     * @param name The name to search for
     * @return List of locations with names containing the search string
     */
    List<Location> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find locations by name containing the given string and city (case insensitive).
     *
     * @param name The name to search for
     * @param city The city name
     * @return List of locations with names containing the search string in the specified city
     */
    List<Location> findByNameContainingIgnoreCaseAndCity(String name, String city);
}
