package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.ChatAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment, Integer> {
    List<ChatAttachment> findByMessageId(int messageId);
}