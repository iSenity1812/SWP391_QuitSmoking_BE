package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatConversation;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Integer> {
    // Tìm các cuộc trò chuyện mà người dùng là Initiator HOẶC Recipient
    // Phương thức này sẽ tìm các cuộc trò chuyện mà tham số initiator trùng với trường initiator HOẶC tham số recipient trùng với trường recipient.
    List<ChatConversation> findByInitiatorOrRecipient(User initiator, User recipient);
}
