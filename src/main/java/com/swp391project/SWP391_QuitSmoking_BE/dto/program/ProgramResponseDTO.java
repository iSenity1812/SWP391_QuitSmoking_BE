package com.swp391project.SWP391_QuitSmoking_BE.dto.program;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.swp391project.SWP391_QuitSmoking_BE.dto.user.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramResponseDTO {

    private Integer programId;
    private String programTitle;
    private String programName;
    private String programType;
    private String programImage;
    private String contentUrl;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private UserResponseDTO createdBy; // Thông tin người tạo
}
