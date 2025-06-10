package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlackList, UUID> {
    Optional<TokenBlackList> findByJti(String jti);
    boolean existsByJti(String jti);
    void deleteAllByExpirationTimeBefore(Instant now);
}
