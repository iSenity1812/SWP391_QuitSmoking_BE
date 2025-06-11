package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatConversationService {
    @Autowired
    private ChatConversationRepository chatConversationRepository;

    public ChatConversation createConversation(ChatConversation conversation) {
        return chatConversationRepository.save(conversation);
    }

    public Optional<ChatConversation> getConversationById(Integer id) {
        return chatConversationRepository.findById(id);
    }

    public List<ChatConversation> getConversationsByUser(User user) {
        return chatConversationRepository.findAll().stream()
                .filter(c -> c.getInitiator().equals(user) || c.getRecipient().equals(user))
                .toList();
    }

    public List<ChatConversation> getAllConversations() {
        return chatConversationRepository.findAll();
    }
}