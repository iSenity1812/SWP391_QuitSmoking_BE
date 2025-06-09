package com.swp391project.SWP391_QuitSmoking_BE.dto.response;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import lombok.Data;

@Data
public class AccountResponse {
    private String username;
    private String email;
    private String avatarUrl;
    private Role role; // Chỉ sử dụng khi cần phân quyền
    private boolean isActive; // Trạng thái hoạt động của tài khoản
    private String token; // Token JWT để xác thực người dùng
}
