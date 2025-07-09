package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.program.ProgramRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.program.ProgramResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.program.ProgramUpdateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.ProgramService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class ProgramController {

    private final ProgramService programService;

    // --- ENDPOINT CHO PREMIUM_MEMBER (XEM PROGRAMS) ---

    // Lấy tất cả programs hoặc tìm kiếm theo keyword (title/name), CÓ PHÂN TRANG
    @GetMapping
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<ProgramResponseDTO>>> getAllPrograms(
            @RequestParam(required = false) String keyword,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<ProgramResponseDTO> programs = programService.getAllPrograms(keyword, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(programs, "Lấy danh sách programs thành công."));
    }

    // Lấy thông tin một program cụ thể bằng ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponseDTO>> getProgramById(@PathVariable Integer id) {
        ProgramResponseDTO program = programService.getProgramById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(program, "Lấy thông tin program thành công."));
    }

    // Lấy programs theo type
    @GetMapping("/type/{programType}")
    @PreAuthorize("hasAnyRole('PREMIUM_MEMBER', 'CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<ProgramResponseDTO>>> getProgramsByType(
            @PathVariable String programType,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<ProgramResponseDTO> programs = programService.getProgramsByType(programType, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(programs, "Lấy danh sách programs theo type thành công."));
    }

    // --- ENDPOINT CHO CONTENT_ADMIN (TẠO, SỬA, XÓA PROGRAMS) ---

    // Tạo Program: Hỗ trợ cả có và không có image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponseDTO>> createProgram(
            @Valid @ModelAttribute ProgramRequestDTO programRequest,
            @AuthenticationPrincipal User currentUser) {
        ProgramResponseDTO createdProgram = programService.createProgram(programRequest, currentUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdProgram, "Tạo program mới thành công."));
    }

    // Sửa Program: Hỗ trợ cả có và không có image
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProgramResponseDTO>> updateProgram(
            @PathVariable Integer id,
            @Valid @ModelAttribute ProgramUpdateRequestDTO programRequest,
            @AuthenticationPrincipal User currentUser) {
        ProgramResponseDTO updatedProgram = programService.updateProgram(id, programRequest, currentUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(updatedProgram, "Cập nhật program thành công."));
    }

    // Xóa Program: Yêu cầu quyền CONTENT_ADMIN
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProgram(
            @PathVariable Integer id,
            @AuthenticationPrincipal User currentUser) {
        programService.deleteProgram(id, currentUser);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.success(null, "Xóa program thành công."));
    }

    // --- ENDPOINT CHO ADMIN (QUẢN LÝ PROGRAMS) ---

    // Lấy programs của một creator cụ thể
    @GetMapping("/creator/{creatorId}")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<ProgramResponseDTO>>> getProgramsByCreator(
            @PathVariable UUID creatorId,
            @PageableDefault(
                    page = 0,
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        Page<ProgramResponseDTO> programs = programService.getProgramsByCreator(creatorId, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(programs, "Lấy danh sách programs của creator thành công."));
    }

    // Đếm số lượng programs của một creator
    @GetMapping("/creator/{creatorId}/count")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> countProgramsByCreator(@PathVariable UUID creatorId) {
        long count = programService.countProgramsByCreator(creatorId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(count, "Đếm số lượng programs thành công."));
    }
}
