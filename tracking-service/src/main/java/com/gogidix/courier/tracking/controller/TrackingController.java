package com.gogidix.courier.tracking.controller;

import com.gogidix.courier.tracking.dto.CreatePackageRequest;
import com.gogidix.courier.tracking.dto.PackageDTO;
import com.gogidix.courier.tracking.dto.TrackingEventDTO;
import com.gogidix.courier.tracking.dto.UpdatePackageStatusRequest;
import com.gogidix.courier.tracking.hateoas.PackageResourceAssembler;
import com.gogidix.courier.tracking.hateoas.TrackingEventResourceAssembler;
import com.gogidix.courier.tracking.model.TrackingStatus;
import com.gogidix.courier.tracking.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for package tracking operations.
 */
@RestController
@RequestMapping("/api/v1/tracking")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tracking", description = "Package tracking operations")
public class TrackingController {

    private final TrackingService trackingService;
    private final PackageResourceAssembler packageAssembler;
    private final TrackingEventResourceAssembler eventAssembler;
    private final PagedResourcesAssembler<PackageDTO> pagedResourcesAssembler;

    /**
     * Create a new package in the tracking system.
     *
     * @param request the package creation request
     * @return the created package with tracking number
     */
    @PostMapping("/packages")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new package", description = "Create a new package in the tracking system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Package created successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public EntityModel<PackageDTO> createPackage(@Valid @RequestBody CreatePackageRequest request) {
        PackageDTO createdPackage = trackingService.createPackage(request);
        return packageAssembler.toModel(createdPackage);
    }

