package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatAttachment;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatMessage;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatAttachmentService {
    @Autowired
    private ChatAttachmentRepository chatAttachmentRepository;

    public ChatAttachment saveAttachment(ChatAttachment attachment) {
        return chatAttachmentRepository.save(attachment);
    }

    public List<ChatAttachment> getAttachmentsByMessage(ChatMessage message) {
        return chatAttachmentRepository.findAll().stream()
                .filter(a -> a.getMessage().equals(message))
                .toList();
    }
}