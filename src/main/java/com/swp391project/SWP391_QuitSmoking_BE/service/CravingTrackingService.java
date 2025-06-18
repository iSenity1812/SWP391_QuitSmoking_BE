//package com.swp391project.SWP391_QuitSmoking_BE.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
//import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingUpdateRequest;
//import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.RawTrackingCreateRequest;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
//import com.swp391project.SWP391_QuitSmoking_BE.enums.Situation;
//import com.swp391project.SWP391_QuitSmoking_BE.enums.WithWhom;
//import com.swp391project.SWP391_QuitSmoking_BE.exception.CravingTrackingDeletedException;
//import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
//import com.swp391project.SWP391_QuitSmoking_BE.repository.CravingTrackingRepository;
//import lombok.AllArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@AllArgsConstructor
//@Service
//public class CravingTrackingService {
//    private final CravingTrackingRepository cravingTrackingRepository;
//    private final DailySummaryService dailySummaryService;
//    private final ModelMapper modelMapper;
//    private final ObjectMapper objectMapper;
//    private final StringRedisTemplate stringRedisTemplate;
//
//    //chuyển đổi entity thành response DTO
//    private CravingTrackingResponse convertToResponseDto(CravingTracking cravingTracking) {
//        if (cravingTracking == null) {
//            return null; // Trả về null nếu cravingTracking là null
//        }
//        CravingTrackingResponse response = modelMapper.map(cravingTracking, CravingTrackingResponse.class);
//        if (cravingTracking.getDailySummary() != null && cravingTracking.getDailySummary().getQuitPlan() != null &&
//                cravingTracking.getDailySummary().getQuitPlan().getMember() != null) {
//            response.setMemberId(cravingTracking.getDailySummary().getQuitPlan().getMember().getMemberId());
//        } else {
//            throw new ResourceNotFoundException("Không tìm thấy thông tin liên quan đến bản ghi. Không thể chuyển đổi sang DTO");
//        }
//        return response;
//    }
//
//    //tổng hợp dữ liệu RawCraving theo giờ từ Redis và lưu vào CravingTracking
//    //Nếu không có sự kiện nào trong giờ đó, hay bản ghi CravingTracking đã tồn tại và count = 0, nó sẽ bị xóa
//    //Sau khi tổng hợp từ các bản ghi Redis -> tìm CravingTracking hiện có cho giờ đó -> Nếu bản ghi không tồn tại -> tạo mới bản ghi
//    @Transactional
//    public CravingTracking aggregateHourlyCravingData(UUID memberId, LocalDateTime hourToAggregate) {
//        //chuyển thời gian đầu vào về thời điểm bắt đầu chính xác của giờ đó
//        LocalDateTime startOfHour = hourToAggregate.withMinute(0).withSecond(0).withNano(0);
//        LocalDate dateOfHour = startOfHour.toLocalDate();
//
//        // Tìm hoặc tạo DailySummary cho thành viên và ngày này
//        DailySummary dailySummary = dailySummaryService.findOrCreateDailySummary(memberId, dateOfHour);
//        if (dailySummary == null) {
//            //nếu DailySummaryService không tìm/tạo được
//            throw new IllegalStateException("Không tìm thấy hoặc tạo được DailySummary cho thành viên " + memberId + " vào ngày " + dateOfHour);
//        }
//
//        //Lấy tất cả các sự kiện thô trong giờ đó từ Redis dựa theo keys
//        //Pattern: raw_craving_event:{memberId}:{epoch_day}:{hour_of_day}:*
//        String keyPattern = String.format("raw_craving_event:%s:%d:%d:*",
//                memberId.toString(),
//                startOfHour.toLocalDate().toEpochDay(),
//                startOfHour.getHour());
//
//        //tìm kiếm tất cả các khóa trong Redis khớp với keyPattern
//        Set<String> rawEventKeys = stringRedisTemplate.keys(keyPattern);
//
//        //Lấy danh sách các sự kiện thô theo giờ đó từ Redis
//        //trả về list rỗng nếu không có keys nào
//        //Lấy chuỗi JSON được lưu trữ dưới key
//        //Chuyển đổi chuỗi JSON thành DTO
//        //loại bỏ các giá trị null khỏi stream (từ map)
//        List<RawTrackingCreateRequest> rawEventsInHour = List.of(); // Khởi tạo rỗng
//        if (!rawEventKeys.isEmpty()) {
//            rawEventsInHour = rawEventKeys.stream()
//                    .map(key -> {
//                        String eventJson = stringRedisTemplate.opsForValue().get(key);
//                        if (eventJson != null) {
//                            try {
//                                return objectMapper.readValue(eventJson, RawTrackingCreateRequest.class);
//                            } catch (JsonProcessingException e) {
//                                System.err.println("Lỗi khi đọc JSON từ Redis cho key " + key + ": " + e.getMessage());
//                            }
//                        }
//                        return null;
//                    })
//                    .filter(java.util.Objects::nonNull) // Loại bỏ các giá trị null khỏi stream (từ map)
//                    .toList();
//        }
//
//        //Tổng hợp dữ liệu
//        int totalSmoked = rawEventsInHour.stream().mapToInt(RawTrackingCreateRequest::getSmokedCount).sum();
//        int totalCravings = rawEventsInHour.stream().mapToInt(RawTrackingCreateRequest::getCravingsCount).sum();
//
//        //tìm record CravingTracking hiện có cho giờ-ngày này dựa theo DailySummary của member đang tạo
//        Optional<CravingTracking> existingAggregatedRecord = dailySummary.getCravingTrackings().stream()
//                .filter(ct -> ct.getTrackTime().withMinute(0).withSecond(0).withNano(0).equals(startOfHour))
//                .findFirst();
//
//        CravingTracking aggregatedRecord;
//        boolean isNewRecord = false;
//        if (existingAggregatedRecord.isPresent()) {
//            aggregatedRecord = existingAggregatedRecord.get();
//        } else {
//            aggregatedRecord = new CravingTracking();
//            aggregatedRecord.setTrackTime(startOfHour);
//        }
//
//        //Thu thập tất cả các giá trị Situation và WithWhom từ raw events
//        Set<Situation> aggregatedSituations = rawEventsInHour.stream()
//                .map(RawTrackingCreateRequest::getSituation)
//                .filter(java.util.Objects::nonNull) //Lọc bỏ các giá trị null
//                .collect(Collectors.toCollection(HashSet::new)); //HashSet để đảm bảo duy nhất
//
//        Set<WithWhom> aggregatedWithWhoms = rawEventsInHour.stream()
//                .map(RawTrackingCreateRequest::getWithWhom)
//                .filter(java.util.Objects::nonNull) //Lọc bỏ các giá trị null
//                .collect(Collectors.toCollection(HashSet::new)); //HashSet để đảm bảo duy nhất
//
//        //Cập nhật giá trị mới tổng hợp
//        aggregatedRecord.setSmokedCount(totalSmoked);
//        aggregatedRecord.setCravingsCount(totalCravings);
//        aggregatedRecord.setSituations(aggregatedSituations);
//        aggregatedRecord.setWithWhoms(aggregatedWithWhoms);
//
//        //Nếu tổng smokedCount và cravingsCount đều là 0, thì xóa bản ghi nếu tồn tại
//        if (aggregatedRecord.getSmokedCount() == 0 && aggregatedRecord.getCravingsCount() == 0) {
//            if (existingAggregatedRecord.isPresent()) { // Chỉ xóa nếu là bản ghi đã tồn tại trong DB
//                deleteCravingTracking(dailySummary, aggregatedRecord.getCravingTrackingId());
//            }
//            if (!rawEventKeys.isEmpty()) {
//                stringRedisTemplate.delete(rawEventKeys); // Xóa tất cả raw events đã xử lý từ Redis
//            }
//            return null; //Không có bản ghi cũ và tổng bằng 0, không cần lưu
//        }
//
//        // Lưu/cập nhật bản ghi đã tổng hợp vào DB
//        CravingTracking savedAggregatedRecord = createCravingTracking(dailySummary, aggregatedRecord);
//
//        // Xóa tất cả raw events đã tổng hợp khỏi Redis - khi có sự kiện và đã lưu vào DB
//        if (!rawEventKeys.isEmpty()) {
//            stringRedisTemplate.delete(rawEventKeys);
//        }
//
//        return savedAggregatedRecord;
//    }
//
//    @Transactional
//    public CravingTracking createCravingTracking(DailySummary dailySummary, CravingTracking cravingTracking) {
//        // Kiểm tra xem cravingTracking đã có trong danh sách của dailySummary chưa
//        //nếu là bản ghi mới, thêm vào DailySummary
//        if (!dailySummary.getCravingTrackings().contains(cravingTracking)) {
//            cravingTracking.setDailySummary(dailySummary);
//            dailySummary.getCravingTrackings().add(cravingTracking);
//        }
//        // Các Bean Validation trên entity sẽ tự động được kích hoạt
//        CravingTracking savedRecord = cravingTrackingRepository.save(cravingTracking);
//        // Tái tính toán tổng của DailySummary liên quan sau khi CravingTracking được lưu/cập nhật
//        dailySummaryService.recalculateDailyTotals(dailySummary);
//        return savedRecord;
//    }
//
//    @Transactional
//    public void deleteCravingTracking(DailySummary dailySummary, Integer id) {
//        CravingTracking cravingTracking = cravingTrackingRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi với ID: " + id));
//        if (!Objects.equals(cravingTracking.getDailySummary().getDailySummaryId(), dailySummary.getDailySummaryId())) {
//            throw new IllegalArgumentException("Bản ghi theo dõi không thuộc về DailySummary được cung cấp");
//        }
//        dailySummary.getCravingTrackings().remove(cravingTracking);
//        dailySummaryService.recalculateDailyTotals(dailySummary); //cập nhật DailySummary
//        //cravingTrackingRepository.deleteById(id); // Không cần xóa trực tiếp vì orphanRemoval = true đã xử lý việc này
//    }
//
//    @Transactional
//    public CravingTrackingResponse updateCravingTracking(Integer cravingTrackingId, CravingTrackingUpdateRequest request) {
//        Optional<CravingTracking> existingTrackingOptional = cravingTrackingRepository.findById(cravingTrackingId);
//
//        if (existingTrackingOptional.isEmpty()) {
//            throw new IllegalArgumentException("Không tìm thấy bản theo dõi với ID: " + cravingTrackingId);
//        }
//
//        CravingTracking existingTracking = existingTrackingOptional.get();
//        LocalDate today = LocalDate.now();
//
//        // Nếu ngày hiện tại đã vượt qua tracktime -> không cho phép chỉnh sửa
//        if (existingTracking.getTrackTime().toLocalDate().isBefore(today)) {
//            throw new DailySummaryEditForbiddenException("Không thể chỉnh sửa bản theo dõi đã qua");
//        }
//        // Nếu TrackTime là cùng ngày với ngày hiện tại, cho phép chỉnh sửa
//        existingTracking.setSmokedCount(request.getSmokedCount());
//        existingTracking.setCravingsCount(request.getCravingsCount());
//        // Nếu request.getSituations() là null, giữ nguyên giá trị hiện có
//        // Nếu request.getSituations() là một Set rỗng, nó sẽ xóa tất cả situations
//        if (request.getSituations() != null) {
//            existingTracking.setSituations(request.getSituations());
//        }
//        if (request.getWithWhoms() != null) {
//            existingTracking.setWithWhoms(request.getWithWhoms());
//        }
//
//        //Tự động xóa bản ghi nếu cả smokedCount và cravingsCount đều bằng 0
//        if (existingTracking.getSmokedCount() == 0 && existingTracking.getCravingsCount() == 0) {
//            deleteCravingTracking(existingTracking.getDailySummary(), existingTracking.getCravingTrackingId());
//            throw new CravingTrackingDeletedException("Bản ghi đã được xóa vì số lượng thuốc hút và số lần thèm thuốc đều bằng 0");
//        }
//
//        // Lưu bản ghi đã cập nhật
//        // Các Bean Validation khác trên entity sẽ được áp dụng tự động khi save
//        CravingTracking updatedTracking = cravingTrackingRepository.save(existingTracking);
//
//        //tái tính toán tổng của DailySummary liên quan
//        dailySummaryService.recalculateDailyTotals(existingTracking.getDailySummary());
//
//        return convertToResponseDto(updatedTracking);
//    }
//
//    @Transactional
//    public CravingTrackingResponse getCravingTrackingById(Integer id) {
//        CravingTracking cravingTracking = cravingTrackingRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi với ID: " + id));
//        return convertToResponseDto(cravingTracking);
//    }
//
//    @Transactional
//    public List<CravingTrackingResponse> getCravingTrackingsByDate(LocalDate date) {
//        List<CravingTracking> cravingTrackingList = cravingTrackingRepository.findAllByDate(date);
//        if (cravingTrackingList.isEmpty()) {
//            throw new ResourceNotFoundException("Không tìm thấy bản ghi nào cho ngày: " + date);
//        }
//
//        return cravingTrackingList.stream()
//                .map(this::convertToResponseDto)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public List<CravingTrackingResponse> getCravingTrackingsByDailySummaryId(Integer dailySummaryId) {
//        List<CravingTracking> cravingTrackingList = cravingTrackingRepository.findByDailySummary_DailySummaryId(dailySummaryId);
//        if (cravingTrackingList.isEmpty()) {
//            throw new ResourceNotFoundException("Không tìm thấy bản ghi nào cho daily summary: " + dailySummaryId);
//        }
//        return cravingTrackingList.stream()
//                .map(this::convertToResponseDto)
//                .collect(Collectors.toList());
//    }
//}
