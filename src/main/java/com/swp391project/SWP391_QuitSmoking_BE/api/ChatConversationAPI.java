package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.service.ChatConversationService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/conversations")
public class ChatConversationAPI {
    @Autowired
    private ChatConversationService chatConversationService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ChatConversation createConversation(@RequestBody ChatConversation conversation) {
        return chatConversationService.createConversation(conversation);
    }

    @GetMapping("/{id}")
    public Optional<ChatConversation> getConversationById(@PathVariable Integer id) {
        return chatConversationService.getConversationById(id);
    }

    @GetMapping("/user/{userId}")
    public List<ChatConversation> getConversationsByUser(@PathVariable UUID userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(chatConversationService::getConversationsByUser).orElse(List.of());
    }

    @GetMapping
    public List<ChatConversation> getAllConversations() {
        return chatConversationService.getAllConversations();
    }
}