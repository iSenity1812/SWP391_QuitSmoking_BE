package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "google_oauth_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleOAuthAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;
    
    @Column(name = "google_id", nullable = false, unique = true, length = 100)
    private String googleId;
    
    @Column(name = "email", nullable = false, length = 255)
    private String email;
    
    @Column(name = "name", length = 255)
    private String name;
    
    @Column(name = "given_name", length = 100)
    private String givenName;
    
    @Column(name = "family_name", length = 100)
    private String familyName;
    
    @Column(name = "picture_url", length = 500)
    private String pictureUrl;
    
    @Column(name = "locale", length = 10)
    private String locale;
    
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    
    // Relationship với User entity
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "UserID")
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 