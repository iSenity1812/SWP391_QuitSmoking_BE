package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "token_blacklist", indexes = @Index(name = "idx_jti", columnList = "jti"))
public class TokenBlackList {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "jti", nullable = false, unique = true)
    private String jti; // JWT ID, used to uniquely identify the token

    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime; // Expiration time of the token in milliseconds since epoch

    public TokenBlackList(String jti, Instant expirationTime) {
        this.jti = jti;
        this.expirationTime = expirationTime;
    }
}
