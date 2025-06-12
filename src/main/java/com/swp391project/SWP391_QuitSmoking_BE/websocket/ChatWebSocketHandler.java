package com.swp391project.SWP391_QuitSmoking_BE.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391project.SWP391_QuitSmoking_BE.dto.ChatMessageDTO;
import com.swp391project.SWP391_QuitSmoking_BE.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageDTO messageDTO = objectMapper.readValue(payload, ChatMessageDTO.class);

        // Save message to database
        chatService.saveMessage(messageDTO);

        // Broadcast message to recipient
        String recipientSessionId = sessions.entrySet().stream()
                .filter(entry -> entry.getValue().getAttributes().get("userId") != null &&
                        entry.getValue().getAttributes().get("userId").equals(messageDTO.getReceiverId().toString()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (recipientSessionId != null && sessions.containsKey(recipientSessionId)) {
            WebSocketSession recipientSession = sessions.get(recipientSessionId);
            recipientSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageDTO)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
    }
}