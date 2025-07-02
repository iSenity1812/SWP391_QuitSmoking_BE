package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanAdminResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanCreateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanUpdateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.QuitPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
@RequestMapping("/quit-plans")
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

    private UUID getAuthenticatedMemberId(UserDetails userDetails) {
        if (!(userDetails instanceof User currentUser)) {
            throw new AccessDeniedException("Người dùng chưa được xác thực hoặc không có quyền truy cập.");
        }
        return currentUser.getUserId();
    }

    // -- Create --
    @Operation(summary = "Tạo kế hoạch bỏ thuốc lá",
            description = "Chỉ dành cho thành viên đã đăng nhập (NORMAL_MEMBER hoặc PREMIUM_MEMBER)")
    @PostMapping
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> createQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuitPlanCreateRequestDTO request) {
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            QuitPlanResponseDTO quitPlan = quitPlanService.createQuitPlan(memberId, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(quitPlan, "Tạo kế hoạch bỏ thuốc lá thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi tạo kế hoạch bỏ thuốc lá: " + e.getMessage()));
        }
    }

    // -- Get All Quit plan of a Member --
    @Operation(summary = "Lấy tất cả kế hoạch của thành viên")
    @GetMapping
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<QuitPlanResponseDTO>>> getAllQuitPlans(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            List<QuitPlanResponseDTO> quitPlans = quitPlanService.getQuitPlansByMemberId(memberId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(quitPlans, "Lấy danh sách kế hoạch bỏ thuốc lá thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi lấy danh sách kế hoạch bỏ thuốc lá: " + e.getMessage()));
        }
    }

    // -- Get By ID (Member) --
    @Operation(summary = "Lấy kế hoạch bỏ thuốc lá theo ID")
    @GetMapping("/{quitPlanId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> getQuitPlanById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer quitPlanId) {
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            QuitPlanResponseDTO quitPlan = quitPlanService.getQuitPlanById(quitPlanId, memberId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(quitPlan, "Lấy kế hoạch bỏ thuốc lá thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi lấy kế hoạch bỏ thuốc lá theo ID: " + e.getMessage()));
        }
    }

    // -- Get current Quit Plan of a Member --
    @Operation(summary = "Lấy kế hoạch bỏ thuốc lá hiện tại của thành viên")
    @GetMapping("/current-plan")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> getCurrentQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            QuitPlanResponseDTO quitPlan = quitPlanService.getCurrentQuitPlanByMemberId(memberId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(quitPlan, "Lấy kế hoạch bỏ thuốc lá hiện tại thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi lấy kế hoạch bỏ thuốc lá hiện tại: " + e.getMessage()));
        }
    }

    // -- Update current Quit Plan Information --
    @Operation(summary = "Cập nhật kế hoạch bỏ thuốc lá hiện tại của thành viên",
            description = "Tự động lấy kế hoạch đang trong quá trình thực hiện (IN_PROGRESS) của người dùng. " +
                    "Cho phép họ cập nhật trên kế hoạch đó")
    @PatchMapping("/current-plan")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> updateCurrentQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QuitPlanUpdateRequestDTO request) {
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            QuitPlanResponseDTO updatedQuitPlan = quitPlanService.updateCurrentActivePlan(memberId, request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(updatedQuitPlan, "Cập nhật kế hoạch bỏ thuốc lá thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi cập nhật kế hoạch bỏ thuốc lá hiện tại: " + e.getMessage()));
        }
    }

    @Operation(summary = "Cập nhật kế hoạch bỏ thuốc lá theo ID",
            description = "Lấy kế hoạch của người dùng thông qua ID và cho phép họ cập nhật trên kế hoạch đó")
    @PatchMapping("/{quitPlanId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> updateQuitPlanById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer quitPlanId,
            @Valid @RequestBody QuitPlanUpdateRequestDTO request) { // Sử dụng Update DTO
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            QuitPlanResponseDTO updatedQuitPlan = quitPlanService.updateQuitPlan(quitPlanId, memberId, request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(updatedQuitPlan, "Cập nhật kế hoạch bỏ thuốc lá thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi cập nhật kế hoạch bỏ thuốc lá: " + e.getMessage()));
        }
    }

    // -- Giveup plan --
    @Operation(summary = "Bỏ kế hoạch hiện tại của thành viên",
    description = "Cho phép thành viên bỏ kế hoạch bỏ thuốc lá hiện tại của họ, thông qua ID kế hoạch)")
    @PostMapping("/giveup/{quitPlanId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> giveUpQuitPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer quitPlanId) {
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            QuitPlanResponseDTO updatedQuitPlan = quitPlanService.giveUpQuitPlan(quitPlanId, memberId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(updatedQuitPlan, "Bỏ kế hoạch bỏ thuốc lá thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi từ bỏ kế hoạch bỏ thuốc lá: " + e.getMessage()));
        }
    }

    // -- RESET QUIT PLAN STATUS --
    @Operation(summary = "Đặt lại trạng thái kế hoạch của thành viên",
            description = "Đặt lại trạng thái của một kế hoạch từ FAILED sang IN_PROGRESS " +
                    "(nếu người dùng chọn \"giữ kế hoạch hiện tại\" sau khi tái nghiện)")
    public ResponseEntity<ApiResponse<QuitPlanResponseDTO>> resetQuitPlanStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Integer quitPlanId) {
        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            QuitPlanResponseDTO response = quitPlanService.resetQuitPlanToInProgress(quitPlanId, memberId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(response, "Kế hoạch đã được đặt lại trạng thái IN_PROGRESS."));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi đặt lại trạng thái kế hoạch: " + e.getMessage()));
        }
    }

    // --- ADMIN ENDPOINTS ---

    // -- ADMIN: Get All Quit Plans --
    @Operation(summary = "Lấy tất cả kế hoạch bỏ thuốc (Chỉ dành cho SUPER_ADMIN)")
    @GetMapping("/superadmin/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuitPlanAdminResponseDTO>>> getAllQuitPlansForAdmin() {
        try {
            List<QuitPlanAdminResponseDTO> quitPlans = quitPlanService.getAllQuitPlansForAdmin();
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(quitPlans, "Lấy danh sách tất cả kế hoạch bỏ thuốc lá thành công"));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi lấy tất cả kế hoạch bỏ thuốc lá cho admin: " + e.getMessage()));
        }
    }

    // -- ADMIN: Get Quit Plan By ID --
    @Operation(summary = "Lấy kế hoạch bỏ thuốc theo ID (Chỉ dành cho SUPER_ADMIN)")
    @GetMapping("/superadmin/{quitPlanId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<QuitPlanAdminResponseDTO>> getQuitPlanByIdForAdmin(
            @PathVariable Integer quitPlanId) {
        try {
            QuitPlanAdminResponseDTO quitPlan = quitPlanService.getQuitPlanByIdForAdmin(quitPlanId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(quitPlan, "Lấy kế hoạch bỏ thuốc lá thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi lấy kế hoạch bỏ thuốc lá cho admin: " + e.getMessage()));
        }
    }

    // -- ADMIN: Get all quit plan by member ID --
    @Operation(summary = "Lấy tất cả kế hoạch của thành viên theo ID (Chỉ dành cho SUPER_ADMIN)")
    @GetMapping("/superadmin/member/{memberId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<QuitPlanAdminResponseDTO>>> getQuitPlansByMemberIdForAdmin(
            @PathVariable UUID memberId) {
        try {
            List<QuitPlanAdminResponseDTO> quitPlans = quitPlanService.getQuitPlansByMemberIdForAdmin(memberId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(quitPlans, "Lấy danh sách kế hoạch bỏ thuốc lá của thành viên thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi lấy kế hoạch bỏ thuốc lá của thành viên cho admin: " + e.getMessage()));
        }
    }

    // -- ADMIN: Delete Quit Plan by ID --
    @Operation(summary = "Xóa kế hoạch bỏ thuốc theo ID (Chỉ dành cho SUPER_ADMIN)")
    @DeleteMapping("/superadmin/{quitPlanId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteQuitPlanByIdForAdmin(
            @PathVariable Integer quitPlanId) {
        try {
            quitPlanService.deleteQuitPlanById(quitPlanId);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body(ApiResponse.success(null, "Xóa kế hoạch bỏ thuốc lá thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi xóa kế hoạch bỏ thuốc lá cho admin: " + e.getMessage()));
        }
    }
}
