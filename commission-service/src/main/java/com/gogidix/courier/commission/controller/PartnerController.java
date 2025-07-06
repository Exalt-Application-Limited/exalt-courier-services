package com.gogidix.courier.commission.controller;

import com.gogidix.courier.commission.model.Partner;
import com.gogidix.courier.commission.model.PartnerStatus;
import com.gogidix.courier.commission.model.PartnerType;
import com.gogidix.courier.commission.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/partners")
@Slf4j
public class PartnerController {

    
    
    @Autowired
    private PartnerService partnerService;
    
    @PostMapping
    public ResponseEntity<Partner> createPartner(@Valid @RequestBody Partner partner) {
        log.info("REST request to create a partner");
        Partner createdPartner = partnerService.createPartner(partner);
        return new ResponseEntity<>(createdPartner, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Partner> getPartner(@PathVariable String id) {
        log.info("REST request to get partner with ID: {}", id);
        Partner partner = partnerService.getPartner(id);
        return ResponseEntity.ok(partner);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Partner> updatePartner(@PathVariable String id, @Valid @RequestBody Partner partner) {
        log.info("REST request to update partner with ID: {}", id);
        
        // Ensure the ID in the path matches the ID in the body
        if (!id.equals(partner.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        Partner updatedPartner = partnerService.updatePartner(partner);
        return ResponseEntity.ok(updatedPartner);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<Partner> updatePartnerStatus(
            @PathVariable String id, 
            @RequestParam PartnerStatus status) {
        log.info("REST request to update status to {} for partner with ID: {}", status, id);
        Partner updatedPartner = partnerService.updatePartnerStatus(id, status);
        return ResponseEntity.ok(updatedPartner);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartner(@PathVariable String id) {
        log.info("REST request to delete partner with ID: {}", id);
        partnerService.deletePartner(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<Partner>> getAllPartners(
            @RequestParam(required = false) PartnerType type,
            @RequestParam(required = false) PartnerStatus status) {
        log.info("REST request to get all partners with type: {}, status: {}", type, status);
        
        List<Partner> partners;
        
        if (type != null && status != null) {
            partners = partnerService.findPartnersByTypeAndStatus(type, status);
        } else if (type != null) {
            partners = partnerService.findPartnersByType(type);
        } else if (status != null) {
            partners = partnerService.findPartnersByStatus(status);
        } else {
            // Get all partners
            partners = partnerService.findPartnersByStatus(null);
        }
        
        return ResponseEntity.ok(partners);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Partner>> searchPartners(@RequestParam String name) {
        log.info("REST request to search partners by name: {}", name);
        List<Partner> partners = partnerService.searchPartnersByName(name);
        return ResponseEntity.ok(partners);
    }
}

