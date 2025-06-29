package com.exalt.courier.international.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialecommerceecosystem.international.model.CustomsDeclaration;
import com.socialecommerceecosystem.international.model.InternationalShipment;
import com.socialecommerceecosystem.international.service.InternationalShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternationalShipmentController.class)
public class InternationalShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InternationalShipmentService shipmentService;

    private InternationalShipment testShipment;
    private CustomsDeclaration testCustomsDeclaration;

    @BeforeEach
    void setUp() {
        // Create test shipment
        testShipment = new InternationalShipment();
        testShipment.setReferenceId("INT-TEST-12345");
        testShipment.setOriginCountryCode("US");
        testShipment.setDestinationCountryCode("CA");
        testShipment.setCarrierCode("fedex");
        testShipment.setServiceLevel(InternationalShipment.ServiceLevel.EXPRESS);
        testShipment.setStatus(InternationalShipment.ShipmentStatus.DRAFT);
        testShipment.setSpecialHandlingRequired(false);
        testShipment.setDutiesPrepaid(true);
        testShipment.setIncoterms("DAP");
        testShipment.setCreatedAt(LocalDateTime.now());
        testShipment.setUpdatedAt(LocalDateTime.now());

        // Create test customs declaration
        testCustomsDeclaration = new CustomsDeclaration();
        testCustomsDeclaration.setDeclarationType(CustomsDeclaration.DeclarationType.COMMERCIAL);
        testCustomsDeclaration.setShipmentPurpose(CustomsDeclaration.ShipmentPurpose.SALE);
        testCustomsDeclaration.setDeclaredValue(BigDecimal.valueOf(500.0));
        testCustomsDeclaration.setCurrencyCode("USD");
        
        testShipment.setCustomsDeclaration(testCustomsDeclaration);
    }

    @Test
    @DisplayName("POST /api/international/shipments - Create shipment")
    void testCreateShipment() throws Exception {
        // Arrange
        when(shipmentService.createShipment(any(InternationalShipment.class))).thenReturn(testShipment);

        // Act & Assert
        mockMvc.perform(post("/api/international/shipments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testShipment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.referenceId", is("INT-TEST-12345")))
                .andExpect(jsonPath("$.originCountryCode", is("US")))
                .andExpect(jsonPath("$.destinationCountryCode", is("CA")))
                .andExpect(jsonPath("$.carrierCode", is("fedex")))
                .andExpect(jsonPath("$.serviceLevel", is("EXPRESS")))
                .andExpect(jsonPath("$.status", is("DRAFT")));
        
        verify(shipmentService).createShipment(any(InternationalShipment.class));
    }

    @Test
    @DisplayName("GET /api/international/shipments/{referenceId} - Get shipment by reference ID")
    void testGetShipmentByReferenceId() throws Exception {
        // Arrange
        when(shipmentService.getShipmentByReferenceId("INT-TEST-12345")).thenReturn(Optional.of(testShipment));

        // Act & Assert
        mockMvc.perform(get("/api/international/shipments/INT-TEST-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceId", is("INT-TEST-12345")))
                .andExpect(jsonPath("$.originCountryCode", is("US")))
                .andExpect(jsonPath("$.destinationCountryCode", is("CA")));
        
        verify(shipmentService).getShipmentByReferenceId("INT-TEST-12345");
    }

    @Test
    @DisplayName("GET /api/international/shipments/{referenceId} - 404 for non-existent shipment")
    void testGetNonExistentShipment() throws Exception {
        // Arrange
        when(shipmentService.getShipmentByReferenceId("NON-EXISTENT")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/international/shipments/NON-EXISTENT"))
                .andExpect(status().isNotFound());
        
        verify(shipmentService).getShipmentByReferenceId("NON-EXISTENT");
    }

    @Test
    @DisplayName("PUT /api/international/shipments/{referenceId} - Update shipment")
    void testUpdateShipment() throws Exception {
        // Arrange
        InternationalShipment updatedShipment = testShipment;
        updatedShipment.setCarrierCode("dhl");
        updatedShipment.setServiceLevel(InternationalShipment.ServiceLevel.STANDARD);
        
        when(shipmentService.updateShipment(eq("INT-TEST-12345"), any(InternationalShipment.class)))
                .thenReturn(updatedShipment);

        // Act & Assert
        mockMvc.perform(put("/api/international/shipments/INT-TEST-12345")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedShipment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceId", is("INT-TEST-12345")))
                .andExpect(jsonPath("$.carrierCode", is("dhl")))
                .andExpect(jsonPath("$.serviceLevel", is("STANDARD")));
        
        verify(shipmentService).updateShipment(eq("INT-TEST-12345"), any(InternationalShipment.class));
    }

    @Test
    @DisplayName("POST /api/international/shipments/{referenceId}/submit - Submit shipment to carrier")
    void testSubmitShipmentToCarrier() throws Exception {
        // Arrange
        InternationalShipment submittedShipment = testShipment;
        submittedShipment.setStatus(InternationalShipment.ShipmentStatus.LABEL_GENERATED);
        submittedShipment.setExternalShipmentId("CARRIER-12345");
        submittedShipment.setTrackingNumber("TRACK-12345");
        submittedShipment.setLabelUrl("https://example.com/label/12345");
        submittedShipment.setSubmittedAt(LocalDateTime.now());
        
        when(shipmentService.submitShipmentToCarrier("INT-TEST-12345")).thenReturn(submittedShipment);

        // Act & Assert
        mockMvc.perform(post("/api/international/shipments/INT-TEST-12345/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceId", is("INT-TEST-12345")))
                .andExpect(jsonPath("$.status", is("LABEL_GENERATED")))
                .andExpect(jsonPath("$.externalShipmentId", is("CARRIER-12345")))
                .andExpect(jsonPath("$.trackingNumber", is("TRACK-12345")))
                .andExpect(jsonPath("$.labelUrl", is("https://example.com/label/12345")))
                .andExpect(jsonPath("$.submittedAt", notNullValue()));
        
        verify(shipmentService).submitShipmentToCarrier("INT-TEST-12345");
    }

    @Test
    @DisplayName("POST /api/international/shipments/{referenceId}/customs - Update customs declaration")
    void testUpdateCustomsDeclaration() throws Exception {
        // Arrange
        CustomsDeclaration updatedDeclaration = new CustomsDeclaration();
        updatedDeclaration.setDeclarationType(CustomsDeclaration.DeclarationType.GIFT);
        updatedDeclaration.setShipmentPurpose(CustomsDeclaration.ShipmentPurpose.PERSONAL);
        updatedDeclaration.setDeclaredValue(BigDecimal.valueOf(100.0));
        updatedDeclaration.setCurrencyCode("CAD");
        
        InternationalShipment updatedShipment = testShipment;
        updatedShipment.setCustomsDeclaration(updatedDeclaration);
        
        when(shipmentService.updateCustomsDeclaration(eq("INT-TEST-12345"), any(CustomsDeclaration.class)))
                .thenReturn(updatedShipment);

        // Act & Assert
        mockMvc.perform(post("/api/international/shipments/INT-TEST-12345/customs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDeclaration)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customsDeclaration.declarationType", is("GIFT")))
                .andExpect(jsonPath("$.customsDeclaration.shipmentPurpose", is("PERSONAL")))
                .andExpect(jsonPath("$.customsDeclaration.declaredValue", is(100.0)))
                .andExpect(jsonPath("$.customsDeclaration.currencyCode", is("CAD")));
        
        verify(shipmentService).updateCustomsDeclaration(eq("INT-TEST-12345"), any(CustomsDeclaration.class));
    }

    @Test
    @DisplayName("POST /api/international/shipments/check-eligibility - Check eligibility")
    void testCheckEligibility() throws Exception {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("originCountryCode", "US");
        request.put("destinationCountryCode", "CA");
        request.put("categories", Arrays.asList("8517.12", "4202.12"));
        
        when(shipmentService.isEligibleForInternationalShipping(
                eq("US"), eq("CA"), anyList())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/international/shipments/check-eligibility")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eligible", is(true)))
                .andExpect(jsonPath("$.originCountryCode", is("US")))
                .andExpect(jsonPath("$.destinationCountryCode", is("CA")));
        
        verify(shipmentService).isEligibleForInternationalShipping(eq("US"), eq("CA"), anyList());
    }

    @Test
    @DisplayName("POST /api/international/tariffs/calculate - Calculate duties and taxes")
    void testCalculateDutiesAndTaxes() throws Exception {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("destinationCountryCode", "CA");
        request.put("hsCodeList", Arrays.asList("8517.12", "4202.12"));
        request.put("declaredValueList", Arrays.asList(400.0, 100.0));
        request.put("currencyCode", "USD");
        
        Map<String, Double> expectedResult = new HashMap<>();
        expectedResult.put("8517.12", 40.0);
        expectedResult.put("4202.12", 15.0);
        
        when(shipmentService.estimateDutiesAndTaxes(
                eq("CA"), anyList(), anyList(), eq("USD"))).thenReturn(expectedResult);

        // Act & Assert
        mockMvc.perform(post("/api/international/tariffs/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dutiesAndTaxes.8517\\.12", is(40.0)))
                .andExpect(jsonPath("$.dutiesAndTaxes.4202\\.12", is(15.0)))
                .andExpect(jsonPath("$.totalCharges", is(55.0)))
                .andExpect(jsonPath("$.currencyCode", is("USD")));
        
        verify(shipmentService).estimateDutiesAndTaxes(
                eq("CA"), anyList(), anyList(), eq("USD"));
    }
}
