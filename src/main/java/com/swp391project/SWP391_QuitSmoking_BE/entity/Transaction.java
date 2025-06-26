package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Transaction", indexes = {
        @Index(name = "idx_transaction_user_id", columnList = "UserID"),
        @Index(name = "idx_transaction_status", columnList = "Status"),
        @Index(name = "idx_transaction_created_at", columnList = "CreatedAt"),
        @Index(name = "idx_transaction_vnpay_ref", columnList = "VnpayTransactionRef")
})
public class Transaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "TransactionID", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID transactionId;

    @Column(name = "UserID", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "Currency", nullable = false, length = 3)
    private String currency = "VND";

    @Column(name = "Status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "PaymentMethod", nullable = false, length = 50)
    private String paymentMethod = "VNPAY";

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    // VnPay specific fields
    @Column(name = "VnpayTransactionRef", length = 100, unique = true)
    private String vnpayTransactionRef;

    @Column(name = "VnpayResponseCode", length = 10)
    private String vnpayResponseCode;

    @Column(name = "VnpayBankCode", length = 20)
    private String vnpayBankCode;

    @Column(name = "VnpayBankTranNo", length = 255)
    private String vnpayBankTranNo;

    @Column(name = "VnpayTransactionNo", length = 255)
    private String vnpayTransactionNo;

    @Column(name = "VnpaySecureHash", length = 255)
    private String vnpaySecureHash;

    // Subscription information
    @Column(name = "SubscriptionId", columnDefinition = "uuid")
    private UUID subscriptionId;

    @Column(name = "SubscriptionType", length = 50)
    private String subscriptionType;

    @Column(name = "SubscriptionDuration", length = 20)
    private String subscriptionDuration;

    // Audit fields
    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "CompletedAt")
    private LocalDateTime completedAt;

    @Column(name = "ExpiredAt")
    private LocalDateTime expiredAt;

    // Client information
    @Column(name = "ClientIP", length = 45)
    private String clientIP;

    @Column(name = "UserAgent", length = 500)
    private String userAgent;

    // Refund information
    @Column(name = "RefundAmount", precision = 15, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "RefundReason", length = 500)
    private String refundReason;

    @Column(name = "RefundedAt")
    private LocalDateTime refundedAt;

    // Helper methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (isCompleted() && this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public boolean isCompleted() {
        return "SUCCESS".equals(status) || "FAILED".equals(status) || "CANCELLED".equals(status)
                || "EXPIRED".equals(status);
    }

    public boolean isSuccessful() {
        return "SUCCESS".equals(status);
    }

    public boolean canBeRefunded() {
        return "SUCCESS".equals(status) && (refundAmount == null || refundAmount.compareTo(amount) < 0);
    }

    public BigDecimal getRemainingRefundAmount() {
        if (refundAmount == null) {
            return amount;
        }
        return amount.subtract(refundAmount);
    }

    public boolean isExpired() {
        return expiredAt != null && LocalDateTime.now().isAfter(expiredAt);
    }
}
