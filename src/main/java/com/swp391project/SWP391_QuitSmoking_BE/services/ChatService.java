package com.swp391project.SWP391_QuitSmoking_BE.services;

import com.swp391project.SWP391_QuitSmoking_BE.dto.ChatMessageDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatMessage;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatConversationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatService {

    @Autowired
    private ChatConversationRepository conversationRepository;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Transactional
    public ChatMessage saveMessage(ChatMessageDTO messageDTO) {
        Optional<ChatConversation> conversationOpt = conversationRepository
                .findByInitiatorIdAndRecipientIdOrRecipientIdAndInitiatorId(
                        messageDTO.getSenderId(), messageDTO.getReceiverId(),
                        messageDTO.getReceiverId(), messageDTO.getSenderId());

        ChatConversation conversation;
        if (conversationOpt.isPresent()) {
            conversation = conversationOpt.get();
        } else {
            conversation = new ChatConversation();
            conversation.setInitiatorId(messageDTO.getSenderId());
            conversation.setRecipientId(messageDTO.getReceiverId());
            conversation.setCreatedAt(LocalDateTime.now());
            conversation = conversationRepository.save(conversation);
        }

        conversation.setLastMessageDate(LocalDateTime.now());
        conversationRepository.save(conversation);

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderId(messageDTO.getSenderId());
        message.setReceiverId(messageDTO.getReceiverId());
        message.setContent(messageDTO.getContent());
        message.setSentDate(LocalDateTime.now());

        return messageRepository.save(message);
    }
}