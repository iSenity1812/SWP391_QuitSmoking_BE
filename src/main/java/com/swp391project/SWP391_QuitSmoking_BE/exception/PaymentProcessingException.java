package com.swp391project.SWP391_QuitSmoking_BE.exception;

public class PaymentProcessingException extends RuntimeException {
    private final String errorCode;

    public PaymentProcessingException(String message) {
        super(message);
        this.errorCode = "PAYMENT_ERROR";
    }

    public PaymentProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "PAYMENT_ERROR";
    }

    public PaymentProcessingException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
