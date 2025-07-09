package com.swp391project.SWP391_QuitSmoking_BE.dto.program;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProgramUpdateRequestDTO {

    @NotBlank(message = "Program title cannot be empty")
    @Size(max = 255, message = "Program title cannot exceed 255 characters")
    private String programTitle;

    @Size(max = 100, message = "Program name cannot exceed 100 characters")
    private String programName;

    @Size(max = 50, message = "Program type cannot exceed 50 characters")
    private String programType;

    // Image file để upload (có thể null nếu không muốn thay đổi image)
    private MultipartFile programImage;

    // Flag để xóa image hiện tại (true = xóa image, false = giữ nguyên)
    private Boolean removeImage = false;

    @Size(max = 255, message = "Content URL cannot exceed 255 characters")
    private String contentUrl;

    private String description;
}
