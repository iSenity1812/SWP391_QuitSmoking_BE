package com.swp391project.SWP391_QuitSmoking_BE.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class VnPaySecurityService {
    private static final Logger logger = LoggerFactory.getLogger(VnPaySecurityService.class);

    @Value("${vnpay.security.ip-whitelist:113.160.92.202,118.107.79.174,203.171.21.94}")
    private Set<String> allowedIPs;

    @Value("${vnpay.security.rate-limit.max-requests:10}")
    private int maxRequestsPerMinute;

    @Value("${vnpay.security.enable-ip-validation:true}")
    private boolean enableIpValidation;

    // Rate limiting storage
    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    // Processed transaction IDs to prevent replay attacks
    private final Map<String, LocalDateTime> processedTransactions = new ConcurrentHashMap<>();

    public boolean isValidCallbackRequest(String clientIP, String transactionId) {
        try {
            // 1. Check IP whitelist
            if (enableIpValidation && !isIPAllowed(clientIP)) {
                logger.warn("Callback from unauthorized IP: {}", clientIP);
                return false;
            }

            // 2. Check rate limiting
            if (!isRateLimitOk(clientIP)) {
                logger.warn("Rate limit exceeded for IP: {}", clientIP);
                return false;
            }

            // 3. Check for replay attacks
            if (isTransactionAlreadyProcessed(transactionId)) {
                logger.warn("Duplicate transaction callback: {}", transactionId);
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("Error validating callback request", e);
            return false;
        }
    }

    private boolean isIPAllowed(String clientIP) {
        if (allowedIPs.isEmpty()) {
            return true; // If no whitelist configured, allow all
        }
        return allowedIPs.contains(clientIP);
    }

    private boolean isRateLimitOk(String clientIP) {
        LocalDateTime now = LocalDateTime.now();

        rateLimitMap.compute(clientIP, (ip, info) -> {
            if (info == null) {
                return new RateLimitInfo(1, now);
            }

            // Reset counter if more than 1 minute has passed
            if (ChronoUnit.MINUTES.between(info.getFirstRequest(), now) >= 1) {
                return new RateLimitInfo(1, now);
            }

            info.incrementCount();
            return info;
        });

        RateLimitInfo info = rateLimitMap.get(clientIP);
        return info.getCount() <= maxRequestsPerMinute;
    }

    private boolean isTransactionAlreadyProcessed(String transactionId) {
        LocalDateTime now = LocalDateTime.now();

        // Clean up old entries (older than 24 hours)
        processedTransactions.entrySet().removeIf(entry ->
                ChronoUnit.HOURS.between(entry.getValue(), now) > 24);

        return processedTransactions.containsKey(transactionId);
    }

    public void markTransactionAsProcessed(String transactionId) {
        processedTransactions.put(transactionId, LocalDateTime.now());
    }

    public void cleanupOldEntries() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        // Clean rate limit entries
        rateLimitMap.entrySet().removeIf(entry ->
                ChronoUnit.HOURS.between(entry.getValue().getFirstRequest(), LocalDateTime.now()) > 1);

        // Clean processed transactions
        processedTransactions.entrySet().removeIf(entry ->
                entry.getValue().isBefore(cutoff));
    }

    private static class RateLimitInfo {
        private final AtomicInteger count;
        private final LocalDateTime firstRequest;

        public RateLimitInfo(int initialCount, LocalDateTime firstRequest) {
            this.count = new AtomicInteger(initialCount);
            this.firstRequest = firstRequest;
        }

        public int getCount() { return count.get(); }
        public void incrementCount() { count.incrementAndGet(); }
        public LocalDateTime getFirstRequest() { return firstRequest; }
    }
}
