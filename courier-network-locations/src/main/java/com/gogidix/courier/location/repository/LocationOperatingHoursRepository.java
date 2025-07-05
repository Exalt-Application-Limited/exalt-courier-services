package com.gogidix.courier.location.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.socialecommerceecosystem.location.model.LocationOperatingHours;

/**
 * Repository interface for LocationOperatingHours entity.
 * Provides methods for accessing and querying operating hours data.
 */
@Repository
public interface LocationOperatingHoursRepository extends JpaRepository<LocationOperatingHours, Long> {
    
    /**
     * Find operating hours by physical location ID.
     * 
     * @param physicalLocationId the ID of the physical location
     * @return list of operating hours for the specified location
     */
    List<LocationOperatingHours> findByPhysicalLocationId(Long physicalLocationId);
    
    /**
     * Find operating hours by physical location ID and day of week.
     * 
     * @param physicalLocationId the ID of the physical location
     * @param dayOfWeek the day of week to search for
     * @return list of operating hours for the specified location and day
     */
    List<LocationOperatingHours> findByPhysicalLocationIdAndDayOfWeek(Long physicalLocationId, DayOfWeek dayOfWeek);
    
    /**
     * Find operating hours by physical location ID for locations that are open on a specific day.
     * 
     * @param physicalLocationId the ID of the physical location
     * @param dayOfWeek the day of week to search for
     * @return list of operating hours for the specified location and day where the location is open
     */
    List<LocationOperatingHours> findByPhysicalLocationIdAndDayOfWeekAndIsClosedFalse(Long physicalLocationId, DayOfWeek dayOfWeek);
    
    /**
     * Find operating hours with special hours.
     * 
     * @param specialHours whether to find special hours
     * @return list of operating hours with special hours flag matching the parameter
     */
    List<LocationOperatingHours> findBySpecialHours(boolean specialHours);
    
    /**
     * Check if a location is open at a specific time on a specific day.
     * 
     * @param physicalLocationId the ID of the physical location
     * @param dayOfWeek the day of week to check
     * @param time the time to check
     * @return true if the location is open, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM LocationOperatingHours h " +
           "WHERE h.physicalLocation.id = :locationId AND h.dayOfWeek = :dayOfWeek AND " +
           "h.isClosed = false AND :time >= h.openingTime AND :time <= h.closingTime")
    boolean isLocationOpenAt(
            @Param("locationId") Long physicalLocationId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);
    
    /**
     * Find all locations open on a specific day at a specific time.
     * 
     * @param dayOfWeek the day of week to check
     * @param time the time to check
     * @return list of operating hours for locations open at the specified time on the specified day
     */
    @Query("SELECT h FROM LocationOperatingHours h " +
           "WHERE h.dayOfWeek = :dayOfWeek AND h.isClosed = false AND " +
           ":time >= h.openingTime AND :time <= h.closingTime")
    List<LocationOperatingHours> findLocationsOpenAt(
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("time") LocalTime time);
    
    /**
     * Find operating hours for locations that are open all days of the week.
     * 
     * @return list of location IDs that are open all days of the week
     */
    @Query("SELECT h.physicalLocation.id FROM LocationOperatingHours h " +
           "WHERE h.isClosed = false " +
           "GROUP BY h.physicalLocation.id " +
           "HAVING COUNT(DISTINCT h.dayOfWeek) = 7")
    List<Long> findLocationIdsOpenAllWeek();
    
    /**
     * Find operating hours for locations with late hours (closing after 8 PM).
     * 
     * @return list of operating hours for locations with late hours
     */
    @Query("SELECT h FROM LocationOperatingHours h " +
           "WHERE h.isClosed = false AND h.closingTime > '20:00:00'")
    List<LocationOperatingHours> findLocationsWithLateHours();
    
    /**
     * Delete all operating hours by physical location ID.
     * 
     * @param physicalLocationId the ID of the physical location
     */
    void deleteByPhysicalLocationId(Long physicalLocationId);
}
