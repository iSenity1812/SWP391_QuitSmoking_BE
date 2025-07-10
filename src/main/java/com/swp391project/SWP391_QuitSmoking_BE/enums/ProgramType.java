package com.swp391project.SWP391_QuitSmoking_BE.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ProgramType {
    BEGINNER("BEGINNER", "Người mới bắt đầu"),
    INTERMEDIATE("INTERMEDIATE", "Trung cấp"),
    ADVANCED("ADVANCED", "Nâng cao"),
    MEDITATION("MEDITATION", "Thiền định"),
    EXERCISE("EXERCISE", "Tập thể dục"),
    NUTRITION("NUTRITION", "Dinh dưỡng"),
    PSYCHOLOGY("PSYCHOLOGY", "Tâm lý học"),
    SUPPORT_GROUP("SUPPORT_GROUP", "Nhóm hỗ trợ"),
    EDUCATIONAL("EDUCATIONAL", "Giáo dục"),
    MOTIVATIONAL("MOTIVATIONAL", "Động lực");

    private final String value;
    private final String displayName;

    ProgramType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    // Phương thức để chuyển đổi từ string sang enum
    public static ProgramType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        for (ProgramType type : ProgramType.values()) {
            if (type.value.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
