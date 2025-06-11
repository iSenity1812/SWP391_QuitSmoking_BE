package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.RawTrackingCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.RawTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
//không được lưu vào DB, chỉ lưu trữ tạm thời trong Redis
public class RawCravingTrackingService {
    private final DailySummaryRepository dailySummaryRepository;
    private final StringRedisTemplate stringRedisTemplate; // Inject StringRedisTemplate để thao tác với Redis
    private final ObjectMapper objectMapper; // Inject ObjectMapper để chuyển đổi DTO sang/từ JSON
    private final ModelMapper modelMapper;

    //chuyển đổi Request sang Response DTO, chỉ nhầm để xác nhận dữ liệu
    private RawTrackingResponse convertToResponseDto(RawTrackingCreateRequest request) {
        return modelMapper.map(request, RawTrackingResponse.class);
    }

    //Tạo một sự kiện thô về cơn thèm/hút thuốc mới
    //lưu sự kiện thô vào Redis, việc tổng hợp sẽ được xử lý bởi Scheduled Task
    @Transactional
    public RawTrackingResponse createRawCravingEvent(RawTrackingCreateRequest request) {
        try {
            // Chuyển đổi request DTO thành chuỗi JSON
            String eventJson = objectMapper.writeValueAsString(request);

            // Tạo một key duy nhất cho sự kiện thô trong Redis
            // Cấu trúc key: raw_craving_event:{epoch_day}:{hour_of_day}:{uuid}
            String key = String.format("raw_craving_event:%d:%d:%s",
                    request.getTrackTime().toLocalDate().toEpochDay(), // Ngày (Epoch Day)
                    request.getTrackTime().getHour(),
                    UUID.randomUUID().toString()); // ID duy nhất cho mỗi sự kiện

            // Lưu sự kiện vào Redis với TTL
            // TTL đảm bảo dữ liệu sẽ tự động hết hạn nếu scheduler không xử lý kịp trong khoảng thời gian chấp nhận được
            stringRedisTemplate.opsForValue().set(key, eventJson, 25, TimeUnit.HOURS);

            return convertToResponseDto(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi chuyển đổi RawTrackingCreateRequest sang JSON: " + e.getMessage(), e);
        }
    }
}
