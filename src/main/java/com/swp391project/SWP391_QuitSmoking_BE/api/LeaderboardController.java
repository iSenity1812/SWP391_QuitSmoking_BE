package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.response.LeaderboardEntry;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class LeaderboardController {
    
    private final LeaderboardService leaderboardService;
    
    /**
     * Lấy leaderboard theo tiền tiết kiệm
     */
    @GetMapping("/money-saved")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get money saved leaderboard", description = "Get top users by money saved")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getMoneySavedLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntry> leaderboard = leaderboardService.getMoneySavedLeaderboard(limit);
        return ResponseEntity.ok(ApiResponse.success(leaderboard, "Lấy leaderboard tiền tiết kiệm thành công"));
    }
    
    /**
     * Lấy leaderboard theo số ngày bỏ thuốc
     */
    @GetMapping("/days-quit")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get days quit leaderboard", description = "Get top users by days quit")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getDaysQuitLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntry> leaderboard = leaderboardService.getDaysQuitLeaderboard(limit);
        return ResponseEntity.ok(ApiResponse.success(leaderboard, "Lấy leaderboard số ngày bỏ thuốc thành công"));
    }
    
    /**
     * Lấy leaderboard theo số điếu đã tránh hút
     */
    @GetMapping("/cigarettes-avoided")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get cigarettes avoided leaderboard", description = "Get top users by cigarettes avoided")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getCigarettesAvoidedLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntry> leaderboard = leaderboardService.getCigarettesAvoidedLeaderboard(limit);
        return ResponseEntity.ok(ApiResponse.success(leaderboard, "Lấy leaderboard số điếu đã tránh hút thành công"));
    }
    
    /**
     * Lấy leaderboard theo số lượng thành tựu
     */
    @GetMapping("/achievement-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get achievement count leaderboard", description = "Get top users by achievement count")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getAchievementCountLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntry> leaderboard = leaderboardService.getAchievementCountLeaderboard(limit);
        return ResponseEntity.ok(ApiResponse.success(leaderboard, "Lấy leaderboard thành tựu thành công"));
    }
    
    /**
     * Lấy rank của user hiện tại trong leaderboard
     */
    @GetMapping("/user/rank")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user rank", description = "Get current user's rank in specified leaderboard")
    public ResponseEntity<ApiResponse<LeaderboardEntry>> getUserRank(
            Authentication authentication,
            @RequestParam String leaderboardType) {
        User user = (User) authentication.getPrincipal();
        UUID memberId = user.getMember().getMemberId();
        
        LeaderboardEntry userRank = leaderboardService.getUserRank(memberId, leaderboardType);
        if (userRank != null) {
            return ResponseEntity.ok(ApiResponse.success(userRank, "Lấy rank của user thành công"));
        } else {
            return ResponseEntity.ok(ApiResponse.success(null, "User chưa có rank trong leaderboard này"));
        }
    }
    
    /**
     * Lấy tất cả leaderboards cho user hiện tại
     */
    @GetMapping("/user/all-ranks")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all user ranks", description = "Get current user's rank in all leaderboards")
    public ResponseEntity<ApiResponse<List<LeaderboardEntry>>> getAllUserRanks(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UUID memberId = user.getMember().getMemberId();
        
        List<LeaderboardEntry> allRanks = List.of(
            leaderboardService.getUserRank(memberId, "MONEY_SAVED"),
            leaderboardService.getUserRank(memberId, "DAYS_QUIT"),
            leaderboardService.getUserRank(memberId, "CIGARETTES_AVOIDED")
        );
        
        return ResponseEntity.ok(ApiResponse.success(allRanks, "Lấy tất cả rank của user thành công"));
    }
} 