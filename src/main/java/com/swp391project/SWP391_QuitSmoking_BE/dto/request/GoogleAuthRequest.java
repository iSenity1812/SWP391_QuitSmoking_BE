package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleAuthRequest {
    private String googleId;
    private String email;
    private String name;
    private String picture;
} 