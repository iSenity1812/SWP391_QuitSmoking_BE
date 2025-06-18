package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import com.swp391project.SWP391_QuitSmoking_BE.util.QuitPlanCalculator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

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

    private final QuitPlanCalculator quitPlanCalculator; // Cần để tính toán kế hoạch cai thuốc

    private final ModelMapper modelMapper;

    @Autowired
    public QuitPlanService(
            QuitPlanRepository quitPlanRepository,
            MemberRepository memberRepository,
            DailySummaryRepository dailySummaryRepository,
            QuitPlanCalculator quitPlanCalculator, ModelMapper modelMapper) {
        this.quitPlanRepository = quitPlanRepository;
        this.memberRepository = memberRepository;
        this.dailySummaryRepository = dailySummaryRepository;
        this.quitPlanCalculator = quitPlanCalculator;
        this.modelMapper = modelMapper;
    }

    private static final int RELAPSE_CONSECUTIVE_DAYS_THRESHOLD = 3;
    // Ví dụ: 0.2 (20%) nghĩa là nếu hút 12 điếu khi mục tiêu là 10 điếu (vượt 20%) thì coi là tái nghiện.
    private static final double RELAPSE_PERCENTAGE_OVER_TARGET_THRESHOLD = 0.20; // 20%

    @Transactional
    public Optional<QuitPlan> getProgressQuitPlansByMemberId(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với ID: " + memberId));
        return quitPlanRepository.findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(memberId, QuitPlanStatus.IN_PROGRESS);
    }

    @Transactional
    public QuitPlanResponseDTO createQuitPlan(UUID memberId, QuitPlanCreateRequestDTO request) {
        // Kiểm tra sự tồn tại của Member (ID của Member chính là ID của User)
        // Vì Member entity sử dụng @MapsId, memberRepository.findById(userId) sẽ tìm Member có userId đó.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> existingActivePlans = quitPlanRepository.findByMemberAndStatusIn(
                member,
                List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)
        );

        if (existingActivePlans.isEmpty()) {
            log.info("Không có kế hoạch cai thuốc nào đang hoạt động cho thành viên: {}", memberId);
        } else {
            log.warn("Thành viên {} đã có kế hoạch cai thuốc đang hoạt động. Không thể tạo kế hoạch mới.", memberId);
            throw new IllegalArgumentException("Thành viên đã có kế hoạch cai thuốc đang hoạt động.");
        }

        LocalDateTime nowForConsistency = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1);
        QuitPlan quitPlan = new QuitPlan();
        quitPlan.setMember(member);
        quitPlan.setReductionType(request.getReductionType());
        quitPlan.setCreatedAt(nowForConsistency); // Đặt ngày tạo là hiện tại

        LocalDateTime actualStartDate;
        QuitPlanStatus status;

        if (request.getStartDate() == null) {
            // Startdate ko được cung cấp, sử dụng ngày hiện tại
            actualStartDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1); // Truncated để đảm bảo tính nhất quán
            status = QuitPlanStatus.IN_PROGRESS; // Ngày bắt đầu `là hôm nay
            log.info("Ngay bắt đầu không được cung cấp, sử dụng ngày hiện tại (+1s): {}", actualStartDate);
        } else {
            // Startdate được cung cấp, sử dụng ngày đó
            actualStartDate = request.getStartDate();
            if (actualStartDate.isAfter(LocalDateTime.now())) {
                status = QuitPlanStatus.NOT_STARTED; // Ngày bắt đầu trong tương lai
                log.info("Ngày bắt đầu trong tương lai: {}", actualStartDate);
            } else {
                status = QuitPlanStatus.IN_PROGRESS; // Ngày bắt đầu là hôm nay hoặc trong quá khứ
                log.info("Ngày bắt đầu là hôm nay hoặc trong quá khứ: {}", actualStartDate);
            }
        }

        quitPlan.setStartDate(actualStartDate); // Chuyển đổi sang LocalDateTime
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
        return quitPlanRepository.findAllWithMemberAndUser().stream()
                .map(quitPlan -> modelMapper.map(quitPlan, QuitPlanAdminResponseDTO.class))
                .collect(Collectors.toList());
    }

    // get quit plans by Id for admin
    @Transactional
    public QuitPlanAdminResponseDTO getQuitPlanByIdForAdmin(Integer id) {
        QuitPlan quitPlan = quitPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch cai thuốc với ID: " + id));
        return modelMapper.map(quitPlan, QuitPlanAdminResponseDTO.class);
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

    @Transactional(readOnly = true)
    public List<QuitPlanAdminResponseDTO> getQuitPlansByMemberIdForAdmin(UUID memberId) {
        // Kiểm tra sự tồn tại của Member trước
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> quitPlans = quitPlanRepository.findByMemberOrderByCreatedAtDesc(member);
        return quitPlans.stream()
                .map(quitPlan -> modelMapper.map(quitPlan, QuitPlanAdminResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Lấy quit plan hiện tại của một thành viên (chỉ có một kế hoạch cai thuốc đang hoạt động)
    @Transactional(readOnly = true)
    public QuitPlanResponseDTO getCurrentQuitPlanByMemberId(UUID memberId) {
        // Kiểm tra sự tồn tại của Member trước
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        // Lấy kế hoạch cai thuốc hiện tại (chỉ có một kế hoạch cai thuốc đang hoạt động)
        QuitPlan currentPlan = quitPlanRepository.findByMemberAndStatusIn(
                member,
                List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)
        ).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch cai thuốc đang hoạt động cho thành viên với ID: " + memberId));

        return modelMapper.map(currentPlan, QuitPlanResponseDTO.class);
    }

    // -- UPDATE --
    @Transactional
    public QuitPlanResponseDTO updateQuitPlan(Integer quitPlanId, UUID memberId, QuitPlanUpdateRequestDTO updateRequestDTO) {
        // 1. Tìm plan
        QuitPlan existingPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        // Kiểm tra quyền sở hữu
        if (!existingPlan.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Kế hoạch này không thuộc về thành viên với ID: " + memberId);
        }

        // 2. Cập nhật các trường từ DTO vào entity
        modelMapper.map(updateRequestDTO, existingPlan);

        //3. lưu lại entity đã cập nhật
        QuitPlan updatedPlan = quitPlanRepository.save(existingPlan);
        return modelMapper.map(updatedPlan, QuitPlanResponseDTO.class);
    }

    // Cập nhaat kế hoạch cai thuốc hiện tại của người dùng
    @Transactional
    public QuitPlanResponseDTO updateCurrentActivePlan(UUID memberId, QuitPlanUpdateRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> activePlans  = quitPlanRepository.findByMemberAndStatusIn(
                member,
                List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)
        );

        if (activePlans.isEmpty()) {
            log.info("Không có kế hoạch cai thuốc nào đang hoạt động cho thành viên: {}", memberId);
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch cai thuốc đang hoạt động với thành viên với ID: " + memberId);
        }

        QuitPlan currentPlan = activePlans.getFirst();
        modelMapper.map(request, currentPlan);
        QuitPlan updatedPlan = quitPlanRepository.save(currentPlan);
        return modelMapper.map(updatedPlan, QuitPlanResponseDTO.class);
    }

    // -- DELETE --
    @Transactional
    public void deleteQuitPlanById(Integer quitPlanId) {
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
                }
                break;
            case COMPLETED:
            case FAILED:
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
                log.warn("Bản ghi DailySummary cho kế hoạch ID {} có ngày theo dõi {} nằm ngoài phạm vi kế hoạch.", plan.getQuitPlanId(), record.getTrackDate());
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


    // GIVE UP QUIT PLAN
    @Transactional
    public QuitPlanResponseDTO giveUpQuitPlan(Integer quitPlanId, UUID memberId) {
        QuitPlan currentPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        // Kiểm tra quyền sở hữu
        if (!currentPlan.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Kế hoạch này không thuộc về thành viên với ID: " + memberId);
        }
        // Kiểm tra trạng thái hiện tại
        if (currentPlan.getStatus() == QuitPlanStatus.FAILED ||
                currentPlan.getStatus() == QuitPlanStatus.COMPLETED) {
            throw new IllegalArgumentException("Không thể bỏ cuộc kế hoạch đã hoàn thành hoặc đã thất bại");
        }


        // Đánh dấu kế hoạch là FAILED
//        int updateRow = quitPlanRepository.updateQuitPlanStatus(quitPlanId, QuitPlanStatus.FAILED);

//        currentPlan.setStatus(QuitPlanStatus.FAILED);
//        log.info("Current plan status is set to {} for quit plan ID {}", currentPlan.getStatus(), quitPlanId);
//        System.out.println("current plan: " + currentPlan);
//        quitPlanRepository.save(currentPlan);

        quitPlanRepository.updateQuitPlanStatus(quitPlanId, QuitPlanStatus.FAILED);
        log.info("Kế hoạch ID {} của thành viên {} đã được đánh dấu là FAILED do người dùng bỏ cuộc.", quitPlanId, memberId);
        return modelMapper.map(currentPlan, QuitPlanResponseDTO.class);

    }
}
