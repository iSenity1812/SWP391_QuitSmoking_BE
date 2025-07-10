package com.swp391project.SWP391_QuitSmoking_BE.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreationRequestDTO {
    @NotBlank(message = "Tên nhiệm vụ không được để trống")
    @Size(max = 100, message = "Tên nhiệm vụ không được vượt quá 100 ký tự")
    private String taskName;

    @Size(max = 1000, message = "Mô tả nhiệm vụ không được vượt quá 1000 ký tự")
    private String description;
}