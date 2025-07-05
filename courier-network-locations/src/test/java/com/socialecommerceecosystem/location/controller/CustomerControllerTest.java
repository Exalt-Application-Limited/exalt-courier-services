package com.gogidix.courier.location.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.location.model.WalkInCustomer;
import com.socialecommerceecosystem.location.service.NotificationService;
import com.socialecommerceecosystem.location.service.WalkInCustomerService;

/**
 * Unit tests for the CustomerController class.
 */
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalkInCustomerService customerService;
    
    @MockBean
    private NotificationService notificationService;

    private WalkInCustomer testCustomer;

    @BeforeEach
    void setUp() {
        // Create test customer
        testCustomer = new WalkInCustomer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@example.com");
        testCustomer.setPhone("+15551234567");
        testCustomer.setAddress("456 Customer Ave");
        testCustomer.setCity("Customer City");
        testCustomer.setState("Customer State");
        testCustomer.setZipCode("54321");
        testCustomer.setCountry("Customer Country");
        testCustomer.setEmailNotificationsEnabled(true);
        testCustomer.setSmsNotificationsEnabled(true);
        testCustomer.setActive(true);
    }

    @Test
    @DisplayName("GET /api/customers - Should return all customers")
    void testGetAllCustomers() throws Exception {
        // Arrange
        List<WalkInCustomer> customers = Arrays.asList(testCustomer);
        when(customerService.getAllCustomers()).thenReturn(customers);

        // Act & Assert
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Customer")));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Should return customer by ID")
    void testGetCustomerById() throws Exception {
        // Arrange
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Customer")))
                .andExpect(jsonPath("$.email", is("customer@example.com")));

        verify(customerService, times(1)).getCustomerById(1L);
    }

    @Test
    @DisplayName("GET /api/customers/{id} - Should return 404 if customer not found")
    void testGetCustomerByIdNotFound() throws Exception {
        // Arrange
        when(customerService.getCustomerById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById(999L);
    }

    @Test
    @DisplayName("POST /api/customers - Should create a new customer")
    void testCreateCustomer() throws Exception {
        // Arrange
        when(customerService.createCustomer(any(WalkInCustomer.class))).thenReturn(testCustomer);

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Customer")))
                .andExpect(jsonPath("$.email", is("customer@example.com")));

        verify(customerService, times(1)).createCustomer(any(WalkInCustomer.class));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - Should update an existing customer")
    void testUpdateCustomer() throws Exception {
        // Arrange
        WalkInCustomer updatedCustomer = new WalkInCustomer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("Updated Customer");
        updatedCustomer.setEmail("updated.customer@example.com");
        updatedCustomer.setPhone("+15559876543");
        updatedCustomer.setAddress("789 Updated Ave");
        updatedCustomer.setActive(true);
        
        when(customerService.updateCustomer(eq(1L), any(WalkInCustomer.class))).thenReturn(updatedCustomer);

        // Act & Assert
        mockMvc.perform(put("/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Customer")))
                .andExpect(jsonPath("$.email", is("updated.customer@example.com")));

        verify(customerService, times(1)).updateCustomer(eq(1L), any(WalkInCustomer.class));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} - Should return 404 if customer not found")
    void testUpdateCustomerNotFound() throws Exception {
        // Arrange
        when(customerService.updateCustomer(eq(999L), any(WalkInCustomer.class)))
                .thenThrow(new IllegalArgumentException("Customer not found"));

        // Act & Assert
        mockMvc.perform(put("/api/customers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCustomer)))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).updateCustomer(eq(999L), any(WalkInCustomer.class));
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} - Should delete a customer")
    void testDeleteCustomer() throws Exception {
        // Arrange
        doNothing().when(customerService).deactivateCustomer(1L);
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isOk());

        verify(customerService, times(1)).deactivateCustomer(1L);
    }

    @Test
    @DisplayName("GET /api/customers/phone/{phone} - Should return customer by phone")
    void testGetCustomerByPhone() throws Exception {
        // Arrange
        when(customerService.getCustomerByPhone("+15551234567")).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(get("/api/customers/phone/+15551234567"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Customer")))
                .andExpect(jsonPath("$.phone", is("+15551234567")));

        verify(customerService, times(1)).getCustomerByPhone("+15551234567");
    }

    @Test
    @DisplayName("GET /api/customers/email/{email} - Should return customer by email")
    void testGetCustomerByEmail() throws Exception {
        // Arrange
        when(customerService.getCustomerByEmail("customer@example.com")).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(get("/api/customers/email/customer@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Customer")))
                .andExpect(jsonPath("$.email", is("customer@example.com")));

        verify(customerService, times(1)).getCustomerByEmail("customer@example.com");
    }

    @Test
    @DisplayName("PUT /api/customers/{id}/notification-preferences - Should update notification preferences")
    void testUpdateNotificationPreferences() throws Exception {
        // Arrange
        Map<String, Boolean> preferences = new HashMap<>();
        preferences.put("emailEnabled", false);
        preferences.put("smsEnabled", true);
        
        WalkInCustomer updatedCustomer = new WalkInCustomer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("Test Customer");
        updatedCustomer.setEmail("customer@example.com");
        updatedCustomer.setEmailNotificationsEnabled(false);
        updatedCustomer.setSmsNotificationsEnabled(true);
        
        when(customerService.updateNotificationPreferences(eq(1L), eq(false), eq(true)))
                .thenReturn(updatedCustomer);
        when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));

        // Act & Assert
        mockMvc.perform(put("/api/customers/1/notification-preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferences)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailNotificationsEnabled", is(false)))
                .andExpect(jsonPath("$.smsNotificationsEnabled", is(true)));

        verify(customerService, times(1)).updateNotificationPreferences(1L, false, true);
    }
}
