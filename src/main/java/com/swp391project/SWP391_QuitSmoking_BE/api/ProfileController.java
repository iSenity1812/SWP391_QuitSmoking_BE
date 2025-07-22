package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.CustomUserDetails;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.ProfileService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@SecurityRequirement(name = "user_api")
@AllArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Ban chưa đăng nhập hoặc thông tin người dùng không hợp lệ"));
        }

        UUID userId = userService.getUserIdFromUserDetails(userDetails);
        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Không tìm thấy thông tin người dùng với ID: " + null));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(profileService.getMyProfile(userId), "Lấy thông tin cá nhân thành công"));

//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponse.success("Test message: Profile API is reachable!", "Lấy thông tin cá nhân thành công"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Object>> getPublicProfile(
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userId == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "ID người dùng không hợp lệ"));
        }

        UUID currentUserId = userService.getUserIdFromUserDetails(userDetails);
        if (currentUserId == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Bạn chưa đăng nhập hoặc thông tin người dùng không hợp lệ"));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(profileService.getPublicProfile(userId, currentUserId), "Lấy thông tin công khai thành công"));
    }
}
