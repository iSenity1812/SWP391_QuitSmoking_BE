package com.swp391project.SWP391_QuitSmoking_BE.dto.craving;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CravingTrackingCreateRequestDTO {
    @NotNull(message = "Thời gian theo dõi không được để trống")
    @PastOrPresent(message = "Thời gian theo dõi không thể ở tương lai")
    private LocalDateTime trackTime;

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    @NotNull(message = "Số lượng thuốc đã hút không được để trống")
    private Integer smokedCount;

    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    @NotNull(message = "Số lần thèm thuốc không được để trống")
    private Integer cravingsCount;

    private Situation situation;
    private WithWhom withWhom;
}
