package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DailySummaryDeletedException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DailySummaryEditForbiddenException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CravingTrackingRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.QuitPlanCalculator;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

@AllArgsConstructor
@Service
public class DailySummaryService {
    private static final Logger log = LoggerFactory.getLogger(DailySummaryService.class);
    private final DailySummaryRepository dailySummaryRepository;
    private final CravingTrackingRepository cravingTrackingRepository;
    private final ModelMapper modelMapper;
    private final QuitPlanCalculator quitPlanCalculator;
    private final QuitPlanService quitPlanService;

    private static final int RELAPSE_CONSECUTIVE_DAYS_THRESHOLD = 3;
    private static final double RELAPSE_PERCENTAGE_OVER_TARGET_THRESHOLD = 0.20; // 20%
    private static final LocalDate MAX_GOAL_DATE = LocalDate.of(2999, 12, 31);


    public DailySummaryResponse convertToResponseDto(DailySummary dailySummary) {
        return modelMapper.map(dailySummary, DailySummaryResponse.class);
    }

    //Phương thức để auto một bản ghi DailySummary khi một CravingTracking được tạo
    @Transactional
    private DailySummary createDailySummary(QuitPlan quitPlan, LocalDate trackDate) {
        // Kiểm tra xem đã có DailySummary cho ngày này và quit plan này chưa
        Optional<DailySummary> existingSummary = dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate);
        if (existingSummary.isPresent()) {
            throw new IllegalArgumentException
                    ("Bản ghi nhận hằng ngày cho kế hoạch với ID " + quitPlan.getQuitPlanId() + " vào ngày " + trackDate + " đã tồn tại");
        }

        //tự động tạo nên các giá trị đều set về mặc định
        DailySummary dailySummary = new DailySummary();
        dailySummary.setQuitPlan(quitPlan);
        dailySummary.setTrackDate(trackDate);
        dailySummary.setTotalSmokedCount(0);
        dailySummary.setTotalCravingCount(0);
        dailySummary.setManualSmokedCount(0);
        dailySummary.setManualCravingCount(0);
        dailySummary.setTrackedSmokedCount(0);
        dailySummary.setTrackedCravingCount(0);
        dailySummary.setMoneySaved(null);
        dailySummary.setCreatedAt(LocalDateTime.now());
        dailySummary.setUpdatedAt(null);
        dailySummary.setGoalAchievedToday(false);

