package com.swp391project.SWP391_QuitSmoking_BE.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class UserOauthAccountId implements Serializable {
    private UUID userId;
    private String oauthProvider;
    private String oauthId;

    // equals and hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserOauthAccountId that = (UserOauthAccountId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(oauthProvider, that.oauthProvider) &&
                Objects.equals(oauthId, that.oauthId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, oauthProvider, oauthId);
    }
}
