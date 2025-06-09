package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ChatAttachment")
public class ChatAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "MessageID", nullable = false)
    private Integer messageId;

    @Column(name = "FileURL", nullable = false)
    private String fileUrl;
}