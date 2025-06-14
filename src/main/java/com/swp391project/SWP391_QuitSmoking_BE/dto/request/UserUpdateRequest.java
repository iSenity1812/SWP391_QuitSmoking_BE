package com.swp391project.SWP391_QuitSmoking_BE.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @Size(max = 100, message = "Email cannot exceed 100 characters")
    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 255, message = "Avatar URL cannot exceed 255 characters")
    @Pattern(
            regexp = "^$|.*\\.(jpg|jpeg|png|gif|webp|bmp|svg)$",
            message = "Tên file ảnh không hợp lệ hoặc định dạng không được hỗ trợ",
            flags = Pattern.Flag.CASE_INSENSITIVE
    )
    private String profilePicture;

    @Size(min = 8, message = "Password must be more than 8 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password; // Mật khẩu mới (nếu có)
//    private String confirmPassword; // Xác nhận mật khẩu mới (nếu có)
}