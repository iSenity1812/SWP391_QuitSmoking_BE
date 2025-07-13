package com.swp391project.SWP391_QuitSmoking_BE.enums;

public enum LeaderboardType {
    MONEY_SAVED("Tiền tiết kiệm"),
    DAYS_QUIT("Số ngày bỏ thuốc"),
    CIGARETTES_AVOIDED("Số điếu đã tránh hút"),
    ACHIEVEMENT_COUNT("Số thành tựu");
    
    private final String displayName;
    
    LeaderboardType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
} 