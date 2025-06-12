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
public class ChatMessageDTO {
    private Integer messageId;
    private Integer conversationId;
    private UUID senderId;
    private UUID receiverId;
    private String senderUsername;
    private String receiverUsername;
    private String content;
    private LocalDateTime sentDate;
    private String messageType; // MESSAGE, JOIN, LEAVE
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ChatMessageDTO dto = new ChatMessageDTO();
        
        public Builder messageId(Integer messageId) {
            dto.messageId = messageId;
            return this;
        }
        
        public Builder conversationId(Integer conversationId) {
            dto.conversationId = conversationId;
            return this;
        }
        
        public Builder senderId(UUID senderId) {
            dto.senderId = senderId;
            return this;
        }
        
        public Builder receiverId(UUID receiverId) {
            dto.receiverId = receiverId;
            return this;
        }
        
        public Builder senderUsername(String senderUsername) {
            dto.senderUsername = senderUsername;
            return this;
        }
        
        public Builder receiverUsername(String receiverUsername) {
            dto.receiverUsername = receiverUsername;
            return this;
        }
        
        public Builder content(String content) {
            dto.content = content;
            return this;
        }
        
        public Builder sentDate(LocalDateTime sentDate) {
            dto.sentDate = sentDate;
            return this;
        }
        
        public Builder messageType(String messageType) {
            dto.messageType = messageType;
            return this;
        }
        
        public ChatMessageDTO build() {
            return dto;
        }
    }
    
    // Getters and setters
    public Integer getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }
    
    public Integer getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }
    
    public UUID getSenderId() {
        return senderId;
    }
    
    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
    
    public UUID getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getSenderUsername() {
        return senderUsername;
    }
    
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
    
    public String getReceiverUsername() {
        return receiverUsername;
    }
    
    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getSentDate() {
        return sentDate;
    }
    
    public void setSentDate(LocalDateTime sentDate) {
        this.sentDate = sentDate;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}