//package com.swp391project.SWP391_QuitSmoking_BE.service;
//
//import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryResponse;
//import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryUpdateRequest;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
//import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
//import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
//import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
//import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
//import lombok.AllArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.time.LocalDate;
//import java.util.*;
//
//class DailySummaryEditForbiddenException extends RuntimeException {
//    public DailySummaryEditForbiddenException(String message) {
//        super(message);
//    }
//}
//
//@AllArgsConstructor
//@Service
//public class DailySummaryService {
//    private final DailySummaryRepository dailySummaryRepository;
//    private final QuitPlanRepository quitPlanRepository;
//    private final QuitPlanService quitPlanService;
//    private final CravingTrackingService cravingTrackingService;
//    private final ModelMapper modelMapper;
//
//    private DailySummaryResponse convertToResponseDto(DailySummary dailySummary) {
//        DailySummaryResponse response = modelMapper.map(dailySummary, DailySummaryResponse.class);
//        if (dailySummary.getQuitPlan() != null) {
//            response.setQuitPlanId(dailySummary.getQuitPlan().getQuitPlanId());
//        }
//        return response;
//    }
//
//    @Transactional
//    public DailySummary createDailySummary(QuitPlan quitPlan, LocalDate trackDate) {
//        // Kiểm tra xem đã có DailySummary cho ngày này và quit plan này chưa
//        DailySummary existingSummary = getDailySummaryByQuitPlanAndDate(quitPlan.getQuitPlanId(), trackDate);
//        if (existingSummary != null) {
//            throw new IllegalArgumentException("Bản ghi nhận hằng ngày cho kế hoạch với ID " + quitPlan.getQuitPlanId() + " vào ngày " + trackDate + " đã tồn tại");
//        }
//
//        DailySummary dailySummary = new DailySummary();
//        dailySummary.setQuitPlan(quitPlan);
//        dailySummary.setTrackDate(trackDate);
//        dailySummary.setTotalSmokedCount(0);
//        dailySummary.setTotalCravingCount(0);
//        dailySummary.setMoneySaved(BigDecimal.valueOf(0.0));
//        dailySummary.setGoalAchievedToday(false);
//
//        return dailySummaryRepository.save(dailySummary);
//    }
//
//    //Tìm hoặc tạo một DailySummary cho một QuitPlan và ngày cụ thể
//    //dùng cho scheduler hoặc RawCravingEventService
//    //để đảm bảo DailySummary tồn tại trước khi tạo CravingTracking
//    @Transactional
//    public DailySummary findOrCreateDailySummary(UUID memberId, LocalDate trackDate) {
//        //Tìm quit plan của người dùng đang tracking
//        Optional<QuitPlan> quitPlanOptional = quitPlanService.getProgressQuitPlansByMemberId(memberId);
//        if (quitPlanOptional.isEmpty()) {
//            throw new ResourceNotFoundException("Không tìm thấy kế hoạch của người dùng với ID: " + memberId);
//        }
//
//        return dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlanOptional.get(), trackDate)
//                .orElseGet(() -> createDailySummary(quitPlanOptional.get(), trackDate));
//    }
//
//    @Transactional
//    public DailySummary getDailySummaryById(Integer id) {
//        return dailySummaryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi nhận hằng ngày với ID: " + id));
//    }
//
//    @Transactional
//    public DailySummary getDailySummaryByQuitPlanAndDate(Integer quitPlanId, LocalDate trackDate) {
//        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
//                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch với ID: " + quitPlanId));
//        return dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, trackDate)
//                .orElseThrow(() -> new ResourceNotFoundException("DailySummary not found for QuitPlan ID " + quitPlanId + " on date " + trackDate));
//    }
//
//    @Transactional
//    public DailySummary updateDailySummary(DailySummaryUpdateRequest request) {
//        Optional<DailySummary> existingSummaryOptional = dailySummaryRepository.findById(request.getDailySummaryId());
//        if (existingSummaryOptional.isEmpty()) {
//            throw new IllegalArgumentException("Không tìm thấy nhật ký với ID: " + request.getDailySummaryId());
//        }
//        DailySummary existingSummary = existingSummaryOptional.get();
//
//        //Nếu TrackDate đã vượt qua ngày hiện tại, không cho phép chỉnh sửa
//        LocalDate today = LocalDate.now();
//        if (existingSummary.getTrackDate().isBefore(today)) {
//            throw new DailySummaryEditForbiddenException("Không thể chỉnh sửa nhật ký cho ngày đã qua");
//        }
//
//        boolean changed = false;
//        if (existingSummary.getTotalSmokedCount() != request.getTotalSmokedCount()) {
//            existingSummary.setTotalSmokedCount(request.getTotalSmokedCount());
//
//            //Chỉ cập nhật moneySaved nếu có thay đổi
//            QuitPlan quitPlan = existingSummary.getQuitPlan();
//            if (quitPlan == null) {
//                throw new IllegalArgumentException("DailySummary không có QuitPlan liên kết. Không thể tính toán tiền đã tiết kiệm");
//            }
//
//            BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, existingSummary.getTotalSmokedCount());
//            if (existingSummary.getMoneySaved() == null || newMoneySaved == null || existingSummary.getMoneySaved().compareTo(newMoneySaved) != 0) {
//                existingSummary.setMoneySaved(newMoneySaved);
//            }
//            changed = true;
//        }
//        if (existingSummary.getTotalCravingCount() != request.getTotalCravingCount()) {
//            existingSummary.setTotalCravingCount(request.getTotalCravingCount());
//            changed = true;
//        }
//        // Sử dụng Objects.equals để xử lý trường hợp một trong hai hoặc cả hai là null
//        if (request.getMood() != null && !Objects.equals(existingSummary.getMood(), request.getMood())) {
//            existingSummary.setMood(request.getMood());
//            changed = true;
//        }
//        if (request.getNote() != null && !Objects.equals(existingSummary.getNote(), request.getNote())) {
//            existingSummary.setNote(request.getNote());
//            changed = true;
//        }
//        if (changed) {
//            return dailySummaryRepository.save(existingSummary);
//        } else {
//            throw new IllegalArgumentException("Không có thay đổi nào để cập nhật cho nhật ký với ID: " + request.getDailySummaryId());
//        }
//
//        //chưa update is Goal Achieved
//    }
//
//    @Transactional
//    public List<DailySummary> getDailySummariesByQuitPlanId(Integer quitPlanId) {
//        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
//                .orElseThrow(() -> new ResourceNotFoundException("QuitPlan not found with ID: " + quitPlanId));
//        return new ArrayList<>(dailySummaryRepository.findByQuitPlan(quitPlan));
//    }
//
//    @Transactional
//    public void deleteDailySummary(Integer id) {
//        if (!dailySummaryRepository.existsById(id)) {
//            throw new ResourceNotFoundException("DailySummary not found with ID: " + id);
//        }
//        dailySummaryRepository.deleteById(id);
//    }
//
//    @Transactional
//    public BigDecimal caculateMoneySaved(QuitPlan quitPlan, int totalSmoked) {
//        BigDecimal initialSmokingAmount = BigDecimal.valueOf(quitPlan.getInitialSmokingAmount());
//        BigDecimal cigarettesPerPack = BigDecimal.valueOf(quitPlan.getCigarettesPerPack());
//        BigDecimal pricePerPack = quitPlan.getPricePerPack();
//
//        // Tránh chia cho 0
//        if (cigarettesPerPack.compareTo(BigDecimal.ZERO) == 0) {
//            throw new IllegalArgumentException("CigarettesPerPack = 0. Không thể tính toán chi phí");
//        } else {
//            // Chi phí ước tính ban đầu cho số điếu hút hàng ngày: (InitialSmokingAmount / CigarettesPerPack) * PricePerPack
//            BigDecimal initialDailyCost = initialSmokingAmount
//                    .divide(cigarettesPerPack, 2, RoundingMode.HALF_UP)
//                    .multiply(pricePerPack);
//
//            // Chi phí thực tế dựa trên số điếu đã hút hôm nay: (TotalSmokedCount / CigarettesPerPack) * PricePerPack
//            BigDecimal todayCost = BigDecimal.valueOf(totalSmoked)
//                    .divide(cigarettesPerPack, 2, RoundingMode.HALF_UP)
//                    .multiply(pricePerPack);
//
//            return initialDailyCost.subtract(todayCost);
//        }
//    }
//
//    //Tái tính toán và cập nhật tổng số điếu hút và số lần thèm thuốc
////để đồng bộ với các bản ghi CravingTracking liên quan
//    @Transactional
//    public void recalculateDailyTotals(DailySummary dailySummary) {
//        // Lấy tất cả CravingTracking records cho DailySummary này
//        int totalSmoked = cravingTrackingService.getCravingTrackingsByDailySummaryId(dailySummary.getDailySummaryId())
//                .stream().mapToInt(CravingTracking::getSmokedCount).sum();
//        int totalCravings = cravingTrackingService.getCravingTrackingsByDailySummaryId(dailySummary.getDailySummaryId())
//                .stream().mapToInt(CravingTracking::getCravingsCount).sum();
//
//        QuitPlan quitPlan = dailySummary.getQuitPlan();
//        if (quitPlan == null) {
//            throw new IllegalArgumentException("DailySummary không có QuitPlan liên kết. Không thể tính toán tiền đã tiết kiệm");
//        }
//
//        BigDecimal oldMoneySaved = dailySummary.getMoneySaved();
//        BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, totalSmoked);
//
//        //Chỉ cập nhật nếu có thay đổi
//        boolean changed = false;
//        if (dailySummary.getTotalSmokedCount() != totalSmoked) {
//            dailySummary.setTotalSmokedCount(totalSmoked);
//            changed = true;
//        }
//        if (dailySummary.getTotalCravingCount() != totalCravings) {
//            dailySummary.setTotalCravingCount(totalCravings);
//            changed = true;
//        }
//        if (oldMoneySaved == null || newMoneySaved == null || oldMoneySaved.compareTo(newMoneySaved) != 0) {
//            dailySummary.setMoneySaved(newMoneySaved);
//            changed = true;
//        }
//
//        if (changed) {
//            dailySummaryRepository.save(dailySummary);
//        } else {
//            System.out.println("DailySummary ID " + dailySummary.getDailySummaryId() + ": Không có thay đổi tổng số liệu");
//        }
//    }
//
//    //Tái tính toán MoneySaved cho tất cả DailySummary
//    //Phương thức được gọi khi các thông tin của QuitPlan thay đổi
//    @Transactional
//    public void recalculateMoneySavedForQuitPlan(QuitPlan quitPlan) {
//        // Lấy tất cả DailySummary liên quan đến QuitPlan
//        List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan(quitPlan);
//
//        for (DailySummary dailySummary : dailySummaries) {
//            BigDecimal oldMoneySaved = dailySummary.getMoneySaved();
//            // Tính toán lại MoneySaved dựa trên QuitPlan đã được cập nhật và totalSmokedCount hiện tại của DailySummary
//            BigDecimal newMoneySaved = caculateMoneySaved(quitPlan, dailySummary.getTotalSmokedCount());
//
//            // Cập nhật DailySummary nếu MoneySaved thay đổi
//            if (oldMoneySaved == null || newMoneySaved == null || oldMoneySaved.compareTo(newMoneySaved) != 0) {
//                dailySummary.setMoneySaved(newMoneySaved);
//                dailySummaryRepository.save(dailySummary);
//            }
//        }
//    }
//}
