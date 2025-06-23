package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.PaymentConfirmRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuitPlanService {
    private static final Logger log = LoggerFactory.getLogger(QuitPlanService.class);
    private final QuitPlanRepository quitPlanRepository;
    private final MemberRepository memberRepository;
    private final DailySummaryRepository dailySummaryRepository;

    private final QuitPlanCalculator quitPlanCalculator;

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
    // Ví dụ: 0.2 (20%) nghĩa là nếu hút 12 điếu khi mục tiêu là 10 điếu (vượt 20%)
    // thì coi là tái nghiện.
    private static final double RELAPSE_PERCENTAGE_OVER_TARGET_THRESHOLD = 0.20; // 20%

    @Transactional
    public Optional<QuitPlan> getProgressQuitPlansByMemberId(UUID memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với ID: " + memberId));
        return quitPlanRepository.findFirstByMember_MemberIdAndStatusOrderByCreateDateDesc(memberId,
                QuitPlanStatus.IN_PROGRESS);
    }

    @Transactional
    public QuitPlanResponseDTO createQuitPlan(UUID memberId, QuitPlanCreateRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> existingActivePlans = quitPlanRepository.findByMemberAndStatusIn(
                member,
                List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED));

        if (!existingActivePlans.isEmpty()) {
            log.warn("Thành viên {} đã có kế hoạch cai thuốc đang hoạt động. Không thể tạo kế hoạch mới.", memberId);
            throw new IllegalArgumentException("Thành viên đã có kế hoạch cai thuốc đang hoạt động.");
        }

        QuitPlan quitPlan = new QuitPlan();
        quitPlan.setMember(member);
        quitPlan.setReductionType(request.getReductionType());
        quitPlan.setCreateDate(LocalDate.now());

        LocalDate actualStartDate;
        QuitPlanStatus status;

        if (request.getStartDate() == null) {
            actualStartDate = LocalDate.now();
            status = QuitPlanStatus.IN_PROGRESS;
            log.info("Ngay bắt đầu không được cung cấp, sử dụng ngày hiện tại: {}", actualStartDate);
        } else {
            actualStartDate = request.getStartDate();
            if (actualStartDate.isAfter(LocalDate.now())) {
                status = QuitPlanStatus.NOT_STARTED;
                log.info("Ngày bắt đầu trong tương lai: {}", actualStartDate);
            } else {
                status = QuitPlanStatus.IN_PROGRESS;
                log.info("Ngày bắt đầu là hôm nay hoặc trong quá khứ: {}", actualStartDate);
            }
        }

        quitPlan.setStartDate(actualStartDate);
        quitPlan.setStatus(status);
        quitPlan.setGoalDate(request.getGoalDate());
        quitPlan.setInitialSmokingAmount(request.getInitialSmokingAmount());
        quitPlan.setCigarettesPerPack(request.getCigarettesPerPack());
        quitPlan.setPricePerPack(request.getPricePerPack());

        QuitPlan savedPlan = quitPlanRepository.save(quitPlan);
        log.info("Kế hoạch cai thuốc đã được lưu với ID: {}", savedPlan.getQuitPlanId());
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

    @Transactional
    public QuitPlanAdminResponseDTO getQuitPlanByIdForAdmin(Integer id) {
        QuitPlan quitPlan = quitPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch cai thuốc với ID: " + id));
        return modelMapper.map(quitPlan, QuitPlanAdminResponseDTO.class);
    }

    @Transactional(readOnly = true)
    public List<QuitPlanResponseDTO> getQuitPlansByMemberId(UUID memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> quitPlans = quitPlanRepository.findByMember_MemberId(memberId);
        return quitPlans.stream()
                .map(quitPlan -> modelMapper.map(quitPlan, QuitPlanResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuitPlanAdminResponseDTO> getQuitPlansByMemberIdForAdmin(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> quitPlans = quitPlanRepository.findByMemberOrderByCreateDateDesc(member);
        return quitPlans.stream()
                .map(quitPlan -> modelMapper.map(quitPlan, QuitPlanAdminResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuitPlanResponseDTO getCurrentQuitPlanByMemberId(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        QuitPlan currentPlan = quitPlanRepository.findByMemberAndStatusIn(
                member,
                List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)).stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy kế hoạch cai thuốc đang hoạt động cho thành viên với ID: " + memberId));

        return modelMapper.map(currentPlan, QuitPlanResponseDTO.class);
    }

    @Transactional
    public QuitPlanResponseDTO updateQuitPlan(Integer quitPlanId, UUID memberId,
            QuitPlanUpdateRequestDTO updateRequestDTO) {
        QuitPlan existingPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        if (!existingPlan.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Kế hoạch này không thuộc về thành viên với ID: " + memberId);
        }

        modelMapper.map(updateRequestDTO, existingPlan);

        QuitPlan updatedPlan = quitPlanRepository.save(existingPlan);
        return modelMapper.map(updatedPlan, QuitPlanResponseDTO.class);
    }

    @Transactional
    public QuitPlanResponseDTO updateCurrentActivePlan(UUID memberId, QuitPlanUpdateRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> activePlans = quitPlanRepository.findByMemberAndStatusIn(
                member,
                List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED));

        if (activePlans.isEmpty()) {
            log.info("Không có kế hoạch cai thuốc nào đang hoạt động cho thành viên: {}", memberId);
            throw new ResourceNotFoundException(
                    "Không tìm thấy kế hoạch cai thuốc đang hoạt động với thành viên với ID: " + memberId);
        }

        QuitPlan currentPlan = activePlans.get(0);
        modelMapper.map(request, currentPlan);
        QuitPlan updatedPlan = quitPlanRepository.save(currentPlan);
        return modelMapper.map(updatedPlan, QuitPlanResponseDTO.class);
    }

    @Transactional
    public void deleteQuitPlanById(Integer quitPlanId) {
        QuitPlan existingPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        quitPlanRepository.delete(existingPlan);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateQuitPlanStatus() {
        LocalDate today = LocalDate.now();
        List<QuitPlan> allPlans = quitPlanRepository.findAll();

        for (QuitPlan plan : allPlans) {
            updateQuitPlanStatus(plan, today);
        }
    }

    private void updateQuitPlanStatus(QuitPlan plan, LocalDate currentDate) {
        if (plan.getStatus() == QuitPlanStatus.COMPLETED || plan.getStatus() == QuitPlanStatus.FAILED) {
            return;
        }

        if (plan.getStatus() == QuitPlanStatus.NOT_STARTED && !currentDate.isBefore(plan.getStartDate())) {
            plan.setStatus(QuitPlanStatus.IN_PROGRESS);
            log.info("QuitPlan ID {} has been started.", plan.getQuitPlanId());
        }

        if (plan.getStatus() == QuitPlanStatus.IN_PROGRESS) {
            if (isPlanCompleted(plan)) {
                plan.setStatus(QuitPlanStatus.COMPLETED);
                log.info("QuitPlan ID {} has been completed.", plan.getQuitPlanId());
            } else if (isUserRelapsed(plan)) {
                plan.setStatus(QuitPlanStatus.FAILED);
                log.info("QuitPlan ID {} has failed due to relapse.", plan.getQuitPlanId());
            }
        }
        quitPlanRepository.save(plan);
    }

    private boolean isPlanCompleted(QuitPlan plan) {
        long totalDaysInPlan = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getGoalDate());
        if (totalDaysInPlan <= 0)
            return false;

        List<QuitPlanCalculator.QuitPlanDay> targetCigarettesPerDay = quitPlanCalculator.generateQuitPlan(
                plan.getInitialSmokingAmount(),
                totalDaysInPlan,
                plan.getReductionType());

        List<DailySummary> records = dailySummaryRepository.findByQuitPlanAndTrackDateBetween(
                plan, plan.getStartDate(), plan.getGoalDate());

        long daysAchievedGoal = 0;
        for (DailySummary record : records) {
            Optional<QuitPlanCalculator.QuitPlanDay> currentDayTarget = targetCigarettesPerDay.stream()
                    .filter(d -> d.getDay() == ChronoUnit.DAYS.between(plan.getStartDate(), record.getTrackDate()) + 1)
                    .findFirst();

            if (currentDayTarget.isPresent()) {
                int allowedCigarettes = currentDayTarget.get().getCigarettes();
                if (record.getTotalSmokedCount() <= allowedCigarettes) {
                    daysAchievedGoal++;
                }
            } else {
                log.warn("Bản ghi DailySummary cho kế hoạch ID {} có ngày theo dõi {} nằm ngoài phạm vi kế hoạch.",
                        plan.getQuitPlanId(), record.getTrackDate());
            }
        }

        double completionPercentage = (double) daysAchievedGoal / totalDaysInPlan;
        return completionPercentage >= 0.8;
    }

    private boolean isUserRelapsed(QuitPlan plan) {
        long totalDaysInPlan = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getGoalDate());
        if (totalDaysInPlan <= 0)
            return false;

        List<QuitPlanCalculator.QuitPlanDay> targetCigarettesPerDay = quitPlanCalculator.generateQuitPlan(
                plan.getInitialSmokingAmount(),
                totalDaysInPlan,
                plan.getReductionType());

        for (int i = 0; i < RELAPSE_CONSECUTIVE_DAYS_THRESHOLD; i++) {
            LocalDate dateToCheck = LocalDate.now().minusDays(i);

            if (dateToCheck.isBefore(plan.getStartDate())) {
                return false;
            }
            Optional<DailySummary> recordOpt = dailySummaryRepository.findByQuitPlanAndTrackDate(plan, dateToCheck);

            if (recordOpt.isEmpty()) {
                return false;
            }

            DailySummary record = recordOpt.get();

            Optional<QuitPlanCalculator.QuitPlanDay> currentDayTarget = targetCigarettesPerDay.stream()
                    .filter(d -> d.getDay() == ChronoUnit.DAYS.between(plan.getStartDate(), record.getTrackDate()) + 1)
                    .findFirst();

            if (currentDayTarget.isPresent()) {
                int allowedCigarettes = currentDayTarget.get().getCigarettes();
                double relapseLimit = allowedCigarettes * (1 + RELAPSE_PERCENTAGE_OVER_TARGET_THRESHOLD);

                if (record.getTotalSmokedCount() <= relapseLimit) {
                    return false;
                }
            } else {
                log.warn("DailySummary for plan ID {} has a track date {} outside the plan's calculation range.",
                        plan.getQuitPlanId(), record.getTrackDate());
                return false;
            }
        }
        return true;
    }

    @Transactional
    public QuitPlanResponseDTO giveUpQuitPlan(Integer quitPlanId, UUID memberId) {
        QuitPlan currentPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        if (!currentPlan.getMember().getMemberId().equals(memberId)) {
            throw new SecurityException("Bạn không có quyền thực hiện hành động này.");
        }

        if (currentPlan.getStatus() == QuitPlanStatus.COMPLETED || currentPlan.getStatus() == QuitPlanStatus.FAILED) {
            throw new IllegalArgumentException("Không thể bỏ cuộc kế hoạch đã hoàn thành hoặc đã thất bại");
        }

        quitPlanRepository.updateQuitPlanStatus(quitPlanId, QuitPlanStatus.FAILED);
        log.info("Kế hoạch ID {} của thành viên {} đã được đánh dấu là FAILED do người dùng bỏ cuộc.", quitPlanId,
                memberId);
        return modelMapper.map(currentPlan, QuitPlanResponseDTO.class);
    }
}
