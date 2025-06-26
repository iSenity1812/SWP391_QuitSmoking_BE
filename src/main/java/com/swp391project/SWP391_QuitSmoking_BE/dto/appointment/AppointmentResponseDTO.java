package com.swp391project.SWP391_QuitSmoking_BE.dto.appointment;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {
    private Long appointmentId;
    private UserSimpleResponseDTO member; // Thông tin Member đơn giản
    private CoachScheduleResponseDTO coachSchedule; // Toàn bộ thông tin CoachSchedule
    private AppointmentStatus status;
    private String note;
    private LocalDateTime bookingTime;
    // private LocalDateTime createdAt;
    // private LocalDateTime updatedAt;

    // Additional getter methods for compatibility
    public CoachScheduleResponseDTO getCoachSchedule() {
        return this.coachSchedule;
    }

    public void setCoachSchedule(CoachScheduleResponseDTO coachSchedule) {
        this.coachSchedule = coachSchedule;
    }
}
