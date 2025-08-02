package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementTriggerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievement-trigger")
@RequiredArgsConstructor
public class AchievementTriggerController {
    private final AchievementTriggerService achievementTriggerService;

    @PostMapping("/manual/{memberId}")
    public ResponseEntity<String> triggerManualCheck(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.triggerAchievementCheck(memberId, "MANUAL_TRIGGER");
            return ResponseEntity.ok("Achievement check triggered successfully for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering achievement check: " + e.getMessage());
        }
    }

    @PostMapping("/craving-resisted/{memberId}")
    public ResponseEntity<String> triggerCravingResisted(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onCravingResisted(memberId);
            return ResponseEntity.ok("Craving resisted trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering craving resisted: " + e.getMessage());
        }
    }

    @PostMapping("/daily-goal-completed/{memberId}")
    public ResponseEntity<String> triggerDailyGoalCompleted(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onDailyGoalCompleted(memberId);
            return ResponseEntity.ok("Daily goal completed trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering daily goal completed: " + e.getMessage());
        }
    }

    @PostMapping("/smoking-data-updated/{memberId}")
    public ResponseEntity<String> triggerSmokingDataUpdated(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onSmokingDataUpdated(memberId);
            return ResponseEntity.ok("Smoking data updated trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering smoking data updated: " + e.getMessage());
        }
    }

    @PostMapping("/daily-summary-added/{memberId}")
    public ResponseEntity<String> triggerDailySummaryAdded(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onDailySummaryAdded(memberId);
            return ResponseEntity.ok("Daily summary added trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering daily summary added: " + e.getMessage());
        }
    }

    @PostMapping("/user-login/{memberId}")
    public ResponseEntity<String> triggerUserLogin(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onUserLogin(memberId);
            return ResponseEntity.ok("User login trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering user login: " + e.getMessage());
        }
    }

    @PostMapping("/achievement-page-refresh/{memberId}")
    public ResponseEntity<String> triggerAchievementPageRefresh(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onAchievementPageRefresh(memberId);
            return ResponseEntity.ok("Achievement page refresh trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering achievement page refresh: " + e.getMessage());
        }
    }

    @PostMapping("/profile-update/{memberId}")
    public ResponseEntity<String> triggerProfileUpdate(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onProfileUpdate(memberId);
            return ResponseEntity.ok("Profile update trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering profile update: " + e.getMessage());
        }
    }

    @PostMapping("/quit-plan-change/{memberId}")
    public ResponseEntity<String> triggerQuitPlanChange(@PathVariable UUID memberId) {
        try {
            achievementTriggerService.onQuitPlanChange(memberId);
            return ResponseEntity.ok("Quit plan change trigger sent for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error triggering quit plan change: " + e.getMessage());
        }
    }
}
