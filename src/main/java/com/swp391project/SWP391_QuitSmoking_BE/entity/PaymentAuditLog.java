package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PaymentAuditLog", indexes = {
        @Index(name = "idx_audit_transaction", columnList = "TransactionID"),
        @Index(name = "idx_audit_timestamp", columnList = "Timestamp"),
        @Index(name = "idx_audit_action", columnList = "Action")
})
public class PaymentAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuditID")
    private Long auditId;

    @Column(name = "TransactionID", columnDefinition = "uuid")
    private UUID transactionId;

    @NotBlank(message = "Action cannot be blank")
    @Column(name = "Action", length = 50, nullable = false)
    private String action;

    @NotBlank(message = "Status cannot be blank")
    @Column(name = "Status", length = 20, nullable = false)
    private String status;

    @Column(name = "Details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ClientIP", length = 45)
    private String clientIP;

    @NotNull(message = "Timestamp cannot be null")
    @Column(name = "Timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    // Getters and setters
    public Long getAuditId() { return auditId; }
    public void setAuditId(Long auditId) { this.auditId = auditId; }

    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public String getClientIP() { return clientIP; }
    public void setClientIP(String clientIP) { this.clientIP = clientIP; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
