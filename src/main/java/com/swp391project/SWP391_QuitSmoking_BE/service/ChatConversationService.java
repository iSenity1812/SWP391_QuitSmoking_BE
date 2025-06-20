package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatConversationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatConversationService {
    @Autowired
    private ChatConversationRepository chatConversationRepository;
    @Autowired
    private UserRepository userRepository;

    public ChatConversation createConversation(ChatConversation conversation) {
        return chatConversationRepository.save(conversation);
    }

    public Optional<ChatConversation> getConversationById(Integer id) {
        return chatConversationRepository.findById(id);
    }

    public List<ChatConversation> getConversationsByUserId(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        // Đã sửa: Thay findByUser bằng findByInitiatorOrRecipient
        return chatConversationRepository.findByInitiatorOrRecipient(user, user);
    }

    public List<ChatConversation> getConversationsByUser(User user) {
        // Đã sửa: Thay findByUser bằng findByInitiatorOrRecipient
        return chatConversationRepository.findByInitiatorOrRecipient(user, user);
    }

    public List<ChatConversation> getAllConversations() {
        return chatConversationRepository.findAll();
    }
}
