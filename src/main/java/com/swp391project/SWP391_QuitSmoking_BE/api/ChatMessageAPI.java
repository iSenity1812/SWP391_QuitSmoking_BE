package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatMessage;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.service.ChatMessageService;
import com.swp391project.SWP391_QuitSmoking_BE.service.ChatConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat/messages")
public class ChatMessageAPI {
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private ChatConversationService chatConversationService;

    @PostMapping
    public ChatMessage sendMessage(@RequestBody ChatMessage message) {
        return chatMessageService.sendMessage(message);
    }

    @GetMapping("/{id}")
    public Optional<ChatMessage> getMessageById(@PathVariable Integer id) {
        return chatMessageService.getMessageById(id);
    }

    @GetMapping("/conversation/{conversationId}")
    public List<ChatMessage> getMessagesByConversation(@PathVariable Integer conversationId) {
        Optional<ChatConversation> conversation = chatConversationService.getConversationById(conversationId);
        return conversation.map(chatMessageService::getMessagesByConversation).orElse(List.of());
    }
}