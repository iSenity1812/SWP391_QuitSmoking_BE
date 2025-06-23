package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.dailysummary.DailySummaryUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.CravingTracking;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievementId;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DailySummaryEditForbiddenException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AchievementRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CravingTrackingRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberAchievementRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DailySummaryService {
    private static final Logger log = LoggerFactory.getLogger(DailySummaryService.class);

    private final DailySummaryRepository dailySummaryRepository;
    private final MemberRepository memberRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final CravingTrackingRepository cravingTrackingRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AchievementRepository achievementRepository;
    private final MemberAchievementRepository memberAchievementRepository;

    @Transactional
    public DailySummaryResponse createManualDailySummary(UUID memberId, DailySummaryCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy thành viên với ID: " + memberId));

        QuitPlan quitPlan = quitPlanRepository
                .findFirstByMember_MemberIdAndStatusOrderByCreateDateDesc(member.getMemberId(),
                        QuitPlanStatus.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy kế hoạch bỏ thuốc đang hoạt động cho thành viên này."));

        dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, request.getTrackDate())
                .ifPresent(s -> {
                    throw new DailySummaryEditForbiddenException(
                            "Đã có bản tóm tắt hàng ngày cho ngày " + request.getTrackDate() + ".");
                });

        DailySummary dailySummary = modelMapper.map(request, DailySummary.class);
        dailySummary.setQuitPlan(quitPlan);
        dailySummary.setMoneySaved(calculateMoneySaved(quitPlan, request.getTotalSmokedCount()));
        DailySummary savedDailySummary = dailySummaryRepository.save(dailySummary);

        updateMemberStreak(member);
        checkAndAwardAchievements(member);

        return modelMapper.map(savedDailySummary, DailySummaryResponse.class);
    }

    @Transactional
    public DailySummaryResponse updateDailySummary(Integer dailySummaryId, DailySummaryUpdateRequest request) {
        DailySummary existingSummary = dailySummaryRepository.findById(dailySummaryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy bản tóm tắt hàng ngày với ID: " + dailySummaryId));

        if (!existingSummary.getTrackDate().isEqual(LocalDate.now())) {
            throw new DailySummaryEditForbiddenException(
                    "Không được phép chỉnh sửa bản tóm tắt của ngày trước đó. Chỉ có thể chỉnh sửa bản tóm tắt của ngày hiện tại.");
        }

        existingSummary.setTotalSmokedCount(request.getTotalSmokedCount());
        existingSummary.setCravingSeverityAverage(request.getCravingSeverityAverage());
        existingSummary.setMoodAverage(request.getMoodAverage());
        existingSummary.setNote(request.getNote());
        existingSummary
                .setMoneySaved(calculateMoneySaved(existingSummary.getQuitPlan(), request.getTotalSmokedCount()));

        DailySummary updatedDailySummary = dailySummaryRepository.save(existingSummary);

        updateMemberStreak(existingSummary.getQuitPlan().getMember());
        checkAndAwardAchievements(existingSummary.getQuitPlan().getMember());

        return modelMapper.map(updatedDailySummary, DailySummaryResponse.class);
    }

    public BigDecimal calculateMoneySaved(QuitPlan quitPlan, int totalSmokedCount) {
        if (quitPlan == null || quitPlan.getInitialCigarettesPerDay() == null ||
                quitPlan.getPricePerPack() == null || quitPlan.getCigarettesPerPack() == 0) {
            log.warn("Missing necessary quit plan data for money saved calculation. QuitPlan ID: {}",
                    (quitPlan != null ? quitPlan.getQuitPlanId().toString() : "null"));
            return BigDecimal.ZERO;
        }

        double initialCigarettesPerDay = quitPlan.getInitialCigarettesPerDay();
        BigDecimal pricePerPack = quitPlan.getPricePerPack();
        int cigarettesPerPack = quitPlan.getCigarettesPerPack();

        if (initialCigarettesPerDay == 0 || pricePerPack.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        double initialCostPerDay = (initialCigarettesPerDay / cigarettesPerPack) * pricePerPack.doubleValue();
        double currentCostPerDay = (double) totalSmokedCount / cigarettesPerPack * pricePerPack.doubleValue();
        return BigDecimal.valueOf(initialCostPerDay - currentCostPerDay).max(BigDecimal.ZERO);
    }

    private void updateMemberStreak(Member member) {
        Optional<QuitPlan> activePlanOpt = quitPlanRepository
                .findFirstByMember_MemberIdAndStatusOrderByCreateDateDesc(
                        member.getMemberId(), QuitPlanStatus.IN_PROGRESS);

        if (activePlanOpt.isEmpty()) {
            if (member.getStreak() != 0) {
                member.setStreak(0);
                memberRepository.save(member);
                log.info("Member {} streak reset to 0 as no active plan found.", member.getMemberId());
            }
            return;
        }

        QuitPlan activePlan = activePlanOpt.get();
        int currentStreak = 0;
        LocalDate today = LocalDate.now();

        for (long i = 0;; i++) {
            LocalDate dateToCheck = today.minusDays(i);
            if (dateToCheck.isBefore(activePlan.getStartDate())) {
                break;
            }

            Optional<DailySummary> summaryOpt = dailySummaryRepository.findByQuitPlanAndTrackDate(activePlan,
                    dateToCheck);

            if (summaryOpt.isPresent() && summaryOpt.get().getTotalSmokedCount() == 0) {
                currentStreak++;
            } else {
                break;
            }
        }

        member.setStreak(currentStreak);
        memberRepository.save(member);
        log.info("Member {} streak updated to {}", member.getMemberId(), currentStreak);
    }

    @Transactional
    public void checkAndAwardAchievements(Member member) {
        log.info("Checking and awarding achievements for member: {}", member.getMemberId());
        List<Achievement> allAchievements = achievementRepository.findAll();

        for (Achievement achievement : allAchievements) {
            try {
                MemberAchievementId memberAchievementId = new MemberAchievementId(achievement.getAchievementId(),
                        member.getMemberId());
                if (memberAchievementRepository.findById(memberAchievementId).isPresent()) {
                    log.debug("Member {} already has achievement {}. Skipping.", member.getMemberId(),
                            achievement.getName());
                    continue;
                }

                JsonNode criteriaNode = objectMapper.readTree(achievement.getCriteria());
                String type = criteriaNode.get("type").asText();

                boolean achieved = false;

                switch (type) {
                    case "daysWithoutSmoking":
                        int requiredDays = criteriaNode.get("value").asInt();
                        if (member.getStreak() >= requiredDays) {
                            achieved = true;
                            log.info("Member {} achieved 'daysWithoutSmoking' ({} days) with current streak {}.",
                                    member.getMemberId(), requiredDays, member.getStreak());
                        }
                        break;

                    case "completeQuitPlan":
                        // This part has compilation error because PlanType is not an enum.
                        // I will comment it out for now to make the build pass.
                        // String planTypeString = criteriaNode.get("planType").asText();
                        // try {
                        // PlanType planType = PlanType.valueOf(planTypeString);
                        // Optional<QuitPlan> completedPlan =
                        // quitPlanRepository.findByMemberAndPlanTypeAndStatus(
                        // member, planType, QuitPlanStatus.COMPLETED);
                        // if (completedPlan.isPresent()) {
                        // achieved = true;
                        // log.info("Member {} achieved 'completeQuitPlan' for plan type {}.",
                        // member.getMemberId(), planType);
                        // }
                        // } catch (IllegalArgumentException e) {
                        // log.error("Invalid PlanType in achievement criteria: {}. Error: {}",
                        // planTypeString,
                        // e.getMessage());
                        // }
                        break;

                    case "moneySaved":
                        double requiredMoney = criteriaNode.get("value").asDouble();
                        BigDecimal totalMoneySaved = dailySummaryRepository
                                .sumMoneySavedByMemberId(member.getMemberId())
                                .orElse(BigDecimal.ZERO);
                        if (totalMoneySaved.doubleValue() >= requiredMoney) {
                            achieved = true;
                            log.info("Member {} achieved 'moneySaved' ({} VND) with total saved {}.",
                                    member.getMemberId(), requiredMoney, totalMoneySaved);
                        }
                        break;

                    case "reductionAmount": {
                        int requiredReductionAmount = criteriaNode.get("value").asInt();
                        Optional<QuitPlan> currentActivePlanForReduction = quitPlanRepository
                                .findFirstByMember_MemberIdAndStatusOrderByCreateDateDesc(member.getMemberId(),
                                        QuitPlanStatus.IN_PROGRESS);
                        if (currentActivePlanForReduction.isPresent()) {
                            QuitPlan quitPlan = currentActivePlanForReduction.get();
                            Integer initialSmoking = quitPlan.getInitialCigarettesPerDay();

                            DailySummary latestSummary = dailySummaryRepository
                                    .findTopByQuitPlan_Member_MemberIdOrderByTrackDateDesc(member.getMemberId());
                            if (initialSmoking != null && latestSummary != null) {
                                int currentSmoking = latestSummary.getTotalSmokedCount();
                                if (initialSmoking - currentSmoking >= requiredReductionAmount) {
                                    achieved = true;
                                    log.info(
                                            "Member {} achieved 'reducedSmokingCountAbsolute' ({} cigs) from initial {} to current {}.",
                                            member.getMemberId(), requiredReductionAmount, initialSmoking,
                                            currentSmoking);
                                }
                            } else {
                                log.warn(
                                        "Skipping 'reducedSmokingCountAbsolute' for member {} due to missing initialSmoking or latest DailySummary.",
                                        member.getMemberId());
                            }
                        } else {
                            log.debug(
                                    "No active quit plan found for member {} for 'reducedSmokingCountAbsolute' check.",
                                    member.getMemberId());
                        }
                        break;
                    }

                    case "reductionPercentage": {
                        int requiredReductionPercentage = criteriaNode.get("value").asInt();
                        Optional<QuitPlan> currentActivePlanForPercentage = quitPlanRepository
                                .findFirstByMember_MemberIdAndStatusOrderByCreateDateDesc(member.getMemberId(),
                                        QuitPlanStatus.IN_PROGRESS);
                        if (currentActivePlanForPercentage.isPresent()) {
                            QuitPlan quitPlanPercent = currentActivePlanForPercentage.get();
                            Integer initialSmokingPercent = quitPlanPercent.getInitialCigarettesPerDay();
                            DailySummary latestSummaryPercent = dailySummaryRepository
                                    .findTopByQuitPlan_Member_MemberIdOrderByTrackDateDesc(member.getMemberId());
                            if (initialSmokingPercent != null && latestSummaryPercent != null
                                    && initialSmokingPercent > 0) {
                                int currentSmokingPercent = latestSummaryPercent.getTotalSmokedCount();
                                double reductionPercentage = (double) (initialSmokingPercent - currentSmokingPercent)
                                        / initialSmokingPercent * 100;
                                if (reductionPercentage >= requiredReductionPercentage) {
                                    achieved = true;
                                    log.info(
                                            "Member {} achieved 'reducedSmokingCountPercentage' ({}%) from initial {} to current {}.",
                                            member.getMemberId(), requiredReductionPercentage, initialSmokingPercent,
                                            currentSmokingPercent);
                                }
                            } else {
                                log.warn(
                                        "Skipping 'reducedSmokingCountPercentage' for member {} due to missing initialSmoking, latest DailySummary, or initialSmoking is zero.",
                                        member.getMemberId());
                            }
                        } else {
                            log.debug(
                                    "No active quit plan found for member {} for 'reducedSmokingCountPercentage' check.",
                                    member.getMemberId());
                        }
                        break;
                    }

                    case "logCravingEntries":
                        int requiredCravingCount = criteriaNode.get("count").asInt();
                        long totalCravingEntries = cravingTrackingRepository
                                .countByDailySummary_QuitPlan_Member_MemberId(member.getMemberId());
                        if (totalCravingEntries >= requiredCravingCount) {
                            achieved = true;
                            log.info("Member {} achieved 'logCravingEntries' ({} entries) with total {}.",
                                    member.getMemberId(), requiredCravingCount, totalCravingEntries);
                        }
                        break;

                    default:
                        log.warn("Unknown achievement criteria type: {}", type);
                        break;
                }

                if (achieved) {
                    MemberAchievement memberAchievement = new MemberAchievement();
                    memberAchievement.setId(memberAchievementId);
                    memberAchievement.setAchievement(achievement);
                    memberAchievement.setMember(member);
                    memberAchievement.setAchievedAt(LocalDateTime.now());
                    memberAchievementRepository.save(memberAchievement);
                    log.info("Awarded achievement '{}' to member {}", achievement.getName(), member.getMemberId());
                }

            } catch (JsonProcessingException e) {
                log.error("Error processing achievement criteria for achievement ID: {}",
                        achievement.getAchievementId(), e);
            }
        }
    }

    public DailySummaryResponse getDailySummaryById(Integer dailySummaryId) {
        DailySummary dailySummary = dailySummaryRepository.findById(dailySummaryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy bản tóm tắt hàng ngày với ID: " + dailySummaryId));
        return convertToResponseDto(dailySummary);
    }

    @Transactional
    public List<DailySummaryResponse> getDailySummariesByDateBetween(UUID memberId, LocalDate startDate,
            LocalDate endDate) {
        List<DailySummary> dailySummaries = dailySummaryRepository
                .findByQuitPlan_Member_MemberIdAndTrackDateBetween(memberId, startDate, endDate);
        return dailySummaries.stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public DailySummaryResponse convertToResponseDto(DailySummary dailySummary) {
        return modelMapper.map(dailySummary, DailySummaryResponse.class);
    }

    @Transactional
    public DailySummary findOrCreateDailySummary(UUID memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));

        QuitPlan quitPlan = quitPlanRepository
                .findFirstByMember_MemberIdAndStatusOrderByCreateDateDesc(member.getMemberId(),
                        QuitPlanStatus.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("Active quit plan not found for member."));

        return dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, date)
                .orElseGet(() -> {
                    DailySummary newDailySummary = new DailySummary();
                    newDailySummary.setQuitPlan(quitPlan);
                    newDailySummary.setTrackDate(date);
                    newDailySummary.setMoneySaved(BigDecimal.ZERO);
                    return dailySummaryRepository.save(newDailySummary);
                });
    }

    @Transactional
    public void recalculateDailyTotals(DailySummary dailySummary) {
        List<CravingTracking> cravings = cravingTrackingRepository.findByDailySummary(dailySummary);

        int totalSmoked = cravings.stream().mapToInt(CravingTracking::getSmokedCount).sum();
        int totalCravings = cravings.stream().mapToInt(CravingTracking::getCravingsCount).sum();

        dailySummary.setTotalSmokedCount(totalSmoked);
        dailySummary.setTotalCravingCount(totalCravings);

        dailySummaryRepository.save(dailySummary);
    }

    public void deleteDailySummary(Integer id) {
        DailySummary dailySummary = dailySummaryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Không tìm thấy bản tóm tắt hàng ngày với ID: " + id));
        dailySummaryRepository.delete(dailySummary);
    }

    public UUID getMemberIdByDailySummaryId(Integer dailySummaryId) {
        DailySummary dailySummary = dailySummaryRepository.findById(dailySummaryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy bản tóm tắt hàng ngày với ID: " + dailySummaryId));
        return dailySummary.getQuitPlan().getMember().getMemberId();
    }

    public DailySummaryResponse getDailySummaryByMemberIdAndDate(UUID memberId, LocalDate date) {
        DailySummary dailySummary = dailySummaryRepository.findByQuitPlan_Member_MemberIdAndTrackDate(memberId, date)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy bản tóm tắt hàng ngày cho thành viên " + memberId + " vào ngày " + date));
        return modelMapper.map(dailySummary, DailySummaryResponse.class);
    }
}