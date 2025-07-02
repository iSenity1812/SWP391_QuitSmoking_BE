package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AchievementRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberAchievementRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
public class AchievementService {
    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private MemberAchievementRepository memberAchievementRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private QuitPlanRepository quitPlanRepository;
    @Autowired
    private DailySummaryRepository dailySummaryRepository;

    // Getter methods for testing
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }

    public MemberAchievementRepository getMemberAchievementRepository() {
        return memberAchievementRepository;
    }

    // CRUD Achievement
    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }
    public Optional<Achievement> getAchievementById(Long id) {
        return achievementRepository.findById(id);
    }
    public Achievement createAchievement(Achievement achievement) {
        achievement.setCreatedAt(LocalDateTime.now());
        achievement.setUpdatedAt(LocalDateTime.now());
        return achievementRepository.save(achievement);
    }
    public Achievement updateAchievement(Long id, Achievement updated) {
        return achievementRepository.findById(id).map(a -> {
            a.setName(updated.getName());
            a.setIconUrl(updated.getIconUrl());
            a.setDescription(updated.getDescription());
            a.setUpdatedAt(LocalDateTime.now());
            return achievementRepository.save(a);
        }).orElseThrow();
    }
    public void deleteAchievement(Long id) {
        achievementRepository.deleteById(id);
    }

    // MemberAchievement
    public List<MemberAchievement> getMemberAchievements(UUID memberId) {
        return memberAchievementRepository.findByMember_MemberId(memberId);
    }

    public List<Achievement> getUnlockedAchievements(UUID memberId) {
        return achievementRepository.findUnlockedAchievementsByMember_MemberId(memberId);
    }

    public List<Achievement> getLockedAchievements(UUID memberId) {
        return achievementRepository.findLockedAchievementsByMember_MemberId(memberId);
    }

    // Method for backward compatibility
    public List<MemberAchievement> getAchievementsOfMember(UUID memberId) {
        return getMemberAchievements(memberId);
    }

    @Transactional
    public MemberAchievement assignAchievementToMember(UUID memberId, Long achievementId, boolean isShared) {
        MemberAchievement ma = new MemberAchievement();
        ma.setMemberId(memberId);
        ma.setAchievementId(achievementId);
        ma.setShared(isShared);
        return memberAchievementRepository.save(ma);
    }

    // Auto-unlock logic
    @Transactional
    public void checkAndUnlockAchievements(UUID memberId) {
        // Tính toán các mốc hiện tại của member
        BigDecimal currentDaysQuit = calculateDaysQuit(memberId);
        BigDecimal currentMoneySaved = calculateMoneySaved(memberId);
        BigDecimal currentCigarettesNotSmoked = calculateCigarettesNotSmoked(memberId);

        // Lấy tất cả achievements
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<MemberAchievement> userAchievements = memberAchievementRepository.findByMember_MemberId(memberId);

        // Xóa các thành tựu đã unlock nhưng không còn đủ điều kiện (do bug/test cũ)
        for (MemberAchievement ma : userAchievements) {
            Achievement achievement = allAchievements.stream()
                .filter(a -> a.getAchievementId().equals(ma.getAchievementId()))
                .findFirst().orElse(null);
            if (achievement != null) {
                boolean shouldStillUnlock = false;
                switch (achievement.getAchievementType()) {
                    case DAYS_QUIT:
                        shouldStillUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case MONEY_SAVED:
                        shouldStillUnlock = currentMoneySaved.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case CIGARETTES_NOT_SMOKED:
                        shouldStillUnlock = currentCigarettesNotSmoked.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case DAILY:
                        shouldStillUnlock = hasConsecutiveDailyAchievements(memberId, achievement.getMilestoneValue().intValue());
                        break;
                    default:
                        shouldStillUnlock = false;
                }
                if (!shouldStillUnlock) {
                    System.out.println("[AchievementService] XÓA thành tựu không hợp lệ: " + achievement.getName() + " (" + achievement.getMilestoneValue() + ") cho memberId: " + memberId + ". Số liệu hiện tại: daysQuit=" + currentDaysQuit + ", moneySaved=" + currentMoneySaved + ", cigarettesNotSmoked=" + currentCigarettesNotSmoked);
                    memberAchievementRepository.delete(ma);
                }
            }
        }

        // Sau khi làm sạch, tiến hành unlock các thành tựu đủ điều kiện
        for (Achievement achievement : allAchievements) {
            if (!isAchievementUnlocked(memberId, achievement.getAchievementId())) {
                boolean shouldUnlock = false;
                switch (achievement.getAchievementType()) {
                    case DAYS_QUIT:
                        shouldUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case MONEY_SAVED:
                        shouldUnlock = currentMoneySaved.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case CIGARETTES_NOT_SMOKED:
                        shouldUnlock = currentCigarettesNotSmoked.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case DAILY:
                        shouldUnlock = hasConsecutiveDailyAchievements(memberId, achievement.getMilestoneValue().intValue());
                        break;
                    default:
                        shouldUnlock = false;
                }
                if (shouldUnlock) {
                    unlockAchievement(memberId, achievement);
                }
            }
        }
    }

    private boolean isAchievementUnlocked(UUID memberId, Long achievementId) {
        return memberAchievementRepository.existsByMember_MemberIdAndAchievementId(memberId, achievementId);
    }

    private void unlockAchievement(UUID memberId, Achievement achievement) {
        MemberAchievement memberAchievement = new MemberAchievement();
        memberAchievement.setMemberId(memberId);
        memberAchievement.setAchievementId(achievement.getAchievementId());
        memberAchievement.setDateAchieved(LocalDateTime.now());
        memberAchievementRepository.save(memberAchievement);
    }

    // Calculation methods
    private BigDecimal calculateDaysQuit(UUID memberId) {
        // Lấy quit plan hiện tại của member
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);
        
        if (quitPlanOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        QuitPlan quitPlan = quitPlanOpt.get();
        LocalDateTime startDate = quitPlan.getStartDate();
        LocalDate today = LocalDate.now();

        // Tính số ngày từ ngày bắt đầu đến hôm nay
        long days = ChronoUnit.DAYS.between(startDate.toLocalDate(), today);
        return BigDecimal.valueOf(Math.max(0, days));
    }

    private BigDecimal calculateMoneySaved(UUID memberId) {
        // Lấy quit plan hiện tại
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);
        if (quitPlanOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }
        QuitPlan plan = quitPlanOpt.get();
        LocalDate startDate = plan.getStartDate().toLocalDate();
        int cigarettesPerDay = plan.getInitialSmokingAmount();
        int pricePerPack = plan.getPricePerPack().intValue();
        int cigarettesPerPack = plan.getCigarettesPerPack();
        int pricePerCigarette = pricePerPack / cigarettesPerPack;

        long days = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        long totalShouldSmoke = days * cigarettesPerDay;

        List<DailySummary> summaries = dailySummaryRepository.findByQuitPlan_QuitPlanId(plan.getQuitPlanId());
        long totalActualSmoked = summaries.stream().mapToLong(DailySummary::getTotalSmokedCount).sum();

        long avoided = totalShouldSmoke - totalActualSmoked;
        if (avoided < 0) avoided = 0;

        long moneySaved = avoided * pricePerCigarette;
        return BigDecimal.valueOf(moneySaved);
    }

    private BigDecimal calculateCigarettesNotSmoked(UUID memberId) {
        // Lấy quit plan hiện tại
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);
        
        if (quitPlanOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        QuitPlan quitPlan = quitPlanOpt.get();
        int initialSmokingAmount = quitPlan.getInitialSmokingAmount();

        // Tính tổng số điếu đã hút từ daily summaries
        List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan_QuitPlanId(quitPlan.getQuitPlanId());
        
        int totalSmoked = 0;
        for (DailySummary summary : dailySummaries) {
            totalSmoked += summary.getTotalSmokedCount();
        }

        // Tính số điếu không hút = số điếu ban đầu * số ngày - số điếu đã hút
        long daysQuit = ChronoUnit.DAYS.between(quitPlan.getStartDate().toLocalDate(), LocalDate.now());
        int expectedSmoked = (int) (initialSmokingAmount * daysQuit);
        int cigarettesNotSmoked = Math.max(0, expectedSmoked - totalSmoked);

        return BigDecimal.valueOf(cigarettesNotSmoked);
    }

    // Method to get current progress for each achievement type
    public BigDecimal getCurrentProgress(UUID memberId, Achievement.AchievementType type) {
        switch (type) {
            case DAYS_QUIT:
                return calculateDaysQuit(memberId);
            case MONEY_SAVED:
                return calculateMoneySaved(memberId);
            case CIGARETTES_NOT_SMOKED:
                return calculateCigarettesNotSmoked(memberId);
            default:
                return BigDecimal.ZERO;
        }
    }

    // Initialize default achievements
    @Transactional
    public void initializeDefaultAchievements() {
        if (achievementRepository.count() > 0) {
            return; // Already initialized
        }

        // Days Quit Achievements
        createAchievement(new Achievement(null, "3 Days Strong", "/icons/3days.png", 
            "You've been smoke-free for 3 days!", Achievement.AchievementType.DAYS_QUIT, 
            new BigDecimal("3"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "1 Week Warrior", "/icons/1week.png", 
            "A full week without smoking!", Achievement.AchievementType.DAYS_QUIT, 
            new BigDecimal("7"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "3 Weeks Champion", "/icons/3weeks.png", 
            "Three weeks of determination!", Achievement.AchievementType.DAYS_QUIT, 
            new BigDecimal("21"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "1 Month Hero", "/icons/1month.png", 
            "One month smoke-free!", Achievement.AchievementType.DAYS_QUIT, 
            new BigDecimal("30"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "3 Months Legend", "/icons/3months.png", 
            "Three months of freedom!", Achievement.AchievementType.DAYS_QUIT, 
            new BigDecimal("90"), LocalDateTime.now(), null));

        // Money Saved Achievements
        createAchievement(new Achievement(null, "100K Saver", "/icons/100k.png", 
            "Saved 100,000 VND!", Achievement.AchievementType.MONEY_SAVED, 
            new BigDecimal("100000"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "500K Master", "/icons/500k.png", 
            "Saved 500,000 VND!", Achievement.AchievementType.MONEY_SAVED, 
            new BigDecimal("500000"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "1M Millionaire", "/icons/1m.png", 
            "Saved 1,000,000 VND!", Achievement.AchievementType.MONEY_SAVED, 
            new BigDecimal("1000000"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "5M Tycoon", "/icons/5m.png", 
            "Saved 5,000,000 VND!", Achievement.AchievementType.MONEY_SAVED, 
            new BigDecimal("5000000"), LocalDateTime.now(), null));

        // Cigarettes Not Smoked Achievements
        createAchievement(new Achievement(null, "10 Cigarettes Free", "/icons/10cigs.png", 
            "Avoided 10 cigarettes!", Achievement.AchievementType.CIGARETTES_NOT_SMOKED, 
            new BigDecimal("10"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "50 Cigarettes Free", "/icons/50cigs.png", 
            "Avoided 50 cigarettes!", Achievement.AchievementType.CIGARETTES_NOT_SMOKED, 
            new BigDecimal("50"), LocalDateTime.now(), null));
        
        createAchievement(new Achievement(null, "200 Cigarettes Free", "/icons/200cigs.png", 
            "Avoided 200 cigarettes!", Achievement.AchievementType.CIGARETTES_NOT_SMOKED, 
            new BigDecimal("200"), LocalDateTime.now(), null));
    }

    // DTO trả về cho FE
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AchievementDTO {
        private Long id;
        private String name;
        private String description;
        private String iconUrl;
        private String achievementType;
        private BigDecimal milestoneValue;
        private boolean completed;
    }

    // Trả về tất cả achievement + trạng thái completed cho user
    public List<AchievementDTO> getAllAchievementsForUser(UUID memberId) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<MemberAchievement> userAchievements = memberAchievementRepository.findByMember_MemberId(memberId);
        Map<Long, MemberAchievement> userAchMap = userAchievements.stream()
            .collect(Collectors.toMap(MemberAchievement::getAchievementId, ma -> ma));
        List<AchievementDTO> result = new ArrayList<>();
        for (Achievement ach : allAchievements) {
            MemberAchievement ma = userAchMap.get(ach.getAchievementId());
            result.add(new AchievementDTO(
                ach.getAchievementId(),
                ach.getName(),
                ach.getDescription(),
                ach.getIconUrl(),
                ach.getAchievementType().name(),
                ach.getMilestoneValue(),
                ma != null
            ));
        }
        return result;
    }

    // Thêm hàm kiểm tra số ngày liên tiếp hoàn thành daily
    /**
     * Kiểm tra user đã hoàn thành liên tiếp requiredDays ngày với isGoalAchievedToday=true chưa
     */
    private boolean hasConsecutiveDailyAchievements(UUID memberId, int requiredDays) {
        // Lấy quit plan hiện tại
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId);
        if (quitPlanOpt.isEmpty()) return false;
        QuitPlan quitPlan = quitPlanOpt.get();
        // Lấy toàn bộ daily summary của quit plan này, sắp xếp giảm dần theo ngày
        List<DailySummary> summaries = dailySummaryRepository.findByQuitPlanOrderByTrackDateDesc(quitPlan);
        if (summaries.isEmpty()) return false;
        int count = 0;
        LocalDate prevDate = null;
        for (DailySummary summary : summaries) {
            if (!summary.isGoalAchievedToday()) {
                count = 0;
                prevDate = null;
                continue;
            }
            if (prevDate == null) {
                count = 1;
            } else {
                // Kiểm tra ngày có liên tiếp không
                if (summary.getTrackDate().plusDays(1).equals(prevDate)) {
                    count++;
                } else {
                    count = 1;
                }
            }
            prevDate = summary.getTrackDate();
            if (count >= requiredDays) return true;
        }
        return false;
    }

    /**
     * Xóa tất cả các thành tựu không còn đủ điều kiện cho memberId (dùng cho admin hoặc tự động làm sạch dữ liệu)
     */
    @Transactional
    public void cleanInvalidAchievements(UUID memberId) {
        BigDecimal currentDaysQuit = calculateDaysQuit(memberId);
        BigDecimal currentMoneySaved = calculateMoneySaved(memberId);
        BigDecimal currentCigarettesNotSmoked = calculateCigarettesNotSmoked(memberId);
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<MemberAchievement> userAchievements = memberAchievementRepository.findByMember_MemberId(memberId);
        for (MemberAchievement ma : userAchievements) {
            Achievement achievement = allAchievements.stream()
                .filter(a -> a.getAchievementId().equals(ma.getAchievementId()))
                .findFirst().orElse(null);
            if (achievement != null) {
                boolean shouldStillUnlock = false;
                switch (achievement.getAchievementType()) {
                    case DAYS_QUIT:
                        shouldStillUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case MONEY_SAVED:
                        shouldStillUnlock = currentMoneySaved.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case CIGARETTES_NOT_SMOKED:
                        shouldStillUnlock = currentCigarettesNotSmoked.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case DAILY:
                        shouldStillUnlock = hasConsecutiveDailyAchievements(memberId, achievement.getMilestoneValue().intValue());
                        break;
                    default:
                        shouldStillUnlock = false;
                }
                if (!shouldStillUnlock) {
                    System.out.println("[AchievementService] XÓA thành tựu không hợp lệ (cleanInvalidAchievements): " + achievement.getName() + " (" + achievement.getMilestoneValue() + ") cho memberId: " + memberId + ". Số liệu hiện tại: daysQuit=" + currentDaysQuit + ", moneySaved=" + currentMoneySaved + ", cigarettesNotSmoked=" + currentCigarettesNotSmoked);
                    memberAchievementRepository.delete(ma);
                }
            }
        }
    }

    /**
     * Trả về milestone/cột mốc tiếp theo cho user (ưu tiên DAYS_QUIT, MONEY_SAVED, CIGARETTES_NOT_SMOKED)
     */
    public NextMilestoneDTO getNextMilestone(UUID memberId) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<MemberAchievement> userAchievements = memberAchievementRepository.findByMember_MemberId(memberId);
        var unlockedIds = userAchievements.stream().map(MemberAchievement::getAchievementId).collect(Collectors.toSet());
        // Ưu tiên DAYS_QUIT trước, sau đó MONEY_SAVED, rồi CIGARETTES_NOT_SMOKED
        Achievement.AchievementType[] types = {Achievement.AchievementType.DAYS_QUIT, Achievement.AchievementType.MONEY_SAVED, Achievement.AchievementType.CIGARETTES_NOT_SMOKED};
        for (Achievement.AchievementType type : types) {
            var milestones = allAchievements.stream()
                .filter(a -> a.getAchievementType() == type)
                .sorted((a, b) -> a.getMilestoneValue().compareTo(b.getMilestoneValue()))
                .collect(Collectors.toList());
            BigDecimal current = getCurrentProgress(memberId, type);
            for (Achievement milestone : milestones) {
                if (!unlockedIds.contains(milestone.getAchievementId())) {
                    BigDecimal left = milestone.getMilestoneValue().subtract(current);
                    if (left.compareTo(BigDecimal.ZERO) < 0) left = BigDecimal.ZERO;
                    return new NextMilestoneDTO(
                        milestone.getName(),
                        left,
                        milestone.getDescription(),
                        type.name(),
                        milestone.getMilestoneValue()
                    );
                }
            }
        }
        // Nếu đã đạt hết, trả về null hoặc milestone cuối cùng
        return null;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NextMilestoneDTO {
        private String name;
        private BigDecimal left;
        private String reward;
        private String type;
        private BigDecimal milestoneValue;
    }
} 