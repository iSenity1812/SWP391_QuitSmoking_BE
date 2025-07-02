package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.HourlyChartDataResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailyChartDataResponse;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.DataVisualizationService;
import com.swp391project.SWP391_QuitSmoking_BE.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/charts")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class DataVisualizationController {
    private final DataVisualizationService dataVisualizationService;
    private final UserService userService;

    private UUID getAuthenticatedMemberId(UserDetails userDetails) {
        if (userDetails == null) {
            throw new AccessDeniedException("Người dùng chưa được xác thực.");
        }
        return userService.getUserIdFromUserDetails(userDetails);
    }

    //Lấy dữ liệu tổng hợp theo giờ cho một ngày cụ thể của người dùng
    //Như Hourly Chart
    @GetMapping("/hourly")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<HourlyChartDataResponse>>> getHourlyDataForDay(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);
            List<HourlyChartDataResponse> data = dataVisualizationService.getHourlyDataForDay(memberId, date);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(data, "Lấy thông tin cho Hourly Chart thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized nếu không xác thực được
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) { // Xử lý các lỗi không mong muốn khác
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi lấy dữ liệu Hourly Chart: " + e.getMessage()));
        }
    }

    //Lấy dữ liệu tổng hợp theo ngày cho một tuần cụ thể của người dùng
    //Như Weekly Chart
    @GetMapping("/week")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<DailyChartDataResponse>>> getDailyDataForWeek(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInWeek) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);

            LocalDate startOfWeek = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = dateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            List<DailyChartDataResponse> data = dataVisualizationService.getDailyDataForPeriod(memberId, startOfWeek, endOfWeek);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(data, "Lấy thông tin cho Week Chart thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized nếu không xác thực được
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
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi lấy dữ liệu Week Chart: " + e.getMessage()));
        }
    }

    //Lấy dữ liệu tổng hợp theo ngày cho một tháng cụ thể của người dùng
    //Như Month Chart
    @GetMapping("/month")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<List<DailyChartDataResponse>>> getDailyDataForMonth(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam int year,
            @RequestParam int month) {

        try {
            UUID memberId = getAuthenticatedMemberId(userDetails);

            // Xác định ngày bắt đầu và kết thúc của tháng
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth());

            List<DailyChartDataResponse> data = dataVisualizationService.getDailyDataForPeriod(memberId, startOfMonth, endOfMonth);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(data, "Lấy thông tin cho Month Chart thành công"));
        } catch (AccessDeniedException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED) // 401 Unauthorized nếu không xác thực được
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
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Có lỗi xảy ra khi lấy dữ liệu Month Chart: " + e.getMessage()));
        }
    }
}
