package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementService;
import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementService.AchievementDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/achievements")
public class AchievementController {
    @Autowired
    private AchievementService achievementService;

    // Lấy tất cả achievements (public)
    @GetMapping("")
    public List<Achievement> getAllAchievements() {
        return achievementService.getAllAchievements();
    }

    // Lấy achievement theo id
    @GetMapping("/{id}")
    public ResponseEntity<Achievement> getAchievementById(@PathVariable Long id) {
        Optional<Achievement> achievement = achievementService.getAchievementById(id);
        return achievement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Tạo mới achievement (admin)
    @PostMapping("")
    public Achievement createAchievement(@RequestBody Achievement achievement) {
        return achievementService.createAchievement(achievement);
    }

    // Sửa achievement (admin)
    @PutMapping("/{id}")
    public ResponseEntity<Achievement> updateAchievement(@PathVariable Long id, @RequestBody Achievement achievement) {
        try {
            return ResponseEntity.ok(achievementService.updateAchievement(id, achievement));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Xóa achievement (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Long id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy achievements của member
    @GetMapping("/member/{memberId}")
    public List<MemberAchievement> getAchievementsOfMember(@PathVariable UUID memberId) {
        return achievementService.getAchievementsOfMember(memberId);
    }

    // Lấy unlocked achievements của member
    @GetMapping("/member/{memberId}/unlocked")
    public List<Achievement> getUnlockedAchievements(@PathVariable UUID memberId) {
        return achievementService.getUnlockedAchievements(memberId);
    }

    // Lấy locked achievements của member
    @GetMapping("/member/{memberId}/locked")
    public List<Achievement> getLockedAchievements(@PathVariable UUID memberId) {
        return achievementService.getLockedAchievements(memberId);
    }

    // Gán achievement cho member (admin hoặc hệ thống tự động)
    @PostMapping("/assign")
    public MemberAchievement assignAchievementToMember(@RequestParam UUID memberId, @RequestParam Long achievementId, @RequestParam(defaultValue = "false") boolean isShared) {
        return achievementService.assignAchievementToMember(memberId, achievementId, isShared);
    }

    // Auto-unlock achievements cho member
    @PostMapping("/member/{memberId}/check-unlock")
    public ResponseEntity<String> checkAndUnlockAchievements(@PathVariable UUID memberId) {
        try {
            achievementService.checkAndUnlockAchievements(memberId);
            return ResponseEntity.ok("Achievement check completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking achievements: " + e.getMessage());
        }
    }

    // Lấy progress hiện tại của member cho từng loại achievement
    @GetMapping("/member/{memberId}/progress")
    public ResponseEntity<ProgressResponse> getMemberProgress(@PathVariable UUID memberId) {
        try {
            BigDecimal daysQuit = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.DAYS_QUIT);
            BigDecimal moneySaved = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.MONEY_SAVED);
            BigDecimal cigarettesNotSmoked = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.CIGARETTES_NOT_SMOKED);
            
            ProgressResponse response = new ProgressResponse(daysQuit, moneySaved, cigarettesNotSmoked);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Response class for progress
    public static class ProgressResponse {
        private final BigDecimal daysQuit;
        private final BigDecimal moneySaved;
        private final BigDecimal cigarettesNotSmoked;

        public ProgressResponse(BigDecimal daysQuit, BigDecimal moneySaved, BigDecimal cigarettesNotSmoked) {
            this.daysQuit = daysQuit;
            this.moneySaved = moneySaved;
            this.cigarettesNotSmoked = cigarettesNotSmoked;
        }

        public BigDecimal getDaysQuit() { return daysQuit; }
        public BigDecimal getMoneySaved() { return moneySaved; }
        public BigDecimal getCigarettesNotSmoked() { return cigarettesNotSmoked; }
    }

    // Initialize default achievements (admin only)
    @PostMapping("/initialize")
    public ResponseEntity<String> initializeDefaultAchievements() {
        try {
            achievementService.initializeDefaultAchievements();
            return ResponseEntity.ok("Default achievements initialized successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error initializing achievements: " + e.getMessage());
        }
    }

    // API trả về tất cả achievement + trạng thái completed cho user
    @GetMapping("/member/{memberId}/all")
    public ResponseEntity<?> getAllAchievementsForUser(@PathVariable UUID memberId) {
        try {
            return ResponseEntity.ok(achievementService.getAllAchievementsForUser(memberId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    // Test endpoint để debug
    @GetMapping("/test/{memberId}")
    public ResponseEntity<String> testEndpoint(@PathVariable UUID memberId) {
        try {
            System.out.println("=== TEST: Testing endpoint with memberId: " + memberId);
            
            // Test 1: Kiểm tra member có tồn tại không
            var memberOpt = achievementService.getMemberRepository().findById(memberId);
            if (memberOpt.isEmpty()) {
                return ResponseEntity.ok("ERROR: Member not found with ID: " + memberId);
            }
            System.out.println("=== TEST: Member found: " + memberOpt.get().getMemberId());
            
            // Test 2: Kiểm tra achievements có tồn tại không
            var achievements = achievementService.getAllAchievements();
            System.out.println("=== TEST: Found " + achievements.size() + " achievements");
            
            // Test 3: Kiểm tra user achievements
            var userAchievements = achievementService.getMemberAchievementRepository().findByMember_MemberId(memberId);
            System.out.println("=== TEST: Found " + userAchievements.size() + " user achievements");
            
            return ResponseEntity.ok("SUCCESS: All tests passed. Member: " + memberId + 
                ", Achievements: " + achievements.size() + 
                ", User Achievements: " + userAchievements.size());
                
        } catch (Exception e) {
            System.out.println("=== TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok("ERROR: " + e.getMessage());
        }
    }

    // API cho admin: Xóa tất cả thành tựu không còn đủ điều kiện cho user
    @DeleteMapping("/member/{memberId}/clean-invalid")
    public ResponseEntity<String> cleanInvalidAchievements(@PathVariable UUID memberId) {
        try {
            achievementService.cleanInvalidAchievements(memberId);
            return ResponseEntity.ok("Đã xóa các thành tựu không còn đủ điều kiện cho user: " + memberId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi xóa thành tựu không hợp lệ: " + e.getMessage());
        }
    }

    // API trả về milestone/cột mốc tiếp theo cho user
    @GetMapping("/member/{memberId}/next-milestone")
    public ResponseEntity<?> getNextMilestone(@PathVariable UUID memberId) {
        try {
            var milestone = achievementService.getNextMilestone(memberId);
            if (milestone == null) return ResponseEntity.ok().body(null);
            return ResponseEntity.ok(milestone);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
} 