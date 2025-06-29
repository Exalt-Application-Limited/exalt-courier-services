package com.exalt.courierservices.international-shipping.$1;

import com.exalt.courier.international.client.ThirdPartyIntegrationClient;
import com.exalt.courier.international.model.CustomsDeclaration;
import com.exalt.courier.international.model.CustomsItem;
import com.exalt.courier.international.repository.CustomsDeclarationRepository;
import com.exalt.courier.international.service.CustomsDeclarationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the CustomsDeclarationService interface.
 * Provides functionality for managing customs declarations.
 */
@Service
@Slf4j
public class CustomsDeclarationServiceImpl implements CustomsDeclarationService {

    private final CustomsDeclarationRepository customsDeclarationRepository;
    private final ThirdPartyIntegrationClient integrationClient;

    @Autowired
    public CustomsDeclarationServiceImpl(
            CustomsDeclarationRepository customsDeclarationRepository,
            ThirdPartyIntegrationClient integrationClient) {
        this.customsDeclarationRepository = customsDeclarationRepository;
        this.integrationClient = integrationClient;
    }

    @Override
    @Transactional
    public CustomsDeclaration createCustomsDeclaration(CustomsDeclaration customsDeclaration) {
        log.info("Creating customs declaration for shipment: {}", customsDeclaration.getShipmentId());
        
        // Generate a unique reference ID if not provided
        if (customsDeclaration.getReferenceId() == null || customsDeclaration.getReferenceId().isEmpty()) {
            customsDeclaration.setReferenceId("CUSTOMS-" + UUID.randomUUID().toString());
        }
        
        // Set initial status if not set
        if (customsDeclaration.getStatus() == null) {
            customsDeclaration.setStatus(CustomsDeclaration.CustomsStatus.DRAFT);
        }
        
        // Validate the declaration before saving
        List<String> validationErrors = validateCustomsDeclaration(customsDeclaration);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Invalid customs declaration: " + String.join(", ", validationErrors));
        }
        
