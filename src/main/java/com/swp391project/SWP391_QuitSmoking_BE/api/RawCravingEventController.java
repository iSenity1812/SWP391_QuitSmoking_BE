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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class RawCravingEventController {
    private final RawCravingTrackingService rawCravingTrackingService;

    //Log một sự kiện thô về cơn thèm/hút thuốc
    //dữ liệu này được lưu tạm thời vào Redis và sẽ được tổng hợp sau
    @PostMapping("/checkin")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<RawTrackingResponse>> logRawCravingEvent(@Valid @RequestBody RawTrackingCreateRequest request) {
        RawTrackingResponse createdEvent = rawCravingTrackingService.createRawCravingEvent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED) // HTTP 201 Created là chuẩn khi tạo tài nguyên mới thành công
                .body(ApiResponse.success(createdEvent, "Tạo bản ghi mới thành công"));
    }

    // Không cần các endpoint GET/PUT/DELETE cho raw events vì chúng chỉ là tạm thời và không được truy vấn trực tiếp
}
