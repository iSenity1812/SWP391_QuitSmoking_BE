package com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule;

import com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot.TimeSlotResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachScheduleResponseDTO {
    private Long scheduleId;
    private CoachSimpleResponseDTO coach; // Thông tin Coach đơn giản
    private TimeSlotResponseDTO timeSlot; // Thông tin TimeSlot
    private LocalDate scheduleDate;
    private boolean isBooked;
}