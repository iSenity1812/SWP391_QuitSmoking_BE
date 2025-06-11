package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatMessage;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage sendMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public Optional<ChatMessage> getMessageById(Integer id) {
        return chatMessageRepository.findById(id);
    }

    public List<ChatMessage> getMessagesByConversation(ChatConversation conversation) {
        return chatMessageRepository.findAll().stream()
                .filter(m -> m.getConversation().equals(conversation))
                .toList();
    }
}