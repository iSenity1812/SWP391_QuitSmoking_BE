package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.RawTrackingCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.RawTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.RawCravingTrackingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkin")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class RawCravingEventController {
    private final RawCravingTrackingService rawCravingTrackingService;

    //Log một sự kiện thô về cơn thèm/hút thuốc
    //dữ liệu này được lưu tạm thời vào Redis và sẽ được tổng hợp sau
    @PostMapping
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<RawTrackingResponse>> logRawCravingEvent(@Valid @RequestBody RawTrackingCreateRequest request) {
        RawTrackingResponse createdEvent = rawCravingTrackingService.createRawCravingEvent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED) // HTTP 201 Created là chuẩn khi tạo tài nguyên mới thành công
                .body(ApiResponse.success(createdEvent, "Tạo bản ghi mới thành công"));
    }

    //FE có thể lấy tổng số liệu thô (chưa tổng hợp vào CravingTracking) cho một giờ cụ thể
    //để hiển thị cập nhật tức thì trên UI
    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getHourlyRawTotals(@PathVariable UUID memberId) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Integer> totals = rawCravingTrackingService.getHourlyRawTotals(memberId, now);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(totals, "Lấy tổng số liệu thô thành công."));
    }

    //cho phép người dùng chỉ định một ngày giờ cụ thể, nếu không thì mặc định là ngày giờ hiện tại
//    @GetMapping("/{memberId}")
//    public ResponseEntity<ApiResponse<Map<String, Integer>>> getHourlyRawTotals(
//            @PathVariable UUID memberId,
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) Integer month,
//            @RequestParam(required = false) Integer day,
//            @RequestParam(required = false) Integer hour) {
//
//        LocalDateTime targetHour;
//        if (year == null || month == null || day == null || hour == null) {
//            targetHour = LocalDateTime.now(); // Mặc định là hiện tại nếu thiếu tham số
//        } else {
//            targetHour = LocalDateTime.of(year, month, day, hour, 0); // Dùng tham số nếu có
//        }
//
//        Map<String, Integer> totals = rawCravingTrackingService.getHourlyRawTotals(memberId, targetHour);
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponse.success(totals, "Lấy tổng số liệu thô thành công."));
//    }
}
