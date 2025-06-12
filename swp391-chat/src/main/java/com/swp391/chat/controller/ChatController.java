package com.swp391.chat.controller;

import com.swp391.chat.model.dto.ChatConversationDTO;
import com.swp391.chat.model.dto.ChatMessageDTO;
import com.swp391.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // API REST cho việc lấy cuộc trò chuyện
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ChatConversationDTO>> getUserConversations(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }

    // API REST cho việc lấy tin nhắn trong cuộc trò chuyện
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getConversationMessages(
            @PathVariable Integer conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(chatService.getConversationMessages(conversationId, page, size));
    }

    // API REST cho việc tạo cuộc trò chuyện mới
    @PostMapping("/conversations")
    public ResponseEntity<ChatConversationDTO> createConversation(
            @RequestParam UUID initiatorId,
            @RequestParam UUID recipientId) {
        return ResponseEntity.ok(chatService.createConversation(initiatorId, recipientId));
    }

    // API REST cho việc tìm cuộc trò chuyện giữa hai người dùng
    @GetMapping("/find-conversation")
    public ResponseEntity<ChatConversationDTO> findConversation(
            @RequestParam UUID user1Id,
            @RequestParam UUID user2Id) {
        ChatConversationDTO conversation = chatService.findConversationBetweenUsers(user1Id, user2Id);
        if (conversation != null) {
            return ResponseEntity.ok(conversation);
        }
        return ResponseEntity.notFound().build();
    }

    // WebSocket endpoint để gửi tin nhắn
    @MessageMapping("/chat.sendMessage")
    public ChatMessageDTO sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        log.info("Nhận tin nhắn từ: {} tới: {}", chatMessageDTO.getSenderUsername(), chatMessageDTO.getReceiverUsername());
        return chatService.saveMessage(chatMessageDTO);
    }

    // WebSocket endpoint để tham gia cuộc trò chuyện
    @MessageMapping("/chat.addUser")
    public ChatMessageDTO addUser(@Payload ChatMessageDTO chatMessageDTO, SimpMessageHeaderAccessor headerAccessor) {
        // Thêm username vào web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessageDTO.getSenderUsername());
        headerAccessor.getSessionAttributes().put("userId", chatMessageDTO.getSenderId());
        
        log.info("Người dùng tham gia: {}", chatMessageDTO.getSenderUsername());
        return chatMessageDTO;
    }
} 