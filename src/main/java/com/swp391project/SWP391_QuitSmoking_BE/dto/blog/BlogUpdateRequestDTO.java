package com.swp391project.SWP391_QuitSmoking_BE.dto.blog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BlogUpdateRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề không được vượt quá 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    // Image file để upload (có thể null nếu không muốn thay đổi image)
    private MultipartFile imageUrl;

    // Flag để xóa image hiện tại (true = xóa image, false = giữ nguyên)
    private Boolean removeImage = false;
}
