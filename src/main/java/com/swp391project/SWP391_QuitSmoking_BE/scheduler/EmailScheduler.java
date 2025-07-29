package com.swp391project.SWP391_QuitSmoking_BE.scheduler;

import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import com.swp391project.SWP391_QuitSmoking_BE.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class EmailScheduler {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final QuitPlanRepository quitPlanRepository;
    private final DailySummaryRepository dailySummaryRepository;

//    @Scheduled(cron = "0 0 8 * * *") // Run every day at 8:00 AM
    @Scheduled(fixedRate = 60000) // For testing, run every minute
    public void sendMotivationalEmails() {
        List<User> activeUsers = userRepository.findUsersWithActiveQuitPlans();
        for (User user : activeUsers) {
            Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(user.getMember().getMemberId(), QuitPlanStatus.IN_PROGRESS);
            if (quitPlanOpt.isPresent()) {
                QuitPlan quitPlan = quitPlanOpt.get();
                long daysQuit = ChronoUnit.DAYS.between(quitPlan.getStartDate(), LocalDate.now());
                long cigarettesNotSmoked = daysQuit * quitPlan.getInitialSmokingAmount();
                long moneySaved = cigarettesNotSmoked * quitPlan.getPricePerPack().longValue() / quitPlan.getCigarettesPerPack();

                Map<String, Object> templateVariables = new HashMap<>();
                templateVariables.put("username", user.getUsername());
                templateVariables.put("daysQuit", daysQuit);
                templateVariables.put("cigarettesNotSmoked", cigarettesNotSmoked);
                templateVariables.put("moneySaved", moneySaved);
                templateVariables.put("motivationalMessage", "You're doing great! Keep it up!"); // You can customize this message

                EmailDetail emailDetail = new EmailDetail(user.getEmail(), "Your Daily Motivation!", null, "dailyMotivationTemplate.html", templateVariables);
                emailService.sendEmail(emailDetail);
            }
        }
    }

//    @Scheduled(cron = "0 0 20 * * *") // Run every day at 8:00 PM
    @Scheduled(fixedRate = 60000) // For testing, run every minute
    public void sendReminderEmails() {
        List<User> activeUsers = userRepository.findUsersWithActiveQuitPlans();
        for (User user : activeUsers) {
            Optional<QuitPlan> quitPlanOpt = quitPlanRepository.findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(user.getMember().getMemberId(), QuitPlanStatus.IN_PROGRESS);
            if (quitPlanOpt.isPresent()) {
                QuitPlan quitPlan = quitPlanOpt.get();
                Optional<DailySummary> dailySummaryOpt = dailySummaryRepository.findByQuitPlanAndTrackDate(quitPlan, LocalDate.now());
                if (dailySummaryOpt.isEmpty()) {
                    Map<String, Object> templateVariables = new HashMap<>();
                    templateVariables.put("username", user.getUsername());
                    EmailDetail emailDetail = new EmailDetail(user.getEmail(), "Don't Forget to Check In!", null, "dailyReminderTemplate.html", templateVariables);
                    emailService.sendEmail(emailDetail);
                }
            }
        }
    }
}
