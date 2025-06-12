package com.swp391project.SWP391_QuitSmoking_BE.services;

import com.swp391project.SWP391_QuitSmoking_BE.dto.ChatMessageDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatMessage;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatAttachment;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatConversationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatMessageRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private ChatAttachmentRepository attachmentRepository;

    public ChatConversation createConversation(UUID initiatorId, UUID recipientId) {
        ChatConversation conversation = new ChatConversation();
        conversation.setInitiatorId(initiatorId);
        conversation.setRecipientId(recipientId);
        return conversationRepository.save(conversation);
    }

    public ChatMessage saveMessage(ChatMessageDTO messageDTO) {
        ChatMessage message = new ChatMessage();
        message.setConversationId(messageDTO.getConversationId());
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setContent(messageDTO.getContent());
        message.setSentDate(LocalDateTime.now());

        ChatMessage savedMessage = messageRepository.save(message);

        if (messageDTO.getAttachmentUrl() != null) {
            ChatAttachment attachment = new ChatAttachment();
            attachment.setMessageId(savedMessage.getMessageId());
            attachment.setFileUrl(messageDTO.getAttachmentUrl());
            attachmentRepository.save(attachment);
        }

        ChatConversation conversation = conversationRepository.findById(messageDTO.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        conversation.setLastMessageDate(LocalDateTime.now());
        conversationRepository.save(conversation);

        return savedMessage;
    }

    public List<ChatMessageDTO> getConversationMessages(int conversationId) {
        List<ChatMessage> messages = messageRepository.findByConversationId(conversationId);
        return messages.stream().map(message -> {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setMessageId(message.getMessageId());
            dto.setConversationId(message.getConversationId());
            dto.setSenderId(message.getSenderId());
            dto.setReceiverId(message.getReceiverId());
            dto.setContent(message.getContent());
            dto.setSentDate(message.getSentDate());

            ChatAttachment attachment = attachmentRepository.findByMessageId(message.getMessageId())
                    .stream().findFirst().orElse(null);
            if (attachment != null) {
                dto.setAttachmentUrl(attachment.getFileUrl());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public List<ChatConversation> getUserConversations(UUID userId) {
        return conversationRepository.findByInitiatorIdOrRecipientId(userId, userId);
    }
}