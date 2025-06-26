package com.swp391project.SWP391_QuitSmoking_BE.dto.achievement;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Target value is required")
    private Integer targetValue;

    @NotBlank(message = "Category is required")
    private String category;

    private String badgeUrl;
    private Integer points;
}