package com.swp391project.SWP391_QuitSmoking_BE.dto;

import java.util.UUID;

public class PaymentConfirmResponse {
    private UUID transactionId;
    private String status;
    private String message;

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}