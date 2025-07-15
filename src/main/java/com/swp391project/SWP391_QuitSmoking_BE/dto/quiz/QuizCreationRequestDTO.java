package com.swp391project.SWP391_QuitSmoking_BE.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
// import java.util.UUID; // Không cần thiết nữa nếu không có createdByAdminId

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizCreationRequestDTO {
    @NotBlank(message = "Tiêu đề câu đố không được để trống")
    @Size(max = 255, message = "Tiêu đề câu đố không được vượt quá 255 ký tự")
    private String title;

    @Size(max = 1000, message = "Mô tả câu đố không được vượt quá 1000 ký tự")
    private String description;

//    @NotNull(message = "Điểm tối đa có thể đạt được không được để trống")
//    private Integer scorePossible;

    // --- DÒNG NÀY ĐÃ BỊ XÓA HOẶC COMMENT OUT ---
    // private UUID createdByAdminId;

    @NotEmpty(message = "Câu đố phải có ít nhất một lựa chọn")
    @Valid // Áp dụng validation cho từng phần tử trong danh sách
    private List<OptionRequest> options;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionRequest {
        @NotBlank(message = "Nội dung lựa chọn không được để trống")
        @Size(max = 255, message = "Nội dung lựa chọn không được vượt quá 255 ký tự")
        private String content;

        @NotNull(message = "Thông tin đúng/sai của lựa chọn không được để trống")
        private Boolean isCorrect;
    }
}