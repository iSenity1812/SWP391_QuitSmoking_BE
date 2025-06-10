package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class TokenCleanupService {
    private static final Logger log = LoggerFactory.getLogger(TokenCleanupService.class);
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight (0 0 0 * * ?)
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting token cleanup process...");
        Instant now = Instant.now();
        tokenBlacklistRepository.deleteAllByExpirationTimeBefore(now);
        log.info("Token cleanup process completed. Expired tokens removed before: {}", now);
    }
}
