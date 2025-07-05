package com.gogidix.courier.international.service;

import lombok.extern.slf4j.Slf4j;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.socialecommerceecosystem.international.client.ThirdPartyIntegrationClient;
import com.socialecommerceecosystem.international.model.CustomsDeclaration;
import com.socialecommerceecosystem.international.model.InternationalShipment;
import com.socialecommerceecosystem.international.repository.InternationalShipmentRepository;
import com.socialecommerceecosystem.international.service.impl.InternationalShipmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class InternationalShipmentServiceTest {

    @Mock
    private InternationalShipmentRepository shipmentRepository;

    @Mock
    private CountryRestrictionService countryRestrictionService;

    @Mock
    private TariffRateService tariffRateService;

    @Mock
    private ThirdPartyIntegrationClient integrationClient;

    @InjectMocks
    private InternationalShipmentServiceImpl shipmentService;

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

        // Create test customs declaration
        testCustomsDeclaration = new CustomsDeclaration();
        testCustomsDeclaration.setDeclarationType(CustomsDeclaration.DeclarationType.COMMERCIAL);
        testCustomsDeclaration.setShipmentPurpose(CustomsDeclaration.ShipmentPurpose.SALE);
        testCustomsDeclaration.setDeclaredValue(BigDecimal.valueOf(500.0));
        testCustomsDeclaration.setCurrencyCode("USD");
        
        testShipment.setCustomsDeclaration(testCustomsDeclaration);
    }

    @Test
    @DisplayName("Should create a new shipment")
    void testCreateShipment() {
        // Arrange
        when(countryRestrictionService.hasEmbargo(anyString())).thenReturn(false);
        when(shipmentRepository.save(any(InternationalShipment.class))).thenReturn(testShipment);

        // Act
        InternationalShipment result = shipmentService.createShipment(testShipment);

        // Assert
        assertNotNull(result);
        assertEquals("INT-TEST-12345", result.getReferenceId());
        assertEquals(InternationalShipment.ShipmentStatus.DRAFT, result.getStatus());
        
        verify(shipmentRepository).save(testShipment);
    }

    @Test
    @DisplayName("Should get shipment by reference ID")
    void testGetShipmentByReferenceId() {
        // Arrange
        when(shipmentRepository.findByReferenceId(anyString())).thenReturn(Optional.of(testShipment));

        // Act
        Optional<InternationalShipment> result = shipmentService.getShipmentByReferenceId("INT-TEST-12345");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("INT-TEST-12345", result.get().getReferenceId());
        
        verify(shipmentRepository).findByReferenceId("INT-TEST-12345");
    }

    @Test
    @DisplayName("Should update a shipment")
    void testUpdateShipment() {
        // Arrange
        when(shipmentRepository.findByReferenceId(anyString())).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(InternationalShipment.class))).thenReturn(testShipment);

        InternationalShipment updateRequest = new InternationalShipment();
        updateRequest.setCarrierCode("dhl");
        updateRequest.setServiceLevel(InternationalShipment.ServiceLevel.STANDARD);
        updateRequest.setSpecialHandlingRequired(true);
        updateRequest.setSpecialHandlingInstructions("Handle with care");
        updateRequest.setDutiesPrepaid(false);
        updateRequest.setIncoterms("DDP");

        // Act
        InternationalShipment result = shipmentService.updateShipment("INT-TEST-12345", updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("dhl", result.getCarrierCode());
        assertEquals(InternationalShipment.ServiceLevel.STANDARD, result.getServiceLevel());
        assertTrue(result.isSpecialHandlingRequired());
        assertEquals("Handle with care", result.getSpecialHandlingInstructions());
        assertFalse(result.isDutiesPrepaid());
        assertEquals("DDP", result.getIncoterms());
        
        verify(shipmentRepository).findByReferenceId("INT-TEST-12345");
        verify(shipmentRepository).save(any(InternationalShipment.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent shipment")
    void testUpdateNonExistentShipment() {
        // Arrange
        when(shipmentRepository.findByReferenceId(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            shipmentService.updateShipment("NON-EXISTENT", new InternationalShipment());
        });
        
        verify(shipmentRepository).findByReferenceId("NON-EXISTENT");
        verify(shipmentRepository, never()).save(any(InternationalShipment.class));
    }

    @Test
    @DisplayName("Should throw exception when updating submitted shipment")
    void testUpdateSubmittedShipment() {
        // Arrange
        InternationalShipment submittedShipment = testShipment;
        submittedShipment.setStatus(InternationalShipment.ShipmentStatus.SUBMITTED_TO_CARRIER);
        
        when(shipmentRepository.findByReferenceId(anyString())).thenReturn(Optional.of(submittedShipment));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            shipmentService.updateShipment("INT-TEST-12345", new InternationalShipment());
        });
        
        verify(shipmentRepository).findByReferenceId("INT-TEST-12345");
        verify(shipmentRepository, never()).save(any(InternationalShipment.class));
    }

    @Test
    @DisplayName("Should submit shipment to carrier")
    void testSubmitShipmentToCarrier() {
        // Arrange
        InternationalShipment readyShipment = testShipment;
        readyShipment.setStatus(InternationalShipment.ShipmentStatus.READY_FOR_PROCESSING);
        
        when(shipmentRepository.findByReferenceId(anyString())).thenReturn(Optional.of(readyShipment));
        when(shipmentRepository.save(any(InternationalShipment.class))).thenReturn(readyShipment);
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("shipmentId", "CARRIER-12345");
        responseBody.put("trackingNumber", "TRACK-12345");
        responseBody.put("labelUrl", "https://example.com/label/12345");
        responseBody.put("estimatedDeliveryDate", LocalDateTime.now().plusDays(3).toString());
        
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(integrationClient.createShipment(anyString(), anyMap())).thenReturn(response);

        // Act
        InternationalShipment result = shipmentService.submitShipmentToCarrier("INT-TEST-12345");

        // Assert
        assertNotNull(result);
        assertEquals(InternationalShipment.ShipmentStatus.LABEL_GENERATED, result.getStatus());
        assertEquals("CARRIER-12345", result.getExternalShipmentId());
        assertEquals("TRACK-12345", result.getTrackingNumber());
        assertEquals("https://example.com/label/12345", result.getLabelUrl());
        assertNotNull(result.getSubmittedAt());
        assertNotNull(result.getEstimatedDeliveryDate());
        
        verify(shipmentRepository).findByReferenceId("INT-TEST-12345");
        verify(integrationClient).createShipment(eq("fedex"), anyMap());
        verify(shipmentRepository).save(any(InternationalShipment.class));
    }

    @Test
    @DisplayName("Should throw exception when submitting shipment that is not ready")
    void testSubmitShipmentNotReady() {
        // Arrange
        when(shipmentRepository.findByReferenceId(anyString())).thenReturn(Optional.of(testShipment));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            shipmentService.submitShipmentToCarrier("INT-TEST-12345");
        });
        
        verify(shipmentRepository).findByReferenceId("INT-TEST-12345");
        verify(integrationClient, never()).createShipment(anyString(), anyMap());
        verify(shipmentRepository, never()).save(any(InternationalShipment.class));
    }

    @Test
    @DisplayName("Should update customs declaration")
    void testUpdateCustomsDeclaration() {
        // Arrange
        when(shipmentRepository.findByReferenceId(anyString())).thenReturn(Optional.of(testShipment));
        when(shipmentRepository.save(any(InternationalShipment.class))).thenReturn(testShipment);
        
        CustomsDeclaration updatedDeclaration = new CustomsDeclaration();
        updatedDeclaration.setDeclarationType(CustomsDeclaration.DeclarationType.GIFT);
        updatedDeclaration.setShipmentPurpose(CustomsDeclaration.ShipmentPurpose.PERSONAL);
        updatedDeclaration.setDeclaredValue(BigDecimal.valueOf(100.0));
        updatedDeclaration.setCurrencyCode("CAD");

        // Act
        InternationalShipment result = shipmentService.updateCustomsDeclaration("INT-TEST-12345", updatedDeclaration);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getCustomsDeclaration());
        assertEquals(CustomsDeclaration.DeclarationType.GIFT, result.getCustomsDeclaration().getDeclarationType());
        assertEquals(CustomsDeclaration.ShipmentPurpose.PERSONAL, 
                result.getCustomsDeclaration().getShipmentPurpose());
        assertEquals(BigDecimal.valueOf(100.0), result.getCustomsDeclaration().getDeclaredValue());
        assertEquals("CAD", result.getCustomsDeclaration().getCurrencyCode());
        
        verify(shipmentRepository).findByReferenceId("INT-TEST-12345");
        verify(shipmentRepository).save(any(InternationalShipment.class));
    }

    @Test
    @DisplayName("Should check eligibility for international shipping")
    void testIsEligibleForInternationalShipping() {
        // Arrange
        List<String> categories = Arrays.asList("8517.12", "4202.12");
        
        when(countryRestrictionService.hasEmbargo("CA")).thenReturn(false);
        when(countryRestrictionService.isCategoryRestricted(eq("CA"), anyString())).thenReturn(false);
        when(tariffRateService.isProductRestricted(eq("CA"), anyString())).thenReturn(false);

        // Act
        boolean result = shipmentService.isEligibleForInternationalShipping("US", "CA", categories);

        // Assert
        assertTrue(result);
        verify(countryRestrictionService).hasEmbargo("CA");
        verify(countryRestrictionService, times(2)).isCategoryRestricted(eq("CA"), anyString());
        verify(tariffRateService, times(2)).isProductRestricted(eq("CA"), anyString());
    }

    @Test
    @DisplayName("Should check ineligibility due to embargo")
    void testIsIneligibleDueToEmbargo() {
        // Arrange
        List<String> categories = Arrays.asList("8517.12", "4202.12");
        
        when(countryRestrictionService.hasEmbargo("IR")).thenReturn(true);

        // Act
        boolean result = shipmentService.isEligibleForInternationalShipping("US", "IR", categories);

        // Assert
        assertFalse(result);
        verify(countryRestrictionService).hasEmbargo("IR");
        verify(countryRestrictionService, never()).isCategoryRestricted(anyString(), anyString());
        verify(tariffRateService, never()).isProductRestricted(anyString(), anyString());
    }

    @Test
    @DisplayName("Should check ineligibility due to restricted category")
    void testIsIneligibleDueToRestrictedCategory() {
        // Arrange
        List<String> categories = Arrays.asList("8517.12", "9301.10"); // 9301.10 is for weapons
        
        when(countryRestrictionService.hasEmbargo("CA")).thenReturn(false);
        when(countryRestrictionService.isCategoryRestricted("CA", "8517.12")).thenReturn(false);
        when(countryRestrictionService.isCategoryRestricted("CA", "9301.10")).thenReturn(true);

        // Act
        boolean result = shipmentService.isEligibleForInternationalShipping("US", "CA", categories);

        // Assert
        assertFalse(result);
        verify(countryRestrictionService).hasEmbargo("CA");
        verify(countryRestrictionService, times(2)).isCategoryRestricted(anyString(), anyString());
        verify(tariffRateService, times(1)).isProductRestricted(eq("CA"), eq("8517.12"));
    }
}