    /**
     * Get a package by its tracking number.
     *
     * @param trackingNumber the tracking number
     * @return the package if found
     */
    @GetMapping("/packages/{trackingNumber}")
    @Operation(summary = "Get a package by tracking number", description = "Get a package by its tracking number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public ResponseEntity<EntityModel<PackageDTO>> getPackageByTrackingNumber(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber) {
        return trackingService.getPackageByTrackingNumber(trackingNumber)
                .map(packageAssembler::toModel)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "Package not found with tracking number: " + trackingNumber));
    }

    /**
     * Update the status of a package.
     *
     * @param trackingNumber the tracking number
     * @param request the status update request
     * @return the updated package
     */
    @PutMapping("/packages/{trackingNumber}/status")
    @Operation(summary = "Update package status", description = "Update the status of a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<PackageDTO> updatePackageStatus(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber,
            @Valid @RequestBody UpdatePackageStatusRequest request) {
        PackageDTO updatedPackage = trackingService.updatePackageStatus(trackingNumber, request);
        return packageAssembler.toModel(updatedPackage);
    }

    /**
     * Record a delivery attempt for a package.
     *
     * @param trackingNumber the tracking number
     * @param description the description of the attempt
     * @param location the location of the attempt
     * @return the updated package
     */
    @PostMapping("/packages/{trackingNumber}/delivery-attempts")
    @Operation(summary = "Record a delivery attempt", description = "Record a delivery attempt for a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery attempt recorded successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<PackageDTO> recordDeliveryAttempt(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber,
            @Parameter(description = "Description of the attempt") @RequestParam String description,
            @Parameter(description = "Location of the attempt") @RequestParam String location) {
        PackageDTO updatedPackage = trackingService.recordDeliveryAttempt(trackingNumber, description, location);
        return packageAssembler.toModel(updatedPackage);
    }

    /**
     * Mark a package as delivered.
     *
     * @param trackingNumber the tracking number
     * @param description the delivery description
     * @param signatureImage the signature image (base64 encoded)
     * @param location the delivery location
     * @return the updated package
     */
    @PostMapping("/packages/{trackingNumber}/delivered")
    @Operation(summary = "Mark a package as delivered", description = "Mark a package as delivered")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Package marked as delivered successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<PackageDTO> markDelivered(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber,
            @Parameter(description = "Description of the delivery") @RequestParam String description,
            @Parameter(description = "Signature image (base64 encoded)") @RequestParam(required = false) String signatureImage,
            @Parameter(description = "Location of the delivery") @RequestParam String location) {
        PackageDTO updatedPackage = trackingService.markDelivered(trackingNumber, description, signatureImage, location);
        return packageAssembler.toModel(updatedPackage);
    }

    /**
     * Add a tracking event to a package.
     *
     * @param trackingNumber the tracking number
     * @param status the event status
     * @param description the event description
     * @param location the event location
     * @return the created tracking event
     */
    @PostMapping("/packages/{trackingNumber}/events")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a tracking event", description = "Add a tracking event to a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tracking event added successfully",
                    content = @Content(schema = @Schema(implementation = TrackingEventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<TrackingEventDTO> addTrackingEvent(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber,
            @Parameter(description = "Status of the event") @RequestParam TrackingStatus status,
            @Parameter(description = "Description of the event") @RequestParam String description,
            @Parameter(description = "Location of the event") @RequestParam(required = false) String location) {
        TrackingEventDTO createdEvent = trackingService.addTrackingEvent(trackingNumber, status, description, location);
        return eventAssembler.toModel(createdEvent);
    }

    /**
     * Add a tracking event with geolocation data.
     *
     * @param trackingNumber the tracking number
     * @param status the event status
     * @param description the event description
     * @param location the event location
     * @param latitude the latitude
     * @param longitude the longitude
     * @return the created tracking event
     */
    @PostMapping("/packages/{trackingNumber}/events/geo")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a tracking event with geolocation", description = "Add a tracking event with geolocation data to a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tracking event added successfully",
                    content = @Content(schema = @Schema(implementation = TrackingEventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<TrackingEventDTO> addTrackingEventWithGeolocation(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber,
            @Parameter(description = "Status of the event") @RequestParam TrackingStatus status,
            @Parameter(description = "Description of the event") @RequestParam String description,
            @Parameter(description = "Location of the event") @RequestParam String location,
            @Parameter(description = "Latitude of the event") @RequestParam Double latitude,
            @Parameter(description = "Longitude of the event") @RequestParam Double longitude) {
        TrackingEventDTO createdEvent = trackingService.addTrackingEventWithGeolocation(trackingNumber, status, description, 
                location, latitude, longitude);
        return eventAssembler.toModel(createdEvent);
    }

    /**
     * Get all tracking events for a package.
     *
     * @param trackingNumber the tracking number
     * @return list of tracking events
     */
    @GetMapping("/packages/{trackingNumber}/events")
    @Operation(summary = "Get tracking events", description = "Get all tracking events for a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = TrackingEventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public CollectionModel<EntityModel<TrackingEventDTO>> getTrackingEvents(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber) {
        List<TrackingEventDTO> events = trackingService.getTrackingEvents(trackingNumber);
        List<EntityModel<TrackingEventDTO>> eventModels = events.stream()
                .map(eventAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(eventModels,
                linkTo(methodOn(TrackingController.class).getPackageByTrackingNumber(trackingNumber)).withRel("package"));
    }

    /**
     * Get all packages with a specific status.
     *
     * @param status the status
     * @param pageable pagination information
     * @return page of packages
     */
    @GetMapping("/packages/status/{status}")
    @Operation(summary = "Get packages by status", description = "Get all packages with a specific status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public PagedModel<EntityModel<PackageDTO>> getPackagesByStatus(
            @Parameter(description = "Status to filter by") @PathVariable TrackingStatus status, 
            Pageable pageable) {
        Page<PackageDTO> packages = trackingService.getPackagesByStatus(status, pageable);
        return pagedResourcesAssembler.toModel(packages, packageAssembler);
    }

    /**
     * Get all packages assigned to a courier.
     *
     * @param courierId the courier ID
     * @param pageable pagination information
     * @return page of packages
     */
    @GetMapping("/packages/courier/{courierId}")
    @Operation(summary = "Get packages by courier", description = "Get all packages assigned to a courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public PagedModel<EntityModel<PackageDTO>> getPackagesByCourier(
            @Parameter(description = "Courier ID") @PathVariable Long courierId, 
            Pageable pageable) {
        Page<PackageDTO> packages = trackingService.getPackagesByCourier(courierId, pageable);
        return pagedResourcesAssembler.toModel(packages, packageAssembler);
    }

    /**
     * Get all packages on a route.
     *
     * @param routeId the route ID
     * @return list of packages
     */
    @GetMapping("/packages/route/{routeId}")
    @Operation(summary = "Get packages by route", description = "Get all packages on a route")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> getPackagesByRoute(
            @Parameter(description = "Route ID") @PathVariable Long routeId) {
        List<PackageDTO> packages = trackingService.getPackagesByRoute(routeId);
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Get all packages for an order.
     *
     * @param orderId the order ID
     * @return list of packages
     */
    @GetMapping("/packages/order/{orderId}")
    @Operation(summary = "Get packages by order", description = "Get all packages for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> getPackagesByOrder(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        List<PackageDTO> packages = trackingService.getPackagesByOrder(orderId);
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Search for packages by recipient name.
     *
     * @param recipientName the recipient name (partial match)
     * @return list of packages
     */
    @GetMapping("/packages/search/recipient-name")
    @Operation(summary = "Search packages by recipient name", description = "Search for packages by recipient name (partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> searchPackagesByRecipientName(
            @Parameter(description = "Recipient name to search for") @RequestParam String recipientName) {
        List<PackageDTO> packages = trackingService.searchPackagesByRecipientName(recipientName);
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Search for packages by recipient address.
     *
     * @param recipientAddress the recipient address (partial match)
     * @return list of packages
     */
    @GetMapping("/packages/search/recipient-address")
    @Operation(summary = "Search packages by recipient address", description = "Search for packages by recipient address (partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> searchPackagesByRecipientAddress(
            @Parameter(description = "Recipient address to search for") @RequestParam String recipientAddress) {
        List<PackageDTO> packages = trackingService.searchPackagesByRecipientAddress(recipientAddress);
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Find packages that are out for delivery.
     *
     * @return list of packages
     */
    @GetMapping("/packages/out-for-delivery")
    @Operation(summary = "Get packages out for delivery", description = "Find packages that are out for delivery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> getPackagesOutForDelivery() {
        List<PackageDTO> packages = trackingService.getPackagesOutForDelivery();
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Find packages that are delayed.
     *
     * @return list of packages
     */
    @GetMapping("/packages/delayed")
    @Operation(summary = "Get delayed packages", description = "Find packages that are delayed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> getDelayedPackages() {
        List<PackageDTO> packages = trackingService.getDelayedPackages();
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Find packages with multiple delivery attempts.
     *
     * @param minAttempts minimum number of attempts
     * @return list of packages
     */
    @GetMapping("/packages/multiple-attempts")
    @Operation(summary = "Get packages with multiple delivery attempts", description = "Find packages with multiple delivery attempts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> getPackagesWithMultipleAttempts(
            @Parameter(description = "Minimum number of attempts") 
            @RequestParam @Min(value = 1, message = "Minimum attempts must be at least 1") int minAttempts) {
        List<PackageDTO> packages = trackingService.getPackagesWithMultipleAttempts(minAttempts);
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Find packages by estimated delivery date range.
     *
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of packages
     */
    @GetMapping("/packages/delivery-date-range")
    @Operation(summary = "Get packages by estimated delivery date range", description = "Find packages by estimated delivery date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> getPackagesByEstimatedDeliveryDateRange(
            @Parameter(description = "Start of date range") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End of date range") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<PackageDTO> packages = trackingService.getPackagesByEstimatedDeliveryDateRange(startDate, endDate);
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Find packages that are overdue for delivery.
     *
     * @return list of packages
     */
    @GetMapping("/packages/overdue")
    @Operation(summary = "Get overdue packages", description = "Find packages that are overdue for delivery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class)))
    })
    public CollectionModel<EntityModel<PackageDTO>> getOverduePackages() {
        List<PackageDTO> packages = trackingService.getOverduePackages();
        List<EntityModel<PackageDTO>> packageModels = packages.stream()
                .map(packageAssembler::toModel)
                .collect(Collectors.toList());
        
        return CollectionModel.of(packageModels);
    }

    /**
     * Get package delivery statistics.
     *
     * @return map of statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get package delivery statistics", description = "Get statistics about package delivery")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = TrackingService.PackageStatistics.class)))
    })
    public TrackingService.PackageStatistics getPackageStatistics() {
        return trackingService.getPackageStatistics();
    }

    /**
     * Update courier assignment for a package.
     *
     * @param trackingNumber the tracking number
     * @param courierId the courier ID
     * @return the updated package
     */
    @PutMapping("/packages/{trackingNumber}/courier/{courierId}")
    @Operation(summary = "Assign courier to package", description = "Update courier assignment for a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courier assigned successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<PackageDTO> assignCourier(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber, 
            @Parameter(description = "Courier ID") @PathVariable Long courierId) {
        PackageDTO updatedPackage = trackingService.assignCourier(trackingNumber, courierId);
        return packageAssembler.toModel(updatedPackage);
    }

    /**
     * Update route assignment for a package.
     *
     * @param trackingNumber the tracking number
     * @param routeId the route ID
     * @return the updated package
     */
    @PutMapping("/packages/{trackingNumber}/route/{routeId}")
    @Operation(summary = "Assign route to package", description = "Update route assignment for a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Route assigned successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<PackageDTO> assignRoute(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber, 
            @Parameter(description = "Route ID") @PathVariable Long routeId) {
        PackageDTO updatedPackage = trackingService.assignRoute(trackingNumber, routeId);
        return packageAssembler.toModel(updatedPackage);
    }

    /**
     * Update delivery instructions for a package.
     *
     * @param trackingNumber the tracking number
     * @param instructions the delivery instructions
     * @return the updated package
     */
    @PutMapping("/packages/{trackingNumber}/delivery-instructions")
    @Operation(summary = "Update delivery instructions", description = "Update delivery instructions for a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery instructions updated successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<PackageDTO> updateDeliveryInstructions(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber,
            @Parameter(description = "Delivery instructions") @RequestParam String instructions) {
        PackageDTO updatedPackage = trackingService.updateDeliveryInstructions(trackingNumber, instructions);
        return packageAssembler.toModel(updatedPackage);
    }

    /**
     * Set signature requirement for a package.
     *
     * @param trackingNumber the tracking number
     * @param signatureRequired whether signature is required
     * @return the updated package
     */
    @PutMapping("/packages/{trackingNumber}/signature-required")
    @Operation(summary = "Set signature requirement", description = "Set signature requirement for a package")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signature requirement updated successfully",
                    content = @Content(schema = @Schema(implementation = PackageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Package not found")
    })
    public EntityModel<PackageDTO> setSignatureRequired(
            @Parameter(description = "Tracking number of the package") @PathVariable String trackingNumber,
            @Parameter(description = "Whether signature is required") @RequestParam boolean signatureRequired) {
        PackageDTO updatedPackage = trackingService.setSignatureRequired(trackingNumber, signatureRequired);
        return packageAssembler.toModel(updatedPackage);
    }
} 