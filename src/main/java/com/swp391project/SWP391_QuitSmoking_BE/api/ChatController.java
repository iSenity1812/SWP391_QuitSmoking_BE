package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.ChatMessageDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/conversation")
    public ResponseEntity<ChatConversation> createConversation(@RequestParam UUID initiatorId,
                                                               @RequestParam UUID recipientId) {
        ChatConversation conversation = chatService.createConversation(initiatorId, recipientId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ChatConversation>> getUserConversations(@PathVariable UUID userId) {
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }

    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<List<ChatMessageDTO>> getConversationMessages(@PathVariable int conversationId) {
        return ResponseEntity.ok(chatService.getConversationMessages(conversationId));
    }
}