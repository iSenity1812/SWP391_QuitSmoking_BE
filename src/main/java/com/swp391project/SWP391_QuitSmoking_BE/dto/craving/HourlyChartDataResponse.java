package com.swp391project.SWP391_QuitSmoking_BE.dto.craving;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class HourlyChartDataResponse {
    private int hour; // Giờ trong ngày (0-23)
    private int smokedCount; // Tổng số điếu thuốc đã hút trong giờ đó
    private int cravingCount; // Tổng số lần thèm thuốc trong giờ đó

    // Các tình huống, người đi cùng liên quan đến giờ đó
    private Set<Situation> situations = new HashSet<>();
    private Set<WithWhom> withWhoms = new HashSet<>();

    // Constructor để khởi tạo khi không có situations/withWhoms
    public HourlyChartDataResponse(int hour, int smokedCount, int cravingCount) {
        this.hour = hour;
        this.smokedCount = smokedCount;
        this.cravingCount = cravingCount;
        this.situations = new HashSet<>();
        this.withWhoms = new HashSet<>();
    }
}
