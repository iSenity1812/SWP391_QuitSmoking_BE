package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "ChatConversation")
public class ChatConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "InitiatorID", nullable = false)
    private UUID initiatorId;

    @Column(name = "RecipientID", nullable = false)
    private UUID recipientId;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "LastMessageDate")
    private LocalDateTime lastMessageDate;
}