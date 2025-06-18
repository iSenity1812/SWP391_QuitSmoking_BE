//package com.swp391project.SWP391_QuitSmoking_BE.util;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.RawTrackingCreateRequest;
//import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
//import com.swp391project.SWP391_QuitSmoking_BE.service.CravingTrackingService;
//import jakarta.transaction.Transactional;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import lombok.Data;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
//@Component
//@AllArgsConstructor
//// Lớp này sẽ được sử dụng để định kỳ tổng hợp dữ liệu cho craving tracking theo giờ
//public class HourlyAggregationScheduler {
//    private final DailySummaryRepository dailySummaryRepository;
//    private final CravingTrackingService cravingTrackingService;
//    private final StringRedisTemplate stringRedisTemplate;
//    private final ObjectMapper objectMapper; //để chuyển đổi JSON
//
//    //cơ chế thử lại
//    private static final int MAX_RETRIES = 3; // Số lần thử lại tối đa cho một tác vụ bị lỗi
//    private static final String FAILED_AGGREGATION_KEY_PREFIX = "failed_hourly_aggregation:";
//
//    //để lưu trữ thông tin tác vụ tổng hợp bị lỗi vào Redis
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    private static class FailedAggregationTask {
//        private UUID memberId;
//        private LocalDateTime hourToAggregate;
//        private int retryCount;
//    }
//
//    //định kỳ tổng hợp dữ liệu RawCravingEvent của giờ trước đó (so với giờ hiện tại)
//    //sau đó cập nhật/tạo bản ghi trong CravingTracking
//    @Scheduled(cron = "0 5 * * * ?") // Chạy vào phút thứ 5 của mỗi giờ (ví dụ: 14:05:00, 15:05:00)
//    //đảm bảo tất cả các sự kiện của giờ trước đó (ví dụ: 13:00 - 13:59:59) đã hoàn tất
//    @Transactional
//    public void aggregatePreviousHourData() {
//        LocalDateTime now = LocalDateTime.now();
//        //tính toán giờ cần tổng hợp (ví dụ: nếu bây giờ là 14:05, chúng ta muốn tổng hợp giờ 13:00)
//        LocalDateTime hourToAggregate = now.minusHours(1).withMinute(0).withSecond(0).withNano(0);
//        LocalDate dateOfHour = hourToAggregate.toLocalDate();
//
//        System.out.println("Bắt đầu tổng hợp dữ liệu thèm thuốc theo giờ cho: " + hourToAggregate);
//
//        // Pattern để lấy tất cả các khóa sự kiện thô cho giờ cần tổng hợp, bất kể memberId
//        // Format: raw_craving_event:{memberId_wildcard}:{epoch_day}:{hour_of_day}:{uuid_wildcard}
//        String keyPattern = String.format("raw_craving_event:*:%d:%d:*",
//                dateOfHour.toEpochDay(),
//                hourToAggregate.getHour());
//
//        //tìm kiếm tất cả các khóa trong Redis khớp với keyPattern
//        Set<String> rawEventKeys = stringRedisTemplate.keys(keyPattern);
//
//        if (rawEventKeys.isEmpty()) { //Trường hợp không có raw events mới
//            System.out.println("Không có sự kiện thô nào để tổng hợp cho giờ: " + hourToAggregate);
//            return;
//        }
//
//        // Lấy tất cả các sự kiện thô từ Redis và nhóm chúng theo memberId
//        Map<UUID, List<RawTrackingCreateRequest>> groupedEventsByMember = rawEventKeys.stream()
//                .map(key -> {
//                    String eventJson = stringRedisTemplate.opsForValue().get(key);
//                    try {
//                        // Chuyển đổi chuỗi JSON thành DTO
//                        return (eventJson != null) ? objectMapper.readValue(eventJson, RawTrackingCreateRequest.class) : null;
//                    } catch (JsonProcessingException e) {
//                        System.err.println("Lỗi khi đọc JSON từ Redis cho key " + key + ": " + e.getMessage());
//                        return null;
//                    }
//                })
//                .filter(java.util.Objects::nonNull) // Loại bỏ các giá trị null
//                .collect(Collectors.groupingBy(RawTrackingCreateRequest::getMemberId)); // Nhóm theo memberId
//
//        //lặp qua từng memberId và gọi aggregateHourlyCravingData để xử lý tổng hợp cho từng người dùng
//        for (Map.Entry<UUID, List<RawTrackingCreateRequest>> entry : groupedEventsByMember.entrySet()) {
//            UUID memberId = entry.getKey();
//            try {
//                //tổng hợp dữ liệu cho thành viên này và giờ này
//                cravingTrackingService.aggregateHourlyCravingData(memberId, hourToAggregate);
//                System.out.println("Đã tổng hợp dữ liệu cho thành viên ID: " + memberId + " vào giờ: " + hourToAggregate.getHour());
//            } catch (Exception e) {
//                System.err.println("Lỗi khi tổng hợp dữ liệu cho thành viên ID: " + memberId + " vào giờ: " + hourToAggregate.getHour() + ": " + e.getMessage());
//                //Lưu thông tin tác vụ bị lỗi vào Redis để thử lại sau
//                FailedAggregationTask failedTask = new FailedAggregationTask(memberId, hourToAggregate, 0); //Bắt đầu với retryCount = 0
//                try {
//                    String failedTaskJson = objectMapper.writeValueAsString(failedTask);
//                    String failedKey = FAILED_AGGREGATION_KEY_PREFIX + memberId.toString() + ":" + dateOfHour.toEpochDay() + ":" + hourToAggregate.getHour();
//                    stringRedisTemplate.opsForValue().set(failedKey, failedTaskJson, 48, TimeUnit.HOURS); // Lưu với TTL 48 giờ
//                    System.out.println("Đã lưu tác vụ tổng hợp bị lỗi vào Redis để thử lại: " + failedKey);
//                } catch (JsonProcessingException jsonE) {
//                    System.err.println("Lỗi khi chuyển đổi FailedAggregationTask sang JSON: " + jsonE.getMessage());
//                }
//            }
//        }
//        System.out.println("Hoàn tất tổng hợp dữ liệu cho các bảng ghi theo giờ");
//    }
//
//    //Tác vụ định kỳ thử lại các tác vụ tổng hợp dữ liệu đã thất bại trước đó
//
//    @Scheduled(fixedRate = 1800000) // Chạy mỗi 30 phút (1800000ms)
//    @Transactional
//    public void retryFailedAggregations() {
//        System.out.println("Bắt đầu thử lại các tác vụ tổng hợp bị lỗi");
//
//        Set<String> failedKeys = stringRedisTemplate.keys(FAILED_AGGREGATION_KEY_PREFIX + "*");
//
//        if (failedKeys.isEmpty()) {
//            System.out.println("Không có tác vụ tổng hợp bị lỗi nào");
//            return;
//        }
//
//        for (String key : failedKeys) {
//            String failedTaskJson = stringRedisTemplate.opsForValue().get(key);
//            if (failedTaskJson == null) {
//                System.out.println(" Key có thể đã hết hạn hoặc bị xóa");
//                continue;
//            }
//
//            try {
//                FailedAggregationTask failedTask = objectMapper.readValue(failedTaskJson, FailedAggregationTask.class);
//
//                // Kiểm tra số lần thử lại
//                if (failedTask.getRetryCount() >= MAX_RETRIES) {
//                    System.err.println("Tác vụ tổng hợp cho thành viên ID: " + failedTask.getMemberId() +
//                            " vào giờ: " + failedTask.getHourToAggregate().getHour() +
//                            " đã đạt số lần thử lại tối đa (" + MAX_RETRIES + "). Bỏ qua và xóa key.");
//                    stringRedisTemplate.delete(key); // Xóa key để không thử lại nữa
//                    // Có thể gửi cảnh báo đến admin hoặc lưu vào dead-letter queue
//                    continue;
//                }
//
//                System.out.println("Đang thử lại tác vụ tổng hợp cho thành viên ID: " + failedTask.getMemberId() +
//                        " vào giờ: " + failedTask.getHourToAggregate().getHour() +
//                        " (lần thử: " + (failedTask.getRetryCount() + 1) + ")");
//
//                // Thử lại quá trình tổng hợp
//                cravingTrackingService.aggregateHourlyCravingData(failedTask.getMemberId(), failedTask.getHourToAggregate());
//
//                System.out.println("Tác vụ tổng hợp đã thử lại thành công cho thành viên ID: " + failedTask.getMemberId() +
//                        " vào giờ: " + failedTask.getHourToAggregate().getHour() + ". Xóa key lỗi.");
//                stringRedisTemplate.delete(key); // Xóa key khi thành công
//
//            } catch (Exception e) {
//                System.err.println("Tác vụ tổng hợp bị lỗi lại cho key " + key + ": " + e.getMessage());
//                // Tăng số lần thử lại và lưu lại vào Redis
//                try {
//                    failedTaskJson = stringRedisTemplate.opsForValue().get(key);
//                    if (failedTaskJson != null) {
//                        FailedAggregationTask updatedTask = objectMapper.readValue(failedTaskJson, FailedAggregationTask.class);
//                        updatedTask.setRetryCount(updatedTask.getRetryCount() + 1);
//                        String updatedTaskJson = objectMapper.writeValueAsString(updatedTask);
//                        stringRedisTemplate.opsForValue().set(key, updatedTaskJson, 48, TimeUnit.HOURS); // Cập nhật và giữ TTL
//                    }
//                } catch (JsonProcessingException jsonE) {
//                    System.err.println("Lỗi khi cập nhật FailedAggregationTask trong Redis cho key " + key + ": " + jsonE.getMessage());
//                }
//            }
//        }
//        System.out.println("Hoàn tất thử lại các tác vụ tổng hợp bị lỗi");
//    }
//}
