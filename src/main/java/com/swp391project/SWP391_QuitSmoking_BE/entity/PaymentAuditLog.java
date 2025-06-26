package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
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
@Table(name = "PaymentAuditLog")
public class PaymentAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuditID", updatable = false, nullable = false)
    private Integer auditId;

    @Column(name = "TransactionID", columnDefinition = "uuid")
    private UUID transactionId;

    @Column(name = "EventType", nullable = false, length = 50)
    private String eventType;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @Column(name = "Details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "ClientIP", length = 45)
    private String clientIP;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}