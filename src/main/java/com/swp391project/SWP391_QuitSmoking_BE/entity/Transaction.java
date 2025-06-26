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
@Table(name = "transaction", indexes = {
        @Index(name = "idx_transaction_user_id", columnList = "user_id"),
        @Index(name = "idx_transaction_status", columnList = "status"),
        @Index(name = "idx_transaction_created_at", columnList = "created_at"),
        @Index(name = "idx_transaction_vnpay_ref", columnList = "vnpay_transaction_ref")
})
public class Transaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "transaction_id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID transactionId;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "VND";

    @Column(name = "status", nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod = "VNPAY";

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // VnPay specific fields
    @Column(name = "vnpay_transaction_ref", length = 100, unique = true)
    private String vnpayTransactionRef;

    @Column(name = "vnpay_response_code", length = 10)
    private String vnpayResponseCode;

    @Column(name = "vnpay_bank_code", length = 20)
    private String vnpayBankCode;

    @Column(name = "vnpay_bank_tran_no", length = 255)
    private String vnpayBankTranNo;

    @Column(name = "vnpay_transaction_no", length = 255)
    private String vnpayTransactionNo;

    @Column(name = "vnpay_secure_hash", length = 255)
    private String vnpaySecureHash;

    // Subscription information
    @Column(name = "subscription_id", columnDefinition = "uuid")
    private UUID subscriptionId;

    @Column(name = "subscription_type", length = 50)
    private String subscriptionType;

    @Column(name = "subscription_duration", length = 20)
    private String subscriptionDuration;

    // Audit fields
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    // Client information
    @Column(name = "client_ip", length = 45)
    private String clientIP;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    // Refund information
    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason", length = 500)
    private String refundReason;

    @Column(name = "refunded_at")
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
