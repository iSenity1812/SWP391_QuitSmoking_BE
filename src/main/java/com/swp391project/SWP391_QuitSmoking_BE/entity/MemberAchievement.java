package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MemberAchievement")
public class MemberAchievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MemberAchievementID")
    private Long memberAchievementId;

    @Column(name = "MemberID", nullable = false)
    private UUID memberId;

    @Column(name = "AchievementID", nullable = false)
    private Long achievementId;

    @Column(name = "IsShared", nullable = false)
    private boolean isShared = false;

    @Column(name = "date_achieved", nullable = false)
    private LocalDateTime dateAchieved;

    // Relationships for convenience
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MemberID", insertable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AchievementID", insertable = false, updatable = false)
    private Achievement achievement;
} 