package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.CoachProfile;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.CoachSpecialtiesUpdateDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coachschedule.CoachSimpleResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.CoachService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coach")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class CoachController {
    private final CoachService coachService;


    @Operation(summary = "Cập nhật chuyên môn của huấn luyện viên",
            description = "Huấn luyện viên có thể cập nhật chuyên môn của mình. " +
                    "Chuyên môn là một tập hợp các giá trị từ enum CoachSpecialty. " +
                    "Nếu không có chuyên môn nào được cung cấp, chuyên môn sẽ được đặt thành rỗng.",
            tags = {"Coach"})
    @PutMapping("/my-specialties")
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiResponse<CoachSimpleResponseDTO>> updateCoachSpecialties (
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid CoachSpecialtiesUpdateDTO coachSpecialtiesUpdateDTO) {
        UUID coachId = ((User) userDetails).getUserId();
        CoachSimpleResponseDTO response = coachService.updateCoachSpecialties(coachId, coachSpecialtiesUpdateDTO.getSpecialties());
        return ResponseEntity.ok(ApiResponse.success(response, "Cập nhật chuyên môn thành công"));
    }

    @Operation(summary = "Lấy thông tin cơ bản của huấn luyện viên",
            description = "Lấy thông tin cơ bản của huấn luyện viên dựa trên ID người dùng.",
            tags = {"Coach"})
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CoachProfile>> getCoachProfile(
            @PathVariable UUID userId) {
//        UUID coachId = ((User) userDetails).getUserId();
        CoachProfile response = coachService.getCoachProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "Lấy thông tin huấn luyện viên thành công"));
    }
}
