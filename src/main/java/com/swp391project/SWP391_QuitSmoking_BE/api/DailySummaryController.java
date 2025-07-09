package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DailySummaryEditForbiddenException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.DailySummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
@SecurityRequirement(name = "diary_api")
public class DailySummaryController {
    private final DailySummaryService dailySummaryService;

    private UUID getAuthenticatedMemberId(UserDetails userDetails) {
        if (!(userDetails instanceof User currentUser)) {
            throw new AccessDeniedException("Người dùng chưa được xác thực hoặc không có quyền truy cập");
        }
        return currentUser.getUserId();
    }

    // Tạo bản ghi DailySummary - do chính người dùng nhập thủ công
    @Operation(summary = "Tạo nhật ký thủ công",
            description = "Cho phép người dùng chủ động tạo nhật ký hàng ngày")
    @PostMapping
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<DailySummaryResponse>> createManualDailySummary(
            @Valid @RequestBody DailySummaryCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            DailySummaryResponse response = dailySummaryService.createManualDailySummary(memberId, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Nhật ký hàng ngày được tạo thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi tạo nhật ký hàng ngày: " + e.getMessage()));
        }
    }

    //Lấy bản ghi DailySummary bằng ID của nó và ID của thành viên sở hữu
    //Đảm bảo rằng bản ghi này thuộc về người dùng được xác thực
    @Operation(summary = "Lấy nhật ký hàng ngày theo ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<DailySummaryResponse>> getDailySummaryById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            UUID memberIdFromDailySummary = dailySummaryService.getMemberIdByDailySummaryId(id);
            if (!memberId.equals(memberIdFromDailySummary)) {
                // Nếu DailySummary không thuộc về thành viên được cung cấp, trả về lỗi FORBIDDEN
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào bản ghi này"));
            }

            DailySummary dailySummary = dailySummaryService.getDailySummaryById(id);
            //Nếu hợp lệ, chuyển đổi sang DTO và trả về modelMapper.map(dailySummary, DailySummaryResponse.class)
            DailySummaryResponse response = dailySummaryService.convertToResponseDto(dailySummary);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(response, "Lấy nhật ký hằng ngày thành công"));
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
                            "Có lỗi xảy ra khi lấy nhật ký hàng ngày theo ID: " + e.getMessage()));
        }
    }

    //Lấy bản ghi DailySummary cho một ngày cụ thể của một thành viên
    @Operation(summary = "Lấy nhật ký theo ngày",
            description = "Cho phép người dùng lấy nhật ký hàng ngày của mình theo ngày cụ thể")
    @GetMapping("/by-date")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<DailySummaryResponse>> getDailySummaryByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            DailySummaryResponse response = dailySummaryService.getDailySummaryByMemberIdAndDate(memberId, date);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(response, "Lấy nhật ký hằng ngày thành công"));
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
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi lấy nhật ký hàng ngày theo ngày: " + e.getMessage()));
        }
    }

    @Operation(summary = "Cập nhật nhật ký",
            description = "Cho phép người dùng cập nhật nhật ký hàng ngày của chính mình thông qua ID nhật ký")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<DailySummaryResponse>> updateDailySummary(
            @PathVariable Integer id,
            @Valid @RequestBody DailySummaryUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            UUID memberIdFromDailySummary = dailySummaryService.getMemberIdByDailySummaryId(id);

            if (!memberId.equals(memberIdFromDailySummary)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào bản ghi này"));
            }

            DailySummaryResponse response = dailySummaryService.updateDailySummary(id, request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(response, "Cập nhật nhật ký hằng ngày thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            //lỗi này có thể trả về do dailysummary đã bị xóa do cập nhật giá trị về 0
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT) // 204 No Content
                    .body(ApiResponse.success(null, e.getMessage()));
        } catch (IllegalArgumentException | DailySummaryEditForbiddenException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi cập nhật nhật ký hàng ngày: " + e.getMessage()));
        }
    }

    @Operation(summary = "Xóa nhật ký hàng ngày",
            description = "Cho phép người dùng xóa nhật ký hàng ngày của chính mình thông qua ID nhật ký")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<Void>> deleteDailySummary(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            UUID memberIdFromDailySummary = dailySummaryService.getMemberIdByDailySummaryId(id);
            // Kiểm tra quyền sở hữu: Đảm bảo DailySummary thuộc về thành viên này
            if (!memberId.equals(memberIdFromDailySummary)) {
                // Nếu DailySummary không thuộc về thành viên được cung cấp, trả về lỗi FORBIDDEN
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào bản ghi này"));
            }

            dailySummaryService.deleteDailySummary(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT) // HTTP 204 No Content
                    .body(ApiResponse.success(null, "Nhật ký hằng ngày đã được xóa thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
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
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi xóa nhật ký hàng ngày: " + e.getMessage()));
        }
    }
}
