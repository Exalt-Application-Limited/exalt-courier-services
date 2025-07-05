package com.gogidix.courier.tracking.hateoas;

import com.gogidix.courier.tracking.controller.TrackingController;
import com.gogidix.courier.tracking.dto.PackageDTO;
import com.gogidix.courier.tracking.model.TrackingStatus;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler for converting PackageDTO to EntityModel with HATEOAS links.
 */
@Component
public class PackageResourceAssembler implements RepresentationModelAssembler<PackageDTO, EntityModel<PackageDTO>> {

    @Override
    public EntityModel<PackageDTO> toModel(PackageDTO packageDTO) {
        EntityModel<PackageDTO> packageModel = EntityModel.of(packageDTO);
        
        // Add self link
        packageModel.add(linkTo(methodOn(TrackingController.class)
                .getPackageByTrackingNumber(packageDTO.getTrackingNumber()))
                .withSelfRel());
        
        // Add link to events
        packageModel.add(linkTo(methodOn(TrackingController.class)
                .getTrackingEvents(packageDTO.getTrackingNumber()))
                .withRel("events"));
        
        // Add conditional links based on package status
        if (!packageDTO.getStatus().isFinalState()) {
            // Add status update link
            packageModel.add(linkTo(methodOn(TrackingController.class)
                    .updatePackageStatus(packageDTO.getTrackingNumber(), null))
                    .withRel("update-status"));
            
            // Add delivery attempt link
            if (packageDTO.getStatus() == TrackingStatus.OUT_FOR_DELIVERY) {
                packageModel.add(linkTo(methodOn(TrackingController.class)
                        .recordDeliveryAttempt(packageDTO.getTrackingNumber(), null, null))
                        .withRel("record-delivery-attempt"));
                
                packageModel.add(linkTo(methodOn(TrackingController.class)
                        .markDelivered(packageDTO.getTrackingNumber(), null, null, null))
                        .withRel("mark-delivered"));
            }
        }
        
        return packageModel;
    }
} 