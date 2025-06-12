package com.swp391.chat.config;

import com.swp391.chat.model.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Nhận kết nối WebSocket mới");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        UUID userId = (UUID) headerAccessor.getSessionAttributes().get("userId");
        
        if (Objects.nonNull(username)) {
            log.info("Người dùng ngắt kết nối: {}", username);
            
            // Tạo thông báo người dùng rời đi
            ChatMessageDTO chatMessage = ChatMessageDTO.builder()
                    .messageType("LEAVE")
                    .senderId(userId)
                    .senderUsername(username)
                    .content(username + " đã ngắt kết nối")
                    .build();
            
            // Gửi thông báo cho tất cả người dùng
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
} 