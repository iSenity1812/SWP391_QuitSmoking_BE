package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ChatAttachment")
public class ChatAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AttachmentID")
    private Integer attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MessageID", referencedColumnName = "MessageID", nullable = false)
    private ChatMessage message;

    @Column(name = "FileURL", nullable = false, length = 255)
    private String fileUrl;

    // Getters and setters
    public Integer getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Integer attachmentId) {
        this.attachmentId = attachmentId;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}