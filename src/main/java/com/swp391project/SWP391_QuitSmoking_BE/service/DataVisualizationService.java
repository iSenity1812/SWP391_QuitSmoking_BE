package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.HourlyChartDataResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailyChartDataResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryResponse;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DataVisualizationService {
    private final CravingTrackingService cravingTrackingService;
    private final DailySummaryRepository dailySummaryRepository;
    private final DailySummaryService dailySummaryService;

    //Lấy dữ liệu theo giờ trong một ngày cụ thể của người dùng
    //Nếu một giờ không có bản ghi, nó sẽ được điền với giá trị 0
    @Transactional
    public List<HourlyChartDataResponse> getHourlyDataForDay(UUID memberId, LocalDate date) {
        // Lấy tất cả các bản ghi CravingTracking cho ngày và thành viên này
        List<CravingTrackingResponse> cravingTrackings = cravingTrackingService.getCravingTrackingsByDate(memberId, date);

        //dùng MAP để dễ dàng tra cứu dữ liệu theo giờ
        Map<Integer, CravingTrackingResponse> hourlyMap = cravingTrackings.stream()
                .collect(Collectors.toMap(
                        ct -> ct.getTrackTime().getHour(), // Key là giờ
                        ct -> ct // Value là bản ghi CravingTrackingResponse
                ));

        List<HourlyChartDataResponse> result = new ArrayList<>();
        // Điền dữ liệu cho 24 giờ
        for (int hour = 0; hour < 24; hour++) {
            CravingTrackingResponse record = hourlyMap.get(hour);
            if (record != null) {
                result.add(new HourlyChartDataResponse(
                        hour,
                        record.getSmokedCount(),
                        record.getCravingsCount(),
                        record.getSituations(),
                        record.getWithWhoms()
                ));
            } else {
                // Giờ không có dữ liệu sẽ có giá trị 0 và Set rỗng cho situations/withWhoms
                result.add(new HourlyChartDataResponse(hour, 0, 0));
            }
        }
        return result;
    }

    //Lấy dữ liệu tổng hợp theo ngày cho biểu đồ dạng tuần hoặc tháng
    //Nếu một ngày không có bản ghi DailySummary, nó sẽ được điền với giá trị 0
    @Transactional
    public List<DailyChartDataResponse> getDailyDataForPeriod(UUID memberId, LocalDate startDate, LocalDate endDate) {
        // Lấy tất cả các bản ghi DailySummary cho khoảng thời gian và thành viên này
        List<DailySummaryResponse> dailySummaries = dailySummaryService.getDailySummariesByDateBetween(memberId, startDate, endDate);

        // Tạo một Map để dễ dàng tra cứu dữ liệu theo ngày
        Map<LocalDate, DailySummaryResponse> dailyMap = dailySummaries.stream()
                .collect(Collectors.toMap(
                        DailySummaryResponse::getTrackDate, // Key là ngày
                        ds -> ds));     // Value

        List<DailyChartDataResponse> result = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            DailySummaryResponse record = dailyMap.get(currentDate);
            if (record != null) {
                result.add(new DailyChartDataResponse(
                        currentDate,
                        record.getTotalSmokedCount(),
                        record.getTotalCravingCount(),
                        record.getMoneySaved(),
                        record.getMood()
                ));
            } else {
                result.add(new DailyChartDataResponse(
                        currentDate,
                        0,
                        0,
                        BigDecimal.ZERO, // Hoặc giá trị mặc định phù hợp cho tiền tiết kiệm
                        null //mặc định là null nếu không có DailySummary cho ngày đó
                ));
            }
            currentDate = currentDate.plusDays(1); // Chuyển sang ngày tiếp theo
        }
        return result;
    }
}
