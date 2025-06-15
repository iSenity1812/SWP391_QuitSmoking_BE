package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserProfile;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.CravingTrackingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class CravingTrackingController {
    private final CravingTrackingService cravingTrackingService;

     //lấy thông tin một bản ghi (đã tổng hợp) theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<CravingTrackingResponse>> getCravingTrackingById(@PathVariable Integer id) {
        CravingTrackingResponse response = cravingTrackingService.getCravingTrackingById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Lấy thông tin bản ghi thành công"));
    }

    //Lấy tất cả bản ghi (đã tổng hợp) cho một dailySummary cụ thể
    @GetMapping("/daily-summary/{dailySummaryId}")
    public ResponseEntity<List<CravingTrackingResponse>> getCravingTrackingsByDailySummaryId(@PathVariable Integer dailySummaryId) {
        List<CravingTrackingResponseDTO> response = cravingTrackingService.getCravingTrackingsByDailySummaryId(dailySummaryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Cập nhật thông tin một bản ghi theo dõi cơn thèm (đã tổng hợp).
     * Chỉ cho phép cập nhật Situation và WithWhom. SmokedCount/CravingsCount được quản lý bởi tổng hợp.
     * Endpoint: PUT /api/craving-tracking/{id}
     * @param id ID của bản ghi cần cập nhật.
     * @param request CravingTrackingUpdateRequestDTO chứa thông tin cập nhật.
     * @return ResponseEntity chứa CravingTrackingResponseDTO đã cập nhật và HTTP status 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CravingTrackingResponseDTO> updateCravingTracking(@PathVariable Integer id, @Valid @RequestBody CravingTrackingUpdateRequestDTO request) {
        CravingTrackingResponseDTO updated = cravingTrackingService.updateCravingTracking(id, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    /**
     * Xóa một bản ghi theo dõi cơn thèm (đã tổng hợp) theo ID.
     * Endpoint: DELETE /api/craving-tracking/{id}
     * @param id ID của bản ghi cần xóa.
     * @return ResponseEntity với HTTP status 204 No Content nếu xóa thành công.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCravingTracking(@PathVariable Integer id) {
        cravingTrackingService.deleteCravingTracking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
