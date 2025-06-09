package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "ChatMessage")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ConversationID", nullable = false)
    private Integer conversationId;

    @Column(name = "SenderID", nullable = false)
    private UUID senderId;

    @Column(name = "ReceiverID", nullable = false)
    private UUID receiverId;

    @Column(name = "Content", nullable = false)
    private String content;

    @Column(name = "SentDate", nullable = false)
    private LocalDateTime sentDate = LocalDateTime.now();
}