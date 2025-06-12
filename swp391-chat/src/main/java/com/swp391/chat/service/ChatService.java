package com.swp391.chat.service;

import com.swp391.chat.model.dto.ChatConversationDTO;
import com.swp391.chat.model.dto.ChatMessageDTO;

import java.util.List;
import java.util.UUID;

public interface ChatService {
    
    // Lấy danh sách cuộc trò chuyện của người dùng
    List<ChatConversationDTO> getUserConversations(UUID userId);
    
    // Lấy lịch sử tin nhắn của cuộc trò chuyện
    List<ChatMessageDTO> getConversationMessages(Integer conversationId, int page, int size);
    
    // Tạo cuộc trò chuyện mới giữa hai người dùng
    ChatConversationDTO createConversation(UUID initiatorId, UUID recipientId);
    
    // Tìm cuộc trò chuyện giữa hai người dùng (nếu tồn tại)
    ChatConversationDTO findConversationBetweenUsers(UUID user1Id, UUID user2Id);
    
    // Lưu tin nhắn và cập nhật cuộc trò chuyện
    ChatMessageDTO saveMessage(ChatMessageDTO messageDTO);
    
    // Đếm tổng số tin nhắn trong cuộc trò chuyện
    long countMessagesByConversation(Integer conversationId);
} 