package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Basic finders
    Optional<Transaction> findByTransactionId(UUID transactionId);

    List<Transaction> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status);

    List<Transaction> findByPaymentMethodOrderByCreatedAtDesc(PaymentMethod paymentMethod);

    Optional<Transaction> findByVnpayTransactionRef(String vnpayTransactionRef);

    // Paginated queries
    Page<Transaction> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Transaction> findByStatusOrderByCreatedAtDesc(TransactionStatus status, Pageable pageable);

    // Date range queries
    List<Transaction> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    // Status-based queries
    List<Transaction> findByUserIdAndStatusOrderByCreatedAtDesc(UUID userId, TransactionStatus status);

    List<Transaction> findByStatusAndCreatedAtBeforeOrderByCreatedAtDesc(
            TransactionStatus status, LocalDateTime beforeDate);

    // Amount-based queries
    List<Transaction> findByAmountBetweenOrderByCreatedAtDesc(BigDecimal minAmount, BigDecimal maxAmount);

    List<Transaction> findByUserIdAndAmountGreaterThanEqualOrderByCreatedAtDesc(
            UUID userId, BigDecimal minAmount);

    // VnPay specific queries
    List<Transaction> findByVnpayResponseCodeOrderByCreatedAtDesc(String responseCode);

    List<Transaction> findByVnpayBankCodeOrderByCreatedAtDesc(String bankCode);

    // Subscription queries
    List<Transaction> findBySubscriptionIdOrderByCreatedAtDesc(UUID subscriptionId);

    List<Transaction> findBySubscriptionTypeOrderByCreatedAtDesc(String subscriptionType);

    // Complex queries using @Query
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId AND t.status = :status " +
            "AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    List<Transaction> findUserTransactionsByStatusAndDateRange(
            @Param("userId") UUID userId,
            @Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.userId = :userId AND t.status = :status")
    BigDecimal getTotalAmountByUserAndStatus(@Param("userId") UUID userId, @Param("status") TransactionStatus status);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.userId = :userId AND t.status = :status")
    Long countTransactionsByUserAndStatus(@Param("userId") UUID userId, @Param("status") TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status AND t.expiredAt < :currentTime")
    List<Transaction> findExpiredTransactions(@Param("status") TransactionStatus status,
            @Param("currentTime") LocalDateTime currentTime);

    // Statistical queries
    @Query("SELECT t.status, COUNT(t) FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate GROUP BY t.status")
    List<Object[]> getTransactionStatsByDateRange(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT t.paymentMethod, COUNT(t), SUM(t.amount) FROM Transaction t WHERE t.status = :status " +
            "AND t.createdAt BETWEEN :startDate AND :endDate GROUP BY t.paymentMethod")
    List<Object[]> getPaymentMethodStats(@Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(t.createdAt), COUNT(t), SUM(t.amount) FROM Transaction t WHERE t.status = :status " +
            "AND t.createdAt BETWEEN :startDate AND :endDate GROUP BY DATE(t.createdAt) ORDER BY DATE(t.createdAt)")
    List<Object[]> getDailyTransactionStats(@Param("status") TransactionStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Refund queries
    @Query("SELECT t FROM Transaction t WHERE t.status = :status AND t.refundAmount IS NOT NULL ORDER BY t.refundedAt DESC")
    List<Transaction> findRefundedTransactions(@Param("status") TransactionStatus status);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status AND t.refundAmount < t.amount ORDER BY t.refundedAt DESC")
    List<Transaction> findPartiallyRefundedTransactions(@Param("status") TransactionStatus status);

    // Performance queries
    @Query("SELECT t FROM Transaction t WHERE t.userId = :userId ORDER BY t.createdAt DESC LIMIT 10")
    List<Transaction> findRecentTransactionsByUser(@Param("userId") UUID userId);

    @Query("SELECT DISTINCT t.userId FROM Transaction t WHERE t.status = :status AND t.createdAt >= :since")
    List<UUID> findActiveUsersSince(@Param("status") TransactionStatus status, @Param("since") LocalDateTime since);
}