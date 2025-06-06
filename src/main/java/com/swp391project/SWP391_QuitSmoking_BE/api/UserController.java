package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.request.AdminUserCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserProfile;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class UserController {
    private final UserService userService;

    // Lấy tất cả người dùng
    // Bao gồm cả user đã inactive, vì superadmin cần quản lý tất cả
    @GetMapping("/superadmin/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<UserProfile>>> getAllUsers() {
        List<UserProfile> users = userService.getAllUsers(); // Lấy tất cả người dùng (active và inactive)
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(users, "Lấy danh sách người dùng thành công."));
    }

    // Lấy thông tin một người dùng cụ thể bằng ID
    @GetMapping("/superadmin/users/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfile>> getUserById(@PathVariable UUID userId) {
        UserProfile user = userService.getUserById(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(user, "Lấy thông tin người dùng thành công."));
    }


    // Tạo người dùng mới (chỉ dành cho SUPER_ADMIN)
    @PostMapping("/superadmin/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfile>> createUser(@Valid @RequestBody AdminUserCreateRequest userCreateRequest) {
        UserProfile newUser = userService.createUser(userCreateRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED) // HTTP 201 Created là chuẩn khi tạo tài nguyên mới thành công
                .body(ApiResponse.success(newUser, "Tạo người dùng mới thành công."));
    }

    // Cập nhật thông tin người dùng bởi SUPER_ADMIN
    @PatchMapping("/superadmin/users/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfile>> updateUserByAdmin(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        UserProfile updatedProfile = userService.updateUserProfile(userId, userUpdateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedProfile, "Cập nhật thông tin người dùng thành công."));
    }

    // --- ENDPOINTS QUẢN LÝ TRẠNG THÁI NGƯỜI DÙNG BỞI SUPER_ADMIN ---
    // (Đã thay đổi sang PATCH để đúng nguyên tắc soft delete)

    @PatchMapping("/superadmin/users/{userId}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfile>> deactivateUser(@PathVariable UUID userId) {
        UserProfile updatedUser = userService.deactivateUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedUser, "Người dùng đã được vô hiệu hóa thành công."));
    }

    @PatchMapping("/superadmin/users/{userId}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfile>> activateUser(@PathVariable UUID userId) {
        UserProfile updatedUser = userService.activateUser(userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedUser, "Người dùng đã được kích hoạt lại thành công."));
    }

    @PatchMapping("/superadmin/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserProfile>> toggleUserStatus(@PathVariable UUID userId) {
        UserProfile updatedUser = userService.toggleUserStatus(userId);
        String message = updatedUser.isActive() ? "Người dùng đã được kích hoạt thành công." : "Người dùng đã được vô hiệu hóa thành công.";
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedUser, message));
    }

    // --- ENDPOINTS CỦA NGƯỜI DÙNG HIỆN TẠI (PROFILE CỦA CHÍNH HỌ) ---

    // Lấy thông tin hồ sơ của người dùng hiện tại
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfile>> getCurrentUserProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserProfile userProfile = userService.getUserById(user.getUserId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(userProfile, "Lấy thông tin hồ sơ người dùng thành công."));
    }

    // Cập nhật toàn bộ hoặc một phần hồ sơ của người dùng hiện tại (ngoại trừ username/email nếu chúng có API riêng)
    // Đây là endpoint chung để người dùng tự cập nhật profile của mình (ví dụ: ảnh đại diện, cài đặt thông báo)
    @PatchMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfile>> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        User user = (User) authentication.getPrincipal();
        UserProfile updatedProfile = userService.updateUserProfile(user.getUserId(), userUpdateRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedProfile, "Cập nhật hồ sơ thành công."));
    }

    // Cập nhật username của người dùng hiện tại
    // Bạn đã có endpoint này tách riêng, rất tốt.
    @PatchMapping("/profile/username") // Đổi /profile/me/username thành /profile/username cho gọn
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserProfile>> updateCurrentUserUsername(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        User user = (User) authentication.getPrincipal();
        UserProfile updatedProfile = userService.updateUsername(user.getUserId(), userUpdateRequest.getUsername());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedProfile, "Cập nhật tên người dùng thành công."));
    }
}
