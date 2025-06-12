package com.swp391.chat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "chat_attachment")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Integer attachmentId;

    @Column(name = "message_id", nullable = false)
    private Integer messageId;

    @Column(name = "file_url", nullable = false, length = 255)
    private String fileUrl;
} 