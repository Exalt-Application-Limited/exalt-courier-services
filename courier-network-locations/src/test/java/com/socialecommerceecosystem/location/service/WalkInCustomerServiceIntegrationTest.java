package com.gogidix.courier.location.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.socialecommerceecosystem.location.model.LocationType;
import com.socialecommerceecosystem.location.model.PhysicalLocation;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.repository.PhysicalLocationRepository;
import com.socialecommerceecosystem.location.repository.WalkInCustomerRepository;

/**
 * Integration tests for WalkInCustomerService.
 * These tests verify that the service layer integrates correctly with repositories
 * and other dependent services.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class WalkInCustomerServiceIntegrationTest {

    @Autowired
    private WalkInCustomerService customerService;

    @MockBean
    private WalkInCustomerRepository customerRepository;

    @MockBean
    private PhysicalLocationRepository locationRepository;

    @MockBean
    private NotificationService notificationService;

    private PhysicalLocation testLocation;
    private WalkInCustomer testCustomer;

    @BeforeEach
    void setUp() {
        // Create test location
        testLocation = new PhysicalLocation();
        testLocation.setId(1L);
        testLocation.setName("Test Location");
        testLocation.setLocationType(LocationType.BRANCH_OFFICE);
        testLocation.setAddress("123 Test Street");
        testLocation.setCity("Test City");
        testLocation.setState("Test State");
        testLocation.setCountry("Test Country");
        testLocation.setZipCode("12345");
        testLocation.setContactPhone("123-456-7890");
        testLocation.setActive(true);

        // Create test customer
        testCustomer = new WalkInCustomer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("test@example.com");
        testCustomer.setPhone("555-1234");
        testCustomer.setLocation(testLocation);
    }

    @Test
    @DisplayName("Should retrieve all customers")
    void testGetAllCustomers() {
        // Arrange
        List<WalkInCustomer> expectedCustomers = Arrays.asList(testCustomer);
        when(customerRepository.findAll()).thenReturn(expectedCustomers);

        // Act
        List<WalkInCustomer> results = customerService.getAllCustomers();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Customer", results.get(0).getName());
        
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve customer by ID")
    void testGetCustomerById() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<WalkInCustomer> result = customerService.getCustomerById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Customer", result.get().getName());
        
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create new customer")
    void testCreateCustomer() {
        // Arrange
        WalkInCustomer customerToCreate = new WalkInCustomer();
        customerToCreate.setName("New Customer");
        customerToCreate.setEmail("new@example.com");
        customerToCreate.setPhone("555-5678");
        customerToCreate.setLocation(testLocation);
        
        when(locationRepository.findById(1L)).thenReturn(Optional.of(testLocation));
        when(customerRepository.save(any(WalkInCustomer.class))).thenReturn(customerToCreate);
        doNothing().when(notificationService).sendCustomerNotification(any(WalkInCustomer.class), anyString(), anyString());

        // Act
        WalkInCustomer result = customerService.createCustomer(customerToCreate);

        // Assert
        assertNotNull(result);
        assertEquals("New Customer", result.getName());
        assertEquals("new@example.com", result.getEmail());
        
        verify(customerRepository, times(1)).save(any(WalkInCustomer.class));
        verify(notificationService, times(1)).sendCustomerNotification(any(WalkInCustomer.class), anyString(), anyString());
    }

    @Test
    @DisplayName("Should update customer")
    void testUpdateCustomer() {
        // Arrange
        WalkInCustomer updatedCustomer = new WalkInCustomer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("Updated Customer");
        updatedCustomer.setEmail("updated@example.com");
        updatedCustomer.setPhone("555-8765");
        updatedCustomer.setLocation(testLocation);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(WalkInCustomer.class))).thenReturn(updatedCustomer);

        // Act
        WalkInCustomer result = customerService.updateCustomer(1L, updatedCustomer);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Customer", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(WalkInCustomer.class));
    }

    @Test
    @DisplayName("Should delete customer")
    void testDeleteCustomer() {
        // Arrange
        doNothing().when(customerRepository).deleteById(1L);

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should find customers by location")
    void testFindCustomersByLocation() {
        // Arrange
        List<WalkInCustomer> expectedCustomers = Arrays.asList(testCustomer);
        when(customerRepository.findByLocationId(1L)).thenReturn(expectedCustomers);

        // Act
        List<WalkInCustomer> results = customerService.findCustomersByLocation(1L);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getLocation().getId());
        
        verify(customerRepository, times(1)).findByLocationId(1L);
    }

    @Test
    @DisplayName("Should find customers by email")
    void testFindCustomersByEmail() {
        // Arrange
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<WalkInCustomer> result = customerService.findCustomerByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        
        verify(customerRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should find customers by phone")
    void testFindCustomersByPhone() {
        // Arrange
        when(customerRepository.findByPhone("555-1234")).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<WalkInCustomer> result = customerService.findCustomerByPhone("555-1234");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("555-1234", result.get().getPhone());
        
        verify(customerRepository, times(1)).findByPhone("555-1234");
    }

    @Test
    @DisplayName("Should search customers by name")
    void testSearchCustomersByName() {
        // Arrange
        String searchTerm = "Test";
        List<WalkInCustomer> expectedCustomers = Arrays.asList(testCustomer);
        
        when(customerRepository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(expectedCustomers);

        // Act
        List<WalkInCustomer> results = customerService.searchCustomersByName(searchTerm);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.get(0).getName().contains(searchTerm));
        
        verify(customerRepository, times(1)).findByNameContainingIgnoreCase(searchTerm);
    }
}