        return customsDeclarationRepository.save(customsDeclaration);
    }

    @Override
    @Transactional
    public CustomsDeclaration updateCustomsDeclaration(String referenceId, CustomsDeclaration customsDeclaration) {
        log.info("Updating customs declaration: {}", referenceId);
        
        CustomsDeclaration existingDeclaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Don't allow updates for declarations that are already approved
        if (existingDeclaration.getStatus() == CustomsDeclaration.CustomsStatus.APPROVED ||
                existingDeclaration.getStatus() == CustomsDeclaration.CustomsStatus.CLEARED) {
            throw new IllegalStateException("Cannot update customs declaration that is already approved or cleared");
        }
        
        // Update mutable fields
        existingDeclaration.setDeclarationType(customsDeclaration.getDeclarationType());
        existingDeclaration.setShipmentPurpose(customsDeclaration.getShipmentPurpose());
        existingDeclaration.setDeclaredValue(customsDeclaration.getDeclaredValue());
        existingDeclaration.setCurrencyCode(customsDeclaration.getCurrencyCode());
        existingDeclaration.setCommercial(customsDeclaration.isCommercial());
        existingDeclaration.setEoriNumber(customsDeclaration.getEoriNumber());
        existingDeclaration.setIncoterms(customsDeclaration.getIncoterms());
        existingDeclaration.setReasonForExport(customsDeclaration.getReasonForExport());
        existingDeclaration.setTaxId(customsDeclaration.getTaxId());
        existingDeclaration.setRemarks(customsDeclaration.getRemarks());
        
        // If the declaration was rejected, allow resubmission by changing the status
        if (existingDeclaration.getStatus() == CustomsDeclaration.CustomsStatus.REJECTED) {
            existingDeclaration.setStatus(CustomsDeclaration.CustomsStatus.DRAFT);
        }
        
        // If items are provided, replace all existing items
        if (customsDeclaration.getItems() != null && !customsDeclaration.getItems().isEmpty()) {
            existingDeclaration.getItems().clear();
            existingDeclaration.getItems().addAll(customsDeclaration.getItems());
        }
        
        return customsDeclarationRepository.save(existingDeclaration);
    }

    @Override
    public Optional<CustomsDeclaration> getCustomsDeclarationByReferenceId(String referenceId) {
        log.info("Getting customs declaration by reference ID: {}", referenceId);
        return customsDeclarationRepository.findByReferenceId(referenceId);
    }

    @Override
    public Optional<CustomsDeclaration> getCustomsDeclarationByShipmentId(String shipmentId) {
        log.info("Getting customs declaration by shipment ID: {}", shipmentId);
        return customsDeclarationRepository.findByShipmentId(shipmentId);
    }

    @Override
    public List<CustomsDeclaration> getCustomsDeclarationsByStatus(CustomsDeclaration.CustomsStatus status) {
        log.info("Getting customs declarations by status: {}", status);
        return customsDeclarationRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public CustomsDeclaration addItemToDeclaration(String referenceId, CustomsItem item) {
        log.info("Adding item to customs declaration: {}", referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Don't allow updates for declarations that are already approved
        if (declaration.getStatus() == CustomsDeclaration.CustomsStatus.APPROVED ||
                declaration.getStatus() == CustomsDeclaration.CustomsStatus.CLEARED) {
            throw new IllegalStateException("Cannot update customs declaration that is already approved or cleared");
        }
        
        // Initialize items list if null
        if (declaration.getItems() == null) {
            declaration.setItems(new ArrayList<>());
        }
        
        declaration.getItems().add(item);
        return customsDeclarationRepository.save(declaration);
    }

    @Override
    @Transactional
    public CustomsDeclaration removeItemFromDeclaration(String referenceId, Long itemId) {
        log.info("Removing item {} from customs declaration: {}", itemId, referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Don't allow updates for declarations that are already approved
        if (declaration.getStatus() == CustomsDeclaration.CustomsStatus.APPROVED ||
                declaration.getStatus() == CustomsDeclaration.CustomsStatus.CLEARED) {
            throw new IllegalStateException("Cannot update customs declaration that is already approved or cleared");
        }
        
        // Find and remove the item by ID
        if (declaration.getItems() != null) {
            declaration.getItems().removeIf(item -> item.getId() != null && item.getId().equals(itemId));
        }
        
        return customsDeclarationRepository.save(declaration);
    }

    @Override
    @Transactional
    public CustomsDeclaration updateItemInDeclaration(String referenceId, CustomsItem updatedItem) {
        log.info("Updating item in customs declaration: {}", referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Don't allow updates for declarations that are already approved
        if (declaration.getStatus() == CustomsDeclaration.CustomsStatus.APPROVED ||
                declaration.getStatus() == CustomsDeclaration.CustomsStatus.CLEARED) {
            throw new IllegalStateException("Cannot update customs declaration that is already approved or cleared");
        }
        
        // Find and update the item by ID
        boolean found = false;
        if (declaration.getItems() != null && updatedItem.getId() != null) {
            for (int i = 0; i < declaration.getItems().size(); i++) {
                CustomsItem item = declaration.getItems().get(i);
                if (item.getId() != null && item.getId().equals(updatedItem.getId())) {
                    declaration.getItems().set(i, updatedItem);
                    found = true;
                    break;
                }
            }
        }
        
        if (!found) {
            throw new NoSuchElementException("Item not found in customs declaration with ID: " + updatedItem.getId());
        }
        
        return customsDeclarationRepository.save(declaration);
    }

    @Override
    @Transactional
    public CustomsDeclaration submitCustomsDeclaration(String referenceId) {
        log.info("Submitting customs declaration: {}", referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Check if the declaration is ready for submission
        if (!isReadyForSubmission(referenceId)) {
            throw new IllegalStateException("Customs declaration is not ready for submission");
        }
        
        declaration.setStatus(CustomsDeclaration.CustomsStatus.SUBMITTED);
        return customsDeclarationRepository.save(declaration);
    }

    @Override
    public List<String> validateCustomsDeclaration(CustomsDeclaration customsDeclaration) {
        log.info("Validating customs declaration");
        
        List<String> validationErrors = new ArrayList<>();
        
        // Check required fields
        if (customsDeclaration.getOriginCountryCode() == null || customsDeclaration.getOriginCountryCode().isEmpty()) {
            validationErrors.add("Origin country code is required");
        }
        
        if (customsDeclaration.getDestinationCountryCode() == null || customsDeclaration.getDestinationCountryCode().isEmpty()) {
            validationErrors.add("Destination country code is required");
        }
        
        if (customsDeclaration.getDeclarationType() == null) {
            validationErrors.add("Declaration type is required");
        }
        
        if (customsDeclaration.getShipmentPurpose() == null) {
            validationErrors.add("Shipment purpose is required");
        }
        
        if (customsDeclaration.getDeclaredValue() == null) {
            validationErrors.add("Declared value is required");
        }
        
        if (customsDeclaration.getCurrencyCode() == null || customsDeclaration.getCurrencyCode().isEmpty()) {
            validationErrors.add("Currency code is required");
        }
        
        // Validate items if present
        if (customsDeclaration.getItems() != null) {
            for (int i = 0; i < customsDeclaration.getItems().size(); i++) {
                CustomsItem item = customsDeclaration.getItems().get(i);
                
                if (item.getDescription() == null || item.getDescription().isEmpty()) {
                    validationErrors.add("Item " + (i+1) + ": Description is required");
                }
                
                if (item.getQuantity() == null || item.getQuantity() < 1) {
                    validationErrors.add("Item " + (i+1) + ": Quantity must be at least 1");
                }
                
                if (item.getOriginCountryCode() == null || item.getOriginCountryCode().isEmpty()) {
                    validationErrors.add("Item " + (i+1) + ": Origin country code is required");
                }
                
                if (item.getUnitValue() == null || item.getUnitValue() < 0) {
                    validationErrors.add("Item " + (i+1) + ": Unit value must be non-negative");
                }
                
                if (item.getNetWeight() == null || item.getNetWeight() < 0) {
                    validationErrors.add("Item " + (i+1) + ": Net weight must be non-negative");
                }
                
                if (item.getWeightUnit() == null || item.getWeightUnit().isEmpty()) {
                    validationErrors.add("Item " + (i+1) + ": Weight unit is required");
                }
            }
        }
        
        // For commercial shipments, additional validations
        if (customsDeclaration.isCommercial()) {
            if (customsDeclaration.getIncoterms() == null || customsDeclaration.getIncoterms().isEmpty()) {
                validationErrors.add("Incoterms are required for commercial shipments");
            }
            
            // Check EORI number for EU destinations
            String destinationCountry = customsDeclaration.getDestinationCountryCode();
            List<String> euCountries = Arrays.asList("AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", 
                    "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO", "SK", "SI", "ES", "SE");
            
            if (euCountries.contains(destinationCountry) && 
                    (customsDeclaration.getEoriNumber() == null || customsDeclaration.getEoriNumber().isEmpty())) {
                validationErrors.add("EORI number is required for commercial shipments to EU countries");
            }
        }
        
        return validationErrors;
    }

    @Override
    public String generateCustomsDocument(String referenceId, CustomsDeclaration.DeclarationType documentType) {
        log.info("Generating {} document for customs declaration: {}", documentType, referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Check if the declaration has the required information
        if (declaration.getItems() == null || declaration.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot generate customs document: no items in the declaration");
        }
        
        // Generate document URL (in a real implementation, this would call a document generation service)
        // For now, we'll just return a placeholder URL
        return "/api/documents/customs/" + referenceId + "/" + documentType.toString().toLowerCase();
    }

    @Override
    public boolean isReadyForSubmission(String referenceId) {
        log.info("Checking if customs declaration is ready for submission: {}", referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Check if the declaration has all required information
        List<String> validationErrors = validateCustomsDeclaration(declaration);
        
        // Declaration is ready if there are no validation errors and it has at least one item
        return validationErrors.isEmpty() && 
               declaration.getItems() != null && 
               !declaration.getItems().isEmpty();
    }

    @Override
    public List<CustomsDeclaration> getPendingDeclarations() {
        log.info("Getting pending customs declarations");
        return customsDeclarationRepository.findPendingDeclarations();
    }

    @Override
    @Transactional
    public boolean deleteCustomsDeclaration(String referenceId) {
        log.info("Deleting customs declaration: {}", referenceId);
        
        Optional<CustomsDeclaration> declarationOpt = customsDeclarationRepository.findByReferenceId(referenceId);
        if (!declarationOpt.isPresent()) {
            return false;
        }
        
        CustomsDeclaration declaration = declarationOpt.get();
        
        // Don't allow deletion of declarations that are already submitted or approved
        if (declaration.getStatus() == CustomsDeclaration.CustomsStatus.SUBMITTED ||
                declaration.getStatus() == CustomsDeclaration.CustomsStatus.APPROVED ||
                declaration.getStatus() == CustomsDeclaration.CustomsStatus.CLEARED) {
            throw new IllegalStateException("Cannot delete customs declaration that is already submitted, approved, or cleared");
        }
        
        customsDeclarationRepository.delete(declaration);
        return true;
    }

    @Override
    @Transactional
    public CustomsDeclaration setCustomsDeclarationStatus(String referenceId, CustomsDeclaration.CustomsStatus status) {
        log.info("Setting status of customs declaration {} to {}", referenceId, status);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        declaration.setStatus(status);
        return customsDeclarationRepository.save(declaration);
    }

    @Override
    public Double calculateTotalDeclaredValue(String referenceId) {
        log.info("Calculating total declared value for customs declaration: {}", referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        return declaration.calculateTotalValue();
    }

    @Override
    public boolean requiresExportLicense(String referenceId) {
        log.info("Checking if customs declaration requires export license: {}", referenceId);
        
        CustomsDeclaration declaration = customsDeclarationRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new NoSuchElementException("Customs declaration not found with reference ID: " + referenceId));
        
        // Check if any items are export controlled
        return declaration.getItems() != null && 
               declaration.getItems().stream().anyMatch(CustomsItem::isExportControlled);
    }
}
