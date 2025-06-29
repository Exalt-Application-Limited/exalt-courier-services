package com.exalt.courierservices.management.$1;

import com.exalt.courier.management.assignment.dto.AssignmentTaskDTO;
import com.exalt.courier.management.assignment.model.Assignment;
import com.exalt.courier.management.assignment.model.AssignmentTask;
import com.exalt.courier.management.assignment.repository.AssignmentRepository;
import com.exalt.courier.management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between AssignmentTask entity and AssignmentTaskDTO.
 */
@Component
public class AssignmentTaskMapper {

    private final AssignmentRepository assignmentRepository;

    public AssignmentTaskMapper(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Convert an entity to a DTO.
     *
     * @param task the entity
     * @return the DTO
     */
    public AssignmentTaskDTO toDTO(AssignmentTask task) {
        if (task == null) {
            return null;
        }

        return AssignmentTaskDTO.builder()
                .id(task.getId())
                .assignmentId(task.getAssignment() != null ? task.getAssignment().getId() : null)
                .taskType(task.getTaskType())
                .status(task.getStatus())
                .sequence(task.getSequence())
                .address(task.getAddress())
                .addressLine2(task.getAddressLine2())
                .city(task.getCity())
                .postalCode(task.getPostalCode())
                .stateProvince(task.getStateProvince())
                .country(task.getCountry())
                .latitude(task.getLatitude())
                .longitude(task.getLongitude())
                .scheduledTime(task.getScheduledTime())
                .completedTime(task.getCompletedTime())
                .estimatedDurationMinutes(task.getEstimatedDurationMinutes())
                .actualDurationMinutes(task.getActualDurationMinutes())
                .notes(task.getNotes())
                .contactName(task.getContactName())
                .contactPhone(task.getContactPhone())
                .instructions(task.getInstructions())
                .timeWindowStart(task.getTimeWindowStart())
                .timeWindowEnd(task.getTimeWindowEnd())
                .referenceCode(task.getReferenceCode())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    /**
     * Convert a DTO to an entity.
     *
     * @param dto the DTO
     * @return the entity
     */
    public AssignmentTask toEntity(AssignmentTaskDTO dto) {
        if (dto == null) {
            return null;
        }

        AssignmentTask task = new AssignmentTask();
        
        // Only set ID if it's an update operation
        if (dto.getId() != null) {
            task.setId(dto.getId());
        }
        
        // Set the assignment if assignmentId is provided
        if (dto.getAssignmentId() != null) {
            Assignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + dto.getAssignmentId()));
            task.setAssignment(assignment);
        }
        
        task.setTaskType(dto.getTaskType());
        task.setStatus(dto.getStatus());
        task.setSequence(dto.getSequence());
        task.setAddress(dto.getAddress());
        task.setAddressLine2(dto.getAddressLine2());
        task.setCity(dto.getCity());
        task.setPostalCode(dto.getPostalCode());
        task.setStateProvince(dto.getStateProvince());
        task.setCountry(dto.getCountry());
        task.setLatitude(dto.getLatitude());
        task.setLongitude(dto.getLongitude());
        task.setScheduledTime(dto.getScheduledTime());
        task.setCompletedTime(dto.getCompletedTime());
        task.setEstimatedDurationMinutes(dto.getEstimatedDurationMinutes());
        task.setActualDurationMinutes(dto.getActualDurationMinutes());
        task.setNotes(dto.getNotes());
        task.setContactName(dto.getContactName());
        task.setContactPhone(dto.getContactPhone());
        task.setInstructions(dto.getInstructions());
        task.setTimeWindowStart(dto.getTimeWindowStart());
        task.setTimeWindowEnd(dto.getTimeWindowEnd());
        task.setReferenceCode(dto.getReferenceCode());
        
        return task;
    }

    /**
     * Convert a list of entities to a list of DTOs.
     *
     * @param tasks the list of entities
     * @return the list of DTOs
     */
    public List<AssignmentTaskDTO> toDTOList(List<AssignmentTask> tasks) {
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert a list of DTOs to a list of entities.
     *
     * @param dtos the list of DTOs
     * @return the list of entities
     */
    public List<AssignmentTask> toEntityList(List<AssignmentTaskDTO> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 