package com.swp391project.SWP391_QuitSmoking_BE.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class PaymentConfirmRequest {
    @NotNull
    private UUID transactionId;
    @NotNull
    private boolean success;
    private String message;

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}