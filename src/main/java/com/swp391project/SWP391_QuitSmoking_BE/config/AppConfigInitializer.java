package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class AppConfigInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppConfigInitializer.class);

    @Value("${app.environment}")
    private String environment;

    @Value("${app.base-url.development}")
    private String devBaseUrl;

    @Value("${app.base-url.staging}")
    private String stagingBaseUrl;

    @Value("${app.base-url.production}")
    private String productionBaseUrl;

    @Override
    public void run(String... args) {
        String baseUrl;

        switch (environment) {
            case "production":
                baseUrl = productionBaseUrl;
                break;
            case "staging":
                baseUrl = stagingBaseUrl;
                break;
            default:
                baseUrl = devBaseUrl;
        }

        VnPayConfig.returnUrl = baseUrl;
        logger.info("Initialized VNPay return URL: {}", VnPayConfig.returnUrl);
    }
}
