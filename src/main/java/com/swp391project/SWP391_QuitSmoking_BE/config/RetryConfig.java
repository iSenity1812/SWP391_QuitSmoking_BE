package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
public class RetryConfig {
    // Retry configuration is enabled
    // Individual methods can use @Retryable annotation
}
