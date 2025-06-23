package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.service.VnPaySecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

    @Autowired
    private VnPaySecurityService securityService;

    // Clean up old security entries every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupSecurityEntries() {
        try {
            logger.debug("Cleaning up old security entries...");
            securityService.cleanupOldEntries();
        } catch (Exception e) {
            logger.error("Error cleaning up security entries", e);
        }
    }
}
