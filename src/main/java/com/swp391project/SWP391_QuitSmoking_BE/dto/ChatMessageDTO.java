package com.swp391project.SWP391_QuitSmoking_BE.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ChatMessageDTO {
    private int messageId;
    private int conversationId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private LocalDateTime sentDate;
    private String attachmentUrl;
}