package com.swp391project.SWP391_QuitSmoking_BE.dto.program;

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
public class ProgramRequestDTO {

    @NotBlank(message = "Program title cannot be empty")
    @Size(max = 255, message = "Program title cannot exceed 255 characters")
    private String programTitle;

    @Size(max = 100, message = "Program name cannot exceed 100 characters")
    private String programName;

    @Size(max = 50, message = "Program type cannot exceed 50 characters")
    private String programType;

    // MultipartFile cho image upload (optional)
    private MultipartFile programImage;

    @Size(max = 255, message = "Content URL cannot exceed 255 characters")
    private String contentUrl;

    private String description;
}
