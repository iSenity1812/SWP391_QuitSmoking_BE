package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TransactionMethod;
import com.swp391project.SWP391_QuitSmoking_BE.service.TransactionMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction-methods")
public class TransactionMethodAPI {
    @Autowired
    private TransactionMethodService transactionMethodService;

    @GetMapping
    public List<TransactionMethod> getAllTransactionMethods() {
        return transactionMethodService.getAllTransactionMethods();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionMethod> getTransactionMethodById(@PathVariable Integer id) {
        return transactionMethodService.getTransactionMethodById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public TransactionMethod createTransactionMethod(@RequestBody TransactionMethod transactionMethod) {
        return transactionMethodService.createTransactionMethod(transactionMethod);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionMethod> updateTransactionMethod(@PathVariable Integer id,
            @RequestBody TransactionMethod transactionMethodDetails) {
        try {
            TransactionMethod updatedTransactionMethod = transactionMethodService.updateTransactionMethod(id,
                    transactionMethodDetails);
            return ResponseEntity.ok(updatedTransactionMethod);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionMethod(@PathVariable Integer id) {
        try {
            transactionMethodService.deleteTransactionMethod(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}