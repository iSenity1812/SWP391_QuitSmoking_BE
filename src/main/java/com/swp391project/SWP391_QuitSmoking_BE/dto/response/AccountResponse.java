package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
public class AccountResponse {
    @Getter
    private UUID userId;
    private String username;
    private String email;
    private String profilePicture; // URL của ảnh đại diện người dùng
    private Role role; // Chỉ sử dụng khi cần phân quyền
    private boolean isActive; // Trạng thái hoạt động của tài khoản
    private String token; // Token JWT để xác thực người dùng

}
