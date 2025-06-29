package com.exalt.courier.international.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.exalt.courier.international.client.ThirdPartyIntegrationClient;
import com.exalt.courier.international.model.CustomsDeclaration;
import com.exalt.courier.international.model.InternationalShipment;
import com.exalt.courier.international.repository.InternationalShipmentRepository;
import com.exalt.courier.international.service.CountryRestrictionService;
import com.exalt.courier.international.service.InternationalShipmentService;
import com.exalt.courier.international.service.TariffRateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of the InternationalShipmentService interface.
 * Provides functionality for managing international shipments.
 */
@Service
@Slf4j
public class InternationalShipmentServiceImpl implements InternationalShipmentService {

    private final InternationalShipmentRepository shipmentRepository;
    private final CountryRestrictionService countryRestrictionService;
    private final TariffRateService tariffRateService;
    private final ThirdPartyIntegrationClient integrationClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public InternationalShipmentServiceImpl(
            InternationalShipmentRepository shipmentRepository,
            CountryRestrictionService countryRestrictionService,
            TariffRateService tariffRateService,
            ThirdPartyIntegrationClient integrationClient,
            ObjectMapper objectMapper) {
        this.shipmentRepository = shipmentRepository;
        this.countryRestrictionService = countryRestrictionService;
        this.tariffRateService = tariffRateService;
        this.integrationClient = integrationClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public InternationalShipment createShipment(InternationalShipment shipment) {
        log.info("Creating international shipment with origin: {} and destination: {}", 
                shipment.getOriginCountryCode(), shipment.getDestinationCountryCode());
        
        // Validate eligibility for international shipping
        if (shipment.getCustomsDeclaration() != null && shipment.getCustomsDeclaration().getItems() != null) {
            List<String> categories = new ArrayList<>();
            shipment.getCustomsDeclaration().getItems().forEach(item -> {
                if (item.getHsCode() != null) {
                    categories.add(item.getHsCode());
                }
            });
            
            if (!categories.isEmpty() && !isEligibleForInternationalShipping(
                    shipment.getOriginCountryCode(), 
                    shipment.getDestinationCountryCode(), 
                    categories)) {
                throw new IllegalArgumentException("Shipment contains items that are restricted for the destination country");
            }
        }
        
        // Generate a unique reference ID if not provided
        if (shipment.getReferenceId() == null || shipment.getReferenceId().isEmpty()) {
            shipment.setReferenceId("INT-" + UUID.randomUUID().toString());
        }
        
        // Set initial status if not set
        if (shipment.getStatus() == null) {
            shipment.setStatus(InternationalShipment.ShipmentStatus.DRAFT);
        }
        
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public InternationalShipment updateShipment(String referenceId, InternationalShipment shipment) {
        log.info("Updating international shipment: {}", referenceId);
        
        InternationalShipment existingShipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Don't allow updates for shipments that are already submitted to carrier
        if (existingShipment.getStatus().ordinal() >= InternationalShipment.ShipmentStatus.SUBMITTED_TO_CARRIER.ordinal()) {
            throw new IllegalStateException("Cannot update shipment that is already submitted to the carrier");
        }
        
        // Update the mutable fields
        existingShipment.setCarrierCode(shipment.getCarrierCode());
        existingShipment.setServiceLevel(shipment.getServiceLevel());
        existingShipment.setSpecialHandlingRequired(shipment.isSpecialHandlingRequired());
        existingShipment.setSpecialHandlingInstructions(shipment.getSpecialHandlingInstructions());
        existingShipment.setDutiesPrepaid(shipment.isDutiesPrepaid());
        existingShipment.setIncoterms(shipment.getIncoterms());
        
        // Update customs declaration if provided
        if (shipment.getCustomsDeclaration() != null) {
            existingShipment.setCustomsDeclaration(shipment.getCustomsDeclaration());
        }
        
        return shipmentRepository.save(existingShipment);
    }

    @Override
    public Optional<InternationalShipment> getShipmentByReferenceId(String referenceId) {
        log.info("Getting international shipment by reference ID: {}", referenceId);
        return shipmentRepository.findByReferenceId(referenceId);
    }

    @Override
    public Optional<InternationalShipment> getShipmentByTrackingNumber(String trackingNumber) {
        log.info("Getting international shipment by tracking number: {}", trackingNumber);
        return shipmentRepository.findByTrackingNumber(trackingNumber);
    }

    @Override
    public List<InternationalShipment> getShipmentsByStatus(InternationalShipment.ShipmentStatus status) {
        log.info("Getting international shipments by status: {}", status);
        return shipmentRepository.findByStatus(status);
    }

    @Override
    public List<InternationalShipment> getShipmentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Getting international shipments between {} and {}", startDate, endDate);
        return shipmentRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<InternationalShipment> getShipmentsByDestinationCountry(String countryCode) {
        log.info("Getting international shipments to country: {}", countryCode);
        return shipmentRepository.findByDestinationCountryCode(countryCode);
    }

    @Override
    @Transactional
    public InternationalShipment submitShipmentToCarrier(String referenceId) {
        log.info("Submitting international shipment to carrier: {}", referenceId);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Check if the shipment is in a valid state for submission
        if (shipment.getStatus() != InternationalShipment.ShipmentStatus.READY_FOR_PROCESSING) {
            throw new IllegalStateException("Shipment is not ready for processing");
        }
        
        // Prepare the shipment request for the third-party integration service
        Map<String, Object> shipmentRequest = new HashMap<>();
        shipmentRequest.put("originCountryCode", shipment.getOriginCountryCode());
        shipmentRequest.put("destinationCountryCode", shipment.getDestinationCountryCode());
        shipmentRequest.put("serviceLevel", shipment.getServiceLevel().toString());
        
        // Add customs declaration if available
        if (shipment.getCustomsDeclaration() != null) {
            Map<String, Object> customsInfo = new HashMap<>();
            customsInfo.put("declarationType", shipment.getCustomsDeclaration().getDeclarationType().toString());
            customsInfo.put("shipmentPurpose", shipment.getCustomsDeclaration().getShipmentPurpose().toString());
            customsInfo.put("declaredValue", shipment.getCustomsDeclaration().getDeclaredValue());
            customsInfo.put("currencyCode", shipment.getCustomsDeclaration().getCurrencyCode());
            
            shipmentRequest.put("customsInfo", customsInfo);
        }
        
        try {
            // Submit the shipment to the carrier through the integration service
            ResponseEntity<Map<String, Object>> response = integrationClient.createShipment(
                    shipment.getCarrierCode(), shipmentRequest);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // Update the shipment with the carrier response
                shipment.setExternalShipmentId((String) responseBody.get("shipmentId"));
                shipment.setTrackingNumber((String) responseBody.get("trackingNumber"));
                shipment.setStatus(InternationalShipment.ShipmentStatus.LABEL_GENERATED);
                shipment.setLabelUrl((String) responseBody.get("labelUrl"));
                shipment.setSubmittedAt(LocalDateTime.now());
                
                if (responseBody.containsKey("estimatedDeliveryDate")) {
                    shipment.setEstimatedDeliveryDate(
                            LocalDateTime.parse((String) responseBody.get("estimatedDeliveryDate")));
                }
                
                return shipmentRepository.save(shipment);
            } else {
                shipment.setStatus(InternationalShipment.ShipmentStatus.ERROR);
                shipment.setErrorMessage("Failed to submit shipment to carrier: " + 
                        (response.getBody() != null ? response.getBody().toString() : "Unknown error"));
                return shipmentRepository.save(shipment);
            }
        } catch (Exception e) {
            log.error("Error submitting shipment to carrier", e);
            shipment.setStatus(InternationalShipment.ShipmentStatus.ERROR);
            shipment.setErrorMessage("Error submitting shipment to carrier: " + e.getMessage());
            return shipmentRepository.save(shipment);
        }
    }

    @Override
    @Transactional
    public InternationalShipment cancelShipment(String referenceId) {
        log.info("Cancelling international shipment: {}", referenceId);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Only submitted shipments can be cancelled
        if (shipment.getStatus().ordinal() < InternationalShipment.ShipmentStatus.SUBMITTED_TO_CARRIER.ordinal()) {
            shipment.setStatus(InternationalShipment.ShipmentStatus.CANCELLED);
            return shipmentRepository.save(shipment);
        }
        
        // If the shipment is already with the carrier, try to cancel it through the integration service
        if (shipment.getCarrierCode() != null && shipment.getTrackingNumber() != null) {
            try {
                ResponseEntity<Map<String, Object>> response = integrationClient.cancelShipment(
                        shipment.getCarrierCode(), shipment.getTrackingNumber());
                
                if (response.getStatusCode().is2xxSuccessful()) {
                    shipment.setStatus(InternationalShipment.ShipmentStatus.CANCELLED);
                    return shipmentRepository.save(shipment);
                } else {
                    throw new IllegalStateException("Failed to cancel shipment with carrier: " + 
                            (response.getBody() != null ? response.getBody().toString() : "Unknown error"));
                }
            } catch (Exception e) {
                log.error("Error cancelling shipment with carrier", e);
                throw new IllegalStateException("Error cancelling shipment with carrier: " + e.getMessage());
            }
        } else {
            throw new IllegalStateException("Cannot cancel shipment: missing carrier code or tracking number");
        }
    }

    @Override
    @Transactional
    public InternationalShipment updateCustomsDeclaration(String referenceId, CustomsDeclaration customsDeclaration) {
        log.info("Updating customs declaration for shipment: {}", referenceId);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Don't allow updates for shipments that are already submitted to carrier
        if (shipment.getStatus().ordinal() >= InternationalShipment.ShipmentStatus.SUBMITTED_TO_CARRIER.ordinal()) {
            throw new IllegalStateException("Cannot update customs declaration for a shipment that is already submitted to the carrier");
        }
        
        shipment.setCustomsDeclaration(customsDeclaration);
        
        // If the shipment was awaiting customs details, update the status
        if (shipment.getStatus() == InternationalShipment.ShipmentStatus.AWAITING_CUSTOMS_DETAILS) {
            shipment.setStatus(InternationalShipment.ShipmentStatus.CUSTOMS_DETAILS_SUBMITTED);
        }
        
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public InternationalShipment submitForComplianceApproval(String referenceId) {
        log.info("Submitting shipment for compliance approval: {}", referenceId);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Check if the shipment has customs details
        if (shipment.getCustomsDeclaration() == null) {
            throw new IllegalStateException("Cannot submit for compliance approval: missing customs declaration");
        }
        
        shipment.setStatus(InternationalShipment.ShipmentStatus.AWAITING_COMPLIANCE_APPROVAL);
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public InternationalShipment approveShipmentCompliance(String referenceId, String approvedBy) {
        log.info("Approving compliance for shipment: {} by {}", referenceId, approvedBy);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Check if the shipment is awaiting approval
        if (shipment.getStatus() != InternationalShipment.ShipmentStatus.AWAITING_COMPLIANCE_APPROVAL) {
            throw new IllegalStateException("Shipment is not awaiting compliance approval");
        }
        
        shipment.setComplianceApproved(true);
        shipment.setComplianceApprovedBy(approvedBy);
        shipment.setStatus(InternationalShipment.ShipmentStatus.READY_FOR_PROCESSING);
        
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public InternationalShipment rejectShipmentCompliance(String referenceId, String rejectionReason) {
        log.info("Rejecting compliance for shipment: {} reason: {}", referenceId, rejectionReason);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Check if the shipment is awaiting approval
        if (shipment.getStatus() != InternationalShipment.ShipmentStatus.AWAITING_COMPLIANCE_APPROVAL) {
            throw new IllegalStateException("Shipment is not awaiting compliance approval");
        }
        
        shipment.setComplianceApproved(false);
        shipment.setErrorMessage(rejectionReason);
        shipment.setStatus(InternationalShipment.ShipmentStatus.COMPLIANCE_REJECTED);
        
        return shipmentRepository.save(shipment);
    }

    @Override
    @Transactional
    public InternationalShipment generateShippingDocuments(String referenceId) {
        log.info("Generating shipping documents for shipment: {}", referenceId);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Check if the shipment has a tracking number (has been submitted to the carrier)
        if (shipment.getTrackingNumber() == null || shipment.getExternalShipmentId() == null) {
            throw new IllegalStateException("Cannot generate documents: shipment has not been submitted to the carrier");
        }
        
        try {
            // Generate the label through the integration service
            ResponseEntity<Map<String, Object>> labelResponse = integrationClient.generateLabel(
                    shipment.getCarrierCode(), shipment.getExternalShipmentId());
            
            if (labelResponse.getStatusCode().is2xxSuccessful() && labelResponse.getBody() != null) {
                shipment.setLabelUrl((String) labelResponse.getBody().get("labelUrl"));
            }
            
            // Generate customs documents if needed
            if (shipment.getCustomsDeclaration() != null) {
                ResponseEntity<Map<String, Object>> customsResponse = integrationClient.generateCustomsDocument(
                        shipment.getCarrierCode(), 
                        shipment.getExternalShipmentId(),
                        shipment.getCustomsDeclaration().getDeclarationType().toString());
                
                if (customsResponse.getStatusCode().is2xxSuccessful() && customsResponse.getBody() != null) {
                    shipment.setDocumentsUrl((String) customsResponse.getBody().get("documentUrl"));
                }
            }
            
            return shipmentRepository.save(shipment);
        } catch (Exception e) {
            log.error("Error generating shipping documents", e);
            throw new IllegalStateException("Error generating shipping documents: " + e.getMessage());
        }
    }

    @Override
    public List<InternationalShipment> getShipmentsNeedingAttention() {
        log.info("Getting shipments needing attention");
        return shipmentRepository.findShipmentsNeedingAttention();
    }

    @Override
    public List<InternationalShipment> getShipmentsHeldByCustoms() {
        log.info("Getting shipments held by customs");
        return shipmentRepository.findByStatus(InternationalShipment.ShipmentStatus.HELD_BY_CUSTOMS);
    }

    @Override
    @Transactional
    public InternationalShipment updateTrackingInformation(String referenceId) {
        log.info("Updating tracking information for shipment: {}", referenceId);
        
        InternationalShipment shipment = shipmentRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found with reference ID: " + referenceId));
        
        // Check if the shipment has a tracking number
        if (shipment.getTrackingNumber() == null) {
            throw new IllegalStateException("Cannot update tracking: shipment has no tracking number");
        }
        
        try {
            // Create tracking request
            Map<String, Object> trackingRequest = new HashMap<>();
            trackingRequest.put("trackingNumber", shipment.getTrackingNumber());
            
            // Get tracking information from carrier through the integration service
            ResponseEntity<Map<String, Object>> response = integrationClient.trackShipment(
                    shipment.getCarrierCode(), trackingRequest);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // Update the shipment status based on the tracking response
                String status = (String) responseBody.get("status");
                if (status != null) {
                    switch (status.toUpperCase()) {
                        case "DELIVERED":
                            shipment.setStatus(InternationalShipment.ShipmentStatus.DELIVERED);
                            break;
                        case "IN_TRANSIT":
                            shipment.setStatus(InternationalShipment.ShipmentStatus.IN_TRANSIT);
                            break;
                        case "HELD_BY_CUSTOMS":
                            shipment.setStatus(InternationalShipment.ShipmentStatus.HELD_BY_CUSTOMS);
                            break;
                        case "CUSTOMS_CLEARED":
                            shipment.setStatus(InternationalShipment.ShipmentStatus.CUSTOMS_CLEARED);
                            break;
                        case "OUT_FOR_DELIVERY":
                            shipment.setStatus(InternationalShipment.ShipmentStatus.OUT_FOR_DELIVERY);
                            break;
                        case "RETURNED":
                            shipment.setStatus(InternationalShipment.ShipmentStatus.RETURNED);
                            break;
                    }
                }
                
                // Update estimated delivery date if available
                if (responseBody.containsKey("estimatedDeliveryDate")) {
                    shipment.setEstimatedDeliveryDate(
                            LocalDateTime.parse((String) responseBody.get("estimatedDeliveryDate")));
                }
                
                return shipmentRepository.save(shipment);
            } else {
                log.warn("Failed to update tracking information: {}", 
                        response.getBody() != null ? response.getBody().toString() : "Unknown error");
                return shipment;
            }
        } catch (Exception e) {
            log.error("Error updating tracking information", e);
            throw new IllegalStateException("Error updating tracking information: " + e.getMessage());
        }
    }

    @Override
    public boolean isEligibleForInternationalShipping(
            String originCountry, String destinationCountry, List<String> categoryList) {
        log.info("Checking eligibility for international shipping from {} to {}", originCountry, destinationCountry);
        
        // Check if the destination country has an embargo
        if (countryRestrictionService.hasEmbargo(destinationCountry)) {
            log.info("Destination country {} has an embargo, shipping not allowed", destinationCountry);
            return false;
        }
        
        // Check if any of the categories are restricted for the destination country
        for (String category : categoryList) {
            if (countryRestrictionService.isCategoryRestricted(destinationCountry, category)) {
                log.info("Category {} is restricted for destination country {}", category, destinationCountry);
                return false;
            }
            
            // Check if the product is restricted based on tariff information
            if (tariffRateService.isProductRestricted(destinationCountry, category)) {
                log.info("Product with HS code {} is restricted for destination country {}", category, destinationCountry);
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Map<String, Double> estimateDutiesAndTaxes(
            String destinationCountry, List<String> hsCodeList, 
            List<Double> declaredValueList, String currencyCode) {
        log.info("Estimating duties and taxes for shipment to {}", destinationCountry);
        
        // Validate input
        if (hsCodeList.size() != declaredValueList.size()) {
            throw new IllegalArgumentException("Number of HS codes must match number of declared values");
        }
        
        // Build map of HS codes to declared values
        Map<String, Double> items = new HashMap<>();
        for (int i = 0; i < hsCodeList.size(); i++) {
            items.put(hsCodeList.get(i), declaredValueList.get(i));
        }
        
        // Calculate duties and taxes using the tariff service
        Map<String, Map<String, Double>> calculations = tariffRateService.calculateDutiesAndTaxes(
                destinationCountry, items, currencyCode);
        
        // Extract the total charges for each HS code
        Map<String, Double> totalCharges = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : calculations.entrySet()) {
            if (entry.getValue().containsKey("totalCharges")) {
                totalCharges.put(entry.getKey(), entry.getValue().get("totalCharges"));
            }
        }
        
        return totalCharges;
    }

    @Override
    @Transactional
    public boolean deleteShipment(String referenceId) {
        log.info("Deleting international shipment: {}", referenceId);
        
        Optional<InternationalShipment> shipmentOpt = shipmentRepository.findByReferenceId(referenceId);
        if (!shipmentOpt.isPresent()) {
            return false;
        }
        
        InternationalShipment shipment = shipmentOpt.get();
        
        // Don't actually delete the shipment, just mark it as cancelled
        if (shipment.getStatus().ordinal() < InternationalShipment.ShipmentStatus.SUBMITTED_TO_CARRIER.ordinal()) {
            shipment.setStatus(InternationalShipment.ShipmentStatus.CANCELLED);
            shipmentRepository.save(shipment);
            return true;
        } else {
            // Try to cancel with the carrier first
            try {
                cancelShipment(referenceId);
                return true;
            } catch (Exception e) {
                log.error("Failed to cancel shipment with carrier", e);
                return false;
            }
        }
    }
}
