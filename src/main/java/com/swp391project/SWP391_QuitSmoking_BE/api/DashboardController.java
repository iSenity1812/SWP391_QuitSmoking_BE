package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request.DashboardFilterDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request.RevenueTimeGroupDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request.ExportRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.DashboardService;
import com.swp391project.SWP391_QuitSmoking_BE.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DashboardController - API Controller để quản lý dashboard doanh thu và thống kê (REVENUE DASHBOARD)
 *
 * Chức năng chính:
 * - Cung cấp các API để lấy dữ liệu dashboard doanh thu
 * - Hỗ trợ phân trang và lọc dữ liệu
 * - Chỉ cho phép SUPER_ADMIN truy cập
 *
 * Cấu trúc hoạt động:
 * 1. Nhận request từ frontend qua các endpoint
 * 2. Validate dữ liệu input bằng @Valid
 * 3. Gọi DashboardService để xử lý logic business
 * 4. Trả về response thông qua ApiResponse wrapper
 * 5. Xử lý exception và trả về lỗi phù hợp
 */

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')") // Chỉ cho phép SUPER_ADMIN truy cập
@SecurityRequirement(name = "user_api")
@Tag(name = "Dashboard API", description = "API để quản lý dashboard doanh thu và thống kê")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;
    private final ExportService exportService;

    /**
     * Exception Handler cho validation errors
     * Xử lý lỗi validate dữ liệu input từ @Valid annotation
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.warn("Validation error in dashboard request: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ApiResponse.validationError(HttpStatus.BAD_REQUEST, "Dữ liệu nhập vào không hợp lệ", errors);
    }


    /**
     *  Lấy tổng quan dashboard
     *
     * Hoạt động:
     * - Nhận filter từ request parameters
     * - Tính toán các metrics tổng quan (doanh thu, số giao dịch, tỷ lệ thành công, etc.)
     * - Trả về dữ liệu overview cho dashboard
     *
     * Response bao gồm:
     * - Tổng doanh thu (tất cả thời gian + theo khoảng thời gian)
     * - Số lượng giao dịch theo từng trạng thái
     * - Tỷ lệ thành công, AOV, số subscription active
     * - Tỷ lệ tăng trưởng so với kỳ trước
     */

    @GetMapping("/overview")
    @Operation(summary = "Lấy tổng quan dashboard",
            description = "Trả về các số liệu tổng quan về doanh thu, giao dịch, và metrics khác cho dashboard")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy dữ liệu tổng quan thành công",
                    content = @Content(schema = @Schema(implementation = DashboardOverviewDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống khi lấy dữ liệu"
            )
    })
    public ResponseEntity<ApiResponse<DashboardOverviewDTO>> getDashboardOverview(
            @Parameter(description = "Loại khoảng thời gian (TODAY, YESTERDAY, LAST_7_DAYS, LAST_30_DAYS, THIS_MONTH, LAST_MONTH, THIS_YEAR, CUSTOM)")
            @RequestParam(required = false) String period,

            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd) - Chỉ dùng khi period=CUSTOM")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd) - Chỉ dùng khi period=CUSTOM")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Danh sách trạng thái giao dịch cần lọc")
            @RequestParam(required = false) List<TransactionStatus> statuses,

            @Parameter(description = "Danh sách ID gói cần lọc")
            @RequestParam(required = false) List<Integer> planIds,

            @Parameter(description = "Danh sách ID người dùng cần lọc")
            @RequestParam(required = false) List<UUID> userIds,

            @Parameter(description = "Phương thức thanh toán cần lọc")
            @RequestParam(required = false) String paymentMethod) {

        logger.info("Getting dashboard overview with filter: period={}, startDate={}, endDate={}",
                period, startDate, endDate);

        try {
            // Tạo filter object từ parameters
            DashboardFilterDTO filter = DashboardFilterDTO.builder()
                    .period(period)
                    .startDate(startDate)
                    .endDate(endDate)
                    .statuses(statuses)
                    .planIds(planIds)
                    .userIds(userIds)
                    .paymentMethod(paymentMethod)
                    .build();

            DashboardOverviewDTO overview = dashboardService.getDashboardOverview(filter);

            logger.info("Successfully retrieved dashboard overview. Total revenue: {}, Total transactions: {}",
                    overview.getTotalRevenue(), overview.getTotalTransactions());

            return ResponseEntity.ok(ApiResponse.success(overview, "Lấy dữ liệu tổng quan dashboard thành công"));

        } catch (Exception e) {
            logger.error("Error retrieving dashboard overview: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi lấy dữ liệu tổng quan dashboard",
                            e.getMessage(),
                            "DASHBOARD_OVERVIEW_ERROR"));
        }
    }


    /**
     * Lấy doanh thu theo khoảng thời gian
     *
     *  Nhận thông tin groupBy (DAY/WEEK/MONTH/YEAR) và khoảng thời gian
     *  Tính toán doanh thu được nhóm theo từng period
     *  Trả về dữ liệu để vẽ biểu đồ timeline
     *
     *  Response bao gồm:
     *  - Doanh thu theo từng khoảng thời gian
     *  - Số lượng giao dịch và tỷ lệ thành công
     *  - Thông tin period start/end để FE dễ xử lý
     */
    @GetMapping("/revenue/by-period")
    @Operation(summary = "Lấy doanh thu theo khoảng thời gian",
            description = "Trả về doanh thu được nhóm theo ngày/tuần/tháng/năm để vẽ biểu đồ timeline")
    public ResponseEntity<ApiResponse<List<RevenueByPeriodDTO>>> getRevenueByPeriod(
            @Parameter(description = "Thông tin nhóm theo thời gian (DAY/WEEK/MONTH/YEAR)", required = true)
            @RequestParam String groupBy,

            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("Getting revenue by period: groupBy={}, startDate={}, endDate={}",
                groupBy, startDate, endDate);
        try {
            RevenueTimeGroupDTO request = new RevenueTimeGroupDTO();
            request.setGroupBy(groupBy);
            request.setStartDate(startDate);
            request.setEndDate(endDate);

            List<RevenueByPeriodDTO> revenue = dashboardService.getRevenueByTimePeriod(request);
            logger.info("Successfully retrieved revenue by period. Total periods: {}", revenue.size());
            return ResponseEntity.ok(ApiResponse.success(revenue, "Lấy doanh thu theo khoảng thời gian thành công"));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid groupBy parameter: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Tham số groupBy không hợp lệ", e.getMessage(), "INVALID_GROUP_BY"));
        } catch (Exception e) {
            logger.error("Error retrieving revenue by period: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống khi lấy doanh thu theo khoảng thời gian", e.getMessage(), "REVENUE_BY_PERIOD_ERROR"));
        }
    }

    /**
     * Thống kê doanh thu theo gói
     *
     * Hoạt động:
     * - Lấy thống kê doanh thu và hiệu suất của từng gói subscription
     * - Tính toán tỷ lệ phần trăm doanh thu của mỗi gói
     * - Bao gồm thông tin subscription active hiện tại
     *
     * Response bao gồm:
     * - Doanh thu và số giao dịch theo từng gói
     * - Tỷ lệ thành công và phần trăm đóng góp doanh thu
     * - Thông tin gói (giá, thời hạn, tên hiển thị)
     */
    @GetMapping("/plans/revenue-stats")
    @Operation(summary = "Thống kê doanh thu theo gói",
            description = "Trả về thống kê doanh thu và hiệu suất của từng gói subscription")
    public ResponseEntity<ApiResponse<List<PlanRevenueStatsDTO>>> getPlanRevenueStats(
            @Parameter(description = "Loại thời gian")
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String period,

            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd) - Chỉ dùng khi period=CUSTOM")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd) - Chỉ dùng khi period=CUSTOM")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {


        logger.info("Getting plan revenue stats with filter: period={}", period);
        try {
            DashboardFilterDTO filter = DashboardFilterDTO.builder()
                    .period(period)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            List<PlanRevenueStatsDTO> stats = dashboardService.getPlanRevenueStats(filter);
            logger.info("Successfully retrieved plan revenue stats. Total plans: {}", stats.size());
            return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê doanh thu theo gói thành công"));
        } catch (Exception e) {
            logger.error("Error retrieving plan revenue stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống khi lấy thống kê doanh thu theo gói", e.getMessage(), "PLAN_REVENUE_STATS_ERROR"));
        }
    }

    /**
     *  Lấy danh sách giao dịch chi tiết (chỉ SUCCESS transactions)
     *
     * Cách sử dụng:
     * GET /api/dashboard/transactions?period=LAST_7_DAYS&page=0&size=10
     * GET /api/dashboard/transactions?period=CUSTOM&startDate=2024-01-01&endDate=2024-01-31
     */
    @GetMapping("/transactions")
    @Operation(summary = "Lấy danh sách giao dịch thành công",
            description = "Trả về danh sách giao dịch SUCCESS với thông tin user và gói, có phân trang và lọc")
    public ResponseEntity<ApiResponse<Page<TransactionDetailDTO>>> getTransactionDetails(
            @Parameter(description = "Loại khoảng thời gian")
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String period,

            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Danh sách ID gói")
            @RequestParam(required = false) List<Integer> planIds,

            @Parameter(description = "Danh sách ID người dùng")
            @RequestParam(required = false) List<UUID> userIds,

            @Parameter(description = "Phương thức thanh toán")
            @RequestParam(required = false) String paymentMethod,

            @Parameter(description = "Số trang (bắt đầu từ 0)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Kích thước trang")
            @RequestParam(required = false, defaultValue = "20") int size,

            @Parameter(description = "Trường sắp xếp")
            @RequestParam(required = false, defaultValue = "transactionDate") String sortBy,

            @Parameter(description = "Hướng sắp xếp (ASC, DESC)")
            @RequestParam(required = false, defaultValue = "DESC") String sortDirection,

            @Parameter(description = "Bao gồm tất cả trạng thái (mặc định chỉ SUCCESS)")
            @RequestParam(required = false, defaultValue = "false") boolean includeAllStatuses) {

        logger.info("Getting transaction details: page={}, size={}, includeAllStatuses={}",
                page, size, includeAllStatuses);

        try {
            // Mặc định chỉ lấy SUCCESS, trừ khi includeAllStatuses=true
            List<TransactionStatus> statuses = includeAllStatuses ?
                    null : List.of(TransactionStatus.SUCCESS);

            DashboardFilterDTO filter = DashboardFilterDTO.builder()
                    .period(period)
                    .startDate(startDate)
                    .endDate(endDate)
                    .statuses(statuses)
                    .planIds(planIds)
                    .userIds(userIds)
                    .paymentMethod(paymentMethod)
                    .page(page)
                    .size(size)
                    .sortBy(sortBy)
                    .sortDirection(sortDirection)
                    .build();

            Page<TransactionDetailDTO> transactions = dashboardService.getTransactionDetails(filter);

            logger.info("Transaction details retrieved successfully. {} transactions found, page {}/{}",
                    transactions.getTotalElements(), transactions.getNumber() + 1, transactions.getTotalPages());

            return ResponseEntity.ok(ApiResponse.success(transactions, "Lấy danh sách giao dịch thành công"));

        } catch (Exception e) {
            logger.error("Error getting transaction details: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi khi lấy danh sách giao dịch chi tiết",
                            e.getMessage(),
                            "TRANSACTION_DETAILS_ERROR"));
        }
    }

    /**
     *  Thống kê doanh thu theo người dùng
     *
     *
     * - Lấy thống kê doanh thu của từng user
     * - Bao gồm thông tin gói subscription hiện tại
     * - Sắp xếp theo tổng doanh thu giảm dần
     * - Hỗ trợ phân trang
     *
     * Response
     * - Thông tin user (username, email, role, avatar)
     * - Thông tin gói hiện tại và trạng thái subscription
     * - Thống kê doanh thu và số giao dịch
     * - Thời gian giao dịch đầu tiên và cuối cùng
     */
    @GetMapping("/users/revenue-stats")
    @Operation(summary = "Thống kê doanh thu theo người dùng",
            description = "Trả về thống kê doanh thu của từng user với thông tin gói hiện tại, có phân trang")
    public ResponseEntity<ApiResponse<Page<UserRevenueStatsDTO>>> getUserRevenueStats(
            @Parameter(description = "Loại khoảng thời gian")
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String period,

            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @Parameter(description = "Số trang (bắt đầu từ 0)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Kích thước trang")
            @RequestParam(required = false, defaultValue = "20") int size) {

        logger.info("Getting user revenue stats: page={}, size={}", page, size);

        try {
            DashboardFilterDTO filter = DashboardFilterDTO.builder()
                    .period(period)
                    .startDate(startDate)
                    .endDate(endDate)
                    .page(page)
                    .size(size)
                    .build();

            Page<UserRevenueStatsDTO> userStats = dashboardService.getUserRevenueStats(filter);

            logger.info("User revenue stats retrieved successfully. {} users found, page {}/{}",
                    userStats.getTotalElements(), userStats.getNumber() + 1, userStats.getTotalPages());

            return ResponseEntity.ok(ApiResponse.success(userStats, "Lấy thống kê doanh thu theo người dùng thành công"));

        } catch (Exception e) {
            logger.error("Error getting user revenue stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi khi lấy thống kê doanh thu theo người dùng",
                            e.getMessage(),
                            "USER_REVENUE_STATS_ERROR"));
        }
    }


    /**
     * Thống kê theo phương thức thanh toán
     *
     * GET /api/dashboard/payment-methods/stats?period=LAST_30_DAYS
     */
    @GetMapping("/payment-methods/stats")
    @Operation(summary = "Thống kê theo phương thức thanh toán",
            description = "Trả về thống kê hiệu suất của các phương thức thanh toán")
    public ResponseEntity<ApiResponse<List<PaymentMethodStatsDTO>>> getPaymentMethodStats(
            @Parameter(description = "Loại khoảng thời gian")
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String period,

            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Getting payment method stats with filter: period={}", period);

        try {
            DashboardFilterDTO filter = DashboardFilterDTO.builder()
                    .period(period)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            List<PaymentMethodStatsDTO> stats = dashboardService.getPaymentMethodStats(filter);

            logger.info("Payment method stats retrieved successfully. {} methods found", stats.size());

            return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê phương thức thanh toán thành công"));

        } catch (Exception e) {
            logger.error("Error getting payment method stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi khi lấy thống kê phương thức thanh toán",
                            e.getMessage(),
                            "PAYMENT_METHOD_STATS_ERROR"));
        }
    }

    /**
     * Tính toán tỷ lệ tăng trưởng
     * GET /api/dashboard/growth-rate?period=LAST_30_DAYS
     */
    @GetMapping("/growth-rate")
    @Operation(summary = "Tính toán tỷ lệ tăng trưởng doanh thu",
            description = "So sánh doanh thu kỳ hiện tại với kỳ trước và tính tỷ lệ tăng trưởng")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGrowthRate(
            @Parameter(description = "Loại khoảng thời gian")
            @RequestParam(required = false, defaultValue = "LAST_30_DAYS") String period,

            @Parameter(description = "Ngày bắt đầu (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Ngày kết thúc (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        logger.info("Calculating growth rate with filter: period={}", period);

        try {
            DashboardFilterDTO filter = DashboardFilterDTO.builder()
                    .period(period)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            // Tính toán tỷ lệ tăng trưởng từ service
            BigDecimal growthRate = dashboardService.calculateGrowthRate(filter);

            // Tạo response map
            Map<String, Object> response = new HashMap<>();
            response.put("growthRate", growthRate);
            response.put("period", period);
            response.put("isPositive", growthRate.compareTo(BigDecimal.ZERO) > 0);
            response.put("startDate", startDate);
            response.put("endDate", endDate);

            logger.info("Growth rate calculated successfully: {}%", growthRate);

            return ResponseEntity.ok(ApiResponse.success(response, "Tính toán tỷ lệ tăng trưởng thành công"));

        } catch (Exception e) {
            logger.error("Error calculating growth rate: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi khi tính toán tỷ lệ tăng trưởng",
                            e.getMessage(),
                            "GROWTH_RATE_CALCULATION_ERROR"));
        }
    }

    // ================================================================
    // EXPORT ENDPOINTS
    // ================================================================

    /**
     * Export transactions to CSV/Excel format
     * 
     * POST /api/dashboard/export/transactions
     * 
     * Request body:
     * {
     *   "format": "CSV" | "EXCEL",
     *   "period": "LAST_30_DAYS",
     *   "statuses": ["SUCCESS", "FAILED"],
     *   "paymentMethod": "VNPAY",
     *   "includeDetails": true,
     *   "maxRecords": 5000
     * }
     */
    @PostMapping("/export/transactions")
    @Operation(summary = "Export giao dịch ra file CSV/Excel",
            description = "Export dữ liệu giao dịch với các bộ lọc tùy chỉnh")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Export thành công - file được download trực tiếp",
                    content = @Content(mediaType = "application/octet-stream")
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Tham số export không hợp lệ"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống khi export"
            )
    })
    public ResponseEntity<Resource> exportTransactions(@Valid @RequestBody ExportRequestDTO request) {
        logger.info("Starting transaction export: format={}, period={}, maxRecords={}", 
                request.getFormat(), request.getPeriod(), request.getMaxRecords());

        try {
            // Validate export format
            if (request.getFormat() == null || 
                (!request.getFormat().equalsIgnoreCase("CSV") && !request.getFormat().equalsIgnoreCase("EXCEL"))) {
                logger.warn("Invalid export format: {}", request.getFormat());
                return ResponseEntity.badRequest().build();
            }

            // Set default values
            if (request.getPeriod() == null) {
                request.setPeriod("LAST_30_DAYS");
            }
            if (request.getMaxRecords() == null) {
                request.setMaxRecords(5000); // Default limit
            }
            if (request.getSortBy() == null) {
                request.setSortBy("transactionDate");
            }
            if (request.getSortDirection() == null) {
                request.setSortDirection("DESC");
            }

            // Validate max records limit
            if (request.getMaxRecords() > 10000) {
                logger.warn("Max records limit exceeded: {}", request.getMaxRecords());
                request.setMaxRecords(10000);
            }

            // Perform export
            ResponseEntity<Resource> response = exportService.exportTransactions(request);
            
            logger.info("Transaction export completed successfully: format={}", request.getFormat());
            return response;

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid export parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error during transaction export: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get export metadata/status
     * 
     * GET /api/dashboard/export/info?format=CSV&recordCount=100
     */
    @GetMapping("/export/info")
    @Operation(summary = "Lấy thông tin export metadata",
            description = "Trả về thông tin về file export sẽ được tạo")
    public ResponseEntity<ApiResponse<ExportResponseDTO>> getExportInfo(
            @Parameter(description = "Định dạng export (CSV, EXCEL)")
            @RequestParam String format,
            
            @Parameter(description = "Số lượng records sẽ export")
            @RequestParam(required = false, defaultValue = "0") int recordCount) {

        logger.info("Getting export info: format={}, recordCount={}", format, recordCount);

        try {
            // Validate format
            if (!format.equalsIgnoreCase("CSV") && !format.equalsIgnoreCase("EXCEL")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error(HttpStatus.BAD_REQUEST,
                                "Định dạng export không hợp lệ",
                                "Chỉ hỗ trợ CSV và EXCEL",
                                "INVALID_EXPORT_FORMAT"));
            }

            ExportResponseDTO exportInfo = exportService.generateExportResponse(format, recordCount);
            
            return ResponseEntity.ok(ApiResponse.success(exportInfo, "Lấy thông tin export thành công"));

        } catch (Exception e) {
            logger.error("Error getting export info: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi hệ thống khi lấy thông tin export",
                            e.getMessage(),
                            "EXPORT_INFO_ERROR"));
        }
    }
}
