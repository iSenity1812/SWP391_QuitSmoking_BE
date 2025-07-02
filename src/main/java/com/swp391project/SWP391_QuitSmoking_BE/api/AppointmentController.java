package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.appointment.AppointmentUserResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.enums.AppointmentStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.AppointmentService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@SecurityRequirement(name = "appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserService userService;

    /**
     * API cho Member: Đặt lịch hẹn mới.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'COACH')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> createAppointment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AppointmentRequestDTO request) {
        UUID memberId = userService.getUserIdFromUserDetails(userDetails);
        List<Role> callerRoles = userDetails.getAuthorities().stream()
                .map(ga -> Role.valueOf(ga.getAuthority().replace("ROLE_", "")))
                .collect(Collectors.toList());
        AppointmentResponseDTO response = appointmentService.createAppointment(memberId, callerRoles, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Đặt lịch hẹn thành công"));
    }

    /**
     * API cho Member: Lấy các lịch hẹn của chính mình, có phân trang và lọc theo khoảng ngày.
     */
    @GetMapping("/my-appointments")
    @PreAuthorize("hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<Page<AppointmentUserResponseDTO>>> getMyAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "coachSchedule.scheduleDate,asc") String[] sort) {

        UUID memberId = userService.getUserIdFromUserDetails(userDetails);
        LocalDate actualStartDate = (startDate != null) ? startDate : LocalDate.now().minusMonths(1); // Mặc định 1 tháng trước
        LocalDate actualEndDate = (endDate != null) ? endDate : LocalDate.now().plusMonths(3); // Mặc định 3 tháng tới

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<AppointmentUserResponseDTO> appointments = appointmentService.getMemberAppointments(memberId, actualStartDate, actualEndDate, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(appointments, "Lấy danh sách lịch hẹn của bạn thành công"));
    }


    /**
     * API cho Coach: Lấy các lịch hẹn mà thành viên đã đặt với Coach đó, có phân trang và lọc.
     */
    @GetMapping("/coach-appointments")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getCoachAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<AppointmentStatus> statuses, // Cho phép lọc theo nhiều trạng thái
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "coachSchedule.scheduleDate,asc") String[] sort) {

        UUID coachId = userService.getUserIdFromUserDetails(userDetails);
        LocalDate actualStartDate = (startDate != null) ? startDate : LocalDate.now().minusMonths(1);
        LocalDate actualEndDate = (endDate != null) ? endDate : LocalDate.now().plusMonths(3);

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<AppointmentResponseDTO> appointments = appointmentService.getCoachAppointments(coachId, actualStartDate, actualEndDate, statuses, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(appointments, "Lấy danh sách lịch hẹn của huấn luyện viên thành công"));
    }


    /**
     * API cho Admin/Super Admin: Lấy tất cả lịch hẹn trong hệ thống, có phân trang và lọc.
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getAllAppointments(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<AppointmentStatus> statuses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "coachSchedule.scheduleDate,asc") String[] sort) {

        LocalDate actualStartDate = (startDate != null) ? startDate : LocalDate.now().minusMonths(1);
        LocalDate actualEndDate = (endDate != null) ? endDate : LocalDate.now().plusMonths(3);

        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments(actualStartDate, actualEndDate, statuses, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(appointments, "Lấy tất cả lịch hẹn thành công"));
    }

    /**
     * API cho Member: Lấy các lịch hẹn sắp tới của chính mình (1-2 lịch gần nhất).
     */
    @GetMapping("/my-upcoming")
    @PreAuthorize("hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> getUpcomingMemberAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "2") int limit) {
        UUID memberId = userService.getUserIdFromUserDetails(userDetails);
        List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingMemberAppointments(memberId, limit);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(appointments, "Lấy các lịch hẹn sắp tới của bạn thành công"));
    }

    /**
     * API cho Coach: Lấy các lịch hẹn sắp tới của chính mình (1-2 lịch gần nhất).
     */
    @GetMapping("/coach-upcoming")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<List<AppointmentResponseDTO>>> getUpcomingCoachAppointments(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "2") int limit) {
        UUID coachId = userService.getUserIdFromUserDetails(userDetails);
        List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingCoachAppointments(coachId, limit);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(appointments, "Lấy các lịch hẹn sắp tới của bạn thành công"));
    }

    /**
     * API cho Member/Coach Hủy lịch hẹn, có thể cân nhắc thêm admin
     */
    @PutMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> cancelAppointment(
            @PathVariable Long appointmentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID currentUserId = userService.getUserIdFromUserDetails(userDetails);
//        boolean isAdmin = userDetails.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        AppointmentResponseDTO response = appointmentService.cancelAppointment(appointmentId, currentUserId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Hủy lịch hẹn thành công"));
    }

    /**
     * API cho Coach/Admin: Cập nhật trạng thái lịch hẹn.
     * Có thể dùng để đánh dấu COMPLETED, MISSED
     */
    @PutMapping("/{appointmentId}/status")
    @PreAuthorize("hasAnyRole('COACH', 'CONTENT_ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam AppointmentStatus newStatus, // Nhận trạng thái mới từ request param
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID currentUserId = userService.getUserIdFromUserDetails(userDetails);
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        AppointmentResponseDTO response = appointmentService.updateAppointmentStatus(appointmentId, newStatus, currentUserId, isAdmin);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(response, "Cập nhật trạng thái lịch hẹn thành công"));
    }
}
