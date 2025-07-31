package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthMetricDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthOverviewDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.HealthMetricType;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.HealthMetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * HealthController - API Controller để quản lý theo dõi sức khỏe người dùng
 *
 * Chức năng chính:
 * - Cung cấp các API để theo dõi tiến độ sức khỏe sau khi bỏ thuốc
 * - Hiển thị các milestone sức khỏe và thời gian đạt được
 * - Tính toán tiến độ dựa trên thời gian bỏ thuốc
 * - Cung cấp overview tổng quan về sức khỏe
 *
 * Các tính năng:
 * 1. Health Overview - Tổng quan sức khỏe
 * 2. Health Metrics - Chi tiết từng chỉ số sức khỏe
 * 3. Progress Tracking - Theo dõi tiến độ
 * 4. Milestone Achievement - Thành tựu đạt được
 */

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
@Tag(name = "Health Tracking API", description = "API để theo dõi tiến độ sức khỏe sau khi bỏ thuốc")
@Slf4j
public class HealthController {
    
    private final HealthMetricService healthMetricService;
    
    /**
     * Lấy tổng quan sức khỏe của người dùng
     * 
     * Trả về:
     * - Tổng số metrics và số metrics đã hoàn thành
     * - Tiến độ tổng thể
     * - Các milestone sắp tới
     * - Thành tựu gần đây
     * - Thời gian bỏ thuốc
     */
    @GetMapping("/overview")
    @Operation(
        summary = "Lấy tổng quan sức khỏe",
        description = "Trả về tổng quan về tiến độ sức khỏe của người dùng sau khi bỏ thuốc, bao gồm các milestone và thành tựu"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lấy tổng quan sức khỏe thành công",
            content = @Content(schema = @Schema(implementation = HealthOverviewDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Chưa đăng nhập hoặc token không hợp lệ"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống khi lấy dữ liệu sức khỏe"
        )
    })
    public ResponseEntity<ApiResponse<HealthOverviewDTO>> getHealthOverview(
            @AuthenticationPrincipal User user) {
        
        log.info("Getting health overview for user: {}", user.getUserId());
        
        try {
            HealthOverviewDTO overview = healthMetricService.getHealthOverview(user);
            
            log.info("Successfully retrieved health overview for user: {}. Completed: {}/{} metrics",
                    user.getUserId(), overview.getCompletedMetrics(), overview.getTotalMetrics());
            
            return ResponseEntity.ok(ApiResponse.success(overview, "Lấy tổng quan sức khỏe thành công"));
            
        } catch (Exception e) {
            log.error("Error retrieving health overview for user {}: {}", user.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi lấy tổng quan sức khỏe",
                            e.getMessage(),
                            "HEALTH_OVERVIEW_ERROR"));
        }
    }
    
    /**
     * Lấy tất cả health metrics của người dùng
     * 
     * Trả về danh sách tất cả các chỉ số sức khỏe với tiến độ hiện tại
     */
    @GetMapping("/metrics")
    @Operation(
        summary = "Lấy tất cả chỉ số sức khỏe",
        description = "Trả về danh sách tất cả các chỉ số sức khỏe của người dùng với tiến độ hiện tại"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lấy danh sách chỉ số sức khỏe thành công",
            content = @Content(schema = @Schema(implementation = HealthMetricDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Chưa đăng nhập hoặc token không hợp lệ"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống khi lấy dữ liệu"
        )
    })
    public ResponseEntity<ApiResponse<List<HealthMetricDTO>>> getAllHealthMetrics(
            @AuthenticationPrincipal User user) {
        
        log.info("Getting all health metrics for user: {}", user.getUserId());
        
        try {
            List<HealthMetricDTO> metrics = healthMetricService.getUserHealthMetrics(user);
            
            log.info("Successfully retrieved {} health metrics for user: {}", metrics.size(), user.getUserId());
            
            return ResponseEntity.ok(ApiResponse.success(metrics, "Lấy danh sách chỉ số sức khỏe thành công"));
            
        } catch (Exception e) {
            log.error("Error retrieving health metrics for user {}: {}", user.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi lấy chỉ số sức khỏe",
                            e.getMessage(),
                            "HEALTH_METRICS_ERROR"));
        }
    }
    
    /**
     * Lấy health metric theo loại cụ thể
     * 
     * @param metricType Loại chỉ số sức khỏe (PULSE_RATE, OXYGEN_LEVELS, etc.)
     */
    @GetMapping("/metrics/{metricType}")
    @Operation(
        summary = "Lấy chỉ số sức khỏe theo loại",
        description = "Trả về thông tin chi tiết của một chỉ số sức khỏe cụ thể"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lấy chỉ số sức khỏe thành công",
            content = @Content(schema = @Schema(implementation = HealthMetricDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Không tìm thấy chỉ số sức khỏe"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Chưa đăng nhập hoặc token không hợp lệ"
        )
    })
    public ResponseEntity<ApiResponse<HealthMetricDTO>> getHealthMetricByType(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Loại chỉ số sức khỏe", required = true)
            @PathVariable HealthMetricType metricType) {
        
        log.info("Getting health metric {} for user: {}", metricType, user.getUserId());
        
        try {
            Optional<HealthMetricDTO> metric = healthMetricService.getHealthMetricByType(user, metricType);
            
            if (metric.isPresent()) {
                log.info("Successfully retrieved health metric {} for user: {}", metricType, user.getUserId());
                return ResponseEntity.ok(ApiResponse.success(metric.get(), "Lấy chỉ số sức khỏe thành công"));
            } else {
                log.warn("Health metric {} not found for user: {}", metricType, user.getUserId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND,
                                "Không tìm thấy chỉ số sức khỏe",
                                "Health metric not found",
                                "HEALTH_METRIC_NOT_FOUND"));
            }
            
        } catch (Exception e) {
            log.error("Error retrieving health metric {} for user {}: {}", metricType, user.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi lấy chỉ số sức khỏe",
                            e.getMessage(),
                            "HEALTH_METRIC_ERROR"));
        }
    }
    
    /**
     * Lấy danh sách các milestone sắp tới
     * 
     * Trả về danh sách các chỉ số sức khỏe chưa hoàn thành, sắp xếp theo thời gian đạt được
     */
    @GetMapping("/milestones/upcoming")
    @Operation(
        summary = "Lấy danh sách milestone sắp tới",
        description = "Trả về danh sách các milestone sức khỏe sắp đạt được, sắp xếp theo thời gian"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lấy danh sách milestone thành công",
            content = @Content(schema = @Schema(implementation = HealthMetricDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Chưa đăng nhập hoặc token không hợp lệ"
        )
    })
    public ResponseEntity<ApiResponse<List<HealthMetricDTO>>> getUpcomingMilestones(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Số lượng milestone muốn lấy (mặc định: 5)")
            @RequestParam(defaultValue = "5") int limit) {
        
        log.info("Getting upcoming milestones for user: {} with limit: {}", user.getUserId(), limit);
        
        try {
            HealthOverviewDTO overview = healthMetricService.getHealthOverview(user);
            List<HealthMetricDTO> upcomingMilestones = overview.getUpcomingMilestones();
            
            // Giới hạn số lượng kết quả
            if (upcomingMilestones.size() > limit) {
                upcomingMilestones = upcomingMilestones.subList(0, limit);
            }
            
            log.info("Successfully retrieved {} upcoming milestones for user: {}", upcomingMilestones.size(), user.getUserId());
            
            return ResponseEntity.ok(ApiResponse.success(upcomingMilestones, "Lấy danh sách milestone sắp tới thành công"));
            
        } catch (Exception e) {
            log.error("Error retrieving upcoming milestones for user {}: {}", user.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi lấy milestone sắp tới",
                            e.getMessage(),
                            "UPCOMING_MILESTONES_ERROR"));
        }
    }
    
    /**
     * Lấy danh sách thành tựu đã đạt được
     * 
     * Trả về danh sách các chỉ số sức khỏe đã hoàn thành
     */
    @GetMapping("/achievements")
    @Operation(
        summary = "Lấy danh sách thành tựu đã đạt được",
        description = "Trả về danh sách các milestone sức khỏe đã hoàn thành"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Lấy danh sách thành tựu thành công",
            content = @Content(schema = @Schema(implementation = HealthMetricDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Chưa đăng nhập hoặc token không hợp lệ"
        )
    })
    public ResponseEntity<ApiResponse<List<HealthMetricDTO>>> getAchievements(
            @AuthenticationPrincipal User user,
            @Parameter(description = "Số lượng thành tựu muốn lấy (mặc định: 10)")
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Getting achievements for user: {} with limit: {}", user.getUserId(), limit);
        
        try {
            HealthOverviewDTO overview = healthMetricService.getHealthOverview(user);
            List<HealthMetricDTO> achievements = overview.getRecentAchievements();
            
            // Giới hạn số lượng kết quả
            if (achievements.size() > limit) {
                achievements = achievements.subList(0, limit);
            }
            
            log.info("Successfully retrieved {} achievements for user: {}", achievements.size(), user.getUserId());
            
            return ResponseEntity.ok(ApiResponse.success(achievements, "Lấy danh sách thành tựu thành công"));
            
        } catch (Exception e) {
            log.error("Error retrieving achievements for user {}: {}", user.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi lấy thành tựu",
                            e.getMessage(),
                            "ACHIEVEMENTS_ERROR"));
        }
    }
    
    /**
     * Cập nhật tiến độ health metrics
     * 
     * Endpoint này sẽ tính toán lại tiến độ dựa trên thời gian bỏ thuốc hiện tại
     */
    @PostMapping("/metrics/update-progress")
    @Operation(
        summary = "Cập nhật tiến độ health metrics",
        description = "Tính toán lại tiến độ các chỉ số sức khỏe dựa trên thời gian bỏ thuốc hiện tại"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Cập nhật tiến độ thành công"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Chưa đăng nhập hoặc token không hợp lệ"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Lỗi hệ thống khi cập nhật tiến độ"
        )
    })
    public ResponseEntity<ApiResponse<String>> updateHealthMetricsProgress(
            @AuthenticationPrincipal User user) {
        
        log.info("Updating health metrics progress for user: {}", user.getUserId());
        
        try {
            healthMetricService.updateHealthMetricsProgress(user);
            
            log.info("Successfully updated health metrics progress for user: {}", user.getUserId());
            
            return ResponseEntity.ok(ApiResponse.success("Cập nhật tiến độ thành công", "Tiến độ sức khỏe đã được cập nhật"));
            
        } catch (Exception e) {
            log.error("Error updating health metrics progress for user {}: {}", user.getUserId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi cập nhật tiến độ",
                            e.getMessage(),
                            "UPDATE_PROGRESS_ERROR"));
        }
    }
} 