package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Plan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
