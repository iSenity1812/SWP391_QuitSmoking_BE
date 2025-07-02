package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.*;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachRepository;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.CoachScheduleService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coaches/schedules")
@RequiredArgsConstructor
@SecurityRequirement(name = "booking_schedule_api")
public class CoachScheduleController {
    private final CoachScheduleService coachScheduleService;
    private final UserService userService;
    private final CoachRepository coachRepository;

    /**
     * API cho Coach tạo lịch
     */
    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> createCoachSchedules(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody List<CoachScheduleRequestDTO> requests) {
        UUID coachId = userService.getUserIdFromUserDetails(userDetails);
        List<CoachScheduleResponseDTO> createdSchedules = coachScheduleService. createCoachSchedules(coachId, requests);
        if (createdSchedules.isEmpty() && !requests.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
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
        List<CoachScheduleResponseDTO> schedules = coachScheduleService.getMyCoachSchedules(coachId);
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
                .status(HttpStatus.OK)
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

    /**
     * API cho Coach: Lấy lịch trình tuần của một Coach.
     * @param requestedCoachId ID của Coach cần lấy lịch trình (nếu không cung cấp, sẽ lấy của chính Coach đang đăng nhập).
     * @param dateInWeek Ngày trong tuần để lấy lịch trình (nếu không cung cấp, sẽ lấy tuần hiện tại).
     * @return Lịch trình tuần của Coach, bao gồm thông tin cuộc hẹn nếu có.
     */
    @Operation(summary = "Lấy lịch trình tuần của Coach",
            description = "Lấy chi tiết lịch trình đã đăng ký của một Coach trong một tuần cụ thể, bao gồm thông tin cuộc hẹn nếu có. Ngày trong tuần có thể là bất kỳ ngày nào trong tuần đó.")
    @GetMapping("/weekly")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'COACH', 'PREMIUM_MEMBER')") // Premium Member có thể xem lịch trình của coach để đặt
    public ResponseEntity<ApiResponse<WeeklyScheduleResponseDTO>> getCoachWeeklySchedule(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "coachId", required = false) UUID requestedCoachId,
            @RequestParam(name = "dateInWeek", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dateInWeek) {

        UUID currentUserId = userService.getUserIdFromUserDetails(userDetails);
        // Kiểm tra quyền: ADMIN có thể xem của bất kỳ ai. COACH chỉ xem của chính mình. PREMIUM_MEMBER có thể xem của bất kỳ coach nào.
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CONTENT_ADMIN"));
        boolean isCoach = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COACH"));
        boolean isPremiumMember = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PREMIUM_MEMBER"));


        UUID targetCoachId;

        if (requestedCoachId == null) {
            // Nếu coachId không được cung cấp trong request
            if (isCoach) {
                // Nếu người dùng hiện tại là COACH, thì lấy lịch của chính họ
                targetCoachId = currentUserId;
            } else {
                // Nếu không phải COACH (ví dụ: Member, Admin) và không cung cấp coachId, thì không biết lấy lịch của ai
                throw new IllegalArgumentException("Vui lòng cung cấp coachId để xem lịch trình.");
            }
        } else {
            // Nếu coachId được cung cấp trong request
            targetCoachId = requestedCoachId;

            // Kiểm tra quyền truy cập lịch của coach khác
            if (isCoach && !isAdmin && !currentUserId.equals(targetCoachId)) {
                // Coach chỉ được xem lịch của chính mình, trừ khi là Admin
                throw new AccessDeniedException("Bạn không có quyền truy cập lịch trình của coach khác.");
            }
            // Premium Member và Admin có thể xem lịch của coach khác
        }

        // Đảm bảo targetCoachId tồn tại trong hệ thống
        if (!coachRepository.existsById(targetCoachId)) {
            throw new ResourceNotFoundException("Coach not found with ID: " + targetCoachId);
        }

        if (dateInWeek == null) {
            dateInWeek = LocalDate.now(); // Mặc định là tuần hiện tại
        }

        WeeklyScheduleResponseDTO responseData = coachScheduleService.getWeeklyScheduleForCoach(targetCoachId, dateInWeek);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(responseData, "Lấy lịch trình tuần thành công"));
    }

    /**
     * API cho Premium member
     */
    @Operation(summary = "Lấy lịch trình của các Coach trong ngày cụ thể theo khoảng thời gian",
            description = "Lấy danh sách lịch trình của các Coach trong khoảng thời gian cụ thể của ngày cụ thể")
    @GetMapping("/available/today-by-time-range")
    @PreAuthorize("hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> getAvailableSchedulesTodayByTimeRange(
            @RequestParam@org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.TIME) LocalTime endTime
    ) {
        List<CoachScheduleResponseDTO> availableSchedules = coachScheduleService.findAvailableSchedulesTodayByTimeRange(date, startTime, endTime);
        if (availableSchedules.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(availableSchedules, "Không có lịch trình trống trong khoảng thời gian đã chọn"));
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(availableSchedules, "Lấy danh sách lịch trình trống trong khoảng thời gian đã chọn thành công"));
    }

    @Operation(summary = "Lấy lịch trình rảnh của tất cả các Coach trong khoảng ngày và giờ",
            description = "Lấy danh sách lịch trình rảnh của tất cả các Coach trong khoảng ngày và giờ cụ thể")
    @PostMapping("/available/by-date-time-range") // Sử dụng POST nếu bạn dùng RequestBody DTO, hoặc GET với nhiều @RequestParam
    @PreAuthorize("hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> getAvailableSchedulesByDateRangeAndTimeRange(
            @RequestBody AvailableScheduleSearchRequestDTO searchRequest) {

        List<CoachScheduleResponseDTO> availableSchedules = coachScheduleService.findAvailableSchedulesByDateRangeAndTimeRange(searchRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(availableSchedules, "Lấy danh sách lịch rảnh của tất cả các huấn luyện viên trong khoảng ngày và giờ thành công"));
    }

    @Operation(summary = "Lấy lịch trình rảnh của các Coach theo các time slot trong khoảng ngày")
    @PostMapping("/available/by-date-and-timeslot")
    @PreAuthorize("hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<CoachScheduleResponseDTO>>> getAvailableSchedulesByDateAndTimeSlot(
            @RequestBody SearchAvailableScheduleBySlotDTO searchRequest
            ){

        List<CoachScheduleResponseDTO> availableSchedules = coachScheduleService.findAvailableSchedulesByDateAndTimeSlot(searchRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(availableSchedules, "Lấy danh sách lịch trình rảnh của các Coach trong ngày và khoảng thời gian thành công"));
    }

}
