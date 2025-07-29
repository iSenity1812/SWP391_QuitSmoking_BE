package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_oauth_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserOauthAccountId.class)
@Builder
public class UserOauthAccount {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "oauth_provider", length = 50)
    private String oauthProvider;

    @Id
    @Column(name = "oauth_id", length = 255)
    private String oauthId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "UserID", insertable = false, updatable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
