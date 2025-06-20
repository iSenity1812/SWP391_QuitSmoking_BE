package com.swp391project.SWP391_QuitSmoking_BE.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PaymentMetricsService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentMetricsService.class);

    private final AtomicLong successfulPayments = new AtomicLong(0);
    private final AtomicLong failedPayments = new AtomicLong(0);
    private final AtomicReference<BigDecimal> totalRevenue = new AtomicReference<>(BigDecimal.ZERO);

    public void recordSuccessfulPayment(BigDecimal amount) {
        successfulPayments.incrementAndGet();
        totalRevenue.updateAndGet(current -> current.add(amount));
        logger.info("Payment success recorded. Total successful: {}, Total revenue: {}",
                successfulPayments.get(), totalRevenue.get());
    }

    public void recordFailedPayment() {
        failedPayments.incrementAndGet();
        logger.info("Payment failure recorded. Total failed: {}", failedPayments.get());
    }

    public long getSuccessfulPayments() {
        return successfulPayments.get();
    }

    public long getFailedPayments() {
        return failedPayments.get();
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue.get();
    }

    public double getSuccessRate() {
        long total = successfulPayments.get() + failedPayments.get();
        if (total == 0) return 0.0;
        return (double) successfulPayments.get() / total * 100;
    }
}
