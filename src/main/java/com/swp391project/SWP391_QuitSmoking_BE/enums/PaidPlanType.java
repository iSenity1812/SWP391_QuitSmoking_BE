package com.swp391project.SWP391_QuitSmoking_BE.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PaidPlanType {
    STANDARD_14D("Tiêu chuẩn 14 ngày", 14),
    POPULAR_30D("Phổ biến 30 ngày", 30),
    SUPER_90D("Cao cấp 90 ngày", 90);

    private final String displayName;
    private final int durationDays;

    PaidPlanType(String displayName, int durationDays) {
        this.displayName = displayName;
        this.durationDays = durationDays;
    }


//    @JsonValue // Đảm bảo khi serialize sang JSON sẽ dùng displayName
//    public String getDisplayName() {
//        return displayName;
//    }
//
//    @JsonCreator // Đảm bảo khi deserialize từ JSON (String) sang Enum
//    public static PaidPlanType fromDisplayName(String displayName) {
//        return Arrays.stream(PaidPlanType.values())
//                .filter(type -> type.displayName.equalsIgnoreCase(displayName))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại gói với tên hiển thị: " + displayName));
//    }
}
