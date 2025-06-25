package com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableScheduleSearchRequestDTO {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate; // Ngày bắt đầu (bắt buộc)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;   // Ngày kết thúc (tùy chọn, mặc định là startDate nếu không có)

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime startTime; // Giờ bắt đầu của Timeslot (bắt buộc)
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime endTime;   // Giờ kết thúc của Timeslot (bắt buộc)
}
