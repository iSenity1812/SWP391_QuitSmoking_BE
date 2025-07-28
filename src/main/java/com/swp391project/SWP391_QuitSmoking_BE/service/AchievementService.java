package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.event.UserResistedCravingEvent;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AchievementRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberAchievementRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import lombok.*;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
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

import com.swp391project.SWP391_QuitSmoking_BE.service.EmailService;
import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import com.swp391project.SWP391_QuitSmoking_BE.service.NotificationService;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class AchievementService {
    private final AchievementRepository achievementRepository;
    @Getter
    private final MemberAchievementRepository memberAchievementRepository;
    // Getter methods for testing
    @Getter
    private final MemberRepository memberRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final DailySummaryRepository dailySummaryRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @EventListener
    public void handleUserResistedCravingEvent(UserResistedCravingEvent event) {
        System.out.println("[AchievementService] Received UserResistedCravingEvent for user: " + event.getUser().getUserId());
        checkAndUnlockAchievements(event.getUser().getUserId());
    }

    @Transactional
    public void checkTimeBasedAchievements() {
        List<User> users = memberRepository.findAll().stream().map(Member::getUser).toList();
        for (User user : users) {
            checkAndUnlockAchievements(user.getUserId());
        }
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
        System.out.println("[AchievementService] Starting achievement check for memberId: " + memberId);
        
        // Tính toán các mốc hiện tại của member
        BigDecimal currentDaysQuit = calculateDaysQuit(memberId);
        BigDecimal currentMoneySaved = calculateMoneySaved(memberId);
        BigDecimal currentCigarettesNotSmoked = calculateCigarettesNotSmoked(memberId);
        BigDecimal currentCravingResisted = calculateCravingResisted(memberId);
        BigDecimal currentResilienceScore = calculateResilienceCount(memberId);
        BigDecimal currentHealthScore = calculateDaysQuit(memberId); // Health = days quit

        System.out.println("[AchievementService] Current progress - Days: " + currentDaysQuit + 
                         ", Money: " + currentMoneySaved + 
                         ", Cigarettes: " + currentCigarettesNotSmoked + 
                         ", Cravings: " + currentCravingResisted +
                         ", Resilience: " + currentResilienceScore +
                         ", Health: " + currentHealthScore);

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
                    case CRAVING_RESISTED:
                        shouldStillUnlock = currentCravingResisted.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case DAILY:
                        shouldStillUnlock = hasConsecutiveDailyAchievements(memberId, achievement.getMilestoneValue().intValue());
                        break;
                    case RESILIENCE:
                        shouldStillUnlock = currentResilienceScore.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case HEALTH:
                        shouldStillUnlock = currentHealthScore.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case SOCIAL:
                        shouldStillUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case SPECIAL:
                        shouldStillUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    default:
                        shouldStillUnlock = false;
                }
                if (!shouldStillUnlock) {
                    System.out.println("[AchievementService] XÓA thành tựu không hợp lệ: " + achievement.getName() + " (" + achievement.getMilestoneValue() + ") cho memberId: " + memberId + ". Số liệu hiện tại: daysQuit=" + currentDaysQuit + ", moneySaved=" + currentMoneySaved + ", cigarettesNotSmoked=" + currentCigarettesNotSmoked + ", cravingResisted=" + currentCravingResisted);
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
                    case CRAVING_RESISTED:
                        shouldUnlock = currentCravingResisted.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case DAILY:
                        shouldUnlock = hasConsecutiveDailyAchievements(memberId, achievement.getMilestoneValue().intValue());
                        break;
                    case RESILIENCE:
                        shouldUnlock = currentResilienceScore.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case HEALTH:
                        shouldUnlock = currentHealthScore.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case SOCIAL:
                        shouldUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case SPECIAL:
                        shouldUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    default:
                        shouldUnlock = false;
                }
                if (shouldUnlock) {
                    System.out.println("[AchievementService] Unlocking achievement: " + achievement.getName() + " for memberId: " + memberId);
                    unlockAchievement(memberId, achievement);
                }
            }
        }
        
        System.out.println("[AchievementService] Completed achievement check for memberId: " + memberId);
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
        // Gửi email chúc mừng
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member != null && member.getUser() != null && member.getUser().getEmail() != null) {
            String email = member.getUser().getEmail();
            String subject = "Chúc mừng bạn đã đạt thành tựu mới!";
            // Chuẩn bị biến động cho template
            Map<String, Object> templateVars = new java.util.HashMap<>();
            templateVars.put("username", member.getUser().getUsername());
            templateVars.put("achievementName", achievement.getName());
            templateVars.put("achievementDescription", achievement.getDescription());
            EmailDetail emailDetail = new EmailDetail(email, subject, null, "achievementTemplate.html", templateVars);
            emailService.sendEmail(emailDetail);
        }
        // Tạo notification achievement và gửi WebSocket real-time
        if (member != null && member.getUser() != null) {
            Notification notification = new Notification();
            notification.setUserId(member.getUser().getUserId());
            notification.setTitle("Chúc mừng!");
            notification.setContent("Bạn vừa đạt được thành tựu: " + achievement.getName());
            notification.setNotificationType("ACHIEVEMENT");
            notification.setFromUserId(null); // Hệ thống
            notificationService.createNotification(notification);
            
            // Gửi WebSocket notification real-time cho achievement
            try {
                notificationService.sendAchievementNotification(member.getUser().getUserId(), achievement);
                System.out.println("[AchievementService] Đã gửi WebSocket notification cho achievement: " + achievement.getName() + " tới user: " + member.getUser().getUserId());
            } catch (Exception e) {
                System.err.println("[AchievementService] Lỗi khi gửi WebSocket notification: " + e.getMessage());
            }
        }
    }

    // Calculation methods
    public BigDecimal calculateDaysQuit(UUID memberId) {
        // Lấy quit plan hiện tại của member
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findActiveQuitPlanByMemberId(memberId);
        
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

    public BigDecimal calculateMoneySaved(UUID memberId) {
        // Lấy quit plan hiện tại
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findActiveQuitPlanByMemberId(memberId);
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

    public BigDecimal calculateCigarettesNotSmoked(UUID memberId) {
        // Lấy quit plan hiện tại
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findActiveQuitPlanByMemberId(memberId);
        
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

    public BigDecimal calculateCravingResisted(UUID memberId) {
        // Lấy quit plan hiện tại
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findActiveQuitPlanByMemberId(memberId);
        
        if (quitPlanOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        QuitPlan quitPlan = quitPlanOpt.get();
        
        // Tính tổng số lần thèm thuốc từ daily summaries
        List<DailySummary> dailySummaries = dailySummaryRepository.findByQuitPlan_QuitPlanId(quitPlan.getQuitPlanId());
        
        int totalCravingCount = 0;
        for (DailySummary summary : dailySummaries) {
            totalCravingCount += summary.getTotalCravingCount();
        }

        return BigDecimal.valueOf(totalCravingCount);
    }

    public BigDecimal calculateResilienceCount(UUID memberId) {
        // RESILIENCE  Chỉ đếm số lần attempts
        // Loại bỏ logic score phức tạp, chỉ dựa trên số lần thử lại
        
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            return BigDecimal.ZERO;
        }
        
        List<QuitPlan> allQuitPlans = quitPlanRepository.findByMemberOrderByCreatedAtDesc(member);
        
        // Resilience = số lần attempts (ít nhất phải có 2 lần mới có resilience)
        int totalAttempts = allQuitPlans.size();
        if (totalAttempts <= 1) {
            return BigDecimal.ZERO;
        }
        
        // Đơn giản: mỗi lần attempt sau lần đầu = +1 điểm resilience
        return BigDecimal.valueOf(totalAttempts - 1);
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
            case CRAVING_RESISTED:
                return calculateCravingResisted(memberId);
            case RESILIENCE:
                return calculateResilienceCount(memberId);
            case HEALTH:
                return calculateDaysQuit(memberId); // Health improves with days quit
            case SOCIAL:
                return calculateDaysQuit(memberId); // Social milestones based on days quit
            case SPECIAL:
                return calculateDaysQuit(memberId); // Special achievements based on days quit
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


        // Craving Resisted Achievements
        createAchievement(new Achievement(null, "First Resistance", "/icons/5craving.png",
                "Resisted 5 cravings!", Achievement.AchievementType.CRAVING_RESISTED,
                new BigDecimal("5"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Craving Fighter", "/icons/10craving.png",
                "Resisted 10 cravings!", Achievement.AchievementType.CRAVING_RESISTED,
                new BigDecimal("10"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Willpower Warrior", "/icons/25craving.png",
                "Resisted 25 cravings!", Achievement.AchievementType.CRAVING_RESISTED,
                new BigDecimal("25"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Craving Conqueror", "/icons/50craving.png",
                "Resisted 50 cravings!", Achievement.AchievementType.CRAVING_RESISTED,
                new BigDecimal("50"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Temptation Master", "/icons/100craving.png",
                "Resisted 100 cravings!", Achievement.AchievementType.CRAVING_RESISTED,
                new BigDecimal("100"), LocalDateTime.now(), null));

        // Resilience Achievements - Based on number of attempts (simple logic)
        createAchievement(new Achievement(null, "Second Chance", "/icons/resilience-1.png",
                "Made your second attempt at quitting - that's resilience!", Achievement.AchievementType.RESILIENCE,
                new BigDecimal("1"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Comeback Kid", "/icons/resilience-2.png",
                "Third time's the charm! Your persistence is admirable!", Achievement.AchievementType.RESILIENCE,
                new BigDecimal("2"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Never Give Up", "/icons/resilience-3.png",
                "Four attempts and counting - you never give up!", Achievement.AchievementType.RESILIENCE,
                new BigDecimal("3"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Phoenix Rising", "/icons/resilience-4.png",
                "Fifth attempt shows true phoenix spirit!", Achievement.AchievementType.RESILIENCE,
                new BigDecimal("4"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Unbreakable Spirit", "/icons/resilience-5.png",
                "Six attempts or more - you have an unbreakable spirit!", Achievement.AchievementType.RESILIENCE,
                new BigDecimal("5"), LocalDateTime.now(), null));

        // Health Achievements - Based on days quit (health improvement milestones)
        createAchievement(new Achievement(null, "Fresh Breath", "/icons/health-breath.png",
                "3 days smoke-free: Your breath is getting fresher!", Achievement.AchievementType.HEALTH,
                new BigDecimal("3"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Taste Buds Awakening", "/icons/health-taste.png",
                "1 week smoke-free: Your taste buds are recovering!", Achievement.AchievementType.HEALTH,
                new BigDecimal("7"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Better Circulation", "/icons/health-circulation.png",
                "2 weeks smoke-free: Your circulation is improving!", Achievement.AchievementType.HEALTH,
                new BigDecimal("14"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Lung Recovery", "/icons/health-lungs.png",
                "1 month smoke-free: Your lungs are healing!", Achievement.AchievementType.HEALTH,
                new BigDecimal("30"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Heart Health Hero", "/icons/health-heart.png",
                "3 months smoke-free: Your heart health is significantly better!", Achievement.AchievementType.HEALTH,
                new BigDecimal("90"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Complete Recovery", "/icons/health-recovery.png",
                "1 year smoke-free: You've achieved major health recovery!", Achievement.AchievementType.HEALTH,
                new BigDecimal("365"), LocalDateTime.now(), null));

        // Social Achievements - Based on milestone sharing and community interaction
        createAchievement(new Achievement(null, "First Share", "/icons/social-share.png",
                "Share your first milestone with the community!", Achievement.AchievementType.SOCIAL,
                new BigDecimal("1"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Community Member", "/icons/social-member.png",
                "Active community participation for 7 days!", Achievement.AchievementType.SOCIAL,
                new BigDecimal("7"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Support Hero", "/icons/social-support.png",
                "Help others achieve their 30-day milestones!", Achievement.AchievementType.SOCIAL,
                new BigDecimal("30"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Community Leader", "/icons/social-leader.png",
                "Lead by example for 90 days in the community!", Achievement.AchievementType.SOCIAL,
                new BigDecimal("90"), LocalDateTime.now(), null));

        // Special Achievements - Based on perfect days and special milestones
        createAchievement(new Achievement(null, "Perfect Week", "/icons/special-week.png",
                "Complete a perfect week without any cigarettes!", Achievement.AchievementType.SPECIAL,
                new BigDecimal("7"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Monthly Champion", "/icons/special-month.png",
                "Achieve a perfect month - 30 days smoke-free!", Achievement.AchievementType.SPECIAL,
                new BigDecimal("30"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Quarterly Master", "/icons/special-quarter.png",
                "Complete a perfect quarter - 90 days smoke-free!", Achievement.AchievementType.SPECIAL,
                new BigDecimal("90"), LocalDateTime.now(), null));

        createAchievement(new Achievement(null, "Annual Legend", "/icons/special-year.png",
                "Achieve the ultimate goal - 365 days smoke-free!", Achievement.AchievementType.SPECIAL,
                new BigDecimal("365"), LocalDateTime.now(), null));
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
        Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findActiveQuitPlanByMemberId(memberId);
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
        BigDecimal currentResilienceScore = calculateResilienceCount(memberId);
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
                    case RESILIENCE:
                        shouldStillUnlock = currentResilienceScore.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case HEALTH:
                        shouldStillUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0; // Health uses days quit
                        break;
                    case SOCIAL:
                        shouldStillUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    case SPECIAL:
                        shouldStillUnlock = currentDaysQuit.compareTo(achievement.getMilestoneValue()) >= 0;
                        break;
                    default:
                        shouldStillUnlock = false;
                }
                if (!shouldStillUnlock) {
                    System.out.println("[AchievementService] XÓA thành tựu không hợp lệ (cleanInvalidAchievements): " + achievement.getName() + " (" + achievement.getMilestoneValue() + ") cho memberId: " + memberId + ". Số liệu hiện tại: daysQuit=" + currentDaysQuit + ", moneySaved=" + currentMoneySaved + ", cigarettesNotSmoked=" + currentCigarettesNotSmoked + ", resilience=" + currentResilienceScore);
                    memberAchievementRepository.delete(ma);
                    // Xóa notification liên quan đến achievement này
                    Member member = memberRepository.findById(memberId).orElse(null);
                    if (member != null && member.getUser() != null) {
                        String content = "Bạn vừa đạt được thành tựu: " + achievement.getName();
                        notificationService.deleteNotificationByUserIdAndContent(member.getUser().getUserId(), content);
                    }
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
        // Ưu tiên DAYS_QUIT trước, sau đó MONEY_SAVED, CIGARETTES_NOT_SMOKED, CRAVING_RESISTED, HEALTH, SOCIAL, SPECIAL, rồi RESILIENCE
        Achievement.AchievementType[] types = {Achievement.AchievementType.DAYS_QUIT, Achievement.AchievementType.MONEY_SAVED, Achievement.AchievementType.CIGARETTES_NOT_SMOKED, Achievement.AchievementType.CRAVING_RESISTED, Achievement.AchievementType.HEALTH, Achievement.AchievementType.SOCIAL, Achievement.AchievementType.SPECIAL, Achievement.AchievementType.RESILIENCE};
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