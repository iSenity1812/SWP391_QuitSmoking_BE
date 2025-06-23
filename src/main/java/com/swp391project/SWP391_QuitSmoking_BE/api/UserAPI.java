package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User; // Có thể xóa nếu không trả về entity trực tiếp ở đây
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserProfile; // Import UserProfile
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.AdminUserCreateRequest; // Import AdminUserCreateRequest
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserUpdateRequest; // Import UserUpdateRequest
import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.CoachProfile; // Import CoachProfile
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserAPI {
    @Autowired
    private UserService userService;

    @GetMapping // Trả về List<UserProfile>
    public List<UserProfile> getAllUsers() {
        return userService.getAllUsers(); // Sửa: Gọi đúng phương thức và kiểu trả về
    }

    @GetMapping("/{id}") // Trả về UserProfile
    public ResponseEntity<UserProfile> getUserById(@PathVariable UUID id) {
        UserProfile userProfile = userService.getUserById(id); // userService.getUserById(id) đã trả về UserProfile trực tiếp
        return ResponseEntity.ok(userProfile); // Sửa: Trả về DTO UserProfile
    }

    // API để tạo user (chủ yếu dùng cho Admin)
    @PostMapping
    public ResponseEntity<UserProfile> createUser(@Valid @RequestBody AdminUserCreateRequest request) { // Sửa: nhận AdminUserCreateRequest
        UserProfile created = userService.createUser(request); // Sửa: gọi đúng phương thức
        return ResponseEntity.ok(created);
    }

    // API để cập nhật profile của user
    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) { // Sửa: nhận UserUpdateRequest
        try {
            UserProfile updated = userService.updateUserProfile(id, request); // Sửa: gọi đúng phương thức
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Thêm các API khác cho UserService nếu cần thiết
    @GetMapping("/members")
    public List<UserProfile> getAllMembers() {
        return userService.getAllMembers();
    }

    @GetMapping("/coaches")
    public List<CoachProfile> getAllCoaches() {
        return userService.getAllCoaches();
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserProfile> deactivateUser(@PathVariable UUID id) {
        try {
            UserProfile updated = userService.deactivateUser(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<UserProfile> activateUser(@PathVariable UUID id) {
        try {
            UserProfile updated = userService.activateUser(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<UserProfile> toggleUserStatus(@PathVariable UUID id) {
        try {
            UserProfile updated = userService.toggleUserStatus(id);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}