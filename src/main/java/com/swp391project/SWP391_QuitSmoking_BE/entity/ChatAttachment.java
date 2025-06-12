package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ChatAttachment")
@Data
public class ChatAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int attachmentId;

    @Column(nullable = false)
    private int messageId;

    @Column(nullable = false, length = 255)
    private String fileUrl;
}