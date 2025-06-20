package com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponseDTO {
    private Integer timeSlotId;
    private String label;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean isDeleted;
}
