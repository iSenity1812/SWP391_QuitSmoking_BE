package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.timeslot.TimeSlotResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.TimeSlotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timeslots")
@RequiredArgsConstructor
@SecurityRequirement(name = "booking_schedule_api")
public class TimeSlotController {
    private final TimeSlotService timeSlotService;

    // API cho admin tạo time slot mặc định
    @PostMapping("/generate-default")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TimeSlotResponseDTO>>> generateDefaultTimeSlots() {
        List<TimeSlotResponseDTO> generatedSlots = timeSlotService.generateDefaultTimeSlots();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(generatedSlots, "Tao time slot mặc định thành công"));
    }

    /**
     * API cho user (Member/Coach) lấy danh sách time slot đang hoạt dộng
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('NORMAL_MEMBER', 'PREMIUM_MEMBER', 'COACH', 'SUPER_ADMIN')") // Đã thêm SUPER_ADMIN
    public ResponseEntity<ApiResponse<List<TimeSlotResponseDTO>>> getAllTimeSlots() {
        List<TimeSlotResponseDTO> timeSlots = timeSlotService.getAllActiveTimeSlots();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(timeSlots, "Lấy danh sách tất cả time slot thành công"));
    }

    /**
     * API cho Admin: Đánh dấu một time slot là đã xóa (soft delete) hoặc khôi phục.
     * Chỉ người dùng có vai trò ADMIN hoặc SUPER_ADMIN mới có quyền truy cập.
     */
    @PutMapping("/{id}/toggle-deletion")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')") // Đã thêm SUPER_ADMIN
    public ResponseEntity<ApiResponse<TimeSlotResponseDTO>> toggleTimeSlotDeletion(@PathVariable Integer id,
                                                                                @RequestParam boolean isDeleted) {
        TimeSlotResponseDTO updatedSlot = timeSlotService.toggleTimeSlotDeletion(id, isDeleted);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedSlot, "Cập nhật trạng thái time slot thành công"));
    }
}
