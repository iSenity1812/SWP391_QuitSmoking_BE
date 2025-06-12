package com.swp391project.SWP391_QuitSmoking_BE.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmitQuizAttemptRequestDTO {
    @NotNull(message = "Task ID không được để trống")
    private Integer taskId; // Task ID mà người dùng đang nộp bài cho

    @NotNull(message = "Quiz ID không được để trống")
    private UUID quizId; // Quiz ID của Quiz đang được làm

    @NotEmpty(message = "Danh sách câu trả lời không được để trống")
    @Valid
    private List<QuizAttemptDetail> userAnswers; // Các câu trả lời cho QUIZ ĐÓ (thường là 1 câu trả lời cho 1 quiz)

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizAttemptDetail {
        @NotNull(message = "Selected option ID không được để trống")
        private Integer selectedOptionId;
    }
}