package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.GoogleOAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoogleOAuthAccountRepository extends JpaRepository<GoogleOAuthAccount, UUID> {
    
    /**
     * Tìm Google OAuth account theo Google ID
     */
    Optional<GoogleOAuthAccount> findByGoogleId(String googleId);
    
    /**
     * Tìm Google OAuth account theo email
     */
    Optional<GoogleOAuthAccount> findByEmail(String email);
    
    /**
     * Tìm Google OAuth account theo User ID
     */
    Optional<GoogleOAuthAccount> findByUser_UserId(UUID userId);
    
    /**
     * Kiểm tra xem Google ID đã tồn tại chưa
     */
    boolean existsByGoogleId(String googleId);
    
    /**
     * Kiểm tra xem email đã tồn tại chưa
     */
    boolean existsByEmail(String email);
    
    /**
     * Cập nhật last login time
     */
    @Modifying
    @Query("UPDATE GoogleOAuthAccount g SET g.lastLoginAt = :lastLoginAt WHERE g.id = :id")
    void updateLastLoginAt(@Param("id") UUID id, @Param("lastLoginAt") LocalDateTime lastLoginAt);
    
    /**
     * Tìm tất cả Google OAuth accounts không active
     */
    @Query("SELECT g FROM GoogleOAuthAccount g WHERE g.isActive = false")
    java.util.List<GoogleOAuthAccount> findAllInactive();
    
    /**
     * Đếm số lượng Google OAuth accounts theo email verified
     */
    long countByEmailVerified(Boolean emailVerified);
} 