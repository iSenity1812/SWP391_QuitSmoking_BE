package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Integer> {
    Optional<ChatConversation> findByInitiatorIdAndRecipientIdOrRecipientIdAndInitiatorId(
            UUID user1, UUID user2, UUID user2Again, UUID user1Again);
}