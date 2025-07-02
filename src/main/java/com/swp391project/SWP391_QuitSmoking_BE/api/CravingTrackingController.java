package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.CravingTrackingDeletedException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DailySummaryEditForbiddenException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.CravingTrackingService;
import com.swp391project.SWP391_QuitSmoking_BE.service.DailySummaryService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
@SecurityRequirement(name = "checkin_api")
public class CravingTrackingController {
    private final CravingTrackingService cravingTrackingService;
    private final DailySummaryService dailySummaryService;

    private UUID getAuthenticatedMemberId(UserDetails userDetails) {
        if (!(userDetails instanceof User currentUser)) {
            throw new AccessDeniedException("Người dùng chưa được xác thực hoặc không có quyền truy cập");
        }
        return currentUser.getUserId();
    }

    //để người dùng gửi dữ liệu theo dõi trong giờ hiện tại
    //dữ liệu sẽ được cộng dồn vào bản ghi CravingTracking của giờ đó
    @Operation(summary = "Ghi nhận dữ liệu theo dõi cơn thèm thuốc",
            description = "Cho phép người dùng ghi nhận dữ liệu theo dõi cơn thèm thuốc trong giờ hiện tại. " +
                    "Dữ liệu sẽ được cộng dồn vào bản ghi CravingTracking của giờ đó")
    @PostMapping("/checkin")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<CravingTrackingResponse>> checkInCraving(
            @Valid @RequestBody CravingTrackingCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            CravingTrackingResponse response = cravingTrackingService.createOrUpdateTracking(memberId, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED) // HTTP 201 Created
                    .body(ApiResponse.success(response, "Ghi nhận dữ liệu theo dõi mới thành công"));
        } catch (IllegalArgumentException | ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi ghi nhận dữ liệu theo dõi: " + e.getMessage()));
        }
    }

    //lấy thông tin một bản ghi (đã tổng hợp) theo ID
    @Operation(summary = "Lấy thông tin bản ghi theo ID",
            description = "Cho phép người dùng lấy thông tin một bản ghi theo ID. " +
                    "Chỉ trả về bản ghi nếu nó thuộc về thành viên đã đăng nhập")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<CravingTrackingResponse>> getCravingTrackingById(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            UUID memberIdFromTracking = cravingTrackingService.getMemberIdByCravingTrackingId(id);
            if (!memberId.equals(memberIdFromTracking)) {
                // Nếu CravingTracking không thuộc về thành viên được cung cấp, trả về lỗi FORBIDDEN
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào bản ghi này"));
            }
            CravingTrackingResponse response = cravingTrackingService.
                    convertToResponseDto(cravingTrackingService.getCravingTrackingById(id));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(response, "Lấy thông tin bản ghi thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        }
    }

    //Lấy tất cả bản ghi (đã tổng hợp) cho một dailySummary cụ thể
    @Operation(summary = "Lấy các bản ghi theo DailySummary ID",
            description = "Cho phép người dùng lấy danh sách bản ghi theo DailySummary ID. " +
                    "Chỉ trả về danh sách nếu DailySummary thuộc về thành viên đã đăng nhập")
    @GetMapping("diary/{dailySummaryId}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<CravingTrackingResponse>>> getCravingTrackingsByDailySummaryId(
            @PathVariable Integer dailySummaryId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            UUID memberIdFromDailySummary = dailySummaryService.getMemberIdByDailySummaryId(dailySummaryId);
            // Kiểm tra quyền sở hữu: đảm bảo bản ghi này thuộc về thành viên đó
            if (!memberId.equals(memberIdFromDailySummary)) {
                // Nếu DailySummary không thuộc về thành viên được cung cấp, trả về lỗi FORBIDDEN
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào bản ghi này"));
            }
            List<CravingTrackingResponse> response = cravingTrackingService.
                    getCravingTrackingResponsesByDailySummaryId(dailySummaryId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(response, "Lấy danh sách bản ghi thành công"));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (IllegalArgumentException e) { // For permission errors from service
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Có lỗi xảy ra khi lấy danh sách bản ghi: " + e.getMessage()));
        }
    }

    //Cập nhật thông tin một bản ghi
    //cho phép người dùng tùy ý chỉnh sửa giá trị tổng của một giờ
    @Operation(summary = "Cập nhật bản ghi theo ID",
            description = "Cho phép người dùng cập nhật thông tin một bản ghi theo ID. " +
                    "Chỉ cho phép cập nhật nếu bản ghi thuộc về thành viên đã đăng nhập")
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<CravingTrackingResponse>> updateCravingTracking(
            @PathVariable Integer id,
            @Valid @RequestBody CravingTrackingUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            UUID memberIdFromTracking = cravingTrackingService.getMemberIdByCravingTrackingId(id);
            // Kiểm tra quyền sở hữu: đảm bảo bản ghi này thuộc về thành viên đó
            if (!memberId.equals(memberIdFromTracking)) {
                // Nếu DailySummary không thuộc về thành viên được cung cấp, trả về lỗi FORBIDDEN
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào bản ghi này"));
            }
            CravingTrackingResponse updated = cravingTrackingService.updateCravingTracking(id, request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(updated, "Bản ghi đã được cập nhật thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        }  catch (IllegalArgumentException | DailySummaryEditForbiddenException | CravingTrackingDeletedException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi cập nhật bản ghi: " + e.getMessage()));
        }
    }

    //Xóa bản ghi theo ID
    @Operation(summary = "Xóa bản ghi theo ID",
            description = "Cho phép người dùng xóa một bản ghi theo ID. " +
                    "Chỉ cho phép xóa nếu bản ghi thuộc về thành viên đã đăng nhập")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<Void>> deleteCravingTracking(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            UUID memberIdFromTracking = cravingTrackingService.getMemberIdByCravingTrackingId(id);
            // Kiểm tra quyền sở hữu: đảm bảo bản ghi này thuộc về thành viên đó
            if (!memberId.equals(memberIdFromTracking)) {
                // Nếu DailySummary không thuộc về thành viên được cung cấp, trả về lỗi FORBIDDEN
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập vào bản ghi này"));
            }
            cravingTrackingService.deleteCravingTracking(id);
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT) // HTTP 204 No Content là chuẩn khi xóa thành công
                    .body(ApiResponse.success(null, "Bản ghi đã được xóa thành công"));
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
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi xóa bản ghi: " + e.getMessage()));
        }
    }
}
