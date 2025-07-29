package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.UserOauthAccount;
import com.swp391project.SWP391_QuitSmoking_BE.entity.UserOauthAccountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserOauthAccountRepository extends JpaRepository<UserOauthAccount, UserOauthAccountId> {
    Optional<UserOauthAccount> findByOauthProviderAndOauthId(String oauthProvider, String oauthId);
    Optional<UserOauthAccount> findByUserIdAndOauthProvider(UUID userId, String oauthProvider);
}
