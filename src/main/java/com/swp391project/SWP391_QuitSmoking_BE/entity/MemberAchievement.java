package com.swp391project.SWP391_QuitSmoking_BE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "MemberAchievement")
@IdClass(MemberAchievementId.class)
public class MemberAchievement {
    @Id
    @Column(name = "AchievementID", nullable = false)
    private Integer achievementId;

    @Id
    @Column(name = "MemberID", nullable = false, columnDefinition = "uuid")
    private UUID memberId;

    @Column(name = "AchievedDate")
    private LocalDate achievedDate = LocalDate.now();

    @Column(name = "IsShared", nullable = false)
    private boolean isShared = false;

    public void setAchievementId(Integer achievementId) {
        this.achievementId = achievementId;
    }

    public void setMemberId(java.util.UUID memberId) {
        this.memberId = memberId;
    }

    public void setAchievedDate(java.time.LocalDate achievedDate) {
        this.achievedDate = achievedDate;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }
}