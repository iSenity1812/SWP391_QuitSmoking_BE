package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.RawTrackingCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CravingTrackingRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

//class CravingTrackingDeletedException extends RuntimeException {
//    public CravingTrackingDeletedException(String message) {
//        super(message);
//    }
//}

@AllArgsConstructor
@Service
public class CravingTrackingService {
    private final CravingTrackingRepository cravingTrackingRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    //chuyển đổi entity thành response DTO
    private CravingTrackingResponse convertToResponseDto(CravingTracking cravingTracking) {
        return modelMapper.map(cravingTracking, CravingTrackingResponse.class);
    }

    //tổng hợp dữ liệu RawCraving theo giờ từ Redis và lưu vào CravingTracking
    //Nếu không có sự kiện nào trong giờ đó, hay bản ghi CravingTracking đã tồn tại và count = 0, nó sẽ bị xóa
    @Transactional
    public CravingTracking aggregateHourlyCravingData(LocalDateTime hourToAggregate) {
        //chuyển thời gian đầu vào về thời điểm bắt đầu chính xác của giờ đó
        LocalDateTime startOfHour = hourToAggregate.withMinute(0).withSecond(0).withNano(0);

        //Lấy tất cả các sự kiện thô trong giờ đó từ Redis dựa theo keys
        //Pattern: raw_craving_event:{epoch_day}:{hour_of_day}:*
        String keyPattern = String.format("raw_craving_event:%d:%d:*",
                startOfHour.toLocalDate().toEpochDay(),
                startOfHour.getHour());

        //tìm kiếm tất cả các khóa trong Redis khớp với keyPattern
        Set<String> rawEventKeys = stringRedisTemplate.keys(keyPattern);

        //Lấy danh sách các sự kiện thô theo giờ đó từ Redis
        //trả về list rỗng nếu không có keys nào
        List<RawTrackingCreateRequest> rawEventsInHour = rawEventKeys != null ? rawEventKeys.stream()
                .map(key -> {
                    //Lấy chuỗi JSON được lưu trữ dưới key
                    String eventJson = stringRedisTemplate.opsForValue().get(key);
                    if (eventJson != null) {
                        try {
                            //Chuyển đổi chuỗi JSON thành DTO
                            return objectMapper.readValue(eventJson, RawTrackingCreateRequest.class);
                        } catch (JsonProcessingException e) {
                            System.err.println("Lỗi khi đọc JSON từ Redis cho key " + key + ": " + e.getMessage());
                        }
                    }
                    return null;
                })
                .filter(java.util.Objects::nonNull) //loại bỏ các giá trị null khỏi stream (từ map)
                .collect(Collectors.toList()) : List.of();

        //Tổng hợp dữ liệu
        int totalSmoked = rawEventsInHour.stream().mapToInt(RawTrackingCreateRequest::getSmokedCount).sum();
        int totalCravings = rawEventsInHour.stream().mapToInt(RawTrackingCreateRequest::getCravingsCount).sum();

        //tìm record CravingTracking hiện có cho giờ-ngày này
        Optional<CravingTracking> existingAggregatedRecord = cravingTrackingRepository.findByDateTime(startOfHour);
        CravingTracking aggregatedRecord;
        if (existingAggregatedRecord.isPresent()) {
            aggregatedRecord = existingAggregatedRecord.get();
        } else {
            aggregatedRecord = new CravingTracking();
            aggregatedRecord.setTrackTime(startOfHour);
        }

        //Cập nhật số liệu
        aggregatedRecord.setSmokedCount(totalSmoked);
        aggregatedRecord.setCravingsCount(totalCravings);

        //Thu thập tất cả các giá trị Situation và WithWhom từ raw events
        Set<Situation> aggregatedSituations = rawEventsInHour.stream()
                .map(RawTrackingCreateRequest::getSituation)
                .filter(java.util.Objects::nonNull) //Lọc bỏ các giá trị null
                .collect(Collectors.toCollection(HashSet::new)); //HashSet để đảm bảo duy nhất

        Set<WithWhom> aggregatedWithWhoms = rawEventsInHour.stream()
                .map(RawTrackingCreateRequest::getWithWhom)
                .filter(java.util.Objects::nonNull) //Lọc bỏ các giá trị null
                .collect(Collectors.toCollection(HashSet::new)); //HashSet để đảm bảo duy nhất

        aggregatedRecord.setSituations(aggregatedSituations);
        aggregatedRecord.setWithWhoms(aggregatedWithWhoms);

        //Nếu tổng smokedCount và cravingsCount đều là 0, thì xóa bản ghi nếu tồn tại
        if (aggregatedRecord.getSmokedCount() == 0 && aggregatedRecord.getCravingsCount() == 0) {
            if (existingAggregatedRecord.isPresent()) {
                cravingTrackingRepository.delete(aggregatedRecord);
                if (rawEventKeys != null && !rawEventKeys.isEmpty()) {
                    stringRedisTemplate.delete(rawEventKeys); // Xóa tất cả raw events đã xử lý từ Redis
                }
                dailySummaryService.recalculateDailyTotals(dailySummary); //cập nhật DailySummary
                return null;
            }
            return null; //Không có bản ghi cũ và tổng bằng 0, không cần lưu
        }

        // Lưu bản ghi đã tổng hợp vào DB
        CravingTracking savedAggregatedRecord = createCravingTracking(aggregatedRecord);

        // Xóa tất cả raw events đã tổng hợp khỏi Redis - khi có sự kiện và đã lưu vào DB
        if (rawEventKeys != null && !rawEventKeys.isEmpty()) {
            stringRedisTemplate.delete(rawEventKeys);
        }

        dailySummaryService.recalculateDailyTotals(dailySummary); //cập nhật DailySummary

        return savedAggregatedRecord;
    }


