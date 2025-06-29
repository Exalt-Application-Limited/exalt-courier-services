package com.exalt.courier.management.assignment.service.impl;

import com.exalt.courier.management.assignment.model.Assignment;
import com.exalt.courier.management.assignment.model.AssignmentTask;
import com.exalt.courier.management.assignment.model.Location;
import com.exalt.courier.management.assignment.model.TaskStatus;
import com.exalt.courier.management.assignment.repository.AssignmentTaskRepository;
import com.exalt.courier.management.assignment.service.TaskSequencingService;
import com.exalt.courier.management.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of TaskSequencingService using the Nearest Neighbor algorithm.
 * This is a greedy algorithm that creates an optimal route by always going to the nearest
 * unvisited node from the current node.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NearestNeighborTaskSequencingServiceImpl implements TaskSequencingService {

    private final AssignmentTaskRepository taskRepository;
    
    private static final double AVERAGE_SPEED_KM_PER_HOUR = 30.0; // Average urban travel speed
    private static final double EARTH_RADIUS_KM = 6371.0; // Earth radius in kilometers
    
    // Average service time in minutes per task
    private static final int AVERAGE_SERVICE_TIME_MINUTES = 10;

    @Override
    public List<AssignmentTask> determineOptimalSequence(Assignment assignment) {
        if (assignment == null) {
            throw new BusinessException("Assignment cannot be null");
        }

        List<AssignmentTask> tasks = assignment.getTasks().stream()
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED 
                             && task.getStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());

        return determineOptimalSequence(tasks);
    }

    @Override
    public List<AssignmentTask> determineOptimalSequence(List<AssignmentTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return new ArrayList<>();
        }

        log.info("Determining optimal sequence for {} tasks", tasks.size());

        // Create a copy of the task list to avoid modifying the original
        List<AssignmentTask> remainingTasks = new ArrayList<>(tasks);
        List<AssignmentTask> orderedTasks = new ArrayList<>();

        // Start with the task that has the earliest start time window
        AssignmentTask currentTask = remainingTasks.stream()
                .filter(task -> task.getStartTimeWindow() != null)
                .min(Comparator.comparing(AssignmentTask::getStartTimeWindow))
                .orElse(remainingTasks.get(0));

        remainingTasks.remove(currentTask);
        orderedTasks.add(currentTask);

        // Continue finding the nearest unvisited task
        while (!remainingTasks.isEmpty()) {
            final AssignmentTask finalCurrentTask = currentTask;
            
            // Find the nearest task considering time windows
            currentTask = findNearestTaskWithTimeWindow(finalCurrentTask, remainingTasks);
            
            remainingTasks.remove(currentTask);
            orderedTasks.add(currentTask);
        }

        return orderedTasks;
    }

    @Override
    @Transactional
    public Assignment applySequence(Assignment assignment, List<AssignmentTask> taskSequence) {
        if (assignment == null) {
            throw new BusinessException("Assignment cannot be null");
        }
        
        if (taskSequence == null || taskSequence.isEmpty()) {
            return assignment;
        }
        
        log.info("Applying new sequence to assignment {}", assignment.getId());

        // Validate that all tasks belong to the assignment
        if (!isValidSequence(assignment, taskSequence)) {
            throw new BusinessException("Invalid task sequence: not all tasks belong to the assignment");
        }

        // Update sequence numbers
        for (int i = 0; i < taskSequence.size(); i++) {
            AssignmentTask task = taskSequence.get(i);
            task.setSequenceNumber(i + 1);
        }

        return assignment;
    }

    @Override
    public boolean isValidSequence(Assignment assignment, List<AssignmentTask> taskSequence) {
        if (assignment == null || taskSequence == null) {
            return false;
        }

        // Check if all tasks in the sequence belong to the assignment
        for (AssignmentTask task : taskSequence) {
            if (task.getAssignment() == null || !task.getAssignment().getId().equals(assignment.getId())) {
                return false;
            }
        }

        // Check that all non-completed tasks in the assignment are included in the sequence
        List<AssignmentTask> activeTasks = assignment.getTasks().stream()
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED 
                             && task.getStatus() != TaskStatus.CANCELLED)
                .collect(Collectors.toList());

        if (activeTasks.size() != taskSequence.size()) {
            return false;
        }

        // Use task IDs for comparison
        List<String> taskIds = activeTasks.stream()
                .map(AssignmentTask::getId)
                .collect(Collectors.toList());

        List<String> sequenceIds = taskSequence.stream()
                .map(AssignmentTask::getId)
                .collect(Collectors.toList());

        return taskIds.containsAll(sequenceIds) && sequenceIds.containsAll(taskIds);
    }

    @Override
    public int estimateTravelTime(List<AssignmentTask> tasks) {
        if (tasks == null || tasks.size() <= 1) {
            return 0;
        }

        double totalDistanceKm = 0.0;

        // Calculate total distance between sequential tasks
        for (int i = 0; i < tasks.size() - 1; i++) {
            AssignmentTask currentTask = tasks.get(i);
            AssignmentTask nextTask = tasks.get(i + 1);

            if (currentTask.getLocation() != null && nextTask.getLocation() != null) {
                totalDistanceKm += calculateHaversineDistance(
                        currentTask.getLocation().getLatitude(),
                        currentTask.getLocation().getLongitude(),
                        nextTask.getLocation().getLatitude(),
                        nextTask.getLocation().getLongitude()
                );
            }
        }

        // Convert distance to time in minutes using average speed
        int travelTimeMinutes = (int) Math.ceil((totalDistanceKm / AVERAGE_SPEED_KM_PER_HOUR) * 60);

        // Add the estimated duration for each task
        int taskDurationMinutes = tasks.stream()
                .mapToInt(task -> task.getEstimatedDuration() != null ? task.getEstimatedDuration() : 0)
                .sum();

        return travelTimeMinutes + taskDurationMinutes;
    }

    @Override
    public double estimateDistance(List<AssignmentTask> tasks) {
        if (tasks == null || tasks.size() <= 1) {
            return 0.0;
        }

        double totalDistanceKm = 0.0;

        // Calculate total distance between sequential tasks
        for (int i = 0; i < tasks.size() - 1; i++) {
            AssignmentTask currentTask = tasks.get(i);
            AssignmentTask nextTask = tasks.get(i + 1);

            if (currentTask.getLocation() != null && nextTask.getLocation() != null) {
                totalDistanceKm += calculateHaversineDistance(
                        currentTask.getLocation().getLatitude(),
                        currentTask.getLocation().getLongitude(),
                        nextTask.getLocation().getLatitude(),
                        nextTask.getLocation().getLongitude()
                );
            }
        }

        return totalDistanceKm;
    }

    @Override
    public boolean canCompleteWithinTimeWindows(List<AssignmentTask> tasks, LocalDateTime startTime) {
        if (tasks == null || tasks.isEmpty()) {
            return true;
        }

        LocalDateTime currentTime = startTime;

        for (AssignmentTask task : tasks) {
            // Calculate travel time to this task from previous task
            if (task != tasks.get(0)) { // Skip for the first task
                AssignmentTask previousTask = tasks.get(tasks.indexOf(task) - 1);
                int travelTimeMinutes = 0;
                
                if (previousTask.getLocation() != null && task.getLocation() != null) {
                    double distanceKm = calculateHaversineDistance(
                            previousTask.getLocation().getLatitude(),
                            previousTask.getLocation().getLongitude(),
                            task.getLocation().getLatitude(),
                            task.getLocation().getLongitude()
                    );
                    travelTimeMinutes = (int) Math.ceil((distanceKm / AVERAGE_SPEED_KM_PER_HOUR) * 60);
                }
                
                // Add travel time to current time
                currentTime = currentTime.plusMinutes(travelTimeMinutes);
            }

            // Check if we arrive before the end time window
            if (task.getEndTimeWindow() != null && currentTime.isAfter(task.getEndTimeWindow())) {
                return false;
            }

            // If we arrive before the start time window, wait until the start time
            if (task.getStartTimeWindow() != null && currentTime.isBefore(task.getStartTimeWindow())) {
                currentTime = task.getStartTimeWindow();
            }

            // Add task duration to current time
            int taskDuration = task.getEstimatedDuration() != null ? task.getEstimatedDuration() : 0;
            currentTime = currentTime.plusMinutes(taskDuration);
        }

        return true;
    }

    /**
     * Finds the nearest task from a list of tasks, considering time window constraints.
     *
     * @param currentTask the current task
     * @param remainingTasks the list of remaining tasks
     * @return the nearest task
     */
    private AssignmentTask findNearestTaskWithTimeWindow(AssignmentTask currentTask, List<AssignmentTask> remainingTasks) {
        if (remainingTasks.isEmpty()) {
            throw new IllegalArgumentException("No remaining tasks to find nearest");
        }

        // First, check if we have tasks with time windows
        List<AssignmentTask> tasksWithTimeWindows = remainingTasks.stream()
                .filter(task -> task.getStartTimeWindow() != null)
                .collect(Collectors.toList());

        if (!tasksWithTimeWindows.isEmpty()) {
            // Get the current time based on the completion of the current task
            LocalDateTime currentTime = LocalDateTime.now();
            if (currentTask.getEstimatedDuration() != null) {
                currentTime = currentTime.plusMinutes(currentTask.getEstimatedDuration());
            }

            // Find tasks that need to be done next due to time windows
            final LocalDateTime finalCurrentTime = currentTime;
            List<AssignmentTask> urgentTasks = tasksWithTimeWindows.stream()
                    .filter(task -> {
                        // Calculate travel time to this task
                        int travelTimeMinutes = 0;
                        if (currentTask.getLocation() != null && task.getLocation() != null) {
                            double distanceKm = calculateHaversineDistance(
                                    currentTask.getLocation().getLatitude(),
                                    currentTask.getLocation().getLongitude(),
                                    task.getLocation().getLatitude(),
                                    task.getLocation().getLongitude()
                            );
                            travelTimeMinutes = (int) Math.ceil((distanceKm / AVERAGE_SPEED_KM_PER_HOUR) * 60);
                        }
                        
                        // Calculate arrival time at this task
                        LocalDateTime arrivalTime = finalCurrentTime.plusMinutes(travelTimeMinutes);
                        
                        // If the task has a start time window and we'd arrive after it, it's considered urgent
                        return task.getStartTimeWindow() != null && 
                               !arrivalTime.isAfter(task.getStartTimeWindow()) &&
                               ChronoUnit.MINUTES.between(arrivalTime, task.getStartTimeWindow()) < 60; // Within 60 minutes
                    })
                    .collect(Collectors.toList());

            if (!urgentTasks.isEmpty()) {
                // Sort urgent tasks by start time and then by distance
                return urgentTasks.stream()
                        .min(Comparator.comparing(AssignmentTask::getStartTimeWindow)
                                .thenComparing(task -> calculateTaskDistance(currentTask, task)))
                        .orElse(findNearestTask(currentTask, remainingTasks));
            }
        }

        // If no urgent tasks, just find the nearest task
        return findNearestTask(currentTask, remainingTasks);
    }

    /**
     * Finds the nearest task from a list of tasks.
     *
     * @param currentTask the current task
     * @param tasks the list of tasks
     * @return the nearest task
     */
    private AssignmentTask findNearestTask(AssignmentTask currentTask, List<AssignmentTask> tasks) {
        return tasks.stream()
                .min(Comparator.comparing(task -> calculateTaskDistance(currentTask, task)))
                .orElse(tasks.get(0));
    }

    /**
     * Calculates the distance between two tasks.
     *
     * @param task1 the first task
     * @param task2 the second task
     * @return the distance in kilometers
     */
    private double calculateTaskDistance(AssignmentTask task1, AssignmentTask task2) {
        if (task1.getLocation() == null || task2.getLocation() == null) {
            return Double.MAX_VALUE; // Tasks without location are considered far away
        }

        return calculateHaversineDistance(
                task1.getLocation().getLatitude(),
                task1.getLocation().getLongitude(),
                task2.getLocation().getLatitude(),
                task2.getLocation().getLongitude()
        );
    }

    /**
     * Calculates the distance between two points using the Haversine formula.
     *
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second point
     * @return the distance in kilometers
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
} 