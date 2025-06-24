package com.itss.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    private LocalDateTime timestamp;
    
    /**
     * Create successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data, null, LocalDateTime.now());
    }
    
    /**
     * Create successful response with data and custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }
    
    /**
     * Create successful response with just message (no data)
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null, LocalDateTime.now());
    }
    
    /**
     * Create error response with message
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null, LocalDateTime.now());
    }
    
    /**
     * Create error response with message and error code
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return new ApiResponse<>(false, message, null, errorCode, LocalDateTime.now());
    }
    
    /**
     * Create error response with message, error code, and data
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, T data) {
        return new ApiResponse<>(false, message, data, errorCode, LocalDateTime.now());
    }
    
    /**
     * Create validation error response
     */
    public static <T> ApiResponse<T> validationError(String message) {
        return new ApiResponse<>(false, message, null, "VALIDATION_ERROR", LocalDateTime.now());
    }
    
    /**
     * Create not found error response
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(false, message, null, "NOT_FOUND", LocalDateTime.now());
    }
    
    /**
     * Create unauthorized error response
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(false, message, null, "UNAUTHORIZED", LocalDateTime.now());
    }
    
    /**
     * Create forbidden error response
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(false, message, null, "FORBIDDEN", LocalDateTime.now());
    }
    
    /**
     * Create internal server error response
     */
    public static <T> ApiResponse<T> internalError(String message) {
        return new ApiResponse<>(false, message, null, "INTERNAL_ERROR", LocalDateTime.now());
    }
    
    /**
     * Create conflict error response
     */
    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<>(false, message, null, "CONFLICT", LocalDateTime.now());
    }
    
    /**
     * Create bad request error response
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(false, message, null, "BAD_REQUEST", LocalDateTime.now());
    }
    
    /**
     * Check if response has data
     */
    public boolean hasData() {
        return data != null;
    }
    
    /**
     * Check if response is an error
     */
    public boolean isError() {
        return !success;
    }
    
    /**
     * Get formatted timestamp
     */
    public String getFormattedTimestamp() {
        if (timestamp == null) return null;
        return timestamp.toString();
    }
}