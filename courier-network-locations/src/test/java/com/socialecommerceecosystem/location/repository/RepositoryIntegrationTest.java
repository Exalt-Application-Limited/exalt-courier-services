package com.gogidix.courier.location.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.socialecommerceecosystem.location.model.LocationOperatingHours;
import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;

/**
 * Integration tests for the repository layer.
 */
@DataJpaTest
@ActiveProfiles("test")
public class RepositoryIntegrationTest {

    @Autowired
    private PhysicalLocationRepository locationRepository;

    @Autowired
    private LocationOperatingHoursRepository operatingHoursRepository;

    @Test
    @DisplayName("Should save and retrieve a location")
    void testSaveAndRetrieveLocation() {
        // Arrange
        PhysicalLocation location = new PhysicalLocation();
        location.setName("Test Location");
        location.setLocationType(LocationType.BRANCH_OFFICE);
        location.setAddress("123 Test Street");
        location.setCity("Test City");
        location.setState("Test State");
        location.setCountry("Test Country");
        location.setZipCode("12345");
        location.setContactPhone("123-456-7890");
        location.setActive(true);
        
        // Act
        PhysicalLocation savedLocation = locationRepository.save(location);
        Optional<PhysicalLocation> retrievedLocationOpt = locationRepository.findById(savedLocation.getId());
        
        // Assert
        assertTrue(retrievedLocationOpt.isPresent());
        PhysicalLocation retrievedLocation = retrievedLocationOpt.get();
        assertEquals("Test Location", retrievedLocation.getName());
        assertEquals(LocationType.BRANCH_OFFICE, retrievedLocation.getLocationType());
        assertEquals("Test City", retrievedLocation.getCity());
    }

    @Test
    @DisplayName("Should find locations by type")
    void testFindLocationsByType() {
        // Arrange
        PhysicalLocation branchOffice = new PhysicalLocation();
        branchOffice.setName("Branch Office");
        branchOffice.setLocationType(LocationType.BRANCH_OFFICE);
        branchOffice.setAddress("123 Branch St");
        branchOffice.setCity("City A");
        branchOffice.setState("State A");
        branchOffice.setCountry("Country A");
        branchOffice.setZipCode("12345");
        branchOffice.setContactPhone("123-456-7890");
        branchOffice.setActive(true);
        
        PhysicalLocation sortingCenter = new PhysicalLocation();
        sortingCenter.setName("Sorting Center");
        sortingCenter.setLocationType(LocationType.SORTING_CENTER);
        sortingCenter.setAddress("456 Sorting St");
        sortingCenter.setCity("City B");
        sortingCenter.setState("State B");
        sortingCenter.setCountry("Country B");
        sortingCenter.setZipCode("67890");
        sortingCenter.setContactPhone("987-654-3210");
        sortingCenter.setActive(true);
        
        locationRepository.save(branchOffice);
        locationRepository.save(sortingCenter);
        
        // Act
        List<PhysicalLocation> branchOffices = locationRepository.findByLocationType(LocationType.BRANCH_OFFICE);
        List<PhysicalLocation> sortingCenters = locationRepository.findByLocationType(LocationType.SORTING_CENTER);
        
        // Assert
        assertEquals(1, branchOffices.size());
        assertEquals("Branch Office", branchOffices.get(0).getName());
        
        assertEquals(1, sortingCenters.size());
        assertEquals("Sorting Center", sortingCenters.get(0).getName());
    }

    @Test
    @DisplayName("Should find locations by country and state")
    void testFindLocationsByCountryAndState() {
        // Arrange
        PhysicalLocation location1 = new PhysicalLocation();
        location1.setName("Location 1");
        location1.setLocationType(LocationType.BRANCH_OFFICE);
        location1.setAddress("123 Any St");
        location1.setCity("City1");
        location1.setState("State1");
        location1.setCountry("Country1");
        location1.setZipCode("12345");
        location1.setContactPhone("123-456-7890");
        location1.setActive(true);
        
        PhysicalLocation location2 = new PhysicalLocation();
        location2.setName("Location 2");
        location2.setLocationType(LocationType.BRANCH_OFFICE);
        location2.setAddress("456 Any St");
        location2.setCity("City2");
        location2.setState("State1");
        location2.setCountry("Country1");
        location2.setZipCode("67890");
        location2.setContactPhone("987-654-3210");
        location2.setActive(true);
        
        locationRepository.save(location1);
        locationRepository.save(location2);
        
        // Act
        List<PhysicalLocation> locations = locationRepository.findByCountryAndState("Country1", "State1");
        
        // Assert
        assertEquals(2, locations.size());
    }

    @Test
    @DisplayName("Should find locations by city")
    void testFindLocationsByCity() {
        // Arrange
        PhysicalLocation location1 = new PhysicalLocation();
        location1.setName("Location 1");
        location1.setLocationType(LocationType.BRANCH_OFFICE);
        location1.setAddress("123 Any St");
        location1.setCity("TestCity");
        location1.setState("State1");
        location1.setCountry("Country1");
        location1.setZipCode("12345");
        location1.setContactPhone("123-456-7890");
        location1.setActive(true);
        
        PhysicalLocation location2 = new PhysicalLocation();
        location2.setName("Location 2");
        location2.setLocationType(LocationType.BRANCH_OFFICE);
        location2.setAddress("456 Any St");
        location2.setCity("OtherCity");
        location2.setState("State1");
        location2.setCountry("Country1");
        location2.setZipCode("67890");
        location2.setContactPhone("987-654-3210");
        location2.setActive(true);
        
        locationRepository.save(location1);
        locationRepository.save(location2);
        
        // Act
        List<PhysicalLocation> locations = locationRepository.findByCity("TestCity");
        
        // Assert
        assertEquals(1, locations.size());
        assertEquals("Location 1", locations.get(0).getName());
    }

