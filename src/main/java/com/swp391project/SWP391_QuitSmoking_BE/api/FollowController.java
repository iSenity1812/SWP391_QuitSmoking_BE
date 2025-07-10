package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.follow.FollowRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.follow.FollowResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.follow.UserFollowStatsDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class FollowController {

    private final FollowService followService;

    // Follow một user
    @PostMapping
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    @Operation(summary = "Follow một người dùng")
    public ResponseEntity<ApiResponse<FollowResponseDTO>> followUser(
            @Valid @RequestBody FollowRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        FollowResponseDTO response = followService.followUser(currentUser.getUserId(), request.getFollowedUserId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Follow người dùng thành công."));
    }

    // Unfollow một user
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    @Operation(summary = "Unfollow một người dùng")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {
        followService.unfollowUser(currentUser.getUserId(), userId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Unfollow người dùng thành công."));
    }

    // Lấy danh sách followers của một user
    @GetMapping("/{userId}/followers")
    @Operation(summary = "Lấy danh sách followers của một người dùng")
    public ResponseEntity<ApiResponse<Page<FollowResponseDTO>>> getFollowers(
            @PathVariable UUID userId,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<FollowResponseDTO> followers = followService.getFollowers(userId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(followers, "Lấy danh sách followers thành công."));
    }

    // Lấy danh sách người mà user đang follow
    @GetMapping("/{userId}/following")
    @Operation(summary = "Lấy danh sách người mà user đang follow")
    public ResponseEntity<ApiResponse<Page<FollowResponseDTO>>> getFollowing(
            @PathVariable UUID userId,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<FollowResponseDTO> following = followService.getFollowing(userId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(following, "Lấy danh sách following thành công."));
    }

    // Lấy thống kê follow của một user
    @GetMapping("/{userId}/stats")
    @Operation(summary = "Lấy thống kê follow của một người dùng")
    public ResponseEntity<ApiResponse<UserFollowStatsDTO>> getUserFollowStats(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {
        UUID currentUserId = currentUser != null ? currentUser.getUserId() : null;
        UserFollowStatsDTO stats = followService.getUserFollowStats(userId, currentUserId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(stats, "Lấy thống kê follow thành công."));
    }

    // Kiểm tra có đang follow user không
    @GetMapping("/{userId}/is-following")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    @Operation(summary = "Kiểm tra có đang follow user không")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(
            @PathVariable UUID userId,
            @AuthenticationPrincipal User currentUser) {
        boolean isFollowing = followService.isFollowing(currentUser.getUserId(), userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(isFollowing, "Kiểm tra trạng thái follow thành công."));
    }

    // Lấy danh sách mutual follows (follow lẫn nhau)
    @GetMapping("/mutual")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    @Operation(summary = "Lấy danh sách mutual follows")
    public ResponseEntity<ApiResponse<List<FollowResponseDTO>>> getMutualFollows(
            @AuthenticationPrincipal User currentUser) {
        List<FollowResponseDTO> mutualFollows = followService.getMutualFollows(currentUser.getUserId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(mutualFollows, "Lấy danh sách mutual follows thành công."));
    }

    // Lấy followers của current user
    @GetMapping("/my-followers")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    @Operation(summary = "Lấy danh sách followers của tôi")
    public ResponseEntity<ApiResponse<Page<FollowResponseDTO>>> getMyFollowers(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<FollowResponseDTO> followers = followService.getFollowers(currentUser.getUserId(), pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(followers, "Lấy danh sách followers của tôi thành công."));
    }

    // Lấy danh sách người mà current user đang follow
    @GetMapping("/my-following")
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'CONTENT_ADMIN')")
    @Operation(summary = "Lấy danh sách người tôi đang follow")
    public ResponseEntity<ApiResponse<Page<FollowResponseDTO>>> getMyFollowing(
            @AuthenticationPrincipal User currentUser,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<FollowResponseDTO> following = followService.getFollowing(currentUser.getUserId(), pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(following, "Lấy danh sách người tôi đang follow thành công."));
    }
}