        return dailySummaryRepository.save(dailySummary);
    }

    //Tìm hoặc tạo một DailySummary cho một QuitPlan và ngày cụ thể
    //để đảm bảo DailySummary tồn tại trước khi tạo CravingTracking
    @Transactional
    public DailySummary findOrCreateDailySummary(UUID memberId, LocalDate trackDate) {
        //Tìm quit plan của người dùng đang tracking
        Optional<QuitPlan> quitPlanOptional = quitPlanService.getProgressQuitPlansByMemberId(memberId);
        if (quitPlanOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch của người dùng với ID: " + memberId);
        }
        return dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlanOptional.get(), trackDate)
                .orElseGet(() -> createDailySummary(quitPlanOptional.get(), trackDate));
    }

    //Tạo một bản ghi DailySummary thủ công bởi thành viên
    //totalSmokedCount/totalCravingCount là giá trị người dùng nhập
    @Transactional
    public DailySummaryResponse createManualDailySummary(UUID memberId, DailySummaryCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu tạo nhật ký không được để trống");
        }

        // Kiểm tra để tránh tạo DailySummary rỗng, không liên quan đến việc cai thuốc
        if (request.getTotalSmokedCount() == null && request.getTotalCravingCount() == null) {
            throw new IllegalArgumentException
                    ("Request phải chứa ít nhất một trong hai trường cravingsCount hoặc smokedCount");
        }
        if (request.getTotalSmokedCount() == 0 && request.getTotalCravingCount() == 0) {
            throw new IllegalArgumentException("Cả totalSmokedCount và totalCravingCount đều là 0. " +
                    "Ít nhất một trong hai phải khác 0 để tạo nhật ký hàng ngày");
        }

        Optional<QuitPlan> quitPlanOptional = quitPlanService.getProgressQuitPlansByMemberId(memberId);
        if (quitPlanOptional.isEmpty()) {
            throw new ResourceNotFoundException
                    ("Không tìm thấy kế hoạch cai thuốc đang tiến hành cho thành viên: " + memberId);
        }
        QuitPlan quitPlan = quitPlanOptional.get();

        //Kiểm tra xem ngày theo dõi có hợp lệ không
        //Chỉ cho phép ghi nhận DailySummary cho ngày hiện tại
        LocalDate trackDate = request.getTrackDate() != null ? request.getTrackDate() : LocalDate.now();
        if (!trackDate.isEqual(LocalDate.now())) {
            throw new IllegalArgumentException("Chỉ có thể tạo nhật ký cho ngày hiện tại");
        }

        // Kiểm tra xem ngày theo dõi có hợp lệ không
        //không cho phép ghi nhật ký cho ngày trước khi bắt đầu kế hoạch
        if (trackDate.isBefore(quitPlan.getStartDate().toLocalDate())) {
            throw new IllegalArgumentException("Không thể ghi nhật ký cho ngày trước khi bắt đầu kế hoạch");
        }

        if (isTrackDateExistsForMember(memberId, trackDate)) {
            throw new IllegalArgumentException("Nhật ký cho ngày " + trackDate + " đã tồn tại, " +
                    "không thể tạo bản ghi cùng ngày");
        }

        if (isTrackDateExistsForMember(memberId, trackDate)) {
            throw new IllegalArgumentException("Nhật ký cho ngày " + trackDate + " đã tồn tại, " +
                    "không thể tạo bản ghi cùng ngày");
        }

        DailySummary newDailySummary = new DailySummary();
        newDailySummary.setQuitPlan(quitPlan);
        newDailySummary.setTrackDate(trackDate);

        //Lưu giá trị thủ công vào manualSmokedCount/manualCravingCount
        newDailySummary.setManualSmokedCount(request.getTotalSmokedCount() != null ? request.getTotalSmokedCount() : 0);
        newDailySummary.setManualCravingCount(request.getTotalCravingCount() != null ? request.getTotalCravingCount() : 0);
        newDailySummary.setTrackedSmokedCount(0);
        newDailySummary.setTrackedCravingCount(0);
        // totalCount ban đầu sẽ bằng manualCount (trackedCount ban đầu là 0)
        newDailySummary.setTotalSmokedCount(newDailySummary.getManualSmokedCount());
        newDailySummary.setTotalCravingCount(newDailySummary.getManualCravingCount());

        newDailySummary.setMood(request.getMood());
        newDailySummary.setNote(request.getNote());
        newDailySummary.setGoalAchievedToday(false);
        newDailySummary.setCreatedAt(LocalDateTime.now());
        newDailySummary.setUpdatedAt(null); // Chưa cập nhật, sẽ được cập nhật khi lưu

        // Tiền tiết kiệm được tính toán dựa trên tổng số điếu hút đã cung cấp
        newDailySummary.setMoneySaved(caculateMoneySaved(quitPlan, newDailySummary.getTotalSmokedCount()));

        DailySummary savedDailySummary = dailySummaryRepository.save(newDailySummary);

        // Tái tính toán tổng DailySummary để đảm bảo nó phản ánh cả manual và craving tracking
        recalculateDailyTotals(savedDailySummary);

        quitPlanService.ensureQuitPlanStatusIsCurrent(quitPlan); // Cập nhật trạng thái QuitPlan
        quitPlanService.handleDailySummaryUpdateForRelapse(savedDailySummary); // Kiểm tra và xử lý tái nghiện

        return convertToResponseDto(savedDailySummary);
    }

    @Transactional(readOnly = true)
    public DailySummary getDailySummaryById(Integer id) {
        return dailySummaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi nhật ký hàng ngày với ID: " + id));
    }

    @Transactional(readOnly = true)
    public UUID getMemberIdByDailySummaryId(Integer dailySummaryId) {
        DailySummary dailySummary = getDailySummaryById(dailySummaryId);
        if (dailySummary.getQuitPlan() == null) {
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc lá liên kết với bản ghi hằng ngày này");
        }
        if (dailySummary.getQuitPlan().getMember() == null) {
            throw new ResourceNotFoundException("Không tìm thấy thành viên liên kết với bản ghi hằng ngày này");
        }
        if (dailySummary.getQuitPlan().getMember().getMemberId() == null) {
            throw new ResourceNotFoundException("Không tìm thấy ID thành viên cho bản ghi này");
        }
        return dailySummary.getQuitPlan().getMember().getMemberId();
    }

    //Lấy DailySummary cho một thành viên và ngày cụ thể
    @Transactional(readOnly = true)
    public DailySummaryResponse getDailySummaryByMemberIdAndDate(UUID memberId, LocalDate trackDate) {
        Optional<QuitPlan> quitPlanOptional = quitPlanService.getProgressQuitPlansByMemberId(memberId);
        if (quitPlanOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch cai thuốc đang tiến hành cho thành viên: " + memberId);
        }
        QuitPlan quitPlan = quitPlanOptional.get();

        DailySummary dailySummary = dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Không tìm thấy nhật ký hàng ngày cho thành viên " + memberId + " vào ngày " + trackDate));

        return convertToResponseDto(dailySummary);
    }

    public boolean isTrackDateExistsForMember(UUID memberId, LocalDate trackDate) {
        Optional<QuitPlan> quitPlanOptional = quitPlanService.getProgressQuitPlansByMemberId(memberId);
        if (quitPlanOptional.isEmpty()) {
            return false; // Không có kế hoạch nào cho thành viên này
        }
        QuitPlan quitPlan = quitPlanOptional.get();
        return dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate).isPresent();
    }

    @Transactional(readOnly = true)
    public List<DailySummaryResponse> getDailySummariesByDateBetween(UUID memberId, LocalDate startDate, LocalDate endDate) {
        List<DailySummary> dailySummaryList = dailySummaryRepository.findByQuitPlan_Member_MemberIdAndTrackDateBetween(
                memberId, startDate, endDate
        );

        // Thay vì ném ResourceNotFoundException nếu danh sách rỗng
        // trả về danh sách rỗng để DataVisualizationService có thể xử lý điền giá trị 0
        if(dailySummaryList.isEmpty()) {
            return List.of(); // Trả về danh sách rỗng immutable
        }

        return dailySummaryList.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public DailySummaryResponse getDailySummaryResponseById(Integer id) {
//        Optional<DailySummary> dailySummaryOptional = dailySummaryRepository.findById(id);
//        if (dailySummaryOptional.isEmpty()) {
//            throw new ResourceNotFoundException("Không tìm thấy bản ghi nhật ký hàng ngày với ID: " + id);
//        }
//        return convertToResponseDto(dailySummaryOptional.get());
//    }

//    @Transactional(readOnly = true)
//    public Optional<DailySummary> getDailySummaryByQuitPlanAndDate(Integer quitPlanId, LocalDate trackDate) {
//        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch với ID: " + quitPlanId));
//        return dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate);
//    }

//    @Transactional(readOnly = true)
//    public List<DailySummary> getDailySummariesByQuitPlanId(Integer quitPlanId) {
//        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
//                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan not found with ID: " + quitPlanId));
//        return new ArrayList<>(dailySummaryRepository.findByQuitPlan(quitPlan));
//    }

    //Cho phép người dùng cập nhật totalSmokedCount, totalCravingCount, mood, và note
    //Nếu có thay đổi về totalSmokedCount, sẽ tự động tính toán lại moneySaved
    @Transactional(noRollbackFor = DailySummaryDeletedException.class)
    public DailySummaryResponse updateDailySummary(Integer dailySummaryId, DailySummaryUpdateRequest request) {
        Optional<DailySummary> existingSummaryOptional = dailySummaryRepository.findById(dailySummaryId);
        if (existingSummaryOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy nhật ký với ID: " + dailySummaryId);
        }
        DailySummary existingSummary = existingSummaryOptional.get();

        //Chỉ cho phép cập nhật bản ghi DailySummary cho ngày hiện tại
        LocalDate today = LocalDate.now();
        if (!existingSummary.getTrackDate().isEqual(today)) {
            throw new DailySummaryEditForbiddenException("Chỉ có thể cập nhật nhật ký cho ngày hiện tại");
        }

        // Tính tổng từ CravingTracking liên kết để so sánh
        List<CravingTracking> associatedCravingTrackings = cravingTrackingRepository.
                findByDailySummary_DailySummaryId(existingSummary.getDailySummaryId());
        int sumSmokedFromCraving = associatedCravingTrackings.stream().mapToInt(CravingTracking::getSmokedCount).sum();
        int sumCravingsFromCraving = associatedCravingTrackings.stream().mapToInt(CravingTracking::getCravingsCount).sum();

        boolean changed = false;

        // Cập nhật totalSmokedCount và manualSmokedCount dựa trên request
        if (request.getUpdateSmokedCount() != null) {
            // Giá trị cập nhật cho totalSmokedCount không thể nhỏ hơn tổng số được ghi nhận từ CravingTracking
            if (request.getUpdateSmokedCount() < sumSmokedFromCraving) {
                throw new IllegalArgumentException
                        ("Số lượng thuốc cập nhật (" + request.getUpdateSmokedCount() + ") không thể nhỏ hơn tổng số được ghi nhận " +
                                "từ các lần theo dõi chi tiết (" + sumSmokedFromCraving + ")");
            }
            if (existingSummary.getTotalSmokedCount() != request.getUpdateSmokedCount()) {
                existingSummary.setTotalSmokedCount(request.getUpdateSmokedCount());
                // manualSmokedCount = totalSmokedCount - trackedSmokedCount
                // Đảm bảo manualSmokedCount không âm
                existingSummary.setManualSmokedCount(Math.max(0, request.getUpdateSmokedCount() - sumSmokedFromCraving));
                changed = true;
            }
        } else {
            // Nếu không có updateSmokedCount, totalSmokedCount sẽ dựa trên manualSmokedCount hiện tại và trackedSmokedCount
            int newTotalSmoked = existingSummary.getManualSmokedCount() + sumSmokedFromCraving;
            if (existingSummary.getTotalSmokedCount() != newTotalSmoked) {
                existingSummary.setTotalSmokedCount(newTotalSmoked);
                changed = true;
            }
        }

        // Cập nhật totalCravingCount và manualCravingCount dựa trên request
        if (request.getUpdateCravingCount() != null) {
            // Giá trị cập nhật cho totalCravingCount không thể nhỏ hơn tổng số được ghi nhận từ CravingTracking
            if (request.getUpdateCravingCount() < sumCravingsFromCraving) {
                throw new IllegalArgumentException
                        ("Số lần thèm thuốc cập nhật (" + request.getUpdateCravingCount() + ") không thể nhỏ hơn tổng số được ghi nhận " +
                                "từ các lần theo dõi chi tiết (" + sumCravingsFromCraving + "). Vui lòng cập nhật giá trị lớn hơn hoặc bằng.");
            }
            if (existingSummary.getTotalCravingCount() != request.getUpdateCravingCount()) {
                existingSummary.setTotalCravingCount(request.getUpdateCravingCount());
                // manualCravingCount = totalCravingCount - trackedCravingCount
                // Đảm bảo manualCravingCount không âm
                existingSummary.setManualCravingCount(Math.max(0, request.getUpdateCravingCount() - sumCravingsFromCraving));
                changed = true;
            }
        } else {
            // Nếu không có updateCravingCount, totalCravingCount sẽ dựa trên manualCravingCount hiện tại và trackedCravingCount
            int newTotalCravings = existingSummary.getManualCravingCount() + sumCravingsFromCraving;
            if (existingSummary.getTotalCravingCount() != newTotalCravings) {
                existingSummary.setTotalCravingCount(newTotalCravings);
                changed = true;
            }
        }

        // Cập nhật trackedCount
        // Các giá trị này được lấy trực tiếp từ tổng CravingTracking
        if (existingSummary.getTrackedSmokedCount() != sumSmokedFromCraving) {
            existingSummary.setTrackedSmokedCount(sumSmokedFromCraving);
            changed = true;
        }
        if (existingSummary.getTrackedCravingCount() != sumCravingsFromCraving) {
            existingSummary.setTrackedCravingCount(sumCravingsFromCraving);
            changed = true;
        }

        // Sử dụng Objects.equals để xử lý trường hợp một trong hai hoặc cả hai là null
        if (request.getMood() != null && !Objects.equals(existingSummary.getMood(), request.getMood())) {
            existingSummary.setMood(request.getMood());
            changed = true;
        }
        if (request.getNote() != null && !Objects.equals(existingSummary.getNote(), request.getNote())) {
            existingSummary.setNote(request.getNote());
            changed = true;
        }

        QuitPlan quitPlan = existingSummary.getQuitPlan();
        if (quitPlan == null) {
            throw new IllegalArgumentException
                    ("DailySummary không có QuitPlan liên kết. Không thể tính toán tiền đã tiết kiệm");
        }

        //tính toán tiền đã tiết kiệm được dựa trên TotalSmokedCount
        BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, existingSummary.getTotalSmokedCount());
        if (existingSummary.getMoneySaved() == null || newMoneySaved == null ||
                existingSummary.getMoneySaved().compareTo(newMoneySaved) != 0) {
            existingSummary.setMoneySaved(newMoneySaved);
            changed = true;
        }

        //Kiểm tra và xóa DailySummary nếu nó trở nên rỗng sau khi cập nhật
        boolean hasMeaningfulData = existingSummary.getTotalSmokedCount() > 0
                || existingSummary.getTotalCravingCount() > 0
                || !associatedCravingTrackings.isEmpty();

        if (!hasMeaningfulData) {
            dailySummaryRepository.delete(existingSummary);
            log.warn("Nhật ký ID {} đã được xóa do trở nên rỗng sau khi cập nhật (không còn dữ liệu theo dõi)",
                    existingSummary.getDailySummaryId());

            quitPlanService.ensureQuitPlanStatusIsCurrent(quitPlan);
            quitPlanService.handleDailySummaryUpdateForRelapse(existingSummary);

            throw new DailySummaryDeletedException
                    ("Nhật ký ID " + dailySummaryId + " đã bị xóa do trở nên rỗng (không còn dữ liệu theo dõi)");
        }

        if (changed) {
            existingSummary.setUpdatedAt(LocalDateTime.now()); // Cập nhật thời gian sửa đổi
            DailySummary savedDailySummary = dailySummaryRepository.save(existingSummary);

            quitPlanService.ensureQuitPlanStatusIsCurrent(quitPlan);
            quitPlanService.handleDailySummaryUpdateForRelapse(savedDailySummary);

            return convertToResponseDto(savedDailySummary);
        } else {
            throw new IllegalArgumentException
                    ("Không có thay đổi nào để cập nhật cho nhật ký với ID: " + dailySummaryId);
        }
    }

    @Transactional
    public void deleteDailySummary(Integer id) {
        DailySummary dailySummary = dailySummaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhật ký với ID: " + id));

        QuitPlan quitPlan = dailySummary.getQuitPlan();
        dailySummaryRepository.deleteById(id);

        // Kiểm tra status/tái nghiện sau khi DailySummary bị xóa
        if (quitPlan != null) {
            quitPlanService.ensureQuitPlanStatusIsCurrent(quitPlan);
            quitPlanService.handleDailySummaryUpdateForRelapse(dailySummary);
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal caculateMoneySaved(QuitPlan quitPlan, int totalSmoked) {
        BigDecimal initialSmokingAmount = BigDecimal.valueOf(quitPlan.getInitialSmokingAmount());
        BigDecimal cigarettesPerPack = BigDecimal.valueOf(quitPlan.getCigarettesPerPack());
        BigDecimal pricePerPack = quitPlan.getPricePerPack();

        // Tránh chia cho 0
        if (cigarettesPerPack.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("CigarettesPerPack = 0. Không thể tính toán chi phí");
        } else {
            // Chi phí ước tính ban đầu cho số điếu hút hàng ngày: (InitialSmokingAmount / CigarettesPerPack) * PricePerPack
            BigDecimal initialDailyCost = initialSmokingAmount
                    .divide(cigarettesPerPack, 2, RoundingMode.HALF_UP)
                    .multiply(pricePerPack);

            // Chi phí thực tế dựa trên số điếu đã hút hôm nay: (TotalSmokedCount / CigarettesPerPack) * PricePerPack
            BigDecimal todayCost = BigDecimal.valueOf(totalSmoked)
                    .divide(cigarettesPerPack, 2, RoundingMode.HALF_UP)
                    .multiply(pricePerPack);

            return initialDailyCost.subtract(todayCost);
        }
    }

    //Tái tính toán và cập nhật tổng số điếu hút và số lần thèm thuốc
    //để đồng bộ với các bản ghi CravingTracking liên quan
    @Transactional
    public void recalculateDailyTotals(DailySummary dailySummary) {
        if (dailySummary == null || dailySummary.getDailySummaryId() == null) {
            log.error("DailySummary không hợp lệ (null) hoặc không có ID");
            throw new IllegalArgumentException("DailySummary không hợp lệ hoặc không có ID");
        }

        // Lấy tất cả CravingTracking records liên quan cho DailySummary này
        List<CravingTracking> cravingTrackingList = cravingTrackingRepository.
                findByDailySummary_DailySummaryId(dailySummary.getDailySummaryId());

        int sumSmokedFromCraving = cravingTrackingList.stream().mapToInt(CravingTracking::getSmokedCount).sum();
        int sumCravingsFromCraving = cravingTrackingList.stream().mapToInt(CravingTracking::getCravingsCount).sum();

        //Chỉ cập nhật nếu có thay đổi
        boolean changed = false;

        // Cập nhật trackedCount
        // Các giá trị này được lấy trực tiếp từ tổng CravingTracking
        if (dailySummary.getTrackedSmokedCount() != sumSmokedFromCraving) {
            dailySummary.setTrackedSmokedCount(sumSmokedFromCraving);
            changed = true;
        }
        if (dailySummary.getTrackedCravingCount() != sumCravingsFromCraving) {
            dailySummary.setTrackedCravingCount(sumCravingsFromCraving);
            changed = true;
        }

        // Cập nhật totalCount
        // totalCount = manualCount + trackedCount
        int newTotalSmoked = dailySummary.getManualSmokedCount() + dailySummary.getTrackedSmokedCount();
        int newTotalCravings = dailySummary.getManualCravingCount() + dailySummary.getTrackedCravingCount();

        if (dailySummary.getTotalSmokedCount() != newTotalSmoked) {
            dailySummary.setTotalSmokedCount(newTotalSmoked);
            changed = true;
            log.info("DailySummary ID {}: TotalSmokedCount được cập nhật lên {} (Manual: {}, Tracked: {})",
                    dailySummary.getDailySummaryId(), newTotalSmoked,
                    dailySummary.getManualSmokedCount(), dailySummary.getTrackedSmokedCount());
        }

        if (dailySummary.getTotalCravingCount() != newTotalCravings) {
            dailySummary.setTotalCravingCount(newTotalCravings);
            changed = true;
            log.info("DailySummary ID {}: TotalCravingCount được cập nhật lên {} (Manual: {}, Tracked: {})",
                    dailySummary.getDailySummaryId(), newTotalCravings,
                    dailySummary.getManualCravingCount(), dailySummary.getTrackedCravingCount());
        }

        QuitPlan quitPlan = dailySummary.getQuitPlan();
        if (quitPlan == null) {
            log.error("DailySummary ID {}: Không có QuitPlan liên kết. Không thể tính toán tiền đã tiết kiệm",
                    dailySummary.getDailySummaryId());
            throw new IllegalArgumentException
                    ("DailySummary không có QuitPlan liên kết. Không thể tính toán tiền đã tiết kiệm");
        } else {
            quitPlanService.ensureQuitPlanStatusIsCurrent(quitPlan);

            BigDecimal oldMoneySaved = dailySummary.getMoneySaved();
            BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, dailySummary.getTotalSmokedCount());
            if (oldMoneySaved == null || newMoneySaved == null || oldMoneySaved.compareTo(newMoneySaved) != 0) {
                dailySummary.setMoneySaved(newMoneySaved);
                changed = true;
            }
        }

        //xóa DailySummary nếu nó trở nên rỗng sau khi đồng bộ
        //Một DailySummary là rỗng nếu không có bất kỳ CravingTracking nào liên kết với, và các total = 0
        boolean hasMeaningfulData = dailySummary.getTotalSmokedCount() > 0 ||
                dailySummary.getTotalCravingCount() > 0 || !cravingTrackingList.isEmpty();

        if (!hasMeaningfulData) {
            // Kiểm tra đảm bảo dailySummary đang muốn xóa là một bản ghi đang tồn tại trong DB
            if (dailySummaryRepository.existsById(dailySummary.getDailySummaryId())) {
                dailySummaryRepository.delete(dailySummary);
                quitPlanService.handleDailySummaryUpdateForRelapse(dailySummary);
                log.warn("DailySummaryID {} đã bị xóa (không còn dữ liệu liên quan)", dailySummary.getDailySummaryId());
                return; // Thoát khỏi phương thức sau khi xóa
            }
        }

        if (changed) {
            DailySummary savedDailySummary = dailySummaryRepository.save(dailySummary);
            quitPlanService.handleDailySummaryUpdateForRelapse(savedDailySummary);
        } else {
            log.info("DailySummary ID {}: Không có thay đổi tổng số liệu", dailySummary.getDailySummaryId());
        }
    }

    //Tái tính toán MoneySaved cho tất cả DailySummary
    //Phương thức được gọi khi các thông tin của QuitPlan thay đổi
    @Transactional
    public void recalculateMoneySavedForQuitPlan(QuitPlan quitPlan) {
        // Lấy tất cả DailySummary liên quan đến QuitPlan
        List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan(quitPlan);

        for (DailySummary dailySummary : dailySummaries) {
            // Đảm bảo DailySummary được cập nhật đầy đủ trước khi tính toán tiền tiết kiệm
            recalculateDailyTotals(dailySummary);

            BigDecimal oldMoneySaved = dailySummary.getMoneySaved();
            // Tính toán lại MoneySaved dựa trên QuitPlan đã được cập nhật và totalSmokedCount hiện tại của DailySummary
            BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, dailySummary.getTotalSmokedCount());

            // Cập nhật DailySummary nếu MoneySaved thay đổi
            if (oldMoneySaved == null || newMoneySaved == null || oldMoneySaved.compareTo(newMoneySaved) != 0) {
                dailySummary.setMoneySaved(newMoneySaved);
                dailySummaryRepository.save(dailySummary);
            }
        }
        quitPlanService.ensureQuitPlanStatusIsCurrent(quitPlan);
    }

    //Kiểm tra xem kế hoạch có được coi là COMPLETED hay không
    //COMPLETED: đạt >= 80% mục tiêu
    //Phương thức này không áp dụng cho type IMMEDIATE
    @Transactional(readOnly = true)
    public boolean isPlanCompletedForQuitPlan(QuitPlan plan) {
        //Nếu là type IMMEDIATE hoặc ngày kết thúc là MAX, không áp dụng
        if (plan.getReductionType() == ReductionQuitPlanType.IMMEDIATE || plan.getGoalDate().isEqual(MAX_GOAL_DATE)) {
            return false;
        }

        // Cộng 1 để bao gồm cả ngày bắt đầu và kết thúc
        long totalDaysInPlan = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), plan.getGoalDate()) + 1;
        if (totalDaysInPlan <= 0) return false; // Không hợp lệ nếu ngày bắt đầu >= ngày kết thúc

        // Lấy danh sách mục tiêu hút thuốc hằng ngày từ calculator
        List<QuitPlanCalculator.QuitPlanDay> targetCigarettesPerDay = quitPlanCalculator.generateQuitPlan(
                plan.getInitialSmokingAmount(),
                totalDaysInPlan,
                plan.getReductionType()
        );

        // Lấy tất cả các bản ghi DailySummary trong khoảng thời gian của kế hoạch
        List<DailySummary> records = dailySummaryRepository.findByQuitPlanAndTrackDateBetween(
                plan, plan.getStartDate().toLocalDate(), plan.getGoalDate()
        );
        if (records.isEmpty()) {
            log.warn("Không có bản ghi DailySummary nào cho kế hoạch với ID: {}", plan.getQuitPlanId());
            return false; // Không có bản ghi nào, coi như không hoàn thành
        }

        long daysAchievedGoal = 0; // Số ngày đã đạt được mục tiêu
        for (DailySummary record : records) {
            // Xacs định ngày trong bản ghi
            long dayIndex = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), record.getTrackDate());

            // Tìm mục tiêu số thuốc lá trong ngày tương ứng
            Optional<QuitPlanCalculator.QuitPlanDay> currentDayTarget = targetCigarettesPerDay
                    .stream()
                    .filter(day -> day.getDay() == dayIndex + 1) // dayIndex bắt đầu từ 0, nhưng ngày bắt đầu từ 1
                    .findFirst();

            if (currentDayTarget.isPresent()) {
                int allowedCigarettes = currentDayTarget.get().getCigarettes();
                // Kiểm tra xem số thuốc lá đã hút trong ngày có nhỏ hơn hoặc bằng mục tiêu không
                if (record.getTotalSmokedCount() <= allowedCigarettes) {
                    daysAchievedGoal++;
                }
            } else {
                // Nếu không tìm thấy mục tiêu cho ngày này, coi như không đạt
                log.error("DailySummary cho QuitPlanID {} có ngày theo dõi {} nằm ngoài phạm vi kế hoạch",
                        plan.getQuitPlanId(), record.getTrackDate());
            }
        }
        // Tỉ lệ hoàn thành cần đạt
        double successRateThreshold = 0.8; // 80%
        double actualSuccessRate = (double) daysAchievedGoal / totalDaysInPlan;

        return actualSuccessRate >= successRateThreshold;
    }

    ///Logic kiểm tra xem người dùng có tái nghiện hay không
    //Dựa trên số ngày liên tiếp vượt quá ngưỡng cho phép
    //Logic khác nhau tùy thuộc vào loại kế hoạch (IMMEDIATE hay giảm dần)
    @Transactional(readOnly = true)
    public boolean isUserRelapsedForQuitPlan(QuitPlan plan) {
        // Chỉ áp dụng với IN_PROGRESS
         if (plan.getStatus() != QuitPlanStatus.IN_PROGRESS) {
             return false;
         }

        // Logic tái nghiện cho kế hoạch IMMEDIATE (dừng hẳn)
        // Kiểm tra các bản ghi DailySummary *gần đây* nhất để phát hiện bất kỳ lần hút thuốc nào
        // Nếu có số điếu hút > 0 -> tái nghiện
        if (plan.getReductionType() == ReductionQuitPlanType.IMMEDIATE || plan.getGoalDate().isEqual(MAX_GOAL_DATE)) {
            List<DailySummary> recentRecords = dailySummaryRepository.findByQuitPlanOrderByTrackDateDesc(plan)
                    .stream()
                    .limit(RELAPSE_CONSECUTIVE_DAYS_THRESHOLD * 2) // Lấy nhiều hơn một chút để đảm bảo bao phủ
                    .filter(ds -> ds.getTrackDate().isAfter(plan.getStartDate().toLocalDate().minusDays(1))) // Chỉ các bản ghi sau khi kế hoạch bắt đầu
                    .toList();

            for (DailySummary record : recentRecords) {
                if (record.getTotalSmokedCount() > 0) {
                    log.info("Kế hoạch IMMEDIATE ID {} tái nghiện: Số điếu hút > 0 vào ngày {}",
                            plan.getQuitPlanId(), record.getTrackDate());
                    return true;
                }
            }
            return false; // Không có bản ghi, không tái nghiện
        } else {
            // Logic tái nghiện cho các kế hoạch giảm dần (LINEAR, EXPONENTIAL, LOGARITHMIC)
            // Cộng 1 để bao gồm cả ngày bắt đầu và kết thúc
            long totalDaysInPlan = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), plan.getGoalDate()) + 1;
            if (totalDaysInPlan <= 0) return false; // Không hợp lệ nếu ngày bắt đầu >= ngày kết thúc

            List<QuitPlanCalculator.QuitPlanDay> targetCigarettesPerDay = quitPlanCalculator.generateQuitPlan(
                    plan.getInitialSmokingAmount(),
                    totalDaysInPlan,
                    plan.getReductionType()
            );

            // Lấy tất cả các bản ghi DailySummary gần nhất theo số ngày ngưỡng tái nghiện
            List<DailySummary> recentRecords = dailySummaryRepository.findByQuitPlanOrderByTrackDateDesc(plan)
                    .stream()
                    .filter(record -> !record.getTrackDate().isAfter(LocalDate.now()))
                    .limit(RELAPSE_CONSECUTIVE_DAYS_THRESHOLD)
                    .toList();

            if (recentRecords.size() < RELAPSE_CONSECUTIVE_DAYS_THRESHOLD) {
                return false; // Không đủ dữ liệu để đánh giá
            }
            long consecutiveRelapseDaysCount = 0;
            for (DailySummary record : recentRecords) {
                long dayIndex = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), record.getTrackDate());

                // Đảm bảo dayIndex hợp lệ và nằm trong phạm vi kế hoạch
                if (dayIndex < 0 || dayIndex >= totalDaysInPlan) {
                    // Nếu bản ghi nằm ngoài ngày của kế hoạch, không xét vào tái nghiện
                    break;
                }

                Optional<QuitPlanCalculator.QuitPlanDay> currentDayTarget = targetCigarettesPerDay.stream()
                        .filter(qpDay -> qpDay.getDay() == (dayIndex + 1))
                        .findFirst();

                if (currentDayTarget.isPresent()) {
                    int allowedCigarettes = currentDayTarget.get().getCigarettes();
                    // Kiểm tra xem số điếu hút có vượt quá ngưỡng tái nghiện so với mục tiêu ngày đó không
                    // (hút > mục tiêu + (mục tiêu * RELAPSE_PERCENTAGE_OVER_TARGET_THRESHOLD))
                    double relapseLimit = allowedCigarettes * (1 + RELAPSE_PERCENTAGE_OVER_TARGET_THRESHOLD);

                    if (record.getTotalSmokedCount() > relapseLimit) {
                        consecutiveRelapseDaysCount++;
                    } else {
                        // Nếu có một ngày không vượt ngưỡng, chuỗi liên tiếp bị phá vỡ
                        break;
                    }
                } else {
                    // Nếu không tìm thấy mục tiêu cho ngày này (lỗi logic hoặc dữ liệu không khớp), coi như không tái nghiện
                    break;
                }
            }
            return consecutiveRelapseDaysCount >= RELAPSE_CONSECUTIVE_DAYS_THRESHOLD;
        }
    }

    //chạy định kỳ để cập nhật trạng thái hoàn thành mục tiêu (isGoalAchievedToday)
    //cho các bản ghi DailySummary của ngày hôm trước
    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày lúc 00:00
    @Transactional
    public void updateGoalAchievementStatusForPreviousDay() {
        //lấy dữ liệu của ngày đã qua
        LocalDate previousDay = LocalDate.now().minusDays(1);
        log.info("Bắt đầu cập nhật trạng thái hoàn thành mục tiêu cho ngày: {}", previousDay);

        // Lấy tất cả DailySummary cho ngày hôm trước
        List<DailySummary> dailySummaries = dailySummaryRepository.findAll()
                .stream()
                .filter(ds -> ds.getTrackDate().isEqual(previousDay))
                .toList();

        if (dailySummaries.isEmpty()) {
            log.info("Không tìm thấy bản ghi DailySummary nào cho ngày {}. " +
                    "Bỏ qua cập nhật trạng thái mục tiêu", previousDay);
            return;
        }

        for (DailySummary dailySummary : dailySummaries) {
            try {
                // Tái tính toán DailySummary để đảm bảo tổng số liệu là chính xác nhất
                recalculateDailyTotals(dailySummary);

                QuitPlan quitPlan = dailySummary.getQuitPlan();
                if (quitPlan == null) {
                    log.warn("DailySummary ID {} không có QuitPlan liên kết. Bỏ qua cập nhật isGoalAchievedToday",
                            dailySummary.getDailySummaryId());
                    continue;
                }

                // Nếu là IMMEDIATE hoặc mục tiêu là MAX_GOAL_DATE, mục tiêu là smokedCount = 0
                boolean isImmediateOrMaxGoal = (
                        quitPlan.getReductionType() == ReductionQuitPlanType.IMMEDIATE ||
                                quitPlan.getGoalDate().isEqual(MAX_GOAL_DATE)
                );

                int targetCigarettesForDay;
                if (isImmediateOrMaxGoal) {
                    targetCigarettesForDay = 0; // Mục tiêu là không hút điếu nào
                } else {
                    // Tính toán số ngày trong kế hoạch
                    long totalDaysInPlan = ChronoUnit.DAYS.between(quitPlan.getStartDate().toLocalDate(), quitPlan.getGoalDate()) + 1;
                    if (totalDaysInPlan <= 0) {
                        log.warn("QuitPlan ID {} có tổng số ngày không hợp lệ. Bỏ qua cập nhật isGoalAchievedToday.",
                                quitPlan.getQuitPlanId());
                        continue;
                    }

                    // Lấy danh sách mục tiêu hút thuốc hàng ngày từ calculator
                    List<QuitPlanCalculator.QuitPlanDay> planDays = quitPlanCalculator.generateQuitPlan(
                            quitPlan.getInitialSmokingAmount(),
                            totalDaysInPlan,
                            quitPlan.getReductionType()
                    );

                    // Xác định ngày trong kế hoạch (0-indexed based on start date)
                    long dayIndex = ChronoUnit.DAYS.between(quitPlan.getStartDate().toLocalDate(), dailySummary.getTrackDate());

                    Optional<QuitPlanCalculator.QuitPlanDay> currentDayTarget = planDays.stream()
                            .filter(qpDay -> qpDay.getDay() == (dayIndex + 1)) // qpDay.getDay() is 1-indexed
                            .findFirst();

                    if (currentDayTarget.isEmpty()) {
                        log.warn("Không tìm thấy mục tiêu cho DailySummary ID {} vào ngày {}. Bỏ qua cập nhật isGoalAchievedToday",
                                dailySummary.getDailySummaryId(), dailySummary.getTrackDate());
                        continue;
                    }
                    targetCigarettesForDay = currentDayTarget.get().getCigarettes();
                }

                boolean goalAchieved = dailySummary.getTotalSmokedCount() <= targetCigarettesForDay;

                if (dailySummary.isGoalAchievedToday() != goalAchieved) {
                    dailySummary.setGoalAchievedToday(goalAchieved);
                    dailySummary.setUpdatedAt(LocalDateTime.now());
                    dailySummaryRepository.save(dailySummary);
                    log.info("Cập nhật isGoalAchievedToday cho DailySummary ID {} (Ngày: {}): {}",
                            dailySummary.getDailySummaryId(), dailySummary.getTrackDate(), goalAchieved);
                } else {
                    log.info("isGoalAchievedToday cho DailySummary ID {} (Ngày: {}) không thay đổi",
                            dailySummary.getDailySummaryId(), dailySummary.getTrackDate());
                }

            } catch (Exception e) {
                log.error("Lỗi khi cập nhật isGoalAchievedToday cho DailySummary ID {}: {}",
                        dailySummary.getDailySummaryId(), e.getMessage(), e);
            }
        }
        log.info("Hoàn tất cập nhật trạng thái hoàn thành mục tiêu cho ngày: {}", previousDay);
    }
}
