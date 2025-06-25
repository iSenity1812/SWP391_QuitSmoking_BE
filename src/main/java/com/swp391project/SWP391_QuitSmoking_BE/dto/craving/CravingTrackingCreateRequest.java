package com.swp391project.SWP391_QuitSmoking_BE.dto.craving;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CravingTrackingCreateRequest {
//    @NotNull(message = "Member ID không được để trống")
//    private UUID memberId;
    @PastOrPresent(message = "Thời gian theo dõi không thể ở tương lai")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime trackTime;

    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    @Max(value = 1000, message = "Số lượng thuốc đã hút không thể vượt quá 1000 điếu")
    private Integer smokedCount; // Số điếu thuốc đã hút trong sự kiện này
    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    @Max(value = 1000, message = "Số lần thèm thuốc không thể vượt quá 1000 lần")
    private Integer cravingsCount; // Số lần thèm thuốc trong sự kiện này

    private Situation situation; // Tình huống duy nhất liên quan đến sự kiện (chỉ 1 enum)
    private WithWhom withWhom;   // Người đi cùng duy nhất liên quan đến sự kiện (chỉ 1 enum)
}
