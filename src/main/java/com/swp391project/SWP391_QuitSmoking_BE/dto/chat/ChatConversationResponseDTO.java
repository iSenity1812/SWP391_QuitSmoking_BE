package com.swp391project.SWP391_QuitSmoking_BE.dto.chat;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationResponseDTO {

    private Long conversationId;
    private Long memberId;
    private String memberName;
    private Long coachId;
    private String coachName;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}