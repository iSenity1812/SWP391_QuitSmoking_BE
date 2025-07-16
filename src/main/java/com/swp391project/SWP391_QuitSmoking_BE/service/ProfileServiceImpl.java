package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.profile.PremiumProfileDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.profile.NormalProfileDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService{
    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private final QuitPlanRepository quitPlanRepository;
    private final FollowRepository followRepository;
    private final ProfileRepository profileRepository;
    private final DailySummaryRepository dailySummaryRepository;
    private final CravingTrackingRepository cravingTrackingRepository;
    private final CravingTrackingService cravingTrackingService;
    private final SubscriptionRepository subscriptionRepository;
    private final MemberAchievementRepository memberAchievementRepository;

    @Override
    public Object getMyProfile(UUID userId) {
        User user = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));
        if (user.getRole() == Role.PREMIUM_MEMBER) {
            return getPremiumProfile(user);
        } else if (user.getRole() == Role.NORMAL_MEMBER) {
            return getNormalProfile(user);
        } else {
            throw new RuntimeException("Unsupported user role: " + user.getRole());
        }
    }

    private PremiumProfileDTO getPremiumProfile(User user) {
        Member member = user.getMember();
        if (member == null) throw new RuntimeException("Member not found for user with ID " + user.getUserId());

        PremiumProfileDTO.PremiumProfileDTOBuilder builder = PremiumProfileDTO.builder();

        // Basic Information
        builder.userId(user.getUserId().toString());
        builder.username(user.getUsername());
        builder.email(user.getEmail());
        builder.profilePicture(user.getProfilePicture());
        builder.role(user.getRole().name());
        builder.accountCreationDate(Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        builder.currentStreakCount(member.getStreak());

        // Quit Plan Details
        quitPlanRepository.findActiveQuitPlanByMember(member).ifPresent(quitPlan -> {
            builder.currentQuitPlanId(quitPlan.getQuitPlanId());
            builder.quitPlanStatus(quitPlan.getStatus().name());
            long daysCompleted = ChronoUnit.DAYS.between(quitPlan.getStartDate().toLocalDate(), LocalDate.now());
            long totalDays = ChronoUnit.DAYS.between(quitPlan.getStartDate().toLocalDate(), quitPlan.getGoalDate());
            if (totalDays > 0) {
                double progress = (double) daysCompleted / totalDays * 100;
                builder.progressSnapshot(String.format("%.2f%% Complete", progress));
            }
        });

        // Quit Statistics
        List<DailySummary> activePlanSummaries = dailySummaryRepository.findByQuitPlan(quitPlanRepository.findActiveQuitPlanByMember(member)
                .orElseThrow(() -> new RuntimeException("No active quit plan found for member with ID " + member.getMemberId())));
        long totalCigarettesSmoked = activePlanSummaries.stream().mapToLong(DailySummary::getTotalSmokedCount).sum();
        builder.totalCigarettesSmokedSinceStart(totalCigarettesSmoked);

         // Tính toán số thuốc lá đã tránh được: (init smoking amount * days from quit plan start to now) - totalCigarettesSmoked
        // Tính số thuốc lá đã tránh được
        quitPlanRepository.findActiveQuitPlanByMember(member).ifPresent(quitPlan -> {
            // Tính số ngày từ khi bắt đầu quit plan đến hiện tại
            long daysSinceStart = ChronoUnit.DAYS.between(quitPlan.getStartDate().toLocalDate(), LocalDate.now()) + 1; // +1 để bao gồm ngày bắt đầu
            log.info("Days since start: {}", daysSinceStart);

            // Lấy số thuốc hút ban đầu mỗi ngày
            int initialSmokingAmount = quitPlan.getInitialSmokingAmount();

            if(daysSinceStart > 0) {
                // Công thức: (số thuốc hút ban đầu * số ngày từ khi bắt đầu) - tổng số thuốc đã hút thực tế
                log.info("Initial smoking amount: {}, Total cigarettes smoked: {}", initialSmokingAmount, totalCigarettesSmoked);
                long cigarettesAvoided = (initialSmokingAmount * daysSinceStart) - totalCigarettesSmoked;

                // Tính số ngày không hút thuốc
                /*
                 *  Ngày 1: Hút 2 điếu  → Streak = 0, DaysWithoutSmoking = 0
                 * Ngày 2: Không hút   → Streak = 1, DaysWithoutSmoking = 1
                 * Ngày 3: Hút 1 điếu  → Streak = 0, DaysWithoutSmoking = 1
                 * Ngày 4: Không hút   → Streak = 1, DaysWithoutSmoking = 2
                 * Ngày 5: Không hút   → Streak = 2, DaysWithoutSmoking = 3
                 * */
                long daysWithSmoking = activePlanSummaries.stream()
                    .filter(summary -> summary.getTotalSmokedCount() > 0)
                    .count();
                long daysWithoutSmoking = daysSinceStart - daysWithSmoking;
                log.info("Days with smoking: {}", daysWithSmoking);
                log.info("Days without smoking: {}", daysWithoutSmoking);
                builder.daysWithoutSmoking(daysWithoutSmoking);
                log.info("Cigarettes avoided: {}", cigarettesAvoided);


                // Đảm bảo số thuốc tránh được không âm
                cigarettesAvoided = Math.max(0, cigarettesAvoided);
                builder.cigarettesAvoided(cigarettesAvoided);

                // Tính tiền tiết kiệm được (nếu có thông tin giá)
//                if (quitPlan.getPricePerPack() != null) {
//                    double moneySaved = cigarettesAvoided * (quitPlan.getPricePerPack().doubleValue() / quitPlan.getCigarettesPerPack()); // cigarettes avoid * (price per pack / cigarettes per pack )
//                    builder.moneySaved(BigDecimal.valueOf(moneySaved));
//                }


                // 1. MONEY SAVED HÔM NAY
                LocalDate today = LocalDate.now();
                Optional<DailySummary> todaySummary = activePlanSummaries.stream()
                        .filter(summary -> summary.getTrackDate().isEqual(today))
                        .findFirst();

                if (todaySummary.isPresent()) {
                    builder.moneySaved(todaySummary.get().getMoneySaved() != null ?
                            todaySummary.get().getMoneySaved() : BigDecimal.ZERO);
                } else {
                    builder.moneySaved(BigDecimal.ZERO); // Chưa có daily summary hôm nay
                }

//                2. TOTAL MONEY SAVED
                // Tính tổng tiền đã tiết kiệm được trên toàn bộ quitplan của người dùng đó (sử dụng moneuSaved -> total trong daily summaries)
                BigDecimal totalMoneySaved = dailySummaryRepository.findByQuitPlan_Member(member).stream()
                        .map(DailySummary::getMoneySaved)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                builder.totalMoneySaved(totalMoneySaved);


            } else {
                builder.cigarettesAvoided(0L);
                builder.moneySaved(BigDecimal.ZERO);
            }
            long totalCravings = activePlanSummaries.stream()
                    .mapToLong(summary -> summary.getTotalCravingCount() != 0 ? summary.getTotalCravingCount() : 0)
                    .sum();
            builder.totalCravings(totalCravings);
            if (daysSinceStart > 0) {
                double averageDailyCravings = (double) totalCravings / daysSinceStart;
                builder.averageDailyCravings(Math.round(averageDailyCravings * 100.0) / 100.0);
            } else {
                builder.averageDailyCravings(0.0);
            }

            // Chart Data - 7 days gần nhất
            LocalDate today = LocalDate.now();
            LocalDate weekAgo = today.minusDays(6); // 7 ngày: hôm nay + 6 ngày trước

            List<DailySummary> chartSummaries = dailySummaryRepository.findByQuitPlanAndTrackDateBetween(quitPlan, weekAgo, today);

            // Tạo map để lookup nhanh
            Map<LocalDate, DailySummary> summaryMap = chartSummaries.stream()
                    .collect(Collectors.toMap(DailySummary::getTrackDate, summary -> summary));

            List<PremiumProfileDTO.DailyChartData> chartData = new ArrayList<>();

// Tạo data cho từng ngày trong 7 ngày (từ cũ nhất đến mới nhất)
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                DailySummary summary = summaryMap.get(date);

                chartData.add(PremiumProfileDTO.DailyChartData.builder()
                        .date(date)
                        .cigarettesSmoked(summary != null ? summary.getTotalSmokedCount() : 0)
                        .cravings(summary != null && summary.getTotalCravingCount() != 0 ?
                                summary.getTotalCravingCount() : 0)
                        .build());
            }

            builder.dailyChartData(chartData);

        });



//        List<DailySummary> dailySummaries = dailySummaryRepository.findTop7ByQuitPlan_MemberOrderByTrackDateDesc(member);
//        List<PremiumProfileDTO.DailyChartData> chartData = dailySummaries.stream()
//                .map(summary -> {
////                    long cravings = cravingTrackingRepository.countByDailySummary_QuitPlan_MemberAndTrackTimeBetween(member, summary.getTrackDate().atStartOfDay(), summary.getTrackDate().atTime(23, 59, 59));
//                    return PremiumProfileDTO.DailyChartData.builder()
//                            .date(summary.getTrackDate())
//                            .cigarettesSmoked(summary.getTotalSmokedCount())
//                            .cravings(summary.getTotalCravingCount() != 0 ? summary.getTotalCravingCount() : 0)
//                            .build();
//                })
//                .toList();
//        builder.dailyChartData(chartData);

        // Follow Statistics
        builder.followersCount(followRepository.countByFollowedId(user.getUserId()));
        builder.followingCount(followRepository.countByFollowerId(user.getUserId()));

        // Subscription Information
        subscriptionRepository.findFirstByUserOrderByEndDateDesc(user).ifPresent(subscription -> {
            builder.subscriptionId(subscription.getSubscriptionId());
            builder.packageName(subscription.getPlan().getPlanName().name());
            builder.price(subscription.getPlan().getPrice());
            builder.subscriptionStartDate(Date.from(subscription.getStartDate().atZone(ZoneId.systemDefault()).toInstant()));
            builder.subscriptionEndDate(Date.from(subscription.getEndDate().atZone(ZoneId.systemDefault()).toInstant()));
            builder.daysRemaining(ChronoUnit.DAYS.between(LocalDate.now(), subscription.getEndDate()));
            builder.subscriptionStatus(subscription.isActive() ? "Active" : "Inactive");
        });

        // Achievements
        List<MemberAchievement> memberAchievements = memberAchievementRepository.findTop5ByMemberOrderByDateAchievedDesc(member);
        List<PremiumProfileDTO.AchievementDTO> achievementDTOs = memberAchievements.stream()
                .map(memberAchievement -> {
                    LocalDateTime localDateTime = memberAchievement.getDateAchieved();
                    Date dateAchieved = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                    return PremiumProfileDTO.AchievementDTO.builder()
                            .id(memberAchievement.getAchievement().getAchievementId())
                            .name(memberAchievement.getAchievement().getName())
                            .iconUrl(memberAchievement.getAchievement().getIconUrl())
                            .detailedDescription(memberAchievement.getAchievement().getDescription())
                            .dateAchieved(dateAchieved) // Gán đối tượng Date đã được chuyển đổi
                            .isShared(memberAchievement.isShared())
                            .build();
                })
                .collect(Collectors.toList());
        builder.last5Achievements(achievementDTOs);

        return builder.build();
    }

    private NormalProfileDTO getNormalProfile(User user) {
        Member member = user.getMember();
        if (member == null) throw new RuntimeException("Member not found for user with ID " + user.getUserId());

        NormalProfileDTO.NormalProfileDTOBuilder builder = NormalProfileDTO.builder();

        // Core Information
        builder.userId(user.getUserId().toString());
        builder.username(user.getUsername());
        builder.email(user.getEmail());
        builder.profilePicture(user.getProfilePicture());
        builder.role(user.getRole().name());
        builder.accountCreationDate(Date.from(user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        builder.currentStreakCount(member.getStreak());

        // Quit Plan Details
        quitPlanRepository.findActiveQuitPlanByMember(member).ifPresent(quitPlan -> {
            builder.currentQuitPlanId(quitPlan.getQuitPlanId());
            builder.quitPlanStatus(quitPlan.getStatus().name());
        });

        // Quit Statistics (simplified version for normal members)
        quitPlanRepository.findActiveQuitPlanByMember(member).ifPresent(quitPlan -> {
            List<DailySummary> activePlanSummaries = dailySummaryRepository.findByQuitPlan(quitPlan);
            long totalCigarettesSmoked = activePlanSummaries.stream().mapToLong(DailySummary::getTotalSmokedCount).sum();

            // Tính số ngày từ khi bắt đầu quit plan đến hiện tại
            long daysSinceStart = ChronoUnit.DAYS.between(quitPlan.getStartDate().toLocalDate(), LocalDate.now()) + 1;
            
            if (daysSinceStart > 0) {
                // Lấy số thuốc hút ban đầu mỗi ngày
                int initialSmokingAmount = quitPlan.getInitialSmokingAmount();
                
                // Công thức: (số thuốc hút ban đầu * số ngày từ khi bắt đầu) - tổng số thuốc đã hút thực tế
                long cigarettesAvoided = (initialSmokingAmount * daysSinceStart) - totalCigarettesSmoked;
                cigarettesAvoided = Math.max(0, cigarettesAvoided);
                builder.cigarettesAvoided(cigarettesAvoided);

                // Tính số ngày không hút thuốc
                long daysWithSmoking = activePlanSummaries.stream()
                    .filter(summary -> summary.getTotalSmokedCount() > 0)
                    .count();
                long daysWithoutSmoking = daysSinceStart - daysWithSmoking;
                builder.daysWithoutSmoking(daysWithoutSmoking);

                // Money saved hôm nay (chỉ hiển thị tiền tiết kiệm được hôm nay, không phải tổng)
                LocalDate today = LocalDate.now();
                Optional<DailySummary> todaySummary = activePlanSummaries.stream()
                        .filter(summary -> summary.getTrackDate().isEqual(today))
                        .findFirst();

                builder.moneySaved(todaySummary.filter(summary -> summary.getMoneySaved() != null).map(DailySummary::getMoneySaved).orElse(BigDecimal.ZERO));

                // Chart Data - 7 days gần nhất (chỉ hiển thị cigarettes smoked, không có cravings)
                LocalDate weekAgo = today.minusDays(6);
                List<DailySummary> chartSummaries = dailySummaryRepository.findByQuitPlanAndTrackDateBetween(quitPlan, weekAgo, today);

                // Tạo map để lookup nhanh
                Map<LocalDate, DailySummary> summaryMap = chartSummaries.stream()
                        .collect(Collectors.toMap(DailySummary::getTrackDate, summary -> summary));

                List<NormalProfileDTO.DailyChartData> chartData = new ArrayList<>();

                // Tạo data cho từng ngày trong 7 ngày (từ cũ nhất đến mới nhất)
                for (int i = 6; i >= 0; i--) {
                    LocalDate date = today.minusDays(i);
                    DailySummary summary = summaryMap.get(date);

                    chartData.add(NormalProfileDTO.DailyChartData.builder()
                            .date(date)
                            .cigarettesSmoked(summary != null ? summary.getTotalSmokedCount() : 0)
                            .build());
                }

                builder.dailyChartData(chartData);
            } else {
                builder.cigarettesAvoided(0L);
                builder.daysWithoutSmoking(0L);
                builder.moneySaved(BigDecimal.ZERO);
                builder.dailyChartData(new ArrayList<>());
            }
        });

        // Follow Statistics
        builder.followersCount(followRepository.countByFollowedId(user.getUserId()));
        builder.followingCount(followRepository.countByFollowerId(user.getUserId()));

        // Achievements (chỉ lấy 3 achievements gần nhất cho normal member)
        List<MemberAchievement> memberAchievements = memberAchievementRepository.findTop3ByMemberOrderByDateAchievedDesc(member);
        List<NormalProfileDTO.AchievementDTO> achievementDTOs = memberAchievements.stream()
                .map(memberAchievement -> {
                    LocalDateTime localDateTime = memberAchievement.getDateAchieved();
                    Date dateAchieved = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                    return NormalProfileDTO.AchievementDTO.builder()
                            .id(memberAchievement.getAchievement().getAchievementId())
                            .name(memberAchievement.getAchievement().getName())
                            .iconUrl(memberAchievement.getAchievement().getIconUrl())
                            .dateAchieved(dateAchieved)
                            .build();
                })
                .collect(Collectors.toList());
        builder.last3Achievements(achievementDTOs);

        return builder.build();
    }
}
