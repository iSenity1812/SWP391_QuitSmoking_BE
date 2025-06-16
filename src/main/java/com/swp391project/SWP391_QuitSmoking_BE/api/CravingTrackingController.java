package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.CravingTrackingService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class CravingTrackingController {
    private final CravingTrackingService cravingTrackingService;
    private final UserService userService;

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
    @GetMapping("/{dailySummaryId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<CravingTrackingResponse>>> getCravingTrackingsByDailySummaryId(@PathVariable Integer dailySummaryId) {
        List<CravingTrackingResponse> response = cravingTrackingService.getCravingTrackingResponsesByDailySummaryId(dailySummaryId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Lấy danh sách bản ghi thành công"));
    }

    //Lấy tất cả bản ghi (đã tổng hợp) cho một ngày cụ thể
    @GetMapping("/{date}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<CravingTrackingResponse>>> getCravingTrackingsByDate(@PathVariable LocalDate date) {
        List<CravingTrackingResponse> response = cravingTrackingService.getCravingTrackingsByDate(date);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Lấy danh sách bản ghi thành công"));
    }

    //Cập nhật thông tin một bản ghi
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<CravingTrackingResponse>> updateCravingTracking(@PathVariable Integer id, @Valid @RequestBody CravingTrackingUpdateRequest request) {
        CravingTrackingResponse updated = cravingTrackingService.updateCravingTracking(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updated, "Bản ghi đã được cập nhật thành công"));
    }

    //Xóa bản ghi theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCravingTracking(@PathVariable Integer id) {
        cravingTrackingService.deleteCravingTracking(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT) // HTTP 204 No Content là chuẩn khi xóa thành công
                .body(ApiResponse.success(null, "Bản ghi đã được xóa thành công"));
    }
}
