package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AdminUserCreateRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$#!%*?&])[A-Za-z\\d@$!%*?&]{8,30}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;

    @NotNull(message = "Role cannot be null") // Vai trò không được null
    private Role role; // Sử dụng enum Role của bạn

    @Size(min = 3, max = 50, message = "Full name must be between 3 and 50 characters")
    private String fullName;

    private String coachBio; // Chỉ dành cho Coach, có thể để trống nếu không phải Coach


//    private String avatarUrl; // Có thể để admin set avatar ban đầu
    private Boolean active = true; // Mặc định là active, admin có thể thay đổi


}