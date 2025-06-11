package com.swp391project.SWP391_QuitSmoking_BE.dto.craving;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class CravingTrackingUpdateRequest {
    @Min(value = 0, message = "Số lượng thuốc đã hút không thể là số âm")
    private Integer smokedCount;
    @Min(value = 0, message = "Số lần thèm thuốc không thể là số âm")
    private Integer cravingsCount;
    private Set<Situation> situations;
    private Set<WithWhom> withWhoms;
}
