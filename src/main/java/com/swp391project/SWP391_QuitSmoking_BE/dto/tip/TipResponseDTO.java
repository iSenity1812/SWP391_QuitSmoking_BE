package com.swp391project.SWP391_QuitSmoking_BE.dto.tip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipResponseDTO {
    private UUID tipId;
    private String content;
}