package com.swp391.chat.service.impl;

import com.swp391.chat.model.ChatConversation;
import com.swp391.chat.model.ChatMessage;
import com.swp391.chat.model.User;
import com.swp391.chat.model.dto.ChatConversationDTO;
import com.swp391.chat.model.dto.ChatMessageDTO;
import com.swp391.chat.repository.ChatConversationRepository;
import com.swp391.chat.repository.ChatMessageRepository;
import com.swp391.chat.repository.UserRepository;
import com.swp391.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Override
    public List<ChatConversationDTO> getUserConversations(UUID userId) {
        List<ChatConversation> conversations = conversationRepository.findAllConversationsByUser(userId);
        
        return conversations.stream()
                .map(this::convertToConversationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageDTO> getConversationMessages(Integer conversationId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentDate"));
        List<ChatMessage> messages = messageRepository.findByConversationIdOrderBySentDateDesc(conversationId, pageable);
        
        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatConversationDTO createConversation(UUID initiatorId, UUID recipientId) {
        // Kiểm tra nếu cuộc trò chuyện đã tồn tại
        Optional<ChatConversation> existingConversation = 
                conversationRepository.findConversationBetweenUsers(initiatorId, recipientId);
        
        if (existingConversation.isPresent()) {
            return convertToConversationDTO(existingConversation.get());
        }
        
        // Tạo cuộc trò chuyện mới
        ChatConversation conversation = ChatConversation.builder()
                .initiatorId(initiatorId)
                .recipientId(recipientId)
                .createdAt(LocalDateTime.now())
                .lastMessageDate(LocalDateTime.now())
                .build();
        
        ChatConversation savedConversation = conversationRepository.save(conversation);
        return convertToConversationDTO(savedConversation);
    }

    @Override
    public ChatConversationDTO findConversationBetweenUsers(UUID user1Id, UUID user2Id) {
        Optional<ChatConversation> conversation = 
                conversationRepository.findConversationBetweenUsers(user1Id, user2Id);
        
        return conversation.map(this::convertToConversationDTO).orElse(null);
    }

    @Override
    @Transactional
    public ChatMessageDTO saveMessage(ChatMessageDTO messageDTO) {
        // Tìm cuộc trò chuyện hoặc tạo mới nếu chưa tồn tại
        ChatConversation conversation;
        
        if (messageDTO.getConversationId() != null) {
            conversation = conversationRepository.findById(messageDTO.getConversationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc trò chuyện"));
        } else {
            Optional<ChatConversation> existingConversation = 
                    conversationRepository.findConversationBetweenUsers(
                            messageDTO.getSenderId(), messageDTO.getReceiverId());
            
            if (existingConversation.isPresent()) {
                conversation = existingConversation.get();
            } else {
                // Tạo cuộc trò chuyện mới
                conversation = ChatConversation.builder()
                        .initiatorId(messageDTO.getSenderId())
                        .recipientId(messageDTO.getReceiverId())
                        .createdAt(LocalDateTime.now())
                        .lastMessageDate(LocalDateTime.now())
                        .build();
                conversation = conversationRepository.save(conversation);
            }
        }
        
        // Cập nhật thời gian tin nhắn cuối cùng
        conversation.setLastMessageDate(LocalDateTime.now());
        conversationRepository.save(conversation);
        
        // Tạo và lưu tin nhắn mới
        ChatMessage chatMessage = ChatMessage.builder()
                .conversationId(conversation.getConversationId())
                .senderId(messageDTO.getSenderId())
                .receiverId(messageDTO.getReceiverId())
                .content(messageDTO.getContent())
                .sentDate(LocalDateTime.now())
                .build();
        
        ChatMessage savedMessage = messageRepository.save(chatMessage);
        
        // Gửi thông báo tới người nhận
        ChatMessageDTO savedMessageDTO = convertToMessageDTO(savedMessage);
        messagingTemplate.convertAndSendToUser(
                messageDTO.getReceiverId().toString(),
                "/queue/messages",
                savedMessageDTO
        );
        
        return savedMessageDTO;
    }

    @Override
    public long countMessagesByConversation(Integer conversationId) {
        return messageRepository.countByConversationId(conversationId);
    }
    
    // Phương thức chuyển đổi từ entity sang DTO
    private ChatConversationDTO convertToConversationDTO(ChatConversation conversation) {
        User initiator = userRepository.findById(conversation.getInitiatorId()).orElse(null);
        User recipient = userRepository.findById(conversation.getRecipientId()).orElse(null);
        
        String initiatorUsername = initiator != null ? initiator.getUsername() : "";
        String recipientUsername = recipient != null ? recipient.getUsername() : "";
        
        String initiatorProfilePicture = initiator != null ? initiator.getProfilePicture() : "";
        String recipientProfilePicture = recipient != null ? recipient.getProfilePicture() : "";
        
        // Lấy tin nhắn cuối cùng
        String lastMessageContent = "";
        List<ChatMessage> lastMessages = messageRepository.findByConversationIdOrderBySentDateDesc(
                conversation.getConversationId(), PageRequest.of(0, 1));
        
        if (!lastMessages.isEmpty()) {
            lastMessageContent = lastMessages.get(0).getContent();
        }
        
        return ChatConversationDTO.builder()
                .conversationId(conversation.getConversationId())
                .initiatorId(conversation.getInitiatorId())
                .recipientId(conversation.getRecipientId())
                .initiatorUsername(initiatorUsername)
                .recipientUsername(recipientUsername)
                .initiatorProfilePicture(initiatorProfilePicture)
                .recipientProfilePicture(recipientProfilePicture)
                .createdAt(conversation.getCreatedAt())
                .lastMessageDate(conversation.getLastMessageDate())
                .lastMessageContent(lastMessageContent)
                .build();
    }
    
    private ChatMessageDTO convertToMessageDTO(ChatMessage message) {
        User sender = userRepository.findById(message.getSenderId()).orElse(null);
        User receiver = userRepository.findById(message.getReceiverId()).orElse(null);
        
        String senderUsername = sender != null ? sender.getUsername() : "";
        String receiverUsername = receiver != null ? receiver.getUsername() : "";
        
        return ChatMessageDTO.builder()
                .messageId(message.getMessageId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .senderUsername(senderUsername)
                .receiverUsername(receiverUsername)
                .content(message.getContent())
                .sentDate(message.getSentDate())
                .messageType("MESSAGE")
                .build();
    }
} 