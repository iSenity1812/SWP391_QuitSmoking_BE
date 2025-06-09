package com.swp391project.SWP391_QuitSmoking_BE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationDTO {
    private Integer conversationId;
    private UUID initiatorId;
    private UUID recipientId;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageDate;
    private String lastMessage;
    private UUID lastMessageSenderId;
    private String otherUserUsername;
    private String otherUserProfilePicture;
    private Integer otherUserRoleId;
    private String otherUserRoleName;
}
