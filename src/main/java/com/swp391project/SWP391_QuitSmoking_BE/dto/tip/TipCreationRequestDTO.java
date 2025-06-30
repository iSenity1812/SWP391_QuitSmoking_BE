package com.swp391project.SWP391_QuitSmoking_BE.dto.tip;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipCreationRequestDTO {
    @NotBlank(message = "Nội dung mẹo không được để trống")
    @Size(max = 2000, message = "Nội dung mẹo không được vượt quá 2000 ký tự")
    private String content;

}