package com.swp391project.SWP391_QuitSmoking_BE.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSimpleResponseDTO {
    private UUID userId;
    private String username;
    private String email;
}
