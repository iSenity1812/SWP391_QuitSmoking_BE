package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthMetricDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.health.HealthOverviewDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.HealthMetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "Health Tracking API", description = "API để theo dõi tiến độ sức khỏe sau khi bỏ thuốc")
@Slf4j
public class HealthController {

    private final HealthMetricService healthMetricService;

    /**
     * Lấy health overview cho user hiện tại
     */
    @GetMapping("/overview")
    @Operation(summary = "Lấy health overview", description = "Lấy tổng quan tiến độ sức khỏe của user")
    public ResponseEntity<ApiResponse<HealthOverviewDTO>> getHealthOverview(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            HealthOverviewDTO overview = healthMetricService.getHealthOverview(user);
            
            log.info("Health overview retrieved for user: {}", user.getUserId());
            return ResponseEntity.ok(ApiResponse.success(overview, "Lấy health overview thành công"));
        } catch (Exception e) {
            log.error("Error getting health overview: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy health overview: " + e.getMessage()));
        }
    }

    /**
     * Lấy tất cả health metrics cho user hiện tại
     */
    @GetMapping("/metrics")
    @Operation(summary = "Lấy health metrics", description = "Lấy tất cả health metrics của user")
    public ResponseEntity<ApiResponse<List<HealthMetricDTO>>> getHealthMetrics(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            List<HealthMetricDTO> metrics = healthMetricService.getUserHealthMetrics(user);
            
            log.info("Health metrics retrieved for user: {}", user.getUserId());
            return ResponseEntity.ok(ApiResponse.success(metrics, "Lấy health metrics thành công"));
        } catch (Exception e) {
            log.error("Error getting health metrics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy health metrics: " + e.getMessage()));
        }
    }

    /**
     * Cập nhật health metrics progress cho user hiện tại
     */
    @PostMapping("/update-progress")
    @Operation(summary = "Cập nhật health progress", description = "Cập nhật tiến độ health metrics dựa trên penalty")
    public ResponseEntity<ApiResponse<Void>> updateHealthProgress(Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            healthMetricService.updateHealthMetricsProgress(user);
            
            log.info("Health progress updated for user: {}", user.getUserId());
            return ResponseEntity.ok(ApiResponse.success("Cập nhật health progress thành công"));
        } catch (Exception e) {
            log.error("Error updating health progress: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi cập nhật health progress: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint để lấy sample health metrics
     */
    @GetMapping("/test/sample-metrics")
    @Operation(summary = "Test sample metrics", description = "Lấy sample health metrics để test")
    public ResponseEntity<ApiResponse<List<HealthMetricDTO>>> getSampleHealthMetrics() {
        try {
            List<HealthMetricDTO> sampleMetrics = healthMetricService.createSampleHealthMetrics();
            
            log.info("Sample health metrics generated for testing");
            return ResponseEntity.ok(ApiResponse.success(sampleMetrics, "Lấy sample health metrics thành công"));
        } catch (Exception e) {
            log.error("Error getting sample health metrics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi lấy sample health metrics: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint để kiểm tra logic penalty và progress calculation
     */
    @GetMapping("/test/penalty-logic")
    @Operation(summary = "Test penalty logic", description = "Kiểm tra logic penalty và progress calculation")
    public ResponseEntity<ApiResponse<String>> testPenaltyLogic() {
        try {
            healthMetricService.testPenaltyAndProgressLogic();
            
            log.info("Penalty logic test completed");
            return ResponseEntity.ok(ApiResponse.success("Test penalty logic thành công. Xem log để biết chi tiết.", "Test penalty logic thành công"));
        } catch (Exception e) {
            log.error("Error testing penalty logic: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi test penalty logic: " + e.getMessage()));
        }
    }

    /**
     * Test endpoint để kiểm tra thời gian bỏ thuốc
     */
    @GetMapping("/test/quit-time")
    @Operation(summary = "Test quit time calculation", description = "Kiểm tra tính toán thời gian bỏ thuốc")
    public ResponseEntity<ApiResponse<String>> testQuitTime() {
        try {
            String result = healthMetricService.testQuitTimeCalculation();
            log.info("Quit time test completed: {}", result);
            return ResponseEntity.ok(ApiResponse.success(result, "Test quit time thành công"));
        } catch (Exception e) {
            log.error("Error testing quit time: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi khi test quit time: " + e.getMessage()));
        }
    }
} 