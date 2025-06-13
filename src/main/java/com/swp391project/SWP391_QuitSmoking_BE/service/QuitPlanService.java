package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import com.swp391project.SWP391_QuitSmoking_BE.util.QuitPlanCalculator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuitPlanService {
    private static final Logger log = LoggerFactory.getLogger(QuitPlanService.class);
    private final QuitPlanRepository quitPlanRepository;// Cần để tìm Member
    private final MemberRepository memberRepository;
    private final DailySummaryRepository dailySummaryRepository; // Cần để tìm DailySummary

    @Autowired
    private QuitPlanCalculator quitPlanCalculator; // Cần để tính toán kế hoạch cai thuốc

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public QuitPlanService(
            QuitPlanRepository quitPlanRepository,
            MemberRepository memberRepository,
            DailySummaryRepository dailySummaryRepository
    ) {
        this.quitPlanRepository = quitPlanRepository;
        this.memberRepository = memberRepository;
        this.dailySummaryRepository = dailySummaryRepository;
    }

    private static final int RELAPSE_CONSECUTIVE_DAYS_THRESHOLD = 3;
    // Ví dụ: 0.2 (20%) nghĩa là nếu hút 12 điếu khi mục tiêu là 10 điếu (vượt 20%) thì coi là tái nghiện.
    private static final double RELAPSE_PERCENTAGE_OVER_TARGET_THRESHOLD = 0.20; // 20%

    //Method để chuyển đổi Entity sang Response
//    private QuitPlanResponseDTO convertToResponseDto(QuitPlan quitPlan) {
//        QuitPlanResponseDTO response = new QuitPlanResponse();
//        response.setPlanTypeName(quitPlan.getPlanType().getPlanName());
//        response.setReductionType(quitPlan.getReductionType());
//        response.setStartDate(quitPlan.getStartDate());
//        response.setGoalDate(quitPlan.getGoalDate());
//        response.setInitialSmokingAmount(quitPlan.getInitialSmokingAmount());
//        response.setStatus(quitPlan.getStatus());
//
//        return response;
//    }

    @Transactional
    public QuitPlanResponseDTO createQuitPlan(UUID memberId, QuitPlanCreateRequestDTO request) {
        // Kiểm tra sự tồn tại của Member (ID của Member chính là ID của User)
        // Vì Member entity sử dụng @MapsId, memberRepository.findById(userId) sẽ tìm Member có userId đó.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        // Kiểm tra sự tồn tại của PlanType
//        PlanType planType = planTypeRepository.findById(request.getPlanTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại kế hoạch với ID: " + memberId));

        List<QuitPlan> existingActivePlans = quitPlanRepository.findByMemberAndStatusIn(
                member,
                List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)
        );

        QuitPlan quitPlan = new QuitPlan();
        quitPlan.setMember(member);
//        quitPlan.setPlanType(planType);
        quitPlan.setReductionType(request.getReductionType());
        quitPlan.setCreatedAt(LocalDateTime.now()); // Đặt ngày tạo là hiện tại

//        LocalDate requestStartDate = request.getStartDate().toLocalDate();
//        LocalDate currentLocalDate = LocalDate.now();

        LocalDate actualStartDate;
        QuitPlanStatus status;
        //ưu tiên status từ request nếu có, nếu không thì dùng status tự động xác định
//        quitPlan.setStatus(status);
//        log.info("Tạo kế hoạch cai thuốc cho thành viên: {}, với trạng thái: {}", memberId, status);
//        quitPlan.setStartDate(request.getStartDate());
//        quitPlan.setGoalDate(request.getGoalDate());

        if (request.getStartDate() == null) {
            // Startdate ko được cung cấp, sử dụng ngày hiện tại
            actualStartDate = LocalDate.now();
            status = QuitPlanStatus.IN_PROGRESS; // Ngày bắt đầu là hôm nay
            log.info("Ngay bắt đầu không được cung cấp, sử dụng ngày hiện tại: {}", actualStartDate);
        } else {
            // Startdate được cung cấp, sử dụng ngày đó
            actualStartDate = request.getStartDate().toLocalDate();
            if (actualStartDate.isAfter(LocalDate.now())) {
                status = QuitPlanStatus.NOT_STARTED; // Ngày bắt đầu trong tương lai
                log.info("Ngày bắt đầu trong tương lai: {}", actualStartDate);
            } else {
                status = QuitPlanStatus.IN_PROGRESS; // Ngày bắt đầu là hôm nay hoặc trong quá khứ
                log.info("Ngày bắt đầu là hôm nay hoặc trong quá khứ: {}", actualStartDate);
            }
        }

        quitPlan.setStartDate(actualStartDate.atStartOfDay()); // Chuyển đổi sang LocalDateTime
        quitPlan.setStatus(status); // Trạng thái mới là IN_PROGRESS hoặc NOT_STARTED
        quitPlan.setGoalDate(request.getGoalDate()); // Chuyển đổi sang LocalDateTime
        quitPlan.setInitialSmokingAmount(request.getInitialSmokingAmount());
        quitPlan.setCigarettesPerPack(request.getCigarettesPerPack());
        quitPlan.setPricePerPack(request.getPricePerPack());
        // Lưu kế hoạch cai thuốc vào cơ sở dữ liệu
        log.info("Lưu kế hoạch cai thuốc cho thành viên: {}", memberId);
        QuitPlan savedPlan = quitPlanRepository.save(quitPlan);
        log.info("Kế hoạch cai thuốc đã được lưu với ID: {}", savedPlan.getQuitPlanId());
        // Chuyển đổi sang DTO để trả về
        return modelMapper.map(savedPlan, QuitPlanResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public QuitPlanResponseDTO getQuitPlanById(Integer id, UUID memberId) {
        QuitPlan quitPlan = quitPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch cai thuốc với ID: " + id));
        return modelMapper.map(quitPlan, QuitPlanResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<QuitPlanAdminResponseDTO> getAllQuitPlansForAdmin() {
        return quitPlanRepository.findAll().stream()
                .map(quitPlan -> modelMapper.map(quitPlan, QuitPlanAdminResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuitPlanResponseDTO> getQuitPlansByMemberId(UUID memberId) {
        // Kiểm tra sự tồn tại của Member trước
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> quitPlans = quitPlanRepository.findByMember_MemberId(memberId);
        return quitPlans.stream()
                .map(quitPlan -> modelMapper.map(quitPlan, QuitPlanResponseDTO.class))
                .collect(Collectors.toList());
    }

    // -- UPDATE --
    @Transactional
    public QuitPlanResponseDTO updateQuitPlan(Integer quitPlanId, QuitPlanUpdateRequestDTO updateRequestDTO) {
        // 1. Tìm plan
        QuitPlan existingPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        // 2. Cập nhật các trường từ DTO vào entity
        modelMapper.map(updateRequestDTO, existingPlan);

        //3. lưu lại entity đã cập nhật
        QuitPlan updatedPlan = quitPlanRepository.save(existingPlan);
        return modelMapper.map(updatedPlan, QuitPlanResponseDTO.class);
    }

    // -- DELETE --
    @Transactional
    public void deleteQuitPlan(Integer quitPlanId) {
        // 1. Tìm plan
        QuitPlan existingPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        // 2. Xóa plan
        quitPlanRepository.delete(existingPlan);
    }

    // --- BUSINESS LOGIC ---

    // --- FOR STATUS MANAGEMENT ---
    // Cập nhat status của kế hoạch cai thuốc
    // Có thể chạy Scheduler
    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày lúc 00:00
    @Transactional
    public void updateQuitPlanStatus() {
        LocalDate today = LocalDate.now();
        List<QuitPlan> allPlans = quitPlanRepository.findAll();

        for (QuitPlan plan: allPlans) {
            updateQuitPlanStatus(plan, today);
        }
    }

    /**
     * Cập nhật status của kế hoạch cụ thể
     * @param plan
     * @param currentDate
     */
    private void updateQuitPlanStatus(QuitPlan plan, LocalDate currentDate) {
        QuitPlanStatus currentStatus = plan.getStatus();
        QuitPlanStatus newStatus = currentStatus;

        // Logic cập nhật status
        switch (currentStatus) {
            case NOT_STARTED:
                // Logic: Ngày hiện tại >= ngày bắt đầu -> chuyển sang IN_PROGRESS
                if (!plan.getStartDate().toLocalDate().isAfter(currentDate)) {
                    newStatus = QuitPlanStatus.IN_PROGRESS;
                }
                break;
            case IN_PROGRESS:
                // Logic: Ngày hiện tại > ngày kết thúc -> chuyển sang COMPLETED hoặc FAILED
                if (currentDate.isAfter(plan.getGoalDate())) {
                    // Giả sử nếu người dùng đã hoàn thành ít nhất 80% kế hoạch thì coi là COMPLETED
                    // Cần có logic để tính toán mức độ hoàn thành
                    if (isPlanCompleted(plan)) {
                        newStatus = QuitPlanStatus.COMPLETED;
                    } else {
                        newStatus = QuitPlanStatus.FAILED;
                    }
                } else if (!isPlanCompleted(plan)) {
                    newStatus = QuitPlanStatus.RESTARTED;
                }
                break;
            case COMPLETED:
            case FAILED:
            case RESTARTED:
                break;

        }

        if (newStatus != currentStatus) {
            plan.setStatus(newStatus);
            quitPlanRepository.save(plan); // Lưu lại thay đổi
        }
    }


    /*
    * Kiểm tra xem kế hoạch có được coi là COMPLETED hay không
    * */
    private boolean isPlanCompleted(QuitPlan plan) {
        long totalDaysInPlan = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), plan.getGoalDate());
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

//        if (records.isEmpty() && plan.getGoalDate().isAfter(LocalDate.now()))  {
//            return false; // Nếu không có bản ghi nào và ngày kết thúc vẫn chưa đến, coi là chưa hoàn thành
//        }

        long daysAchievedGoal = 0; // Số ngày đã đạt được mục tiêu

        for (DailySummary record : records) {
            // Xacs định ngày trong bản ghi
            long dayIndex = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), record.getTrackDate());

            // Tìm mục tiêu số thuốc lá trong ngày tương ứng
            Optional<QuitPlanCalculator.QuitPlanDay> currentDayTarget = targetCigarettesPerDay.stream()
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
                continue;
            }
        }

        // Tỉ lệ hoàn thành cần đạt
        double successRateThreshold = 0.8; // 80%
        double actualSuccessRate = (double) daysAchievedGoal / totalDaysInPlan;

        return actualSuccessRate >= successRateThreshold;
    }

    /**
     * Logic kiểm tra xem người dùng có tái nghiện hay không
     *
     * @param plan cần kiêm tra
     * @return true nếu tái nghiện, false nếu không
     */
    private boolean isUserRelapsed(QuitPlan plan) {
        // Chỉ áp dụng với IN_PROGRESS
        if (plan.getStatus() != QuitPlanStatus.IN_PROGRESS) {
            return false;
        }

        long totalDaysInPlan = ChronoUnit.DAYS.between(plan.getStartDate().toLocalDate(), plan.getGoalDate());
        if (totalDaysInPlan <= 0) return false; // Không hợp lệ nếu ngày bắt đầu >= ngày kết thúc

        List<QuitPlanCalculator.QuitPlanDay> targetCigarettesPerDay = quitPlanCalculator.generateQuitPlan(
                plan.getInitialSmokingAmount(),
                totalDaysInPlan,
                plan.getReductionType()
        );

        // Lấy tất cả các bản ghi DailySummary gần nhất theo số ngày ngưỡng tái nghiện
        List<DailySummary> recentRecords = dailySummaryRepository.findByQuitPlanOrderByTrackDateDesc(plan)
                .stream()
                .limit(RELAPSE_CONSECUTIVE_DAYS_THRESHOLD)
                .collect(Collectors.toList());

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

    @Transactional
    public QuitPlanResponseDTO restartedQuitPlan(
            Integer quitPlanId,
            UUID memberId,
            QuitPlanRestartRequestDTO restartRequestDTO) {
        // Tìm plan
        QuitPlan oldPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));


        oldPlan.setStatus(QuitPlanStatus.RESTARTED);
        quitPlanRepository.save(oldPlan);

//        PlanType newPlanType = planTypeRepository.findById(restartRequestDTO.getNewPlanTypeId())
//                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại kế hoạch với ID: " + restartRequestDTO.getNewPlanTypeId()));

        QuitPlan newPlan = new QuitPlan();
        newPlan.setMember(oldPlan.getMember());
        newPlan.setCreatedAt(LocalDateTime.now());

        newPlan.setStartDate(restartRequestDTO.getNewStartDate());
        newPlan.setGoalDate(restartRequestDTO.getNewGoalDate());
        newPlan.setInitialSmokingAmount(restartRequestDTO.getNewInitialSmokingAmount());
        newPlan.setPricePerPack(restartRequestDTO.getNewPricePerPack());
        newPlan.setCigarettesPerPack(restartRequestDTO.getNewCigarettesPerPack());
//        newPlan.setPlanType(newPlanType);
        newPlan.setReductionType(restartRequestDTO.getNewReductionType()); // Trạng thái mới là IN_PROGRESS
        newPlan.setStatus(QuitPlanStatus.NOT_STARTED); // Trạng thái mới là IN_PROGRESS

        // Lưu lại kế hoạch đã cập nhật
        QuitPlan restartedPlan = quitPlanRepository.save(newPlan);
        return modelMapper.map(restartedPlan, QuitPlanResponseDTO.class);
    }
}
