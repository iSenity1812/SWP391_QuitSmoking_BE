package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.service.ChatConversationService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserProfile;

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
        // Thay vì cố gắng chuyển đổi UserProfile sang Optional<User>,
        // chúng ta chỉ cần kiểm tra xem UserProfile có tồn tại không và sử dụng userId
        // của nó.
        // Phương thức getConversationsByUserId trong ChatConversationService (giả định)
        // sẽ nhận UUID userId.
        UserProfile userProfile = userService.getUserById(userId);
        if (userProfile != null) {
            return chatConversationService.getConversationsByUserId(userProfile.getUserId());
        }
        return List.of();
    }

    @GetMapping
    public List<ChatConversation> getAllConversations() {
        return chatConversationService.getAllConversations();
    }
}