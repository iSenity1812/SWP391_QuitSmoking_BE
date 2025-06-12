package com.swp391.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Value("${spring.websocket.path:/ws}")
    private String webSocketPath;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đăng ký endpoint WebSocket và cho phép CORS
        registry.addEndpoint(webSocketPath)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Định nghĩa prefix cho các message broker
        // Người dùng sẽ subscribe vào các kênh với prefix /topic hoặc /queue
        registry.enableSimpleBroker("/topic", "/queue", "/user");
        
        // Prefix cho các message từ client
        registry.setApplicationDestinationPrefixes("/app");
        
        // Prefix cho tin nhắn riêng
        registry.setUserDestinationPrefix("/user");
    }
} 