package com.swp391.chat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_message")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @Column(name = "conversation_id", nullable = false)
    private Integer conversationId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "sent_date", nullable = false)
    private LocalDateTime sentDate;
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ChatMessage message = new ChatMessage();
        
        public Builder messageId(Integer messageId) {
            message.messageId = messageId;
            return this;
        }
        
        public Builder conversationId(Integer conversationId) {
            message.conversationId = conversationId;
            return this;
        }
        
        public Builder senderId(UUID senderId) {
            message.senderId = senderId;
            return this;
        }
        
        public Builder receiverId(UUID receiverId) {
            message.receiverId = receiverId;
            return this;
        }
        
        public Builder content(String content) {
            message.content = content;
            return this;
        }
        
        public Builder sentDate(LocalDateTime sentDate) {
            message.sentDate = sentDate;
            return this;
        }
        
        public ChatMessage build() {
            return message;
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
} 