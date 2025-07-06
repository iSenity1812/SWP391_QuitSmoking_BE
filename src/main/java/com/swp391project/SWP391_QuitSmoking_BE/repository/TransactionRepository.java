package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Plan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
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
    // Define any custom query methods if needed
    // For example, you might want to find transactions by status or user
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByUser(User user);
    List<Transaction> findByPlan(Plan plan);
    Optional<Transaction> findByExternalTransactionId(String externalTransactionId);


    // Basic revenue queries - đã sửa
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = :status")
    BigDecimal getTotalRevenueByStatus(@Param("status") TransactionStatus status);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.status = :status AND t.transactionDate BETWEEN :start AND :end")
    BigDecimal getRevenueByStatusAndDateRange(@Param("status") TransactionStatus status,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = :status")
    Long countTransactionsByStatus(@Param("status") TransactionStatus status);

    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.status = :status AND t.transactionDate BETWEEN :start AND :end")
    Long countTransactionsByStatusAndDateRange(@Param("status") TransactionStatus status,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(AVG(t.amount), 0) FROM Transaction t WHERE t.status = 'SUCCESS'")
    BigDecimal getAverageOrderValue();

    // Revenue by time period - sửa lại với JPQL syntax
    @Query(value = "SELECT " +
            "TO_CHAR(t.transaction_date, 'YYYY-MM-DD') as period, " +
            "DATE_TRUNC('day', t.transaction_date) as period_start, " +
            "DATE_TRUNC('day', t.transaction_date) + INTERVAL '1 day' as period_end, " +
            "COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END), 0) as revenue, " +
            "COUNT(t) as transaction_count, " +
            "COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as success_count, " +
            "COUNT(CASE WHEN t.status = 'FAILED' THEN 1 END) as failed_count, " +
            "CASE WHEN COUNT(t) > 0 THEN " +
            "CAST(COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(t) AS DOUBLE PRECISION) " +
            "ELSE 0.0 END as success_rate " +
            "FROM transaction t " +
            "WHERE t.transaction_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_TRUNC('day', t.transaction_date), TO_CHAR(t.transaction_date, 'YYYY-MM-DD') " +
            "ORDER BY DATE_TRUNC('day', t.transaction_date)", nativeQuery = true)
    List<Object[]> getDailyRevenueRaw(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    // Plan revenue stats - sửa lại với join fetch đúng
    @Query("SELECT " +
            "p.planId as planId, " +
            "p.planName as planName, " +
            "p.price as planPrice, " +
            "p.durationValue as durationDays, " +
            "COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END), 0) as totalRevenue, " +
            "COUNT(t) as totalTransactions, " +
            "COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as successTransactions, " +
            "CASE WHEN COUNT(t) > 0 THEN " +
            "CAST(COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(t) as double) " +
            "ELSE 0.0 END as successRate " +
            "FROM Transaction t " +
            "JOIN t.plan p " +
            "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY p.planId, p.planName, p.price, p.durationValue " +
            "ORDER BY SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END) DESC")
    List<Object[]> getPlanRevenueStatsRaw(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    // Transaction details with pagination - sửa lại không dùng new DTO trong @Query
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE (:statuses IS NULL OR t.status IN :statuses) " +
            "AND (:startDate IS NULL OR t.transactionDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.transactionDate <= :endDate) " +
            "AND (:planIds IS NULL OR p.planId IN :planIds) " +
            "AND (:userIds IS NULL OR u.userId IN :userIds) " +
            "AND (:paymentMethod IS NULL OR t.paymentMethod = :paymentMethod)")
    Page<Transaction> findTransactionsWithDetails(
            @Param("statuses") List<TransactionStatus> statuses,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("planIds") List<Integer> planIds,
            @Param("userIds") List<UUID> userIds,
            @Param("paymentMethod") String paymentMethod,
            Pageable pageable);

    // User revenue stats - sửa lại
    @Query("SELECT " +
            "u.userId as userId, " +
            "u.username as username, " +
            "u.email as email, " +
            "u.role as role, " +
            "u.profilePicture as profilePicture, " +
            "COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END), 0) as totalRevenue, " +
            "COUNT(t) as totalTransactions, " +
            "COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as successTransactions, " +
            "MIN(t.transactionDate) as firstTransactionDate, " +
            "MAX(t.transactionDate) as lastTransactionDate " +
            "FROM User u " +
            "JOIN u.transactions t " +
            "WHERE (:startDate IS NULL OR t.transactionDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.transactionDate <= :endDate) " +
            "GROUP BY u.userId, u.username, u.email, u.role, u.profilePicture " +
            "HAVING COUNT(t) > 0 " +
            "ORDER BY SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END) DESC")
    Page<Object[]> getUserRevenueStatsRaw(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Payment method stats - sửa lại
    @Query("SELECT " +
            "t.paymentMethod as paymentMethod, " +
            "COUNT(t) as totalTransactions, " +
            "COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as successTransactions, " +
            "COUNT(CASE WHEN t.status = 'FAILED' THEN 1 END) as failedTransactions, " +
            "COALESCE(SUM(t.amount), 0) as totalRevenue, " +
            "COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END), 0) as successRevenue, " +
            "CASE WHEN COUNT(t) > 0 THEN " +
            "CAST(COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(t) as double) " +
            "ELSE 0.0 END as successRate " +
            "FROM Transaction t " +
            "WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
            "GROUP BY t.paymentMethod " +
            "ORDER BY SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END) DESC")
    List<Object[]> getPaymentMethodStatsRaw(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Transaction status distribution
    @Query("SELECT " +
            "t.status as status, " +
            "COUNT(t) as count, " +
            "COALESCE(SUM(t.amount), 0) as totalAmount " +
            "FROM Transaction t " +
            "WHERE t.transactionDate BETWEEN :start AND :end " +
            "GROUP BY t.status")
    List<Object[]> getTransactionStatusDistributionRaw(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);



    // Weekly revenue - native query
    @Query(value = "SELECT " +
            "TO_CHAR(DATE_TRUNC('week', t.transaction_date), 'YYYY-\"W\"WW') as period, " +
            "DATE_TRUNC('week', t.transaction_date) as period_start, " +
            "DATE_TRUNC('week', t.transaction_date) + INTERVAL '7 days' as period_end, " +
            "COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END), 0) as revenue, " +
            "COUNT(t) as transaction_count, " +
            "COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as success_count, " +
            "COUNT(CASE WHEN t.status = 'FAILED' THEN 1 END) as failed_count, " +
            "CASE WHEN COUNT(t) > 0 THEN " +
            "CAST(COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(t) AS DOUBLE PRECISION) " +
            "ELSE 0.0 END as success_rate " +
            "FROM transaction t " +
            "WHERE t.transaction_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_TRUNC('week', t.transaction_date), TO_CHAR(DATE_TRUNC('week', t.transaction_date), 'YYYY-\"W\"WW') " +
            "ORDER BY DATE_TRUNC('week', t.transaction_date)", nativeQuery = true)
    List<Object[]> getWeeklyRevenueRaw(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    // Monthly revenue - native query
    @Query(value = "SELECT " +
            "TO_CHAR(DATE_TRUNC('month', t.transaction_date), 'YYYY-MM') as period, " +
            "DATE_TRUNC('month', t.transaction_date) as period_start, " +
            "DATE_TRUNC('month', t.transaction_date) + INTERVAL '1 month' as period_end, " +
            "COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END), 0) as revenue, " +
            "COUNT(t) as transaction_count, " +
            "COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as success_count, " +
            "COUNT(CASE WHEN t.status = 'FAILED' THEN 1 END) as failed_count, " +
            "CASE WHEN COUNT(t) > 0 THEN " +
            "CAST(COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(t) AS DOUBLE PRECISION) " +
            "ELSE 0.0 END as success_rate " +
            "FROM transaction t " +
            "WHERE t.transaction_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_TRUNC('month', t.transaction_date), TO_CHAR(DATE_TRUNC('month', t.transaction_date), 'YYYY-MM') " +
            "ORDER BY DATE_TRUNC('month', t.transaction_date)", nativeQuery = true)
    List<Object[]> getMonthlyRevenueRaw(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    // Yearly revenue - native query
    @Query(value = "SELECT " +
            "TO_CHAR(DATE_TRUNC('year', t.transaction_date), 'YYYY') as period, " +
            "DATE_TRUNC('year', t.transaction_date) as period_start, " +
            "DATE_TRUNC('year', t.transaction_date) + INTERVAL '1 year' as period_end, " +
            "COALESCE(SUM(CASE WHEN t.status = 'SUCCESS' THEN t.amount ELSE 0 END), 0) as revenue, " +
            "COUNT(t) as transaction_count, " +
            "COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) as success_count, " +
            "COUNT(CASE WHEN t.status = 'FAILED' THEN 1 END) as failed_count, " +
            "CASE WHEN COUNT(t) > 0 THEN " +
            "CAST(COUNT(CASE WHEN t.status = 'SUCCESS' THEN 1 END) * 100.0 / COUNT(t) AS DOUBLE PRECISION) " +
            "ELSE 0.0 END as success_rate " +
            "FROM transaction t " +
            "WHERE t.transaction_date BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_TRUNC('year', t.transaction_date), TO_CHAR(DATE_TRUNC('year', t.transaction_date), 'YYYY') " +
            "ORDER BY DATE_TRUNC('year', t.transaction_date)", nativeQuery = true)
    List<Object[]> getYearlyRevenueRaw(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);



    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate")
    Page<Transaction> findTransactionsWithDetailsBase(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Query với filter status
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND t.status IN :statuses")
    Page<Transaction> findTransactionsWithDetailsFilterByStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<TransactionStatus> statuses,
            Pageable pageable);

    // Query với filter payment method
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND t.paymentMethod = :paymentMethod")
    Page<Transaction> findTransactionsWithDetailsFilterByPaymentMethod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("paymentMethod") String paymentMethod,
            Pageable pageable);

    // Query với filter cả status và payment method
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND t.status IN :statuses " +
            "AND t.paymentMethod = :paymentMethod")
    Page<Transaction> findTransactionsWithDetailsFilterByStatusAndPaymentMethod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("statuses") List<TransactionStatus> statuses,
            @Param("paymentMethod") String paymentMethod,
            Pageable pageable);

    // Query với filter plan IDs
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND p.planId IN :planIds")
    Page<Transaction> findTransactionsWithDetailsFilterByPlanIds(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("planIds") List<Integer> planIds,
            Pageable pageable);

    // Query với filter user IDs
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND u.userId IN :userIds")
    Page<Transaction> findTransactionsWithDetailsFilterByUserIds(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userIds") List<UUID> userIds,
            Pageable pageable);


    // Query chỉ lấy SUCCESS transactions
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.status = 'SUCCESS' " +
            "AND t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate")
    Page<Transaction> findSuccessTransactionsInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Query SUCCESS với filter payment method
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.status = 'SUCCESS' " +
            "AND t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND t.paymentMethod = :paymentMethod")
    Page<Transaction> findSuccessTransactionsWithPaymentMethod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("paymentMethod") String paymentMethod,
            Pageable pageable);

    // Query SUCCESS với filter plan IDs
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.status = 'SUCCESS' " +
            "AND t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND p.planId IN :planIds")
    Page<Transaction> findSuccessTransactionsWithPlanIds(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("planIds") List<Integer> planIds,
            Pageable pageable);

    // Query SUCCESS với filter user IDs
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.status = 'SUCCESS' " +
            "AND t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate " +
            "AND u.userId IN :userIds")
    Page<Transaction> findSuccessTransactionsWithUserIds(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userIds") List<UUID> userIds,
            Pageable pageable);

    // Query tổng quát cho trường hợp cần tất cả trạng thái
    @Query("SELECT t FROM Transaction t " +
            "JOIN FETCH t.user u " +
            "JOIN FETCH t.plan p " +
            "LEFT JOIN FETCH t.subscription s " +
            "WHERE t.transactionDate >= :startDate " +
            "AND t.transactionDate <= :endDate")
    Page<Transaction> findAllTransactionsInDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
