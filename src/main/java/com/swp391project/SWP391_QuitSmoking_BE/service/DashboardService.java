package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request.DashboardFilterDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request.RevenueTimeGroupDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Plan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Subscription;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.PaidPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.repository.SubscriptionRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TransactionRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public DashboardOverviewDTO getDashboardOverview(DashboardFilterDTO filter) {
        LocalDateTime[] dateRange = calculateDateRange(filter.getPeriod(), filter.getStartDate(), filter.getEndDate());
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];

        // Calculate metrics
        BigDecimal totalRevenue = transactionRepository.getTotalRevenueByStatus(TransactionStatus.SUCCESS);
        BigDecimal currentRevenue = transactionRepository.getRevenueByStatusAndDateRange(
                TransactionStatus.SUCCESS, startDate, endDate);

        Long totalTransactions = transactionRepository.count();
        Long successTransactions = transactionRepository.countTransactionsByStatus(TransactionStatus.SUCCESS);
        Long pendingTransactions = transactionRepository.countTransactionsByStatus(TransactionStatus.PENDING);
        Long failedTransactions = transactionRepository.countTransactionsByStatus(TransactionStatus.FAILED);
        Long canceledTransactions = transactionRepository.countTransactionsByStatus(TransactionStatus.CANCELED);

        BigDecimal avgOrderValue = transactionRepository.getAverageOrderValue();
        Double successRate = calculateSuccessRate(successTransactions, totalTransactions);

        Long activeSubscriptions = subscriptionRepository.countActiveSubscriptions();
        Long totalUsers = userRepository.count();

        // Calculate growth rate
        LocalDateTime[] previousPeriod = calculatePreviousPeriod(startDate, endDate);
        BigDecimal previousRevenue = transactionRepository.getRevenueByStatusAndDateRange(
                TransactionStatus.SUCCESS, previousPeriod[0], previousPeriod[1]);
        BigDecimal revenueGrowthRate = calculateGrowthRate(currentRevenue, previousRevenue);

        return DashboardOverviewDTO.builder()
                .totalRevenue(totalRevenue)
                .totalRevenueSuccess(currentRevenue)
                .totalTransactions(totalTransactions)
                .successTransactions(successTransactions)
                .pendingTransactions(pendingTransactions)
                .failedTransactions(failedTransactions)
                .canceledTransactions(canceledTransactions)
                .averageOrderValue(avgOrderValue)
                .successRate(successRate)
                .activeSubscriptions(activeSubscriptions)
                .totalUsers(totalUsers)
                .revenueGrowthRate(revenueGrowthRate)
                .build();
    }

    public BigDecimal calculateGrowthRate(DashboardFilterDTO filter) {
        LocalDateTime[] currentPeriod = calculateDateRange(filter.getPeriod(), filter.getStartDate(), filter.getEndDate());
        LocalDateTime[] previousPeriod = calculatePreviousPeriodByType(filter.getPeriod(), currentPeriod[0], currentPeriod[1]);

        BigDecimal currentRevenue = transactionRepository.getRevenueByStatusAndDateRange(
                TransactionStatus.SUCCESS, currentPeriod[0], currentPeriod[1]);
        BigDecimal previousRevenue = transactionRepository.getRevenueByStatusAndDateRange(
                TransactionStatus.SUCCESS, previousPeriod[0], previousPeriod[1]);

        return calculateGrowthRate(currentRevenue, previousRevenue);
    }


    public List<RevenueByPeriodDTO> getRevenueByTimePeriod(RevenueTimeGroupDTO request) {
        switch (request.getGroupBy().toUpperCase()) {
            case "DAY":
                return convertRevenueData(transactionRepository.getDailyRevenueRaw(
                        request.getStartDate().atStartOfDay(),
                        request.getEndDate().atTime(23, 59, 59)
                ));
            case "WEEK":
                return convertRevenueData(transactionRepository.getWeeklyRevenueRaw(
                        request.getStartDate().atStartOfDay(),
                        request.getEndDate().atTime(23, 59, 59)
                ));
            case "MONTH":
                return convertRevenueData(transactionRepository.getMonthlyRevenueRaw(
                        request.getStartDate().atStartOfDay(),
                        request.getEndDate().atTime(23, 59, 59)
                ));
            case "YEAR":
                return convertRevenueData(transactionRepository.getYearlyRevenueRaw(
                        request.getStartDate().atStartOfDay(),
                        request.getEndDate().atTime(23, 59, 59)
                ));
            default:
                throw new IllegalArgumentException("Invalid groupBy parameter: " + request.getGroupBy());
        }
    }

    public List<PlanRevenueStatsDTO> getPlanRevenueStats(DashboardFilterDTO filter) {
        LocalDateTime[] dateRange = calculateDateRange(filter.getPeriod(), filter.getStartDate(), filter.getEndDate());
        List<Object[]> rawData = transactionRepository.getPlanRevenueStatsRaw(dateRange[0], dateRange[1]);

        // Get active subscription counts per plan
        List<Object[]> subscriptionCounts = subscriptionRepository.getActiveSubscriptionCountByPlan();
        Map<Integer, Long> subscriptionCountMap = subscriptionCounts.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> ((Number) row[1]).longValue()
                ));

        List<PlanRevenueStatsDTO> planStats = rawData.stream()
                .map(row -> {
                    Integer planId = (Integer) row[0];
                    String planName = (String) row[1];
                    return PlanRevenueStatsDTO.builder()
                            .planId(planId)
                            .planName(planName)
                            .planDisplayName(getDisplayNameFromPlanName(planName))
                            .planPrice((BigDecimal) row[2])
                            .durationDays((Integer) row[3])
                            .totalRevenue((BigDecimal) row[4])
                            .totalTransactions(((Number) row[5]).longValue())
                            .successTransactions(((Number) row[6]).longValue())
                            .activeSubscriptions(subscriptionCountMap.getOrDefault(planId, 0L))
                            .successRate((Double) row[7])
                            .build();
                })
                .collect(Collectors.toList());

        // Calculate revenue percentages
        BigDecimal totalRevenue = planStats.stream()
                .map(PlanRevenueStatsDTO::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        planStats.forEach(stat -> {
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                double percentage = stat.getTotalRevenue()
                        .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                stat.setRevenuePercentage(percentage);
            }
        });

        return planStats;
    }

    public Page<TransactionDetailDTO> getTransactionDetails(DashboardFilterDTO filter) {
        LocalDateTime[] dateRange = calculateDateRange(filter.getPeriod(), filter.getStartDate(), filter.getEndDate());

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.fromString(filter.getSortDirection()), filter.getSortBy())
        );

        Page<Transaction> transactions;

        // Kiểm tra xem có cần lấy tất cả trạng thái không
        boolean isSuccessOnly = filter.getStatuses() != null &&
                filter.getStatuses().size() == 1 &&
                filter.getStatuses().contains(TransactionStatus.SUCCESS);

        if (isSuccessOnly || filter.getStatuses() == null) {
            // Chỉ lấy SUCCESS transactions (case phổ biến nhất)
            if (filter.getPaymentMethod() != null && !filter.getPaymentMethod().trim().isEmpty()) {
                transactions = transactionRepository.findSuccessTransactionsWithPaymentMethod(
                        dateRange[0], dateRange[1], filter.getPaymentMethod(), pageable);
            } else if (filter.getPlanIds() != null && !filter.getPlanIds().isEmpty()) {
                transactions = transactionRepository.findSuccessTransactionsWithPlanIds(
                        dateRange[0], dateRange[1], filter.getPlanIds(), pageable);
            } else if (filter.getUserIds() != null && !filter.getUserIds().isEmpty()) {
                transactions = transactionRepository.findSuccessTransactionsWithUserIds(
                        dateRange[0], dateRange[1], filter.getUserIds(), pageable);
            } else {
                transactions = transactionRepository.findSuccessTransactionsInDateRange(
                        dateRange[0], dateRange[1], pageable);
            }
        } else {
            // Lấy tất cả trạng thái (trường hợp đặc biệt)
            transactions = transactionRepository.findAllTransactionsInDateRange(
                    dateRange[0], dateRange[1], pageable);
        }

        // Convert to DTOs
        return transactions.map(this::convertToTransactionDetailDTO);
    }

    public Page<UserRevenueStatsDTO> getUserRevenueStats(DashboardFilterDTO filter) {
        LocalDateTime[] dateRange = calculateDateRange(filter.getPeriod(), filter.getStartDate(), filter.getEndDate());

        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(Sort.Direction.DESC, "totalRevenue")
        );

        Page<Object[]> rawData = transactionRepository.getUserRevenueStatsRaw(dateRange[0], dateRange[1], pageable);

        return rawData.map(row -> {
            UUID userId = (UUID) row[0];
            // Get current active subscription
            Optional<Subscription> activeSubscription = subscriptionRepository.findActiveSubscriptionByUserId(userId);

            return UserRevenueStatsDTO.builder()
                    .userId(userId)
                    .username((String) row[1])
                    .email((String) row[2])
                    .role((Role) row[3])
                    .profilePicture((String) row[4])
                    .currentPlanName(activeSubscription
                            .map(s -> s.getPlan().getPlanName().getDisplayName())
                            .orElse("Không có"))
                    .currentSubscriptionEnd(activeSubscription
                            .map(Subscription::getEndDate)
                            .orElse(null))
                    .hasActiveSubscription(activeSubscription.isPresent())
                    .totalRevenue((BigDecimal) row[5])
                    .totalTransactions(((Number) row[6]).longValue())
                    .successTransactions(((Number) row[7]).longValue())
                    .firstTransactionDate((LocalDateTime) row[8])
                    .lastTransactionDate((LocalDateTime) row[9])
                    .build();
        });
    }

    public List<PaymentMethodStatsDTO> getPaymentMethodStats(DashboardFilterDTO filter) {
        LocalDateTime[] dateRange = calculateDateRange(filter.getPeriod(), filter.getStartDate(), filter.getEndDate());
        List<Object[]> rawData = transactionRepository.getPaymentMethodStatsRaw(dateRange[0], dateRange[1]);

        List<PaymentMethodStatsDTO> stats = rawData.stream()
                .map(row -> PaymentMethodStatsDTO.builder()
                        .paymentMethod((String) row[0])
                        .totalTransactions(((Number) row[1]).longValue())
                        .successTransactions(((Number) row[2]).longValue())
                        .failedTransactions(((Number) row[3]).longValue())
                        .totalRevenue((BigDecimal) row[4])
                        .successRevenue((BigDecimal) row[5])
                        .successRate((Double) row[6])
                        .build())
                .collect(Collectors.toList());

        // Calculate revenue percentages
        BigDecimal totalRevenue = stats.stream()
                .map(PaymentMethodStatsDTO::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.forEach(stat -> {
            if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                double percentage = stat.getTotalRevenue()
                        .divide(totalRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                stat.setRevenuePercentage(percentage);
            }
        });

        return stats;
    }

    // Helper method to convert Transaction entity to DTO
    private TransactionDetailDTO convertToTransactionDetailDTO(Transaction transaction) {
        Subscription subscription = transaction.getSubscription();
        User user = transaction.getUser();
        Plan plan = transaction.getPlan();

        return TransactionDetailDTO.builder()
                .transactionId(transaction.getTransactionId())
                .externalTransactionId(transaction.getExternalTransactionId())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .status(transaction.getStatus())
                .paymentMethod(transaction.getPaymentMethod())

                // User info
                .userId(user.getUserId())
                .username(user.getUsername())
                .userEmail(user.getEmail())
                .userRole(user.getRole())
                .userProfilePicture(user.getProfilePicture())

                // Plan info
                .planId(plan.getPlanId())
                .planName(plan.getPlanName().name())
                .planDisplayName(plan.getPlanName().getDisplayName())
                .planPrice(plan.getPrice())

                // Subscription info
                .subscriptionId(subscription != null ? subscription.getSubscriptionId() : null)
                .subscriptionStartDate(subscription != null ? subscription.getStartDate() : null)
                .subscriptionEndDate(subscription != null ? subscription.getEndDate() : null)
                .isSubscriptionActive(subscription != null && subscription.isActive())

                // Metadata
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
    // Other helper methods...
    private List<RevenueByPeriodDTO> convertRevenueData(List<Object[]> rawData) {
        return rawData.stream()
                .map(row -> RevenueByPeriodDTO.builder()
                        .period((String) row[0])
                        .periodStart(((Timestamp) row[1]).toLocalDateTime())
                        .periodEnd(((Timestamp) row[2]).toLocalDateTime())
                        .revenue((BigDecimal) row[3])
                        .transactionCount(((Number) row[4]).longValue())
                        .successCount(((Number) row[5]).longValue())
                        .failedCount(((Number) row[6]).longValue())
                        .successRate((Double) row[7])
                        .build())
                .collect(Collectors.toList());
    }

    private Double calculateSuccessRate(Long success, Long total) {
        if (total == null || total == 0) return 0.0;
        return (success != null ? success : 0) * 100.0 / total;
    }

    private BigDecimal calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current != null && current.compareTo(BigDecimal.ZERO) > 0 ?
                    BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        if (current == null) current = BigDecimal.ZERO;

        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private String getDisplayNameFromPlanName(String planName) {
        try {
            PaidPlanType planType = PaidPlanType.valueOf(planName);
            return planType.getDisplayName(); // Assuming enum has getDisplayName method
        } catch (Exception e) {
            return planName; // Return original if conversion fails
        }
    }


    // Date range calculation methods...
    /**
     * Tính toán khoảng thời gian dựa trên period string hoặc custom date range
     * @param period Loại khoảng thời gian (TODAY, YESTERDAY, LAST_7_DAYS, etc.)
     * @param startDate Ngày bắt đầu tùy chỉnh (dùng khi period = CUSTOM)
     * @param endDate Ngày kết thúc tùy chỉnh (dùng khi period = CUSTOM)
     * @return Array gồm [startDateTime, endDateTime]
     */
    private LocalDateTime[] calculateDateRange(String period, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start, end;
        LocalDate now = LocalDate.now();

        // Nếu period là null hoặc CUSTOM, sử dụng startDate và endDate
        if (period == null || period.equalsIgnoreCase("CUSTOM")) {
            start = (startDate != null ? startDate : now.minusDays(30)).atStartOfDay();
            end = (endDate != null ? endDate : now).atTime(23, 59, 59);
            return new LocalDateTime[]{start, end};
        }

        switch (period.toUpperCase()) {
            case "TODAY":
                start = now.atStartOfDay();
                end = now.atTime(23, 59, 59);
                break;

            case "YESTERDAY":
                start = now.minusDays(1).atStartOfDay();
                end = now.minusDays(1).atTime(23, 59, 59);
                break;

            case "LAST_7_DAYS":
                start = now.minusDays(6).atStartOfDay(); // Bao gồm hôm nay = 7 ngày
                end = now.atTime(23, 59, 59);
                break;

            case "LAST_30_DAYS":
                start = now.minusDays(29).atStartOfDay(); // Bao gồm hôm nay = 30 ngày
                end = now.atTime(23, 59, 59);
                break;

            case "THIS_WEEK":
                // Tuần này từ thứ 2 đến chủ nhật
                start = now.with(DayOfWeek.MONDAY).atStartOfDay();
                end = now.atTime(23, 59, 59);
                break;

            case "LAST_WEEK":
                // Tuần trước từ thứ 2 đến chủ nhật
                LocalDate lastWeekStart = now.minusWeeks(1).with(DayOfWeek.MONDAY);
                LocalDate lastWeekEnd = now.minusWeeks(1).with(DayOfWeek.SUNDAY);
                start = lastWeekStart.atStartOfDay();
                end = lastWeekEnd.atTime(23, 59, 59);
                break;

            case "THIS_MONTH":
                start = now.withDayOfMonth(1).atStartOfDay();
                end = now.atTime(23, 59, 59);
                break;

            case "LAST_MONTH":
                LocalDate lastMonth = now.minusMonths(1);
                start = lastMonth.withDayOfMonth(1).atStartOfDay();
                end = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).atTime(23, 59, 59);
                break;

            case "THIS_QUARTER":
                // Quý hiện tại
                int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
                int startMonth = (currentQuarter - 1) * 3 + 1;
                start = now.withMonth(startMonth).withDayOfMonth(1).atStartOfDay();
                end = now.atTime(23, 59, 59);
                break;

            case "LAST_QUARTER":
                // Quý trước
                int lastQuarter = (now.getMonthValue() - 1) / 3;
                if (lastQuarter == 0) {
                    lastQuarter = 4;
                    now = now.minusYears(1);
                }
                int lastQuarterStartMonth = (lastQuarter - 1) * 3 + 1;
                int lastQuarterEndMonth = lastQuarter * 3;
                start = now.withMonth(lastQuarterStartMonth).withDayOfMonth(1).atStartOfDay();
                end = now.withMonth(lastQuarterEndMonth)
                        .withDayOfMonth(now.withMonth(lastQuarterEndMonth).lengthOfMonth())
                        .atTime(23, 59, 59);
                break;

            case "THIS_YEAR":
                start = now.withDayOfYear(1).atStartOfDay();
                end = now.atTime(23, 59, 59);
                break;

            case "LAST_YEAR":
                LocalDate lastYear = now.minusYears(1);
                start = lastYear.withDayOfYear(1).atStartOfDay();
                end = lastYear.withDayOfYear(lastYear.lengthOfYear()).atTime(23, 59, 59);
                break;

            case "LAST_365_DAYS":
                start = now.minusDays(364).atStartOfDay(); // Bao gồm hôm nay = 365 ngày
                end = now.atTime(23, 59, 59);
                break;

            default:
                // Mặc định là 30 ngày qua
                start = now.minusDays(29).atStartOfDay();
                end = now.atTime(23, 59, 59);
                break;
        }

        return new LocalDateTime[]{start, end};
    }


    /**
     * Tính toán khoảng thời gian trước đó có cùng độ dài để so sánh growth rate
     * @param start Thời điểm bắt đầu của khoảng thời gian hiện tại
     * @param end Thời điểm kết thúc của khoảng thời gian hiện tại
     * @return Array gồm [previousStartDateTime, previousEndDateTime]
     */
    private LocalDateTime[] calculatePreviousPeriod(LocalDateTime start, LocalDateTime end) {
        // Tính độ dài của khoảng thời gian hiện tại
        Duration duration = Duration.between(start, end);
        long durationDays = duration.toDays();

        // Nếu khoảng thời gian nhỏ hơn 1 ngày, tính theo giờ
        if (durationDays == 0) {
            long durationHours = duration.toHours();
            LocalDateTime previousStart = start.minusHours(durationHours + 1);
            LocalDateTime previousEnd = start.minusHours(1);
            return new LocalDateTime[]{previousStart, previousEnd};
        }

        // Tính khoảng thời gian trước đó có cùng độ dài
        LocalDateTime previousStart = start.minusDays(durationDays + 1);
        LocalDateTime previousEnd = start.minusDays(1);

        // Đảm bảo khoảng thời gian trước có cùng độ dài
        // Ví dụ: nếu khoảng hiện tại là 30 ngày, khoảng trước cũng phải là 30 ngày
        if (durationDays > 0) {
            // Điều chỉnh để có cùng số ngày
            long actualPreviousDays = Duration.between(previousStart, previousEnd).toDays();
            if (actualPreviousDays != durationDays) {
                previousStart = previousEnd.minusDays(durationDays);
            }
        }

        return new LocalDateTime[]{previousStart, previousEnd};
    }



    /**
     * Tính toán khoảng thời gian trước đó dựa trên loại period cụ thể
     * Method này cung cấp logic tính toán chính xác hơn cho từng loại period
     */
    private LocalDateTime[] calculatePreviousPeriodByType(String period, LocalDateTime start, LocalDateTime end) {
        if (period == null || period.equalsIgnoreCase("CUSTOM")) {
            return calculatePreviousPeriod(start, end);
        }

        switch (period.toUpperCase()) {
            case "TODAY":
                // Hôm qua
                return new LocalDateTime[]{
                        start.minusDays(1),
                        end.minusDays(1)
                };

            case "YESTERDAY":
                // Hôm kia
                return new LocalDateTime[]{
                        start.minusDays(1),
                        end.minusDays(1)
                };

            case "LAST_7_DAYS":
                // 7 ngày trước đó
                return new LocalDateTime[]{
                        start.minusDays(7),
                        end.minusDays(7)
                };

            case "LAST_30_DAYS":
                // 30 ngày trước đó
                return new LocalDateTime[]{
                        start.minusDays(30),
                        end.minusDays(30)
                };

            case "THIS_WEEK":
                // Tuần trước
                return new LocalDateTime[]{
                        start.minusWeeks(1),
                        start.minusWeeks(1).with(DayOfWeek.SUNDAY).toLocalDate().atTime(23, 59, 59)
                };

            case "LAST_WEEK":
                // Tuần trước nữa
                return new LocalDateTime[]{
                        start.minusWeeks(1),
                        end.minusWeeks(1)
                };

            case "THIS_MONTH":
                // Tháng trước
                LocalDate lastMonth = start.toLocalDate().minusMonths(1);
                return new LocalDateTime[]{
                        lastMonth.withDayOfMonth(1).atStartOfDay(),
                        lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).atTime(23, 59, 59)
                };

            case "LAST_MONTH":
                // Tháng trước nữa
                LocalDate twoMonthsAgo = start.toLocalDate().minusMonths(1);
                return new LocalDateTime[]{
                        twoMonthsAgo.withDayOfMonth(1).atStartOfDay(),
                        twoMonthsAgo.withDayOfMonth(twoMonthsAgo.lengthOfMonth()).atTime(23, 59, 59)
                };

            case "THIS_QUARTER":
                // Quý trước
                return new LocalDateTime[]{
                        start.minusMonths(3),
                        end.minusMonths(3)
                };

            case "LAST_QUARTER":
                // Quý trước nữa
                return new LocalDateTime[]{
                        start.minusMonths(3),
                        end.minusMonths(3)
                };

            case "THIS_YEAR":
                // Năm trước
                return new LocalDateTime[]{
                        start.minusYears(1),
                        end.minusYears(1)
                };

            case "LAST_YEAR":
                // Năm trước nữa
                return new LocalDateTime[]{
                        start.minusYears(1),
                        end.minusYears(1)
                };

            case "LAST_365_DAYS":
                // 365 ngày trước đó
                return new LocalDateTime[]{
                        start.minusDays(365),
                        end.minusDays(365)
                };

            default:
                return calculatePreviousPeriod(start, end);
        }
    }
}