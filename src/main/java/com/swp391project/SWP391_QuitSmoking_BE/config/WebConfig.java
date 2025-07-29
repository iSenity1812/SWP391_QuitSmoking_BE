package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    private final StringToMultipartFileConverter stringToMultipartFileConverter;

    public WebConfig(StringToMultipartFileConverter stringToMultipartFileConverter) {
        this.stringToMultipartFileConverter = stringToMultipartFileConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToMultipartFileConverter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Specific origins instead of wildcard when allowCredentials = true
                .allowedOrigins(
                    "http://localhost:5173",
                    "http://localhost:5174", 
                    "http://localhost:3000"
                )
                // Use allowedOriginPatterns for dynamic domains like ngrok
                .allowedOriginPatterns(
                    "https://*.ngrok-free.app",
                    "https://*.ngrok.io"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files from uploads directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/")
                .setCachePeriod(3600); // Cache for 1 hour
    }
}
