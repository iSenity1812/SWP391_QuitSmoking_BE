package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.service.AchievementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/achievements")
public class AchievementAPI {
    @Autowired
    private AchievementService achievementService;

    @GetMapping
    public List<Achievement> getAllAchievements() {
        return achievementService.getAllAchievements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Achievement> getAchievementById(@PathVariable Integer id) {
        Optional<Achievement> achievement = achievementService.getAchievementById(id);
        return achievement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@Valid @RequestBody Achievement achievement) {
        Achievement created = achievementService.createAchievement(achievement);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Achievement> updateAchievement(@PathVariable Integer id,
            @Valid @RequestBody Achievement achievement) {
        try {
            Achievement updated = achievementService.updateAchievement(id, achievement);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement(@PathVariable Integer id) {
        achievementService.deleteAchievement(id);
        return ResponseEntity.noContent().build();
    }
}