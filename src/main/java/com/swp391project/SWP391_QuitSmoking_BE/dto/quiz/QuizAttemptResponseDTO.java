package com.swp391project.SWP391_QuitSmoking_BE.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID; // Import UUID

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptResponseDTO {
    private Integer taskId; // TaskID vẫn là Integer
    private int totalScore;
    private int correctAnswersCount;
    private int totalQuestions;
    private Map<UUID, Boolean> quizResults; // Key cho quizResults là UUID QuizID
    private String message;
}