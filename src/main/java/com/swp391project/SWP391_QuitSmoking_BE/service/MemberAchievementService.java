package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievementId;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberAchievementService {
    @Autowired
    private MemberAchievementRepository memberAchievementRepository;

    public List<MemberAchievement> findAll() {
        return memberAchievementRepository.findAll();
    }

    public Optional<MemberAchievement> findById(MemberAchievementId id) {
        return memberAchievementRepository.findById(id);
    }

    public MemberAchievement save(MemberAchievement memberAchievement) {
        return memberAchievementRepository.save(memberAchievement);
    }

    public void deleteById(MemberAchievementId id) {
        memberAchievementRepository.deleteById(id);
    }
}