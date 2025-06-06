package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TransactionMethod;
import com.swp391project.SWP391_QuitSmoking_BE.service.TransactionMethodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
        Optional<TransactionMethod> transactionMethod = transactionMethodService.getTransactionMethodById(id);
        return transactionMethod.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TransactionMethod> createTransactionMethod(
            @Valid @RequestBody TransactionMethod transactionMethod) {
        TransactionMethod created = transactionMethodService.createTransactionMethod(transactionMethod);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionMethod> updateTransactionMethod(@PathVariable Integer id,
            @Valid @RequestBody TransactionMethod transactionMethod) {
        try {
            TransactionMethod updated = transactionMethodService.updateTransactionMethod(id, transactionMethod);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionMethod(@PathVariable Integer id) {
        transactionMethodService.deleteTransactionMethod(id);
        return ResponseEntity.noContent().build();
    }
}