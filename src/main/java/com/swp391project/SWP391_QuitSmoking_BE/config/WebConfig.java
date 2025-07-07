package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Autowired
    private StringToMultipartFileConverter stringToMultipartFileConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToMultipartFileConverter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Áp dụng cho tất cả các endpoint dưới /api
                .allowedOrigins("http://localhost:5173") // <-- ĐẶT ĐÚNG CỔNG FRONTEND CỦA BẠN VÀO ĐÂY
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Cho phép các phương thức này
                .allowedHeaders("*") // Cho phép tất cả các header
                .allowCredentials(true) // Cho phép gửi cookies, authorization headers, v.v.
                .maxAge(3600); // Thời gian cache pre-flight request (giây)
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files statically
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
