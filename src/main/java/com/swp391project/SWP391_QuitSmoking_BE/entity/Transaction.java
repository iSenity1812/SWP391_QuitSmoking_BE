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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY) // Một member có thể có nhiều transactions
    @JoinColumn(name = "MemberID", referencedColumnName = "MemberID", nullable = false)
    @NotNull(message = "Thông tin thành viên không được để trống trong giao dịch")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) // Một TransactionMethod có thể được sử dụng bởi nhiều transactions
    @JoinColumn(name = "TransactionMethodID", referencedColumnName = "TransactionMethodID", nullable = false)
    @NotNull(message = "Phương thức giao dịch không được để trống")
    private TransactionMethod transactionMethod;

    @NotNull(message = "Số tiền giao dịch không được để trống")
    @DecimalMin(value = "0.00", inclusive = true, message = "Số tiền giao dịch không thể là số âm")
    @Digits(integer = 6, fraction = 2, message = "Số tiền giao dịch phải có tối đa 6 chữ số phần nguyên và 2 chữ số phần thập phân")
    @Column(name = "Amount", precision = 8, scale = 2, nullable = false) // Tổng 8 chữ số, 2 thập phân -> 6 nguyên
    private BigDecimal amount;

    @PastOrPresent(message = "Ngày giao dịch không thể ở tương lai")
    @Column(name = "TransactionDate", updatable = false)
    private LocalDateTime transactionDate;

    @NotNull(message = "Trạng thái giao dịch không được để trống")
    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    public Member getMember() {
        return member;
    }

    public TransactionMethod getTransactionMethod() {
        return transactionMethod;
    }

    public java.math.BigDecimal getAmount() {
        return amount;
    }

    public java.time.LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus getStatus() {
        return status;
    }

    public void setTransactionId(java.util.UUID transactionId) {
        this.transactionId = transactionId;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setTransactionMethod(TransactionMethod transactionMethod) {
        this.transactionMethod = transactionMethod;
    }

    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionDate(java.time.LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public void setStatus(com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus status) {
        this.status = status;
    }
}
