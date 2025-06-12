package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ChatConversation")
@Data
public class ChatConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int conversationId;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(nullable = false)
    private UUID initiatorId;

    @JdbcTypeCode(SqlTypes.UUID)
    @Column(nullable = false)
    private UUID recipientId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime lastMessageDate;
}