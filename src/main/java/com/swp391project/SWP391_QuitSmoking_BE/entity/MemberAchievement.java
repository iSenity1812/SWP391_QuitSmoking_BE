package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MemberAchievement")
public class MemberAchievement {

    @EmbeddedId
    private MemberAchievementId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("achievementId")
    @JoinColumn(name = "AchievementID", referencedColumnName = "AchievementID", nullable = false)
    private Achievement achievement;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "MemberID", referencedColumnName = "MemberID", nullable = false)
    private Member member;

    @Column(name = "AchievedAt", nullable = false)
    private LocalDateTime achievedAt = LocalDateTime.now();

    @Column(name = "IsShared", nullable = false)
    private Boolean isShared = false;
}