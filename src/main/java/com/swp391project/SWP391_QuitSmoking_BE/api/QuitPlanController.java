package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanAdminResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanCreateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanUpdateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.QuitPlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/quit-plans")
@RequiredArgsConstructor
@SecurityRequirement(name = "quit_plan_api")
public class QuitPlanController {
    private final QuitPlanService quitPlanService;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ApiResponse.validationError(HttpStatus.BAD_REQUEST, "Dữ liệu nhập vào không hợp lệ", errors);
    }

    // -- Create --
    @PostMapping
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> createQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuitPlanCreateRequestDTO request) {
        if (!(userDetails instanceof User currentUser)) {
            // Xử lý lỗi nếu userDetails không phải là instance của User
            // Điều này thường không xảy ra nếu UserDetailsService của bạn trả về đối tượng User
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"));
        }

        UUID memberId = currentUser.getUserId();

        QuitPlanResponseDTO quitPlan = quitPlanService.createQuitPlan(memberId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(quitPlan, "Tạo kế hoạch bỏ thuốc lá thành công"));
    }

    // -- Get All Quit plan of a Member --
    @GetMapping
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<QuitPlanResponseDTO>>> getAllQuitPlans(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (!(userDetails instanceof User currentUser)) {
            // Xử lý lỗi nếu userDetails không phải là instance của User
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"));
        }

        UUID memberId = currentUser.getUserId();
        List<QuitPlanResponseDTO> quitPlans = quitPlanService.getQuitPlansByMemberId(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(quitPlans, "Lấy danh sách kế hoạch bỏ thuốc lá thành công"));
    }

    // -- Get By ID (Member) --
    @GetMapping("/{quitPlanId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> getQuitPlanById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer quitPlanId) {
        if (!(userDetails instanceof User currentUser)) {
            // Xử lý lỗi nếu userDetails không phải là instance của User
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"));
        }

        UUID memberId = currentUser.getUserId();
        QuitPlanResponseDTO quitPlan = quitPlanService.getQuitPlanById(quitPlanId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(quitPlan, "Lấy kế hoạch bỏ thuốc lá thành công"));
    }

    // -- Get current Quit Plan of a Member --
    @GetMapping("/current-plan")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> getCurrentQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (!(userDetails instanceof User currentUser)) {
            // Xử lý lỗi nếu userDetails không phải là instance của User
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"));
        }

        UUID memberId = currentUser.getUserId();
        QuitPlanResponseDTO quitPlan = quitPlanService.getCurrentQuitPlanByMemberId(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(quitPlan, "Lấy kế hoạch bỏ thuốc lá hiện tại thành công"));
    }

    // -- Update current Quit Plan Information --
    @PatchMapping("/current-plan")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> updateCurrentQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuitPlanUpdateRequestDTO request) {
        if (!(userDetails instanceof User currentUser)) {
            // Xử lý lỗi nếu userDetails không phải là instance của User
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"));
        }

        UUID memberId = currentUser.getUserId();
        QuitPlanResponseDTO updatedQuitPlan = quitPlanService.updateCurrentActivePlan(memberId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedQuitPlan, "Cập nhật kế hoạch bỏ thuốc lá thành công"));
    }

    @PatchMapping("/{quitPlanId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> updateQuitPlanById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer quitPlanId,
            @Valid @RequestBody QuitPlanUpdateRequestDTO request) { // Sử dụng Update DTO
        if (!(userDetails instanceof User currentUser)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"));
        }
        UUID memberId = currentUser.getUserId();
        QuitPlanResponseDTO updatedQuitPlan = quitPlanService.updateQuitPlan(quitPlanId, memberId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedQuitPlan, "Cập nhật kế hoạch bỏ thuốc lá thành công"));
    }

    // -- Giveup plan --
    @PostMapping("/giveup/{quitPlanId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> giveUpQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer quitPlanId) {
        if (!(userDetails instanceof User currentUser)) {
            // Xử lý lỗi nếu userDetails không phải là instance của User
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện hành động này"));
        }

        UUID memberId = currentUser.getUserId();
        QuitPlanResponseDTO updatedQuitPlan = quitPlanService.giveUpQuitPlan(quitPlanId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedQuitPlan, "Bỏ kế hoạch bỏ thuốc lá thành công"));
    }

    // -- ADMIN: Get All Quit Plans --
    @GetMapping("/superadmin/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuitPlanAdminResponseDTO>>> getAllQuitPlansForAdmin() {
        List<QuitPlanAdminResponseDTO> quitPlans = quitPlanService.getAllQuitPlansForAdmin();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(quitPlans, "Lấy danh sách tất cả kế hoạch bỏ thuốc lá thành công"));
    }

    // -- ADMIN: Get Quit Plan By ID --
    @GetMapping("/superadmin/{quitPlanId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<QuitPlanAdminResponseDTO>> getQuitPlanByIdForAdmin(
            @PathVariable Integer quitPlanId) {
        QuitPlanAdminResponseDTO quitPlan = quitPlanService.getQuitPlanByIdForAdmin(quitPlanId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(quitPlan, "Lấy kế hoạch bỏ thuốc lá thành công"));
    }

    // -- ADMIN: Get all quit plan by member ID --
    @GetMapping("/superadmin/member/{memberId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuitPlanAdminResponseDTO>>> getQuitPlansByMemberIdForAdmin(
            @PathVariable UUID memberId) {
        List<QuitPlanAdminResponseDTO> quitPlans = quitPlanService.getQuitPlansByMemberIdForAdmin(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(quitPlans, "Lấy danh sách kế hoạch bỏ thuốc lá của thành viên thành công"));
    }

    // -- ADMIN: Delete Quit Plan by ID --
    @DeleteMapping("/superadmin/{quitPlanId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuitPlanByIdForAdmin(
            @PathVariable Integer quitPlanId) {
        quitPlanService.deleteQuitPlanById(quitPlanId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Xóa kế hoạch bỏ thuốc lá thành công"));
    }
}
