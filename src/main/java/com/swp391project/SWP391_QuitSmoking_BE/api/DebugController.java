package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementService;
import com.swp391project.SWP391_QuitSmoking_BE.service.CravingTrackingService;
import com.swp391project.SWP391_QuitSmoking_BE.dto.craving.CravingTrackingCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
public class DebugController {
    private final AchievementService achievementService;
    private final CravingTrackingService cravingTrackingService;

    @PostMapping("/test-achievement/{memberId}")
    public ResponseEntity<String> testAchievementCheck(@PathVariable UUID memberId) {
        try {
            System.out.println("\n=== MANUAL DEBUG TEST ===");
            achievementService.checkAndUnlockAchievements(memberId);
            return ResponseEntity.ok("Debug achievement check completed for memberId: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/test-craving/{memberId}")
    public ResponseEntity<String> testCravingResist(@PathVariable UUID memberId, 
                                                   @RequestParam(defaultValue = "1") Integer cravingCount) {
        try {
            System.out.println("\n=== CRAVING RESIST TEST ===");
            
            CravingTrackingCreateRequest request = new CravingTrackingCreateRequest();
            request.setCravingsCount(cravingCount);
            request.setSmokedCount(0); // Resist = không hút
            
            cravingTrackingService.createOrUpdateTracking(memberId, request);
            
            return ResponseEntity.ok("Craving resist test completed for memberId: " + memberId + 
                                   " with " + cravingCount + " cravings resisted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/check-progress/{memberId}")
    public ResponseEntity<String> checkProgress(@PathVariable UUID memberId) {
        try {
            var daysQuit = achievementService.calculateDaysQuit(memberId);
            var moneySaved = achievementService.calculateMoneySaved(memberId);
            var cigarettesNotSmoked = achievementService.calculateCigarettesNotSmoked(memberId);
            var cravingResisted = achievementService.calculateCravingResisted(memberId);

            String progress = String.format(
                "Progress for memberId %s:\n" +
                "Days Quit: %s\n" +
                "Money Saved: %s\n" +
                "Cigarettes Not Smoked: %s\n" +
                "Cravings Resisted: %s",
                memberId, daysQuit, moneySaved, cigarettesNotSmoked, cravingResisted
            );

            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
