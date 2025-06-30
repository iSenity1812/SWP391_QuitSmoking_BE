package com.swp391project.SWP391_QuitSmoking_BE.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime; // Import LocalDateTime
import java.util.Map; // Import Map cho lỗi validation

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status; // HTTP status code (e.g., 200, 400, 500)
    private String message; // General message about the operation/outcome
    private T data; // Payload for successful responses
    private Object error; // Detailed error message (can be a String or a Map for validation errors)
    private String errorCode; // A custom internal error code (e.g., "USER_NOT_FOUND", "INVALID_EMAIL_FORMAT")
    private LocalDateTime timestamp; // When the response was generated

    // --- Static factory methods for Success responses ---
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Thao tác thành công.");
    }

    public static ApiResponse<Void> success(String message) {
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // --- Static factory methods for Error responses ---

    // For general errors with a simple message
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // For errors with a detailed String error message
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorDetails) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .error(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // For errors with a custom error code (e.g., for specific business logic errors)
    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorDetails, String errorCode) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .error(errorDetails)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // For validation errors, where errorDetails is a Map of field errors
    public static <T> ApiResponse<T> validationError(HttpStatus status, String message, Map<String, String> fieldErrors) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .error(fieldErrors) // error field now holds the map
                .errorCode("VALIDATION_ERROR") // Custom error code for validation
                .timestamp(LocalDateTime.now())
                .build();
    }

}