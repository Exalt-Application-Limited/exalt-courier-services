package com.exalt.courier.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standardized API response model used across all courier services.
 * Provides consistent response format for all API endpoints.
 *
 * @param <T> the type of data contained in the response
 */
@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String message;
    private final LocalDateTime timestamp;
    
    /**
     * Creates a successful response with the specified data.
     *
     * @param <T> the type of data
     * @param data the response data
     * @return a successful API response with the specified data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates a successful response with the specified data and message.
     *
     * @param <T> the type of data
     * @param data the response data
     * @param message the response message
     * @return a successful API response with the specified data and message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response with the specified message.
     *
     * @param <T> the type of data
     * @param message the error message
     * @return an error API response with the specified message
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
    
    /**
     * Creates an error response with the specified message and data.
     *
     * @param <T> the type of data
     * @param message the error message
     * @param data the error data
     * @return an error API response with the specified message and data
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(data)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }


}
