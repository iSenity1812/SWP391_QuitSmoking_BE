package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementService;
import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementService.AchievementDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/achievements")
@SecurityRequirement(name = "user_api")
public class AchievementController {
    @Autowired
    private AchievementService achievementService;

    // Lấy tất cả achievements (public)
    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Achievement>>> getAllAchievements() {
        try {
            List<Achievement> achievements = achievementService.getAllAchievements();
            return ResponseEntity.ok(ApiResponse.success(achievements));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching achievements: " + e.getMessage()));
        }
    }

    // Lấy achievement theo id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Achievement>> getAchievementById(@PathVariable Long id) {
        try {
            Optional<Achievement> achievement = achievementService.getAchievementById(id);
            if (achievement.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(achievement.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Achievement not found", "Achievement with ID " + id + " does not exist"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching achievement: " + e.getMessage()));
        }
    }

    // Tạo mới achievement (admin)
    @PostMapping("")
    public ResponseEntity<ApiResponse<Achievement>> createAchievement(@RequestBody Achievement achievement) {
        try {
            Achievement created = achievementService.createAchievement(achievement);
            return ResponseEntity.ok(ApiResponse.success(created));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error creating achievement: " + e.getMessage()));
        }
    }

    // Sửa achievement (admin)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Achievement>> updateAchievement(@PathVariable Long id, @RequestBody Achievement achievement) {
        try {
            Achievement updated = achievementService.updateAchievement(id, achievement);
            return ResponseEntity.ok(ApiResponse.success(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error updating achievement: " + e.getMessage()));
        }
    }

    // Xóa achievement (admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAchievement(@PathVariable Long id) {
        try {
            achievementService.deleteAchievement(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success("OK", "Achievement deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error deleting achievement: " + e.getMessage()));
        }
    }

    // Lấy achievements của member
    @GetMapping("/member/{memberId}")
    public ResponseEntity<ApiResponse<List<MemberAchievement>>> getAchievementsOfMember(@PathVariable UUID memberId) {
        try {
            List<MemberAchievement> achievements = achievementService.getAchievementsOfMember(memberId);
            return ResponseEntity.ok(ApiResponse.success(achievements));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching member achievements: " + e.getMessage()));
        }
    }

    // Lấy unlocked achievements của member
    @GetMapping("/member/{memberId}/unlocked")
    public ResponseEntity<ApiResponse<List<Achievement>>> getUnlockedAchievements(@PathVariable UUID memberId) {
        try {
            List<Achievement> achievements = achievementService.getUnlockedAchievements(memberId);
            return ResponseEntity.ok(ApiResponse.success(achievements));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching unlocked achievements: " + e.getMessage()));
        }
    }

    // Lấy locked achievements của member
    @GetMapping("/member/{memberId}/locked")
    public ResponseEntity<ApiResponse<List<Achievement>>> getLockedAchievements(@PathVariable UUID memberId) {
        try {
            List<Achievement> achievements = achievementService.getLockedAchievements(memberId);
            return ResponseEntity.ok(ApiResponse.success(achievements));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching locked achievements: " + e.getMessage()));
        }
    }

    // Gán achievement cho member (admin hoặc hệ thống tự động)
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<MemberAchievement>> assignAchievementToMember(
            @RequestParam UUID memberId, 
            @RequestParam Long achievementId, 
            @RequestParam(defaultValue = "false") boolean isShared) {
        try {
            MemberAchievement assigned = achievementService.assignAchievementToMember(memberId, achievementId, isShared);
            return ResponseEntity.ok(ApiResponse.success(assigned));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error assigning achievement: " + e.getMessage()));
        }
    }

    // Auto-unlock achievements cho member
    @PostMapping("/member/{memberId}/check-unlock")
    public ResponseEntity<ApiResponse<String>> checkAndUnlockAchievements(@PathVariable UUID memberId) {
        try {
            achievementService.checkAndUnlockAchievements(memberId);
            return ResponseEntity.ok(ApiResponse.success("","Achievement check completed successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error checking achievements: " + e.getMessage()));
        }
    }

    // Lấy progress hiện tại của member cho từng loại achievement
    @GetMapping("/member/{memberId}/progress")
    public ResponseEntity<ApiResponse<ProgressResponse>> getMemberProgress(@PathVariable UUID memberId) {
        try {
            BigDecimal daysQuit = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.DAYS_QUIT);
            BigDecimal moneySaved = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.MONEY_SAVED);
            BigDecimal cigarettesNotSmoked = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.CIGARETTES_NOT_SMOKED);
            BigDecimal cravingResisted = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.CRAVING_RESISTED);
            BigDecimal resilienceCount = achievementService.getCurrentProgress(memberId, Achievement.AchievementType.RESILIENCE);
            
            ProgressResponse response = new ProgressResponse(daysQuit, moneySaved, cigarettesNotSmoked, cravingResisted, resilienceCount);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching progress: " + e.getMessage()));
        }
    }

    // Response class for progress
    public static class ProgressResponse {
        private final BigDecimal daysQuit;
        private final BigDecimal moneySaved;
        private final BigDecimal cigarettesNotSmoked;
        private final BigDecimal cravingResisted;
        private final BigDecimal resilienceCount;

        public ProgressResponse(BigDecimal daysQuit, BigDecimal moneySaved, BigDecimal cigarettesNotSmoked, BigDecimal cravingResisted, BigDecimal resilienceCount) {
            this.daysQuit = daysQuit;
            this.moneySaved = moneySaved;
            this.cigarettesNotSmoked = cigarettesNotSmoked;
            this.cravingResisted = cravingResisted;
            this.resilienceCount = resilienceCount;
        }

        public BigDecimal getDaysQuit() { return daysQuit; }
        public BigDecimal getMoneySaved() { return moneySaved; }
        public BigDecimal getCigarettesNotSmoked() { return cigarettesNotSmoked; }
        public BigDecimal getCravingResisted() { return cravingResisted; }
        public BigDecimal getResilienceCount() { return resilienceCount; }
    }

    // Initialize default achievements (admin only)
    @PostMapping("/initialize")
    public ResponseEntity<ApiResponse<String>> initializeDefaultAchievements() {
        try {
            achievementService.initializeDefaultAchievements();
            return ResponseEntity.ok(ApiResponse.success("", "Default achievements initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error initializing achievements: " + e.getMessage()));
        }
    }

    // API trả về tất cả achievement + trạng thái completed cho user
    @GetMapping("/member/{memberId}/all")
    public ResponseEntity<ApiResponse<List<AchievementDTO>>> getAllAchievementsForUser(@PathVariable UUID memberId) {
        try {
            List<AchievementDTO> achievements = achievementService.getAllAchievementsForUser(memberId);
            return ResponseEntity.ok(ApiResponse.success(achievements));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching user achievements: " + e.getMessage()));
        }
    }

    // Test endpoint để debug
    @GetMapping("/test/{memberId}")
    public ResponseEntity<ApiResponse<String>> testEndpoint(@PathVariable UUID memberId) {
        try {
            System.out.println("=== TEST: Testing endpoint with memberId: " + memberId);
            
            // Test 1: Kiểm tra member có tồn tại không
            var memberOpt = achievementService.getMemberRepository().findById(memberId);
            if (memberOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Member not found", "Member not found with ID: " + memberId));
            }
            System.out.println("=== TEST: Member found: " + memberOpt.get().getMemberId());
            
            // Test 2: Kiểm tra achievements có tồn tại không
            var achievements = achievementService.getAllAchievements();
            System.out.println("=== TEST: Found " + achievements.size() + " achievements");
            
            // Test 3: Kiểm tra user achievements
            var userAchievements = achievementService.getMemberAchievementRepository().findByMember_MemberId(memberId);
            System.out.println("=== TEST: Found " + userAchievements.size() + " user achievements");
            
            return ResponseEntity.ok(ApiResponse.success("","All tests passed. Member: " + memberId +
                ", Achievements: " + achievements.size() + 
                ", User Achievements: " + userAchievements.size()));
                
        } catch (Exception e) {
            System.out.println("=== TEST ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Test failed: " + e.getMessage()));
        }
    }

    // API cho admin: Xóa tất cả thành tựu không còn đủ điều kiện cho user
    @DeleteMapping("/member/{memberId}/clean-invalid")
    public ResponseEntity<ApiResponse<String>> cleanInvalidAchievements(@PathVariable UUID memberId) {
        try {
            achievementService.cleanInvalidAchievements(memberId);
            return ResponseEntity.ok(ApiResponse.success("","Đã xóa các thành tựu không còn đủ điều kiện cho user: " + memberId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Lỗi khi xóa thành tựu không hợp lệ: " + e.getMessage()));
        }
    }

    // API trả về milestone/cột mốc tiếp theo cho user
    @GetMapping("/member/{memberId}/next-milestone")
    public ResponseEntity<ApiResponse<AchievementService.NextMilestoneDTO>> getNextMilestone(@PathVariable UUID memberId) {
        try {
            var milestone = achievementService.getNextMilestone(memberId);
            return ResponseEntity.ok(ApiResponse.success(milestone));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching next milestone: " + e.getMessage()));
        }
    }

    // API để lấy current progress cho các achievement type mới
    @GetMapping("/member/{memberId}/progress-detailed")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getDetailedProgress(@PathVariable UUID memberId) {
        try {
            Map<String, BigDecimal> progress = new HashMap<>();
            progress.put("daysQuit", achievementService.calculateDaysQuit(memberId));
            progress.put("moneySaved", achievementService.calculateMoneySaved(memberId));
            progress.put("cigarettesNotSmoked", achievementService.calculateCigarettesNotSmoked(memberId));
            progress.put("cravingResisted", achievementService.calculateCravingResisted(memberId));
            progress.put("resilienceCount", achievementService.calculateResilienceCount(memberId));
            
            return ResponseEntity.ok(ApiResponse.success(progress));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error fetching detailed progress: " + e.getMessage()));
        }
    }

    // API để check achievements by type
    @PostMapping("/member/{memberId}/check-by-type")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAchievementsByType(
            @PathVariable UUID memberId, 
            @RequestParam String type) {
        try {
            achievementService.checkAndUnlockAchievements(memberId);
            
            // Lấy achievements theo type
            List<Achievement> achievements = achievementService.getAllAchievements().stream()
                .filter(a -> a.getAchievementType().name().equals(type.toUpperCase()))
                .collect(Collectors.toList());
                
            Map<String, Object> response = new HashMap<>();
            response.put("type", type);
            response.put("achievements", achievements);
            response.put("currentProgress", achievementService.getCurrentProgress(memberId, Achievement.AchievementType.valueOf(type.toUpperCase())));
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Error checking achievements by type: " + e.getMessage()));
        }
    }
}