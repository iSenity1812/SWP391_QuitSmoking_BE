// src/main/java/com/swp391project.SWP391_QuitSmoking_BE/exception/ErrorCode.java

package com.swp391project.SWP391_QuitSmoking_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter // Dùng Lombok để tự động tạo getter cho các trường
public enum ErrorCode {
    // --- General Errors (Lỗi chung) ---
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_PARAM(1001, "Invalid parameter", HttpStatus.BAD_REQUEST),

    // --- Authentication & Authorization Errors (Lỗi xác thực & phân quyền) ---
    UNAUTHENTICATED(1002, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1003, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),

    // --- User Errors (Lỗi liên quan đến người dùng) ---
    USER_NOT_FOUND(2001, "User not found", HttpStatus.NOT_FOUND),
    USERNAME_EXISTED(2002, "Username already exists", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH(2003, "Password does not match", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS(2004, "Invalid username or password", HttpStatus.UNAUTHORIZED),

    // --- Blog Errors (Lỗi liên quan đến Blog) ---
    BLOG_NOT_FOUND(3001, "Blog not found", HttpStatus.NOT_FOUND),
    BLOG_ALREADY_DELETED(3002, "This blog has already been deleted", HttpStatus.BAD_REQUEST), // Thêm cho Soft Delete

    // --- Comment Errors (Lỗi liên quan đến Comment) ---
    COMMENT_NOT_FOUND(4001, "Comment not found", HttpStatus.NOT_FOUND),
    COMMENT_ALREADY_DELETED(4002, "This comment has already been deleted", HttpStatus.BAD_REQUEST), // Thêm cho Soft Delete

    // --- Other Domain-Specific Errors (Các lỗi khác tùy thuộc vào nghiệp vụ của bạn) ---
    // ...
    ; // Dấu chấm phẩy này là bắt buộc nếu có các trường hoặc phương thức khác sau các hằng số enum

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}