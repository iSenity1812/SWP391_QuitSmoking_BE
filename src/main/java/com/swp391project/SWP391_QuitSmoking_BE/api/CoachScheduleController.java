package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachScheduleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.CoachScheduleService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/coaches/schedules")
@RequiredArgsConstructor
@SecurityRequirement(name = "booking_schedule_api")
public class CoachScheduleController {
    private final CoachScheduleService coachScheduleService;
    private final UserService userService;

    /**
     * API cho Coach tạo lịch
     */
    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> createCoachSchedules(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<CoachScheduleRequestDTO> requests) {
        UUID coachId = userService.getUserIdFromUserDetails(userDetails);
        List<CoachScheduleResponseDTO> createdSchedules = coachScheduleService.createCoachSchedules(coachId, requests);
        if (createdSchedules.isEmpty() && !requests.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(createdSchedules, "Các lịch trình đã tồn tại hoặc không có lịch trình mới được tạo."));
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdSchedules, "Tạo lịch trình huấn luyện viên thành công"));
    }

    /**
     * API cho Coach: Lấy tất cả lịch trình của chính mình (bao gồm cả đã đặt và chưa đặt).
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> getMyCoachSchedules(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID coachId = userService.getUserIdFromUserDetails(userDetails);
        List<CoachScheduleResponseDTO> schedules = coachScheduleService.getMyCoachSchedules(coachId); // Đổi tên DTO
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(schedules, "Lấy danh sách lịch trình của bạn thành công"));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> getAvailableCoachSchedules(
            @RequestParam UUID coachId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CoachScheduleResponseDTO> schedules = coachScheduleService.getAvailableCoachSchedules(coachId, startDate, endDate); // Đổi tên DTO
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(schedules, "Lấy danh sách lịch trống của huấn luyện viên thành công"));
    }

    /**
     * API cho Admin/Super Admin: Lấy tất cả lịch trình trống của TẤT CẢ các Coach trong một khoảng thời gian.
     * Dùng để quản lý hoặc hiển thị tổng quan.
     */
    @GetMapping("/all-available")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN', 'PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> getAllAvailableSchedules(
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CoachScheduleResponseDTO> schedules = coachScheduleService.getAllAvailableSchedules(startDate, endDate); // Đổi tên DTO
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(schedules, "Lấy danh sách tất cả lịch trống thành công"));
    }

    /**
     * API cho Coach/Admin: Xóa mềm một lịch trình (chỉ khi chưa được đặt).
     * Yêu cầu ID của lịch trình cần xóa.
     */
    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasAnyRole('COACH', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCoachSchedule(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID currentUserId = userService.getUserIdFromUserDetails(userDetails);
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONTENT_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        coachScheduleService.softDeleteCoachSchedule(scheduleId, currentUserId, isAdmin);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Xóa lịch trình huấn luyện viên thành công"));
    }

    /**
     * API cho Coach: Lấy tất cả lịch trình của chính mình trong một khoảng thời gian có phân trang.
     * @param startDate Ngày bắt đầu (mặc định hôm nay nếu không có).
     * @param endDate Ngày kết thúc (mặc định 7 ngày sau nếu không có).
     * @param page Số trang (mặc định 0).
     * @param size Kích thước trang (mặc định 10).
     * @param sort Sắp xếp (ví dụ: scheduleDate,asc).
     */
    @GetMapping("/my-paged")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<Page<CoachScheduleResponseDTO>>> getMyCoachSchedulesPaged(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduleDate,asc") String[] sort) {

        UUID coachId = userService.getUserIdFromUserDetails(userDetails);

        // Thiết lập khoảng ngày mặc định nếu không được cung cấp
        LocalDate actualStartDate = (startDate != null) ? startDate : LocalDate.now();
        LocalDate actualEndDate = (endDate != null) ? endDate : actualStartDate.plusWeeks(1).minusDays(1); // Mặc định 1 tuần

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<CoachScheduleResponseDTO> schedules = coachScheduleService.getMyCoachSchedulesPaged(coachId, actualStartDate, actualEndDate, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(schedules, "Lấy danh sách lịch trình của bạn có phân trang thành công"));
    }

    /**
     * API cho Member/Admin: Lấy lịch trình trống của một Coach cụ thể trong một khoảng thời gian có phân trang.
     * @param coachId ID của Coach cần xem lịch
     * @param startDate Ngày bắt đầu tìm kiếm
     * @param endDate Ngày kết thúc tìm kiếm
     * @param page Số trang
     * @param size Kích thước trang
     * @param sort Sắp xếp
     */
    @GetMapping("/available-paged")
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<CoachScheduleResponseDTO>>> getAvailableCoachSchedulesPaged(
            @RequestParam UUID coachId,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduleDate,asc") String[] sort) {

        LocalDate actualStartDate = (startDate != null) ? startDate : LocalDate.now();
        LocalDate actualEndDate = (endDate != null) ? endDate : actualStartDate.plusMonths(1); // Mặc định 1 tháng cho tìm kiếm trống

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<CoachScheduleResponseDTO> schedules = coachScheduleService.getAvailableCoachSchedulesPaged(coachId, actualStartDate, actualEndDate, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(schedules, "Lấy danh sách lịch trống của huấn luyện viên có phân trang thành công"));
    }

    /**
     * API cho admin: Lấy tất cả lịch trình trống của TẤT CẢ các Coach trong một khoảng thời gian có phân trang.
     */
    @GetMapping("/all-available-paged")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN', 'PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<Page<CoachScheduleResponseDTO>>> getAllAvailableSchedulesPaged(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduleDate,asc") String[] sort) {

        LocalDate actualStartDate = (startDate != null) ? startDate : LocalDate.now();
        LocalDate actualEndDate = (endDate != null) ? endDate : actualStartDate.plusMonths(1);

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<CoachScheduleResponseDTO> schedules = coachScheduleService.getAllAvailableSchedulesPaged(actualStartDate, actualEndDate, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(schedules, "Lấy danh sách tất cả lịch trống có phân trang thành công"));
    }

    /**
     * API cho Coach: Lấy N lịch hẹn sắp tới của chính mình.
     * @param limit Số lượng lịch hẹn sắp tới muốn lấy (mặc định 2).
     */
    @GetMapping("/my-upcoming")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> getUpcomingMyCoachSchedules(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "2") int limit) {
        UUID coachId = userService.getUserIdFromUserDetails(userDetails);
        List<CoachScheduleResponseDTO> schedules = coachScheduleService.getUpcomingCoachSchedules(coachId, limit);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(schedules, "Lấy các lịch trình sắp tới của bạn thành công"));
    }

}
