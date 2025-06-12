package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Integer> {
    List<ChatConversation> findByInitiatorIdOrRecipientId(UUID initiatorId, UUID recipientId);
}