package com.swp391project.SWP391_QuitSmoking_BE.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogUpdateRequestDTO {

    @NotBlank(message = "Title cannot be empty")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    // MultipartFile cho image upload (optional)
    private MultipartFile imageUrl;

    // Flag để xóa image hiện tại}
}
