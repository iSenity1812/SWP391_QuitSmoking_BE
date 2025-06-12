package com.swp391.chat.repository;

import com.swp391.chat.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Integer> {
    
    @Query("SELECT c FROM ChatConversation c WHERE " +
           "(c.initiatorId = :userId OR c.recipientId = :userId) " +
           "ORDER BY c.lastMessageDate DESC")
    List<ChatConversation> findAllConversationsByUser(@Param("userId") UUID userId);
    
    @Query("SELECT c FROM ChatConversation c WHERE " +
           "(c.initiatorId = :user1Id AND c.recipientId = :user2Id) OR " +
           "(c.initiatorId = :user2Id AND c.recipientId = :user1Id)")
    Optional<ChatConversation> findConversationBetweenUsers(
            @Param("user1Id") UUID user1Id, 
            @Param("user2Id") UUID user2Id);
} 