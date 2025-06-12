package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ChatMessage")
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @Column(nullable = false)
    private int conversationId;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(nullable = false)
    private UUID senderId;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(nullable = false)
    private UUID receiverId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime sentDate = LocalDateTime.now();
}