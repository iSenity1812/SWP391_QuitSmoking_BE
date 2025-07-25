package com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal;

import com.swp391project.SWP391_QuitSmoking_BE.entity.WeeklyGoal;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyGoalRequest {

    @NotNull(message = "Mục tiêu số thuốc lá không được để trống")
    @Min(value = 0, message = "Mục tiêu số thuốc lá không thể âm")
    @Max(value = 500, message = "Mục tiêu số thuốc lá không thể vượt quá 500")
    private Integer targetSmokedCount;

    @NotNull(message = "Mục tiêu chống chọi cơn thèm không được để trống")
    @Min(value = 0, message = "Mục tiêu chống chọi cơn thèm không thể âm")
    @Max(value = 100, message = "Mục tiêu chống chọi cơn thèm không thể vượt quá 100")
    private Integer targetCravingResistance;

    @NotNull(message = "Độ khó không được để trống")
    private WeeklyGoal.DifficultyLevel difficultyLevel;

    @NotNull(message = "Loại mục tiêu không được để trống")
    private WeeklyGoal.GoalType goalType;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String notes;
}
