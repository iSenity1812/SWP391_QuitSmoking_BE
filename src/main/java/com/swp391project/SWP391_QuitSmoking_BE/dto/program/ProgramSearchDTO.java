package com.swp391project.SWP391_QuitSmoking_BE.dto.program;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ProgramType;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramSearchDTO {
    private String programTitle;
    private String programName;
    private ProgramType programType;
    private String description;
}
