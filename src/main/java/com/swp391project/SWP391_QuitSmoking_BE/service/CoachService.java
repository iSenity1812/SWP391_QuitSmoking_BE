package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.CoachProfile;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoachService {
    private final CoachRepository coachRepository;
    private final UserRepository userRepository;

    @Autowired
    public CoachService(
            CoachRepository coachRepository,
            UserRepository userRepository
    ) {
        this.coachRepository = coachRepository;
        this.userRepository = userRepository;
    }

    private CoachProfile convertToResponseDto(Coach coach) {
        CoachProfile response = new CoachProfile();
        response.setCoachBio(coach.getCoachBio());
        response.setFullName(coach.getFullName());
        return response;
    }

    //được gọi sau khi một User vừa mới được đăng ký
    @Transactional
    public void createCoachForUser(User user, String fullName, String coachBio) {

        Coach coach = new Coach();
        coach.setUserId(user.getUserId());
        coach.setCoachBio(coachBio);
        coach.setFullName(fullName);
        coach.setUser(user); // Coach trỏ đến User
        user.setCoach(coach); // User trỏ đến Coach
        coachRepository.save(coach);
    }

    @Transactional
    public List<Coach> getAllCoachesWithUserDetails() {
        return coachRepository.findAllWithUser();
    }
}
