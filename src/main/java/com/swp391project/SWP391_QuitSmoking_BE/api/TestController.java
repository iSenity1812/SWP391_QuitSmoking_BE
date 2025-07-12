package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.service.DailySummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private DailySummaryService dailySummaryService;

    @PostMapping("/send-motivation-email")
    public ResponseEntity<String> sendMotivationEmail() {
        try {
            dailySummaryService.updateGoalAchievementStatusForPreviousDay();
            return ResponseEntity.ok("Đã trigger gửi email motivation thành công!");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi: " + e.getMessage());
        }
    }
} 