package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal.WeeklyGoalRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal.WeeklyGoalResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.weeklygoal.WeeklyGoalSummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.WeeklyGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/weekly-goals")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
@Tag(name = "Weekly Goals", description = "API quản lý mục tiêu hàng tuần")
public class WeeklyGoalController {

    private final WeeklyGoalService weeklyGoalService;

    /**
     * Đặt mục tiêu tuần mới
     */
    @PostMapping
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    @Operation(summary = "Đặt mục tiêu tuần mới", 
               description = "Tạo mục tiêu mới cho tuần hiện tại")
    public ResponseEntity<ApiResponse<WeeklyGoalResponse>> setWeeklyGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WeeklyGoalRequest request) {
        
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            WeeklyGoalResponse response = weeklyGoalService.setWeeklyGoal(memberId, request);
            
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Đặt mục tiêu tuần thành công"));
                    
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                            "Có lỗi xảy ra khi đặt mục tiêu: " + e.getMessage()));
        }
    }

    /**
     * Lấy mục tiêu tuần hiện tại
     */
    @GetMapping("/current")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    @Operation(summary = "Lấy mục tiêu tuần hiện tại", 
               description = "Trả về thông tin mục tiêu của tuần hiện tại")
    public ResponseEntity<ApiResponse<WeeklyGoalResponse>> getCurrentWeeklyGoal(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            WeeklyGoalResponse response = weeklyGoalService.getCurrentWeeklyGoal(memberId);
            
            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND, 
                                "Không có mục tiêu nào cho tuần này"));
            }
            
            return ResponseEntity.ok(ApiResponse.success(response, "Lấy mục tiêu tuần hiện tại thành công"));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                            "Có lỗi xảy ra khi lấy mục tiêu: " + e.getMessage()));
        }
    }

    /**
     * Lấy tổng quan về các mục tiêu tuần
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    @Operation(summary = "Lấy tổng quan mục tiêu tuần", 
               description = "Trả về thống kê và tổng quan về tất cả mục tiêu tuần")
    public ResponseEntity<ApiResponse<WeeklyGoalSummary>> getWeeklyGoalSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            WeeklyGoalSummary summary = weeklyGoalService.getWeeklyGoalSummary(memberId);
            
            return ResponseEntity.ok(ApiResponse.success(summary, "Lấy tổng quan mục tiêu thành công"));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                            "Có lỗi xảy ra khi lấy tổng quan: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật progress manually (for testing)
     */
    @PostMapping("/update-progress")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    @Operation(summary = "Cập nhật tiến độ thủ công", 
               description = "Cập nhật tiến độ mục tiêu tuần hiện tại (chỉ để test)")
    public ResponseEntity<ApiResponse<String>> updateProgress(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            
            // Trigger manual update
            weeklyGoalService.updateWeeklyGoalProgress();
            
            return ResponseEntity.ok(ApiResponse.success("OK", "Cập nhật tiến độ thành công"));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                            "Có lỗi xảy ra khi cập nhật tiến độ: " + e.getMessage()));
        }
    }

    /**
     * Lấy lịch sử mục tiêu tuần (cho premium members)
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('PREMIUM_MEMBER')")
    @Operation(summary = "Lấy lịch sử mục tiêu tuần", 
               description = "Trả về lịch sử các mục tiêu tuần (chỉ dành cho Premium member)")
    public ResponseEntity<ApiResponse<WeeklyGoalSummary>> getWeeklyGoalHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Số trang") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Kích thước trang") @RequestParam(defaultValue = "10") int size) {
        
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            WeeklyGoalSummary history = weeklyGoalService.getWeeklyGoalSummary(memberId);
            
            return ResponseEntity.ok(ApiResponse.success(history, "Lấy lịch sử mục tiêu thành công"));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                            "Có lỗi xảy ra khi lấy lịch sử: " + e.getMessage()));
        }
    }

    /**
     * Lấy gợi ý mục tiêu tuần tiếp theo
     */
    @GetMapping("/suggestions")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    @Operation(summary = "Lấy gợi ý mục tiêu", 
               description = "Trả về gợi ý mục tiêu thông minh dựa trên hiệu suất trước đó")
    public ResponseEntity<ApiResponse<WeeklyGoalSummary>> getGoalSuggestions(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            WeeklyGoalSummary suggestions = weeklyGoalService.getWeeklyGoalSummary(memberId);
            
            return ResponseEntity.ok(ApiResponse.success(suggestions, "Lấy gợi ý mục tiêu thành công"));
            
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                            "Có lỗi xảy ra khi lấy gợi ý: " + e.getMessage()));
        }
    }

    // Helper method
    private UUID getAuthenticatedMemberId(UserDetails userDetails) {
        if (!(userDetails instanceof User currentUser)) {
            throw new RuntimeException("Người dùng chưa được xác thực hoặc không có quyền truy cập.");
        }
        return currentUser.getUserId();
    }
}
