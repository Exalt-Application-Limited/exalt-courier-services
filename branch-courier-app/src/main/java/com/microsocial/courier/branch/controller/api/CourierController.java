package com.exalt.courier.courier.branch.controller.api;

import com.microsocial.courier.branch.model.Courier;
import com.microsocial.courier.branch.model.CourierStatus;
import com.microsocial.courier.branch.service.CourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/branch/couriers")
public class CourierController {

    private final CourierService courierService;

    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping
    public ResponseEntity<List<Courier>> getAllCouriers() {
        List<Courier> couriers = courierService.getAllCouriers();
        return ResponseEntity.ok(couriers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Courier> getCourierById(@PathVariable Long id) {
        return courierService.getCourierById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Courier not found with id: " + id));
    }

    @GetMapping("/search/status/{status}")
    public ResponseEntity<List<Courier>> findCouriersByStatus(@PathVariable CourierStatus status) {
        List<Courier> couriers = courierService.findCouriersByStatus(status);
        return ResponseEntity.ok(couriers);
    }

    @GetMapping("/search/active")
    public ResponseEntity<List<Courier>> findActiveCouriers(@RequestParam boolean active) {
        List<Courier> couriers = courierService.findActiveCouriers(active);
        return ResponseEntity.ok(couriers);
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<Courier>> findCouriersByName(@RequestParam String name) {
        List<Courier> couriers = courierService.findCouriersByName(name);
        return ResponseEntity.ok(couriers);
    }

    @GetMapping("/search/vehicle-type")
    public ResponseEntity<List<Courier>> findCouriersByVehicleType(@RequestParam String vehicleType) {
        List<Courier> couriers = courierService.findCouriersByVehicleType(vehicleType);
        return ResponseEntity.ok(couriers);
    }

    @PostMapping
    public ResponseEntity<Courier> createCourier(@Validated @RequestBody Courier courier) {
        Courier createdCourier = courierService.saveCourier(courier);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCourier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Courier> updateCourier(
            @PathVariable Long id, 
            @Validated @RequestBody Courier courier) {
        
        if (!courierService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Courier not found with id: " + id);
        }
        
        courier.setId(id);
        Courier updatedCourier = courierService.saveCourier(courier);
        return ResponseEntity.ok(updatedCourier);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Courier> updateCourierStatus(
            @PathVariable Long id, 
            @RequestParam CourierStatus status) {
        
        if (!courierService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Courier not found with id: " + id);
        }
        
        Courier updatedCourier = courierService.updateCourierStatus(id, status);
        return ResponseEntity.ok(updatedCourier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourier(@PathVariable Long id) {
        if (!courierService.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "Courier not found with id: " + id);
        }
        
        courierService.deleteCourier(id);
        return ResponseEntity.noContent().build();
    }
}
