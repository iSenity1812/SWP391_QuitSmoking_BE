// src/main/java/com/swp391project.SWP391_QuitSmoking_BE/exception/AppException.java

package com.swp391project.SWP391_QuitSmoking_BE.exception;

import lombok.Getter; // Dùng Lombok để tự động tạo getter

@Getter // Tự động tạo getter cho trường 'errorCode'
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        // Gọi constructor của lớp cha (RuntimeException) với thông điệp từ ErrorCode
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // Bạn cũng có thể thêm constructor với 'cause' nếu muốn giữ lại nguyên nhân gốc của lỗi
    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}