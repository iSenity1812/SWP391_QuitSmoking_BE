package com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule;

import com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot.RegisteredSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyScheduleResponseDTO {
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private List<RegisteredSlotDTO> registeredSlots;
}
