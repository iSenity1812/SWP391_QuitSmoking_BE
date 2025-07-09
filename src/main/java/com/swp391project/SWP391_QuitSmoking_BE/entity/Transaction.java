package com.swp391project.SWP391_QuitSmoking_BE.entity;

import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.validation.transaction.ValidTransactionStatusDate;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidTransactionStatusDate
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "TransactionID", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID transactionId;

    // Mối quan hệ Many-to-One với User
    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false, foreignKey = @ForeignKey(name = "FK_Transaction_User"))
    private User user;

    // Mối quan hệ Many-to-One với Plan
    @NotNull(message = "Gói không được để trống")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanID", nullable = false, foreignKey = @ForeignKey(name = "FK_Transaction_Plan"))
    private Plan plan;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "SubscriptionID", foreignKey = @ForeignKey(name = "FK_Transaction_Subscription"))
    private Subscription subscription; // Có thể null ban đầu, sau đó được gán khi subscription được tạo/cập nhật

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = true, message = "Số tiền phải không âm")
    @Digits(integer = 10, fraction = 2, message = "Số tiền có tối đa 10 chữ số phần nguyên và 2 chữ số phần thập phân")
    @Column(name = "Amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Ngày giao dịch không được để trống")
    @Column(name = "TransactionDate", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Size(max = 50, message = "Phương thức thanh toán không được vượt quá 50 ký tự")
    @Column(name = "PaymentMethod", length = 50, nullable = false)
    private String paymentMethod; // Ví dụ: 'VNPAY', 'Stripe', 'MoMo'

    @NotNull(message = "Trạng thái giao dịch không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "PaymentGatewayResponse", columnDefinition = "jsonb")
    private Map<String, Object> paymentGatewayResponse;

    @Size(max = 255, message = "ID giao dịch ngoại bộ không được vượt quá 255 ký tự")
    @Column(name = "ExternalTransactionId", length = 255, unique = true) // Thêm unique = true cho cặp này
    private String externalTransactionId;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