    @Transactional
    public CravingTracking createCravingTracking(CravingTracking cravingTracking) {
        // Các Bean Validation trên entity sẽ tự động được kích hoạt
        return cravingTrackingRepository.save(cravingTracking);
    }

    @Transactional
    public CravingTrackingResponse updateCravingTracking(Integer cravingTrackingId, CravingTrackingUpdateRequest request) {
        Optional<CravingTracking> existingTrackingOptional = cravingTrackingRepository.findById(cravingTrackingId);

        if (existingTrackingOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy bản theo dõi với ID: " + cravingTrackingId);
        }

        CravingTracking existingTracking = existingTrackingOptional.get();
        LocalDate today = LocalDate.now();

        // Nếu ngày hiện tại đã vượt qua tracktime -> không cho phép chỉnh sửa
        if (existingTracking.getTrackTime().toLocalDate().isBefore(today)) {
            throw new DailySummaryEditForbiddenException("Không thể chỉnh sửa bản theo dõi đã qua");
        }
        // Nếu TrackTime là cùng ngày với ngày hiện tại, cho phép chỉnh sửa
        existingTracking.setSmokedCount(request.getSmokedCount());
        existingTracking.setCravingsCount(request.getCravingsCount());
        // Nếu request.getSituations() là null, giữ nguyên giá trị hiện có
        // Nếu request.getSituations() là một Set rỗng, nó sẽ xóa tất cả situations
        if (request.getSituations() != null) {
            existingTracking.setSituations(request.getSituations());
        }
        // Tương tự cho withWhoms
        if (request.getWithWhoms() != null) {
            existingTracking.setWithWhoms(request.getWithWhoms());
        }

        //Tự động xóa bản ghi nếu cả smokedCount và cravingsCount đều bằng 0
        if (existingTracking.getSmokedCount() == 0 && existingTracking.getCravingsCount() == 0) {
            cravingTrackingRepository.delete(existingTracking);
            throw new CravingTrackingDeletedException("Bản ghi theo dõi cơn thèm đã được xóa vì số lượng thuốc hút và số lần thèm thuốc đều bằng 0");
        }

        // Lưu bản ghi đã cập nhật
        // Các Bean Validation khác trên entity sẽ được áp dụng tự động khi save
        CravingTracking updatedTracking = cravingTrackingRepository.save(existingTracking);

        //tái tính toán tổng của DailySummary liên quan
        dailySummaryService.recalculateDailyTotals(existingTracking.getDailySummary());

        return convertToResponseDto(updatedTracking);
    }

    //review
    @Transactional(readOnly = true)
    public CravingTrackingResponseDTO getCravingTrackingById(Integer id) {
        CravingTracking cravingTracking = cravingTrackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CravingTracking not found with ID: " + id));
        return convertToResponseDto(cravingTracking);
    }

    @Transactional(readOnly = true)
    public List<CravingTrackingResponseDTO> getCravingTrackingsByDailySummaryId(Integer dailySummaryId) {
        DailySummary dailySummary = dailySummaryRepository.findById(dailySummaryId)
                .orElseThrow(() -> new ResourceNotFoundException("DailySummary not found with ID: " + dailySummaryId));

        return cravingTrackingRepository.findByDailySummary(dailySummary).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCravingTracking(Integer id) {
        CravingTracking trackingToDelete = cravingTrackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CravingTracking not found with ID: " + id));

        DailySummary dailySummary = trackingToDelete.getDailySummary(); // Lấy DailySummary trước khi xóa

        cravingTrackingRepository.deleteById(id);

        dailySummaryService.recalculateDailyTotals(dailySummary); // Gọi để cập nhật DailySummary
    }
}