    @Test
    @DisplayName("Should save and retrieve operating hours")
    void testSaveAndRetrieveOperatingHours() {
        // Arrange
        PhysicalLocation location = new PhysicalLocation();
        location.setName("Test Location");
        location.setLocationType(LocationType.BRANCH_OFFICE);
        location.setAddress("123 Test Street");
        location.setCity("Test City");
        location.setState("Test State");
        location.setCountry("Test Country");
        location.setZipCode("12345");
        location.setContactPhone("123-456-7890");
        location.setActive(true);
        
        PhysicalLocation savedLocation = locationRepository.save(location);
        
        LocationOperatingHours operatingHours = new LocationOperatingHours();
        operatingHours.setDayOfWeek(DayOfWeek.MONDAY);
        operatingHours.setOpenTime(LocalTime.of(9, 0));
        operatingHours.setCloseTime(LocalTime.of(17, 0));
        operatingHours.setPhysicalLocation(savedLocation);
        
        // Act
        LocationOperatingHours savedHours = operatingHoursRepository.save(operatingHours);
        Optional<LocationOperatingHours> retrievedHoursOpt = operatingHoursRepository.findById(savedHours.getId());
        
        // Assert
        assertTrue(retrievedHoursOpt.isPresent());
        LocationOperatingHours retrievedHours = retrievedHoursOpt.get();
        assertEquals(DayOfWeek.MONDAY, retrievedHours.getDayOfWeek());
        assertEquals(LocalTime.of(9, 0), retrievedHours.getOpenTime());
        assertEquals(LocalTime.of(17, 0), retrievedHours.getCloseTime());
        assertEquals(savedLocation.getId(), retrievedHours.getPhysicalLocation().getId());
    }

    @Test
    @DisplayName("Should find operating hours by location and day of week")
    void testFindOperatingHoursByLocationAndDayOfWeek() {
        // Arrange
        PhysicalLocation location = new PhysicalLocation();
        location.setName("Test Location");
        location.setLocationType(LocationType.BRANCH_OFFICE);
        location.setAddress("123 Test Street");
        location.setCity("Test City");
        location.setState("Test State");
        location.setCountry("Test Country");
        location.setZipCode("12345");
        location.setContactPhone("123-456-7890");
        location.setActive(true);
        
        PhysicalLocation savedLocation = locationRepository.save(location);
        
        LocationOperatingHours mondayHours = new LocationOperatingHours();
        mondayHours.setDayOfWeek(DayOfWeek.MONDAY);
        mondayHours.setOpenTime(LocalTime.of(9, 0));
        mondayHours.setCloseTime(LocalTime.of(17, 0));
        mondayHours.setPhysicalLocation(savedLocation);
        
        LocationOperatingHours tuesdayHours = new LocationOperatingHours();
        tuesdayHours.setDayOfWeek(DayOfWeek.TUESDAY);
        tuesdayHours.setOpenTime(LocalTime.of(9, 0));
        tuesdayHours.setCloseTime(LocalTime.of(17, 0));
        tuesdayHours.setPhysicalLocation(savedLocation);
        
        operatingHoursRepository.save(mondayHours);
        operatingHoursRepository.save(tuesdayHours);
        
        // Act
        Optional<LocationOperatingHours> mondayHoursOpt = operatingHoursRepository
                .findByPhysicalLocationIdAndDayOfWeek(savedLocation.getId(), DayOfWeek.MONDAY);
        
        // Assert
        assertTrue(mondayHoursOpt.isPresent());
        assertEquals(DayOfWeek.MONDAY, mondayHoursOpt.get().getDayOfWeek());
    }

    @Test
    @DisplayName("Should find active locations")
    void testFindActiveLocations() {
        // Arrange
        PhysicalLocation activeLocation = new PhysicalLocation();
        activeLocation.setName("Active Location");
        activeLocation.setLocationType(LocationType.BRANCH_OFFICE);
        activeLocation.setAddress("123 Active St");
        activeLocation.setCity("City A");
        activeLocation.setState("State A");
        activeLocation.setCountry("Country A");
        activeLocation.setZipCode("12345");
        activeLocation.setContactPhone("123-456-7890");
        activeLocation.setActive(true);
        
        PhysicalLocation inactiveLocation = new PhysicalLocation();
        inactiveLocation.setName("Inactive Location");
        inactiveLocation.setLocationType(LocationType.BRANCH_OFFICE);
        inactiveLocation.setAddress("456 Inactive St");
        inactiveLocation.setCity("City B");
        inactiveLocation.setState("State B");
        inactiveLocation.setCountry("Country B");
        inactiveLocation.setZipCode("67890");
        inactiveLocation.setContactPhone("987-654-3210");
        inactiveLocation.setActive(false);
        
        locationRepository.save(activeLocation);
        locationRepository.save(inactiveLocation);
        
        // Act
        List<PhysicalLocation> activeLocations = locationRepository.findByActiveTrue();
        
        // Assert
        assertEquals(1, activeLocations.size());
        assertEquals("Active Location", activeLocations.get(0).getName());
    }
}
