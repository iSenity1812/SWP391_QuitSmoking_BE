package com.swp391project.SWP391_QuitSmoking_BE.dto.quiz;

import com.swp391project.SWP391_QuitSmoking_BE.dto.option.OptionResponseDTO;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {
    private UUID quizId;
    private String title;
    private String description;
//    private Integer scorePossible;
    private List<OptionResponseDTO> options; // Các lựa chọn cho câu đố
}