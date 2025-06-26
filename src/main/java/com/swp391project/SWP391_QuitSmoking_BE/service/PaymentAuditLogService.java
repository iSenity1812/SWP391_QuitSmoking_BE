package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PaymentAuditLog;
import com.swp391project.SWP391_QuitSmoking_BE.repository.PaymentAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentAuditLogService {
    @Autowired
    private PaymentAuditLogRepository paymentAuditLogRepository;

    public List<PaymentAuditLog> getAllPaymentAuditLogs() {
        return paymentAuditLogRepository.findAll();
    }

    public Optional<PaymentAuditLog> getPaymentAuditLogById(Integer id) {
        return paymentAuditLogRepository.findById(id);
    }

    public PaymentAuditLog createPaymentAuditLog(PaymentAuditLog paymentAuditLog) {
        return paymentAuditLogRepository.save(paymentAuditLog);
    }

    public PaymentAuditLog updatePaymentAuditLog(Integer id, PaymentAuditLog paymentAuditLogDetails) {
        return paymentAuditLogRepository.findById(id).map(paymentAuditLog -> {
            paymentAuditLog.setTransactionId(paymentAuditLogDetails.getTransactionId());
            paymentAuditLog.setEventType(paymentAuditLogDetails.getEventType());
            paymentAuditLog.setStatus(paymentAuditLogDetails.getStatus());
            paymentAuditLog.setDetails(paymentAuditLogDetails.getDetails());
            paymentAuditLog.setClientIP(paymentAuditLogDetails.getClientIP());
            paymentAuditLog.setCreatedAt(paymentAuditLogDetails.getCreatedAt());
            return paymentAuditLogRepository.save(paymentAuditLog);
        }).orElseThrow(() -> new RuntimeException("PaymentAuditLog not found"));
    }

    public void deletePaymentAuditLog(Integer id) {
        paymentAuditLogRepository.deleteById(id);
    }
}