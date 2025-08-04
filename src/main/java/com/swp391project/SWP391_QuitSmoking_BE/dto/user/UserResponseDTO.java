package com.swp391project.SWP391_QuitSmoking_BE.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID userId;
    private String username; // hoặc firstName, lastName
    private String name; // Thêm field name để tương thích với frontend
    private String email;
    // Thêm các trường khác mà bạn muốn hiển thị public
    // private String avatarUrl;
}