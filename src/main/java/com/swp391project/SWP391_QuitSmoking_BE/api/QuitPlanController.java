package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanCreateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanResponseDTO;
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

}
