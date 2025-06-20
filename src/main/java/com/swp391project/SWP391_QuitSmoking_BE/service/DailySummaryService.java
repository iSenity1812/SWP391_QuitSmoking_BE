package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DailySummaryEditForbiddenException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CravingTrackingRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DailySummaryService {
    private final DailySummaryRepository dailySummaryRepository;
    private final CravingTrackingRepository cravingTrackingRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final QuitPlanService quitPlanService;
    private final ModelMapper modelMapper;

    public DailySummaryResponse convertToResponseDto(DailySummary dailySummary) {
        return modelMapper.map(dailySummary, DailySummaryResponse.class);
    }

    //Phương thức để auto một bản ghi DailySummary khi một CravingTracking được tạo
    @Transactional
    private DailySummary createDailySummary(QuitPlan quitPlan, LocalDate trackDate) {
        // Kiểm tra xem đã có DailySummary cho ngày này và quit plan này chưa
        Optional<DailySummary> existingSummary = getDailySummaryByQuitPlanAndDate(quitPlan.getQuitPlanId(), trackDate);
        if (existingSummary.isPresent()) {
            throw new IllegalArgumentException("Bản ghi nhận hằng ngày cho kế hoạch với ID " + quitPlan.getQuitPlanId() + " vào ngày " + trackDate + " đã tồn tại");
        }

        DailySummary dailySummary = new DailySummary();
        dailySummary.setQuitPlan(quitPlan);
        dailySummary.setTrackDate(trackDate);
        dailySummary.setTotalSmokedCount(0);
        dailySummary.setTotalCravingCount(0);
        dailySummary.setMoneySaved(BigDecimal.valueOf(0.0));
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
    @Transactional
    public DailySummaryResponse createManualDailySummary(UUID memberId, DailySummaryCreateRequest request) {
        Optional<QuitPlan> quitPlanOptional = quitPlanService.getProgressQuitPlansByMemberId(memberId);
        if (quitPlanOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch cai thuốc đang tiến hành cho thành viên: " + memberId);
        }
        QuitPlan quitPlan = quitPlanOptional.get();

        Optional<DailySummary> existingSummary = getDailySummaryByQuitPlanAndDate(quitPlan.getQuitPlanId(), request.getTrackDate());
        if (existingSummary.isPresent()) {
            throw new IllegalArgumentException("Bản ghi nhật ký hàng ngày cho ngày " + request.getTrackDate() + " đã tồn tại");
        }

        // Kiểm tra để tránh tạo DailySummary rỗng, không liên quan đến việc cai thuốc
        if ((request.getTotalSmokedCount() == null || request.getTotalSmokedCount() == 0) &&
                (request.getTotalCravingCount() == null || request.getTotalCravingCount() == 0)) {
            throw new IllegalArgumentException("Không thể tạo bản ghi nhật ký hàng ngày rỗng. Vui lòng nhập dữ liệu theo dõi");
        }

        DailySummary newDailySummary = new DailySummary();
        newDailySummary.setQuitPlan(quitPlan);
        newDailySummary.setTrackDate(request.getTrackDate());
        newDailySummary.setTotalSmokedCount(request.getTotalSmokedCount() != null ? request.getTotalSmokedCount() : 0);
        newDailySummary.setTotalCravingCount(request.getTotalCravingCount() != null ? request.getTotalCravingCount() : 0);
        newDailySummary.setMood(request.getMood());
        newDailySummary.setNote(request.getNote());

        // Tiền tiết kiệm được tính toán dựa trên tổng số điếu hút đã cung cấp
        newDailySummary.setMoneySaved(caculateMoneySaved(quitPlan, newDailySummary.getTotalSmokedCount()));
        newDailySummary.setGoalAchievedToday(false);

        DailySummary savedDailySummary = dailySummaryRepository.save(newDailySummary);
        return convertToResponseDto(savedDailySummary);
    }

    @Transactional
    public DailySummaryResponse getDailySummaryResponseById(Integer id) {
        Optional<DailySummary> dailySummaryOptional = dailySummaryRepository.findById(id);
        if (dailySummaryOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy bản ghi nhật ký hàng ngày với ID: " + id);
        }
        return convertToResponseDto(dailySummaryOptional.get());
    }

    @Transactional
    public DailySummary getDailySummaryById(Integer id) {
        return dailySummaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi nhật ký hàng ngày với ID: " + id));
    }

    @Transactional
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

    @Transactional
    public Optional<DailySummary> getDailySummaryByQuitPlanAndDate(Integer quitPlanId, LocalDate trackDate) {
        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch với ID: " + quitPlanId));
        return dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate);
    }

    //Lấy DailySummary cho một thành viên và ngày cụ thể
    @Transactional
    public DailySummaryResponse getDailySummaryByMemberIdAndDate(UUID memberId, LocalDate trackDate) {
        Optional<QuitPlan> quitPlanOptional = quitPlanService.getProgressQuitPlansByMemberId(memberId);
        if (quitPlanOptional.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch cai thuốc đang tiến hành cho thành viên: " + memberId);
        }
        QuitPlan quitPlan = quitPlanOptional.get();

        DailySummary dailySummary = dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhật ký hàng ngày cho thành viên " + memberId + " vào ngày " + trackDate));

        return convertToResponseDto(dailySummary);
    }

    //Cho phép người dùng cập nhật totalSmokedCount, totalCravingCount, mood, và note
    //Nếu có thay đổi về totalSmokedCount, sẽ tự động tính toán lại moneySaved
    @Transactional
    public DailySummaryResponse updateDailySummary(DailySummaryUpdateRequest request) {
        Optional<DailySummary> existingSummaryOptional = dailySummaryRepository.findById(request.getDailySummaryId());
        if (existingSummaryOptional.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy nhật ký với ID: " + request.getDailySummaryId());
        }
        DailySummary existingSummary = existingSummaryOptional.get();

        //Nếu TrackDate đã vượt qua ngày hiện tại, không cho phép chỉnh sửa
        LocalDate today = LocalDate.now();
        if (existingSummary.getTrackDate().isBefore(today)) {
            throw new DailySummaryEditForbiddenException("Không thể chỉnh sửa nhật ký cho ngày đã qua");
        }

        boolean changed = false;
        if (request.getTotalSmokedCount() != null && existingSummary.getTotalSmokedCount() != request.getTotalSmokedCount()) {
            existingSummary.setTotalSmokedCount(request.getTotalSmokedCount());

            //Chỉ cập nhật moneySaved nếu có thay đổi
            QuitPlan quitPlan = existingSummary.getQuitPlan();
            if (quitPlan == null) {
                throw new IllegalArgumentException("DailySummary không có QuitPlan liên kết. Không thể tính toán tiền đã tiết kiệm");
            }

            BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, existingSummary.getTotalSmokedCount());
            if (existingSummary.getMoneySaved() == null || newMoneySaved == null || existingSummary.getMoneySaved().compareTo(newMoneySaved) != 0) {
                existingSummary.setMoneySaved(newMoneySaved);
            }
            changed = true;
        }
        if (request.getTotalCravingCount() != null && existingSummary.getTotalCravingCount() != request.getTotalCravingCount()) {
            existingSummary.setTotalCravingCount(request.getTotalCravingCount());
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

        //Kiểm tra và xóa DailySummary nếu nó trở nên rỗng sau khi cập nhật thủ công
        List<CravingTracking> cravingTrackingList = cravingTrackingRepository.findByDailySummary_DailySummaryId(existingSummary.getDailySummaryId());

        boolean hasAssociatedCravingTrackingList = !cravingTrackingList.isEmpty();
        boolean isDailySummaryTrulyEmpty = existingSummary.getTotalSmokedCount() == 0 && existingSummary.getTotalCravingCount() == 0;

        if (isDailySummaryTrulyEmpty && !hasAssociatedCravingTrackingList) {
            dailySummaryRepository.delete(existingSummary);
            throw new ResourceNotFoundException("Nhật ký hàng ngày đã được xóa do trở nên rỗng sau khi cập nhật (không còn dữ liệu theo dõi)");
        }

        if (changed) {
            DailySummary savedDailySummary = dailySummaryRepository.save(existingSummary);
            return convertToResponseDto(savedDailySummary);
        } else {
            throw new IllegalArgumentException("Không có thay đổi nào để cập nhật cho nhật ký với ID: " + request.getDailySummaryId());
        }

        //chưa update is Goal Achieved
    }

    @Transactional
    public List<DailySummary> getDailySummariesByQuitPlanId(Integer quitPlanId) {
        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan not found with ID: " + quitPlanId));
        return new ArrayList<>(dailySummaryRepository.findByQuitPlan(quitPlan));
    }

    @Transactional
    public void deleteDailySummary(Integer id) {
        if (!dailySummaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("DailySummary not found with ID: " + id);
        }
        dailySummaryRepository.deleteById(id);
    }

    @Transactional
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
        List<CravingTracking> cravingTrackingList = cravingTrackingRepository.findByDailySummary_DailySummaryId(dailySummary.getDailySummaryId());
//        if (cravingTrackingList.isEmpty()) {
//            throw new ResourceNotFoundException("Không tìm thấy bản ghi nào cho daily summary: " + dailySummary.getDailySummaryId());
//        }

        // Lấy tất cả CravingTracking records cho DailySummary này
        int totalSmoked = cravingTrackingList.stream().mapToInt(CravingTracking::getSmokedCount).sum();
        int totalCravings = cravingTrackingList.stream().mapToInt(CravingTracking::getCravingsCount).sum();

        QuitPlan quitPlan = dailySummary.getQuitPlan();
        if (quitPlan == null) {
            throw new IllegalArgumentException("DailySummary không có QuitPlan liên kết. Không thể tính toán tiền đã tiết kiệm");
        }

        BigDecimal oldMoneySaved = dailySummary.getMoneySaved();
        BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, totalSmoked);

        //Chỉ cập nhật nếu có thay đổi
        boolean changed = false;
        if (dailySummary.getTotalSmokedCount() != totalSmoked) {
            dailySummary.setTotalSmokedCount(totalSmoked);
            changed = true;
        }
        if (dailySummary.getTotalCravingCount() != totalCravings) {
            dailySummary.setTotalCravingCount(totalCravings);
            changed = true;
        }
        if (oldMoneySaved == null || newMoneySaved == null || oldMoneySaved.compareTo(newMoneySaved) != 0) {
            dailySummary.setMoneySaved(newMoneySaved);
            changed = true;
        }

        //xóa DailySummary nếu nó trở nên rỗng sau khi đồng bộ với CravingTracking
        //Một DailySummary là rỗng nếu không có bất kỳ CravingTracking nào liên kết với, và các total = 0
        boolean isDailySummaryTrulyEmpty = dailySummary.getTotalSmokedCount() == 0 && dailySummary.getTotalCravingCount() == 0;
        if (isDailySummaryTrulyEmpty) {
            //kiểm tra đảm bảo dailySummary đang muốn xóa là một bản ghi đang tồn tại trong DB
            if (dailySummary.getDailySummaryId() != null && dailySummaryRepository.existsById(dailySummary.getDailySummaryId())) {
                dailySummaryRepository.delete(dailySummary);
                System.out.println("DailySummary ID " + dailySummary.getDailySummaryId() + " đã bị xóa vì không có dữ liệu theo dõi liên quan");
                return; // Thoát khỏi phương thức vì bản ghi đã bị xóa
            }
        }

        if (changed) {
            dailySummaryRepository.save(dailySummary);
        } else {
            System.out.println("DailySummary ID " + dailySummary.getDailySummaryId() + ": Không có thay đổi tổng số liệu");
        }
    }

    //Tái tính toán MoneySaved cho tất cả DailySummary
    //Phương thức được gọi khi các thông tin của QuitPlan thay đổi
    @Transactional
    public void recalculateMoneySavedForQuitPlan(QuitPlan quitPlan) {
        // Lấy tất cả DailySummary liên quan đến QuitPlan
        List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan(quitPlan);

        for (DailySummary dailySummary : dailySummaries) {
            BigDecimal oldMoneySaved = dailySummary.getMoneySaved();
            // Tính toán lại MoneySaved dựa trên QuitPlan đã được cập nhật và totalSmokedCount hiện tại của DailySummary
            BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, dailySummary.getTotalSmokedCount());

            // Cập nhật DailySummary nếu MoneySaved thay đổi
            if (oldMoneySaved == null || newMoneySaved == null || oldMoneySaved.compareTo(newMoneySaved) != 0) {
                dailySummary.setMoneySaved(newMoneySaved);
                dailySummaryRepository.save(dailySummary);
            }
        }
    }

    @Transactional
    public List<DailySummaryResponse> getDailySummariesByDateBetween(UUID memberId, LocalDate startDate, LocalDate endDate) {
        List<DailySummary> dailySummaryListList = dailySummaryRepository.findByQuitPlan_Member_MemberIdAndTrackDateBetween(
                memberId, startDate, endDate
        );

        if(dailySummaryListList.isEmpty()) {
            // Thay vì ném ResourceNotFoundException nếu danh sách rỗng
            // trả về danh sách rỗng để DataVisualizationService có thể xử lý điền giá trị 0
            return List.of(); // Trả về danh sách rỗng immutable
        }

        return dailySummaryListList.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
}
