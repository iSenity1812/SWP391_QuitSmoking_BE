package com.swp391project.SWP391_QuitSmoking_BE.enums;


public enum CoachSpecialty {
    BEHAVIORAL_THERAPY("Tư vấn hành vi"),
    NRT_GUIDANCE("Hướng dẫn liệu pháp thay thế Nicotine"),
    STRESS_MANAGEMENT("Quản lý căng thẳng"),
    RELAPSE_PREVENTION("Phòng ngừa tái nghiện"),
    MOTIVATION_MINDSET("Động lực và Tư duy"),
    VAPING_CESSATION("Hỗ trợ cai thuốc lá điện tử"),
    YOUTH_CESSATION("Hỗ trợ thanh thiếu niên cai thuốc");

    private final String displayName;

    CoachSpecialty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
