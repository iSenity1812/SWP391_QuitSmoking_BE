package com.swp391project.SWP391_QuitSmoking_BE.dto.program;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSearchDTO {
    private String programTitle;
    private String programName;
    private String programType;
    private String description;
}
