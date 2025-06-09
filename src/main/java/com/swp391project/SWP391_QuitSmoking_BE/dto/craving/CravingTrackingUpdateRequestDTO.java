package com.swp391project.SWP391_QuitSmoking_BE.dto.craving;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CravingTrackingUpdateRequestDTO {
    @NotNull(message = "ID theo dõi cơn thèm không được để trống")
    private Integer cravingTrackingId;

    private LocalDateTime trackTime;
    private Integer smokedCount;
    private Integer cravingsCount;
    private Situation situation;
    private WithWhom withWhom;
}
