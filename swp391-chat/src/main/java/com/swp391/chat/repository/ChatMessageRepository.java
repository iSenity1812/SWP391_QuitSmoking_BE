package com.swp391.chat.repository;

import com.swp391.chat.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    List<ChatMessage> findByConversationIdOrderBySentDateAsc(Integer conversationId);
    
    List<ChatMessage> findByConversationIdOrderBySentDateDesc(Integer conversationId, Pageable pageable);
    
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(m.senderId = :user1Id AND m.receiverId = :user2Id) OR " +
           "(m.senderId = :user2Id AND m.receiverId = :user1Id) " +
           "ORDER BY m.sentDate DESC")
    List<ChatMessage> findMessagesBetweenUsers(
            @Param("user1Id") UUID user1Id, 
            @Param("user2Id") UUID user2Id, 
            Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversationId = :conversationId")
    long countByConversationId(@Param("conversationId") Integer conversationId);
} 