package com.swp391.chat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_conversation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conversation_id")
    private Integer conversationId;

    @Column(name = "initiator_id", nullable = false)
    private UUID initiatorId;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_message_date")
    private LocalDateTime lastMessageDate;
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private ChatConversation conversation = new ChatConversation();
        
        public Builder conversationId(Integer conversationId) {
            conversation.conversationId = conversationId;
            return this;
        }
        
        public Builder initiatorId(UUID initiatorId) {
            conversation.initiatorId = initiatorId;
            return this;
        }
        
        public Builder recipientId(UUID recipientId) {
            conversation.recipientId = recipientId;
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            conversation.createdAt = createdAt;
            return this;
        }
        
        public Builder lastMessageDate(LocalDateTime lastMessageDate) {
            conversation.lastMessageDate = lastMessageDate;
            return this;
        }
        
        public ChatConversation build() {
            return conversation;
        }
    }
    
    // Getters and setters
    public Integer getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }
    
    public UUID getInitiatorId() {
        return initiatorId;
    }
    
    public void setInitiatorId(UUID initiatorId) {
        this.initiatorId = initiatorId;
    }
    
    public UUID getRecipientId() {
        return recipientId;
    }
    
    public void setRecipientId(UUID recipientId) {
        this.recipientId = recipientId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getLastMessageDate() {
        return lastMessageDate;
    }
    
    public void setLastMessageDate(LocalDateTime lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
} 