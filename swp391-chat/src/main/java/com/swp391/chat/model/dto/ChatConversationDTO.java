package com.swp391.chat.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationDTO {
    private Integer conversationId;
    private UUID initiatorId;
    private UUID recipientId;
    private String initiatorUsername;
    private String recipientUsername;
    private String initiatorProfilePicture;
    private String recipientProfilePicture;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageDate;
    private String lastMessageContent;
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ChatConversationDTO dto = new ChatConversationDTO();
        
        public Builder conversationId(Integer conversationId) {
            dto.conversationId = conversationId;
            return this;
        }
        
        public Builder initiatorId(UUID initiatorId) {
            dto.initiatorId = initiatorId;
            return this;
        }
        
        public Builder recipientId(UUID recipientId) {
            dto.recipientId = recipientId;
            return this;
        }
        
        public Builder initiatorUsername(String initiatorUsername) {
            dto.initiatorUsername = initiatorUsername;
            return this;
        }
        
        public Builder recipientUsername(String recipientUsername) {
            dto.recipientUsername = recipientUsername;
            return this;
        }
        
        public Builder initiatorProfilePicture(String initiatorProfilePicture) {
            dto.initiatorProfilePicture = initiatorProfilePicture;
            return this;
        }
        
        public Builder recipientProfilePicture(String recipientProfilePicture) {
            dto.recipientProfilePicture = recipientProfilePicture;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            dto.createdAt = createdAt;
            return this;
        }
        
        public Builder lastMessageDate(LocalDateTime lastMessageDate) {
            dto.lastMessageDate = lastMessageDate;
            return this;
        }
        
        public Builder lastMessageContent(String lastMessageContent) {
            dto.lastMessageContent = lastMessageContent;
            return this;
        }
        
        public ChatConversationDTO build() {
            return dto;
        }
    }

    /**
     * @return Integer return the conversationId
     */
    public Integer getConversationId() {
        return conversationId;
    }

    /**
     * @param conversationId the conversationId to set
     */
    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * @return UUID return the initiatorId
     */
    public UUID getInitiatorId() {
        return initiatorId;
    }

    /**
     * @param initiatorId the initiatorId to set
     */
    public void setInitiatorId(UUID initiatorId) {
        this.initiatorId = initiatorId;
    }

    /**
     * @return UUID return the recipientId
     */
    public UUID getRecipientId() {
        return recipientId;
    }

    /**
     * @param recipientId the recipientId to set
     */
    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * @return String return the initiatorUsername
     */
    public String getInitiatorUsername() {
        return initiatorUsername;
    }

    /**
     * @param initiatorUsername the initiatorUsername to set
     */
    public void setInitiatorUsername(String initiatorUsername) {
        this.initiatorUsername = initiatorUsername;
    }

    /**
     * @return String return the recipientUsername
     */
    public String getRecipientUsername() {
        return recipientUsername;
    }

    /**
     * @param recipientUsername the recipientUsername to set
     */
    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    /**
     * @return String return the initiatorProfilePicture
     */
    public String getInitiatorProfilePicture() {
        return initiatorProfilePicture;
    }

    /**
     * @param initiatorProfilePicture the initiatorProfilePicture to set
     */
    public void setInitiatorProfilePicture(String initiatorProfilePicture) {
        this.initiatorProfilePicture = initiatorProfilePicture;
    }

    /**
     * @return String return the recipientProfilePicture
     */
    public String getRecipientProfilePicture() {
        return recipientProfilePicture;
    }

    /**
     * @param recipientProfilePicture the recipientProfilePicture to set
     */
    public void setRecipientProfilePicture(String recipientProfilePicture) {
        this.recipientProfilePicture = recipientProfilePicture;
    }

    /**
     * @return LocalDateTime return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return LocalDateTime return the lastMessageDate
     */
    public LocalDateTime getLastMessageDate() {
        return lastMessageDate;
    }

    /**
     * @param lastMessageDate the lastMessageDate to set
     */
    public void setLastMessageDate(LocalDateTime lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    /**
     * @return String return the lastMessageContent
     */
    public String getLastMessageContent() {
        return lastMessageContent;
    }

    /**
     * @param lastMessageContent the lastMessageContent to set
     */
    public void setLastMessageContent(String lastMessageContent) {
        this.lastMessageContent = lastMessageContent;
    }

}