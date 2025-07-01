package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.plan.PlanRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.plan.PlanResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.PlanService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class PlanController {
    private final PlanService planService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PlanResponseDTO>>> getAllPlans() {
        List<PlanResponseDTO> plans = planService.getAllPlans();
        return ResponseEntity.ok(ApiResponse.success(plans, "Lấy danh sách gói thành công."));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> getPlanById(@PathVariable Integer id) {
        PlanResponseDTO plan = planService.getPlanById(id);
        return ResponseEntity.ok(ApiResponse.success(plan, "Lấy thông tin gói thành công."));
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> createPlan(@Valid @RequestBody PlanRequestDTO requestDTO) {
        PlanResponseDTO createdPlan = planService.createPlan(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdPlan, "Tạo gói mới thành công."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponseDTO>> updatePlan(@PathVariable Integer id, @Valid @RequestBody PlanRequestDTO requestDTO) {
        PlanResponseDTO updatedPlan = planService.updatePlan(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedPlan, "Cập nhật gói thành công."));
    }
}
