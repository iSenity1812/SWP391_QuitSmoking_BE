package com.swp391project.SWP391_QuitSmoking_BE.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class MemberAchievementId implements Serializable {
    private Integer achievementId;
    private UUID memberId;

    public MemberAchievementId() {
    }

    public MemberAchievementId(Integer achievementId, UUID memberId) {
        this.achievementId = achievementId;
        this.memberId = memberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MemberAchievementId that = (MemberAchievementId) o;
        return Objects.equals(achievementId, that.achievementId) && Objects.equals(memberId, that.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(achievementId, memberId);
    }
}