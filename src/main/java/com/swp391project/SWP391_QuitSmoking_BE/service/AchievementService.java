package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Achievement;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AchievementRepository;
import com.swp391project.SWP391_QuitSmoking_BE.exception.AchievementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    public List<Achievement> getAllAchievements() {
        return achievementRepository.findAll();
    }

    public Achievement getAchievementById(Long id) {
        Optional<Achievement> achievement = achievementRepository.findById(id);
        if (achievement.isPresent()) {
            return achievement.get();
        }
        throw new AchievementException("Achievement not found with id: " + id);
    }

    public Achievement createAchievement(Achievement achievement) {
        achievement.setCreatedAt(LocalDateTime.now());
        achievement.setActive(true);
        return achievementRepository.save(achievement);
    }

    public Achievement updateAchievement(Long id, Achievement achievementDetails) {
        Achievement achievement = getAchievementById(id);

        achievement.setTitle(achievementDetails.getTitle());
        achievement.setDescription(achievementDetails.getDescription());
        achievement.setTargetValue(achievementDetails.getTargetValue());
        achievement.setCategory(achievementDetails.getCategory());
        achievement.setBadgeUrl(achievementDetails.getBadgeUrl());
        achievement.setPoints(achievementDetails.getPoints());

        return achievementRepository.save(achievement);
    }

    public boolean deleteAchievement(Long id) {
        Achievement achievement = getAchievementById(id);
        achievementRepository.delete(achievement);
        return true;
    }

    public List<Achievement> getAchievementsByCategory(String category) {
        return achievementRepository.findByCategory(category);
    }

    public List<Achievement> getActiveAchievements() {
        return achievementRepository.findByIsActiveTrue();
    }
}