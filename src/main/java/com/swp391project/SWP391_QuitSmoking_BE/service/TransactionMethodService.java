package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TransactionMethod;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TransactionMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionMethodService {
    @Autowired
    private TransactionMethodRepository transactionMethodRepository;

    public List<TransactionMethod> getAllTransactionMethods() {
        return transactionMethodRepository.findAll();
    }

    public Optional<TransactionMethod> getTransactionMethodById(Integer id) {
        return transactionMethodRepository.findById(id);
    }

    public TransactionMethod createTransactionMethod(TransactionMethod transactionMethod) {
        return transactionMethodRepository.save(transactionMethod);
    }

    public TransactionMethod updateTransactionMethod(Integer id, TransactionMethod transactionMethodDetails) {
        return transactionMethodRepository.findById(id).map(transactionMethod -> {
            transactionMethod.setMethodName(transactionMethodDetails.getMethodName());
            transactionMethod.setActive(transactionMethodDetails.isActive());
            transactionMethod.setDescription(transactionMethodDetails.getDescription());
            return transactionMethodRepository.save(transactionMethod);
        }).orElseThrow(() -> new RuntimeException("TransactionMethod not found"));
    }

    public void deleteTransactionMethod(Integer id) {
        transactionMethodRepository.deleteById(id);
    }
}