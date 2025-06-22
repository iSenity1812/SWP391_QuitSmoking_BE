package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.challenge.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User; // Import User entity của bạn
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.ChallengeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api") // Tên của cấu hình bảo mật trong OpenAPI/Swagger
public class ChallengeController {

    private final ChallengeService challengeService;

    // --- ENDPOINT CHO THÀNH VIÊN (TẠO, SỬA, XÓA THỬ THÁCH CỦA HỌ) ---

    // Tạo Challenge: Chỉ dành cho PREMIUM_MEMBER
    @PostMapping
    @PreAuthorize("hasRole('PREMIUM_MEMBER')") // Chỉ Premium Member được tạo challenge
    public ResponseEntity<ApiResponse<ChallengeResponseDTO>> createChallenge(
            @Valid @RequestBody ChallengeRequestDTO request,
            @AuthenticationPrincipal User currentUser) { // Lấy thông tin User từ SecurityContext
        ChallengeResponseDTO createdChallenge = challengeService.createChallenge(currentUser.getUserId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdChallenge, "Tạo thử thách mới thành công."));
    }

    // Lấy tất cả Challenge của người dùng hiện tại
    @GetMapping("/my-challenges")
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')") // Mọi thành viên có thể xem challenge của họ
    public ResponseEntity<ApiResponse<List<ChallengeResponseDTO>>> getMyChallenges(
            @AuthenticationPrincipal User currentUser) {
        List<ChallengeResponseDTO> challenges = challengeService.getChallengesByMemberId(currentUser.getUserId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(challenges, "Lấy danh sách thử thách của bạn thành công."));
    }

    // Lấy thông tin một Challenge cụ thể bằng ID (chỉ chủ sở hữu hoặc admin/coach có quyền)
    // URL: /api/challenges/{challengeId}
    @GetMapping("/{challengeId}")
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ChallengeResponseDTO>> getChallengeById(
            @PathVariable Integer challengeId,
            @AuthenticationPrincipal User currentUser) {
        // Trong service, bạn sẽ kiểm tra xem currentUser có phải là chủ sở hữu hoặc có quyền xem challenge này không
        ChallengeResponseDTO challenge = challengeService.getChallengeById(challengeId, currentUser.getUserId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(challenge, "Lấy thông tin thử thách thành công."));
    }


    // Sửa Challenge: Chỉ chủ sở hữu challenge được sửa
    @PutMapping("/{challengeId}")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')") // Ai có thể tạo thì có thể sửa
    public ResponseEntity<ApiResponse<ChallengeResponseDTO>> updateChallenge(
            @PathVariable Integer challengeId,
            @Valid @RequestBody ChallengeRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        ChallengeResponseDTO updatedChallenge = challengeService.updateChallenge(currentUser.getUserId(), challengeId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedChallenge, "Cập nhật thử thách thành công."));
    }

    // Xóa Challenge: Chỉ chủ sở hữu hoặc SUPER_ADMIN có thể xóa
    @DeleteMapping("/{challengeId}")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteChallenge(
            @PathVariable Integer challengeId,
            @AuthenticationPrincipal User currentUser) {
        challengeService.deleteChallenge(currentUser.getUserId(), challengeId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Xóa thử thách thành công."));
    }

    // --- CÁC ENDPOINT DÀNH CHO ADMIN (CONTENT_ADMIN / SUPER_ADMIN) ---
    // Ví dụ: Lấy tất cả challenges (cho admin quản lý)
    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<ChallengeResponseDTO>>> getAllChallengesForAdmin() {
        List<ChallengeResponseDTO> challenges = challengeService.getAllChallengesForAdmin();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(challenges, "Lấy danh sách tất cả thử thách (Admin) thành công."));
    }

    // Ví dụ: Cập nhật trạng thái challenge bởi Admin
    @PutMapping("/admin/{challengeId}/status")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ChallengeResponseDTO>> updateChallengeStatusByAdmin(
            @PathVariable Integer challengeId,
            @RequestParam String newStatus, // Ví dụ: "Completed", "Given Up", "Active"
            @AuthenticationPrincipal User adminUser) {
        ChallengeResponseDTO updatedChallenge = challengeService.updateChallengeStatus(challengeId, newStatus, adminUser.getUserId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedChallenge, "Cập nhật trạng thái thử thách thành công."));
    }
}
