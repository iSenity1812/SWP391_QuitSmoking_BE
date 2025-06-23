package com.swp391project.SWP391_QuitSmoking_BE.exception;

public class VnPayException extends RuntimeException {
    private final String errorCode;

    public VnPayException(String message) {
        super(message);
        this.errorCode = "VNPAY_ERROR";
    }

    public VnPayException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public VnPayException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "VNPAY_ERROR";
    }

    public VnPayException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
