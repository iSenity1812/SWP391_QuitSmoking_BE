package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.PaymentMethod;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionResponse {

    private UUID transactionId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private PaymentMethod paymentMethod;
    private String description;

    // VnPay specific fields
    private String vnpayTransactionRef;
    private String vnpayResponseCode;
    private String vnpayBankCode;
    private String vnpayBankTranNo;
    private String vnpayTransactionNo;

    // Subscription information
    private UUID subscriptionId;
    private String subscriptionType;
    private String subscriptionDuration;

    // Timestamps
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

    // Refund information
    private BigDecimal refundAmount;
    private String refundReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime refundedAt;

    // Client information
    private String clientIP;

    // Computed fields
    private boolean isCompleted;
    private boolean isSuccessful;
    private boolean canBeRefunded;
    private BigDecimal remainingRefundAmount;
    private boolean isExpired;

    // Helper methods for computed fields
    public boolean getIsCompleted() {
        return status != null && status.isCompleted();
    }

    public boolean getIsSuccessful() {
        return status != null && status.isSuccessful();
    }

    public boolean getCanBeRefunded() {
        return status != null && status.canBeRefunded() &&
                (refundAmount == null || refundAmount.compareTo(amount) < 0);
    }

    public BigDecimal getRemainingRefundAmount() {
        if (refundAmount == null) {
            return amount;
        }
        return amount.subtract(refundAmount);
    }

    public boolean getIsExpired() {
        return expiredAt != null && LocalDateTime.now().isAfter(expiredAt);
    }
}