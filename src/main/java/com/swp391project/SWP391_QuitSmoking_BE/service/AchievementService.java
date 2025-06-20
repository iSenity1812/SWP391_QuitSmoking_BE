package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AchievementService {
    @Autowired
    private AchievementRepository achievementRepository;

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public Optional<Achievement> getAchievementById(Integer id) {
        return achievementRepository.findById(id);
    }

    public Achievement createAchievement(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    public Achievement updateAchievement(Integer id, Achievement achievementDetails) {
        return achievementRepository.findById(id).map(achievement -> {
            achievement.setName(achievementDetails.getName());
            achievement.setIconUrl(achievementDetails.getIconUrl());
            achievement.setCriteria(achievementDetails.getCriteria());
            achievement.setDescription(achievementDetails.getDescription());
            return achievementRepository.save(achievement);
        }).orElseThrow(() -> new RuntimeException("Achievement not found"));
    }

    public void deleteAchievement(Integer id) {
        achievementRepository.deleteById(id);
    }
}