package com.gogidix.courier.management.assignment.service;

import com.gogidix.courier.management.assignment.model.Assignment;
import com.gogidix.courier.management.assignment.model.AssignmentTask;

import java.util.List;

/**
 * Service interface for task sequencing operations.
 * This service is responsible for determining the optimal sequence of tasks
 * within an assignment based on various factors like location, time windows, etc.
 */
public interface TaskSequencingService {

    /**
     * Determines the optimal sequence for tasks in an assignment.
     *
     * @param assignment the assignment containing tasks to sequence
     * @return the list of tasks in optimal sequence
     */
    List<AssignmentTask> determineOptimalSequence(Assignment assignment);

    /**
     * Determines the optimal sequence for a list of tasks.
     *
     * @param tasks the list of tasks to sequence
     * @return the list of tasks in optimal sequence
     */
    List<AssignmentTask> determineOptimalSequence(List<AssignmentTask> tasks);

    /**
     * Applies a determined sequence to tasks in an assignment.
     *
     * @param assignment the assignment to update
     * @param taskSequence the ordered list of tasks
     * @return the updated assignment with resequenced tasks
     */
    Assignment applySequence(Assignment assignment, List<AssignmentTask> taskSequence);

    /**
     * Validates if a task sequence is valid for an assignment.
     *
     * @param assignment the assignment
     * @param taskSequence the task sequence to validate
     * @return true if the sequence is valid, false otherwise
     */
    boolean isValidSequence(Assignment assignment, List<AssignmentTask> taskSequence);

    /**
     * Estimates the total travel time for a sequence of tasks.
     *
     * @param tasks the ordered list of tasks
     * @return the estimated travel time in minutes
     */
    int estimateTravelTime(List<AssignmentTask> tasks);

    /**
     * Estimates the total distance for a sequence of tasks.
     *
     * @param tasks the ordered list of tasks
     * @return the estimated distance in kilometers
     */
    double estimateDistance(List<AssignmentTask> tasks);

    /**
     * Determines if a sequence of tasks can be completed within their time windows.
     *
     * @param tasks the ordered list of tasks
     * @param startTime the start time for the first task
     * @return true if all tasks can be completed within their time windows, false otherwise
     */
    boolean canCompleteWithinTimeWindows(List<AssignmentTask> tasks, java.time.LocalDateTime startTime);
} 