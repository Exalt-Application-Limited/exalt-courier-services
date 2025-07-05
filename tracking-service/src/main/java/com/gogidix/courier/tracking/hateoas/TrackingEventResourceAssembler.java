package com.gogidix.courier.tracking.hateoas;

import com.gogidix.courier.tracking.controller.TrackingController;
import com.gogidix.courier.tracking.dto.TrackingEventDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Assembler for converting TrackingEventDTO to EntityModel with HATEOAS links.
 */
@Component
public class TrackingEventResourceAssembler implements RepresentationModelAssembler<TrackingEventDTO, EntityModel<TrackingEventDTO>> {

    @Override
    public EntityModel<TrackingEventDTO> toModel(TrackingEventDTO eventDTO) {
        EntityModel<TrackingEventDTO> eventModel = EntityModel.of(eventDTO);
        
        // We need to extract the tracking number from the event context
        // This would typically be available in a real implementation
        // For now, we'll use a placeholder that would be replaced in the actual implementation
        String trackingNumber = "PLACEHOLDER"; // This would be replaced with actual tracking number
        
        // Add link to the package
        eventModel.add(linkTo(methodOn(TrackingController.class)
                .getPackageByTrackingNumber(trackingNumber))
                .withRel("package"));
        
        // Add link to all events for the package
        eventModel.add(linkTo(methodOn(TrackingController.class)
                .getTrackingEvents(trackingNumber))
                .withRel("all-events"));
        
        return eventModel;
    }
} 