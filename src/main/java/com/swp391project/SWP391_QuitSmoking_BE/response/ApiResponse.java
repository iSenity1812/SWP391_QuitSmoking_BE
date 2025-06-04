package com.swp391project.SWP391_QuitSmoking_BE.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;
    private String error; // Thêm trường error cho các trường hợp lỗi

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message, String errorDetails) {
        return ApiResponse.<T>builder()
                .status(status.value())
                .message(message)
                .error(errorDetails)
                .build();
    }
}