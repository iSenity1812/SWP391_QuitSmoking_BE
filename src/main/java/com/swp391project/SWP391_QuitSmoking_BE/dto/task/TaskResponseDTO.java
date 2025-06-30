package com.swp391project.SWP391_QuitSmoking_BE.dto.task;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quiz.QuizResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.tip.TipResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List; // Thêm import này

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private Integer taskId;
    private LocalDateTime createdAt;
    private Integer typeId; // Task Type ID (ví dụ: 1 cho "Thèm thuốc Task")
    private List<QuizResponseDTO> quizzes; // Danh sách Quiz
    private TipResponseDTO tips;     // Danh sách Tip
}