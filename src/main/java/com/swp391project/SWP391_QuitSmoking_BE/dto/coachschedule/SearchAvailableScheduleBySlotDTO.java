package com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class SearchAvailableScheduleBySlotDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate; // Ngày bắt đầu tìm kiếm (YYYY-MM-DD)

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;   // Ngày kết thúc tìm kiếm (YYYY-MM-DD)

    private List<Integer> timeSlotIds; // Danh sách ID của các TimeSlot cần tìm kiếm
}
