package com.swp391project.SWP391_QuitSmoking_BE.dto.option;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionResponseDTO {
    private Integer optionId;
    private String content;
    private boolean isCorrect;
}