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

        for (Achievement achievement : allAchievements) {
            // Kiểm tra xem achievement đã được unlock chưa
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
                        // TODO: Logic unlock daily achievement nếu có
                        break;
                    case RESILIENCE:
                        // TODO: Logic unlock resilience achievement nếu có
                        break;
                    case HEALTH:
                        // TODO: Logic unlock health achievement nếu có
                        break;
                    case SOCIAL:
                        // TODO: Logic unlock social achievement nếu có
                        break;
                    case SPECIAL:
                        // TODO: Logic unlock special achievement nếu có
                        break;
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

        QuitPlan quitPlan = quitPlanOpt.get();
        BigDecimal pricePerPack = quitPlan.getPricePerPack();
        int cigarettesPerPack = quitPlan.getCigarettesPerPack();
        int initialSmokingAmount = quitPlan.getInitialSmokingAmount();

        if (pricePerPack == null || cigarettesPerPack == 0) {
            return BigDecimal.ZERO;
        }

        // Tính số tiền tiết kiệm từ daily summaries
        List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan_QuitPlanId(quitPlan.getQuitPlanId());
        
        BigDecimal totalMoneySaved = BigDecimal.ZERO;
        for (DailySummary summary : dailySummaries) {
            if (summary.getMoneySaved() != null) {
                totalMoneySaved = totalMoneySaved.add(summary.getMoneySaved());
            }
        }

        return totalMoneySaved;
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
} 