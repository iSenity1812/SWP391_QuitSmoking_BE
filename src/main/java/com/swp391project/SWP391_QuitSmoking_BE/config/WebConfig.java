// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/config/WebConfig.java

package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Áp dụng cho tất cả các endpoint dưới /api
                .allowedOrigins("http://localhost:5173") // <-- ĐẶT ĐÚNG CỔNG FRONTEND CỦA BẠN VÀO ĐÂY
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Cho phép các phương thức này
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true) // Cho phép gửi cookies, authorization headers, v.v.
                .maxAge(3600); // Thời gian cache pre-flight request (giây)
    }
}