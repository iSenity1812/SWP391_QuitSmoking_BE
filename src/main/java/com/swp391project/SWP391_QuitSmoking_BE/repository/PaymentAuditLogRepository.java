package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PaymentAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentAuditLogRepository extends JpaRepository<PaymentAuditLog, Long> {

    List<PaymentAuditLog> findByTransactionIdOrderByTimestampDesc(UUID transactionId);

    List<PaymentAuditLog> findByActionAndTimestampBetween(String action, LocalDateTime start, LocalDateTime end);

    @Query("SELECT p FROM PaymentAuditLog p WHERE p.clientIP = :clientIP AND p.timestamp >= :since")
    List<PaymentAuditLog> findByClientIPSince(@Param("clientIP") String clientIP, @Param("since") LocalDateTime since);

    long countByActionAndTimestampBetween(String action, LocalDateTime start, LocalDateTime end);
}
