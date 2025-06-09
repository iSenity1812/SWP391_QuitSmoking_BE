package com.swp391project.SWP391_QuitSmoking_BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(Customizer.withDefaults()) // Use modern CSRF configuration
                .csrf(csrf -> csrf.disable()) // Disable CSRF for WebSocket
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/chat/**").permitAll() // Allow WebSocket endpoint
                        .anyRequest().authenticated() // Require auth for other endpoints
                );
        return http.build();
    }
}