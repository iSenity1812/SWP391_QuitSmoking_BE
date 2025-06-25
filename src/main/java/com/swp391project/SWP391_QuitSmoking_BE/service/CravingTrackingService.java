package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.exception.CravingTrackingDeletedException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DailySummaryEditForbiddenException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CravingTrackingRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CravingTrackingService {
    private final CravingTrackingRepository cravingTrackingRepository;
    private final DailySummaryService dailySummaryService;
    private final ModelMapper modelMapper;
    @Lazy //to break circular dependency
    private final QuitPlanService quitPlanService;

    //chuyển đổi entity thành response DTO
    public CravingTrackingResponse convertToResponseDto(CravingTracking cravingTracking) {
        if (cravingTracking == null) {
            return null; // Trả về null nếu cravingTracking là null
        }
        return modelMapper.map(cravingTracking, CravingTrackingResponse.class);
    }

//    @Transactional
//    public CravingTracking createCravingTracking(DailySummary dailySummary, CravingTracking cravingTracking) {
//        // Kiểm tra xem cravingTracking đã có trong danh sách của dailySummary chưa
//        //nếu là bản ghi mới, thêm vào DailySummary
//        cravingTracking.setDailySummary(dailySummary);
//        if (!dailySummary.getCravingTrackings().contains(cravingTracking)) {
//            dailySummary.getCravingTrackings().add(cravingTracking);
//        }
//        // Các Bean Validation trên entity sẽ tự động được kích hoạt
//        CravingTracking savedRecord = cravingTrackingRepository.save(cravingTracking);
//        // Tái tính toán tổng của DailySummary liên quan sau khi CravingTracking được lưu/cập nhật
//        dailySummaryService.recalculateDailyTotals(dailySummary);
//        return savedRecord;
//    }

    @Transactional
    public CravingTrackingResponse createOrUpdateTracking(UUID memberId, CravingTrackingCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request trống, không thể tạo ghi nhận");
        }

        if (request.getCravingsCount() == null && request.getSmokedCount() == null) {
            throw new IllegalArgumentException
                    ("Request phải chứa ít nhất một trong hai trường cravingsCount hoặc smokedCount");
        }

        LocalDateTime startOfHour;
        LocalDate dateOfHour;

        //Nếu không có trackTime trong request, sử dụng thời gian hiện tại
        LocalDateTime trackTime = request.getTrackTime()!= null ? request.getTrackTime() : LocalDateTime.now();

        // Chỉ cho phép ghi nhận cho ngày hiện tại
        if (!trackTime.toLocalDate().isEqual(LocalDate.now())) {
            throw new DailySummaryEditForbiddenException("Không thể ghi nhận cho ngày đã qua");
        }

        // Chuẩn hóa trackTime về đầu giờ (ví dụ: 13:12:30 -> 13:00:00)
        startOfHour = trackTime.withMinute(0).withSecond(0).withNano(0);
        dateOfHour = startOfHour.toLocalDate();

        // Tìm hoặc tạo DailySummary cho thành viên này và ngày này
        // Nếu không tìm thấy sẽ tạo mới
        DailySummary dailySummary = dailySummaryService.findOrCreateDailySummary(memberId, dateOfHour);
        if (dailySummary == null) {
            throw new IllegalStateException
                    ("Không tìm thấy hoặc tạo được DailySummary cho thành viên " + memberId + " vào ngày " + dateOfHour);
        }

        // Tìm bản ghi CravingTracking hiện có cho giờ đó trong DailySummary này
        Optional<CravingTracking> existingAggregatedRecord = dailySummary.getCravingTrackings().stream()
                .filter(ct -> ct.getTrackTime().withMinute(0).withSecond(0).withNano(0).equals(startOfHour))
                .findFirst();

        CravingTracking aggregatedRecord;
        boolean isNewRecord = false;

        if (existingAggregatedRecord.isPresent()) { // Nếu đã có bản ghi cho giờ này -> cập nhật nó
            aggregatedRecord = existingAggregatedRecord.get();
            // Cộng dồn số điếu hút và số lần thèm thuốc
            if (request.getSmokedCount() != null) {
                aggregatedRecord.setSmokedCount(aggregatedRecord.getSmokedCount() + request.getSmokedCount());
            }
            if (request.getCravingsCount() != null) {
                aggregatedRecord.setCravingsCount(aggregatedRecord.getCravingsCount() + request.getCravingsCount());
            }
            // Hợp nhất Situations và WithWhoms (nếu có)
            if (request.getSituation() != null) {
                if (aggregatedRecord.getSituations() == null) {
                    aggregatedRecord.setSituations(new HashSet<>());
                }
                aggregatedRecord.getSituations().add(request.getSituation());
            }
            if (request.getWithWhom() != null) {
                if (aggregatedRecord.getWithWhoms() == null) {
                    aggregatedRecord.setWithWhoms(new HashSet<>());
                }
                aggregatedRecord.getWithWhoms().add(request.getWithWhom());
            }
        } else { // Tạo bản ghi mới nếu chưa tồn tại cho ngày giờ này
            aggregatedRecord = new CravingTracking();
            aggregatedRecord.setTrackTime(startOfHour);
            aggregatedRecord.setDailySummary(dailySummary);
            if (request.getSmokedCount() == null) {
                aggregatedRecord.setSmokedCount(0); // Mặc định là 0 nếu không có giá trị
            } else {
                aggregatedRecord.setSmokedCount(request.getSmokedCount());
            }
            if (request.getCravingsCount() == null) {
                aggregatedRecord.setCravingsCount(0); // Mặc định là 0 nếu không có giá trị
            } else {
                aggregatedRecord.setCravingsCount(request.getCravingsCount());
            }
            aggregatedRecord.setSituations(request.getSituation() != null ? new HashSet<>(Set.of(request.getSituation())) : new HashSet<>());
            aggregatedRecord.setWithWhoms(request.getWithWhom() != null ? new HashSet<>(Set.of(request.getWithWhom())) : new HashSet<>());
            isNewRecord = true;
        }

        // xóa bản ghi nếu tổng smokedCount và cravingsCount đều là 0
        if (aggregatedRecord.getSmokedCount() == 0 && aggregatedRecord.getCravingsCount() == 0) {
            if (!isNewRecord) { // Chỉ xóa nếu là bản ghi đã tồn tại
                deleteCravingTracking(dailySummary, aggregatedRecord.getCravingTrackingId());
            }
            return null; // Không có bản ghi để lưu hoặc đã bị xóa
        }

        // Lưu/cập nhật bản ghi vào DB
        if (isNewRecord) {
            dailySummary.getCravingTrackings().add(aggregatedRecord);
        }
        CravingTracking savedAggregatedRecord = cravingTrackingRepository.save(aggregatedRecord);

        // Tái tính toán tổng của DailySummary liên quan
        dailySummaryService.recalculateDailyTotals(dailySummary);

        return convertToResponseDto(savedAggregatedRecord);
    }

    @Transactional
    public void deleteCravingTracking(DailySummary dailySummary, Integer id) {
        CravingTracking cravingTracking = cravingTrackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi với ID: " + id));
        if (!Objects.equals(cravingTracking.getDailySummary().getDailySummaryId(), dailySummary.getDailySummaryId())) {
            throw new IllegalArgumentException("Bản ghi theo dõi không thuộc về DailySummary được cung cấp");
        }
        dailySummary.getCravingTrackings().remove(cravingTracking);
        cravingTrackingRepository.delete(cravingTracking);
        dailySummaryService.recalculateDailyTotals(dailySummary); //cập nhật DailySummary
    }

    @Transactional
    public void deleteCravingTracking(Integer id) {
        CravingTracking cravingTracking = cravingTrackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi với ID: " + id));
        if (cravingTracking.getDailySummary() == null) {
            throw new IllegalArgumentException("Bản ghi theo dõi không thuộc về DailySummary nào");
        }
        DailySummary dailySummary = cravingTracking.getDailySummary();
        dailySummary.getCravingTrackings().remove(cravingTracking);
        cravingTrackingRepository.delete(cravingTracking);
        dailySummaryService.recalculateDailyTotals(dailySummary); //cập nhật DailySummary
    }

    @Transactional(noRollbackFor = CravingTrackingDeletedException.class)
    public CravingTrackingResponse updateCravingTracking(Integer cravingTrackingId, CravingTrackingUpdateRequest request) {
        Optional<CravingTracking> existingTrackingOptional = cravingTrackingRepository.findById(cravingTrackingId);
        if (existingTrackingOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy bản theo dõi với ID: " + cravingTrackingId);
        }

        CravingTracking existingTracking = existingTrackingOptional.get();

        //Kiểm tra xem bản ghi có thuộc về QuitPlan đang hoạt động không
        if (existingTracking.getDailySummary() == null || existingTracking.getDailySummary().getQuitPlan() == null) {
            throw new ResourceNotFoundException("Không tìm thấy DailySummary hoặc QuitPlan liên kết với bản ghi này");
        }
        if (!quitPlanService.isPlanInProgress(existingTracking.getDailySummary().getQuitPlan())) {
            throw new IllegalArgumentException("Kế hoạch bỏ thuốc lá đã kết thúc, không thể chỉnh sửa bản theo dõi");
        }

        LocalDate today = LocalDate.now();

        //Chỉ cho phép cập nhật bản ghi cho ngày hiện tại
        if (!existingTracking.getTrackTime().toLocalDate().isEqual(today)) {
            throw new DailySummaryEditForbiddenException("Chỉ có thể cập nhật bản theo dõi cho ngày hiện tại");
        }
        // Nếu TrackTime là cùng ngày với ngày hiện tại, cho phép chỉnh sửa
        if (request.getSmokedCount() != null) {
            existingTracking.setSmokedCount(request.getSmokedCount());
        }
        if (request.getCravingsCount() != null) {
            existingTracking.setCravingsCount(request.getCravingsCount());
        }
        // Nếu request.getSituations() là null, giữ nguyên giá trị hiện có
        // Nếu request.getSituations() là một Set rỗng, nó sẽ xóa tất cả situations
        // Tạo HashSet mới để tránh lỗi thay đổi Set trực tiếp
        if (request.getSituations() != null) {
            existingTracking.setSituations(new HashSet<>(request.getSituations()));
        }
        if (request.getWithWhoms() != null) {
            existingTracking.setWithWhoms(new HashSet<>(request.getWithWhoms()));
        }

        //Tự động xóa bản ghi nếu cả smokedCount và cravingsCount đều bằng 0
        if (existingTracking.getSmokedCount() == 0 && existingTracking.getCravingsCount() == 0) {
            deleteCravingTracking(existingTracking.getDailySummary(), existingTracking.getCravingTrackingId());
            throw new CravingTrackingDeletedException
                    ("Bản ghi đã được xóa vì số lượng thuốc hút và số lần thèm thuốc đều bằng 0");
        }

        // Lưu bản ghi đã cập nhật
        // Các Bean Validation khác trên entity sẽ được áp dụng tự động khi save
        CravingTracking updatedTracking = cravingTrackingRepository.save(existingTracking);

        //tái tính toán tổng của DailySummary liên quan
        dailySummaryService.recalculateDailyTotals(existingTracking.getDailySummary());
        return convertToResponseDto(updatedTracking);
    }

    @Transactional(readOnly = true)
    public CravingTracking getCravingTrackingById(Integer id) {
        return cravingTrackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi với ID: " + id));
    }

    @Transactional(readOnly = true)
    public UUID getMemberIdByCravingTrackingId(Integer cravingTrackingId) {
        CravingTracking cravingTracking = getCravingTrackingById(cravingTrackingId);
        if (cravingTracking.getDailySummary() == null ) {
            throw new ResourceNotFoundException("Không tìm thấy bản ghi hằng ngày liên kết với bản ghi này");
        }
        if (cravingTracking.getDailySummary().getQuitPlan() == null) {
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc lá liên kết với bản ghi này");
        }
        if (cravingTracking.getDailySummary().getQuitPlan().getMember() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thành viên liên kết với bản ghi này");
        }
        if (cravingTracking.getDailySummary().getQuitPlan().getMember().getMemberId() == null) {
            throw new ResourceNotFoundException("Không tìm thấy ID thành viên cho bản ghi này");
        }
        return cravingTracking.getDailySummary().getQuitPlan().getMember().getMemberId();
    }

    //Lấy danh sách các bản ghi CravingTracking theo ngày
    //Phương thức này sẽ trả về tất cả các bản ghi CravingTracking (tức là tổng hợp theo giờ)
    //cho một ngày cụ thể của một thành viên
    @Transactional(readOnly = true)
    public List<CravingTrackingResponse> getCravingTrackingsByDate(UUID memberId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX); // Cuối ngày (23:59:59.999...)

        List<CravingTracking> cravingTrackingList = cravingTrackingRepository.
                findAllByDailySummary_QuitPlan_Member_MemberIdAndTrackTimeBetween(memberId, startOfDay, endOfDay);

        if(cravingTrackingList.isEmpty()) {
            // Thay vì ném ResourceNotFoundException nếu danh sách rỗng
            // trả về danh sách rỗng để DataVisualizationService có thể xử lý điền giá trị 0
            return List.of(); // Trả về danh sách rỗng immutable
        }

        return cravingTrackingList.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CravingTrackingResponse> getCravingTrackingResponsesByDailySummaryId(Integer dailySummaryId) {
        List<CravingTracking> cravingTrackingList = cravingTrackingRepository.
                findByDailySummary_DailySummaryId(dailySummaryId);
        if (cravingTrackingList.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy bản ghi nào cho daily summary: " + dailySummaryId);
        }
        return cravingTrackingList.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}
