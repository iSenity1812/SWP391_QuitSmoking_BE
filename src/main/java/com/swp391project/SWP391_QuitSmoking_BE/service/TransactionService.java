package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(UUID id) {
        return transactionRepository.findById(id);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(UUID id, Transaction transactionDetails) {
        return transactionRepository.findById(id).map(transaction -> {
            transaction.setMember(transactionDetails.getMember());
            transaction.setTransactionMethod(transactionDetails.getTransactionMethod());
            transaction.setAmount(transactionDetails.getAmount());
            transaction.setTransactionDate(transactionDetails.getTransactionDate());
            transaction.setStatus(transactionDetails.getStatus());
            return transactionRepository.save(transaction);
        }).orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public void deleteTransaction(UUID id) {
        transactionRepository.deleteById(id);
    }
}